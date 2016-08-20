package android.qrz.activity.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.qrz.R;
import android.qrz.activity.main_menu.data_source.DataSourceNews;
import android.qrz.model.news.NewsModel;
import android.qrz.rest_service.api.RetrofitV2Config;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentNewsListPart2 extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    public static final String TAG = FragmentNewsListPart2.class.getSimpleName();
    private static final String LIST_NEWS = "LIST_NEWS";
    private static final String PAGE = "PAGE";
    private ListView listNews;
    private ArrayList<NewsModel> news;
    public  boolean loading = true;
    private int page;
    private int savedPage = 0;

    private  ArrayList<NewsModel> newsList;
    private int numberOfNewsPage = 1;
    private int countOfNews = 25;
    private static FragmentNewsListPart2 fragmentNewsListPart2;



    public static FragmentNewsListPart2 getInstance() {
        if(fragmentNewsListPart2 == null){
            fragmentNewsListPart2 = new FragmentNewsListPart2();
        return fragmentNewsListPart2; }
        else return fragmentNewsListPart2;
    }

    public static FragmentNewsListPart2 newInstance() {
            return new FragmentNewsListPart2();
    }


    //region Fragment Life Cycle

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("FRAGMENT LISTPART2: ","onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("FRAGMENT LISTPART2: ","onCreateView");
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("FRAGMENT LISTPART2: ","onViewCreated");
        listNews = (ListView) view.findViewById(R.id.fragment_news_lvList);

        listNews.setOnItemClickListener(this);
        listNews.setOnScrollListener(this);

        if (savedInstanceState == null){
            newsList = new ArrayList<>();
            createRequestGetNewsList(numberOfNewsPage, countOfNews);
            listNews.setAdapter(DataSourceNews.getInstance().getAdapter());
            Log.d("FRAGMENT LIST SavedINST", "1");
        }else {
            Log.d("FRAGMENT LIST SavedINST", "2");
            newsList = savedInstanceState.getParcelableArrayList(LIST_NEWS);
            Log.d("PARCELABLE LIST", newsList.get(0).getTitle());
            DataSourceNews.getInstance().getAdapter().setNews(newsList);
            listNews.setAdapter(DataSourceNews.getInstance().getAdapter());
            savedPage = savedInstanceState.getInt(PAGE);
            Log.e("onViewCreated","SavedPage: " + String.valueOf(savedPage));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE,page);
        outState.putParcelableArrayList(LIST_NEWS, newsList);
    }

    //endregion


    @Override
    public void onResume() {
        Log.i("FRAGMENT LISTPART2: ","onResume");
        super.onResume();

    }

    //region UI LISTENERS
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("ITEM CLICK ", String.valueOf(id));
        news = DataSourceNews.getInstance().getAdapter().getNews();
        Log.d("LIST ITEM ", news.get(position).getTitle());

        Intent intent = new Intent(getActivity(), DetailNewsActivity.class);
        intent.putExtra(DetailNewsActivity.NEWS_ID, news.get(position).getId());
        Log.d("ITEM ID cLICKED", String.valueOf(news.get(position).getId()));
        startActivity(intent);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.e("ONSCROLL","item number: " + String.valueOf(firstVisibleItem));
        if (!loading) {
            if ((firstVisibleItem + visibleItemCount) == totalItemCount - 5) {
                page = totalItemCount / 25;
                Log.e("ONSCROLL","Page: " + String.valueOf(page));
                Log.e("ONSCROLL","SavedPage: " + String.valueOf(savedPage));
                if(savedPage==0){
                    createRequestGetNewsList(page + 1  ,countOfNews);
                }else {
                    createRequestGetNewsList(savedPage + 1  ,countOfNews);
                    savedPage = 0;}
                loading = true;
            }
        }
    }


    private void createRequestGetNewsList(final int numberOfNewsPage, int countOfNews) {
        Call<List<NewsModel>> listNewsCallback = RetrofitV2Config.getService().getListNews(numberOfNewsPage, countOfNews);
        listNewsCallback.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(Call<List<NewsModel>> call, Response<List<NewsModel>> response) {
                Log.e("NEWS DATA", "SUCCESS" + response.message());
                if (response != null && response.body() != null && response.body().size() != 0) {
                    newsList = (ArrayList<NewsModel>) response.body();
                    DataSourceNews.getInstance().getAdapter().setNews(newsList);
                    loading = false;
                }

            }

            @Override
            public void onFailure(Call<List<NewsModel>> call, Throwable t) {
                Log.e("NEWS DATA", "FAILUREEEE");
            }
        });
    }




}
