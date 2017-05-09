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

    /*
    目录项属性
     */
    String getDir_Name();
    void setDir_Name(String name);
    int getDir_Attr();
    void setDir_Attr(int val);
    String getReserved();
    void setReserve(String name);
    String getDir_WrtTime();
    void setDir_WrtTime(String time);
    String getDir_WrtDate();
    void setDir_WrtDate(String date);
    int getDir_FstClus();
    void setDir_FstClus(int val);
    int getDir_FileSize();
    void setDir_FileSize(int val);

    /*
        簇链表数据
     */
    List<Sector> getSector();
    void setSector(List<Sector> sectors);







}
