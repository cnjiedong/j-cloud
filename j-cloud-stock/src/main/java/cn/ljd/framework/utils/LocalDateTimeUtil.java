package cn.ljd.framework.utils;

import org.apache.commons.lang.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 时间转换工具类
 * @author liangjiedong
 * @version V1.0
 * @date 2019/7/26 16:27
 * @description
 */
public class LocalDateTimeUtil {

    private LocalDateTimeUtil(){

    }

    /**
     * 毫秒转LocalDateTime
     * @param millSecond 毫秒
     * @return java.time.LocalDateTime
     * @throws
     * @author liangjiedong
     * @date 2019/7/26
     */
    public static LocalDateTime convertLong2LocalDateTime(Long millSecond) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millSecond),
                ZoneId.systemDefault());
    }

    /**
     * String转LocalDateTime
     * @param strDate yyyy-MM-dd HH:mm:ss
     * @return java.time.LocalDateTime
     * @throws
     * @author liangjiedong
     * @date 2019/7/26
     */
    public static LocalDateTime convertString2LocalDateTime(String strDate) {
        if(StringUtils.isEmpty(strDate)) {
            return null;
        }
        if(!ValidateUtil.isDate(strDate)){
            return null;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(strDate,df);
    }

    public static LocalDateTime convertString2LocalDateTime(String strDate, String format) {
        if(StringUtils.isEmpty(strDate)) {
            return null;
        }
        /*if(!ValidateUtil.isDate(strDate)){
            return null;
        }*/
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(strDate,df);
    }

    /**
     * 计算两个时间之间的天数，时间按照当天0点来计算
     *
     */
    public static long getDaysBetweenDates(LocalDateTime date1, LocalDateTime date2) {

        Duration duration = Duration.between(date1,  date2);
        return duration.toDays();
    }

    /**
     * 计算两个时间之间的天数，隔天只算1天
     *
     * @param startDate
     * @param endDate
     * @return long
     * @throws
     * @author liangjiedong
     * @date 2020/2/10
     */
    public static long getDiffDaysIgnoreStart(LocalDateTime startDate, LocalDateTime endDate) {
        return endDate.toLocalDate().toEpochDay() - startDate.toLocalDate().toEpochDay();
    }


    /**
     * 计算两个时间之间的月数
     *
     * @param date1
     * @param date2
     * @return long
     * @throws
     * @author gaocheng
     * @date 2019/3/26
     */
    public static int getMonthsBetweenDates(LocalDateTime date1, LocalDateTime date2) {
        int month1 = date1.getMonthValue();
        int year1 = date1.getYear();
        int month2 = date2.getMonthValue();
        int year2 = date2.getYear();
        return (year2 - year1) *12 + (month2 - month1);
    }

    public static LocalDateTime localDateTimeIgnoreTime(LocalDateTime localDateTime){
        return LocalDateTime.of(localDateTime.getYear(),
                localDateTime.getMonth(),localDateTime.getDayOfMonth(),0,0,0);
    }


    /**
     * LocalDateTime转String
     * @param  localDateTime
     * @return String yyyy-MM-dd HH:mm:ss
     * @throws
     * @author liangjiedong
     * @date 2019/7/26
     */
    public static String convertLocalDateTime2String(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return df.format(localDateTime);
    }

    public static String convertLocalDateTime2DateString(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return null;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return df.format(localDateTime);
    }

    public static String convertLocalDateTime2DateString(LocalDateTime localDateTime, String format) {
        if(localDateTime == null) {
            return null;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return df.format(localDateTime);
    }

    public static String getCurrentYearMonth() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
        return df.format(LocalDateTime.now());
    }

    public static String getPreYearMonth() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
        return df.format(LocalDateTime.now().minusMonths(1));
    }

    public static String getYearMonthOf(LocalDateTime dateTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
        return df.format(dateTime);
    }

    public static String getNextYearMonth(String yearMonth) {
        LocalDateTime dateTime = LocalDateTimeUtil.convertString2LocalDateTime(yearMonth.substring(0,4) + "-" + yearMonth.substring(4,6)+ "-01 00:00:00");
        dateTime = dateTime.plusMonths(1);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
        return df.format(dateTime);
    }

    public static boolean isHoliday(){
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek){
            return true;
        }

        return false;
    }

}
