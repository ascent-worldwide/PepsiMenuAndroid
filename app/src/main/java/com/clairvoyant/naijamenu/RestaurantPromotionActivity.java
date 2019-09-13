package com.clairvoyant.naijamenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.clairvoyant.naijamenu.utils.Utils;

public class RestaurantPromotionActivity extends AppCompatActivity {

    private static String TAG = RestaurantPromotionActivity.class.getSimpleName();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate()");
        setContentView(R.layout.activity_restaurant_promotion);

        mContext = this;

        Utils.setOrientation(mContext);

        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("RESTAURANT_PROMOTION");
            ImageView image = findViewById(R.id.imageView);
            // Picasso.with(this).load(url).placeholder(R.drawable.promo_placeholder).into(image);
            Utils.renderImage(mContext, url, image);
            TextView skip = findViewById(R.id.skip);
            final int menuId = getIntent().getIntExtra("MENU", 0);
            skip.setOnClickListener(v -> {
                Intent intent1 = new Intent(RestaurantPromotionActivity.this, MainActivity.class);
                intent1.putExtra("MENU", menuId);
                startActivity(intent1);
                finish();
            });
        }
    }
}