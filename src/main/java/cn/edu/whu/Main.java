package cn.edu.whu;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
                    handleFile(file);
                });
                t.start();
            }
        });
    }


    // 递归读取路径下的文件
    private void handleFile(File file) {
        if (file != null && file.isFile()) {
            if (file.getName().endsWith(".html") || file.getName().endsWith(".htm")) {
                // 文件
                Long fileLength = file.length();
                byte[] fileContent = new byte[fileLength.intValue()];
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                    int read = inputStream.read(fileContent);
                    inputStream.close();
                    String s = null;
                    s = new String(fileContent, "UTF-8");
                    String pattern = "<SCRIPT Language=VBScript><!--";
                    if (s.contains(pattern)) {
                        String destContent = s.substring(0, s.indexOf(pattern));
                        PrintStream printStream = null;
                        try {
                            printStream = new PrintStream(new FileOutputStream(file));
                            printStream.print(destContent);
                        } catch (FileNotFoundException e) {
                            System.out.println("文件写入错误");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("文件处理出错");
                }
                textArea.append(file.getAbsoluteFile() + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
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
