package com.vagabond.popularmovie.model;

import java.util.List;

/**
 * Created by HoaNV on 8/18/16.
 */
public class TrailerData {
    private Integer page;
    private List<Trailer> results;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
