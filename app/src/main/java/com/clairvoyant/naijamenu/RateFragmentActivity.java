package com.clairvoyant.naijamenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.PagerSlidingTabStrip;
import com.clairvoyant.naijamenu.utils.Utils;

import java.util.ArrayList;

public class RateFragmentActivity extends AppCompatActivity {

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
        setContentView(R.layout.menu_fragment_activity);
        mContext = this;

        Utils.setOrientation(mContext);

        toolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setLogo(R.drawable.header_logo);
            RobotoRegularTextView title = (RobotoRegularTextView) toolbar.findViewById(R.id.title);
            title.setText(R.string.app_name);
            getSupportActionBar().setTitle("");
        }
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setBackgroundColor(getResources().getColor(R.color.orange));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

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

        public MenuViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    RateRecipeFragment mRateRecipeFragment = new RateRecipeFragment();
                    return mRateRecipeFragment;
                case 1:
                    RateRestaurantFragment mRestaurantFragment = new RateRestaurantFragment();
                    return mRestaurantFragment;
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
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                if (position >= getCount()) {
                    FragmentManager manager = ((Fragment) object).getFragmentManager();
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