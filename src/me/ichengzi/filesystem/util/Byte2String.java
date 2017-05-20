package me.ichengzi.filesystem.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/11
 * @time 17:12
 */
public class Byte2String {

    /**
     * 有这个方法的原因在于：
     * 本来String是由char数组组成的，而java里面char是2字节，但是在boot分区里面由于
     * 表示字符全部都是简单字符，所以用的ASSIC码，所以一个字节就够了，于是就存在了将byte数组
     * 转化为String的任务
     * @param bs
     * @return
     */

    public static String valueOf(byte[] bs){
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < bs.length; i++) {
            if (bs[i]!=0){
                list.add((char)bs[i]);
            }
        }
        char[] cs = new char[list.size()];
        for (int i = 0; i < list.size(); i++) {
            cs[i] = list.get(i);
        }
        return new String(cs);

    }


    /**
     * 很显然和上面对应，在持久化的时候使用
     * @param name
     * @return
     */
    public static byte[] getBytes(String name){
        char[] chars = name.toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }


    /**
     * 专门为文件的内容转化设置的，
     * 因为在文件编辑中会遇到问题
     * @param bs
     * @return
     */
    public static String getContent(byte[] bs){
        int end = bs.length;
        while(end>0&&bs[--end]==0);//把byte数组末尾的所有0去掉
        byte[] bytes = new byte[end+1];
        for (int i = 0; i < end + 1; i++) {
            bytes[i] = bs[i];
        }
        String result = new String(bytes);
        return result;
    }




}
