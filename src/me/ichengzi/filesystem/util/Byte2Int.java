package me.ichengzi.filesystem.util;

import me.ichengzi.filesystem.model.Data;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
            result = Byte.toUnsignedInt(bs[0]);
        }else if(len==2){
            int high = Byte.toUnsignedInt(bs[1]);
            int low = Byte.toUnsignedInt(bs[0]);
            result = (high<<8) + low;
        }else if(len==4){
            int high1 = Byte.toUnsignedInt(bs[3]);
            int high2 = Byte.toUnsignedInt(bs[2]);
            int high3 = Byte.toUnsignedInt(bs[1]);
            int high4 = Byte.toUnsignedInt(bs[0]);
            result = (high1<<2) + (high2<<16) + (high3<<8) + high4;
        }
        return result;
    }


    /**
     * 下面两个方法是专门为设置Item项的时间准备的
     * 主要根据FAT12规范。
     * int数组中有两个，第一个代表时间，第二个代表日期
     * @return
     */
    public static int[] getTime(){
        int[] result = new int[2];
        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.SECOND);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);


        int time = (second/2) + (minute<<5) + (hour<<11);
        int date = (day) + (month<<5) + ((year-1980)<<9);

        result[0] = time;
        result[1] = date;

        return result;



    }


}
