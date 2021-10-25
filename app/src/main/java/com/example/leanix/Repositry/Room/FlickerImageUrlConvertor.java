package com.example.leanix.Repositry.Room;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlickerImageUrlConvertor {

    @TypeConverter
    public static String toString(List<String> flickerImageUrls) {
        StringBuilder sb = new StringBuilder();
        for(String url : flickerImageUrls) {
            sb.append(url).append("-_-");
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<String> toList(String flickerUrls) {
        String [] urls = flickerUrls.split("-_-");
        List<String> flickerUrlList = new ArrayList<>();
        flickerUrlList.addAll(Arrays.asList(urls));
        return flickerUrlList;
    }
}
