package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.model.File;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 22:50
 */
public class DefaultDiskManager implements DiskManager{

    private Disk disk;
    private Deque<Item> currentPathStack;


    private static final String DISK_FILE_PATH = "E:/fat/filesys1.flp";


    /**
     * 由于根目录和普通目录是不同的类型，
     * 所以这个currentDictionary只能代表普通目录，
     * 如果要表示根目录的话，就将这个值设置为null，
     * 然后调用manager.getRoot()就好了
     * @return
     */


    private static final DefaultDiskManager instance = new DefaultDiskManager();

    private DefaultDiskManager() {
        disk = new DefaultDisk(DISK_FILE_PATH);
        currentPathStack = new ArrayDeque<>();
    }

    /**
     * 最简单的单例模式
     * @return
     */
    public static DefaultDiskManager getManager(){
        return instance;
    }

    @Override
    public File findFile(String fileName) {
        return null;
    }

    @Override
    public Dictionary findDir(String dirName) {
        return null;
    }

    @Override
    public boolean createFile(String fileName) {
        return false;
    }

    @Override
    public boolean createDir(String dirName) {
        return false;
    }

    @Override
    public void remove(String file) {

    }

    @Override
    public List list() {
        return null;
    }

    @Override
    public void cd() {

    }



    @Override
    public void init() {
        disk.init();
    }

    @Override
    public void exit() {

    }

    @Override
    public Disk getDisk() {
        return disk;
    }

    @Override
    public Root getRoot() {
        return disk.getRoot();
    }

    @Override
    public Fat getFAT1() {
        return disk.getFAT1();
    }

    @Override
    public Fat getFat2() {
        return disk.getFAT2();
    }

    @Override
    public Data getData() {
        return disk.getDate();
    }


    @Override
    public String getCurrentPath() {
        if (currentPathStack.isEmpty()){
            return "/";
        }
        StringBuilder sb = new StringBuilder();
        for (Item item:currentPathStack){
            sb.append("/").append(item.getDir_Name());
        }
        sb.append("/");
        return sb.toString();
    }

    /**
     * 如果是根目录这里就返回null
     * @return
     */
    @Override
    public Dictionary getCurrentDictionary() {
        if (currentPathStack.isEmpty()){
            return null;
        }
        Item currentItem = currentPathStack.peek();
        Dictionary currentDir = new DefaultDir(currentItem);
        return currentDir;
    }

    @Override
    public Item popItem() {
        if (currentPathStack.isEmpty()){
            return null;
        }else{
            return currentPathStack.pop();
        }

    }

    @Override
    public void pushItem(Item item) {
        currentPathStack.push(item);
    }

    /**
     * 注意，这里仅仅是个浅拷贝，因为上层业务只涉及到读取，而不涉及到更改。
     * 如果业务中遇到更改的需求则要实现深度拷贝的版本
     * @return
     */
    @Override
    public Deque<Item> copyCurrentPathStack() {
        Deque<Item> copyStack = new ArrayDeque<>();
        for (Item item:currentPathStack){
            copyStack.push(item);
        }
        return copyStack;
    }

    @Override
    public void setCurrentPathStack(Deque<Item> stack) {
        this.currentPathStack = stack;
    }

}
