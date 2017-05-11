package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.model.File;

import java.io.*;
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
    private String currentDir;
    private Dictionary currentDictionary;

    private static final String DISK_FILE_PATH = "E:/fat/format1.flp";

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    @Override
    public Dictionary getCurrentDictionary() {
        return currentDictionary;
    }

    @Override
    public void refreshCurrentDir() {

    }

    @Override
    public Deque<String> getPathStack() {
        return null;
    }

    @Override
    public void setPathStack(Deque<String> stack) {

    }

    @Override
    public DefaultDir getRootDir() {
        return null;
    }

    private static final DefaultDiskManager instance = new DefaultDiskManager();

    private DefaultDiskManager() {
        disk = new DefaultDisk(DISK_FILE_PATH);
        currentDir = "/";
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
        return null;
    }

    @Override
    public Root getRoot() {
        return null;
    }

    @Override
    public Fat getFAT1() {
        return null;
    }

    @Override
    public Fat getFat2() {
        return null;
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public String getCurrentPath() {
        return null;
    }
}
