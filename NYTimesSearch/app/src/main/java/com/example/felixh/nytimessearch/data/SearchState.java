package com.example.felixh.nytimessearch.data;

import com.example.felixh.nytimessearch.model.Article;

import java.util.ArrayList;

/**
 * Created by Felix Huang on 9/24/2017.
 */

public class SearchState {
    public static int page = 0;
    public static final int MAX_PAGE = 100;
    public static String query = "";
    public static ArrayList<Article> articles = null;
    public static int date_sort_sign = -1;
    public static String beginDate = null;
}
