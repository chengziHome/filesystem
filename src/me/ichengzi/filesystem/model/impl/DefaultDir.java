package me.ichengzi.filesystem.model.impl;

import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import me.ichengzi.filesystem.model.Dictionary;
import me.ichengzi.filesystem.model.Item;
import me.ichengzi.filesystem.model.Sector;
import me.ichengzi.filesystem.util.Constant;

import java.util.ArrayList;
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

    public DefaultDir() {
    }

    public DefaultDir(Item item) {
        this.item = item;
        loadSectors();
    }

    private void loadSectors(){
        sectors = manager.getData().load(this.item);
        byte[] tmp = new byte[Constant.SECTOR_SIZE * sectors.size()];
        for (int i = 0; i < sectors.size(); i++) {
            Sector sector = sectors.get(i);
            System.arraycopy(sector.getBytes(),0,tmp,i*Constant.SECTOR_SIZE,Constant.SECTOR_SIZE);
        }
        int pos = 0;
        int len = tmp.length;
        items = new ArrayList<>();
        while(pos<len){
            Item item1 = new DefaultItem(Arrays.copyOfRange(tmp,pos,pos+Constant.ITEM_SIZE));
            items.add(item1);
            pos += Constant.ITEM_SIZE;
        }

    }



    @Override
    public Item getItem() {
        return item;
    }


    /**
     * 每次获取目录的Items项的时候，都要是从扇区中提取加载。
     * 不要误以为这里没有做缓存，缓存是坐在Data的扇区表里面的，
     * 这里的过程有点类似于真实场景中的“编码解码”过程。
     * 所以这个方法名字虽然叫getItems但事实上每次items都要去更新,相当于每一次都要初始化
     * @return
     */
    @Override
    public List<Item> getItems() {
        loadSectors();//更新items属性

        List<Item> result = new ArrayList<>();
        for (Item item:items){
            if (item.getFirstByte()==Constant.ITEM_FIRST_NOUSE || item.getFirstByte() == Constant.ITEM_FIRST_DISABLED)
                continue;
            result.add(item);
        }
        return items;
    }

    @Override
    public void addItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            Item tmp = items.get(i);
            byte firstByte = tmp.getFirstByte();
            if(firstByte == Constant.ITEM_FIRST_DISABLED || firstByte == Constant.ITEM_FIRST_NOUSE){
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

    /**
     * 除了删除表项 ，并且持久化之外，还要报缓存清空
     * @param name
     */
    @Override
    public void remove(String name) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (name.equals(item.getDir_Name())){
                item.setFirstByte(Constant.ITEM_FIRST_DISABLED);
            }
        }





    }

    @Override
    public void store() {
        for (int i = 0; i < sectors.size(); i++) {
            Sector sector = sectors.get(i);
            sector.store();
        }
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
    public boolean hasAvailable() {
        for (int i = 0; i < items.size(); i++) {
            if (item.getFirstByte()==Constant.ITEM_FIRST_DISABLED || item.getFirstByte()==Constant.ITEM_FIRST_NOUSE){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRootItem() {
        return false;
    }

    @Override
    public byte[] getBytes() {
        return item.getBytes();
    }

    @Override
    public String getDir_Name() {
        return item.getDir_Name();
    }

    @Override
    public void setDir_Name(String name) {
        item.setDir_Name(name);
    }

    @Override
    public int getDir_Attr() {
        return item.getDir_Attr();
    }

    @Override
    public void setDir_Attr(int val) {
        item.setDir_Attr(val);
    }


    @Override
    public String getReserved() {
        return item.getReserved();
    }

    @Override
    public void setReserve(String name) {
        item.setReserve(name);
    }

    @Override
    public int getDir_WrtTime() {
        return item.getDir_WrtTime();
    }

    @Override
    public void setDir_WrtTime(int time) {
        item.setDir_WrtTime(time);
    }



    @Override
    public int getDir_WrtDate() {
        return item.getDir_WrtDate();
    }

    @Override
    public void setDir_WrtDate(int date) {
        item.setDir_WrtDate(date);
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
    public String getFormatTime() {
        return item.getFormatTime();
    }

    @Override
    public String getFormatDate() {
        return item.getFormatDate();
    }

    @Override
    public void setFormatDateTime() {
        item.setFormatDateTime();
    }

    @Override
    public byte getFirstByte() {
        return item.getFirstByte();
    }

    @Override
    public void setFirstByte(byte b) {
        item.setFirstByte(b);
    }

    @Override
    public String getAbsolutePath() {
        return null;
    }

    @Override
    public String setAbsolutePath(String absolutePath) {
        return null;
    }


}
