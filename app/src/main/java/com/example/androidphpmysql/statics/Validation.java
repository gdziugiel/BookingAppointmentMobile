package com.example.androidphpmysql.statics;

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPhoneNumber(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches() && target.length() == 9);
    }
}
