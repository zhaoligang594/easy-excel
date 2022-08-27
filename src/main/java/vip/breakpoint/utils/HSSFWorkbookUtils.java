package vip.breakpoint.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import vip.breakpoint.annotation.ExcelField;
import vip.breakpoint.annotation.MParam;
import vip.breakpoint.constants.EasyExcelConstants;
import vip.breakpoint.exception.EasyExcelException;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import static vip.breakpoint.constants.EasyExcelConstants.OMIT_EXCEL_VALUE;

/**
 * @author : breakpoint
 * create on 2022/08/27
 * 欢迎关注公众号 《代码废柴》
 */
public class HSSFWorkbookUtils {

    //字体的大小
    private static final Short FONT_SIZE = EasyExcelConstants.FONT_SIZE;
    // 字体名字
    private static final String FONT_NAME = EasyExcelConstants.FONT_NAME;
    // 行高
    private static final Short LINE_HEIGHT = EasyExcelConstants.LINE_HEIGHT;
    // 最小的下拉的数量
    private static final Short MIN_SELECT_ROW_NUM = EasyExcelConstants.MIN_SELECT_ROW_NUM;

    // 获取到 HSSFWorkbook 下载我们的数据
    public static <T> HSSFWorkbook getHSSFWorkbook(String sheetName, List<T> data,
                                                   @MParam("是否下载模版") boolean isMould) throws EasyExcelException {
        if (null == data || data.size() == 0) {
            throw new EasyExcelException("没有数据，无法得到 HSSFWorkbook");
        }
        if (null == sheetName || "".equals(sheetName)) {
            throw new EasyExcelException("sheetName is empty");
        }
        HSSFWorkbook workbook = new HSSFWorkbook();//这里也可以设置sheet的Name
        //创建工作表对象
        HSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultRowHeightInPoints(LINE_HEIGHT);
        sheet.setDefaultColumnWidth(LINE_HEIGHT);

        // 设置单元格的格式
        HSSFFont font = workbook.createFont();
        // 获取到单元的样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 设置数据的格式
        HSSFDataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("General"));
        font.setFontName(FONT_NAME);
        // 设置大小
        font.setFontHeightInPoints(FONT_SIZE);
        // 设置格式的基本信息
        cellStyle.setFont(font);
        // 设置对齐的格式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        setSheetVal(sheet, data, isMould, cellStyle, font);
        return workbook;
    }

    // 生成多页的信息 key: sheetName val: message
    public static <T> HSSFWorkbook getHSSFWorkbook(Map<String, List<T>> dataMapVal,
                                                   @MParam("是否下载模版") boolean isMould) throws EasyExcelException {
        if (null == dataMapVal || dataMapVal.size() == 0) {
            throw new EasyExcelException("没有数据，无法导出");
        }
        HSSFWorkbook workbook = new HSSFWorkbook();//这里也可以设置sheet的Name

        // 设置单元格的格式
        HSSFFont font = workbook.createFont();
        // 获取到单元的样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 设置数据的格式
        HSSFDataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("General"));
        font.setFontName(FONT_NAME);
        // 设置大小
        font.setFontHeightInPoints(FONT_SIZE);
        // 设置格式的基本信息
        cellStyle.setFont(font);
        // 设置对齐的格式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 循环的输出数据
        for (Map.Entry<String, List<T>> entry : dataMapVal.entrySet()) {
            List<T> data = entry.getValue();
            HSSFSheet sheet = workbook.createSheet(entry.getKey());
            // 设置 val
            setSheetVal(sheet, data, isMould, cellStyle, font);
        } // end for Map.Entry<String, List<T>> entry : data.entrySet()
        return workbook;
    }

    // 设置 val 给每一个 sheet
    private static <T> void setSheetVal(HSSFSheet sheet, List<T> data, @MParam("是否下载模版") boolean isMould,
                                        @MParam("单元格的格式") HSSFCellStyle cellStyle, HSSFFont font) throws EasyExcelException {
        // 设置header的行
        HSSFRow header = sheet.createRow(0);//设置第一行，从零开始 设置 header
        // 设置header的行高
        header.setHeightInPoints(LINE_HEIGHT);
        // 获取到第一列的数据
        Map<String, Object> headerData = getDataMap(data.get(0), true, isMould);
        // 获取到Excel的header的信息
        ArrayList<ExcelField> headerTitle = (ArrayList) headerData.get(HEADER_KEY);

        // 设置模版的颜色 如果是模版的化话 那么就进行 设置为红色 其他的情况设置为 普通的
        short cellColor = isMould ? Font.COLOR_RED : Font.COLOR_NORMAL;
        // 设置颜色
        font.setColor(cellColor);

        // 获取最大的列宽
        Map<Integer, Integer> maxWith = new HashMap<>();

        // 循环的操作
        for (int i = 0; i < headerTitle.size(); i++) {
            // 设置默认的格式信息
            sheet.setDefaultColumnStyle(i, cellStyle);
            // 设置标题
            HSSFCell cell = header.createCell(i);
            setCellValue(headerTitle.get(i).name(), cell, i, maxWith);
            // 只有多选以及下载模板的时候 才会有效果的
            if (headerTitle.get(i).selectValues().length > 0 && isMould) {
                // 设置最后的下拉框
                int lastRow = data.size() < MIN_SELECT_ROW_NUM ? MIN_SELECT_ROW_NUM : data.size();
                DataValidationHelper helper = sheet.getDataValidationHelper();
                DataValidationConstraint constraint = helper.createExplicitListConstraint(headerTitle.get(i).selectValues());
                CellRangeAddressList addressList = new CellRangeAddressList(1, lastRow, i, i);
                DataValidation dataValidation = helper.createValidation(constraint, addressList);
                sheet.addValidationData(dataValidation);
            }
        }


        HSSFRow row = sheet.createRow(1);
        row.setHeightInPoints(LINE_HEIGHT);
        for (int i = 0; i < headerTitle.size(); i++) {
            // 获取到值信息

            Object cellData = headerData.get(headerTitle.get(i).name());
            HSSFCell cell = row.createCell(i);
            // 标题一定是正常的颜色的
            setCellValue(cellData, cell, i, maxWith);
        }
        for (int i = 1; i < data.size(); i++) {
            HSSFRow innerRow = sheet.createRow(i + 1);
            // 设置行高
            innerRow.setHeightInPoints(LINE_HEIGHT);
            Map<String, Object> dataMap = getDataMap(data.get(i), false, isMould);
            for (int j = 0; j < headerTitle.size(); j++) {
                Object cellData = dataMap.get(headerTitle.get(j).name());
                HSSFCell cell = innerRow.createCell(j);
                // 数据需要设置真实的颜色的
                setCellValue(cellData, cell, j, maxWith);
            }
        }


        // 设置列宽
        for (int i = 0; i < headerTitle.size(); i++) {
            //sheet.autoSizeColumn(i);
            //sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 15 / 10);
            Integer width = maxWith.get(i);
            sheet.setColumnWidth(i, (int) (width * 1.5 * 256));
        }

    }

    // 设置值的操作 设置单元格的格式 以及
    private static void setCellValue(@MParam("数据信息") Object cellData, @MParam("单元格") HSSFCell cell,
                                     int index, Map<Integer, Integer> maxWith) {
        // 目标值
        String targetVal = null;
        if (cellData instanceof Integer) {
            targetVal = String.valueOf(cellData);
        } else if (cellData instanceof Long) {
            targetVal = String.valueOf(cellData);
        } else if (cellData instanceof Double) {
            // 保留6位有效数字
            //BigDecimal bigDecimal = new BigDecimal((double) Math.round(((Double) cellData) * 1000000) / 1000000);
            targetVal = String.valueOf((double) Math.round(((Double) cellData) * 1000000) / 1000000);
        } else if (cellData instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            targetVal = sdf.format((Date) cellData);
        } else if (cellData instanceof String) {
            targetVal = (String) cellData;
        } else {
            // 其他的情况的时候 我们的操作
            targetVal = String.valueOf(cellData);
        }
        if (null != targetVal && !OMIT_EXCEL_VALUE.equals(targetVal)) {
            // 设置数值
            cell.setCellValue(targetVal);
        }
        try {
            maxWith.put(index, Math.max(maxWith.getOrDefault(index, 3), targetVal.getBytes("utf-8").length));
        } catch (Exception e) {
            maxWith.put(index, 0);
        }
    }

    // header 的 key
    private static final String HEADER_KEY = "header_key";

    // 得到一个 对象 的map
    private static <T> Map<String, Object> getDataMap(
            @MParam("请求的数据信息") T data,
            @MParam("返回的结果里面是否具有header信息") boolean isAttachHeaderMsg,
            @MParam("是否下载模版") boolean isMould) throws EasyExcelException {
        Map<String, Object> res = null;
        List<ExcelField> tempList = new ArrayList<>();
        if (data != null) {
            res = new TreeMap<>();
            // 获取到实体类的信息
            Class<?> clazz = data.getClass();
            List<Field> declaredFields = ReflectUtils.getFieldsFromClazz(clazz);
            if (declaredFields.size() > 0) {
                for (Field field : declaredFields) {
                    // 获取到注释
                    ExcelField annotation = field.getAnnotation(ExcelField.class);
                    if (null == annotation) {
                        // next, omit the none ann
                        continue;
                    }
                    String key = field.getName();
                    if ("serialVersionUID".equals(key)) {
                        continue;
                    }
                    if (!"".equals(annotation.name())) {
                        key = annotation.name();
                    }
                    // 最小堆来进行排序的操作
                    if (annotation.isExplored()) { // 是否是导出列
                        // 操作的逻辑 如果是导出的列 查看当前是否是模版的下载，如果是的话 那么检查当前是痘需要这一列
                        if (!isMould || (isMould && annotation.isMouldColumn())) {
                            tempList.add(annotation);
                        }
                    } else if (isMould && annotation.isMouldColumn()) {
                        // 如果当前不是导出列 但是是模板列 如么就加入其中
                        tempList.add(annotation);
                    }
                    if (res.containsKey(key)) {
                        throw new EasyExcelException("具有相同的意思的属性，不满足单一性");
                    }
                    try {
                        // get field value
                        Object fieldValue = ReflectUtils.getFieldValueFromObj(field, data);
                        if (null != fieldValue) {
                            if (fieldValue instanceof Date) {
                                String pattern = annotation.datePattern();
                                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                                res.put(key, sdf.format(fieldValue));
                            } else if (fieldValue instanceof Boolean) {
                                // 实际保存的对象
                                Object targetVal = ((boolean) fieldValue) ? "true" : "false";
                                res.put(key, targetVal);
                            } else {
                                res.put(key, fieldValue);
                            }
                        }//
                    } catch (Exception e) {
                        // throw new BaseException(e.getMessage());
                        // nothing to do
                        continue;
                    }
                }
            }
        }
        // 是否进行保存
        if (res != null && isAttachHeaderMsg) {
            if (!res.containsKey(HEADER_KEY)) res.put(HEADER_KEY, getSortedRes(tempList));
        }
        return res;
    }

    // 稳定性排序
    private static List<ExcelField> getSortedRes(List<ExcelField> origin) {
        ExcelField[] excelFields = origin.toArray(new ExcelField[0]);
        for (int i = 0; i < excelFields.length; i++) {
            //boolean swap = false;
            for (int j = i; j < excelFields.length; j++) {
                if (excelFields[i].order() > excelFields[j].order()) {
                    ExcelField temp = excelFields[i];
                    excelFields[i] = excelFields[j];
                    excelFields[j] = temp;
                    //swap = true;
                }
            }
            //if (!swap) break;
        }
        List<ExcelField> res = new ArrayList<>();
        for (ExcelField field : excelFields) {
            res.add(field);
        }
        return res;
    }
}
