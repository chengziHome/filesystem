package me.ichengzi.filesystem.util;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.util.Map;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 23:28
 */
public class ReturnUtil {

    private int ret_code;
    private String err_msg;
    private Map data;

    public ReturnUtil(int ret_code, String err_msg, Map data) {
        this.ret_code = ret_code;
        this.err_msg = err_msg;
        this.data = data;
    }

    public int getRet_code() {
        return ret_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public Map getData() {
        return data;
    }

    public static ReturnUtil getResult(int ret_code, String err_msg,Map data){
        return new ReturnUtil(ret_code,err_msg,data);
    }

    public static ReturnUtil success(){
        return new ReturnUtil(ReturnCode.SUCCESS,"",null);
    }

    public static ReturnUtil success(Map data){
        return new ReturnUtil(ReturnCode.SUCCESS,"",data);
    }

    public static ReturnUtil error(String err_msg){
        return new ReturnUtil(ReturnCode.FAILURE,err_msg,null);
    }


}
