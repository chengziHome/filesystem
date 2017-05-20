import me.ichengzi.filesystem.model.impl.DefaultItem;
import me.ichengzi.filesystem.util.Byte2Int;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 20:13
 */
public class Main {

    private static int i = 0;

    public static void main(String[] args) throws IOException {


        System.out.println(Integer.toHexString(0x014E*512+0x4200));
        System.out.println(new Date().getTime()/1000);
        System.out.println(Integer.MAX_VALUE);

        Byte2Int.getTime();
        System.out.println(Integer.parseInt("0100101010110100",2));

    }



}
