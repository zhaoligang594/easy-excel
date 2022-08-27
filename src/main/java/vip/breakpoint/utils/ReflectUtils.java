package vip.breakpoint.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : breakpoint
 * create on 2022/08/27
 * 欢迎关注公众号 《代码废柴》
 */
public class ReflectUtils {

    /**
     * get all field
     *
     * @param clazz clazz for every class
     * @param <T>   type
     * @return all fields from this class
     */
    public static <T> List<Field> getFieldsFromClazz(Class<T> clazz) {
        List<Field> res = new ArrayList<Field>();
        Class<? super T> superclass = clazz.getSuperclass();
        if (null != superclass && !superclass.isInterface()) {
            // 将所有的 ExcelField 加入
            res.addAll(getFieldsFromClazz(superclass));
        }
        if (!clazz.isInterface()) {
            Field[] declaredFields = clazz.getDeclaredFields();
            res.addAll(new ArrayList<>(Arrays.asList(declaredFields)));
        }
        return res;
    }

    /**
     * get field value from special object
     *
     * @param field  field
     * @param object special object
     * @return field value
     */
    public static Object getFieldValueFromObj(Field field, Object object) throws
            IllegalArgumentException, IllegalAccessException {
        Object res = null;
        if (field.isAccessible()) {
            res = field.get(object);
        } else {
            field.setAccessible(true);
            res = field.get(object);
            field.setAccessible(false);
        }
        return res;
    }

    /**
     * set field value
     *
     * @param field  field
     * @param object special object
     * @param value  value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setFieldValue2Object(Field field, Object object, Object value) throws
            IllegalArgumentException, IllegalAccessException {
        if (value == null) return;
        Object res = null;
        if (field.isAccessible()) {
            field.set(object, value);
        } else {
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        }
    }
}
