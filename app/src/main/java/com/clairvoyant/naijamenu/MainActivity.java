package com.clairvoyant.naijamenu;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clairvoyant.naijamenu.adapter.SlidingDrawerAdapter;
import com.clairvoyant.naijamenu.bean.DrawerBean;
import com.clairvoyant.naijamenu.fonts.RobotoRegularEditText;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.interfaces.IPasswordConfirmListener;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener, IPasswordConfirmListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3000;

    private static int currentListPosition = 1;
    protected SlidingDrawerAdapter mAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private FragmentTransaction transanction;
    private Context mContext;
    private Toolbar toolbar;
    private ListView listView;
    private RelativeLayout listbox, logout;
    private QuizkBackPressedListener onBackPressListener;
    private RobotoRegularTextView title;

    private Integer[] draweeIcon = {R.drawable.home_unselected, R.drawable.menu_unselected,
            R.drawable.challenge_unselected, R.drawable.rate_recipe_unselected, R.drawable.rate_restaurant_unselected,
            R.drawable.update_menu_unselected};
    // private Integer[] selected_draweeIcon = {R.drawable.home_selected,
    // R.drawable.menu_selected, R.drawable.challenge_selected,
    // R.drawable.rate_recipe_selected, R.drawable.rate_restaurant_selected,
    // R.drawable.update_menu_unselected};
    private String[] draweeData = {"Home", "Menu", "Challenge", "Rate Recipes", "Rate Restaurant", "Update Menu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        /*
         * if (!Utils.isOnline(mContext)){
         * setContentView(R.layout.no_network_activity); RobotoRegularButton
         * tryAgain = (RobotoRegularButton) findViewById(R.id.try_again);
         * tryAgain.setOnClickListener(new OnClickListener() {
         *
         * @Override public void onClick(View arg0) { reload(); } }); } else {
         */
        setContentView(R.layout.activity_main);
        Utils.setOrientation(mContext);
        PreferencesUtils.setItemPosition(MainActivity.this, -1);
        toolbar = findViewById(R.id.main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            try {
                toolbar.setBackgroundColor(
                        Color.parseColor(PreferencesUtils.getString(mContext, Constants.RESTAURANT_THEME, "#f25a43")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // toolbar.setLogo(R.drawable.header_logo);
            ImageView logo = toolbar.findViewById(R.id.logo);

            Glide.with(mContext).load(PreferencesUtils.getString(mContext, Constants.RESTAURANT_LOGO)).centerCrop().placeholder(R.drawable.pepsi_logo).fitCenter().into(logo);

            title = toolbar.findViewById(R.id.title);

            int restaurantLogoWithText = PreferencesUtils.getInt(mContext, Constants.RESTAURANT_LOGO_WITH_TEXT);

            if (restaurantLogoWithText == 1)
                title.setText(PreferencesUtils.getString(mContext, Constants.RESTAURANT_NAME));
            else if (restaurantLogoWithText == 0)
                title.setText("");

            Log.i("RESTAURANT_NAME", PreferencesUtils.getString(mContext, Constants.RESTAURANT_NAME));
            Log.i("RESTAURANT_LOGO", PreferencesUtils.getString(mContext, Constants.RESTAURANT_LOGO));
        }

        Intent intent = getIntent();
        if (intent != null) {
            int passedPos = intent.getIntExtra("MENU", 0);
            initializeView(passedPos);
            initDrawer();
        }
        checkForStoragePermission();
    }

    public void reload() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    private void initializeView(int passedPos) {
        listView = findViewById(R.id.drawer_list);
        drawerLayout = findViewById(R.id.navigation_drawer);
        listbox = findViewById(R.id.listbox);
        logout = findViewById(R.id.logoutbox);
        // tvLogout =findViewById(R.id.tvLogout);

        List<DrawerBean> list = getDraweeList();
        mAdapter = new SlidingDrawerAdapter(mContext, R.layout.slide_menu_row, list);
        listView.setAdapter(mAdapter);
        logout.setOnClickListener(this);
        transanction = getSupportFragmentManager().beginTransaction();
        currentListPosition = passedPos;
        if (passedPos == 1 || passedPos == 0) {
            // title.setText(R.string.menu);
            transanction.add(R.id.content_holder, new MenuFragment()).commit();
        } else if (passedPos == 2) {
            // title.setText(R.string.challenge);
            transanction.replace(R.id.content_holder, new GamePlayFragment()).commit();
        } else if (passedPos == 3) {
            // title.setText(R.string.rate_racipe);
            transanction.replace(R.id.content_holder, new RateRecipeFragment1()).commit();
            // startActivity(new Intent(mContext, RateFragmentActivity.class));
        } else if (passedPos == 4) {
            // title.setText(R.string.rate_restaurant);
            transanction.replace(R.id.content_holder, new RateRestaurantFragment()).commit();
            // startActivity(new Intent(mContext, RateFragmentActivity.class));
        } else if (passedPos == 5) {
            int updatedMenuVersion = getIntent().getIntExtra(Constants.UPDATED_MENU_VERSION, 0);
            UpdateMenuFragment updateMenuFragment = new UpdateMenuFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.UPDATED_MENU_VERSION, updatedMenuVersion);
            updateMenuFragment.setArguments(bundle);
            transanction.replace(R.id.content_holder, updateMenuFragment).commit();
            // startActivity(new Intent(mContext, RateFragmentActivity.class));
        }

        listView.setSelection(currentListPosition);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // LinearLayout view1 =
                // (LinearLayout)listView.getChildAt(position);
                TextView tvName = view.findViewById(R.id.tvName);
                ImageView ivImg = view.findViewById(R.id.ivImg);

                view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

                drawerLayout.closeDrawer(listbox);
                transanction = getSupportFragmentManager().beginTransaction();
                transanction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                switch (position) {

                    case 0:
                        setIcon(ivImg, R.drawable.home_unselected, tvName, view);
                        startActivity(new Intent(mContext, HomeActivity.class));
                        // PreferencesUtils.setItemPosition(MainActivity.this,
                        // position);
                        finish();
                        currentListPosition = position;
                        break;
                    case 1:
                        setIcon(ivImg, R.drawable.menu_unselected, tvName, view);
                        PreferencesUtils.setItemPosition(MainActivity.this, position);
                        if (currentListPosition != position) {
                            title.setText(R.string.menu);
                            transanction.replace(R.id.content_holder, new MenuFragment()).commit();
                            currentListPosition = position;
                        }
                        break;
                    case 2:
                        setIcon(ivImg, R.drawable.challenge_unselected, tvName, view);
                        PreferencesUtils.setItemPosition(MainActivity.this, position);
                        if (currentListPosition != position) {
                            title.setText(R.string.challenge);
                            transanction.replace(R.id.content_holder, new GamePlayFragment()).commit();
                            currentListPosition = position;
                        }
                        break;
                    case 3:
                        setIcon(ivImg, R.drawable.rate_recipe_unselected, tvName, view);
                        PreferencesUtils.setItemPosition(MainActivity.this, position);
                        if (currentListPosition != position) {
                            title.setText(R.string.rate_racipe);
                            transanction.replace(R.id.content_holder, new RateRecipeFragment1()).commit();
                            currentListPosition = position;
                        }
                        break;
                    case 4:
                        setIcon(ivImg, R.drawable.rate_restaurant_unselected, tvName, view);
                        PreferencesUtils.setItemPosition(MainActivity.this, position);
                        if (currentListPosition != position) {
                            title.setText(R.string.rate_restaurant);
                            transanction.replace(R.id.content_holder, new RateRestaurantFragment()).commit();
                            currentListPosition = position;
                        }
                        break;
                    case 5:
                        if (currentListPosition != position) {
                            new PasswordConfirmationDialog(MainActivity.this, view, position);
                        }
                        break;
                }
            }
        });
    }

    protected void setIcon(ImageView ivImg, int icon, TextView tvName, View view) {
        ivImg.setBackgroundResource(icon);
        tvName.setTextColor(ContextCompat.getColor(mContext, R.color.slider_drawer_text_color));
        view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mAdapter.notifyDataSetChanged();
                // tvLogout.setTextColor(ContextCompat.getColor(mContext,
                // R.color.slider_drawer_text_color));
                // logout.setBackgroundColor(ContextCompat.getColor(mContext,
                // R.color.white));
                // tvLogout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logout,
                // 0, 0, 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // mAdapter.notifyDataSetChanged();
                // tvLogout.setTextColor(ContextCompat.getColor(mContext,
                // R.color.slider_drawer_text_color));
                // logout.setBackgroundColor(ContextCompat.getColor(mContext,
                // R.color.white));
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

    public void setOnBackPressListener(QuizkBackPressedListener onBackPressListener) {
        this.onBackPressListener = onBackPressListener;
    }

    @Override
    public void onBackPressed() {
        if (currentListPosition == 2) {
            if (onBackPressListener != null) {
                onBackPressListener.onBackPress();
            }
        } else {
            super.onBackPressed();
        }
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

    private void logout() {
        PreferencesUtils.removeValue(mContext, Constants.LOGGED);
        PreferencesUtils.removeValue(mContext, Constants.RESTAURANT_ID);
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
            hideSoftKeyboard(dialog);
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
            logout();
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            MainActivity.this.finish();
        });
        dialog.show();
        hideSoftKeyboard(dialog);
    }

    /**
     * Hides the soft keyboard
     */
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
        TextView tvName = view.findViewById(R.id.tvName);
        ImageView ivImg = view.findViewById(R.id.ivImg);
        setIcon(ivImg, R.drawable.update_menu_unselected, tvName, view);
        PreferencesUtils.setItemPosition(MainActivity.this, position);
        title.setText(R.string.updateMenuTxt);
        UpdateMenuFragment updateMenuFragment = new UpdateMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.UPDATED_MENU_VERSION, updatedMenuVersion);
        updateMenuFragment.setArguments(bundle);
        transanction.replace(R.id.content_holder, updateMenuFragment).commit();
        currentListPosition = position;
        mAdapter.notifyDataSetChanged();
    }

    public interface QuizkBackPressedListener {
        void onBackPress();
    }

    public  void checkForStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                return;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        } else {
            Toast.makeText(mContext, R.string.write_external_storage_permission, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}