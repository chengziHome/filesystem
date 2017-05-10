package me.ichengzi.filesystem.model.impl;

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

    public DefaultSector(byte[] bytes) {
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
    public void setBytes(byte[] bs, int offset, int len) {
        for (int i = 0; i < len; i++) {
            bytes[offset++] = bs[i];
        }
    }
}
