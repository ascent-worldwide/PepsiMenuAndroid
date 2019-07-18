package com.clairvoyant.naijamenu.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class RobotoRegularEditText extends EditText {

    public RobotoRegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RobotoRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoRegularEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface mTypeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_regular.ttf");
        setTypeface(mTypeFace);
    }
}