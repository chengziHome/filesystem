package me.ichengzi.filesystem.controller;

import me.ichengzi.filesystem.model.Dictionary;
import me.ichengzi.filesystem.model.Disk;
import me.ichengzi.filesystem.model.DiskManager;
import me.ichengzi.filesystem.model.Item;
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
    public void init(){
        manager.init();
        manager.setCurrentDir("/");
        hasInitialized = true;
    }


    public boolean hasInitialized(){
        return hasInitialized;
    }

    /**
     * 简单起见，除了Boot分区，所有的数据结构全部清空即可
     */
    public void format(){
        Disk disk = manager.getDisk();
        disk.format();
    }


    /**
     * 创建一个新的文件，此时文件是空的也就是说簇链表中只有一个簇。
     * @param fileName
     */
    public ReturnUtil touch(String fileName){
        Dictionary currentDir = manager.getCurrentDictionary();
        DefaultFile file = new DefaultFile();

        // TODO: 2017/5/9 定义完DefaultFile的数据结构之后，这里还需要进一步设置

        currentDir.addItem(file);
        currentDir.store();//更新到Disk中
        return ReturnUtil.success();
    }


    /**
     * 创建一个新的目录
     * @param dirName
     */
    public ReturnUtil mkdir(String dirName){
        Dictionary currentDir = manager.getCurrentDictionary();
        DefaultDir dir = new DefaultDir();
        // TODO: 2017/5/9 等DefaultDir的数据结构定义完之后，这里还要进一步设置
        currentDir.addItem(dir);
        currentDir.store();

        return ReturnUtil.success();
    }

    /**
     * 删除掉指定文件或者目录(递归删除)
     * @param name
     */
    public ReturnUtil remove(String name){
        Dictionary currentDir = manager.getCurrentDictionary();
        Item item = currentDir.find(name);
        if (item == null){
            return ReturnUtil.error("未找到指定文件或目录");
        }
        currentDir.delete(item);
        currentDir.store();

        return ReturnUtil.success();
    }


    /**
     * 列出当前目录下面的所有项目
     * @return
     */
    public ReturnUtil list(){
        Dictionary currentDir = manager.getCurrentDictionary();
        List<Item> items = currentDir.getItems();
        Collections.sort(items, new Comparator<Item>() {
            /**
             * 让目录排在文件前面
             */
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.getDir_Attr() == o2.getDir_Attr()){
                    return 0;
                }else if(o1.getDir_Attr() == 0x10){
                    return 1;
                }else{
                    return -1;
                }
            }
        });

        // TODO: 2017/5/10 关于具体怎么显示这些目录项是View层，也即Shell的责任

        return ReturnUtil.success(new MapUtil().add("items",items).build());
    }

    /**
     * 切换目录,
     * Shell里面还是要对cd的参数做一定的校验，起码格式要正确撒
     */
    public ReturnUtil cd(String dest){
        Deque<String> currentPath = manager.getPathStack();
        if (dest.startsWith("/")){
            String[] paths = dest.split("/");
            DefaultDir father = manager.getRootDir();
            Deque<String> tmpPathStack = new ArrayDeque<>();
            for (int i = 1; i < paths.length; i++) {
                Item item = father.find(paths[i]);
                if (item == null || item.getDir_Attr()== Constant.ITEM_ATTR_FILE){
                    return ReturnUtil.error(item.getDir_Name() + "是文件，并非目录");
                }
                tmpPathStack.push(paths[i]);
                father = new DefaultDir(item);
            }

            if (tmpPathStack.isEmpty()){
                manager.setCurrentDir("/");
            }else{
                StringBuilder sb = new StringBuilder();
                for(String path:tmpPathStack){
                    sb.append("/"+path);
                }
                manager.setCurrentDir(sb.toString());
            }
            manager.setPathStack(tmpPathStack);
            manager.refreshCurrentDir();

        }else if (".".equals(dest) || "./".equals(dest)){
            return ReturnUtil.success();
        }else{
            String[] paths = dest.split("/");
            Deque<String> destPath = new ArrayDeque<>();
            Deque<String> currentPathStack = manager.getPathStack();
            Dictionary currentDir = manager.getCurrentDictionary();
            for (int i = 0; i < paths.length; i++) {//0号元素为空
                if ("..".equals(paths[i])){
                    if (currentPathStack.isEmpty()){
                        return ReturnUtil.error("根目录无上层目录");
                    }else{
                        currentPathStack.pop();
                    }
                }else{
                    Item item = currentDir.find(paths[i]);
                    if (item == null){
                        return ReturnUtil.error("未找到目录："+item.getDir_Name());
                    }else if(item.getDir_Attr() == Constant.ITEM_ATTR_FILE){
                        return ReturnUtil.error("目录项"+item.getDir_Name()+"是文件，非目录");
                    }else{
                        currentPathStack.push(paths[i]);
                    }
                }
            }

            if (currentPathStack.isEmpty()){
                manager.setCurrentDir("/");
            }else{
                StringBuilder sb = new StringBuilder();
                for(String path:currentPathStack){
                    sb.append("/"+path);
                }
                manager.setCurrentDir(sb.toString());
            }
            manager.setPathStack(currentPathStack);
            manager.refreshCurrentDir();
        }

        return ReturnUtil.success();

    }
    public void saveAll(){
        Disk disk = manager.getDisk();
        disk.store();
    }



}
