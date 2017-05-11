package me.ichengzi.filesystem.model;

import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 用户区的一个抽象，这里面是按照扇区分割的，注意和根目录区不一样
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 21:46
 */
public interface Data {

    List<Sector> load(Item item);//根据item来记载目录或者文件
    void store(int[] indexs);//将buffer中一系列索引的Sector项投射到byte数组中去。

    void delete(Item item);//删除目录项
    void addFile();//添加一个目录项(currentDir)
    void addDir();
    void edit(Item item,String content);//






}
