package me.ichengzi.filesystem.model.impl;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import jdk.internal.util.xml.impl.Input;
import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.util.Constant;

import java.io.*;
import java.io.File;
import java.util.Arrays;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 22:59
 */
public class DefaultDisk implements Disk {

    private String diskPath;
    /*
        这里用byte数组代表整个磁盘的话，数组最大值Integer.MAX_VALUE,32位，也即最大2GB，
        但是这对于做个试验来说绝对够用了。实验中按照MS-DOS里面的磁盘大小的话，一个软盘
        才不足2MB。
     */
    private byte[] bytes;
    private int max_len;
    private Boot boot;
    private Fat FAT1;
    private Fat FAT2;
    private Root rootDir;
    private Data data;


    public DefaultDisk(String diskPath) {
        this.diskPath = diskPath;

    }

    /**
     * 也就是load过程
     */
    @Override
    public void init() {
        /*
            1.Load过程
         */
        InputStream in = null;
        try {
            File file = new File(diskPath);
            in = new FileInputStream(file);
            bytes = new byte[Constant.DISK_TOTAL_SIZE];
            int pos = 0;
            int len = 0;
            byte[] tmp = new byte[1024 * 10];
            while((len = in.read(tmp))!=-1){
                for (int i = 0; i < len; i++) {
                    bytes[pos+i] = tmp[i];
                }
                pos += len;
            }
            System.out.println("bytes:"+Arrays.toString(Arrays.copyOfRange(bytes,0x0000,0x200)));
            max_len = pos+1;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /*
            2.创建内部数据结构
         */

        loadBoot();

        int FAT_SecNum = boot.getBPB_FATSz16();
        loadFAT(Constant.BOOT_SECNUM*Constant.SECTOR_SIZE,FAT_SecNum);
        loadRootDir((Constant.BOOT_SECNUM+FAT_SecNum*2)* Constant.SECTOR_SIZE,Constant.ROOT_ITEMNUM);
        loadData((Constant.BOOT_SECNUM+FAT_SecNum*2)* Constant.SECTOR_SIZE+Constant.ROOT_ITEMNUM*Constant.ITEM_SIZE);


        /**
         * 测试使用，看看读进来的到底是什么东西
         */

        OutputStream out = null;
        try {
            out = new FileOutputStream("E:/fat/result.flp");
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 将磁盘格式化，因为在这个系统里假定BOOT是不会改变的，所以格式化的操作完全按照Boot里面的参数来进行
     * 简单起见，这了不用创建任何的分区的对象，值需要得到内存中的Boot对象，取出响应的参数
     * 找到相应的偏移位置进行初始化。就连盘符名称也不可以改变了
     */
    @Override
    public void format() {
        int Sec_Size = boot.getBPB_BytePerSec();//扇区大小，假定不变
        int fat1_offset = boot.getBPB_ResvSecCnt()*Sec_Size;//FAT1分区偏移地址。
        int fat1_end = (boot.getBPB_ResvSecCnt()+boot.getBPB_FATSz16())*Sec_Size;
        int fat2_offset = fat1_end;
        int fat2_end = (boot.getBPB_ResvSecCnt()+boot.getBPB_FATSz16()*2)*Sec_Size;
        int root_offset = fat2_end;
        int root_end = (boot.getBPB_ResvSecCnt()+boot.getBPB_FATSz16()*2)*Sec_Size + boot.getBPB_RootEntCnt()*Constant.ITEM_SIZE;
        int data_offset = root_end;
        int data_end = boot.getBPB_TotSec16() * Sec_Size;

        byte[] bs = new byte[data_end];
        //初始化Boot分区，数据不变
        int pos = 0;
        for (int i = 0; i <fat1_offset ; i++) {
            bs[pos++] = bytes[i];
        }
        //初始化FAT1分区
        int len = boot.getBPB_FATSz16()*Sec_Size;
        bs[pos++] = (byte) 0xF0;
        bs[pos++] = (byte) 0xFF;
        bs[pos++] = (byte) 0xFF;
        for (int i = 0; i < len-3; i++) {
            bs[pos++] = 0x00;
        }

        //初始化FAT2分区
        bs[pos++] = (byte) 0xF0;
        bs[pos++] = (byte) 0xFF;
        bs[pos++] = (byte) 0xFF;
        for (int i = 0; i < len-3; i++) {
            bs[pos++] = 0x00;
        }

        //初始化根目录区
        String name = boot.getBS_VolLab();
        byte[] nameBytes = name.getBytes();
        byte[] rootName = new byte[11];
        for (int i = 0; i < 11; i++) {
            if (i<nameBytes.length){
                bs[pos++] = nameBytes[i];
            }else{
                bs[pos++] = 0x00;
            }
        }
        bs[pos++] = 0x28;
        for (int i = 0; i < 10; i++) {
            bs[pos++] = 0x00;
        }
        // TODO: 2017/5/20 下面四位是时间到后面统一更改
        bs[pos++] = 0x4C;
        bs[pos++] = 0x76;
        bs[pos++] = (byte) 0xB2;
        bs[pos++] = 0x4A;
        bs[pos++] = 0x00;
        bs[pos++] = 0x00;
        bs[pos++] = 0x00;
        bs[pos++] = 0x00;
        bs[pos++] = 0x00;
        bs[pos++] = 0x00;

        int root_len = boot.getBPB_RootEntCnt()*Constant.ITEM_SIZE;
        for (int i = 0; i < root_len-32; i++) {
            bs[pos++] = 0x00;
        }

        //最后就是数据区，
        while(pos<data_end){
            bs[pos++] = (byte) 0xF6;
        }

        bytes = bs;

        //把Data区的数据缓存全部清空
        data.clear();


        //这里做持久化，但是并不退出系统。
        DefaultDiskManager.getManager().exit();

        init();

    }

    /*
        set方法在创建硬盘文件的时候可能用到
     */

    @Override
    public Boot getBoot() {
        return boot;
    }

    @Override
    public void setBoot(Boot boot) {
        this.boot = boot;
    }

    @Override
    public Fat getFAT1() {
        return FAT1;
    }

    @Override
    public void setFAT1(Fat fat1) {
        this.FAT1 = FAT1;
    }

    @Override
    public Fat getFAT2() {
        return FAT2;
    }

    @Override
    public void setFAT2(Fat fat2) {
        this.FAT2 = FAT2;
    }

    @Override
    public Root getRoot() {
        return rootDir;
    }

    @Override
    public void setRoot(Root root) {
        this.rootDir = root;
    }

    @Override
    public Data getDate() {
        return data;
    }

    @Override
    public void setData(Data data) {
        this.data = data;
    }


    /**
     * @param bytes
     * @param start
     */
    @Override
    public void store(byte[] bytes,int start) {
        for (int i = 0; i < bytes.length; i++) {
            this.bytes[start++] = bytes[i];
        }
    }

    /**
     * 这个store方法是讲各个数据区的byte[]数组汇总到Disk的byte数组中
     * 其实总共四个Root,FAT,ROOT,DATA,只有中间两个需要汇总
     */
    @Override
    public void store() {
        store(FAT1.getBytes(),Constant.BOOT_SECNUM*Constant.SECTOR_SIZE);
        store(FAT2.getBytes(),(Constant.BOOT_SECNUM + boot.getBPB_FATSz16())*Constant.SECTOR_SIZE);
        store(rootDir.getBytes(),(Constant.BOOT_SECNUM + boot.getBPB_FATSz16()*2)*Constant.SECTOR_SIZE);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    // TODO: 2017/5/10 定义好各个数据类型之后再来写这里的load过程



    private Boot loadBoot(){
        boot = new DefaultBoot(Arrays.copyOfRange(bytes,0,Constant.BOOT_SECNUM*Constant.SECTOR_SIZE));
        return null;
    }

    /**
     * 注意哈，这里要加载两个FAT分区
     * @param offset
     * @param secNum
     * @return
     */
    private Fat loadFAT(int offset,int secNum){
        FAT1 = new FAT12(Arrays.copyOfRange(bytes,offset,offset+secNum*Constant.SECTOR_SIZE),offset);
        FAT2 = new FAT12(Arrays.copyOfRange(bytes,offset+secNum*Constant.SECTOR_SIZE,offset+secNum*2*Constant.SECTOR_SIZE),offset+secNum*Constant.SECTOR_SIZE);
        return null;
    }

    private RootDir loadRootDir(int offset,int itemNum){
        rootDir = new RootDir(Arrays.copyOfRange(bytes,offset,offset + itemNum*Constant.ITEM_SIZE));
        return null;
    }

    private Data loadData(int offset){
        data = new DefaultData(offset);
        return null;
    }


}
