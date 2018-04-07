package bh.edu.ahlia.avel.movieviewer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Avel on 06/12/2017.
 */

public class ActorResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Actor> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setResults(List<Actor> results) {
        this.results = results;
    }

    public List<Actor> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
