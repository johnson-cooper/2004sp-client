package com.gradwahl.rs254.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public final class GameConnection implements WebSocket.Listener, AutoCloseable {
    private final Object lock = new Object();
    private final Queue<Integer> bytes = new ArrayDeque<>();
    private final ByteArrayOutputStream partial = new ByteArrayOutputStream();
    private WebSocket socket;
    private boolean closed;

    public static GameConnection open(String uri) throws Exception {
        GameConnection connection = new GameConnection();
        connection.socket = HttpClient.newHttpClient()
            .newWebSocketBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .buildAsync(URI.create(uri), connection)
            .get(20, TimeUnit.SECONDS);
        return connection;
    }

    public void send(byte[] data) {
        socket.sendBinary(ByteBuffer.wrap(data), true).join();
    }

    public int read() throws InterruptedException, IOException {
        synchronized (lock) {
            long deadline = System.currentTimeMillis() + 20_000L;
            while (bytes.isEmpty() && !closed) {
                long wait = deadline - System.currentTimeMillis();
                if (wait <= 0) throw new IOException("Timed out waiting for server data");
                lock.wait(wait);
            }
            if (bytes.isEmpty()) throw new IOException("Connection closed");
            return bytes.remove();
        }
    }

    public byte[] readBytes(int len) throws InterruptedException, IOException {
        byte[] out = new byte[len];
        for (int i = 0; i < len; i++) out[i] = (byte) read();
        return out;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        byte[] chunk = new byte[data.remaining()];
        data.get(chunk);
        synchronized (lock) {
            if (last && partial.size() == 0) {
                for (byte b : chunk) bytes.add(b & 0xff);
            } else {
                partial.writeBytes(chunk);
                if (last) {
                    byte[] full = partial.toByteArray();
                    partial.reset();
                    for (byte b : full) bytes.add(b & 0xff);
                }
            }
            lock.notifyAll();
        }
        webSocket.request(1);
        return null;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        synchronized (lock) {
            closed = true;
            lock.notifyAll();
        }
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        synchronized (lock) {
            closed = true;
            lock.notifyAll();
        }
    }

    @Override
    public void close() {
        closed = true;
        if (socket != null) socket.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
    }
}
