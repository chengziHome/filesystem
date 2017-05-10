package me.ichengzi.filesystem.model.impl;

import me.ichengzi.filesystem.model.File;
import me.ichengzi.filesystem.model.Item;
import me.ichengzi.filesystem.model.Sector;

import java.util.List;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:03
 */
public class DefaultFile implements File,Item{





    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public void setContent(byte[] bytes) {

    }

    @Override
    public String getDir_Name() {
        return null;
    }

    @Override
    public void setDir_Name(String name) {

    }

    @Override
    public int getDir_Attr() {
        return 0;
    }


    @Override
    public void setDir_Attr(int val) {

    }



    @Override
    public String getReserved() {
        return null;
    }

    @Override
    public void setReserve(String name) {

    }

    @Override
    public String getDir_WrtTime() {
        return null;
    }

    @Override
    public void setDir_WrtTime(String time) {

    }

    @Override
    public String getDir_WrtDate() {
        return null;
    }

    @Override
    public void setDir_WrtDate(String date) {

    }

    @Override
    public int getDir_FstClus() {
        return 0;
    }

    @Override
    public void setDir_FstClus(int val) {

    }

    @Override
    public int getDir_FileSize() {
        return 0;
    }

    @Override
    public void setDir_FileSize(int val) {

    }

    @Override
    public List<Sector> getSector() {
        return null;
    }

    @Override
    public void setSector(List<Sector> sectors) {

    }

    @Override
    public byte getFirstByte() {
        return 0;
    }
}
