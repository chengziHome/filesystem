package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.model.File;
import me.ichengzi.filesystem.util.Constant;
import me.ichengzi.filesystem.util.ReturnUtil;

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

    /**
     * 创建文件的核心逻辑
     * @param fileName
     * @return
     */
    @Override
    public ReturnUtil createFile(String fileName) {
        if ("/".equals(getCurrentPath())){
            if (!getRoot().hasAvailable())
                return ReturnUtil.error("根目录下的目录项已经达到上限");
        }
        int[] indexs = getFAT1().getFreeClus(1);
        if (indexs==null){
            return ReturnUtil.error("磁盘已满，无可用空间");
        }
        getData().initFileSector(indexs);

        Item fileItem = new DefaultItem();

        // TODO: 2017/5/17 文件名在长度和扩展名方面还有BUG待改善
        fileItem.setDir_Name(fileName);
        fileItem.setDir_Attr(Constant.ITEM_ATTR_FILE);
        fileItem.setReserve("");

        // TODO: 2017/5/17 设置时间还是有点麻烦
        fileItem.setDir_WrtTime(0x00007648);
        fileItem.setDir_WrtDate(0x00004AAc);
        fileItem.setDir_FstClus(indexs[0]);
        fileItem.setDir_FileSize(0);

        fileItem.store();
        if ("/".equals(getCurrentPath())){
            getRoot().addItem(fileItem);
            getRoot().store();
        }else{
            Dictionary currentDir = getCurrentDictionary();
            if (currentDir.hasAvailable()){
                currentDir.addItem(fileItem);
                currentDir.store();
            }else{
                // TODO: 2017/5/17 需要给目录项添页的逻辑还没有写，
                /*
                    注意：这里新添加的扇区要添加在链表尾部，而不是重新申请一块链表，
                    因为重新申请回更加的麻烦，还要更改当前目录本身的Item想的fst_sec属性。
                 */
            }
        }

        getData().removeItem(getCurrentPath());

        return ReturnUtil.success();
    }


    /**
     * 创造目录和文件大体上是完全相同的，仅仅是扇区的初始化方式不同
     * @param dirName
     * @return
     */
    @Override
    public ReturnUtil createDir(String dirName) {

        if ("/".equals(getCurrentPath())){
            if (!getRoot().hasAvailable())
                return ReturnUtil.error("根目录下的目录项已经达到上限");
        }
        int[] indexs = getFAT1().getFreeClus(1);
        if (indexs==null){
            return ReturnUtil.error("磁盘已满，无可用空间");
        }
        getData().initDirSector(indexs);

        Item dirItem = new DefaultItem();

        dirItem.setDir_Name(dirName);
        dirItem.setDir_Attr(Constant.ITEM_ATTR_DIR);
        dirItem.setReserve("");

        // TODO: 2017/5/17 设置时间还是有点麻烦
        dirItem.setDir_WrtTime(0x00007648);
        dirItem.setDir_WrtDate(0x00004AAc);
        dirItem.setDir_FstClus(indexs[0]);
        dirItem.setDir_FileSize(0);

        dirItem.store();
        if ("/".equals(getCurrentPath())){
            getRoot().addItem(dirItem);
            getRoot().store();
        }else{
            Dictionary currentDir = getCurrentDictionary();
            if (currentDir.hasAvailable()){
                currentDir.addItem(dirItem);
                currentDir.store();
            }else{
                // TODO: 2017/5/17 需要给目录项添页的逻辑还没有写
            }
        }

        getData().removeItem(getCurrentPath());

        return ReturnUtil.success();
    }


    @Override
    public ReturnUtil remove(String file) {
        int[] fat_indexs = null;
        if ("/".equals(getCurrentPath())){
            int fstClus = getRoot().find(file).getDir_FstClus();
            fat_indexs = getFAT1().getClusList(fstClus);
            getRoot().remove(file);
        }else{
            Dictionary currentDir = getCurrentDictionary();
            int fstClus = currentDir.find(file).getDir_FstClus();
            fat_indexs = getFAT1().getClusList(fstClus);
            currentDir.remove(file);
        }
        getFAT1().freeClusList(fat_indexs);
        //删除缓存,目录文件一并处理，不冲突
        getData().removeItem(getCurrentPath()+file);
        getData().removeItem(getCurrentPath()+file+"/");


        return ReturnUtil.success();
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
        try {
            disk.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 和init刚好是相反的过程，这个要把Disk中的byte数组写到文件中区
     */
    @Override
    public void exit() {
        byte[] bytes = disk.getBytes();
        try {
            OutputStream out = new FileOutputStream(DISK_FILE_PATH);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
