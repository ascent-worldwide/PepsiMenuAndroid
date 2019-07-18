package com.clairvoyant.naijamenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.clairvoyant.naijamenu.utils.Utils;

public class RestaurantPromotionActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_promotion);

        mContext = this;

        Utils.setOrientation(mContext);

        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("RESTAURANT_PROMOTION");
            ImageView image = (ImageView) findViewById(R.id.imageView);
           // Picasso.with(this).load(url).placeholder(R.drawable.promo_placeholder).into(image);
            Utils.renderImage(mContext, url, image);
            TextView skip = (TextView) findViewById(R.id.skip);
            final int menuId = getIntent().getIntExtra("MENU", 0);
            skip.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RestaurantPromotionActivity.this, MainActivity.class);
                    intent.putExtra("MENU", menuId);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}