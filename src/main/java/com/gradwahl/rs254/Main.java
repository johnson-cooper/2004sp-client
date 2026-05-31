package com.gradwahl.rs254;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientFrame frame = new ClientFrame(ClientConfig.fromSystemProperties());
            frame.setVisible(true);
            frame.start();
        });
    }
}
