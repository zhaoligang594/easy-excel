package vip.breakpoint.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import vip.breakpoint.exception.EasyExcelException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * java对象直接写本地文件
 *
 * @author : breakpoint
 * create on 2022/08/29
 * 欢迎关注公众号 《代码废柴》
 */
public class ExcelWriteLocalStoreUtils {

    private static final String DEFAULT_SHEET_NAME = "sheet1";
    private static final String DEFAULT_FILE_SUFFIX = ".xls";

    /**
     * 写入文件
     *
     * @param absoluteStorePath store excel file path
     * @param fileName          file name
     * @param sheetName         sheet name
     * @param data              the data
     * @param <T>               java class
     */
    public static <T> void writeJavaObjectToExcelFile(String absoluteStorePath, String fileName, String sheetName,
                                                      List<T> data) throws EasyExcelException {
        if (null == data || data.size() <= 0) {
            return;
        }
        HSSFWorkbook hssfWorkbook = HSSFWorkbookUtils.getHSSFWorkbook(sheetName, data, false);
        String realFilePath = absoluteStorePath + File.separator + fileName + DEFAULT_FILE_SUFFIX;
        try {
            FileOutputStream out = new FileOutputStream(realFilePath);
            hssfWorkbook.write(out);
            out.close();
        } catch (IOException e) {
            throw new EasyExcelException(e.getMessage(), e);
        }
    }

    /**
     * 写入文件
     *
     * @param absoluteStorePath store excel file path
     * @param fileName          file name
     * @param data              the data
     * @param <T>               java class
     */
    public static <T> void writeJavaObjectToExcelFile(String absoluteStorePath, String fileName,
                                                      List<T> data) throws EasyExcelException {
        writeJavaObjectToExcelFile(absoluteStorePath, fileName, DEFAULT_SHEET_NAME, data);
    }
}
