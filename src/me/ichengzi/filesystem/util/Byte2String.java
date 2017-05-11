package me.ichengzi.filesystem.util;

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
        char[] chars = new char[bs.length];
        for (int i = 0; i < bs.length; i++) {
            chars[i] = (char) bs[i];
        }
        return new String(chars);

    }
}
