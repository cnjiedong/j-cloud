package cn.ljd.framework.utils;

import cn.ljd.framework.po.StockQuotePo;
import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    private ListUtil(){

    }

    public static List<List<StockQuotePo>> groupList(List<StockQuotePo> list, int number) {
        List<List<StockQuotePo>> listGroup = new ArrayList<>();
        int listSize = list.size();
        //子集合的长度
        int toIndex = number;
        for (int i = 0; i < list.size(); i += number) {
            if (i + number > listSize) {
                toIndex = listSize - i;
            }
            List<StockQuotePo> newList = list.subList(i, i + toIndex);
            listGroup.add(newList);
        }
        return listGroup;
    }

    public static List<List<Long>> groupLongList(List<Long> list, int number) {
        List<List<Long>> listGroup = new ArrayList<>();
        int listSize = list.size();
        //子集合的长度
        int toIndex = number;
        for (int i = 0; i < list.size(); i += number) {
            if (i + number > listSize) {
                toIndex = listSize - i;
            }
            List<Long> newList = list.subList(i, i + toIndex);
            listGroup.add(newList);
        }
        return listGroup;
    }
}
