package com.gradwahl.rs254;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public final class GameCanvas extends Canvas implements Runnable {
    public static final int WIDTH = 765;
    public static final int HEIGHT = 503;

    private final ClientConfig config;
    private volatile boolean running;
    private volatile String status;
    private Thread thread;

    public GameCanvas(ClientConfig config) {
        this.config = config;
        this.status = "Ready. Server " + config.host() + ':' + config.port() + ", revision " + config.revision();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setIgnoreRepaint(true);
        setFocusable(true);
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "game-loop");
        thread.setDaemon(true);
        thread.start();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void run() {
        createBufferStrategy(2);
        long last = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 50.0;
        double accumulator = 0.0;

        while (running) {
            long now = System.nanoTime();
            accumulator += (now - last) / nsPerTick;
            last = now;

            while (accumulator >= 1.0) {
                tick();
                accumulator -= 1.0;
            }

            render();
            Thread.onSpinWait();
        }
    }

    private void tick() {
        // Game-state update hook. The real client decoder/scene renderer will attach here.
    }

    private void render() {
        BufferStrategy strategy = getBufferStrategy();
        Graphics g = strategy.getDrawGraphics();
        try {
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawString("Java 254 Client", 24, 32);
            g.drawString("50 TPS shell running", 24, 54);
            g.drawString(status, 24, 76);
        } finally {
            g.dispose();
        }
        strategy.show();
    }
}
