package bh.edu.ahlia.avel.movieviewer.api;

import bh.edu.ahlia.avel.movieviewer.model.ActorResponse;
import bh.edu.ahlia.avel.movieviewer.model.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Avel on 01/12/2017.
 */

public interface Service {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MoviesResponse> getSearchMovie(@Query("api_key") String apiKeyName,
                                        @Query("query") String option);

    @GET("discover/movie")
    Call<MoviesResponse> getSearchCountry(@Query("api_key") String apiKeyName,
                                          @Query("region") String option);

    @GET("search/person")
    Call<ActorResponse> getSearchActor(@Query("api_key") String apiKeyName,
                                       @Query("query") String option);
}




