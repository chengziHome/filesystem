package me.ichengzi.filesystem.model;

import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 目录的一个抽象
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 17:19
 */
public interface Dictionary {

    List<Item> getItems();
    void addItem(Item item);



}
