package me.ichengzi.filesystem.model.impl;

import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
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
    return result;
}

    @Override
    public void addItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            Item tmp = items.get(i);
            byte firstByte = tmp.getFirstByte();
            if(firstByte == Constant.ITEM_FIRST_DISABLED || firstByte == Constant.ITEM_FIRST_NOUSE){
                items.add(i,item);
                //要把该item处修改对应的byte数组投射到对应的扇区上
                int item_num = Constant.SECTOR_SIZE/Constant.ITEM_SIZE;
                int a = i/item_num;
                int b = i%item_num;
                Sector sector = sectors.get(a);
                sector.setBytes(item.getBytes(),b* Constant.ITEM_SIZE);
                return;
            }
        }
        //如果到这里还没有返回，说明需要加入新的扇区。
        DefaultDiskManager manager = DefaultDiskManager.getManager();
        int[] sec_indexs = manager.getFAT1().getClusList(this.item.getDir_FstClus());
        int[] new_indexs = manager.getFAT1().ensure(this.item.getDir_FstClus(), sec_indexs.length+1);

        manager.getFAT1().store();
        //清空缓存后重新加载sectors同时也就更新了items链表，这个时候回多出16个空项。
        manager.getData().removeItem(this.item.getAbsolutePath());
        loadSectors();
        addItem(item);//注意这里并不是绝对递归，最多递归一次


    }

    @Override
    public Item find(String name) {
        for(Item item:items){
            if (name.equals(item.getDir_Name())){
                return item;
            }
        }
        return null;
    }


    @Override
    public void delete(Item item) {
        throw new UnsupportedOperationException("暂不使用这个方法");
//        for (Item tmp:items){
//            if(tmp.getDir_Name() == item.getDir_Name()){
//                tmp.setFirstByte((byte) 0xE5);
//            }
//
//        }
    }

    /**
     * 这个方法只能删除文件
     * @param name
     */
    @Override
    public void remove(String name) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (name.equals(item.getDir_Name())){
                item.setFirstByte(Constant.ITEM_FIRST_DISABLED);
                int item_num = Constant.SECTOR_SIZE/Constant.ITEM_SIZE;
                int a = i/item_num;
                int b = i%item_num;
                Sector sector = sectors.get(a);
                sector.setBytes(item.getBytes(),b* Constant.ITEM_SIZE);
                return;
            }
        }
    }


    /**
     * 删除整个目录的递归方法
     * 注意，这里在加载子目录的Sector链的时候直接手动加载，不通过内存管理模块，
     * 不然的话特别的麻烦。
     */
    @Override
    public void removeAllSubDir() {
        if (this.item.getAbsolutePath()==null){
            throw new IllegalArgumentException("要给本目录设置absolutePath属性");
        }
        //i从2开始略过头两项特殊项
        for (int i = 2; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getFirstByte()!=Constant.ITEM_FIRST_DISABLED && item.getFirstByte()!=Constant.ITEM_FIRST_NOUSE){
                if (item.getDir_Attr() == Constant.ITEM_ATTR_FILE){
                    remove(item.getDir_Name());
                }else{//如果是目录
                    item.setAbsolutePath(this.item.getAbsolutePath()+item.getDir_Name()+"/");
                    System.out.println("item.name:"+item.getDir_Name());
                    Dictionary dir = new DefaultDir(item);
                    dir.removeAllSubDir();
                }
                //每个目录项处理完之后都应该立即更新FAT表
                int[] sec_indexs = DefaultDiskManager .getManager().getFAT1().getClusList(item.getDir_FstClus());
                DefaultDiskManager.getManager().getFAT1().freeClusList(sec_indexs);
            }
        }
        //当本目录的所有目录项都处理完之后，应该讲Items的高级数据结构统一映射到sectors上面去。
        for (int i = 0; i < items.size(); i++) {
            int item_num = Constant.SECTOR_SIZE/Constant.ITEM_SIZE;
            int a = i/item_num;
            int b = i%item_num;
            Sector sector = sectors.get(a);
            sector.setBytes(item.getBytes(),b* Constant.ITEM_SIZE);
        }

        store();//将sectors的byte数组中的变化映射到Data数组中去
    }


    /**
     * 注意这里的store方法有两个任务：
     * 1，items高级数据结构转化到sectors的数组里面
     * 2,依次调用sectors里面的store方法。
     *
     *
     */
    @Override
    public void store() {

        /*
            但是又想，其实任务一应该放在addItem和deleteItem方法里面
         */

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
    public void setDir_Name(byte[] bs) {
        item.setDir_Name(bs);
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
