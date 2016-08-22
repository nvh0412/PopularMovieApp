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
    private String orderType;

    private boolean mTwoPane;
    private MovieFragment mfPopular;
    private MovieFragment mfTopRated;

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

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, new DetailFragment(), MOVIEFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
            if (viewPager != null) {
                setupViewPager(viewPager);
            }

            TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
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
        mfPopular = new MovieFragment();
        mfTopRated = new MovieFragment();
        PagerFragmentAdapter adapter = new PagerFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mfPopular, getString(R.string.pref_order_popular_label));
        adapter.addFragment(mfTopRated, getString(R.string.pref_order_toprated_label));
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mfPopular.onOrderChange(getString(R.string.pref_order_popular));
                } else {
                    mfTopRated.onOrderChange(getString(R.string.pref_order_toprated));
                }
            }
        });
    }
}
