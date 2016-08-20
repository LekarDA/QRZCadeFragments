package android.qrz.activity.news;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.qrz.R;
import android.qrz.model.news.NewsDetail;
import android.qrz.rest_service.api.RetrofitV2Config;
import android.qrz.utils.URLImageParser;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dmitriy on 17.05.16.
 */
public class DetailNewsActivity extends AppCompatActivity {

    public static final String NEWS_ID = "NEWS_ID";
    private static final String NEWS_DETAIL = "NEWS_DETAIL";

    //region Widgets
    private TextView title;
    private WebView snippet;
    private TextView url;
    private TextView author;
    private TextView date;
    //endregion

    private NewsDetail newsDetail;
    private int id;

    //region Activity Life Cycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.news_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.activity_news_detail_tvTitle);
        snippet = (WebView) findViewById(R.id.activity_news_detail_WebView);
        url = (TextView) findViewById(R.id.activity_news_detail_tvURL);
        author = (TextView) findViewById(R.id.activity_news_detail_tvAuthor);
        date = (TextView) findViewById(R.id.activity_news_detail_tvDate);

        snippet.getSettings().setDefaultTextEncodingName("utf-8");
        snippet.getSettings().setJavaScriptEnabled(true);

        id = getIntent().getExtras().getInt(NEWS_ID);
        if (savedInstanceState == null)
            getDetailNewsRequest(id);
        else setData((NewsDetail) savedInstanceState.getParcelable(NEWS_DETAIL));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(NEWS_DETAIL, newsDetail);
    }
    //endregion

    //region UI LISTENERS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    //endregion

    //region Request
    private void getDetailNewsRequest(int id) {
        final Call<NewsDetail> newsDetailCall = RetrofitV2Config.getService().getDetailNews(id);
        newsDetailCall.enqueue(new Callback<NewsDetail>() {
            @Override
            public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response) {
                Log.e("onResponse", response.body().getTitle());
                newsDetail = response.body();
                setData(newsDetail);
            }

            @Override
            public void onFailure(Call<NewsDetail> call, Throwable t) {
                Log.d("onFailure", "request getDetailNews failure");
            }
        });
    }
    //endregion

    //region Utils
    public void setData(NewsDetail data) {
        newsDetail = data;
        title.setText(data.getTitle());
        String msnippet = data.getSnippet();
        snippet.loadDataWithBaseURL(null, convert(msnippet), "text/html", "en_US", null);
        url.setText(data.getUrl());
        author.setText(data.getAuthor());
        date.setText(data.getDate());
    }

    /**метод для масштабирования картинки по размеру экрана в вебвью, если работает не удалять*/
    private String convert(String html) {
        final String key = "<img ";
        final String modifiedKey = "< img ";
        final String widthAttr = " width=\"100%\" ";
        StringBuilder htmlStringBuffer = new StringBuilder(html);
        int index;
        while ((index = htmlStringBuffer.indexOf(key)) >= 0)
            htmlStringBuffer.insert(index + key.length(), widthAttr).replace(index, index + key.length(), modifiedKey);
        while ((index = htmlStringBuffer.indexOf(modifiedKey)) >= 0)
            htmlStringBuffer.replace(index, index + modifiedKey.length(), key);
        return htmlStringBuffer.toString();
    }
    //endregion
}
