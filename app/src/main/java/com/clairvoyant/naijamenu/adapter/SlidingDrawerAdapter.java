package com.clairvoyant.naijamenu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clairvoyant.naijamenu.R;
import com.clairvoyant.naijamenu.bean.DrawerBean;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;

import java.util.List;

public class SlidingDrawerAdapter extends ArrayAdapter<DrawerBean> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<DrawerBean> list;

    public SlidingDrawerAdapter(Context context, int resource, List<DrawerBean> list) {
        super(context, resource, list);
        this.list = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.slide_menu_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemIcon = convertView.findViewById(R.id.ivImg);
            viewHolder.itemName = convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemIcon.setBackgroundResource(list.get(position).getItemImg());
        if (list.get(position).isSelected()) {
            viewHolder.itemName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            viewHolder.itemName.setTextColor(ContextCompat.getColor(mContext, R.color.slider_drawer_text_color));
        }

        if (PreferencesUtils.getItemPosition(mContext) != -1 && position == PreferencesUtils.getItemPosition(mContext)) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            viewHolder.itemName.setTextColor(ContextCompat.getColor(mContext, R.color.slider_drawer_text_color));
            switch (position) {

                case 0:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.home_unselected);
                    break;
                case 1:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.menu_unselected);
                    break;
                case 2:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.challenge_unselected);
                    break;
                case 3:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.rate_recipe_unselected);
                    break;
                case 4:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.rate_restaurant_unselected);
                    break;
                case 5:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.update_menu_unselected);
                    break;
                default:
                    break;
            }
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_variant_1));
            viewHolder.itemName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            switch (position) {


                case 0:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.home_selected);
                    break;
                case 1:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.menu_selected);
                    break;
                case 2:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.menu_selected);
                    break;
                case 3:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.rate_recipe_selected);
                    break;
                case 4:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.rate_restaurant_selected);
                    break;
                case 5:
                    viewHolder.itemIcon.setBackgroundResource(R.drawable.update_menu_selected);
                    break;
                default:
                    break;
            }

        }

        viewHolder.itemName.setText(list.get(position).getItemName());

        return convertView;
    }

    private class ViewHolder {
        ImageView itemIcon;
        TextView itemName;
    }
}