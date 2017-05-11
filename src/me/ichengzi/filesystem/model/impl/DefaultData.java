package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Data;
import me.ichengzi.filesystem.model.Item;
import me.ichengzi.filesystem.model.Sector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coding is pretty charming when you love it!\
 *
 * 注意哈，Data区和其他数据区结构和目的不一样，其他都是完全映射，
 * 而这里更像是一个内存去，其核心逻辑也在于固定大小内存的管理。
 * 内存管理所有核心的操作基本都集中在了这里
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:02
 */
public class DefaultData implements Data {

    /**
     * 登记所有已经加载到内存中的文件或者目录名称
     *
     */
    private Map<String,Item> registeTable = new HashMap<>();



    private List<Sector> modifiedSectors;

    public DefaultData() {
        modifiedSectors = new ArrayList<>();
    }

    @Override
    public List<Sector> load(Item item) {
        return null;
    }

    @Override
    public void store(int[] indexs) {

    }

    @Override
    public void delete(Item item) {

    }

    @Override
    public void addFile() {

    }

    @Override
    public void addDir() {

    }

    @Override
    public void edit(Item item, String content) {

    }



}
