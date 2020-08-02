package UI;

import javax.swing.*;
import java.awt.*;

public class IntranetStart {
    private JFrame jf = new JFrame();
    private Box vertical = Box.createVerticalBox();
    private Color white = new Color(246, 243, 238);
    private Color black = new Color(38,38,38);
    private Color yellow = new Color(240, 202, 95);
    private Font wFont = new Font("Droid Sans Mono Slashed",0,16);
    private Font yFont = new Font("Droid Sans Mono Slashed",1,18);

    public void showUI() {
        jf.setSize(350,540);    //窗体大小
        jf.setDefaultCloseOperation(3);    //可以退出
        jf.setLocationRelativeTo(null);    //相对屏幕居中
        jf.setTitle("RocT");              //窗体名字
        jf.setLayout(new FlowLayout());
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/RocTPic5.png"));
        ImageIcon icon = new ImageIcon();
        icon.setImage(image);
        icon.setImage(icon.getImage().getScaledInstance(80, 70, Image.SCALE_DEFAULT));
        JLabel jla = new JLabel(icon);
        jla.setAlignmentX(0.5F);

        JLabel intranetLabel = new JLabel("RocT-Intranet");
        intranetLabel.setFont(yFont);
        intranetLabel.setForeground(yellow);
        intranetLabel.setAlignmentX(0.5F);

        JLabel serverAddLabel = new JLabel("Wan Address：");
        serverAddLabel.setForeground(white);
        serverAddLabel.setFont(wFont);
        serverAddLabel.setAlignmentX(0.5F);
        JTextField serverAddTextField = new JTextField();
        serverAddTextField.setPreferredSize(new Dimension(250,30));

        JLabel serverPortLabel = new JLabel("Wan Port：");
        serverPortLabel.setForeground(white);
        serverPortLabel.setFont(wFont);
        serverPortLabel.setAlignmentX(0.5F);
        JTextField serverPortTextField = new JTextField();
        serverPortTextField.setPreferredSize(new Dimension(250,30));

        JLabel passwordLabel = new JLabel("Password：");
        JPasswordField passwordTextField = new JPasswordField();
        passwordTextField.setPreferredSize(new Dimension(250,30));
        passwordLabel.setForeground(white);
        passwordLabel.setFont(wFont);
        passwordLabel.setAlignmentX(0.5F);

        JPanel jPanel = new JPanel();
        JButton jb = new JButton("START");
        jb.setBackground(yellow);
        jb.setPreferredSize(new Dimension(200, 30));
        IntranetButtonListener clientButtonListener = new IntranetButtonListener(serverPortTextField,serverAddTextField,passwordTextField,jf);
        jb.addActionListener(clientButtonListener);
        jb.setAlignmentX(0.5F);
        jPanel.add(jb);

        vertical.add(Box.createVerticalStrut(60));
        vertical.add(jla);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(intranetLabel);
        vertical.add(Box.createVerticalStrut(40));
        vertical.add(serverAddLabel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(serverAddTextField);
        vertical.add(Box.createVerticalStrut(20));
        vertical.add(serverPortLabel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(serverPortTextField);
        vertical.add(Box.createVerticalStrut(20));
        vertical.add(passwordLabel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(passwordTextField);
        vertical.add(Box.createVerticalStrut(40));
        vertical.add(jb);

        jf.add(vertical, BorderLayout.CENTER);
        jf.setVisible(true);
        jf.getContentPane().setBackground(black);

    }

    public static void main(String args[]){
        IntranetStart clientStart = new IntranetStart();
        clientStart.showUI();
    }
}
