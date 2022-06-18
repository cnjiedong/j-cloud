package cn.ljd.framework.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @version V1.0
 * @description:
 * @author: liangjiedong
 * @date: 2019/7/23 11:58
 */
public class ValidateUtil {

    private static final int MIN_LENGTH = 11;
    private static final int MAX_LENGTH = 13;
    private static final String REGEX_DOUBLE = "^[-\\+]?[.\\d]*$";

    private ValidateUtil(){

    }



    public static boolean isDate(String strDate) {
        if (StringUtils.isEmpty(strDate)) {
            return true;
        }
        String str = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|" +
                "((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
                "((0[48]|[2468][048]|[3579][26])00))-02-29))" +
                "\\s([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        Pattern pattern = compile(str);

        return pattern.matcher(strDate).matches();
    }

    public static boolean isNumber(String strInt) {
        if (StringUtils.isEmpty(strInt)) {
            return true;
        }
        Pattern pattern = compile("[0-9]*");
        return pattern.matcher(strInt).matches();
    }

    public static boolean isDouble(String strDouble) {
        if (StringUtils.isEmpty(strDouble)) {
            return true;
        }
        Pattern pattern = compile(REGEX_DOUBLE);
        return pattern.matcher(strDouble).matches();
    }
}
