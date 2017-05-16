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
        try {
            File file = new File(diskPath);
            InputStream in = new FileInputStream(file);
            bytes = new byte[Constant.DISK_TOTAL_SIZE];
            int pos = 0;
            int len = 0;
            byte[] tmp = new byte[1024 * 10];
            while((len = in.read(tmp))!=-1){
                for (int i = pos; i < len; i++) {
                    bytes[pos+i] = tmp[i];
                }
                pos += len;
            }
            System.out.println("bytes:"+Arrays.toString(Arrays.copyOfRange(bytes,0x2800,0x2820)));
            max_len = pos+1;
        } catch (IOException e) {
            e.printStackTrace();

        }
        /*
            2.创建内部数据结构
         */

        loadBoot();

        int FAT_SecNum = boot.getBPB_FATSz16();
        loadFAT(Constant.BOOT_SECNUM*Constant.SECTOR_SIZE,FAT_SecNum);
        loadRootDir((Constant.BOOT_SECNUM+FAT_SecNum*2)* Constant.SECTOR_SIZE,Constant.ROOT_ITEMNUM);
        loadData((Constant.BOOT_SECNUM+FAT_SecNum*2)* Constant.SECTOR_SIZE+Constant.ROOT_ITEMNUM*Constant.ITEM_SIZE);

    }

    @Override
    public void format() {

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


    // TODO: 2017/5/10 load写完之后再来写store过程

    @Override
    public void store() {

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
        FAT1 = new FAT12(Arrays.copyOfRange(bytes,offset,offset+secNum*Constant.SECTOR_SIZE));
        FAT2 = new FAT12(Arrays.copyOfRange(bytes,offset+secNum*Constant.SECTOR_SIZE,offset+secNum*2*Constant.SECTOR_SIZE));
        return null;
    }

    private RootDir loadRootDir(int offset,int itemNum){
        rootDir = new RootDir(Arrays.copyOfRange(bytes,offset,offset + itemNum*Constant.ITEM_SIZE));
        return null;
    }

    private Data loadData(int offset){
        data = new DefaultData(Arrays.copyOfRange(bytes,offset,max_len));
        return null;
    }


}
