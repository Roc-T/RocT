package UI;

import javax.swing.*;

import java.awt.*;

public class WanStart extends JFrame{

    private JFrame jf = new JFrame();
    private Box horizontalIcon = Box.createHorizontalBox();
    private Box horizontalBtn = Box.createHorizontalBox();
    private Box vertical = Box.createVerticalBox();
    private final Color white = new Color(246, 243, 238);
    private final Color black = new Color(38,38,38);
    private final Color yellow = new Color(240, 202, 95);
    private final Dimension textSize = new Dimension(250,30);
    private final Font wFont = new Font("Droid Sans Mono Slashed",0,16);

    public void showUI() {
        jf.setSize(310,490);    //窗体大小
        jf.setDefaultCloseOperation(3);    //可以退出
        jf.setLocationRelativeTo(null);    //相对屏幕居中
        jf.setTitle("RocT");              //窗体名字
        jf.setLayout(new FlowLayout());

        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/RocTPic5.png"));
        ImageIcon icon = new ImageIcon();
        icon.setImage(image);
        icon.setImage(icon.getImage().getScaledInstance(110, 100,
                Image.SCALE_DEFAULT));

        JLabel jla = new JLabel(icon);
        jla.setAlignmentX(0.5F);
        JLabel serverPortLabel = new JLabel("Server Port");
        serverPortLabel.setForeground(white);
        serverPortLabel.setFont(wFont);
        serverPortLabel.setAlignmentX(0.5F);
        JTextField serverPortTextField = new JTextField();
        serverPortTextField.setPreferredSize(textSize);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(white);
        passwordLabel.setFont(wFont);
        passwordLabel.setAlignmentX(0.5F);
        JPasswordField passwordTextField = new JPasswordField();
        passwordTextField.setPreferredSize(textSize);

        JButton jb = new JButton();
        jb.setText("Start");
        jb.setPreferredSize(textSize);
        WanButtonListener serverButtonListener = new WanButtonListener(serverPortTextField,passwordTextField,jf);
        jb.addActionListener(serverButtonListener);
        jb.setAlignmentX(0.5F);
        jb.setBackground(yellow);

        vertical.add(Box.createVerticalStrut(60));
        vertical.add(jla);
        vertical.add(Box.createVerticalStrut(60));
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
        WanStart wanStart = new WanStart();
        wanStart.showUI();
    }
}

