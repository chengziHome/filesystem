package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Fat;
import me.ichengzi.filesystem.util.Constant;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:01
 */
public class FAT12 implements Fat{

    private byte[] bytes;

    private int[] array;


    public FAT12(int secNum) {
        bytes = new byte[secNum * Constant.SECTOR_SIZE];
    }

    /**
     * 用于加载FAT分区的数据结构
     * @param bs
     */
    public FAT12(byte[] bs){
        bytes = bs;
        int arr_len = bytes.length*2/3;//可能最后的余数部分的byte不能使用了，但是也没有必要，就当废弃掉了。

        array = new int[arr_len];
        for (int i = 0; i < arr_len; i++) {
            //这一部分的优先级顺序问题贼他妈烦，总之多多加括号就好了
            if (i%2==0){
                array[i] = ((Byte.toUnsignedInt(bytes[(i/2)*3+1])&0x0f)<<8) + Byte.toUnsignedInt(bytes[(i/2)*3]);
            }else{
                array[i] =
                        ((Byte.toUnsignedInt(bytes[(i/2)*3+2]))<<4)
                                + ((Byte.toUnsignedInt(bytes[(i/2)*3+1])&0xF0)>>4);
            }
        }
    }

    @Override
    public int[] getIntArray() {
        return array;
    }

    @Override
    public int get(int i) {
        return array[i];
    }

    @Override
    public int[] getClusList(int first) {
        Deque deque = new ArrayDeque<>();
        int currentPos = first;
        do {
            deque.addLast(currentPos);
            currentPos = array[currentPos];
        }while(currentPos != Constant.CLUS_LIST_END);
        int[] result = new int[deque.size()];
        int len = deque.size();
        for (int i = 0; i < len; i++) {
            result[i] = (int) deque.removeFirst();
        }
        return result;
    }

    @Override
    public void set(int index, int val) {
        array[index] = val;
    }

    /**
     * 注意哈，这里数组是个引用类型。如果有足够的空间，返回true，并且clus数组被赋值为簇链的索引。
     * 如果不能分配到足够的空间，那么返回false，就上层模块就不会再使用clus数组了
     * @param first
     * @param clus
     * @return
     */
    @Override
    public boolean setClusList(int first, int[] clus,int len) {
        int[] result = new int[len];
        int j = 0;
        for (int i = 2; i < array.length; i++) {
            if (array[i] == 0){//空闲簇
                result[j++] = i;
                if (j==len)
                    break;
            }
        }
        if (result[len-1] == 0){
            return false;
        }else{
            clus = result;
            return true;
        }
    }

    @Override
    public int[] getFreeClus(int len) {
        return new int[0];
    }
}
