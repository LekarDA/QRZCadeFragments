package android.qrz.activity.main_menu;

import android.content.Context;
import android.qrz.R;
import android.qrz.activity.main_menu.data_source.CalendarPageDataSource;

import android.qrz.fragment.calendar.FragmentCalendar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by dmitriy on 11.05.16.
 */
public class SectionPagerAdapterMainMenu extends FragmentPagerAdapter {

    private Fragment targetFragment;
    private int requestCode;

    public SectionPagerAdapterMainMenu(FragmentManager fm, Fragment targetFragment, int requestCode) {
        super(fm);
        this.targetFragment = targetFragment;
        this.requestCode = requestCode;
        }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FragmentCalendar fragmentYesterday = FragmentCalendar.newInstance(CalendarPageDataSource.YESTERDAY);
                return fragmentYesterday;
            case 1:
                FragmentCalendar fragmentToday = FragmentCalendar.newInstance(CalendarPageDataSource.TODAY);
                return fragmentToday;
            case 2:
                FragmentCalendar fragmentTomorrow = FragmentCalendar.newInstance(CalendarPageDataSource.TOMORROW);
                return fragmentTomorrow;
            case 3:
                FragmentCalendar fragmentWeek = FragmentCalendar.newInstance(CalendarPageDataSource.WEEK);
                return fragmentWeek;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return targetFragment.getActivity().getResources().getString(R.string.page_title_yesterday);
            case 1:
                return targetFragment.getActivity().getResources().getString(R.string.page_title_today);
            case 2:
                return targetFragment.getActivity().getResources().getString(R.string.page_title_tomorrow);
            case 3:
                return targetFragment.getActivity().getResources().getString(R.string.page_title_week);
        }
        return super.getPageTitle(position);
    }

}
