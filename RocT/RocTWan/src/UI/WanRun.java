package UI;

import Handler.RocTWanHandler;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WanRun extends JFrame {
    private JPanel topPanel = new JPanel();
    private JPanel processPanel = new JPanel();
    private JPanel contentPane = new JPanel();
    static int length = 0;    //用于标识results链表的长度
    static String resultsStr;
    static String httpsPortStr;
    static RocTWanHandler rocTServerHandler;
    private JTable table;
    private JLabel workResultLabel = new JLabel();
    private JPanel resultsPanel = new JPanel();
    private JPanel portsPanel = new JPanel();
    private JLabel httpsProxyLabel = new JLabel();
    private final Color white = new Color(246, 243, 238);
    private final Color black = new Color(38,38,38);
    private final Color yellow = new Color(240, 202, 95);
    private final Font titleFont = new Font("Droid Sans Mono Slashed",0,30);
    private final Font wFont = new Font("Droid Sans Mono Slashed",0,16);
    private final Font y1Font = new Font("Droid Sans Mono Slashed",1,20);
    private final Font y2Font = new Font("Droid Sans Mono Slashed",1,16);
    private final Border border = BorderFactory.createLineBorder(white, 1);
    /**
     * 添加静态链表，用于存储server端运行结果
     */
    static LinkedList<String> results;

    public WanRun(LinkedList<String> resultsDemo) {
        results = resultsDemo;
    }

    public void showUI() {
        setSize(1200,800);    //窗体大小
        setDefaultCloseOperation(3);    //可以退出
        setLocationRelativeTo(null);    //相对屏幕居中
        setTitle("RocT");              //窗体名字
        setLayout(new BorderLayout());

        setTopPanel();
        setProcessPanel();

        add(topPanel, BorderLayout.NORTH);
        add(processPanel, BorderLayout.CENTER);
        setVisible(true);
        do_this_windowActivated();
        Update();
    }

    private void setTopPanel() {
        Image image1 = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/RocTPic5.png"));
        ImageIcon image = new ImageIcon();
        image.setImage(image1);
        Box vertical = Box.createVerticalBox();
        image.setImage(image.getImage().getScaledInstance(65, 60,Image.SCALE_DEFAULT ));
        JLabel jla = new JLabel(image);
        jla.setSize(70,70);
        jla.setAlignmentX(0.5F);

        JLabel titleLabel = new JLabel("RocT-WAN");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(yellow);
        titleLabel.setAlignmentX(0.5F);

        JLabel infoLabel = new JLabel("Connect with Intranet easily");
        infoLabel.setFont(wFont);
        infoLabel.setAlignmentX(0.5F);
        infoLabel.setForeground(white);

        vertical.add(Box.createVerticalStrut(20));
        vertical.add(jla);
        vertical.add(Box.createVerticalStrut(15));
        vertical.add(titleLabel);
        vertical.add(Box.createVerticalStrut(10));
        vertical.add(infoLabel);
        vertical.add(Box.createVerticalStrut(20));

        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER,700,30));
        topPanel.add(vertical);
        topPanel.setBackground(black);
        topPanel.setSize(new Dimension(1200,300));
    }

    private void setProcessPanel() {
        //processPanel
        Box horizontal = Box.createHorizontalBox();
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel proxyPanel = new JPanel();

        setLeftPanel(leftPanel);
        setProxyPanel(proxyPanel);
        setContentPane();

        horizontal.add(Box.createHorizontalStrut(10));
        horizontal.add(leftPanel);
        horizontal.add(Box.createHorizontalStrut(15));
        horizontal.add(proxyPanel);
        horizontal.add(Box.createHorizontalStrut(15));
        horizontal.add(contentPane);

        processPanel.setPreferredSize(new Dimension(1200, 500));
        processPanel.setBackground(black);
        processPanel.setLayout(new FlowLayout());
        processPanel.add(horizontal);
    }

    private void setLeftPanel(JPanel leftPanel) {

        leftPanel.setPreferredSize(new Dimension(400, 500));

        JLabel workLabel = new JLabel("The Server is working", JLabel.CENTER);
        workLabel.setFont(new Font("Droid Sans Mono Slashed",1,16));
        workLabel.setForeground(yellow);
        workLabel.setBorder(border);
        workLabel.setAlignmentX(0.5F);

        workResultLabel.setForeground(white);
        workResultLabel.setFont(new Font("Droid Sans Mono Slashed",0,16));
        workResultLabel.setAlignmentX(0.5F);

        resultsPanel.add(workResultLabel);
        resultsPanel.setPreferredSize(new Dimension(300,400));
        resultsPanel.setBackground(black);

        JScrollPane resultScrollPane = new JScrollPane(resultsPanel);
        resultScrollPane.setVerticalScrollBarPolicy(20);
        resultScrollPane.setBackground(black);
        resultScrollPane.setBorder(border);

        leftPanel.add(workLabel, BorderLayout.NORTH);
        leftPanel.add(resultScrollPane, BorderLayout.CENTER);
        leftPanel.setBackground(black);
        leftPanel.setBorder(border);
    }

    private void setProxyPanel(JPanel proxyPanel) {

        proxyPanel.setPreferredSize(new Dimension(230, 500));

        JLabel proxyTitleLabel = new JLabel("HTTPS Proxy Ports", JLabel.CENTER);
        proxyTitleLabel.setFont(y2Font);
        proxyTitleLabel.setForeground(yellow);
        proxyTitleLabel.setBorder(border);

        httpsProxyLabel.setFont(wFont);
        httpsProxyLabel.setForeground(white);
        httpsProxyLabel.setVerticalAlignment(JLabel.TOP);
        httpsProxyLabel.setHorizontalAlignment(JLabel.CENTER);
        httpsProxyLabel.setPreferredSize(new Dimension(200, 400));

        portsPanel.add(httpsProxyLabel);
        portsPanel.setPreferredSize(new Dimension(200, 400));
        portsPanel.setBackground(black);

        JScrollPane portScrollPane = new JScrollPane(portsPanel);
        portScrollPane.setVerticalScrollBarPolicy(20);
        portScrollPane.setBackground(black);
        portScrollPane.setBorder(border);

        proxyPanel.setBackground(black);
        proxyPanel.setBorder(border);
        proxyPanel.setLayout(new BorderLayout());
        proxyPanel.add(proxyTitleLabel, BorderLayout.NORTH);
        proxyPanel.add(portScrollPane, BorderLayout.CENTER);
    }

    private void setContentPane() {

        Border tabelBorder = BorderFactory.createLineBorder(white, 2);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        table = new JTable();
        table.getTableHeader().setFont(y2Font);
        table.getTableHeader().setForeground(yellow);
        table.getTableHeader().setBackground(black);
        table.getTableHeader().setBorder(tabelBorder);

        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setPreferredSize(new Dimension(450, contentPane.getHeight()));
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        r.setBackground(black);
        r.setBorder(border);
        table.setDefaultRenderer(Object.class, r);

        table.setForeground(white);
        table.setBackground(black);
        table.setSelectionBackground(white);
        table.setEnabled(false);
        table.setGridColor(white);
        table.setShowGrid(true);
        table.setBorder(tabelBorder);
        scrollPane.setViewportView(table);
        scrollPane.setBackground(black);
        scrollPane.setVerticalScrollBarPolicy(20);
        scrollPane.setPreferredSize(new Dimension(450, 400));

        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.setPreferredSize(new Dimension(450, 400));
        contentPane.setBackground(black);
    }

    protected void do_this_windowActivated()
    {
        //table
        DefaultTableModel tableModel=(DefaultTableModel) table.getModel();    //获得表格模型
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{" Remote Port"," Proxy Address"," Proxy Port"});    //设置表头
        ConcurrentHashMap<Integer,String> remoteToProxy = RocTWanHandler.remoteToProxy;
        Set<Integer> remotePortSet = remoteToProxy.keySet();
        Iterator iterator = remotePortSet.iterator();

        while (iterator.hasNext()) {
            Integer remotePort = (Integer)iterator.next();
            String proxy = remoteToProxy.get(remotePort);
            String proxyAddress = proxy.split(":")[0];
            String proxyPort = proxy.split(":")[1];
            tableModel.addRow(new Object[]{String.valueOf(remotePort), proxyAddress, proxyPort});
        }

        table.setModel(tableModel);    //应用表格模型

        //results
        while (results.size() != length) {
            length = results.size();
            resultsStr = "<html><body>";
            for (String item: results) {
                resultsStr +=  item + "<br>";
            }
            resultsStr += "<br>";
            workResultLabel.setText(resultsStr);
        }
        int h = getLabelHeight(workResultLabel);
        resultsPanel.setPreferredSize(new Dimension(300,h));

        //proxy ports
        httpsPortStr = "<html><body>";
        for (int item: RocTWanHandler.httpsPortList) {
            httpsPortStr += String.valueOf(item) + "<br>";
        }
        httpsPortStr += "<br>";
        httpsProxyLabel.setText(httpsPortStr);
        int h2 = getLabelHeight(httpsProxyLabel);
        portsPanel.setPreferredSize(new Dimension(200,h2));
    }

    public int getLabelHeight(JLabel lbl) {
        int maxWidth = 350;
        javax.swing.text.View v = javax.swing.plaf.basic.BasicHTML.createHTMLView(lbl, lbl.getText());
        v.setSize(maxWidth, Integer.MAX_VALUE);
        int h = (int) v.getMinimumSpan(View.Y_AXIS); //这是取得的高度
        return h;
    }

    private void Update(){

        Timer timeAction = new Timer(50, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                do_this_windowActivated();
            }
        });
        timeAction.start();
    }
}


