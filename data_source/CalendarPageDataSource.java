package android.qrz.activity.main_menu.data_source;

import android.qrz.adapter.calendar_adapter.CalendarAdapter;

/**
 * Created by dmitriy on 25.05.16.
 */
public enum CalendarPageDataSource {
    YESTERDAY("yesterday"),
    TODAY("today"),
    TOMORROW("tomorrow"),
    WEEK("week");

    private String title;
    private CalendarAdapter adapter;

    CalendarPageDataSource(String title) {
        this.title = title;
        adapter = new CalendarAdapter();
    }

    public String getTitle() {
        return title;
    }

    public CalendarAdapter getAdapter() {
        return adapter;
    }

    public static boolean hasAllData() {
        for (CalendarPageDataSource dataSource: values())
        if(!dataSource.adapter.hasData()) return false;
        return true;
    }
}
