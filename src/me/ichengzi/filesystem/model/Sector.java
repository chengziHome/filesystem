package me.ichengzi.filesystem.model;

/**
 * Coding is pretty charming when you love it!
 *
 * 一个扇区的抽象,一个扇区512bit
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 17:18
 */
public interface Sector {

    byte[] getBytes();
    byte[] getBytes(int offset,int end);

    void setBytes(byte[] bs,int offset,int len);

    void store();

}
