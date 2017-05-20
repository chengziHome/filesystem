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

    private int start;


    public FAT12(int secNum) {
        bytes = new byte[secNum * Constant.SECTOR_SIZE];
    }

    /**
     * 用于加载FAT分区的数据结构
     * @param bs
     */
    public FAT12(byte[] bs,int start){
        this.start = start;
        bytes = bs;
        int arr_len = (bytes.length/3)*2;//可能最后的余数部分的byte不能使用了，但是也没有必要，就当废弃掉了。

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
     * 这个方法就是用来申请扇区的，需要把FAT中的链表建立
     * @param len
     * @return
     */
    @Override
    public int[] getFreeClus(int len) {
        int[] result = new int[len];
        int pos = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i]==0){
                result[pos++] = i;
            }
            if (pos==len) break;
        }

        if (pos!=len)
            return null;

        //如果成功申请到，还要修改FAT数组
        for (int i = 0; i < result.length - 1; i++) {
            array[result[i]] = result[i+1];
        }
        array[result[len-1]] = 0xFFF;

        return result;
    }


    /**
     * 释放一个簇链表
     * @param indexs
     */
    @Override
    public void freeClusList(int[] indexs) {
        for (int i = 0; i < indexs.length; i++) {
            array[indexs[i]] = 0;
        }
    }

    /**
     * 确保以fst_sec开始的链表有need需要的扇区个数
     * 如果need远远大于原先链表，可能不够，则返回null
     * 如果够了，就返回新的链表索引
     *
     *
     * @param fst_sec
     * @param need  上层模块已经保证，need是一定大于等于1的
     * @return
     */
    @Override
    public int[] ensure(int fst_sec, int need) {

        int[] result = new int[need];
        int[] origin_indexs = getClusList(fst_sec);
        int origin_len = origin_indexs.length;
        if (origin_len==need){//这也是绝大多数情况
            result = origin_indexs;
        }else if(origin_len>need){
            array[origin_indexs[need-1]] = Constant.CLUS_LIST_END;
            for (int i = need; i < origin_len; i++) {
                array[origin_indexs[i]] = 0;
            }
            for (int i = 0; i < need; i++) {
                result[i] = origin_indexs[i];
            }
        }else{
            int[] more_indexs =  getFreeClus(need-origin_len);
            if (more_indexs==null){
                return null;
            }
            //这里容易忘记，要把新分配的扇区初始化
            DefaultDiskManager.getManager().getData().initFileSector(more_indexs);


            //在结果中把两个链表拼接起来
            for (int i = 0; i < origin_len; i++) {
                result[i] = origin_indexs[i];
            }
            for (int i = 0; i < more_indexs.length; i++) {
                result[origin_len+i] = more_indexs[i];
            }
            //在FAT数组中将两个链表拼接起来
            array[origin_indexs[origin_len-1]] = more_indexs[0];
        }

        return result;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }


    /**
     * 这个方法是将高级数据结构转化为byte数组
     */
    @Override
    public void store() {
        int byte_pos = 0;
        int int_pos = 0;
        for (int i = 0; i < array.length / 2; i++) {
            byte_pos = i*3;
            int_pos = i*2;
            bytes[byte_pos] = new Integer(array[int_pos]&0x00FF).byteValue();
            int high = (array[int_pos+1]&0x00F)<<4;
            int low = (array[int_pos]&0x0F00)>>8;
            int res = high|low;
            byte b = new Integer(res).byteValue();
            bytes[byte_pos+1] = b;


//            bytes[byte_pos+1] = new Integer(((array[int_pos+1]&0x00F)<<4)&((array[int_pos]&0x0F00)>>8)).byteValue();
            bytes[byte_pos+2] = new Integer((array[int_pos+1]&0x0FF0)>>4).byteValue();
        }
        //如果array是奇数
        if (array.length%2==1){
            // 在实验中偷个懒，一般不会用到数组的最后的，
        }

    }
}
