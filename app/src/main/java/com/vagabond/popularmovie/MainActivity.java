package com.vagabond.popularmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DETAILFRAGMENT";
    private static final String MOVIEFRAGMENT_TAG = "MOVIEFRAGMENT";

    private boolean mTwoPane;
    private String ORDER_TYPE = "ORDER_TYPE";
    private String FILTER_TYPE = "FILTER_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, new DetailFragment(), MOVIEFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieIdUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, movieIdUri);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, df, DETAILFRAGMENT_TAG)
                .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(movieIdUri);
            startActivity(intent);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        MovieFragment mfPopular = new MovieFragment();
        Bundle popularBundle = new Bundle();
        popularBundle.putString(ORDER_TYPE, "popular");
        mfPopular.setArguments(popularBundle);

        MovieFragment mfTopRated = new MovieFragment();
        Bundle topRatedBundle = new Bundle();
        topRatedBundle.putString(ORDER_TYPE, "top_rated");
        mfTopRated.setArguments(topRatedBundle);

        MovieFragment mfFavourite = new MovieFragment();
        Bundle favoriteBundle = new Bundle();
        favoriteBundle.putBoolean(FILTER_TYPE, true);
        mfFavourite.setArguments(favoriteBundle);

        PagerFragmentAdapter adapter = new PagerFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mfPopular, getString(R.string.pref_order_popular_label));
        adapter.addFragment(mfTopRated, getString(R.string.pref_order_toprated_label));
        adapter.addFragment(mfFavourite, getString(R.string.pref_order_favourite_label));
        viewPager.setAdapter(adapter);
    }
}
