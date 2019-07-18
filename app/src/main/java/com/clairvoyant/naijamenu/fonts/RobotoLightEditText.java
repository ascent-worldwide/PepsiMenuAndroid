package com.clairvoyant.naijamenu.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class RobotoLightEditText extends EditText {

    public RobotoLightEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RobotoLightEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoLightEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface mTypeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_regular.ttf");
        setTypeface(mTypeFace);
    }
}