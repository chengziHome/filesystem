package me.ichengzi.filesystem.model.impl;

/**
 * Coding is pretty charming when you love it!
 *
 * Boot分区里面的属性对象
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 19:16
 */
public class BootAttr {

    private String name;
    private byte[] bytes;//最终要写入Boot分区的字节
    private int offset;
    private int len;
    private String content;
    private String strVal;//这里用十进制表示，但是向磁盘写入的时候需要转化为二进制
    private int intVal;
    private boolean isNumber;

    public BootAttr(int len) {
        this.len = len;
        bytes = new byte[len];
    }


    public BootAttr(int len, String strVal) {
        byte[] tmp = strVal.getBytes();
        if (tmp.length>len){
            throw new IllegalArgumentException("strVal的字节长度超过len指定的不匹配");
        }

        this.len = len;
        bytes = new byte[len];
        this.strVal = strVal;
        for (int i = 0; i < tmp.length; i++) {
            bytes[i] = tmp[i];
        }
        // TODO: 2017/5/9 是够要对未填满的地方进行填充

    }

    public BootAttr(int len, int intVal) {

        this.len = len;
        bytes = new byte[len];
        this.intVal = intVal;
        // TODO: 2017/5/9 编码的问题是真的蛋疼，搭建起来框架之后再去实现这个
        if(len == 1){
            bytes[0] = new Integer(intVal).byteValue();
        }else if(len == 2){

        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLen() {
        return len;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStrVal() {
        return strVal;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public int getIntVal() {
        return intVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public boolean isNumber() {
        return isNumber;
    }

    public void setNumber(boolean number) {
        isNumber = number;
    }
}
