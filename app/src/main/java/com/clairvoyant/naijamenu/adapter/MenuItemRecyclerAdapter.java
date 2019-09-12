package com.clairvoyant.naijamenu.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clairvoyant.naijamenu.R;
import com.clairvoyant.naijamenu.bean.ProductBean;
import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.DynamicHeightViewPager;
import com.clairvoyant.naijamenu.utils.Utils;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MenuItemRecyclerAdapter extends RecyclerView.Adapter<MenuItemRecyclerAdapter.MenuViewHolder> {

    private static String TAG = MenuItemRecyclerAdapter.class.getSimpleName();


    private YouTubePlayerFragment mYouTubePlayerFragment;
    private Activity mContext;
    private ArrayList<ProductBean> menuItemList;

    public MenuItemRecyclerAdapter(Activity mContext, ArrayList<ProductBean> menuItemList) {
        this.mContext = mContext;
        this.menuItemList = menuItemList;
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    @Override
    public void onBindViewHolder(MenuViewHolder MenuViewHolder, int position) {

        final ProductBean mMenuBean = menuItemList.get(position);
        MenuViewHolder.productName.setText(mMenuBean.getProductName());
        Utils.renderImage(mContext, mMenuBean.getProductUrl(), MenuViewHolder.productImage);
        MenuViewHolder.productDesc.setText(mMenuBean.getProductDesc());

        DecimalFormat formatter = new DecimalFormat("#,###,###");

        MenuViewHolder.price.setText("" + formatter.format(mMenuBean.getPrice()));

        if (mMenuBean.getProductType().equals("veg")) {
            MenuViewHolder.productType.setImageResource(R.drawable.veg_icon);
        } else if (mMenuBean.getProductType().equals("nonveg")) {
            MenuViewHolder.productType.setImageResource(R.drawable.nonveg_icon);
        } else {
            MenuViewHolder.productType.setImageResource(android.R.color.transparent);
        }
		
		/*code by afsar
		if (mMenuBean.getSpiceLevel() == 0) {
			MenuViewHolder.spiceLevel1.setVisibility(View.INVISIBLE);
		} else if (mMenuBean.getSpiceLevel() == 1) {
			MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_gray);
			MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
		} else if (mMenuBean.getSpiceLevel() == 2) {
			MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_green);
			MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
		} else if (mMenuBean.getSpiceLevel() == 3) {
			MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_red);
			MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
		}*/

        int spiceLevel = mMenuBean.getSpiceLevel();

//		code by negi
        if (spiceLevel == 0) {
            // 0 spice level mean gray chilli
            MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_gray);
            MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
        } else if (spiceLevel == 1) {
            // 1 spice level mean 1 red chilli
            MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
        } else if (spiceLevel == 2) {
            // 2 spice level mean 2 red chilli
            MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel2.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
        } else if (spiceLevel == 3) {
            // 3 spice level mean 3 red chilli
            MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel2.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel3.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel2.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel3.setVisibility(View.VISIBLE);
        } else if (spiceLevel == 4) {
            // 4 spice level mean 4 red chilli
            MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel2.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel3.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel4.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel2.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel3.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel4.setVisibility(View.VISIBLE);
        } else if (spiceLevel == 5) {
            // 5 spice level mean 5 red chilli
            MenuViewHolder.spiceLevel1.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel2.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel3.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel4.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel5.setImageResource(R.drawable.chilli_red);
            MenuViewHolder.spiceLevel1.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel2.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel3.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel4.setVisibility(View.VISIBLE);
            MenuViewHolder.spiceLevel5.setVisibility(View.VISIBLE);
        }

        MenuViewHolder.productImage.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.product_detail_dialog_view);
            dialog.setCancelable(false);

            Display display = mContext.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;


            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                height = (width / 2 * 900) / 1200;
                dialog.getWindow().setLayout(width - 50, height);
//					dialog.getWindow().setLayout(2340, 954);
            } else {
//					dialog.getWindow().setLayout(1200, 1800);
                height = (3 * width * 900 * 2) / (1200 * 4);
                dialog.getWindow().setLayout((int) (.9 * width), height);
            }

            final DynamicHeightViewPager viewPager = dialog.findViewById(R.id.product_detail_viewpager);
            final ImageView slideLeft = dialog.findViewById(R.id.left);
            final ImageView slideRight = dialog.findViewById(R.id.right);
            TextView productName = dialog.findViewById(R.id.product_name);
            ImageView vegNonveg = dialog.findViewById(R.id.veg_nonveg);
            ImageView close = dialog.findViewById(R.id.close);
            ImageView close1 = dialog.findViewById(R.id.close1);
            TextView description = dialog.findViewById(R.id.description);
            TextView preparationTime = dialog.findViewById(R.id.preparation_time);
            TextView menuCost = dialog.findViewById(R.id.menu_cost);
            ImageView chillies1 = dialog.findViewById(R.id.chilli1);
            ImageView chillies2 = dialog.findViewById(R.id.chilli2);
            ImageView chillies3 = dialog.findViewById(R.id.chilli3);
            ImageView chillies4 = dialog.findViewById(R.id.chilli4);
            ImageView chillies5 = dialog.findViewById(R.id.chilli5);
            RelativeLayout pagerBox = dialog.findViewById(R.id.pager_box);
            RelativeLayout videoBox = dialog.findViewById(R.id.video_box);

            CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(mContext, mMenuBean.getProductDetailUrl());
            mYouTubePlayerFragment = (YouTubePlayerFragment) mContext.getFragmentManager().findFragmentById(R.id.youtube_fragment);

            if (mMenuBean.getDetail_url_type() == 1) {
                close.setVisibility(View.GONE);
                if (close1 != null) {
                    close1.setVisibility(View.VISIBLE);
                }
                videoBox.setVisibility(View.GONE);
                pagerBox.setVisibility(View.VISIBLE);
            } else if (mMenuBean.getDetail_url_type() == 2) {
                pagerBox.setVisibility(View.GONE);
                videoBox.setVisibility(View.VISIBLE);
                if (close1 != null)
                    close1.setVisibility(View.GONE);

                close.setVisibility(View.VISIBLE);
            }

            viewPager.setAdapter(pagerAdapter);

            final int count = mMenuBean.getProductDetailUrl().length;

            if (count > 1 && viewPager.getCurrentItem() <= 0) {
                slideLeft.setImageResource(R.drawable.arrow_left_disabled);
                slideRight.setImageResource(R.drawable.arrow_right_enabled);
            } else if (count == 1) {
                slideLeft.setImageResource(R.drawable.arrow_left_disabled);
                slideRight.setImageResource(R.drawable.arrow_right_disabled);
            }

            slideLeft.setOnClickListener(v13 -> {

                if (count > 1 && viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                    if (viewPager.getCurrentItem() == 0) {
                        slideLeft.setImageResource(R.drawable.arrow_left_disabled);
                    } else {
                        slideLeft.setImageResource(R.drawable.arrow_left_enabled);
                    }

                    if (count > 1 && viewPager.getCurrentItem() < (count - 1)) {
                        slideRight.setImageResource(R.drawable.arrow_right_enabled);
                    }
                }
            });

            slideRight.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (count > 1 && viewPager.getCurrentItem() < count) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        if (viewPager.getCurrentItem() == (count - 1)) {
                            slideRight.setImageResource(R.drawable.arrow_right_disabled);
                        } else {
                            slideRight.setImageResource(R.drawable.arrow_right_enabled);
                        }

                        if (count > 1 && viewPager.getCurrentItem() > 0) {
                            slideLeft.setImageResource(R.drawable.arrow_left_enabled);
                        }
                    }
                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {

                    if (count == 1) {
                        slideLeft.setImageResource(R.drawable.arrow_left_disabled);
                        slideRight.setImageResource(R.drawable.arrow_right_disabled);
                    }

                    if (count > 1 && viewPager.getCurrentItem() > 0) {
                        slideLeft.setImageResource(R.drawable.arrow_left_enabled);
                    } else {
                        slideLeft.setImageResource(R.drawable.arrow_left_disabled);
                    }

                    if (count > 1 && viewPager.getCurrentItem() < (count - 1)) {
                        slideRight.setImageResource(R.drawable.arrow_right_enabled);
                    } else {
                        slideRight.setImageResource(R.drawable.arrow_right_disabled);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }
            });

            productName.setText(mMenuBean.getProductName());

            if (mMenuBean.getProductType().equals("veg")) {
                vegNonveg.setImageResource(R.drawable.veg_icon_large);
            } else if (mMenuBean.getProductType().equals("nonveg")) {
                vegNonveg.setImageResource(R.drawable.nonveg_icon_large);
            } else {
                vegNonveg.setImageResource(android.R.color.transparent);
            }

            description.setText(mMenuBean.getProductDesc());
            int prepareTime = mMenuBean.getPreparationTime();
            if (prepareTime > 0) {
                preparationTime.setVisibility(View.VISIBLE);
                preparationTime.setText("PREPARATION TIME:  " + prepareTime + Constants.MINUTE);
            } else
                preparationTime.setVisibility(View.GONE);

            menuCost.setText("" + mMenuBean.getPrice());

            int spiceLevel1 = mMenuBean.getSpiceLevel();

            // code by afsar
            /*if (mMenuBean.getSpiceLevel() == 0) {
                chillies.setVisibility(View.INVISIBLE);
            } else if (mMenuBean.getSpiceLevel() == 1) {
                chillies.setImageResource(R.drawable.chilli_gray);
                chillies.setVisibility(View.VISIBLE);
            } else if (mMenuBean.getSpiceLevel() == 2) {
                chillies.setImageResource(R.drawable.chilli_green);
                chillies.setVisibility(View.VISIBLE);
            } else if (mMenuBean.getSpiceLevel() == 3) {
                chillies.setImageResource(R.drawable.chilli_red);
                chillies.setVisibility(View.VISIBLE);
            }*/

            // code by negi
            if (spiceLevel1 == 0) {
                chillies1.setImageResource(R.drawable.chilli_gray);
                chillies1.setVisibility(View.VISIBLE);
            } else if (spiceLevel1 == 1) {
                chillies1.setImageResource(R.drawable.chilli_red);
                chillies1.setVisibility(View.VISIBLE);
            } else if (spiceLevel1 == 2) {
                chillies1.setImageResource(R.drawable.chilli_red);
                chillies2.setImageResource(R.drawable.chilli_red);
                chillies1.setVisibility(View.VISIBLE);
                chillies2.setVisibility(View.VISIBLE);
            } else if (spiceLevel1 == 3) {
                chillies1.setImageResource(R.drawable.chilli_red);
                chillies2.setImageResource(R.drawable.chilli_red);
                chillies3.setImageResource(R.drawable.chilli_red);
                chillies1.setVisibility(View.VISIBLE);
                chillies2.setVisibility(View.VISIBLE);
                chillies3.setVisibility(View.VISIBLE);
            } else if (spiceLevel1 == 4) {
                chillies1.setImageResource(R.drawable.chilli_red);
                chillies2.setImageResource(R.drawable.chilli_red);
                chillies3.setImageResource(R.drawable.chilli_red);
                chillies4.setImageResource(R.drawable.chilli_red);
                chillies1.setVisibility(View.VISIBLE);
                chillies2.setVisibility(View.VISIBLE);
                chillies3.setVisibility(View.VISIBLE);
                chillies4.setVisibility(View.VISIBLE);
            } else if (spiceLevel1 == 5) {
                chillies1.setImageResource(R.drawable.chilli_red);
                chillies2.setImageResource(R.drawable.chilli_red);
                chillies3.setImageResource(R.drawable.chilli_red);
                chillies4.setImageResource(R.drawable.chilli_red);
                chillies5.setImageResource(R.drawable.chilli_red);
                chillies1.setVisibility(View.VISIBLE);
                chillies2.setVisibility(View.VISIBLE);
                chillies3.setVisibility(View.VISIBLE);
                chillies4.setVisibility(View.VISIBLE);
                chillies5.setVisibility(View.VISIBLE);
            }

            close.setOnClickListener(v1 -> {
                resetFragment();
                dialog.dismiss();
            });

            if (close1 != null) {
                close1.setOnClickListener(v12 -> {
                    resetFragment();
                    dialog.dismiss();
                });
            }

            if (mMenuBean.getDetail_url_type() == 2) {
                videoBox.setVisibility(View.VISIBLE);
                mYouTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new OnInitializedListener() {

                    @Override
                    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
                        if (!wasRestored) {
                            player.cueVideo("nCgQDjiotG0");
                        }
                    }

                    @Override
                    public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
                    }
                });
            } else {
                videoBox.setVisibility(View.GONE);
            }

            dialog.show();
        });
    }

    private void resetFragment() {
        FragmentManager fm = mContext.getFragmentManager();
        android.app.Fragment fragment = fm.findFragmentById(R.id.youtube_fragment);
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_row_item, viewGroup, false);
        return new MenuViewHolder(categoryView);
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productDesc;
        TextView price;
        ImageView productType;
        ImageView spiceLevel1;
        ImageView spiceLevel2;
        ImageView spiceLevel3;
        ImageView spiceLevel4;
        ImageView spiceLevel5;

        MenuViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.product_image);
            productName = view.findViewById(R.id.product_name);
            productDesc = view.findViewById(R.id.product_desc);
            price = view.findViewById(R.id.price);
            productType = view.findViewById(R.id.product_type);
            spiceLevel1 = view.findViewById(R.id.spice_level1);
            spiceLevel2 = view.findViewById(R.id.spice_level2);
            spiceLevel3 = view.findViewById(R.id.spice_level3);
            spiceLevel4 = view.findViewById(R.id.spice_level4);
            spiceLevel5 = view.findViewById(R.id.spice_level5);
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        private String[] mResources;

        CustomPagerAdapter(Context context, String[] mResources) {
            mContext = context;
            this.mResources = mResources;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = itemView.findViewById(R.id.imageView);
            String imageUrl = mResources[position];
            Log.d(TAG, "image url=" + imageUrl);

            Utils.renderImage(mContext, imageUrl, imageView);
//	        if(!TextUtils.isEmpty(imageUrl))
//	        	Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.product_placeholder).fit().into(imageView);
//	        else
//	        	Picasso.with(mContext).load("xyz").placeholder(R.drawable.product_placeholder).error(R.drawable.product_placeholder).into(imageView);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}