package vip.breakpoint.base;


import vip.breakpoint.annotation.MParam;
import vip.breakpoint.exception.EasyExcelException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author breakpoint/赵先生
 * 2020/10/13
 */
public abstract class BaseDataSupport {

    // 解析日期的操作
    public static Date parseDateStr(@MParam("日期字符串") String source) throws EasyExcelException {
        if (null == source || "".equals(source)) {
            return null;
        }
        String pattern = "";
        if (source.matches("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$")) {
            pattern = "yyyy-MM-dd";
        } else if (source.matches("^(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$")) {
            pattern = "HH:mm:ss";
        } else if (source.matches("^[1-9]\\d{3}/(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])$")) {
            pattern = "yyyy/MM/dd";
        } else if (source.matches("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$")) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        } else {
            throw new EasyExcelException("not match eg:2020-11-11 or 2020-11-11 11:11:11");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            Date parse = simpleDateFormat.parse(source);
            return parse;
        } catch (ParseException e) {
            //e.printStackTrace();
            throw new EasyExcelException(e.getMessage());
        }
    }
}
