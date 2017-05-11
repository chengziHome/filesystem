package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.util.Constant;

import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:03
 */
public class DefaultFile implements File,Item{

    private Item item;
    private List<Sector> sectors;
    private String content;


    private static final DiskManager manager = DefaultDiskManager.getManager();

    /**
     * 要注意，当要创建文件对象的时候，所有的Item对象和Sector链表都是已经就绪的。
     * 无论是在查询操作，还是创建文件的操作中。
     * @param item
     */
    public DefaultFile(Item item) {
        this.item = item;
        sectors = manager.getData().load(this.item);
        byte[] tmp = new byte[Constant.SECTOR_SIZE * sectors.size()];
        for (int i = 0; i < sectors.size(); i++) {
            Sector sector = sectors.get(i);
            System.arraycopy(sector.getBytes(),0,tmp,i*Constant.SECTOR_SIZE,Constant.SECTOR_SIZE);
        }
        content = new String(tmp);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(byte[] bytes) {

    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public List<Sector> getSectors() {
        return sectors;
    }

    @Override
    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    @Override
    public String getDir_Name() {
        return item.getDir_Name();
    }

    @Override
    public void setDir_Name(String name) {
        this.item.setDir_Name(name);
    }

    @Override
    public int getDir_Attr() {
        return item.getDir_Attr();
    }


    @Override
    public void setDir_Attr(int val) {
        this.setDir_Attr(val);
    }



    @Override
    public String getReserved() {
        return item.getReserved();
    }

    @Override
    public void setReserve(String name) {
        this.item.setReserve(name);
    }

    @Override
    public int getDir_WrtTime() {
        return item.getDir_WrtTime();
    }

    @Override
    public void setDir_WrtTime(int time) {
        this.item.setDir_WrtTime(time);
    }

    @Override
    public int getDir_WrtDate() {
        return item.getDir_WrtDate();
    }

    @Override
    public void setDir_WrtDate(int date) {
        this.item.setDir_WrtDate(date);
    }

    @Override
    public int getDir_FstClus() {
        return item.getDir_FstClus();
    }

    @Override
    public void setDir_FstClus(int val) {
        item.setDir_FstClus(val);
    }

    @Override
    public int getDir_FileSize() {
        return item.getDir_FileSize();
    }

    @Override
    public void setDir_FileSize(int val) {
        item.setDir_FileSize(val);
    }

    @Override
    public byte getFirstByte() {
        return item.getFirstByte();
    }

    @Override
    public void setFirstByte(byte b) {
        item.setFirstByte(b);
    }
}
