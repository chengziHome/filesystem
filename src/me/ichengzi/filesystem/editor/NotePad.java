package me.ichengzi.filesystem.editor;

import me.ichengzi.filesystem.model.File;
import me.ichengzi.filesystem.model.impl.DefaultDiskManager;
import me.ichengzi.filesystem.model.impl.DefaultFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class NotePad extends JFrame implements ActionListener{

    /**
     * @param args
     */
    JTextArea jta = null;
    JMenuBar jmb = null;
    JMenu jm1 = null;
    JMenuItem jmi1 = null;
    JMenuItem jmi2 = null;
    File file = null;

    public static void main(String[] args) {

        File file = new DefaultFile();
        file.setContent("init1");

        new NotePad(file);
    }

    public NotePad(File file) {


        //文本编辑区
        jta = new JTextArea();
        //菜单栏
        jmb = new JMenuBar();
        //菜单栏上的第一个菜单
        jm1 = new JMenu("文件(F)");
        //设置助记符,即设置快捷键alt+F
        jm1.setMnemonic('F');
        //两个子菜单
        jmi1 = new JMenuItem("打开（O）");
        jmi1.setMnemonic('O');
        jmi2 = new JMenuItem("保存");

        this.setJMenuBar(jmb);
        //在JMenuBar上添加JMenu
        jmb.add(jm1);
        //在JMenu上添加JMenuItem
        jm1.add(jmi1);
        jm1.add(jmi2);

        //为“打开”和“保存”两个按钮注册监听器
        jmi1.addActionListener(this);
        jmi2.addActionListener(this);

        this.add(jta);
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        this.file = file;
        jta.setText(file.getContent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newContent = jta.getText();

        /*
            这里应该调用一个回调函数，
            暂时不支持多窗口，所以假设在保存之前，
            当前目录是不会改变的
         */

//        System.out.println("NodePad-newContext:"+newContent.trim());


        // TODO: 2017/5/18 得到的内容不一致，可能和文本编辑器的初始化有关系，先空过去。
        DefaultDiskManager.getManager().saveFile(file,newContent);
    }

}