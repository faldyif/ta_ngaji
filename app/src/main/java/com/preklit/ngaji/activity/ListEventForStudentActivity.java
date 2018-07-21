package com.preklit.ngaji.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.preklit.ngaji.R;
import com.preklit.ngaji.fragment.ListEventStudentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.mobiwise.materialintro.MaterialIntroConfiguration;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class ListEventForStudentActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Context ctx;
    private MaterialIntroConfiguration config;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event_for_student);

        initToolbar();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        ctx = this;
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), ctx);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);         /* limit is a fixed integer*/

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        ButterKnife.bind(this);

        config = new MaterialIntroConfiguration();
        config.setDelayMillis(0);
        config.setFocusGravity(FocusGravity.CENTER);
        config.setFocusType(Focus.NORMAL);
        config.setFadeAnimationEnabled(true);

        showIntroPending();
    }

    private void showIntroPending() {
        View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        new MaterialIntroView.Builder(ListEventForStudentActivity.this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText("Daftar pengajuan jadwal anda yang belum dikonfirmasi akan masuk ke sini.")
                .setShape(ShapeType.RECTANGLE)
                .setTarget(tab)
                .setConfiguration(config)
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String s) {
                        showIntroDiterima();
                    }
                })
                .setUsageId("intro_jadwal_pending") //THIS SHOULD BE UNIQUE ID
                .show();
    }

    private void showIntroDiterima() {
        View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        new MaterialIntroView.Builder(ListEventForStudentActivity.this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText("Daftar pengajuan jadwal anda yang sudah dikonfirmasi akan masuk ke sini.")
                .setShape(ShapeType.RECTANGLE)
                .setTarget(tab)
                .setConfiguration(config)
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String s) {
                        showIntroDitolak();
                    }
                })
                .setUsageId("intro_jadwal_accepted") //THIS SHOULD BE UNIQUE ID
                .show();
    }

    private void showIntroDitolak() {
        View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2);
        new MaterialIntroView.Builder(ListEventForStudentActivity.this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText("Daftar pengajuan jadwal anda yang ditolak oleh pengajar akan masuk ke sini.")
                .setShape(ShapeType.RECTANGLE)
                .setTarget(tab)
                .setConfiguration(config)
                .setUsageId("intro_jadwal_rejected") //THIS SHOULD BE UNIQUE ID
                .show();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Jadwal NgajiKu");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Context ctx;

        public SectionsPagerAdapter(FragmentManager fm, Context ctx) {
            super(fm);
            this.ctx = ctx;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ListEventStudentFragment.newInstance(ctx, "pending");
            } else if(position == 1) {
                return ListEventStudentFragment.newInstance(ctx, "accepted");
            } else {
                return ListEventStudentFragment.newInstance(ctx, "rejected");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
