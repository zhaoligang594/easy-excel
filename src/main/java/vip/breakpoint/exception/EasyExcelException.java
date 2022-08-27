package vip.breakpoint.exception;

/**
 * @author : breakpoint
 * create on 2022/08/27
 * 欢迎关注公众号 《代码废柴》
 */
public class EasyExcelException extends Exception {

    public EasyExcelException(String message) {
        super(message);
    }

    public EasyExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyExcelException(Throwable cause) {
        super(cause);
    }
}
