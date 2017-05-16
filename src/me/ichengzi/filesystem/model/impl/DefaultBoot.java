package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.Boot;
import me.ichengzi.filesystem.util.Byte2Int;
import me.ichengzi.filesystem.util.Byte2String;
import me.ichengzi.filesystem.util.Constant;

import java.util.Arrays;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:00
 */
public class DefaultBoot implements Boot {

    byte[] boot;

    private BootAttr BS_JmpBoot;
    private BootAttr BS_OEMName;
    private BootAttr BPB_BytePerSec;
    private BootAttr BPB_SecPerClus;
    private BootAttr BPB_ResvSecCnt;
    private BootAttr BPB_NumFATS;
    private BootAttr BPB_RootEntCnt;
    private BootAttr BPB_TotSec16;
    private BootAttr BPB_Media;
    private BootAttr BPB_FATz16;
    private BootAttr BPB_SecPerTrk;
    private BootAttr BPB_NumHeads;
    private BootAttr BPB_HiddSec;
    private BootAttr BPB_TotSec32;
    private BootAttr BS_DrvNum;
    private BootAttr BS_Reserved1;
    private BootAttr BS_BootSig;
    private BootAttr BS_VolID;
    private BootAttr BS_VolLab;
    private BootAttr BS_FileSysType;
    private BootAttr BS_BootCode;
    private BootAttr BS_End;

    /**
     * 这个无参构造器主要用于创建Disk文件
     */
    public DefaultBoot() {
        this.boot = new byte[Constant.BOOT_SECNUM*Constant.SECTOR_SIZE];
    }

    /**
     * 这个构造器用于加载Boot数据结构
     * @param bs
     */
    public DefaultBoot(byte[] bs){
        boot = bs;
        setBS_JmpBoot(Arrays.copyOfRange(boot,0,3));
        setBS_OEMName(Byte2String.valueOf(Arrays.copyOfRange(boot,3,11)));
        setBPB_BytePerSec(Byte2Int.getInt(Arrays.copyOfRange(boot,11,13)));
        setBPB_SecPerClus(Byte2Int.getInt(Arrays.copyOfRange(boot,13,14)));
        setBPB_ResvSecCnt(Byte2Int.getInt(Arrays.copyOfRange(boot,14,16)));
        setBPB_NumFATS(Byte2Int.getInt(Arrays.copyOfRange(boot,16,17)));
        setBPB_RootEntCnt(Byte2Int.getInt(Arrays.copyOfRange(boot,17,19)));
        setBPB_TotSec16(Byte2Int.getInt(Arrays.copyOfRange(boot,19,21)));
        setBPB_Media(Byte2Int.getInt(Arrays.copyOfRange(boot,21,22)));
        setBPB_FATz16(Byte2Int.getInt(Arrays.copyOfRange(boot,22,24)));
        setBPB_SecPerTrk(Byte2Int.getInt(Arrays.copyOfRange(boot,24,26)));
        setBPB_NumHeads(Byte2Int.getInt(Arrays.copyOfRange(boot,26,28)));
        setBPB_HiddSec(Byte2Int.getInt(Arrays.copyOfRange(boot,28,32)));

        setBPB_TotSec32(Byte2Int.getInt(Arrays.copyOfRange(boot,32,36)));
        setBS_DrvNum(Byte2Int.getInt(Arrays.copyOfRange(boot,36,37)));
        setBS_Reserved1(Byte2Int.getInt(Arrays.copyOfRange(boot,37,38)));
        setBS_BootSig(Byte2Int.getInt(Arrays.copyOfRange(boot,38,39)));
        setBS_VolID(Byte2Int.getInt(Arrays.copyOfRange(boot,39,43)));
        setBS_VolLab(Byte2String .valueOf(Arrays.copyOfRange(boot,43,54)));
        setBS_FileSysType(Byte2String.valueOf(Arrays.copyOfRange(boot,54,62)));
        setBoot_code(Byte2String.valueOf(Arrays.copyOfRange(boot,62,448)));
        setBoot_end(Byte2Int.getInt(Arrays.copyOfRange(boot,510,512)));

    }




    /*
        每个set方法都要对其参数的长度进行检查.
        注意，这里的set方法全部接受的String类型或者int类型，而不是byte数组类型，
        其实这里也不是很矛盾，因为这里的set方法有两个用处，一个用byte数组初始化的时候，
        另一种情况是用高级数据结构创建磁盘的时候就很方便了。
     */
    @Override
    public byte[] getBS_JmpBoot() {
        return BS_JmpBoot.getBytes();
    }


    @Override
    public void setBS_JmpBoot(byte[] bs) {
        if (bs.length!=3)
            throw new IllegalArgumentException("BS_JmpBoot的长度为3");
        if (BS_JmpBoot!=null){
//            BS_JmpBoot.setStrVal(val);
        }else{
            BS_JmpBoot = new BootAttr(3);
            BS_JmpBoot.setName("BS_JmpBoot");
//            BS_JmpBoot.setStrVal(val);
            BS_JmpBoot.setContent("Boot程序的跳转地址");
            BS_JmpBoot.setIsNumber(false);
            BS_JmpBoot.setOffset(0);
        }

    }

    @Override
    public String getBS_OEMName() {
        return BS_OEMName.getStrVal();
    }

    @Override
    public void setBS_OEMName(String name) {
        if(name.toCharArray().length>8)
            throw new IllegalArgumentException("OEMName长度最大为8");
        if (BS_OEMName!=null){
            BS_OEMName.setStrVal(name);
        }else{
            BS_OEMName = new BootAttr(8);
            BS_OEMName.setName("BS_OEMName");
            BS_OEMName.setStrVal(name);
            BS_OEMName.setContent("厂商名");
            BS_OEMName.setIsNumber(false);
            BS_OEMName.setOffset(3);
        }
    }

    @Override
    public int getBPB_BytePerSec() {
        return BPB_BytePerSec.getIntVal();
    }

    @Override
    public void setBPB_BytePerSec(int val) {
        if (val>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_BytePerSec只有2Byte空间，最大值为32767");
        if (BPB_BytePerSec!=null){
            BPB_BytePerSec.setIntVal(val);
        }else{
            BPB_BytePerSec = new BootAttr(2);
            BPB_BytePerSec.setName("BPB_BytePerSec");
            BPB_BytePerSec.setIntVal(val);
            BPB_BytePerSec.setContent("每扇区字节数");
            BPB_BytePerSec.setIsNumber(true);
            BPB_BytePerSec.setOffset(11);
        }
    }

    @Override
    public int getBPB_SecPerClus() {
        return BPB_SecPerClus.getIntVal();
    }

    @Override
    public void setBPB_SecPerClus(int val) {
        if (val>Byte.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_SecPerClus只有1Byte空间，最大值为127");
        if (BPB_SecPerClus!=null){
            BPB_SecPerClus.setIntVal(val);
        }else{
            BPB_SecPerClus = new BootAttr(1);
            BPB_SecPerClus.setName("BPB_SecPerClus");
            BPB_SecPerClus.setIntVal(val);
            BPB_SecPerClus.setContent("每簇扇区数");
            BPB_SecPerClus.setIsNumber(true);
            BPB_SecPerClus.setOffset(13);
        }
    }

    @Override
    public int getBPB_ResvSecCnt() {
        return BPB_ResvSecCnt.getIntVal();
    }

    @Override
    public void setBPB_ResvSecCnt(int val) {
        if (val>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_ResvSecCnt只有2Byte空间，最大值为32767");
        if (BPB_ResvSecCnt!=null){
            BPB_ResvSecCnt.setIntVal(val);
        }else{
            BPB_ResvSecCnt = new BootAttr(2);
            BPB_ResvSecCnt.setName("BPB_ResvSecCnt");
            BPB_ResvSecCnt.setIntVal(val);
            BPB_ResvSecCnt.setContent("Boot记录占用几个扇区");
            BPB_ResvSecCnt.setIsNumber(true);
            BPB_ResvSecCnt.setOffset(14);
        }
    }

    @Override
    public int getBPB_NumFATS() {
        return BPB_NumFATS.getIntVal();
    }

    @Override
    public void setBPB_NumFATS(int val) {
        if (val>Byte.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_NumFATS只有1Byte空间，最大值为127");
        if (BPB_NumFATS!=null){
            BPB_NumFATS.setIntVal(val);
        }else{
            BPB_NumFATS = new BootAttr(1);
            BPB_NumFATS.setName("BPB_NumFATS");
            BPB_NumFATS.setIntVal(val);
            BPB_NumFATS.setContent("共有多少FAT表");
            BPB_NumFATS.setIsNumber(true);
            BPB_NumFATS.setOffset(16);
        }
    }

    @Override
    public int getBPB_RootEntCnt() {
        return BPB_RootEntCnt.getIntVal();
    }

    @Override
    public void setBPB_RootEntCnt(int val) {
        if (val>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_RootEntCnt只有1Byte空间，最大值为127");
        if (BPB_RootEntCnt!=null){
            BPB_RootEntCnt.setIntVal(val);
        }else{
            BPB_RootEntCnt = new BootAttr(2);
            BPB_RootEntCnt.setName("BPB_RootEntCnt");
            BPB_RootEntCnt.setIntVal(val);
            BPB_RootEntCnt.setContent("根目录文件数的最大值");
            BPB_RootEntCnt.setIsNumber(true);
            BPB_RootEntCnt.setOffset(17);
        }
    }

    @Override
    public int getBPB_TotSec16() {
        return BPB_TotSec16.getIntVal();
    }

    @Override
    public void setBPB_TotSec16(int val) {
        if (val>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_TotSec16只有1Byte空间，最大值为127");
        if (BPB_TotSec16!=null){
            BPB_TotSec16.setIntVal(val);
        }else{
            BPB_TotSec16 = new BootAttr(2);
            BPB_TotSec16.setName("BPB_TotSec16");
            BPB_TotSec16.setIntVal(val);
            BPB_TotSec16.setContent("扇区总数");
            BPB_TotSec16.setIsNumber(true);
            BPB_TotSec16.setOffset(19);
        }
    }

    @Override
    public int getBPB_Media() {
        return BPB_Media.getIntVal();
    }

    @Override
    public void setBPB_Media(int name) {
        if (name>Byte.MAX_VALUE*2+1){
            throw new IllegalArgumentException("BPB_Media的长度为1");
        }

        if (BPB_Media!=null){
            BPB_Media.setIntVal(name);
        }else{
            BPB_Media = new BootAttr(1);
            BPB_Media.setName("BPB_Media");
            BPB_Media.setIntVal(name);
            BPB_Media.setContent("介质描述");
            BPB_Media.setIsNumber(false);
            BPB_Media.setOffset(21);
        }
    }

    @Override
    public int getBPB_FATSz16() {
        return BPB_FATz16.getIntVal();
    }

    @Override
    public void setBPB_FATz16(int val) {
        if (val>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_FATz16只有2Byte空间，最大值为32767");
        if (BPB_FATz16!=null){
            BPB_FATz16.setIntVal(val);
        }else{
            BPB_FATz16 = new BootAttr(2);
            BPB_FATz16.setName("BPB_FATz16");
            BPB_FATz16.setIntVal(val);
            BPB_FATz16.setContent("每FAT扇区数");
            BPB_FATz16.setIsNumber(true);
            BPB_FATz16.setOffset(22);
        }
    }

    @Override
    public int getBPB_SecPerTrk() {
        return BPB_SecPerTrk.getIntVal();
    }

    @Override
    public void setBPB_SecPerTrk(int val) {
        if (val>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_SecPerTrk只有2Byte空间，最大值为32767");
        if (BPB_SecPerTrk!=null){
            BPB_SecPerTrk.setIntVal(val);
        }else{
            BPB_SecPerTrk = new BootAttr(2);
            BPB_SecPerTrk.setName("BPB_SecPerTrk");
            BPB_SecPerTrk.setIntVal(val);
            BPB_SecPerTrk.setContent("每磁道扇区数");
            BPB_SecPerTrk.setIsNumber(true);
            BPB_SecPerTrk.setOffset(24);
        }
    }

    @Override
    public int getBPB_NumHeads() {
        return BPB_NumHeads.getIntVal();
    }

    @Override
    public void setBPB_NumHeads(int val) {
        if (val>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BPB_NumHeads只有2Byte空间，最大值为32767");
        if (BPB_NumHeads!=null){
            BPB_NumHeads.setIntVal(val);
        }else{
            BPB_NumHeads = new BootAttr(2);
            BPB_NumHeads.setName("BPB_NumHeads");
            BPB_NumHeads.setIntVal(val);
            BPB_NumHeads.setContent("磁头数");
            BPB_NumHeads.setIsNumber(true);
            BPB_NumHeads.setOffset(26);
        }
    }

    @Override
    public int getBPB_HiddSec() {
        return BPB_HiddSec.getIntVal();
    }

    @Override
    public void setBPB_HiddSec(int val) {
//        if (val>Integer.MAX_VALUE)
//            throw new IllegalArgumentException("BPB_NumHeads只有2Byte空间，最大值为32767");
        if (BPB_HiddSec!=null){
            BPB_HiddSec.setIntVal(val);
        }else{
            BPB_HiddSec = new BootAttr(4);
            BPB_HiddSec.setName("BPB_HiddSec");
            BPB_HiddSec.setIntVal(val);
            BPB_HiddSec.setContent("隐藏扇区数");
            BPB_HiddSec.setIsNumber(true);
            BPB_HiddSec.setOffset(28);
        }
    }

    @Override
    public int getBPB_TotSec32() {
        return BPB_TotSec32.getIntVal();
    }

    @Override
    public void setBPB_TotSec32(int val) {
        if (BPB_TotSec32!=null){
            BPB_TotSec32.setIntVal(val);
        }else{
            BPB_TotSec32 = new BootAttr(4);
            BPB_TotSec32.setName("BPB_TotSec32");
            BPB_TotSec32.setIntVal(val);
            BPB_TotSec32.setContent("BPB_TotSec16是零，则由这个值记录扇区数");
            BPB_TotSec32.setIsNumber(true);
            BPB_TotSec32.setOffset(32);
        }
    }

    @Override
    public int getBS_DrvNum() {
        return BS_DrvNum.getIntVal();
    }

    @Override
    public void setBS_DrvNum(int val) {
        if (val>Byte.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BS_DrvNum只有1Byte空间，最大值为127");
        if (BS_DrvNum!=null){
            BS_DrvNum.setIntVal(val);
        }else{
            BS_DrvNum = new BootAttr(1);
            BS_DrvNum.setName("BS_DrvNum");
            BS_DrvNum.setIntVal(val);
            BS_DrvNum.setContent("中断13的驱动器号");
            BS_DrvNum.setIsNumber(true);
            BS_DrvNum.setOffset(36);
        }
    }

    @Override
    public int getBS_Reserved1() {
        return BS_Reserved1.getIntVal();
    }

    @Override
    public void setBS_Reserved1(int val) {
        if (val>Byte.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BS_Reserved1有1Byte空间，最大值为127");
        if (BS_Reserved1!=null){
            BS_Reserved1.setIntVal(val);
        }else{
            BS_Reserved1 = new BootAttr(1);
            BS_Reserved1.setName("BS_Reserved1");
            BS_Reserved1.setIntVal(val);
            BS_Reserved1.setContent("保留位");
            BS_Reserved1.setIsNumber(true);
            BS_Reserved1.setOffset(37);
        }
    }

    @Override
    public int getBS_BootSig() {
        return BS_BootSig.getIntVal();
    }

    @Override
    public void setBS_BootSig(int val) {
        if (val>Byte.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BS_BootSig有1Byte空间，最大值为127");
        if (BS_BootSig!=null){
            BS_BootSig.setIntVal(val);
        }else{
            BS_BootSig = new BootAttr(1);
            BS_BootSig.setName("BS_BootSig");
            BS_BootSig.setIntVal(val);
            BS_BootSig.setContent("扩展引导标记");
            BS_BootSig.setIsNumber(true);
            BS_BootSig.setOffset(38);
        }
    }

    @Override
    public int getBS_VolID() {
        return BS_VolID.getIntVal();
    }

    @Override
    public void setBS_VolID(int val) {
        if (BS_VolID!=null){
            BS_VolID.setIntVal(val);
        }else{
            BS_VolID = new BootAttr(4);
            BS_VolID.setName("BS_VolID");
            BS_VolID.setIntVal(val);
            BS_VolID.setContent("卷序列号");
            BS_VolID.setIsNumber(true);
            BS_VolID.setOffset(39);
        }
    }

    @Override
    public String getBS_VolLab() {
        return BS_VolLab.getStrVal();
    }

    @Override
    public void setBS_VolLab(String name) {
        if(name.toCharArray().length>11)
            throw new IllegalArgumentException("BS_VolLab长度最大为11");
        if (BS_VolLab!=null){
            BS_VolLab.setStrVal(name);
        }else{
            BS_VolLab = new BootAttr(11);
            BS_VolLab.setName("BS_VolLab");
            BS_VolLab.setStrVal(name);
            BS_VolLab.setContent("卷标");
            BS_VolLab.setIsNumber(false);
            BS_VolLab.setOffset(43);
        }
    }

    @Override
    public String getBS_FileSysType() {
        return BS_FileSysType.getStrVal();
    }

    @Override
    public void setBS_FileSysType(String type) {
        if(type.toCharArray().length>11)
            throw new IllegalArgumentException("BS_FileSysType长度最大为8");
        if (BS_FileSysType!=null){
            BS_FileSysType.setStrVal(type);
        }else{
            BS_FileSysType = new BootAttr(8);
            BS_FileSysType.setName("BS_FileSysType");
            BS_FileSysType.setStrVal(type);
            BS_FileSysType.setContent("文件系统的类型");
            BS_FileSysType.setIsNumber(false);
            BS_FileSysType.setOffset(54);
        }
    }

    @Override
    public String getBoot_code() {
        return BS_BootCode.getStrVal();
    }

    @Override
    public void setBoot_code(String code) {
        if(code.toCharArray().length>448)
            throw new IllegalArgumentException("Boot_code长度最大为448");
        if (BS_BootCode!=null){
            BS_BootCode.setStrVal(code);
        }else{
            BS_BootCode = new BootAttr(448);
            BS_BootCode.setName("BS_BootCode");
            BS_BootCode.setStrVal(code);
            BS_BootCode.setContent("引导代码、数据、填充字符");
            BS_BootCode.setIsNumber(false);
            BS_BootCode.setOffset(62);
        }
    }

    @Override
    public int getBoot_end() {
        return BS_End.getIntVal();
    }

    @Override
    public void setBoot_end(int end) {
        if(end>Short.MAX_VALUE*2+1)
            throw new IllegalArgumentException("BS_FileSysType长度最大为8");
        if (BS_End!=null){
            BS_End.setIntVal(end);
        }else{
            BS_End = new BootAttr(2);
            BS_End.setName("BS_End");
            BS_End.setIntVal(end);
            BS_End.setContent("结束标志");
            BS_End.setIsNumber(false);
            BS_End.setOffset(510);
        }
    }

    @Override
    public void store() {
        throw new UnsupportedOperationException("Boot分区不会更改,不需要回写");
    }


    @Override
    public String toString() {
        return "DefaultBoot{" +
                "\nboot=" + Arrays.toString(boot) +
                "\n, BS_JmpBoot=" + BS_JmpBoot.getBytes() +
                "\n, BS_OEMName=" + BS_OEMName.getStrVal() +
                "\n, BPB_BytePerSec=" + BPB_BytePerSec.getIntVal() +
                "\n, BPB_SecPerClus=" + BPB_SecPerClus.getIntVal() +
                "\n, BPB_ResvSecCnt=" + BPB_ResvSecCnt.getIntVal() +
                "\n, BPB_NumFATS=" + BPB_NumFATS.getIntVal() +
                "\n, BPB_RootEntCnt=" + BPB_RootEntCnt.getIntVal() +
                "\n, BPB_TotSec16=" + BPB_TotSec16.getIntVal() +
                "\n, BPB_Media=" + BPB_Media.getIntVal() +
                "\n, BPB_FATz16=" + BPB_FATz16.getIntVal() +
                "\n, BPB_SecPerTrk=" + BPB_SecPerTrk.getIntVal() +
                "\n, BPB_NumHeads=" + BPB_NumHeads.getIntVal() +
                "\n, BPB_HiddSec=" + BPB_HiddSec.getIntVal() +
                "\n, BPB_TotSec32=" + BPB_TotSec32.getIntVal() +
                "\n, BS_DrvNum=" + BS_DrvNum.getIntVal() +
                "\n, BS_Reserved1=" + BS_Reserved1.getIntVal() +
                "\n, BS_BootSig=" + BS_BootSig.getIntVal() +
                "\n, BS_VolID=" + BS_VolID.getIntVal() +
                "\n, BS_VolLab=" + BS_VolLab.getStrVal() +
                "\n, BS_FileSysType=" + BS_FileSysType.getStrVal() +
                "\n, BS_BootCode=" + Arrays.toString(BS_BootCode.getBytes()) +
                "\n, BS_End=" + BS_End.getIntVal() +
                "\n}";
    }
}
