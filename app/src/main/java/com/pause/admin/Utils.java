package com.pause.admin;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static final int TIME_DELAY = 2000;
    public static long back_pressed;

    public static void spanText(int start, int end){

    }

    public static boolean backTwice(Context c){
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            return true;
        }
        Toast.makeText(c, "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
        return false;
    }

}
