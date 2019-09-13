package com.clairvoyant.naijamenu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.PagerSlidingTabStrip;
import com.clairvoyant.naijamenu.utils.Utils;

import java.util.ArrayList;

public class RateFragmentActivity extends AppCompatActivity {

    private static String TAG = RateFragmentActivity.class.getSimpleName();

    String[] tabNames = {"RATE OUR RECIPES", "RATE OUR RESTAURANT"};
    private ViewPager mViewPager;
    private PagerSlidingTabStrip tabs;
    private Toolbar toolbar;
    private ArrayList<MenuCategoryBean> menuList;
    private int passedPosition = 0;
    private MenuViewPagerAdapter pagerAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate()");
        setContentView(R.layout.menu_fragment_activity);
        mContext = this;

        Utils.setOrientation(mContext);

        toolbar = findViewById(R.id.menu_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setLogo(R.drawable.header_logo);
            RobotoRegularTextView title = toolbar.findViewById(R.id.title);
            title.setText(R.string.app_name);
            getSupportActionBar().setTitle("");
        }
        tabs = findViewById(R.id.tabs);
        tabs.setBackgroundColor(getResources().getColor(R.color.orange));
        mViewPager = findViewById(R.id.viewpager);

//		Intent intent = getIntent();
//		if (intent != null) {
//			Bundle bundle = intent.getBundleExtra("PRODUCT_BUNDLE");
//			menuList = (ArrayList<MenuCategoryBean>) bundle.getSerializable("PRODUCTS");
//			passedPosition = bundle.getInt("POSITION", 0);
//		}

        initialTabInitialization();

    }

    private void initialTabInitialization() {

        pagerAdapter = new MenuViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(mViewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MenuViewPagerAdapter extends FragmentPagerAdapter {

        MenuViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RateRecipeFragment();
                case 1:
                    return new RateRestaurantFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            try {
                if (position >= getCount()) {
                    FragmentManager manager = ((Fragment) object).getFragmentManager();
                    assert manager != null;
                    FragmentTransaction transanction = manager.beginTransaction();
                    transanction.remove((Fragment) object);
                    transanction.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}