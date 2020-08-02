package UI;

import Handler.RocTWanHandler;
import TcpServer.TcpServer;
import relyOn.RocTMessageDecoder;
import relyOn.RocTMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class WanButtonListener implements ActionListener {
    private JTextField wanPort;
    private JTextField password;
    private JFrame jFrame;
    private int num;
    /**
     * 添加静态链表，用于存储server端运行结果
     */
    static LinkedList<String> results = new LinkedList<String>();
    static LinkedList<RocTWanHandler> HandlerList = new LinkedList<>();

    public WanButtonListener(JTextField serverPort, JTextField password, JFrame jFrame) {
        this.wanPort = serverPort;
        this.password = password;
        this.jFrame = jFrame;
        this.num = 0;
    }

    public void setServerPort(JTextField serverPort) {
        this.wanPort = serverPort;
    }

    public void setPassword(JTextField password) {
        this.password = password;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        System.out.println("clicking...");
        validate();
        try {
            startServer();
            jFrame.dispose();
            new WanRun(results).showUI();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 验证是否格式输入正确*/
    private void validate() {
        if (wanPort.getText().equals("")) {
            System.out.println("未输入端口号，默认为7731");
            JOptionPane.showMessageDialog(null,"未输入端口号，默认为7731");
            setServerPort(new JTextField("7731"));
        }
    }

    public void startServer() throws InterruptedException {
        int port = Integer.parseInt(this.wanPort.getText());
        String password = this.password.getText();
        TcpServer server = new TcpServer();
        server.bind(port, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch)
                    throws Exception {
                RocTWanHandler roctServerHandler = new RocTWanHandler(password,results,num,HandlerList);

                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new RocTMessageDecoder(), new RocTMessageEncoder(),
                        new IdleStateHandler(60, 30, 0), roctServerHandler);
                HandlerList.add(roctServerHandler);
                num += 1;
            }
        });

        JOptionPane.showMessageDialog(null,"😄RocT server started on port " + port);
        results.add("😄RocT server started on port " + port);
    }
}
