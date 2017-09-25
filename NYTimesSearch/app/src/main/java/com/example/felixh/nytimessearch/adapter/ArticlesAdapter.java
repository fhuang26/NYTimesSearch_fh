package com.example.felixh.nytimessearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.felixh.nytimessearch.R;
import com.example.felixh.nytimessearch.main.ArticleActivity;
import com.example.felixh.nytimessearch.model.Article;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by Felix Huang on 9/23/2017.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {
    private Context mContext;
    private List<Article> mArticles;
    Picasso picasso;

    public ArticlesAdapter(Context context, List<Article> Articles) {
        mContext = context;
        mArticles = Articles;
        OkHttpClient client = new OkHttpClient();
        picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mArticles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView tvTitle;
        private TextView tvDate;
        private ImageView ivImage;
        private LinearLayout llItemArticle;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);

            llItemArticle = (LinearLayout) itemView.findViewById(R.id.llItemArticle);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }

        // Involves populating data into the item through holder
        public void bind(final Article article) {
            tvTitle.setText(article.getHeadLine());
            tvDate.setText(article.getDate());
            if (article.getThumbNail() != null && !article.getThumbNail().isEmpty()) { // to do : to check more for valid url
                picasso.with(mContext).load(article.getThumbNail()).into(ivImage);
                // Toast.makeText(this.itemView.getContext(), article.getThumbNail(), Toast.LENGTH_LONG);
            }

            llItemArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (article.getWebUrl() == null || article.getWebUrl().isEmpty()) {
                        Toast.makeText(mContext, "Web url is not available", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(mContext, ArticleActivity.class);
                    intent.putExtra("article", Parcels.wrap(article));
                    mContext.startActivity(intent);
                }
            });

        }
    }
}
