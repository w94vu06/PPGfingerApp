package com.example.myapplication.Util;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static boolean isValidEmailFormat(String email){
        if (email == null){
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPhoneLegal(String str) {
        if (str == null){
            return false;
        }
        String regExp = "09[0-9]{8}";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isHeightLegal(String height){
        if (height == null){
            return false;
        }
        Pattern p = Pattern.compile("[0-9]{3}");
        Matcher m = p.matcher(height);
        return m.matches();
    }
}
