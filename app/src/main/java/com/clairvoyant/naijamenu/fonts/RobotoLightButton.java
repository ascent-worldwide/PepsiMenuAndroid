package com.clairvoyant.naijamenu.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class RobotoLightButton extends AppCompatButton {

    public RobotoLightButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RobotoLightButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoLightButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface mTypeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_regular.ttf");
        setTypeface(mTypeFace);
    }
}