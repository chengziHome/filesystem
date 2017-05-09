package me.ichengzi.filesystem.model;

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



    Sector getSector(int index);
    void setSector(int index,Sector sector);



}
