package com.clairvoyant.naijamenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clairvoyant.naijamenu.adapter.SlidingDrawerAdapter;
import com.clairvoyant.naijamenu.bean.DrawerBean;
import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularEditText;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.interfaces.IPasswordConfirmListener;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PagerSlidingTabStrip;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class MenuFragmentActivity extends AppCompatActivity implements OnClickListener, IPasswordConfirmListener {

    private static String TAG = MenuFragmentActivity.class.getSimpleName();

    private static int currentListPosition = 1;
    protected SlidingDrawerAdapter mAdapter;
    Bundle bundle11;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip tabs;
    private Toolbar toolbar;
    private Context mContext;
    private ArrayList<MenuCategoryBean> menuList;
    private int passedPosition = 0;
    private String[] menuNames;
    private MenuViewPagerAdapter pagerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private RelativeLayout listbox;
    private boolean subCategory;
    private RelativeLayout logout;

    private Integer[] draweeIcon = {R.drawable.home_unselected, R.drawable.menu_unselected,
            R.drawable.challenge_unselected, R.drawable.rate_recipe_unselected, R.drawable.rate_restaurant_unselected,
            R.drawable.update_menu_unselected};
    private Integer[] selected_draweeIcon = {R.drawable.home_selected, R.drawable.menu_selected,
            R.drawable.challenge_selected, R.drawable.rate_recipe_selected, R.drawable.rate_restaurant_selected,
            R.drawable.update_menu_selected};
    private String[] draweeData = {"Home", "Menu", "Challenge", "Rate Recipes", "Rate Restaurant", "Update Menu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.menu_fragment_activity);
        Utils.setOrientation(mContext);
        toolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            try {
                toolbar.setBackgroundColor(
                        Color.parseColor(PreferencesUtils.getString(mContext, Constants.RESTAURANT_THEME, "#f25a43")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // toolbar.setLogo(R.drawable.header_logo);
            ImageView logo = (ImageView) toolbar.findViewById(R.id.logo);
            Glide.with(mContext).load(PreferencesUtils.getString(mContext, Constants.RESTAURANT_LOGO)).centerCrop().placeholder(R.drawable.pepsi_logo).fitCenter().into(logo);


            RobotoRegularTextView title = toolbar.findViewById(R.id.title);
            // title.setText(R.string.app_name);
            // title.setText(PreferencesUtils.getString(mContext,
            // Constants.RESTAURANT_NAME));

            int restaurantLogoWithText = PreferencesUtils.getInt(mContext, Constants.RESTAURANT_LOGO_WITH_TEXT);

            if (restaurantLogoWithText == 1)
                title.setText(PreferencesUtils.getString(mContext, Constants.RESTAURANT_NAME));
            else if (restaurantLogoWithText == 0)
                title.setText("");

            getSupportActionBar().setTitle("");
        }
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        try {
            tabs.setBackgroundColor(
                    Color.parseColor(PreferencesUtils.getString(mContext, Constants.RESTAURANT_THEME, "#f25a43")));
            tabs.setDividerColor(
                    Color.parseColor(PreferencesUtils.getString(mContext, Constants.RESTAURANT_THEME, "#f25a43")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // tabs.setBackgroundColor(getResources().getColor(R.color.orange));

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        listView = (ListView) findViewById(R.id.drawer_list);
        logout = (RelativeLayout) findViewById(R.id.logoutbox);
        logout.setOnClickListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        listbox = (RelativeLayout) findViewById(R.id.listbox);
        List<DrawerBean> list = getDraweeList();
        mAdapter = new SlidingDrawerAdapter(mContext, R.layout.slide_menu_row, list);
        listView.setAdapter(mAdapter);
        initDrawer();
        Intent intent = getIntent();
        if (intent != null) {
            bundle11 = intent.getBundleExtra("PRODUCT_BUNDLE");
            menuList = (ArrayList<MenuCategoryBean>) bundle11.getSerializable("PRODUCTS");
            subCategory = bundle11.getBoolean("subCategory", false);
            passedPosition = bundle11.getInt("POSITION", 0);
            menuNames = new String[menuList.size()];
            for (int i = 0; i < menuNames.length; i++) {
                menuNames[i] = menuList.get(i).getCategoryName();
            }
        }

        initialTabInitialization();

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                drawerLayout.closeDrawer(listbox);

                Intent intent = new Intent(mContext, MainActivity.class);

                switch (position) {

                    case 0:
                        Intent intent1 = new Intent(mContext, HomeActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        finish();
                        break;
                    case 1:
                        intent.putExtra("MENU", 1);
                        startActivity(intent);
                        finish();
                        break;

                    case 2:
                        intent.putExtra("MENU", 2);
                        startActivity(intent);
                        finish();
                        break;

                    case 3:
                        intent.putExtra("MENU", 3);
                        startActivity(intent);
                        finish();
                        break;

                    case 4:
                        intent.putExtra("MENU", 4);
                        startActivity(intent);
                        finish();
                        break;

                    case 5:
                        new PasswordConfirmationDialog(MenuFragmentActivity.this, view, position);
                        break;
                }
            }
        });

    }

    private void initialTabInitialization() {

        final List<Fragment> fragmentList = new Vector<>();

        if (menuList != null && menuList.size() > 0) {
            for (int i = 0; i < menuList.size(); i++) {
                if ("Y".equalsIgnoreCase(menuList.get(i).getDoescontainCat())) {
                    fragmentList
                            .add((SubCategoryFragment) Fragment.instantiate(this, SubCategoryFragment.class.getName()));
                } else {
                    fragmentList.add((ProductsFragment) Fragment.instantiate(this, ProductsFragment.class.getName()));
                }
            }
        }

        pagerAdapter = new MenuViewPagerAdapter(getSupportFragmentManager(), menuList, fragmentList);
        mViewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(mViewPager);
        mViewPager.setCurrentItem(passedPosition);

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                // tabs.setTextColor(Color.BLUE);
                /*
                 * for (int i = 0; i < tabs.getChildCount(); i++) {
                 * tabs.getChildAt(position); if(i == position) {
                 * tabs.setTextColor(Color.BLUE); } else {
                 * tabs.setTextColor(Color.WHITE); } }
                 */

                Log.d(TAG, "hi");
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // TODO Auto-generated method stub
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // TODO Auto-generated method stub
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    private List<DrawerBean> getDraweeList() {
        List<DrawerBean> list = new ArrayList<DrawerBean>();
        for (int i = 0; i < draweeData.length; i++) {
            DrawerBean data = new DrawerBean();
            data.setItemImg(draweeIcon[i]);
            data.setItemName(draweeData[i]);
            list.add(data);
        }
        return list;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // if (Utils.isOnline(mContext))
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // if (Utils.isOnline(mContext))
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logoutbox:
                // logout();
                // tvLogout.setCompoundDrawables( R.drawable.logout_selected, null,
                // null, null );
                // tvLogout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logout_selected,
                // 0, 0, 0);
                // tvLogout.setTextColor(ContextCompat.getColor(mContext,
                // R.color.white));
                // logout.setBackgroundColor(ContextCompat.getColor(mContext,
                // R.color.red_variant_1));
                showConfirmationDialog(R.string.please_confirm_password);
                break;
        }
    }

    private void showConfirmationDialog(int resource) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.logout_alert_dialog_view);
        RobotoRegularTextView messageView = dialog.findViewById(R.id.message);
        messageView.setText(resource);
        RobotoRegularTextView cancel = dialog.findViewById(R.id.cancel);
        RobotoRegularTextView ok = dialog.findViewById(R.id.ok);
        final RobotoRegularEditText etPassword = dialog.findViewById(R.id.etPassword);
        cancel.setOnClickListener(v -> {
            hideSoftKeyboard();
            dialog.dismiss();
        });

        ok.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            String userPassword = PreferencesUtils.getString(mContext, Constants.PASSWORD, "");
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(mContext, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                return;
            } else if (!userPassword.equals(password)) {
                Toast.makeText(mContext, getString(R.string.please_enter_correct_password), Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            Utils.logout(mContext);
            hideSoftKeyboard(dialog);
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            MenuFragmentActivity.this.finish();
        });
        dialog.show();
        hideSoftKeyboard(dialog);
    }

    public void hideSoftKeyboard(Dialog dialog) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onDialogCancel() {
        hideSoftKeyboard();
    }

    @Override
    public void onDialogSuccess(View view, int position, int updatedMenuVersion) {
        PreferencesUtils.setItemPosition(this, position);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("MENU", 5);
        intent.putExtra(Constants.UPDATED_MENU_VERSION, updatedMenuVersion);
        startActivity(intent);
        finish();
    }

    public class MenuViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<MenuCategoryBean> menuList;
        private List<Fragment> fragmentList;

        public MenuViewPagerAdapter(FragmentManager fm, ArrayList<MenuCategoryBean> menuList,
                                    List<Fragment> fragmentList) {
            super(fm);
            this.menuList = menuList;
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = this.fragmentList.get(position);
            Bundle bundle = new Bundle();

            if ("Y".equalsIgnoreCase(menuList.get(position).getDoescontainCat())) {
                bundle.putString("CATEGORY_NAME", menuList.get(position).getCategoryName());
                bundle.putSerializable("CATEGORY_DATA", menuList.get(position));
                fragment.setArguments(bundle);
            } else {
                bundle.putInt("CATEGORY_ID", menuList.get(position).getCategoryId());
                if (subCategory)
                    bundle.putBoolean("subCategory", true);
                fragment.setArguments(bundle);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return menuNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return menuNames[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                if (position >= getCount()) {
                    FragmentManager manager = ((Fragment) object).getFragmentManager();
                    FragmentTransaction transanction = manager.beginTransaction();
                    transanction.remove((Fragment) object);
                    System.gc();
                    transanction.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}