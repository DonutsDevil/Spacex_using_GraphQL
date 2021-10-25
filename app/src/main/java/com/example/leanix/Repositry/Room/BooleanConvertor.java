package com.example.leanix.Repositry.Room;


import androidx.room.TypeConverter;

import com.example.leanix.Utils.Utils;

public class BooleanConvertor {

    @TypeConverter
    public static boolean toBoolean(Integer wasLaunchValue) {
        if (wasLaunchValue == Utils.LAUNCH_SUCCESS) {
            return true;
        }else {
            return false;
        }
    }

    @TypeConverter
    public static Integer toInt(boolean wasLaunchSuccess){
        if (wasLaunchSuccess) {
            return Utils.LAUNCH_SUCCESS;
        }else {
            return Utils.LAUNCH_FAIL;
        }
    }


}
