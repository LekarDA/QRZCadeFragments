package android.qrz.activity.news;

import android.os.Environment;
import android.qrz.R;
import android.qrz.model.news.NewsModel;
import android.qrz.utils.ImageTarget;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;


public class NewsAdapter extends BaseAdapter {

    private ArrayList<NewsModel> news;
    private ImageView image;


    public NewsAdapter() {
        news = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return news.size();
    }

    @Override
    public NewsModel getItem(int position) {
        return news.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsModel news = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null)
            convertView = inflater.inflate(R.layout.news_layout, parent, false);
        image = (ImageView) convertView.findViewById(R.id.newsLayout_ivImage);
        TextView title = (TextView) convertView.findViewById(R.id.listItemNewsTextViewTitle);
        TextView snippet = (TextView) convertView.findViewById(R.id.listItemNewsTextViewSnippet);
        TextView author = (TextView) convertView.findViewById(R.id.listItemNewsTextViewAuthor);
        TextView date = (TextView) convertView.findViewById(R.id.listItemNewsTextViewDate);

        title.setText(news.getTitle());
        snippet.setText(news.getSnippet());
        author.setText(news.getAuthor());
        date.setText(news.getDate());

        if (news.getImage() != null) {
            settingImage(news);
        } else {
            image.setImageDrawable(parent.getContext().getResources().getDrawable(R.drawable.gallery_icon));
        }

        return convertView;
    }

    public void setNews(ArrayList<NewsModel> newsList) {
        if (news.size() == 0)
            news = newsList;
        else news.addAll(newsList);
        Log.d("Adapter new size: ", String.valueOf(news.size()));
        notifyDataSetChanged();
    }

    public ArrayList<NewsModel> getNews() {
        return news;
    }


    //region Caching Photos

    private void settingImage(NewsModel news) {
        Picasso picasso = Picasso.with(image.getContext());
        picasso.cancelRequest(image);
        Target target = (Target) image.getTag();
        if (target != null) picasso.cancelRequest(target);
        int imageSize = 170;
        final File imageFile = createImageFile(news, imageSize);
        Log.d("TAG", "imageFile is exists: " + imageFile.exists());
        if (imageFile.exists()) picasso.load(imageFile).into(image);
        else {
            target = new ImageTarget(imageFile, image);
            image.setTag(target);
            picasso.load(news.getImage()).resize(imageSize, 0).into(target);
        }
    }

    private File createImageFile(NewsModel newsModel, Integer size) {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File picassoSampleStorageDir = new File(externalStorageDir, "QRZ Sample");
        File mediaStorageDir = new File(picassoSampleStorageDir, "media");

        StringBuilder fileNameBuilder = new StringBuilder(String.format(Locale.getDefault(), "news_%d", newsModel.getId()));
        if (size != null) fileNameBuilder.append("_").append(size).append("px");
        fileNameBuilder.append(".png");

        File imageFile = new File(mediaStorageDir, fileNameBuilder.toString());
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    //endregion

}
