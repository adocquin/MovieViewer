package bh.edu.ahlia.avel.movieviewer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.test.InstrumentationTestRunner;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bh.edu.ahlia.avel.movieviewer.adapter.MoviesAdapter;
import bh.edu.ahlia.avel.movieviewer.api.Client;
import bh.edu.ahlia.avel.movieviewer.api.Service;
import bh.edu.ahlia.avel.movieviewer.model.Actor;
import bh.edu.ahlia.avel.movieviewer.model.ActorResponse;
import bh.edu.ahlia.avel.movieviewer.model.Movie;
import bh.edu.ahlia.avel.movieviewer.model.MoviesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private RecyclerView        recyclerView;
    private MoviesAdapter       adapter;
    private List<Movie>         movieList;
    ProgressDialog              pd;
    private SwipeRefreshLayout  swipeContainer;

    Dialog                      searchDialog;
    private RadioButton         radioMovie, radioActor, radioCountry;

    private EditText            editSearch;
    private Spinner             spinnerSearch;
    private Button              buttonSearch;
    private int                 typeCall;
    private String              option;

    ArrayAdapter<CharSequence> adapterCountrys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeCall = 0;
        option = "";
        initViews();

        /* Initialize the SwipeRefreshLayout */
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.moviesRefreshed),
                        Toast.LENGTH_SHORT).show();
            }
        });


        adapterCountrys = ArrayAdapter.createFromResource(this,
                R.array.countrys_array, android.R.layout.simple_spinner_item);
    }

    /* Return the current activity informations */
    public Activity getActivity() {
        Context context = this;

        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /* Init the views */
    private void initViews() {
        pd =  new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.fetchingMovies));
        pd.setCancelable(false);
        pd.show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        /* Adapt the view for landscape and portrait modes */
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        loadJSON();
    }

    private void loadJSON() {
        adapter.notifyDataSetChanged();
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.emptyAPIKey), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            if (typeCall != 3)
            {
                Call<MoviesResponse> call = null;
                switch (typeCall) {
                    case 1:
                        call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
                        break;
                    case 2:
                        call = apiService.getSearchMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN, option);
                        break;
                    case 4:
                        call = apiService.getSearchCountry(BuildConfig.THE_MOVIE_DB_API_TOKEN, option);
                        break;
                    default:
                        call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
                        break;
                }
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        List<Movie> movies = response.body().getResults();
                        recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.errorFetchingData), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Call<ActorResponse> call = apiService.getSearchActor(BuildConfig.THE_MOVIE_DB_API_TOKEN, option);
                call.enqueue(new Callback<ActorResponse>() {
                    @Override
                    public void onResponse(Call<ActorResponse> call, Response<ActorResponse> response) {
                        List<Actor> actors = response.body().getResults();
                        List<Movie> movies;
                        if (actors.size() > 0)
                            movies = actors.get(0).getKnown_for();
                        else
                            movies = new ArrayList<Movie>();
                        recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ActorResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.errorFetchingData), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         //adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_top:
                typeCall = 1;
                option = "";
                loadJSON();
                return true;
            case R.id.menu_popular:
                typeCall = 0;
                option = "";
                loadJSON();
                return true;
            case R.id.menu_search:
                searchDialogManager();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchDialogManager() {
        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.search_dialog);
        searchDialog.show();

        editSearch = (EditText) searchDialog.findViewById(R.id.editSearch);
        spinnerSearch = (Spinner) searchDialog.findViewById(R.id.spinnerSearch);
        radioMovie = (RadioButton) searchDialog.findViewById(R.id.radioMovie);
        radioActor = (RadioButton) searchDialog.findViewById(R.id.radioActor);
        radioCountry = (RadioButton) searchDialog.findViewById(R.id.radioCountry);
        buttonSearch = (Button) searchDialog.findViewById(R.id.buttonSearch);
        editSearch = (EditText) searchDialog.findViewById(R.id.editSearch);

        radioMovie.setChecked(true);
        radioMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioMovie();
                editSearch.setVisibility(View.VISIBLE);
                spinnerSearch.setVisibility(View.GONE);
            }
        });
        radioActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioActor();
                editSearch.setVisibility(View.VISIBLE);
                spinnerSearch.setVisibility(View.GONE);
            }
        });
        radioCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCountry();
                editSearch.setVisibility(View.GONE);
                spinnerSearch.setVisibility(View.VISIBLE);
                spinnerSearch.setAdapter(adapterCountrys);
                spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        option = parent.getItemAtPosition(position).toString();
                        if (option.equals("France"))
                            option = "fr";
                        else if (option.equals("Germany"))
                            option = "de";
                        else if (option.equals("United States of America"))
                            option = "us";
                        else if (option.equals("Brazil"))
                            option = "br";
                        else if (option.equals("China"))
                            option = "cn";
                        else if (option.equals("Saudi Arabia"))
                            option = "sa";
                        else if (option.equals("Thailand"))
                            option = "th";
                        else if (option.equals("India"))
                            option = "in";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioMovie.isChecked()) {
                    String tmp = editSearch.getText().toString();
                    if (tmp.length() > 0) {
                        typeCall = 2;
                        option = editSearch.getText().toString();
                        loadJSON();
                    }
                }
                if (radioActor.isChecked()) {
                    String tmp = editSearch.getText().toString();
                    if (tmp.length() > 0) {
                        typeCall = 3;
                        option = editSearch.getText().toString();
                        loadJSON();
                    }
                }
                if (radioCountry.isChecked()) {
                    typeCall = 4;
                    loadJSON();
                }
                searchDialog.dismiss();
            }
        });
    }

    private void uncheckRadioButtons() {
        if (radioMovie.isChecked())
            radioMovie.setChecked(false);
        if (radioCountry.isChecked())
            radioCountry.setChecked(false);
        if (radioActor.isChecked())
            radioActor.setChecked(false);
    }

    public void radioMovie() {
        uncheckRadioButtons();
        radioMovie.setChecked(true);
    }

    public void radioActor() {
        uncheckRadioButtons();
        radioActor.setChecked(true);
    }

    public void radioCountry() {
        uncheckRadioButtons();
        radioCountry.setChecked(true);
    }
}