package me.ichengzi.filesystem.util;

import java.io.*;
import java.util.Arrays;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/8
 * @time 21:41
 */
public class CopyToSector {


    public static void main(String[] args) throws IOException {
//        String executeFilePath = args[0];
//        String softFilePath = args[1];


        InputStream in = new FileInputStream("E:/fat/boot.com");
        byte[] bs = new byte[1024];
        int len ;
        while((len=in.read(bs))!=-1){
            System.out.println(Arrays.toString(bs));
        }


        OutputStream out = new FileOutputStream("E:/fat/print.flp");
        out.write(bs);

        in.close();
        out.flush();
        out.close();






    }



}
