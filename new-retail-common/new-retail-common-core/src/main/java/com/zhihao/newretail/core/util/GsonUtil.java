package com.zhihao.newretail.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

/*
 * @Project: NewRetail-Cloud
 * @Author: Zhihao
 * @Email: cafebabe0508@163.com
 * */
public class GsonUtil {

    private static final Gson gson = new GsonBuilder().create();

    public static String obj2Json(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T json2Obj(String jsonStr, Class<T> objClass) {
        return gson.fromJson(jsonStr, objClass);
    }

    public static <T> List<T> json2List(String jsonStr, Class<T[]> clazz) {
        T[] arr = gson.fromJson(jsonStr, clazz);
        return Arrays.asList(arr);
    }

}
