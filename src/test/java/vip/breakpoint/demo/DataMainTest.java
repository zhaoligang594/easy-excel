package vip.breakpoint.demo;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;
import vip.breakpoint.utils.HSSFWorkbookUtils;
import vip.breakpoint.utils.ParseExcelInputStreamUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : breakpoint
 * create on 2022/08/27
 * 欢迎关注公众号 《代码废柴》
 */
class DataMainTest {

    private final String dir = "C:\\work\\idea_work\\";

    @Test
    public void test01() throws Exception {
        List<Data> list = new ArrayList<>();
        list.add(new Data("lisi", 30));
        list.add(new Data("wangwu", 26));
        HSSFWorkbook hssfWorkbook = HSSFWorkbookUtils.getHSSFWorkbook("sheet1", list, false);
        FileOutputStream out = new FileOutputStream(dir + "2022.08.27" + ".xls");
        hssfWorkbook.write(out);
        out.close();
        System.out.println("==okk==");
    }

    @Test
    public void test02() throws Exception {
        FileInputStream inputStream = new FileInputStream(dir + "2022.08.27" + ".xls");
        List<Data> dataList = ParseExcelInputStreamUtils.getDataList(inputStream, Data.class);
        System.out.println(dataList);
        System.out.println("==okk==");
    }
}