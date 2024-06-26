package ru.stepup.course;

import java.lang.reflect.Proxy;

public class Utils {
    public static <T> T cache(T objectIncome){
        return (T) Proxy.newProxyInstance(
                objectIncome.getClass().getClassLoader(),
                objectIncome.getClass().getInterfaces(),
                new MakeCache(objectIncome)
        );
    }

    public static <T> T cacheWithClean(T objectIncome, int numElements){
        return (T) Proxy.newProxyInstance(
                objectIncome.getClass().getClassLoader(),
                objectIncome.getClass().getInterfaces(),
                new MakeCache(objectIncome, numElements)
        );
    }

}
