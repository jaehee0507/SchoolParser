package me.blog.colombia2.schoolparser.utils;

import android.support.design.widget.*;
import android.view.*;
import me.blog.colombia2.schoolparser.*;

public class ErrorDisplayer {
    public static void showInternetError(View v) {
        Snackbar.make(v, R.string.check_internet, Snackbar.LENGTH_LONG).show();
    }
}
