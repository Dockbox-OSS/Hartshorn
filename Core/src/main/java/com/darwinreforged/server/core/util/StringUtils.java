package com.darwinreforged.server.core.util;


import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 The type String utils.
 */
public class StringUtils {

    /**
     Replace from map string.

     @param string
     the string
     @param replacements
     the replacements

     @return the string
     */
    public static String replaceFromMap(String string, Map<String, String> replacements) {
        StringBuilder sb = new StringBuilder(string);
        int size = string.length();
        Iterator var4 = replacements.entrySet().iterator();

        while(var4.hasNext()) {
            Entry<String, String> entry = (Entry)var4.next();
            if (size == 0) {
                break;
            }

            String key = (String)entry.getKey();
            String value = (String)entry.getValue();

            int nextSearchStart;
            for(int start = sb.indexOf(key, 0); start > -1; start = sb.indexOf(key, nextSearchStart)) {
                int end = start + key.length();
                nextSearchStart = start + value.length();
                sb.replace(start, end, value);
                size -= end - start;
            }
        }

        return sb.toString();
    }

}
