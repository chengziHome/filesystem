package me.ichengzi.filesystem.model;

import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 根目录区的抽象
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 21:44
 */
public interface Root {

    List<Item> getItems();
    boolean addItem(Item item);


}
