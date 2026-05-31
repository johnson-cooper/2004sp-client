package com.gradwahl.rs254;

import com.gradwahl.rs254.net.LoginClient;
import com.gradwahl.rs254.net.LoginResult;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public final class ClientFrame extends JFrame {
    private final GameCanvas canvas;
    private final ClientConfig config;

    public ClientFrame(ClientConfig config) {
        super("Java 254 Client");
        this.config = config;
        this.canvas = new GameCanvas(config);

        JTextField username = new JTextField("Callum", 12);
        JPasswordField password = new JPasswordField("password", 12);
        JButton login = new JButton("Test Login");
        login.addActionListener(e -> testLogin(username.getText(), new String(password.getPassword())));

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(username);
        bar.add(password);
        bar.add(login);

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);
        pack();
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void start() {
        canvas.start();
    }

    private void testLogin(String username, String password) {
        canvas.setStatus("Connecting to " + config.websocketUri());
        Thread loginThread = new Thread(() -> {
            try {
                LoginResult result = new LoginClient(config).login(username, password, false);
                canvas.setStatus(result.toString());
            } catch (Exception ex) {
                canvas.setStatus("Login failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        }, "login-test");
        loginThread.setDaemon(true);
        loginThread.start();
    }
}
