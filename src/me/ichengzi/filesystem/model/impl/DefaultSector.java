package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Disk;
import me.ichengzi.filesystem.model.Sector;
import me.ichengzi.filesystem.util.Constant;

import java.util.Arrays;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:03
 */
public class DefaultSector implements Sector {

    private byte[] bytes;

    //注意这里不是绝对偏移，而是数据区的扇区索引，直接从FAT数组中得到的结果
    private int sec_num;

    public int getSec_num() {
        return sec_num;
    }

    public void setSec_num(int sec_num) {
        this.sec_num = sec_num;
    }

    public DefaultSector(byte[] bytes,int sec_num) {
        this.sec_num = sec_num;
        this.bytes = bytes;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public byte[] getBytes(int offset, int end) {
        if (offset<0 || end>= Constant.SECTOR_SIZE || offset>end)
            throw new IllegalArgumentException("参数错误");
        return Arrays.copyOfRange(bytes,offset,end);
    }

    @Override
    public void setBytes(byte[] bs, int offset) {
        for (int i = 0; i < bs.length; i++) {
            bytes[offset++] = bs[i];
        }
    }

    /**
     * 注意这里Sector仅仅只Data区的扇区，且索引起始值为1
     * 而且Data数据模型中没有缓存，直接想Disk对象中写入byte数组
     */
    @Override
    public void store() {
        Disk disk = DefaultDiskManager.getManager().getDisk();
        int dataOffset = disk.getDate().getDataOffset();
        disk.store(bytes,dataOffset+(sec_num-2)*Constant.SECTOR_SIZE);

    }




}
