package me.ichengzi.filesystem;

import me.ichengzi.filesystem.controller.Controller;
import me.ichengzi.filesystem.model.Item;
import me.ichengzi.filesystem.util.ReturnUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Coding is pretty charming when you love it!
 *
 * @author Chengzi Start
 * @date 2017/5/9
 * @time 15:23
 */
public class Main {

    private static final Controller controller;

    static {
        controller = new Controller();
    }


    public static void main(String[] args) throws IOException {

        controller.init();
        System.out.println("请输入命令");
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String cmdStr = scanner.nextLine();
            if ("".equals(cmdStr)){
                continue;
            }
            String[] cmds = cmdStr.trim().split("\\s+");
            String operation = cmds[0];

            switch (operation){
                //暂时隐藏掉这个命令
                case "init":

                    if (controller.hasInitialized()){
                        error("The disk has been initialized already!!!");
                    }else{
                        controller.init();
                        success("init");
                    }
                    break;
                case "format":
                    if (!controller.hasInitialized()){
                        initError();
                    }else if (cmds.length!=1){
                        parameterError();
                    }else{
                        controller.format();
                        success("format!");
                    }
                    currentPath();
                    break;
                case "touch":
                    if (!controller.hasInitialized()){
                        initError();
                    }else if(cmds.length!=2){
                        parameterError();
                    } else{
                        String filename = cmds[1];
                        ReturnUtil result = controller.touch(filename);
                        if (result.getRet_code()!=0)
                            error(result.getErr_msg());
                    }
                    currentPath();
                    break;
                case "mkdir":
                    if (!controller.hasInitialized()){
                        initError();
                    }else if(cmds.length!=2){
                        parameterError();
                    }else{
                        String dirName = cmds[1];
                        ReturnUtil result = controller.mkdir(dirName);
                        if (result.getRet_code()!=0)
                            error(result.getErr_msg());
                    }
                    currentPath();
                    break;
                case "rm":
                    if (!controller.hasInitialized()){
                        initError();
                    }else if(cmds.length!=2){
                        parameterError();
                    }else{
                        String name = cmds[1];
                        ReturnUtil result = controller.remove(name);
                        if (result.getRet_code()!=0)
                            error(result.getErr_msg());
                    }
                    currentPath();
                    break;

                case "edit":
                    if (!controller.hasInitialized()){
                        initError();
                    }else if(cmds.length!=2){
                        parameterError();
                    }else{
                        String fileName = cmds[1];
                        ReturnUtil result = controller.edit(fileName);
                        if (result.getRet_code()!=0)
                            error(result.getErr_msg());
                    }
                    currentPath();
                    break;
                case "ls":
                    if (!controller.hasInitialized()){
                        initError();
                    }else if(cmds.length!=1){
                        parameterError();
                    }else{
                        // TODO: 2017/5/20 List的命令还有待加强
                        ReturnUtil result = controller.list();
                        List<Item> dirItems = (List<Item>) result.getData().get("dirItems");
                        List<Item> fileItems = (List<Item>) result.getData().get("fileItems");
                        printDirList(dirItems);
                        printFileList(fileItems);
                    }
                    currentPath();
                    break;
                case "cd":
                    if (!controller.hasInitialized()){
                        initError();
                    }else if(cmds.length!=2){
                        parameterError();
                    }else{
                        String destPath = cmds[1];
                        ReturnUtil result = controller.cd(destPath);
                        if (result.getRet_code()!=0)
                            error(result.getErr_msg());
                    }
                    currentPath();
                    break;
                case "help":
                    printHelpMessage();
                    currentPath();
                    break;
                case "flush":
                    controller.saveAll();
                    currentPath();
                    break;
                case "exit":
                    controller.saveAll();
                    System.out.println("Bye!");
                    return ;
                default:
                    System.out.println("Incorrect input! Type help for more detailed information.");
                    currentPath();
                    break;

                /*
                    测试工具
                 */
                case "pwd":
                    if (!controller.hasInitialized()){
                        initError();
                    }else{
//                        controller.getManager().getData().printTable();
                        System.out.println("currentPath:"+controller.getManager().getCurrentPath());
                        System.out.println("currentDictionary:"+controller.getManager().getCurrentDictionary());
                    }
                    break;
                case "pb":
                    if (!controller.hasInitialized()){
                        initError();
                    }else{
                        int start = Integer.parseInt(cmds[1],16);
                        int end = Integer.parseInt(cmds[2],16);
                        byte[] bs = controller.getManager().getDisk().getBytes();

                        for (int i = start; i <=end; i++) {
                            System.out.printf(bs[i]+",");
                        }

                    }
                    break;
            }

        }

    }

    private static void currentPath(){
        String currentPath = controller.getManager().getCurrentPath();
        System.out.printf(currentPath.substring(0,currentPath.length()-1)+">");
    }

    private static void success(String opteration){
        System.out.println(opteration+" successfully!");
    }

    private static void error(String err_msg){
        System.out.println("Error: "+err_msg);
    }

    private static void initError(){
        error("Init process should be invoked firstly!!!");
    }

    private static void parameterError(){
        error("Unrecognized parameter! Type help for detailed information.");
    }

    private static void printHelpMessage(){
        System.out.println("usage:  operation [one or more options]");
        System.out.println("operations include:");

//        System.out.println("     init:               Initialize the disk.(You need to do this at the very beginning!");
        System.out.println("     format:             Format the disk.");
        System.out.println("     touch filename:     Create a new file with specified file name at current dictionary!");
        System.out.println("     mkdir dirname:      Create a new dictionary with specified name at current dictionary!");
        System.out.println("     rm file/dir:        Remove the specified file/dictionary.");
        System.out.println("     ls:                 List all files and subDir at current dictionary!");
        System.out.println("     cd dir|.|..:        Enter or exit the dictionary!");
        System.out.println("     edit filename:      Edit the file");
        System.out.println("     exit:               Exit the File System");
        System.out.println("     flush:              Save all modification into the Disk");
        System.out.println("     help:               Print this help massage!");
        System.out.println("   Welcome to https://github.com/chengziHome/filesystem");
        System.out.println("help end.");



    }


    private static void printDirList(List<Item> items){
        if (!items.isEmpty()){
            for (Item item:items){
                System.out.printf("%-12s%-12s<DIR>%-12s  %-30s\n",item.getFormatDate(),item.getFormatTime(),"",item.getDir_Name());
            }
        }
    }

    private static void printFileList(List<Item> items){
        if (!items.isEmpty()){
            for (Item item:items){
                System.out.printf("%-12s%-12s%17s  %-30s\n",item.getFormatDate(),item.getFormatTime(),String.valueOf(item.getDir_FileSize()),item.getDir_Name());
            }
        }
    }




}
