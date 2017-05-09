package me.ichengzi.filesystem.controller;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 15:56
 */
public interface Controller {

    void init();
    boolean hasInitialized();
    void format();
    void touch(String fileName);
    void mkdir(String dirName);
    void remove(String name);
    void list();
    void cd();
    void edit();
    void exit();
    Object find(String name);




}
