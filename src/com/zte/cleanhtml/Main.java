package com.zte.cleanhtml;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private JPanel contentPane;
    private JButton btnChooser;
    private JButton btnExecute;
    private JTextArea filename;
    private JTextArea textArea;
    private List<String> list = new ArrayList<>();

    private Main() {
        filename.setEditable(false);
        setContentPane(contentPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        btnChooser.addActionListener((e) -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jfc.showDialog(new JLabel(), "选择");
            File file = jfc.getSelectedFile();
            list.clear();
            if (file != null) {
                if (file.isDirectory()) {
                    list.add("文件夹");
                    list.add(file.getAbsolutePath());
                    filename.setText(list.get(0) + list.get(1));
                } else if (file.isFile()) {
                    list.add("文件");
                    list.add(file.getAbsolutePath());
                    filename.setText(list.get(0) + list.get(1));
                }
            }
        });

        btnExecute.addActionListener((e) -> {
            int size = list.size();
            if (size < 2) {
                filename.setText("请先选择文件或文件夹");
                filename.setForeground(new Color(255, 0, 0));
            } else {
                // TODO 执行扫描操作
                File file = new File(list.get(1));
                Thread t = new Thread(() -> {
                    try {
                        handleFile(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
                t.start();
            }
        });
    }


    // 递归读取路径下的文件
    private void handleFile(File file) throws IOException {
        if (file != null && file.isFile()) {
//            if (file.getName().endsWith(".html") || file.getName().endsWith(".html")) {
                // 文件
                Long fileLength = file.length();
                byte[] fileContent = new byte[fileLength.intValue()];
                FileInputStream inputStream = new FileInputStream(file);
                int read = inputStream.read(fileContent);
                inputStream.close();
                String s = new String(fileContent, "UTF-8");
                textArea.append(file.getAbsoluteFile() + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
//            }
        } else if (file != null && file.isDirectory()) {
            // 路径
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    handleFile(f);
                }
            }
        }
    }


    public static void main(String[] args) {
        Main dialog = new Main();
        dialog.setTitle("清除html文件中的病毒内容");
        dialog.pack();
        dialog.setBounds(10, 10, 800, 600);
        dialog.setVisible(true);
    }

}