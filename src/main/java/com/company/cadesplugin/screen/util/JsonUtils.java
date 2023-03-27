package com.company.cadesplugin.screen.util;

import elemental.json.JsonArray;

import java.util.HashMap;
import java.util.Map;

public class JsonUtils {
    public static Map<String, String> jsonArraysToMap(JsonArray nameArray, JsonArray valueArray) {
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < nameArray.length(); i++) {
            result.put(nameArray.getString(i), valueArray.getString(i));
        }
        return result;
    }
}
