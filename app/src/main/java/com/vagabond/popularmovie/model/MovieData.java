package com.vagabond.popularmovie.model;

import java.util.List;

/**
 * Created by HoaNV on 7/20/16.
 */
public class MovieData {

    private Integer page;
    private List<Movie> results;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
