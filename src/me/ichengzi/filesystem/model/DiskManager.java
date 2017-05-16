package me.ichengzi.filesystem.model;

import me.ichengzi.filesystem.model.impl.DefaultDir;

import java.util.Deque;
import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 磁盘操作的抽象，注意这里的粒度和Controller里面的区分。
 * 这里更多面对的是底层磁盘的数据操作，而Controller里面还会涉及到更多系统状态的信息。
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 17:18
 */
public interface DiskManager {

    File findFile(String fileName);
    Dictionary findDir(String dirName);

    boolean createFile(String fileName);
    boolean createDir(String dirName);

    void remove(String file);

    List list();

    void cd();


    void init();
    void exit();



    Disk getDisk();
    Root getRoot();
    Fat getFAT1();
    Fat getFat2();
    Data getData();


    /*
        对当前目录的维护
     */



    String getCurrentPath();
    Dictionary getCurrentDictionary();
    Item popItem();
    void pushItem(Item item);

    Deque<Item> copyCurrentPathStack();
    void setCurrentPathStack(Deque<Item> stack);





}
