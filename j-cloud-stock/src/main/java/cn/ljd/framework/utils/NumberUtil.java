package cn.ljd.framework.utils;

public class NumberUtil {
    public static boolean equals(Integer int1, Integer int2){
        if(int1 == null && int2 == null){
            return true;
        }

        if(int1 == null || int2 == null){
            return false;
        }

        return int1.equals(int2);
    }
}
