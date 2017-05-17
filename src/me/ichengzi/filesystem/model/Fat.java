package me.ichengzi.filesystem.model;

/**
 * Coding is pretty charming when you love it!
 *
 * Fat表的一个抽象
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 17:18
 */
public interface Fat {


    int[] getIntArray();
    int get(int i);
    int[] getClusList(int first);

    void set(int index,int val);
    int[] getFreeClus(int len);
    void freeClusList(int[] indexs);

    byte[] getBytes();
    void store();





}
