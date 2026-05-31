package com.gradwahl.rs254;

public record ClientConfig(String host, int port, boolean secure, int revision) {
    public static ClientConfig fromSystemProperties() {
        String host = System.getProperty("rs254.host", "localhost");
        int port = Integer.getInteger("rs254.port", 43594);
        boolean secure = Boolean.getBoolean("rs254.secure");
        int revision = Integer.getInteger("rs254.revision", 254);
        return new ClientConfig(host, port, secure, revision);
    }

    public String httpBaseUri() {
        return (secure ? "https" : "http") + "://" + host + ":" + port;
    }

    public String websocketUri() {
        return (secure ? "wss" : "ws") + "://" + host + ":" + port + "/";
    }
}
