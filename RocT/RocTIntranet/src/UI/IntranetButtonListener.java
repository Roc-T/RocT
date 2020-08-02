package UI;

import Handler.RocTIntranetHandler;
import TCPconnection.TcpConnection;
import replyon.RocTMessageDecoder;
import replyon.RocTMessageEncoder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class IntranetButtonListener implements ActionListener {
    private JTextField serverPort;
    private JTextField serverAdd;
    private JTextField password;
    private JFrame jFrame;
    private RocTIntranetHandler roctClientHandler;
    /**
     * 添加静态链表，用于存储server端运行结果
     */
    static LinkedList<String> results = new LinkedList<String>();

    public IntranetButtonListener(JTextField serverPort, JTextField serverAdd, JTextField password, JFrame jFrame) {
        this.serverPort = serverPort;
        this.serverAdd = serverAdd;
        this.password = password;
        this.jFrame = jFrame;
    }

    public JFrame getjFrame() {
        return jFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        System.out.println("clicking...");
        try {
            startClient();
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }
    public void startClient() throws InterruptedException, IOException {

        String serverAddress = this.serverAdd.getText();
        if (serverAddress == null) {
            System.out.println("server_addr cannot be null");
            JOptionPane.showMessageDialog(null,"server_addr cannot be null");
            return;
        }
        String serverPort = this.serverPort.getText();
        if (serverPort == null) {
            System.out.println("server_port cannot be null");
            JOptionPane.showMessageDialog(null,"server_port cannot be null");
            return;
        }
        String password = this.password.getText();
        roctClientHandler = new RocTIntranetHandler( password, results, this,new  IntranetProxyListener(),new ConcurrentHashMap<Integer,String>());//this.roctClientHandler.clientProxyListener= new
        connect(serverAddress,serverPort);
    }
    void connect(String serverAddress,String serverPort) throws NumberFormatException, InterruptedException, IOException {
        TcpConnection roctConnection = new TcpConnection();
        ChannelFuture future = roctConnection.connect(serverAddress, Integer.parseInt(serverPort), new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new RocTMessageDecoder(), new RocTMessageEncoder(),
                        new IdleStateHandler(60, 30, 0), roctClientHandler);
            }
        });
        future.addListener(future1 -> new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        connect(serverAddress,serverPort);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }.start());

    }
}
