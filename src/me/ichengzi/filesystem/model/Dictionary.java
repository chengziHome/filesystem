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

    Item getItem();

    List<Item> getItems();
    void addItem(Item item);
    Item find(String name);
    void delete(Item item);

    void store();//尽管目前每次修改目录页仅仅涉及到一条目录项的变动，但是实现方式上还是整个扇区的覆盖直接重写。

    List<Sector> getSectors();
    void setSectors(List<Sector> sectors);

    boolean hasAvailable();

}
