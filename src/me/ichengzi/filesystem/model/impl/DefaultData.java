package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.*;
import me.ichengzi.filesystem.model.Dictionary;
import me.ichengzi.filesystem.util.Constant;

import java.util.*;

/**
 * Coding is pretty charming when you love it!\
 *
 * 注意哈，Data区和其他数据区结构和目的不一样，其他都是完全映射，
 * 而这里更像是一个内存去，其核心逻辑也在于固定大小内存的管理。
 * 内存管理所有核心的操作基本都集中在了这里
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:02
 */
public class DefaultData implements Data {


    private int offset;


    //事实证明，这个数据结构太简单，不够用
//    private Map<String,Integer[]> registeTable;
    /**
     * 登记所有已经加载到内存中的文件或者目录名称
     * 键是代表某个项的根路径(因为这个属性是绝对唯一的)，值为这个项所占用的缓存表的所有索引。
     *
     */
    private Deque<RegisteEntry> registeTable;

    private List<SectorEntry> sectorTable;

    private static final int SECTOR_TABLE_MAX = 100;

    private int remaining;

    private static final DefaultDiskManager manager = DefaultDiskManager.getManager();

    public DefaultData(int offset) {
        this.offset = offset;

        registeTable = new ArrayDeque<RegisteEntry>();
        //这里是个固定大小，模拟内存仅能容纳100个扇区，所以理论上下面的所有方法都不应该调用List的remove方法。
        sectorTable = new ArrayList<SectorEntry>();
        for (int i = 0; i < SECTOR_TABLE_MAX; i++) {
            sectorTable.add(null);
        }

        remaining = SECTOR_TABLE_MAX;
    }


    /*
        下面的方法都非常的重要，是整个内存管理模块最最核心的部分。
     */


    /**
     * 查找某个Item项在Sector缓存表涉及到的所有索引
     * @param item
     * @return
     */
    @Override
    public int[] search(Item item) {
        String absolutePath = item.getAbsolutePath();
        for (RegisteEntry entry:registeTable){
            String str1 = entry.getAbsolutePath();
            if (str1.equals(absolutePath)){
                return entry.getIndexs();
            }
        }
        return null;
    }


    /**
     * 加载某个目录项对应的文件或者目录的所有扇区。
     * @param item
     * @return
     */
    @Override
    public List<Sector> load(Item item) {

        String absolutePath = item.getAbsolutePath();
        if (search(item)!=null){
            List<Sector> result = new ArrayList<>();
            int[] indexs = search(item);

            for (Integer integer:indexs){
                SectorEntry entry = sectorTable.get(integer);
                result.add(entry.getSector());
            }
            return result;
        }else{//需要从byte数组加载
            int fstSec = item.getDir_FstClus();

            int[] indexs = manager.getFAT1().getClusList(fstSec);
            int need = indexs.length;
            while(need>remaining){//最简单的FIFO替换策略
                RegisteEntry entry = registeTable.removeFirst();
                int[] inds = entry.getIndexs();
                removeSectorTable(inds);
                remaining += inds.length;
            }
            List<Sector> list = getSectorList(indexs);
            int pos = 0;
            int[] res_indexs = new int[need];
            for (int i = 0; i < SECTOR_TABLE_MAX; i++) {
                if (sectorTable.get(i)==null){//空闲
                    SectorEntry entry = new SectorEntry(indexs[pos],list.get(pos));
                    sectorTable.add(i,entry);
                    res_indexs[pos] = i;
                    pos++;
                }
                if (pos == need)
                    break;
            }
            RegisteEntry entry = new RegisteEntry(absolutePath,res_indexs);
            registeTable.addLast(entry);
            return list;
        }

    }

    /**
     * 从SectorTable缓存中删除指定的一系列空间
     * @param indexs
     */
    private void removeSectorTable(int[] indexs){
        for (int i = 0; i < indexs.length; i++) {
            sectorTable.add(indexs[i],null);
        }
    }


    /**
     * 将指定的索引处的Sector写回到byte数组里面
     * @param indexs
     */
    @Override
    public void store(int[] indexs) {
        // TODO: 2017/5/11 这个方法都不知道会不会用到
    }

    @Override
    public void store(int[] indexs, List<Sector> sectors) {

    }


    @Override
    public void delete(Item item) {
        load(manager.getCurrentDictionary().getItem());
        Dictionary currentDir = manager.getCurrentDictionary();
        currentDir.delete(item);
        currentDir.store();
        if (search(item)!=null){
            int[] indexs = search(item);
            removeSectorTable(indexs);
            remaining += indexs.length;
        }
    }

    /**
     * 添加一个空文件，其实主要是更新一下注册表的sectorTable
     * @return
     */
    @Override
    public int addFile(Item file) {
//        int[] indexs = null;
//        if ((indexs = manager.getFAT1().getFreeClus(1))!=null){//有空闲空间
//            Sector sector = new DefaultSector(new byte[Constant.SECTOR_SIZE]);
//            List<Sector> sectors = new ArrayList<>();
//            sectors.add(sector);
//            store(indexs,sectors);
//            load(file);
//            Dictionary currentDir = manager.getCurrentDictionary();
//            currentDir.addItem(file);
//            currentDir.store();
//        }
        return -1;
    }

    @Override
    public int[] addDir(Item dir) {
//        int[] indexs = null;
//        if ((indexs = manager.getFAT1().getFreeClus(1))!=null){//有空闲空间
//            Sector sector = new DefaultSector(new byte[Constant.SECTOR_SIZE]);
//            List<Sector> sectors = new ArrayList<>();
//            sectors.add(sector);
//            store(indexs,sectors);
//            load(dir);
//            Dictionary currentDir = manager.getCurrentDictionary();
//            currentDir.addItem(dir);
//            currentDir.store();
//        }
        return null;
    }

    @Override
    public int[] edit(Item item, String content) {
        // TODO: 2017/5/11 这个暂时不用去实现
        return null;
    }

    @Override
    public Sector getSector(int secNum) {
        return null;
    }


    /**
     * 从byte数组中加载Sector对象的数组，
     * 要注意，这里索引从2开始，就是说byte数组的第一组的索引是2
     * @param indexs
     * @return
     */
    @Override
    public List<Sector> getSectorList(int[] indexs) {

        List<Sector> result = new ArrayList<>();
        for (int i = 0; i < indexs.length; i++) {
            int index = indexs[i];
            byte[] bytes = DefaultDiskManager.getManager().getDisk().getBytes();
            int copy_start = offset+(index-2)*Constant.SECTOR_SIZE;
            int copy_end = offset+(index-1)*Constant.SECTOR_SIZE;
            Sector sector = new DefaultSector(Arrays.copyOfRange(bytes,copy_start,copy_end),index);
            result.add(sector);
        }

        return result;
    }


    /**
     * 之所以把这个“页表项”的概念给抽象出来，一方面是确实数据够复杂，需要单独抽象封装了，
     * 另一方面，方便后期扩展，比如后面如果要给表格加个“标志位”之类的属性。
     *
     *
     */
    class SectorEntry{
        private int secNum;//真实的扇区编号
        private Sector sector;

        public SectorEntry(int secNum, Sector sector) {
            this.secNum = secNum;
            this.sector = sector;
        }

        public int getSecNum() {
            return secNum;
        }

        public Sector getSector() {
            return sector;
        }

        @Override
        public String toString() {
            return "SectorEntry{" +
                    "secNum=" + secNum +
                    ", sector=" + sector +
                    '}';
        }
    }

    class RegisteEntry{
        private String absolutePath;
        private int[] indexs;

        public RegisteEntry(String absolutePath, int[] indexs) {
            this.absolutePath = absolutePath;
            this.indexs = indexs;
        }


        public String getAbsolutePath() {
            return absolutePath;
        }

        public int[] getIndexs() {
            return indexs;
        }

        @Override
        public String toString() {
            return "RegisteEntry{" +
                    "absolutePath='" + absolutePath + '\'' +
                    ", indexs=" + Arrays.toString(indexs) +
                    '}';
        }
    }


    /*
        测试方法
     */

    public void printTable(){
        System.out.println("regisTable:"+Arrays.toString(registeTable.toArray()));
        System.out.println("sectorTable:"+Arrays.toString(sectorTable.toArray()));

    }

    @Override
    public void store() {
        throw new UnsupportedOperationException("Data数据区已扇区为单位更新，故Data不需要持久化");
    }



    @Override
    public int getDataOffset() {
        return offset;
    }

    @Override
    public void initFileSector(int[] indexs) {
        for (int i = 0; i < indexs.length; i++) {
            int sec_num = indexs[i];
            int start = offset + (sec_num-2)*Constant.SECTOR_SIZE;
            DefaultDiskManager.getManager().getDisk().store(new byte[Constant.SECTOR_SIZE],start);
        }
    }


    @Override
    public void initDirSector(int[] indexs) {
        /*
            目录的第一个扇区不为空，有两个目录项.
            这里简单起见，直接操作一个32长度的byte数组
         */
        byte[] currentItem = new byte[Constant.ITEM_SIZE];
        currentItem[0] = Constant.CURRENT_ITEM_NAME;
        for (int i = 1; i < 11; i++) {
            currentItem[i]  = Constant.BLANK_SPACE;
        }
        currentItem[11] = Constant.ITEM_ATTR_DIR;
        byte[] fatherItem = new byte[Constant.ITEM_SIZE];
        fatherItem[0] = Constant.CURRENT_ITEM_NAME;
        fatherItem[1] = Constant.CURRENT_ITEM_NAME;
        for (int i = 2; i < 11; i++) {
            fatherItem[i] = Constant.BLANK_SPACE;
        }
        fatherItem[11] = Constant.ITEM_ATTR_DIR;
        // TODO: 2017/5/17 时间暂时不做处理
        byte[] fst_Sec = new byte[Constant.SECTOR_SIZE];
        System.arraycopy(currentItem,0,fst_Sec,0,Constant.ITEM_SIZE);
        System.arraycopy(fatherItem,0,fst_Sec,Constant.ITEM_SIZE,Constant.ITEM_SIZE);

        DefaultDiskManager.getManager().getDisk().store(fst_Sec,offset+(indexs[0]-2)*Constant.SECTOR_SIZE);
        for (int i = 1; i < indexs.length; i++) {
            byte[] initBytes = new byte[Constant.SECTOR_SIZE];
            int sec_num = indexs[i];
            int start = offset + (sec_num-2)*Constant.SECTOR_SIZE;
            DefaultDiskManager.getManager().getDisk().store(initBytes,start);
        }

    }


    /**
     * 删除掉注册表和扇区链表中某个item项的记录，
     * 有则删除，没有则什么都不做
     * @param absolute
     */
    @Override
    public void removeItem(String absolute) {
        int[] indexs = null;
        for (RegisteEntry entry:registeTable){
            if (absolute.equals(entry.getAbsolutePath())){
                indexs = entry.getIndexs();
                registeTable.remove(entry);
                break;
            }
        }
        if (indexs==null) return;
        for (int i = 0; i < indexs.length; i++) {
            sectorTable.remove(indexs[i]);
        }
    }


}
