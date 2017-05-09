package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Sector;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:03
 */
public class DefaultSector implements Sector {
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public byte[] getBytes(int offset, int end) {
        return new byte[0];
    }

    @Override
    public void setBytes(byte[] bs, int offset, int len) {

    }
}
