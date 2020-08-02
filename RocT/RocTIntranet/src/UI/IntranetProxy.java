package UI;

import Handler.RocTIntranetHandler;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class IntranetProxy {
    private JFrame jf = new JFrame();
    /**
     * 添加静态链表，用于存储server端运行结果
     */
    private Box vertical = Box.createVerticalBox();
    static LinkedList<String> results;
    static RocTIntranetHandler rocTClientHandler;
    static IntranetProxyListener clientProxyListener;
    private Color white = new Color(246, 243, 238);
    private Color black = new Color(38,38,38);
    private Color yellow = new Color(240, 202, 95);
    private Font wFont = new Font("Droid Sans Mono Slashed",0,16);
    private Font yFont = new Font("Droid Sans Mono Slashed",1,18);

    public IntranetProxy(LinkedList<String> resultsDemo, RocTIntranetHandler rocTClientHandlerDemo) {
        results = resultsDemo;
        rocTClientHandler = rocTClientHandlerDemo;
        clientProxyListener = rocTClientHandler.clientProxyListener;
    }
    public JFrame getJFrame() {
        return jf;
    }

    public void showUI() {
        jf.setSize(350,540);    //窗体大小
        jf.setDefaultCloseOperation(2);
        jf.setLocationRelativeTo(null);    //相对屏幕居中
        jf.setTitle("RocT");              //窗体名字
        jf.setLayout(new FlowLayout());
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/RocTPic5.png"));
        ImageIcon icon = new ImageIcon();
        icon.setImage(image);
        icon.setImage(icon.getImage().getScaledInstance(80, 70,
                Image.SCALE_DEFAULT));
        JLabel jla = new JLabel(icon);
        jla.setAlignmentX(0.5F);

        JLabel intranetLabel = new JLabel("RocT-Proxy");
        intranetLabel.setFont(yFont);
        intranetLabel.setForeground(yellow);
        intranetLabel.setAlignmentX(0.5F);

        JLabel proxyAddLabel = new JLabel("Proxy Address：");
        proxyAddLabel.setForeground(white);
        proxyAddLabel.setFont(wFont);
        proxyAddLabel.setAlignmentX(0.5F);
        JTextField proxyAddTextField = new JTextField();
        proxyAddTextField.setPreferredSize(new Dimension(250,30));

        JLabel proxyPortLabel = new JLabel("Proxy Port：");
        proxyPortLabel.setForeground(white);
        proxyPortLabel.setFont(wFont);
        proxyPortLabel.setAlignmentX(0.5F);
        JTextField proxyPortTextField = new JTextField();
        proxyPortTextField.setPreferredSize(new Dimension(250,30));

        JLabel remotePortLabel = new JLabel("Remote Port：");
        remotePortLabel.setForeground(white);
        remotePortLabel.setFont(wFont);
        remotePortLabel.setAlignmentX(0.5F);
        JTextField remotePortTextField = new JTextField();
        remotePortTextField.setPreferredSize(new Dimension(250,30));

        JButton jb = new JButton("BIND");
        clientProxyListener.setting(proxyPortTextField, proxyAddTextField, remotePortTextField, jf,rocTClientHandler,results);
        jb.addActionListener(clientProxyListener);
        jb.setBackground(yellow);
        jb.setPreferredSize(new Dimension(250, 30));
        jb.setAlignmentX(0.5F);

        vertical.add(Box.createVerticalStrut(60));
        vertical.add(jla);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(intranetLabel);
        vertical.add(Box.createVerticalStrut(40));
        vertical.add(proxyAddLabel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(proxyAddTextField);
        vertical.add(Box.createVerticalStrut(20));
        vertical.add(proxyPortLabel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(proxyPortTextField);
        vertical.add(Box.createVerticalStrut(20));
        vertical.add(remotePortLabel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(remotePortTextField);
        vertical.add(Box.createVerticalStrut(40));
        vertical.add(jb);

        jf.add(vertical, BorderLayout.CENTER);
        jf.setVisible(true);
        jf.getContentPane().setBackground(black);

    }
}


