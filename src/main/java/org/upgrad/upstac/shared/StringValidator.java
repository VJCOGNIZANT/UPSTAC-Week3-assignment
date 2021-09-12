package org.upgrad.upstac.shared;

public class StringValidator {

    public static boolean isNotEmptyOrNull(String str) {
        return null != str && !str.isEmpty();
    }

    public static boolean isNotEmptyOrNull(Object str) {
        return null != str;
    }

    public static boolean isNotEmptyOrNull(Integer str) {
        return null != str && str != 0;
    }

}
