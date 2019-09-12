package com.clairvoyant.naijamenu;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.fonts.RobotoLightTextView;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;

import java.io.InputStream;
import java.util.ArrayList;

public class SubCategoryFragment extends Fragment {

    private RecyclerView menuRecycler;
    private int columns = 4;
    private View menuView;
    private Activity mContext;
    private MenuCategoryBean categoryBean;
    private ArrayList<MenuCategoryBean> categoryList;
    private RelativeLayout progressView, rlRootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        // if (Utils.isOnline(mContext)) {
        menuView = inflater.inflate(R.layout.menu_category_fragment, container, false);

        if (getArguments() != null) {
            categoryBean = (MenuCategoryBean) getArguments().getSerializable("CATEGORY_DATA");
            MenuCategoryBean[] categoryArray = categoryBean.getSubCategories();
            categoryList = new ArrayList<>();
            for (MenuCategoryBean menuCategoryBean : categoryArray) {
                MenuCategoryBean bean = new MenuCategoryBean();
                bean.setCategoryId(menuCategoryBean.getCategoryId());
                bean.setCategoryName(menuCategoryBean.getCategoryName());
                bean.setCategoryURL(menuCategoryBean.getCategoryURL());
                categoryList.add(bean);
            }
        }

        initialiseViews(menuView);
        // }
        /*
         * else { menuView = inflater.inflate(R.layout.no_network_activity,
         * container, false); }
         */

        return menuView;
    }

    private void initialiseViews(View menuView) {
        progressView = menuView.findViewById(R.id.progress_view_menu);
        menuRecycler = menuView.findViewById(R.id.menu_recycler);
        rlRootLayout = menuView.findViewById(R.id.rlRootLayout);
        menuRecycler.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns = 4;
        else
            columns = 2;
        menuRecycler.setLayoutManager(new GridLayoutManager(mContext, columns));
        menuRecycler.setAdapter(new MenuRecyclerAdapter(mContext, categoryList));
    }

    @Override
    public void onResume() {
        super.onResume();
        String imgUrl;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imgUrl = PreferencesUtils.getString(mContext, Constants.RESTAURANT_BACKGROUND_IMG_LANDSCAPE);
            if (!TextUtils.isEmpty(imgUrl)) {
                new DownloadImage().execute(imgUrl);
            }
        } else {
            imgUrl = PreferencesUtils.getString(mContext, Constants.RESTAURANT_BACKGROUND_IMG_PORTRAIT);
            if (!TextUtils.isEmpty(imgUrl)) {
                new DownloadImage().execute(imgUrl);
            }
        }
    }

    public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder> {

        private ArrayList<MenuCategoryBean> menuList;
        private Activity mContext;

        private MenuRecyclerAdapter(Activity mContext, ArrayList<MenuCategoryBean> menuList) {
            this.mContext = mContext;
            this.menuList = menuList;
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }

        @Override
        public void onBindViewHolder(MenuViewHolder MenuViewHolder, final int position) {

            final MenuCategoryBean mMenuBean = menuList.get(position);
            MenuViewHolder.menuName.setText(mMenuBean.getCategoryName());
            Utils.renderImage(mContext, mMenuBean.getCategoryURL(), MenuViewHolder.menuImage);
            MenuViewHolder.menuImage.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MenuFragmentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("POSITION", position);
                bundle.putSerializable("PRODUCTS", menuList);
                bundle.putBoolean("subCategory", true);
                intent.putExtra("PRODUCT_BUNDLE", bundle);
                mContext.startActivity(intent);
                mContext.finish();
            });
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_menu_item_view,
                    viewGroup, false);
            return new MenuViewHolder(categoryView);
        }

        class MenuViewHolder extends RecyclerView.ViewHolder {

            private ImageView menuImage;
            private RobotoLightTextView menuName;

            private MenuViewHolder(View view) {
                super(view);
                menuImage = view.findViewById(R.id.iv_menu_image);
                menuName = view.findViewById(R.id.tv_menu_name);
            }
        }
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            /*
             * mProgressDialog = new ProgressDialog(mContext); // Set
             * progressdialog title
             * mProgressDialog.setTitle("Download Image Tutorial"); // Set
             * progressdialog message mProgressDialog.setMessage("Loading...");
             * mProgressDialog.setIndeterminate(false);
             */
            progressView.setVisibility(View.VISIBLE);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            try {
                Drawable dr = new BitmapDrawable(getResources(), result);
                rlRootLayout.setBackground(dr);
                progressView.setVisibility(View.GONE);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}