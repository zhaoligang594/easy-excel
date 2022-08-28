package vip.breakpoint.utils;

/**
 * @author : breakpoint
 * create on 2022/08/27
 * 欢迎关注公众号 《代码废柴》
 */
public class JavaTypeUtils {

    // GET VALUE
    @SuppressWarnings("unchecked")
    public static <T> T getTargetValue(Object originValue, Class<T> clazz) {
        if (originValue.getClass() == clazz) return (T) originValue;
        if (clazz == Integer.class || clazz == int.class) {
            if (originValue instanceof Double) {
                return (T) (Object) ((Double) originValue).intValue();
            } else if (originValue instanceof String) {
                return (T) (Integer.valueOf((String) originValue));
            }
            return null;
        }
        if (clazz == Long.class || clazz == long.class) {
            if (originValue instanceof Double) {
                return (T) (Object) ((Double) originValue).longValue();
            } else if (originValue instanceof String) {
                return (T) Long.valueOf((String) originValue);
            }
            return null;
        }
        if (clazz == Double.class || clazz == double.class) {
            return (T) Double.valueOf((String) originValue);
        }
        return null;
    }
}
