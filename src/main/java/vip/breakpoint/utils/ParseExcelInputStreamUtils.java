package vip.breakpoint.utils;

import org.apache.poi.ss.usermodel.*;
import vip.breakpoint.annotation.ExcelField;
import vip.breakpoint.annotation.MParam;
import vip.breakpoint.base.BaseDataSupport;
import vip.breakpoint.exception.EasyExcelException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author : breakpoint
 * create on 2022/08/27
 * 欢迎关注公众号 《代码废柴》
 */
public class ParseExcelInputStreamUtils {

    // Map<String, Class<? extends T>> clazzMap  key:sheetName value:clazz msg
    public static <T> Map<String, List<T>> getDataMapFromCurrentInput(
            @MParam("文件流的信息") InputStream inputStream,
            @MParam("类信息") Map<String, Class<? extends T>> clazzMap) throws EasyExcelException {
        try {
            // 获取到工作本
            Workbook sheets = WorkbookFactory.create(inputStream);
            // 设置返回对象
            Map<String, List<T>> res = new HashMap<String, List<T>>();
            // 循环解析操作
            for (Map.Entry<String, Class<? extends T>> entry : clazzMap.entrySet()) {
                Sheet sheet = sheets.getSheet(entry.getKey());
                if (null == sheet) throw new EasyExcelException("没有对应的 sheet 等于" + entry.getKey() + ",请检查上传的文件");
                // 设置返回值 加入每一个页面的数据
                res.put(entry.getKey(), getDataList(sheet, entry.getValue()));
            } //end for Map.Entry<String, Class<? extends T>> entry : clazzMap.entrySet()
            // 获取到第一个数据
            Sheet sheetAt = sheets.getSheetAt(0);
            // 返回上传的数据
            return res;
        } catch (IOException e) {
            throw new EasyExcelException("解析上传信息失败 e={" + e.getMessage() + "}");
        }
    }

    // 解析上传的数据
    public static <T> List<T> getDataList(@MParam("输入流") InputStream inputStream,
                                          @MParam("返回的数据的类型") Class<? extends T> clazz) throws EasyExcelException {
        try {
            // 获取到工作本
            Workbook sheets = WorkbookFactory.create(inputStream);
            // 获取到第一个数据
            Sheet sheetAt = sheets.getSheetAt(0);
            // 返回上传的数据
            return getDataList(sheetAt, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new EasyExcelException("解析上传信息失败 e={" + e.getMessage() + "}");
        }
    }

    // 解析一个页面的数据
    private static <T> List<T> getDataList(Sheet sheet, Class<? extends T> clazz) throws EasyExcelException {
        List<T> res = new ArrayList<T>();
        List<String> fields = null;
        if (null != sheet) {
            for (Row row : sheet) {
                //如果第一行不是标题 那就去掉
                if (row.getRowNum() == 0) {
                    if (null == fields) fields = getTitleMethodList(row);
                    continue;
                }
                try {
                    T data = getDataFromRow(row, clazz, fields);
                    res.add(data);
                } catch (EasyExcelException e) {
                    throw new EasyExcelException(e.getMessage());
                } catch (Exception e1) {
                    //  nothing to do
                    e1.printStackTrace();
                }
            }// end for
        } // end if
        return res;
    }// end method

    // 获取数据 根据数据行 来获取
    private static <T> T getDataFromRow(Row row, Class<? extends T> clazz, List<String> fields)
            throws EasyExcelException {
        // 临时数据的存储 excel表里面的数据
        Map<String, Object> valMap = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            valMap.put(fields.get(i), getRealVal(row.getCell(i)));
        }
        try {
            // 获取构造函数 默认获取无参数的构造方法
            Constructor<? extends T> constructor = clazz.getConstructor();
            // 创建一个新的返回对象
            T res = constructor.newInstance();
            // 获取到所有的属性
            List<Field> declaredFields = ReflectUtils.getFieldsFromClazz(clazz);
            // 设置我们属性值
            if (declaredFields.size() > 0) {
                for (Field field : declaredFields) {
                    // 获取到注释
                    ExcelField annotation = field.getAnnotation(ExcelField.class);
                    // 没有注解的 那么就 进行 其他属性的操作
                    if (annotation == null || !annotation.isMouldColumn()) {
                        continue;
                    }
                    String key = annotation.name();
                    Object oriValue = valMap.get(key);
                    if (null == oriValue) {
                        continue;
                    }
                    // field type
                    Class<?> type = field.getType();
                    Object value = null;
                    if (type == Integer.class || type == int.class) {
                        value = JavaTypeUtils.getTargetValue(oriValue, Integer.class);
                    }
                    if (type == Long.class || type == long.class) {
                        value = JavaTypeUtils.getTargetValue(oriValue, Long.class);
                    }
                    if (type == Double.class || type == double.class) {
                        value = JavaTypeUtils.getTargetValue(oriValue, Double.class);
                    }
                    if (type == String.class) {
                        String targetVal = null;
                        // 暂时仅仅string的数据需要进行校验的操作
                        if (annotation.selectValues().length > 0) {
                            // 获取到目标的值
                            targetVal = (String) valMap.get(key);
                            if (annotation.selectValues().length > 0) {
                                Set<String> allSelectValue =
                                        new HashSet<>(Arrays.asList(annotation.selectValues()));
                                if (!allSelectValue.contains(targetVal)) {
                                    throw new EasyExcelException("请下拉选择excel表中的" + key + "列的数据");
                                }
                            }
                        }
                        // 解决科学记数法的问题
                        if (valMap.get(key) instanceof Double) {
                            BigDecimal bigDecimal = new BigDecimal(((Double) valMap.get(key)));
                            targetVal = bigDecimal.toPlainString();
                        } else {
                            targetVal = (String) valMap.get(key);
                        }
                        value = targetVal;
                    }
                    if (type == Date.class) {
                        // 日期的类型的比较特殊
                        // 解析日期数据
                        Object val = valMap.get(key);
                        if (val instanceof Date) {
                            value = val;
                        } else {
                            value = BaseDataSupport.parseDateStr((String) val);
                        }
                    }
                    ReflectUtils.setFieldValue2Object(field, res, value);
                }
            }
            return res;
        } catch (NoSuchMethodException e) {
            throw new EasyExcelException("请保留无参数的构造方法");
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e2) {
            e2.printStackTrace();
            throw new EasyExcelException("创建新的对象失败");
        }// end
    }// end method

    // 获取相应的 excel 的第一行的数据
    private static <T> List<String> getTitleMethodList(Row row) throws EasyExcelException {
        short lastCellNum = row.getLastCellNum();
        List<String> res = new ArrayList<>();
        for (int i = 0; i < (int) lastCellNum; i++) {
            String key = row.getCell(i).getStringCellValue();
            // 仅仅取到前面的数据
            if (null == key || "".equals(key)) break;
            if (res.contains(key)) throw new EasyExcelException("具有相同的意思的属性，不满足单一性");
            res.add(key);
        }
        return res;
    }

    // 获取真实的值
    private static Object getRealVal(Cell cell) {
        if (null == cell) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC: {
                // NUMERIC(0),
                if (!"General".equals(cell.getCellStyle().getDataFormatString())) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return null;
        }
    }
}
