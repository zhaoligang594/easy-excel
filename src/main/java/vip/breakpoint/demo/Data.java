package vip.breakpoint.demo;

import vip.breakpoint.annotation.ExcelField;

import java.util.Date;

/**
 * @author : breakpoint
 * create on 2022/08/27
 * 欢迎关注公众号 《代码废柴》
 */
// 注意访问的权限  public 可以访问其他的情况有可能 获取不到数据
public class Data {

    @ExcelField(name = "名字")
    private String name;
    @ExcelField(name = "年龄")
    private Integer age;

    @ExcelField(name = "日期")
    private Date date;
    @ExcelField(name = "其他信息", selectValues = {"AA", "BB", "CC"})
    private String message;

    public Data() {
    }

    public Data(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "Data{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", date=" + date +
                '}';
    }
}
