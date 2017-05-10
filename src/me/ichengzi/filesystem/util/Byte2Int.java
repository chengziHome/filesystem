package me.ichengzi.filesystem.util;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/10
 * @time 15:49
 */
public class Byte2Int {

    /**
     * 数值型只有三种情况，除了FAT分区
     * @param bs
     * @return
     */
    public static int getInt(byte[] bs){
        int result = 0;
        int len = bs.length;
        if (len==1){
            result = bs[0];
        }else if(len==2){
            int high = bs[1];
            int low = bs[0];
            result = high<<8 + low;
        }else if(len==4){
            int high1 = bs[3];
            int high2 = bs[2];
            int high3 = bs[1];
            int high4 = bs[0];
            result = high1<<24 + high2<<16 + high3<<8 + high4;
        }
        return result;
    }
}
