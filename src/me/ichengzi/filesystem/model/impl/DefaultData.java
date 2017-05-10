package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Data;
import me.ichengzi.filesystem.model.Sector;

import java.util.ArrayList;
import java.util.List;

/**
 * Coding is pretty charming when you love it!\
 *
 * 注意哈，Data区和其他数据区结构和目的不一样，其他都是完全映射，
 * 而这里更像是一个保存用户行为的数据结构
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:02
 */
public class DefaultData implements Data {

    private List<Sector> modifiedSectors;

    public DefaultData() {
        modifiedSectors = new ArrayList<>();
    }

    @Override
    public void addModifiedSector(Sector sector) {
        modifiedSectors.add(sector);
    }

    @Override
    public void store() {

    }

    /**
     * 注意这里用户区的index索引实际上是从1开始的，而且从FAT表中可以看出0和1都被系统占用
     * 所以这里实际上第一块也是空闲的，真正能用的从第二块开始
     * @param index
     * @return
     */

}
