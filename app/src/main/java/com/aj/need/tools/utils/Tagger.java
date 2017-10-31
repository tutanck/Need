package com.aj.need.tools.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by joan on 30/10/2017.
 */

public class Tagger {

    public static String tags(String s) {
        String tags = "";
        for (String word : tagList(s))
            tags += "#" + word+" ";
        return tags;

    }


    public static List<String> tagList(String s) {
        return Arrays.asList(s.trim().split(PatternsHolder.blank));
    }
}
