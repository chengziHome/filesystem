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


//    public BootAttr(int len, String strVal) {
//        byte[] tmp = strVal.getBytes();
//        if (tmp.length>len){
//            throw new IllegalArgumentException("strVal的字节长度超过len指定的不匹配");
//        }
//
//        this.len = len;
//        bytes = new byte[len];
//        this.strVal = strVal;
//        for (int i = 0; i < tmp.length; i++) {
//            bytes[i] = tmp[i];
//        }
//        // TODO: 2017/5/9 是够要对未填满的地方进行填充
//
//    }
//
//    public BootAttr(int len, int intVal) {
//
//        this.len = len;
//        bytes = new byte[len];
//        this.intVal = intVal;
//        // TODO: 2017/5/9 编码的问题是真的蛋疼，搭建起来框架之后再去实现这个
//        if(len == 1){
//            bytes[0] = new Integer(intVal).byteValue();
//        }else if(len == 2){
//
//        }
//
//    }

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

    /**
     * 这里也要同时修改byte数组,
     * 长度不够的话就补零(假设)
     * @param strVal
     */
    public void setStrVal(String strVal) {
        this.strVal = strVal;
        for (int i = 0; i < len; i++) {
            bytes[i] = 0;
        }
        byte[] bs = strVal.getBytes();
        for (int i = 0; i < bs.length; i++) {
            bytes[i] = bs[i];
        }
    }

    public int getIntVal() {
        return intVal;
    }

    /**
     * 这里需要同时修改byte数组，
     * 注意intVal值过大的判断已经在DefaultBoot的方法里面做过判断了，
     * 这里只需要考虑补零，依据是按照机器的“小端在头”的方式去填充byte数组。
     *
     * @param intVal
     */
    public void setIntVal(int intVal) {
        this.intVal = intVal;
        if (len==1){
            bytes[0] = new Integer(intVal).byteValue();
        }else if(len==2){
            bytes[0] = new Integer(intVal%256).byteValue();
            bytes[1] = new Integer(intVal/256).byteValue();
        }else if(len==4){//正数的原码、反码、补码都是一样的
            bytes[0] = new Integer(intVal%256).byteValue();
            intVal = intVal>>8;
            bytes[1] = new Integer(intVal%256).byteValue();
            intVal = intVal>>8;
            bytes[2] = new Integer(intVal%256).byteValue();
            intVal = intVal>>8;
            bytes[3] = new Integer(intVal%256).byteValue();
        }

    }

    public boolean isNumber() {
        return isNumber;
    }

    public void setIsNumber(boolean number) {
        isNumber = number;
    }
}
