package com.clairvoyant.naijamenu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.clairvoyant.naijamenu.MenuFragmentActivity;
import com.clairvoyant.naijamenu.R;
import com.clairvoyant.naijamenu.bean.MenuCategoryBean;
import com.clairvoyant.naijamenu.fonts.RobotoLightTextView;
import com.clairvoyant.naijamenu.utils.AppController;
import com.clairvoyant.naijamenu.utils.Utils;

import java.util.ArrayList;

public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder> {

    private ArrayList<MenuCategoryBean> menuList;
    private Activity mContext;

    public MenuRecyclerAdapter(Activity mContext, ArrayList<MenuCategoryBean> menuList) {
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
        MenuViewHolder.rootView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MenuFragmentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("POSITION", position);
            AppController.getInstance().setSelectedParentMenuId(position);
            bundle.putSerializable("PRODUCTS", menuList);
            intent.putExtra("PRODUCT_BUNDLE", bundle);
            mContext.startActivity(intent);
//				mContext.finish();
        });
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_menu_item_view, viewGroup, false);
        return new MenuViewHolder(categoryView);
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rootView;
        private ImageView menuImage;
        private RobotoLightTextView menuName;

        private MenuViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.rootView);
            menuImage = view.findViewById(R.id.iv_menu_image);
            menuName = view.findViewById(R.id.tv_menu_name);
        }
    }
}