package com.example.android.mybakingapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.widget.LinearLayout;

import com.example.android.mybakingapp.R;

/**
 * Created by Luis on 26/06/2017.
 */

public class Util {

    public static LinearLayout.LayoutParams getLayoutParamsGone(Context context) {
        Resources resources = context.getResources();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.WRAP_CONTENT);
        int margin = resources.getDimensionPixelSize(R.dimen.generic_cardview_margin_visible);
        layoutParams.leftMargin = margin;
        layoutParams.rightMargin = margin;
        layoutParams.topMargin = margin;
        layoutParams.bottomMargin = margin;

        return layoutParams;
    }

    public static LinearLayout.LayoutParams getLayoutParamsVisible(Context context) {
        Resources resources = context.getResources();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.WRAP_CONTENT);
        int margin = resources.getDimensionPixelSize(R.dimen.generic_cardview_margin_visible);
        layoutParams.leftMargin = margin;
        layoutParams.rightMargin = margin;
        layoutParams.topMargin = margin;
        margin = resources.getDimensionPixelSize(R.dimen.generic_cardview_margin_gone);
        layoutParams.bottomMargin = margin;

        return layoutParams;
    }
}
