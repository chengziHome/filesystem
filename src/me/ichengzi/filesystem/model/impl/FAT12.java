package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Fat;
import me.ichengzi.filesystem.util.Constant;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:01
 */
public class FAT12 implements Fat{

    private byte[] bytes;

    private short[] array;

    public FAT12(int secNum) {
        bytes = new byte[secNum * Constant.SECTOR_SIZE];
    }

    @Override
    public int[] getIntArray() {
        return new int[0];
    }

    @Override
    public int get(int i) {
        return 0;
    }

    @Override
    public int[] getClusList(int first) {
        return new int[0];
    }

    @Override
    public void set(int index, int val) {

    }

    @Override
    public boolean setClusList(int first, int[] clus) {
        return false;
    }
}
