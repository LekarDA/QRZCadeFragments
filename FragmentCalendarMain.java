package android.qrz.activity.main_menu;


import android.os.Bundle;
import android.qrz.R;
import android.qrz.activity.main_menu.data_source.CalendarPageDataSource;
import android.qrz.fragment.ProgressFragment;
import android.qrz.model.calendar.Calendar;
import android.qrz.rest_service.api.RetrofitV2Config;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dmitriy on 10.05.16.
 */


public class FragmentCalendarMain extends Fragment {

    public static final String TAG = FragmentCalendarMain.class.getSimpleName();
    private final String YESTERDAY = "yesterday";
    private final String TODAY = "today";
    private final String TOMORROW = "tomorrow";
    private final String WEEK = "week";


    private Calendar calendarYesterday;
    private Calendar calendarToday;
    private Calendar calendarTomorrow;
    private Calendar calendarWeek;

    private SectionPagerAdapterMainMenu pagerAdapterMainMenu;
    private ProgressFragment progress;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static FragmentCalendarMain fragmentCalendarMain;


    public static FragmentCalendarMain getInstance() {
        if (fragmentCalendarMain == null) {
            fragmentCalendarMain = new FragmentCalendarMain();
            return fragmentCalendarMain;
        } else return fragmentCalendarMain;
    }

    public static FragmentCalendarMain newInstance() {
        fragmentCalendarMain = new FragmentCalendarMain();
        return fragmentCalendarMain;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerAdapterMainMenu = new SectionPagerAdapterMainMenu(getChildFragmentManager(), this, 0);
        if (!CalendarPageDataSource.hasAllData()) {
            Log.i("Fragment Calendar: ", "its first time creating fragment");
            createRequestGetCalendarYesterday(YESTERDAY);
            showProgress();
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (ViewPager) view.findViewById(R.id.fragment_main_menu_app_bar_vpContainer);
        viewPager.setAdapter(pagerAdapterMainMenu);

        tabLayout = (TabLayout) view.findViewById(R.id.fragment_main_menu_app_bar_tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }


    public void showProgress() {
        NewMainMenu.isProgressShowing = true;
        getFragmentManager().beginTransaction().replace(R.id.progressContainer,
                ProgressFragment.newInstance(true), ProgressFragment.TAG).commit();
    }

    public void dismiss() {
        progress = (ProgressFragment) getFragmentManager().findFragmentByTag(ProgressFragment.TAG);
        if (progress != null) {
            NewMainMenu.isProgressShowing = false;
            getFragmentManager().beginTransaction().remove(progress).commit();
        }
    }

    //region Requests to Server
    private void createRequestGetCalendarYesterday(String period) {
        Call<Calendar> calendarCallback = RetrofitV2Config.getService().getCalendar(period);
        calendarCallback.enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                //      Log.e("CALENDAR yesterday", response.body().getData().get(0).getDate());
                calendarYesterday = response.body();
                createRequestGetCalendarToday(TODAY);
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {

            }
        });
    }

    private void createRequestGetCalendarToday(String period) {
        Call<Calendar> calendarCallback = RetrofitV2Config.getService().getCalendar(period);
        calendarCallback.enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                //  Log.e("CALENDAR today", response.body().getData().get(0).getDate());
                calendarToday = response.body();
                createRequestGetCalendarTomorrow(TOMORROW);
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {

            }
        });
    }

    private void createRequestGetCalendarTomorrow(String period) {
        Call<Calendar> calendarCallback = RetrofitV2Config.getService().getCalendar(period);
        calendarCallback.enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
//                Log.e("CALENDAR tomorrow", response.body().getData().get(0).getDate());
                calendarTomorrow = response.body();
                createRequestGetCalendarWeek(WEEK);
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {

            }
        });
    }

    private void createRequestGetCalendarWeek(String period) {
        Call<Calendar> calendarCallback = RetrofitV2Config.getService().getCalendar(period);
        calendarCallback.enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
//                Log.e("CALENDAR week", response.body().getData().get(0).getDate());
                calendarWeek = response.body();
                setEnumData(calendarYesterday, calendarToday, calendarTomorrow, calendarWeek);
                dismiss();
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {

            }
        });
    }
    //endregion

    private void setEnumData(Calendar yesterday, Calendar today, Calendar tomorrow, Calendar week) {
        CalendarPageDataSource.YESTERDAY.getAdapter().setData(yesterday.getData().get(0));
        CalendarPageDataSource.TODAY.getAdapter().setData(today.getData().get(0));
        CalendarPageDataSource.TOMORROW.getAdapter().setData(tomorrow.getData().get(0));
        CalendarPageDataSource.WEEK.getAdapter().setData(week.getData());
    }

}
