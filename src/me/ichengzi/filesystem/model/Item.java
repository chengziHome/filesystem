package me.ichengzi.filesystem.model;

import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 目录项的抽象，一个目录项32bit
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 19:03
 */
public interface Item {


    boolean isRootItem();
    byte[] getBytes();
    /*
    目录项属性
     */
    String getDir_Name();
    void setDir_Name(byte[] bs);
    void setDir_Name(String name);
    int getDir_Attr();
    void setDir_Attr(int val);
    String getReserved();
    void setReserve(String name);
    int getDir_WrtTime();
    void setDir_WrtTime(int time);
    int getDir_WrtDate();
    void setDir_WrtDate(int date);
    int getDir_FstClus();
    void setDir_FstClus(int val);
    int getDir_FileSize();
    void setDir_FileSize(int val);

    String getFormatTime();
    String getFormatDate();
    void setFormatDateTime();


    byte getFirstByte();
    void setFirstByte(byte b);


    void store();


    /*
        业务中需要的一些实用的方法。
     */

    String getAbsolutePath();
    String setAbsolutePath(String absolutePath);


}
