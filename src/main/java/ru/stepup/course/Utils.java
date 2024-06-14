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
}
