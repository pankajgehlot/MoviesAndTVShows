package com.example.note.pankajpc.latestmoviesandtvshows.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.note.pankajpc.latestmoviesandtvshows.R;
import com.example.note.pankajpc.latestmoviesandtvshows.moviepackage.MovieAdapter;
import com.example.note.pankajpc.latestmoviesandtvshows.network.ApiService;
import com.example.note.pankajpc.latestmoviesandtvshows.network.RetrofitClient;
import com.example.note.pankajpc.latestmoviesandtvshows.pojo.TopRatedMoviesList;
import com.example.note.pankajpc.latestmoviesandtvshows.pojo.TopRatedMoviesPojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class InitialMovieFragement extends Fragment {

    RecyclerView recyclerView;
    Context context;
    MovieAdapter movieAdapter;
    LinearLayoutManager llm;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 8;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;
    List<TopRatedMoviesList> movieList;
    String movieTypes;
    List<TopRatedMoviesList> tempList;

    public InitialMovieFragement() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View v = inflater.inflate(R.layout.fragment_movie_layout, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.main_list);
        llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = llm.getItemCount();
                firstVisibleItem = llm.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading
                        && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && current_page < 20) {
                    // End has been reached

                    // Do something
                    current_page++;
                    ApiService api = RetrofitClient.getApiService();
                    Call<TopRatedMoviesPojo> call = null;
                    call = api.getnowPlaying("55678da3e71af39e568440d6c13a4d3b", "en-US", current_page);
                    call.enqueue(new Callback<TopRatedMoviesPojo>() {
                        @Override
                        public void onResponse(Call<TopRatedMoviesPojo> call, Response<TopRatedMoviesPojo> response) {
                            Log.i("response", "responsecode" + response);
                            tempList = response.body().getResults();
                            movieList.addAll(tempList);
                            movieAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<TopRatedMoviesPojo> call, Throwable t) {
                            Log.i("failure", "failure" + t.getLocalizedMessage());

                        }
                    });
                    loading = true;
                }

            }
        });
        loadJson();
        return v;
    }


    private void loadJson() {
        ApiService api = RetrofitClient.getApiService();
        Call<TopRatedMoviesPojo> call = null;
        call = api.getnowPlaying("55678da3e71af39e568440d6c13a4d3b", "en-US", current_page);
        call.enqueue(new Callback<TopRatedMoviesPojo>() {
                @Override
                public void onResponse(Call<TopRatedMoviesPojo> call, Response<TopRatedMoviesPojo> response) {
                    movieList = response.body().getResults();
                    movieAdapter = new MovieAdapter(context, movieList);
                    recyclerView.setAdapter(movieAdapter);
                }

                @Override
                public void onFailure(Call<TopRatedMoviesPojo> call, Throwable t) {
                    Log.i("failure", "failure" + t.getLocalizedMessage());

                }
            });

    }


}
