package com.example.android.mybakingapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    public static Snackbar getSnackMessage(View anchor, String message, boolean error) {
        int color;
        if (error) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }

        Snackbar snackbar = Snackbar
                .make(anchor, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        return snackbar;
    }
}
