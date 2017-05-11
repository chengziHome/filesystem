package me.ichengzi.filesystem.model.impl;

import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import me.ichengzi.filesystem.model.Dictionary;
import me.ichengzi.filesystem.model.Item;
import me.ichengzi.filesystem.model.Sector;
import me.ichengzi.filesystem.util.Constant;

import java.util.Arrays;
import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 *
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:04
 */
public class DefaultDir implements Dictionary,Item {

    private Item item;
    /*
        注意哈，这里的Item的List是一个16的倍数，因为即使某些Item是0,或者e5也要表现出来。
     */
    private List<Item> items;
    private List<Sector> sectors;


    private static final DefaultDiskManager manager = DefaultDiskManager.getManager();

    public DefaultDir(Item item) {
        this.item = item;
        sectors = manager.getData().load(this.item);
        byte[] tmp = new byte[Constant.SECTOR_SIZE * sectors.size()];
        for (int i = 0; i < sectors.size(); i++) {
            Sector sector = sectors.get(i);
            System.arraycopy(sector.getBytes(),0,tmp,i*Constant.SECTOR_SIZE,Constant.SECTOR_SIZE);
        }
        int pos = 0;
        int len = tmp.length;
        while(pos<len){
            Item item1 = new DefaultItem(Arrays.copyOfRange(tmp,pos,pos+Constant.ITEM_SIZE));
            items.add(item1);
            pos += Constant.ITEM_SIZE;
        }
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public void addItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            Item tmp = items.get(i);
            byte firstByte = tmp.getFirstByte();
            if(firstByte == Constant.ITEM_FITST_DISABLED || firstByte == Constant.ITEM_FIRST_NOUSE){
                items.add(i,item);
                return;
            }
        }

        // TODO: 2017/5/11 要加入一个新的扇区



    }

    @Override
    public Item find(String name) {
        for(Item item:items){
            if (item.getDir_Name() == name){
                return item;
            }
        }
        return null;
    }

    @Override
    public void delete(Item item) {
        for (Item tmp:items){
            if(tmp.getDir_Name() == item.getDir_Name()){
                tmp.setFirstByte((byte) 0xE5);
            }
        }
    }

    @Override
    public void store() {

    }

    @Override
    public List<Sector> getSectors() {
        return null;
    }

    @Override
    public void setSectors(List<Sector> sectors) {

    }

    @Override
    public String getDir_Name() {
        return null;
    }

    @Override
    public void setDir_Name(String name) {

    }

    @Override
    public int getDir_Attr() {
        return 0;
    }

    @Override
    public void setDir_Attr(int val) {

    }


    @Override
    public String getReserved() {
        return null;
    }

    @Override
    public void setReserve(String name) {

    }

    @Override
    public int getDir_WrtTime() {
        return 0;
    }

    @Override
    public void setDir_WrtTime(int time) {

    }



    @Override
    public int getDir_WrtDate() {
        return 0;
    }

    @Override
    public void setDir_WrtDate(int date) {

    }



    @Override
    public int getDir_FstClus() {
        return 0;
    }

    @Override
    public void setDir_FstClus(int val) {

    }

    @Override
    public int getDir_FileSize() {
        return 0;
    }

    @Override
    public void setDir_FileSize(int val) {

    }

    @Override
    public byte getFirstByte() {
        return 0;
    }

    @Override
    public byte setFirstByte(byte b) {
        return 0;
    }


}
