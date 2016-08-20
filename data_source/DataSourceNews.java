package android.qrz.activity.main_menu.data_source;

import android.qrz.activity.news.NewsAdapter;


public  class DataSourceNews {
    private static DataSourceNews instance;
    private static NewsAdapter adapter;

    private DataSourceNews() {
        adapter = new NewsAdapter();
    }

    public static DataSourceNews getInstance() {
        if(instance==null)
            return  instance = new DataSourceNews();
         else return instance;
    }

    public  NewsAdapter getAdapter() {
        return adapter;
    }
}
