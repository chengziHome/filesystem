package me.ichengzi.filesystem.model;

/**
 * Coding is pretty charming when you love it!
 *
 *
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 17:18
 */
public interface Boot {

    void init();//也就是将Boot对象的属性持久化

    String getBS_JmpBoot();
    void setBS_JmpBoot(String val);
    String getBS_OEMName();
    void setBS_OEMName(String name);
    int getBPB_BytePerSec();
    void setBPB_BytePerSec(int val);
    int getBPB_SecPerClus();
    void setBPB_SecPerClus(int val);

    int getBPB_ResvSecCnt();
    void setBPB_ResvSecCnt(int val);
    int getBPB_NumFATS();
    void setBPB_NumFATS(int val);
    int getBPB_RootEntCnt();
    void setBPB_RootEntCnt(int val);
    int getBPB_TotSec16();
    void setBPB_TotSec16(int val);
    String getBPB_Media();
    void setBPB_Media(String name);
    int getBPB_FATSz16();
    void setBPB_FATz16(int val);
    int getBPB_SecPerTrk();
    void setBPB_SecPerTrk();
    int getBPB_NumHeads();
    void setBPB_NumHeads(int val);
    int getBPB_HiddSec();
    void setBPB_HiddSec(int val);
    int getBPB_TotSec32();
    void setBPB_TotSec32(int val);
    int getBS_DrvNum();
    void setBS_DrvNum(int val);
    int getBS_Reserved1();
    void setBS_Reserved1(int val);
    int getBS_BootSig();
    void setBS_BootSig();
    int getBS_VolID();
    void setBS_VolID(int val);
    String getBS_VolLab();
    void setBS_VolLab(String name);
    String getBS_FileSysType();
    void setBS_FileSysType(String type);
    String getBoot_code();
    void setBoot_code(String code);
    String getBoot_end();
    void setBoot_end(String end);


}
