package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Item;
import me.ichengzi.filesystem.model.Root;
import me.ichengzi.filesystem.util.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 注意哈，要把整个的根目录的数据全部记载，包括删除的记录和为使用的记录，
 * 所以这个高级数据结构的大小是固定的只不过是里面的属性和内容在变化。
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:02
 */
public class RootDir implements Root {

    private byte[] bytes;

    private int start;

    private List<Item> items;

    public RootDir(int itemNum) {
        bytes = new byte[itemNum * Constant.ITEM_SIZE];
    }

    public RootDir(byte[] bs){
        this.bytes = bs;
        int itemNum = bs.length/32;
        items = new ArrayList<>(itemNum);
        for (int i = 0; i < itemNum; i++) {
            Item item = new DefaultItem(Arrays.copyOfRange(bs,i*Constant.ITEM_SIZE,(i+1)*Constant.ITEM_SIZE));
            items.add(item);
        }
    }


    /**
     * 注意哈，按照业务要求，仅仅返回有效的Item项
     * @return
     */
    @Override
    public List<Item> getItems() {
        List<Item> result = new ArrayList<>();
        for (Item item:items){
            if (item.getFirstByte()==Constant.ITEM_FIRST_NOUSE || item.getFirstByte() == Constant.ITEM_FIRST_DISABLED)
                continue;
            result.add(item);
        }
        return result;
    }

    /**
     * 使用方法和FAT12的addClusList相似的
     * @param item
     * @return
     */
    @Override
    public boolean addItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            Item item1 = items.get(i);
            if (item1.getFirstByte()==0xE5 || item1.getFirstByte()==0x00){
                items.set(i,item);
                return true;
            }
        }
        return false;
    }

    @Override
    public Item find(String name) {
        for (Item item :items){
            if (name.equals(item.getDir_Name()) && item.getFirstByte()!=Constant.ITEM_FIRST_DISABLED && item.getFirstByte()!=Constant.ITEM_FIRST_NOUSE){
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean hasAvailable() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getFirstByte()==Constant.ITEM_FIRST_DISABLED || item.getFirstByte()==Constant.ITEM_FIRST_NOUSE)
                return true;
        }
        return false;
    }

    @Override
    public void remove(String name) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (name.equals(item.getDir_Name())){
                item.setFirstByte(Constant.ITEM_FIRST_DISABLED);
                //这里不能执行item.store，会把刚刚修改的首位byte覆盖掉
//                item.store();
                store();
            }
        }
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }


    /**
     * 将高级数据结构转化为byte数组
     */
    @Override
    public void store() {
        for (int i = 0; i < items.size(); i++) {
            int pos = start + i * Constant.ITEM_SIZE;
            Item item = items.get(i);
            byte[] bs = item.getBytes();
            for (int j = 0; j < Constant.ITEM_SIZE; j++) {
                bytes[pos+j] = bs[j];
            }
        }
    }


    @Override
    public String toString() {

        return "RootDir{" +
                "items=" + Arrays.toString(items.toArray()) +
                '}';
    }
}
