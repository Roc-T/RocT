package UI;

import Handler.RocTIntranetHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IntranetRun extends JFrame{
    private JPanel topPanel = new JPanel();
    private JPanel processPanel = new JPanel();
    private JPanel contentPane = new JPanel();
    private JPanel resultsPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JTable table;
    private JLabel workResultLabel = new JLabel();
    private JLabel httpsProxyLabel = new JLabel();
    private final Color white = new Color(246, 243, 238);
    private final Color black = new Color(38,38,38);
    private final Color yellow = new Color(240, 202, 95);
    private final Font titleFont = new Font("Droid Sans Mono Slashed",0,30);
    private final Font wFont = new Font("Droid Sans Mono Slashed",0,16);
    private final Font y1Font = new Font("Droid Sans Mono Slashed",1,24);
    private final Font y2Font = new Font("Droid Sans Mono Slashed",1,16);
    private final Border border = BorderFactory.createLineBorder(white, 1);
    private final Border btnBorder = BorderFactory.createLineBorder(yellow, 1);
    static RocTIntranetHandler rocTClientHandler;
    private int clicked = 0;
    /**
     * 添加静态链表，用于存储server端运行结果
     */
    static LinkedList<String> results = new LinkedList<String>();
    static int length = 0;    //用于标识results链表的长度
    static String resultsStr;
    static IntranetProxy clientProxy;
    private String httpsPort = "";
    private int rowSelected = 0;

    public void setHttpsPort(String httpsPort) {
        this.httpsPort = httpsPort;
    }

    public IntranetRun(LinkedList<String> resultsDemo, RocTIntranetHandler rocTClientHandlerDemo) {
        results = resultsDemo;
        rocTClientHandler = rocTClientHandlerDemo;
        clientProxy = new IntranetProxy(results, rocTClientHandler);
    }

    public void showUI() {
        setSize(1200,830);    //窗体大小
        setDefaultCloseOperation(3);    //可以退出
        setLocationRelativeTo(null);    //相对屏幕居中
        setTitle("RocT");              //窗体名字
        setLayout(new BorderLayout());
        setTopPanel();
        setProcessPanel();
        setBottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(processPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        updateLabel();
        updateTable();
        Update();
        clientProxy.showUI();
    }

    private void setTopPanel() {

        Box vertical = Box.createVerticalBox();

        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/RocTPic5.png"));
        ImageIcon icon = new ImageIcon();
        icon.setImage(image);

        icon.setImage(icon.getImage().getScaledInstance(65, 60,Image.SCALE_DEFAULT ));

        JLabel jla = new JLabel(icon);
        jla.setSize(70,70);
        jla.setAlignmentX(0.5F);
        JLabel titleLabel = new JLabel("RocT-Intranet");
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
        leftPanel.setPreferredSize(new Dimension(500, 400));
        //包含resultScrollPane
        setResultsPanel(leftPanel);
        //包含table
        setContentPane();

        horizontal.add(Box.createHorizontalStrut(20));
        horizontal.add(leftPanel);
        horizontal.add(Box.createHorizontalStrut(20));
        horizontal.add(contentPane);
        processPanel.setPreferredSize(new Dimension(1200, 500));
        processPanel.setBackground(black);
        processPanel.setLayout(new FlowLayout());
        processPanel.add(horizontal);
    }

    private void setBottomPanel() {
        //包含https proxy port
        bottomPanel.setBackground(black);
        JLabel titleLabel = new JLabel("HTTPS Proxy Port", JLabel.CENTER);
        titleLabel.setFont(y2Font);
        titleLabel.setForeground(yellow);
        titleLabel.setBackground(black);
        titleLabel.setPreferredSize(new Dimension(250, 40));
        titleLabel.setBorder(border);

        JTextField textField = new JTextField();

        Image image01 = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/edit.png"));
        ImageIcon image = new ImageIcon();
        image.setImage(image01);

        Image image02 = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/ok.png"));
        ImageIcon image2 = new ImageIcon();
        image2.setImage(image02);
        image.setImage(image.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));
        image2.setImage(image2.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));
        JButton button = new JButton(image);
        button.setContentAreaFilled(false);
        button.setBorder(null);
        button.setMargin(new Insets(0, 0, 0, 0));

        button.addActionListener(new IntranetHttpsProxyListener(rocTClientHandler, textField, button));

        textField.setBackground(black);
        textField.setBorder(border);
        textField.setForeground(white);
        textField.setCaretColor(yellow);
        textField.setPreferredSize(new Dimension(250, 40));
        textField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                clicked ++;
                if (clicked == 1) {
                    button.setEnabled(true);
                    button.setIcon(image2);
                    button.setBorder(null);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //Delete button
        JButton deleteButton = new JButton("DELETE");
        deleteButton.setPreferredSize(new Dimension(100, 30));
        deleteButton.setBorder(btnBorder);
        deleteButton.setBackground(black);
        deleteButton.setForeground(yellow);
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                do_deleteButton_actionPerformed(e);
            }
        });
        //Add Button
        JButton addButton = new JButton("ADD");
        addButton.setPreferredSize(new Dimension(100, 30));
        addButton.setBorder(btnBorder);
        addButton.setBackground(black);
        addButton.setForeground(yellow);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                do_addButton_actionPerformed(actionEvent);
            }
        });

        JPanel proxyPanel = new JPanel();
        proxyPanel.setBackground(black);
        BoxLayout boxLayout = new BoxLayout(proxyPanel, BoxLayout.X_AXIS);
        proxyPanel.add(Box.createHorizontalStrut(5));
        proxyPanel.add(titleLabel);
        proxyPanel.add(textField);
        proxyPanel.add(button);
        proxyPanel.add(Box.createHorizontalStrut(200));
        proxyPanel.add(addButton);
        proxyPanel.add(Box.createHorizontalStrut(50));
        proxyPanel.add(deleteButton);

        Box vertical = Box.createVerticalBox();
        vertical.add(proxyPanel);
        vertical.add(Box.createVerticalStrut(30));
        bottomPanel.add(vertical);
//        bottomPanel.add(Box.createVerticalStrut(30));
    }

    private void setResultsPanel(JPanel panel) {
        JLabel workLabel = new JLabel("The RocT-Intranet is working", JLabel.CENTER);
        workLabel.setFont(y2Font);
        workLabel.setForeground(yellow);
        workLabel.setBorder(border);


        workResultLabel.setHorizontalAlignment(JLabel.CENTER);
        workResultLabel.setForeground(white);
        workResultLabel.setFont(wFont);

        resultsPanel.add(workResultLabel);
        resultsPanel.setPreferredSize(new Dimension(300,400));
        resultsPanel.setBackground(black);
        JScrollPane resultScrollPane = new JScrollPane(resultsPanel);
        resultScrollPane.setVerticalScrollBarPolicy(20);
        resultScrollPane.setBackground(black);
        resultScrollPane.setBorder(border);

        panel.setBackground(black);
        panel.add(workLabel, BorderLayout.NORTH);
        panel.add(resultScrollPane, BorderLayout.CENTER);
        panel.setBorder(border);
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
        table.setPreferredSize(new Dimension(500, contentPane.getHeight()));
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        r.setBackground(black);
        table.setDefaultRenderer(Object.class, r);

        table.setForeground(white);
        table.setBackground(black);
        table.setSelectionBackground(white);
        table.setGridColor(white);
        table.setShowGrid(true);
        table.setBorder(tabelBorder);
        scrollPane.setViewportView(table);
        scrollPane.setBackground(black);
        scrollPane.setVerticalScrollBarPolicy(20);
        scrollPane.setPreferredSize(new Dimension(500, 440));
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.setPreferredSize(new Dimension(500,440));
        contentPane.setBackground(black);
    }

    protected void do_deleteButton_actionPerformed(ActionEvent e)
    {
        DefaultTableModel model=(DefaultTableModel) table.getModel();    //获得表格模型

        Integer port;
        try {
            port = Integer.valueOf((String) model.getValueAt(rowSelected, 0));
            model.removeRow(rowSelected);
        }catch(Exception e1) {
            JOptionPane.showMessageDialog(null,"😂Please choose which to delete");
            return;
        }
        rocTClientHandler.portDisconnected(port);
        table.setModel(model);
        updateTable();
    }

    protected void do_addButton_actionPerformed(ActionEvent e)
    {
        clientProxy.getJFrame().show();
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
                updateLabel();
                updateTable();

            }
        });
        timeAction.start();
    }

    protected void updateLabel()
    {
        while (results.size() != length) {
            length = results.size();
            resultsStr = "<html><body>";
            for (String item: results) {
                resultsStr += "        " + item + "<br>";
            }
            resultsStr += "<br>";

            workResultLabel.setText(resultsStr);
        }
        int h = getLabelHeight(workResultLabel);
        resultsPanel.setPreferredSize(new Dimension(350,h));

        httpsProxyLabel.setText("    Https Proxy Port: " + httpsPort);


    }
    public void updateTable(){

        DefaultTableModel tableModel=(DefaultTableModel) table.getModel();//获得表格模型
        rowSelected = table.getSelectedRow();
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"Remote Port","Proxy Address","Proxy Port"});    //设置表头
        ConcurrentHashMap<Integer,String> remoteToProxy = rocTClientHandler.remoteToProxy;
        Set<Integer> remotePortSet = remoteToProxy.keySet();
        Iterator iterator = remotePortSet.iterator();
        while (iterator.hasNext()) {
            Integer remotePort = (Integer)iterator.next();
            String proxy = remoteToProxy.get(remotePort);
            String proxyAddress = proxy.split(":")[0];
            String proxyPort = proxy.split(":")[1];
            tableModel.addRow(new Object[]{String.valueOf(remotePort), proxyAddress, proxyPort});
        }

        //table.setRowHeight(30);
        table.setModel(tableModel);    //应用表格模型
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, r);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel model = table.getSelectionModel();
        model.setSelectionInterval(rowSelected,rowSelected);

    }

}

