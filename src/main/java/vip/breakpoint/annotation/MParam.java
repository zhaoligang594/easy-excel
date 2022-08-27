package vip.breakpoint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * parameters desc
 *
 * @author breakpoint/赵先生
 * 2020/09/23
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MParam {

    String value(); // 作用

    String example() default ""; // 示例
}
