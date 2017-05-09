package me.ichengzi.filesystem.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/10
 * @time 0:06
 */
public class MapUtil {

    private Map<String,Object> data;

    public MapUtil() {
        this.data = new HashMap<>();
    }

    public MapUtil(Map map) {
        this.data = map;
    }

    public MapUtil  add(String key,Object val){
        data.put(key,val);
        return this;
    }

    public Map build(){
        return this.data;
    }


}
