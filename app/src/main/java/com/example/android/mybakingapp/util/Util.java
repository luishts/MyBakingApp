package com.example.android.mybakingapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.LinearLayout;

import com.example.android.mybakingapp.R;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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

    public static Uri getUriFromURL(String url) {
        Uri uri = null;
        try {
            uri = Uri.parse(new URL(url).toURI().toString());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }
}
