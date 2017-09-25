package com.example.felixh.nytimessearch.main;

/**
 * Created by Felix Huang on 9/22/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.felixh.nytimessearch.R;
import com.example.felixh.nytimessearch.adapter.ArticlesAdapter;
import com.example.felixh.nytimessearch.data.SearchState;
import com.example.felixh.nytimessearch.model.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.PriorityQueue;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    public static SearchActivity currActivity;
    EditText etQuery;
    Button bnSearch;
    RecyclerView rvArticles;
    RelativeLayout rlSearch;
    ArrayList<Article> articles;
    ArticlesAdapter articleAdapter;
    public NetworkInfo activeNetwork;
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        boolean flagConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
        return flagConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currActivity = this;
        setContentView(R.layout.activity_search);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        setupViews();
        if (SearchState.articles == null) {
            articles = new ArrayList<>();
            SearchState.articles = articles;
        } else {
            articles = SearchState.articles;
        }

        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);

        // Create adapter passing in article data
        articleAdapter = new ArticlesAdapter(this, articles);

        // Attach the adapter to the recyclerview to populate items
        rvArticles.setAdapter(articleAdapter);

        // Set layout manager to position the items
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        rvArticles.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                // loadNextDataFromApi(page);
                // Log.d("DEBUG","page=" + page + " c=" + totalItemsCount);
                SearchState.page = page - 1;
                onArticleSearch(bnSearch);
            }
        };

        // Adds the scroll listener to RecyclerView
        rvArticles.addOnScrollListener(scrollListener);
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        bnSearch = (Button) findViewById(R.id.bnSearch);
        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.miSetting) {
            //Toast.makeText(this, "setting is pressed", Toast.LENGTH_LONG).show();
            // Intent intent = new Intent(this, SettingActivity.class);
            // startActivity(intent);

            FragmentManager fm = getSupportFragmentManager();
            SettingFragment settingfrag = SettingFragment.newInstance("Setting");
            settingfrag.show(fm, "fragment_setting");

            return true;
        }
        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rlSearch.getWindowToken(), 0);
    }

    public void onArticleSearch(View view) {
        boolean flagNetworkConnected = isNetworkConnected();
        if (flagNetworkConnected == false) {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_LONG).show();
            return;
        }
        String query = etQuery.getText().toString();
        if (query == null || query.isEmpty()) {
            return;
        }
        // Toast.makeText(this, "Search for " + query, Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("api-key", "48e49e8b6ce74a91aefe4cdfeb001b68");
        params.put("q", query);
        if (query.equals(SearchState.query)) {
            SearchState.page = SearchState.page + 1;
            if (SearchState.page >= SearchState.MAX_PAGE) {
                SearchState.page = 0;
            }
        } else {
            SearchState.page = 0;
            SearchState.query = query;
        }
        if (SearchState.page == 0) {
            scrollListener.resetState();
        }
        params.put("page", SearchState.page);
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Log.d("DEBUG", response.toString());
                // Toast.makeText(SearchActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                // super.onSuccess(statusCode, headers, response);
                JSONArray articleJsonArray = null;

                try {
                    articleJsonArray = response.getJSONObject("response").getJSONArray("docs");
                    if (SearchState.page == 0) {
                        articles.clear();
                    }
                    if (SearchState.date_sort_sign == 0) {
                        articles.addAll(Article.parseJSONArray(articleJsonArray));
                    } else { //  1: oldest --> newest
                             // -1: newest --> oldest
                        ArrayList<Article> v2 = Article.parseJSONArray(articleJsonArray);
                        PriorityQueue<Article> p = new PriorityQueue<Article>();
                        for (Article x : articles) p.add(x);
                        for (Article x : v2) p.add(x);
                        articles.clear();
                        while (p.size() > 0) {
                            Article x = p.remove();
                            articles.add(x);
                        }
                        articleAdapter.notifyItemRangeInserted(0, articles.size());
                    }

                    articleAdapter.notifyDataSetChanged();
                    SearchActivity.this.hideSoftKeyboard();
                    // articleAdapter.addAll(Article.parseJSONArray(articleJsonArray)); equiv to previous 2 lines
                    // Log.d("DEBUG", articleJsonArray.toString());
                    // Toast.makeText(SearchActivity.this, articles.toString(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
