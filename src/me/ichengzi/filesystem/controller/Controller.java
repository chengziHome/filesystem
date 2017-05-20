package me.ichengzi.filesystem.controller;

import me.ichengzi.filesystem.editor.NotePad;
import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.model.Dictionary;
import me.ichengzi.filesystem.model.File;
import me.ichengzi.filesystem.model.impl.*;
import me.ichengzi.filesystem.util.Constant;
import me.ichengzi.filesystem.util.MapUtil;
import me.ichengzi.filesystem.util.ReturnUtil;

import java.util.*;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 15:56
 */
public class Controller {

    private DiskManager manager = DefaultDiskManager.getManager();

    private boolean hasInitialized = false;


    /**
     * 初始化整个硬盘，从真实OS中加载文件，并且初始化内存中的数据结构
     */
    public void init() {
        manager.init();
        manager.setCurrentPathStack(new ArrayDeque<>());
        hasInitialized = true;
    }


    public boolean hasInitialized() {
        return hasInitialized;
    }

    public DiskManager getManager() {
        return manager;
    }



    /**
     * 简单起见，除了Boot分区，所有的数据结构全部清空即可
     * 实现了这个方法并且在系统中开始使用之后，就完全是按照自己的定义的文件方式来进行测试了
     * 既是好事也是坏事，一方面能够彻底的保证系统是完全自己实现的，出了问题好找，
     * 另一方面，也有可能因为是自己做的所以会有一些不完善的地方导致隐匿的BUG
     *
     *
     */
    public void format() {
        Disk disk = manager.getDisk();
        disk.format();
    }


    /**
     * 创建一个新的文件，此时文件是空的也就是说簇链表中只有一个簇。
     *
     * @param fileName
     */
    public ReturnUtil touch(String fileName) {
        ReturnUtil result = manager.createFile(fileName);
        return result;
    }


    /**
     * 创建一个新的目录
     *
     * @param dirName
     */
    public ReturnUtil mkdir(String dirName) {
        ReturnUtil result = manager.createDir(dirName);
        return result;
    }

    /**
     * 删除掉指定文件或者目录(递归删除)
     *
     * @param name
     */
    public ReturnUtil remove(String name) {
        ReturnUtil result = manager.remove(name);
        return result;
    }

    public ReturnUtil edit(String fileName){
        File file = manager.findFile(fileName);
        if (file == null){
            System.out.println("file==null");
            return ReturnUtil.error("未找到文件");
        }


        new NotePad(file);


        return ReturnUtil.success();
    }



    /**
     * 列出当前目录下面的所有项目
     *
     * @return
     */
    public ReturnUtil list() {
        List<Item> items = null;
        if ("/".equals(manager.getCurrentPath())) {
            Root rootDir = manager.getRoot();
            items = rootDir.getItems();
        } else {
            items = manager.getCurrentDictionary().getItems();
        }

        List<Item> dirItems = new ArrayList<>();
        List<Item> fileItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getDir_Attr() == Constant.ITEM_ATTR_DIR) {
                dirItems.add(item);
            } else if (item.getDir_Attr() == Constant.ITEM_ATTR_FILE) {
                fileItems.add(item);
            }
        }

        return ReturnUtil.success(new MapUtil()
                .add("dirItems", dirItems)
                .add("fileItems", fileItems)
                .build());

    }


    /**
     * 切换目录,
     * Shell里面还是要对cd的参数做一定的校验，起码格式要正确撒
     * 注意，对Item的absolutePath的维护统一放在这里，因为也只有这里会更换目录
     * 在创建DefaultDir对象之前要设置到item的absolute属性，因为在Default的构造函数里面会登记到内存管理的注册表
     * 这里也是绝对路径的主要用途之一。总之这个设计非常糟糕。
     * 另外注意，所有pop出currentPathStack的都已经设置了absolutePath,仅需对那些新push进去的设置
     * 或者说，push和new DefaultDir之前一定要设置absolutepath
     */
    public ReturnUtil cd(String dest) {
        if (dest.startsWith("/")) {//是绝对路径，就和当前目录无关了
            String[] paths = dest.split("/");
            if (paths.length < 2) {//直接转到根目录
                manager.setCurrentPathStack(new ArrayDeque<>());
            } else {//不仅是根目录
                Root rootDir = manager.getRoot();
                Item rootItem = rootDir.find(paths[1]);
                if (rootItem == null) {
                    return ReturnUtil.error("根目录下无目录:" + paths[1]);
                }
                rootItem.setAbsolutePath("/" + rootItem.getDir_Name() + "/");
                Deque<Item> tmpPathStack = new ArrayDeque<>();

                tmpPathStack.push(rootItem);
                Dictionary father = new DefaultDir(rootItem);//内部可能跟新registerTable

                StringBuilder fatherPath = new StringBuilder();
                fatherPath.append("/" + rootItem + "/");
                for (int i = 2; i < paths.length; i++) {
                    Item item = father.find(paths[i]);
                    if (item == null || item.getDir_Attr() == Constant.ITEM_ATTR_FILE) {
                        return ReturnUtil.error(item.getDir_Name() + "目录未找到");
                    }
                    fatherPath.append(item.getDir_Name() + "/");
                    item.setAbsolutePath(fatherPath.toString());

                    tmpPathStack.push(item);
                    father = new DefaultDir(item);//内部可能跟新registerTable
                }
                manager.setCurrentPathStack(tmpPathStack);
            }
        } else if (".".equals(dest) || "./".equals(dest)) {//如果是当前目录
            return ReturnUtil.success();
        } else {//相对目录的话，就需要根据当前目录的值来计算

            String[] paths = dest.split("/");
            Deque<Item> copyStack = manager.copyCurrentPathStack();
            for (int i = 0; i < paths.length; i++) {
                if ("..".equals(paths[i])) {//向上
                    if (copyStack.isEmpty()) {
                        return ReturnUtil.error("根目录无上层目录");
                    } else {
                        copyStack.pop();
                    }
                } else {//向下
                    Item son = null;
                    if (copyStack.isEmpty()) {
                        son = manager.getRoot().find(paths[i]);
                    } else {
                        Dictionary currentDir = new DefaultDir(copyStack.peek());//栈里面的Item一定有absolutePath属性
                        son = currentDir.find(paths[i]);
                    }


                    if (son == null) {
                        return ReturnUtil.error("未找到目录：" + paths[i]);
                    } else if (son.getDir_Attr() == Constant.ITEM_ATTR_FILE) {
                        return ReturnUtil.error("目录项" + son.getDir_Name() + "是文件，非目录");
                    } else {
                        if (copyStack.isEmpty()) {
                            son.setAbsolutePath("/"+son.getDir_Name()+"/");
                        } else {
                            String fatherAPath = copyStack.peek().getAbsolutePath();
                            son.setAbsolutePath(fatherAPath + son.getDir_Name() + "/");
                        }
                        copyStack.push(son);
                    }
                }
            }
            //当for循环执行完，说明所有的目录节点都是合法的，这个时候要更新currentPathStack
            manager.setCurrentPathStack(copyStack);
        }

        /*
            见《设计中的变化》4.2
         */



        //为了加载当前目录的扇区链表
        if (!"/".equals(manager.getCurrentPath())){
            Dictionary currentDir = manager.getCurrentDictionary();
            //因为在getCurrentDictionary方法里面创建DefaultDir的过程中已经加载过一次了，
//            manager.getData().load(currentDir.getItem());
        }


        return ReturnUtil.success();

    }

    public void saveAll() {
        manager.getDisk().store();//收集数据
        manager.exit();//写入文件
    }


}
