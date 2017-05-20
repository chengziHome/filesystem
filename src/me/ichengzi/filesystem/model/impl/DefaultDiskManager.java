package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.model.Dictionary;
import me.ichengzi.filesystem.model.File;
import me.ichengzi.filesystem.util.Byte2Int;
import me.ichengzi.filesystem.util.Constant;
import me.ichengzi.filesystem.util.ReturnUtil;

import java.io.*;
import java.util.*;

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


    private static final String DISK_FILE_PATH = "E:/fat/startand.flp";


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


    /**
     * 如果找不到就返回null.
     *
     * @param fileName
     * @return
     */
    @Override
    public File findFile(String fileName) {
        Item item = null;
        if ("/".equals(getCurrentPath())){
            item = getRoot().find(fileName);
        }else{
            Dictionary currentDir = getCurrentDictionary();
            item = currentDir.find(fileName);
        }
        if (item==null){
            return null;
        }else if(item.getDir_Attr() == Constant.ITEM_ATTR_DIR){
            return null;
        }

        item.setAbsolutePath(getCurrentPath()+item.getDir_Name()+"/");
        File findFile = new DefaultFile(item);



        return findFile;
    }


    /**
     * 编辑以后的文件重新保存主要的逻辑，
     * 首先搞清楚都需要做些什么
     * 1，不用重新查找，字节操作File对象就可以(早期实现中，不会有多窗口的复杂任务)
     * 2，对新的内容判断sectors链表是否够用。少了就在链表尾部添加，多了就截断释放多余扇区。并更新FAT表。重新构建Sector链表
     *    首先更新FAT分区
     * 3，将content内容映射到扇区链表的byte数组中。
     * 4，将扇区链表的内容映射到Data分区中
     * 5，删除Data内存管理中对该文件的缓存。
     *
     * @param file
     * @param newContext
     */
    @Override
    public void saveFile(File file, String newContext) {
        byte[] bs = newContext.getBytes();
//        int need_sec = bs.length/Constant.SECTOR_SIZE + (bs.length%Constant.SECTOR_SIZE==0?0:1);
        int need_sec = bs.length/Constant.SECTOR_SIZE + 1;//绝大多数情况。
        Item item = file.getItem();

        int fst_sec = item.getDir_FstClus();
        int[] new_indexs = getFAT1().ensure(fst_sec,need_sec);
        getFAT1().store();
        /*
            把第3、4步骤合并，直接把content内容映射到Data分区中，不用维护这个File对象了
            因为马上它在Data中的缓存就要被删除掉
         */
        for (int i = 0; i < new_indexs.length; i++) {
            Sector sector = new DefaultSector(Arrays.copyOfRange(bs,i*Constant.SECTOR_SIZE,(i+1)*Constant.SECTOR_SIZE),new_indexs[i]);
            sector.store();
        }

        getData().removeItem(file.getItem().getAbsolutePath());

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
        getFAT1().store();

        if (indexs==null){
            return ReturnUtil.error("磁盘已满，无可用空间");
        }
        getData().initFileSector(indexs);

        Item fileItem = new DefaultItem();

        fileItem.setDir_Name(fileName);
        fileItem.setDir_Attr(Constant.ITEM_ATTR_FILE);
        fileItem.setReserve("");

        int[] time = Byte2Int.getTime();
        fileItem.setDir_WrtTime(time[0]);
        fileItem.setDir_WrtDate(time[1]);
        fileItem.setDir_FstClus(indexs[0]);
        fileItem.setDir_FileSize(0);

        fileItem.store();
        if ("/".equals(getCurrentPath())){
            getRoot().addItem(fileItem);
            getRoot().store();
        }else{
            Dictionary currentDir = getCurrentDictionary();
            currentDir.addItem(fileItem);//注意，需要添加新扇区的操作聚合在addItem方法里面
            currentDir.store();
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
        getFAT1().store();
        if (indexs==null){
            return ReturnUtil.error("磁盘已满，无可用空间");
        }
        getData().initDirSector(indexs);

        Item dirItem = new DefaultItem();

        dirItem.setDir_Name(dirName);
        dirItem.setDir_Attr(Constant.ITEM_ATTR_DIR);
        dirItem.setReserve("");

        int[] time = Byte2Int.getTime();
        dirItem.setDir_WrtTime(time[0]);
        dirItem.setDir_WrtDate(time[1]);

        dirItem.setDir_FstClus(indexs[0]);
        dirItem.setDir_FileSize(0);

        dirItem.store();
        if ("/".equals(getCurrentPath())){
            getRoot().addItem(dirItem);
            getRoot().store();
        }else{
            Dictionary currentDir = getCurrentDictionary();
            currentDir.addItem(dirItem);
            currentDir.store();
        }

        getData().removeItem(getCurrentPath());

        return ReturnUtil.success();
    }


    /**
     * 删除文件或者目录的主要逻辑
     * @param file
     * @return
     */
    @Override
    public ReturnUtil remove(String file) {
        int[] fat_indexs = null;
        Item findItem = null;
        if ("/".equals(getCurrentPath())){
            findItem = getRoot().find(file);
            int fstClus = findItem.getDir_FstClus();
            fat_indexs = getFAT1().getClusList(fstClus);
            getRoot().remove(file);
        }else{
            Dictionary currentDir = getCurrentDictionary();
            findItem = currentDir.find(file);
            int fstClus = findItem.getDir_FstClus();
            fat_indexs = getFAT1().getClusList(fstClus);
            currentDir.remove(file);
            currentDir.store();
        }
        if (findItem.getDir_Attr() == Constant.ITEM_ATTR_DIR){

            findItem.setAbsolutePath(getCurrentPath()+findItem.getDir_Name()+"/");
            Dictionary dir = new DefaultDir(findItem);
            dir.removeAllSubDir();

        }

        //更新FAT表
        getFAT1().freeClusList(fat_indexs);
        getFAT1().store();
        //删除缓存,目录文件一并处理，不冲突
        getData().removeItem(getCurrentPath()+file);
        getData().removeItem(getCurrentPath()+file+"/");

        //和添加方法一样，当前目录的Data中的缓存同样要清空。
        getData().removeItem(getCurrentPath());

        return ReturnUtil.success();
    }


    /**
     * 删除文件和目录还是有很大不同，删除文件直接将Item首位bit置失效就可以了
     * 但是删除目录，还要做的一项工作是：将所有子目录下所有的Item项的FAT扇区链
     * 统统置位为0；
     * @param dirName
     * @return
     */
    @Override
    public ReturnUtil removeDir(String dirName) {

        return null;
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
        Iterator<Item> iterator = currentPathStack.descendingIterator();
        while(iterator.hasNext()){
            Item item = iterator.next();
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
        /*
            有个细节，deque作为栈的时候，push是addFirst，就是说低索引处是栈顶。
            而默认迭代又是从低索引开始的，特别小心这个顺序
         */
        for (Item item:currentPathStack){
            copyStack.addLast(item);
        }
        return copyStack;
    }

    @Override
    public void setCurrentPathStack(Deque<Item> stack) {
        this.currentPathStack = stack;
    }

}
