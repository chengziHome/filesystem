package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Dictionary;
import me.ichengzi.filesystem.model.Disk;
import me.ichengzi.filesystem.model.DiskManager;
import me.ichengzi.filesystem.model.File;

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

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    @Override
    public Dictionary getCurrentDictionary() {
        return null;
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
    public void load() {

    }

    @Override
    public void store() {

    }

    @Override
    public Disk getDisk() {
        return null;
    }

    @Override
    public String getCurrentPath() {
        return null;
    }
}
