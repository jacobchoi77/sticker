package com.masterpiece.sticker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static void setTextWatcher(final Drawable checkedDrawable, final Drawable unCheckedDrawable,
                                      final TextView textView, final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    textView.setCompoundDrawables(unCheckedDrawable, null, null, null);
                } else {
                    textView.setCompoundDrawables(checkedDrawable, null, null, null);
                }
            }
        });
    }

    public static void setFacebookConnection(Context context, boolean connect) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean current = sp.getBoolean(Constants.KEY_FACEBOOK_CONNECT, false);
        if (current == connect) {
            return;
        } else {
            sp.edit().putBoolean(Constants.KEY_FACEBOOK_CONNECT, connect).commit();
        }
    }

    public static void setInstagramConnection(Context context, boolean connect) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean current = sp.getBoolean(Constants.KEY_INSTAGRAM_CONNECT, false);
        if (current == connect) {
            return;
        } else {
            sp.edit().putBoolean(Constants.KEY_INSTAGRAM_CONNECT, connect).commit();
        }
    }

    public static Date stringToDate(String dateString, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date, String format) {
        if (date == null)
            return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);
        return dateFormat.format(date);
    }

    public static String intTo3Format(int i) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
        return decimalFormat.format(i);
    }

}
