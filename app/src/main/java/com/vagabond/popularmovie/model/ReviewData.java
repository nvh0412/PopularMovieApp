package com.vagabond.popularmovie.model;

import java.util.List;

/**
 * Created by HoaNV on 8/18/16.
 */
public class ReviewData {
    private Integer page;
    private List<Review> results;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
