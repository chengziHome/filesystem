package me.ichengzi.filesystem.util;

/**
 * Coding is pretty charming when you love it!
 *
 * 保存一些系统中约定的约定的常量。
 *
 * @author Chengzi Start
 * @date 2017/5/10
 * @time 0:20
 */
public class Constant {

    public static final byte ITEM_ATTR_DISKID = 0x28;
    public static final byte ITEM_ATTR_FILE = 0x20;
    public static final byte ITEM_ATTR_DIR = 0x10;
    public static final byte ITEM_FIRST_DISABLED = (byte) 0xE5;
    public static final byte ITEM_FIRST_NOUSE = 0x00;

    public static final int BOOT_SECNUM = 1;//必须引用这个值，这个值不变。
    public static final int SECTOR_SIZE = 512;
    public static final int ITEM_SIZE = 32;
    public static final int ROOT_ITEMNUM = 224;


    public static final int CLUS_LIST_END = 0xFFF;

    public static final int DISK_TOTAL_SIZE = 1500 * 1024;//给Disk的byte数组充足空间



}
