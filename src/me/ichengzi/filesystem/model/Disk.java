package me.ichengzi.filesystem.model;

import java.io.*;
import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 硬盘的一个抽象，这个接口里面的方法更多的是面向底层的数据结构
 * 因为面向上层模块操作的功能定义在了DiskManager里面去了
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 17:17
 */
public interface Disk {

    void init();
    void format();

    Boot getBoot();
    void setBoot(Boot boot);
    Fat getFAT1();
    void setFAT1(Fat fat1);
    Fat getFAT2();
    void setFAT2(Fat fat2);
    Root getRoot();
    void setRoot(Root root);
    Data getDate();
    void setData(Data data);

    void store(byte[] bytes,int start);
    void store();

    byte[] getBytes();




}
