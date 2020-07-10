package com.example.parstagram;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

// Allows all the posts to be squares
public class Icon extends ImageView {

public Icon(final Context context) {
    super(context);
}

public Icon(final Context context, final AttributeSet attrs) {
    super(context, attrs);
}

public Icon(final Context context, final AttributeSet attrs,
        final int defStyle) {
    super(context, attrs, defStyle);
}

@Override
protected void onMeasure(int width, int height) {
    super.onMeasure(width, height);
    int measuredWidth = getMeasuredWidth();
    int measuredHeight = getMeasuredHeight();
    if (measuredWidth > measuredHeight) {
        setMeasuredDimension(measuredHeight, measuredHeight);
    } else {
        setMeasuredDimension(measuredWidth, measuredWidth);

    }

}

}