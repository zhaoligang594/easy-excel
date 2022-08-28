### Easy Excel 

Short for easy excel ,EE is a tool based on poi tech. 
Also, it can solve the excel download and parse it to java object.

### How to use it?

#### 0. pre condition

put this code block to your pom.xml in your project.

```xml
    <dependency>
        <groupId>vip.breakpoint</groupId>
        <artifactId>easy-excel</artifactId>
        <version>0.1.0</version>
    </dependency>
```

This is the first version of our tool. And, we would advance it for a long time.

Thank you very much for your use it in your project.
#### 1. demo code

```java
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
```

This is a demo to use this tool to explore a excel!

```java
    private final String dir = "C:\\work\\idea_work\\";
    @Test
    public void test02() throws Exception {
        FileInputStream inputStream = new FileInputStream(dir + "2022.08.27" + ".xls");
        List<Data> dataList = ParseExcelInputStreamUtils.getDataList(inputStream, Data.class);
        System.out.println(dataList);
        System.out.println("==okk==");
    }
```

This is a demo to parse the excel and new a java object.

### Download an excl file

####  STEP 1
put this map to your project
```xml
    <dependency>
        <groupId>vip.breakpoint</groupId>
        <artifactId>easy-excel</artifactId>
        <version>0.1.0</version>
    </dependency>
```
####  STEP 1
demo code

```java
    @GetMapping("testDownload")
    public void downExcel(HttpServletResponse response) throws Exception {
        List<Data> list = new ArrayList<>();
        list.add(new Data("lisi", 30));
        list.add(new Data("wangwu", 26));
        ExcelDownloadUtils.downLoadExcelByCurrentData(response, list, "测试", "sheet1");
        System.out.println("==okk==");
    }
```

The end !

### Related links:
None