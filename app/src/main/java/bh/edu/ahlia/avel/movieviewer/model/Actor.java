package bh.edu.ahlia.avel.movieviewer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Avel on 06/12/2017.
 */

public class Actor {
    @SerializedName("popularity")
    private Double popularity;
    @SerializedName("id")
    private Integer id;
    @SerializedName("profil_path")
    private String profilPath;
    @SerializedName("name")
    private String name;
    @SerializedName("known_for")
    private List<Movie> known_for = new ArrayList<Movie>();

    public Actor(Double popularity, Integer id, String profilPath, String name, List<Movie> known_for) {
        this.popularity = popularity;
        this.profilPath = profilPath;
        this.name = name;
        this.known_for = known_for;
    }

    public String getProfilPath() {
        return "https://image.tmdb.org/t/p/w500" + profilPath;
    }

    public void setProfilPath(String posterPath) {
        this.profilPath = profilPath;
    }

    public Double getPopularity() {
        return this.popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Movie> getKnown_for() {
        return this.known_for;
    }

    public void setKnown_for(ArrayList<Movie> known_for) {
        this.known_for = known_for;
    }
}
