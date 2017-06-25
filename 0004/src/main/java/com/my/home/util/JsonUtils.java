package com.my.home.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 05.09.16
 * Time: 20:09
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtils
{
    public static <V> V getObject(String json, Class<V> cls)
    {
        Gson gson = new Gson();
        V out = gson.fromJson(json, cls);
        return out;
    }
    public static <V> String getJson(V object)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);
        return json;
    }
}
