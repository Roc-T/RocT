package UI;

import Handler.RocTIntranetHandler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

class IntranetHttpsProxyListener implements ActionListener {
    static RocTIntranetHandler rocTClientHandler;
    private JTextField portTextField;
    private JButton button;



    private ImageIcon image = new ImageIcon();

    public IntranetHttpsProxyListener(RocTIntranetHandler rocTClientHandlerDemo, JTextField portTextField, JButton button) {
        Image image0 = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pic/ok.png"));
        image.setImage(image0);
        rocTClientHandler = rocTClientHandlerDemo;
        this.portTextField = portTextField;
        this.button = button;
        image.setImage(image.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int port = Integer.valueOf(portTextField.getText());
        portTextField.setEditable(false);
        rocTClientHandler.setHttpsProxy(port);
        button.setIcon(image);
        button.setEnabled(false);
    }
}
