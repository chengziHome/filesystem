package me.ichengzi.filesystem.model;

import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * 对文件的一个抽象。
 * 注意哈，File本来也应该包含Item的东西，但是这里千万不要臃肿结构
 * 定义接口的时候尽量去符合"单一职责原则"和"隔离原则"。
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 17:19
 */
public interface File {


    byte[] getBytes();
    String getContent();
    void setContent(byte[] bytes);

    List<Sector> getSectors();
    void setSectors(List<Sector> sectors);


}
