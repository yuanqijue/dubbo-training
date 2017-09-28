package com.xksh.dubbo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aaron on 2017/9/27.
 */
public class Monitor {

    private static Map<Class, Integer> countMap = new HashMap<Class, Integer>();

    public static void count(Class cls) {

        Integer count = countMap.get(cls);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        countMap.put(cls, count);
        System.out.println(cls + "调用" + count + "次");
    }
}
