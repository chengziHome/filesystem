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

    private boolean isRoot;

    /*
        临时变量
     */
    private String absolutePath;


    public DefaultItem() {
        bytes = new byte[Constant.ITEM_SIZE];
    }

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
    public boolean isRootItem() {
        return isRoot;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * 这里又是一个坑，dir_Name是一个固定长度的字符串，不够11位会用空格补齐
     * @return
     */
    @Override
    public String getDir_Name() {
        return dir_Name.trim();
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


    /**
     * 时间解析参照微软FAT规范pdf，第17页。
     * @return
     */
    @Override
    public String getFormatTime() {
        byte[] bs = Arrays.copyOfRange(bytes,0x16,0x18);
        int time = (Byte.toUnsignedInt(bs[1])<<8) + (Byte.toUnsignedInt(bs[0]));
        int second = (time&0x0000001F)*2;
        int minute = (time&0x000007E0)>>5;
        int hour = (time&0x0000F800)>>11;
        String formatTime = (hour<10?"0"+hour:hour) + ":" + (minute<10?"0"+minute:minute) + ":" + (second<10?"0"+second:second);

        return formatTime;
    }


    /**
     * 日期的解析方法，参照微软的FAT规范pdf，第17页。
     * @return
     */
    @Override
    public String getFormatDate() {
        byte[] bs = Arrays.copyOfRange(bytes,0x18,0x1A);
        int date = (Byte.toUnsignedInt(bs[1])<<8) + Byte.toUnsignedInt(bs[0]);
        int day = (date&0x0000001F);
        int mouth = (date&0x000001E0)>>5;
        int year = ((date&0x0000FE00)>>9)+1980;

        String formatDate = year + "/" + (mouth<10?"0"+mouth:mouth) + "/" + (day<10?"0"+day:day);
        return formatDate;
    }

    @Override
    public void setFormatDateTime() {

    }


    @Override
    public byte getFirstByte() {
        return bytes[0];
    }

    @Override
    public void setFirstByte(byte b) {
        bytes[0] = b;
    }


    /**
     * 主要是目录项会间接调用这个。
     */
    @Override
    public void store() {
        byte[] bs = Byte2String.getBytes(dir_Name);
        int pos = 0;
        while(pos<bs.length && pos<11){
            bytes[pos] = bs[pos];
            pos++;
        }
        bytes[11] = (byte) dir_Attr;
        byte[] bs1 = Byte2String.getBytes(reserved);
        int pos1 = 0 ;
        while(pos1<10 && pos1<bs1.length){
            bytes[pos1+12] = bs1[pos1];
            pos1++;
        }

        bytes[22] = new Integer(dir_WrtTime&0x00FF).byteValue();
        bytes[23] = new Integer((dir_WrtTime&0xFF00)>>8).byteValue();
        bytes[24] = new Integer((dir_WrtDate&0x00FF)).byteValue();
        bytes[25] = new Integer((dir_WrtDate&0xFF00)).byteValue();
        bytes[26] = new Integer((dir_FstClus&0x00FF)).byteValue();
        bytes[27] = new Integer((dir_FstClus&0xFF00)).byteValue();
        bytes[28] = new Integer(dir_FileSize&0x000000FF).byteValue();
        bytes[29] = new Integer((dir_FstClus&0x0000FF00)>>8).byteValue();
        bytes[30] = new Integer((dir_FstClus&0x00FF0000)>>16).byteValue();
        bytes[31] = new Integer((dir_FstClus&0xFF000000)>>24).byteValue();

    }


    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public String setAbsolutePath(String absolutePath) {
        return this.absolutePath = absolutePath;
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
