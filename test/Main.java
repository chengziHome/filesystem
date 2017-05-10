import java.io.*;
import java.util.Arrays;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 20:13
 */
public class Main {


    public static void main(String[] args) throws IOException {
//        OutputStream out = new FileOutputStream("E:/fat/test.flp");
//        byte[] bs = new byte[1024];
//        for (int i = 0; i < bs.length; i++) {
//            bs[i] = 0;
//        }
//        out.write(bs);
//        out.write("chengzi".getBytes());

//        System.out.println(Byte.MAX_VALUE);
//        System.out.println(0x0100>>6);
//        System.out.println("贾昭鹤".length());
//        System.out.println("贾昭鹤".getBytes().length);

//        File file = new File("E:/fat/format.flp");
//        System.out.println(file.length());
//        System.out.println((Integer.MAX_VALUE/1024)/1024);

        byte[] bs = new byte[]{0,1,2,3,4,5};
        System.out.println(Arrays.toString(Arrays.copyOfRange(bs,0,3)));

        System.out.println(0xfff);

    }



}
