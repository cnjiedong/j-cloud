package cn.ljd.framework.utils;


/**
 * @version V1.0
 * @description: 获取主键ID
 * @author: liangjiedong
 * @date: 2021/3/11 10:19
 */
public class SnowflakeIdUtil {

    static SnowflakeIdWorker snowflakeIdWorker;

    public static long nextId() {
        if (snowflakeIdWorker == null) {
            snowflakeIdWorker = SpringUtil.getBean(SnowflakeIdWorker.class);
        }
        return snowflakeIdWorker.nextId();
    }

}
