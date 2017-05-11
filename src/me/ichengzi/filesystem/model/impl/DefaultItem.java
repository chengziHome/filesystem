package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Item;
import me.ichengzi.filesystem.model.Sector;
import me.ichengzi.filesystem.util.Byte2Int;
import me.ichengzi.filesystem.util.Byte2String;
import me.ichengzi.filesystem.util.Constant;

import java.util.Arrays;
import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:03
 */
public class DefaultItem implements Item {

    /**
     * 其实Item里面这个byte数组是否保有都可以，因为这些Byte在Dir里面必然是有的
     *
     */
    private byte[] bytes;
    private String dir_Name;
    private int dir_Attr;
    private String reserved;
    private int dir_WrtTime;
    private int dir_WrtDate;
    private int dir_FstClus;
    private int dir_FileSize;




    public DefaultItem(byte[] bs) {
        this.bytes = bs;
        setDir_Name(Byte2String.valueOf(Arrays.copyOfRange(bytes,0,0xB)));
        setDir_Attr(Byte2Int.getInt(Arrays.copyOfRange(bytes,0xB,0xC)));
        setReserve(Byte2String.valueOf(Arrays.copyOfRange(bytes,0xC,0x16)));
        setDir_WrtTime(Byte2Int.getInt(Arrays.copyOfRange(bytes,0x16,0x18)));
        setDir_WrtDate(Byte2Int.getInt(Arrays.copyOfRange(bytes,0x18,0x1A)));
        setDir_FstClus(Byte2Int.getInt(Arrays.copyOfRange(bytes,0x1A,0x1C)));
        setDir_FileSize(Byte2Int.getInt(Arrays.copyOfRange(bytes,0x1c,0x20)));
    }

    @Override
    public String getDir_Name() {
        return dir_Name;
    }

    @Override
    public void setDir_Name(String name) {
        this.dir_Name = name;
    }

    @Override
    public int getDir_Attr() {
        return dir_Attr;
    }


    @Override
    public void setDir_Attr(int val) {
        this.dir_Attr = val;
    }



    @Override
    public String getReserved() {
        return reserved;
    }

    @Override
    public void setReserve(String name) {
        this.reserved = name;
    }

    @Override
    public int getDir_WrtTime() {
        return dir_WrtTime;
    }

    @Override
    public void setDir_WrtTime(int time) {
        this.dir_WrtTime = time;
    }

    @Override
    public int getDir_WrtDate() {
        return dir_WrtDate;
    }

    @Override
    public void setDir_WrtDate(int date) {
        this.dir_WrtDate = date;
    }

    @Override
    public int getDir_FstClus() {
        return dir_FstClus;
    }

    @Override
    public void setDir_FstClus(int val) {
        this.dir_FstClus = val;
    }

    @Override
    public int getDir_FileSize() {
        return dir_FileSize;
    }

    @Override
    public void setDir_FileSize(int val) {
        this.dir_FileSize = val;
    }


    @Override
    public byte getFirstByte() {
        return bytes[0];
    }

    @Override
    public void setFirstByte(byte b) {
        bytes[0] = b;
    }

    @Override
    public String getAbsolutePath() {
        return null;
    }

    @Override
    public String toString() {
        return "DefaultItem{" +
                "firstByte="+(int)getFirstByte()+
                "dir_Name='" + dir_Name + '\'' +
                ", dir_Attr=" + (dir_Attr== Constant.ITEM_ATTR_FILE?"file":"dir") +
                ", dir_WrtTime=" + dir_WrtTime +
                ", dir_WrtDate=" + dir_WrtDate +
                ", dir_FstClus=" + dir_FstClus +
                ", dir_FileSize=" + dir_FileSize +
                "}\n";
    }
}
