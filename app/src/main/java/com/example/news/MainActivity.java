package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.news.ModelClasses.ArticlesItem;
import com.example.news.ModelClasses.MainResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView newsRecycler;
    private Retrofit retrofit;
    private NewsInterface newsInterface;
    private final String generalCategory="general",healthCategory="health",sportsCategory="sports",technologyCategory="technology",businessCategory="business";
    private BottomNavigationView bottomNavigationView;
    private ProgressBar progressBar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsRecycler=findViewById(R.id.newsrecycle);

        bottomNavigationView=findViewById(R.id.bottomNavigation);
        progressBar=findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.action_bar)));
        newsRecycler.setVisibility(View.INVISIBLE);

        actionBar=getSupportActionBar();
        actionBar.setTitle("General");
        setNavigationListener();
        setNewsRetrofit(generalCategory);

    }
    private void setNavigationListener()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id =item.getItemId();
                switch (id){
                    case R.id.general:
                        setNewsRetrofit(generalCategory);
                        return true;

                    case R.id.business:
                        setNewsRetrofit(businessCategory);
                        return true;

                    case R.id.tech:
                        setNewsRetrofit(technologyCategory);
                        return true;

                    case R.id.health:
                        setNewsRetrofit(healthCategory);
                        return true;

                    case R.id.sport:
                        setNewsRetrofit(sportsCategory);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }
    private void setNewsRetrofit(String category)
    {
        char ch= category.charAt(0);
        ch=Character.toUpperCase(ch);
        actionBar.setTitle(ch+category.substring(1));
        progressBar.setVisibility(View.VISIBLE);
        newsRecycler.setVisibility(View.INVISIBLE);

        retrofit= new Retrofit.Builder().baseUrl("https://newsapi.org/").addConverterFactory(GsonConverterFactory.create()).build();
        newsInterface=retrofit.create(NewsInterface.class);
        Call<MainResponse> responsecall=newsInterface.getNewsData(category);
        responsecall.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {

                if(response.isSuccessful())
                {
                    MainResponse mainResponse=response.body();
                    List<ArticlesItem> news=mainResponse.getArticles();

                    newsRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    NewsAdapter adapter= new NewsAdapter(MainActivity.this,news);
                    newsRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                    newsRecycler.setVisibility(View.VISIBLE);
                }
                else
                {

                }
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {

            }
        });
    }
}