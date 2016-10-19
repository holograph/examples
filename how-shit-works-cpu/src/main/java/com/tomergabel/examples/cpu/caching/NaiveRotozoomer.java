package com.tomergabel.examples.cpu.caching;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class NaiveRotozoomer extends Frame {
    protected static int TEXTURE_W = 256;                                     // Must be a power of 2
    protected static int TEXTURE_H = 256;                                     // Must be a power of 2
    protected static long FPS_COUNTER_INTERVAL_SECONDS = 1;
    protected static long FPS_COUNTER_INTERVAL_NANOS = FPS_COUNTER_INTERVAL_SECONDS * 1_000_000_000L;

    protected int[] texture;
    private Controller controller;
    private BufferedImage backbuffer;
    private Graphics2D backbufferGraphics;
    private int frameCounter;
    private long lastUpdated = Long.MIN_VALUE;
    private String currentFPS = "FPS: Unknown";

    private class Controller implements MouseMotionListener, MouseWheelListener, KeyListener {
        private double prevX = 0;
        private double prevY = 0;
        private double angle = 0.0;
        private double horizontalOffset = TEXTURE_W / 2.0;
        private double verticalOffset = TEXTURE_H / 2.0;
        private double zoom = 1.0;
        private boolean demoMode = true;

        @Override public void mouseDragged(MouseEvent e) { }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (!demoMode) {
                double dx = e.getX() - prevX;
                double dy = e.getY() - prevY;

                if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                    horizontalOffset += dx;
                    verticalOffset += dy;
                } else {
                    angle += (dx / getWidth() + dy / getHeight()) * Math.PI * 2.0;
                }

                prevX = e.getX();
                prevY = e.getY();
                repaint();
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!demoMode) {
                zoom += e.getPreciseWheelRotation() / 10.0;
                repaint();
            }
        }

        double getAngle() {
            return angle;
        }

        double getHorizontalOffset() {
            return horizontalOffset;
        }

        double getVerticalOffset() {
            return verticalOffset;
        }

        double getZoom() {
            return zoom;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'd':
                    demoMode = !demoMode;
                    break;
            }
            repaint();
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        boolean isDemoModeEnabled() {
            return demoMode;
        }
    }

    private NaiveRotozoomer() {
        this("Rotozoomer");
    }

    protected NaiveRotozoomer(String title) {
        super(title);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setSize(640, 480);
        initializeController();
        initializeTexture();
        setVisible(true);
    }

    private void initializeController() {
        controller = new Controller();
        addMouseMotionListener(controller);
        addMouseWheelListener(controller);
        addKeyListener(controller);
    }

    private void initializeTexture() {
        texture = new int[TEXTURE_W * TEXTURE_H];
        int offset = 0;
        for (int y = 0; y < TEXTURE_H; y++)
            for (int x = 0; x < TEXTURE_W; x++) {
                int intensity = (x ^ y) & 0xff;
                texture[offset] = 0xff000000 + intensity;
                offset++;
            }
    }

    private void reportFPS() {
        Rectangle2D bounds =
                backbufferGraphics.getFontMetrics().getStringBounds("FPS: Unknown", backbufferGraphics);
        int margin = 50;
        int padding = 10;
        backbufferGraphics.clearRect(
                getWidth() - ((int) bounds.getWidth() + padding * 2 + margin),
                margin,
                (int) bounds.getWidth() + padding * 2,
                (int) bounds.getHeight() + padding * 2);
        backbufferGraphics.drawString(
                currentFPS,
                getWidth() - ((int) bounds.getWidth() + padding + margin),
                margin + (int) bounds.getHeight() + padding /* Drawing upwards?! */ );
    }

    @Override
    public void paint(Graphics g) {
        if (backbuffer == null) {
            backbuffer = (BufferedImage) createImage(getWidth(), getHeight());
            backbufferGraphics = backbuffer.createGraphics();
            backbufferGraphics.setBackground(Color.BLACK);
            backbufferGraphics.setColor(Color.YELLOW);
        }

        long now = System.nanoTime();
        if (controller.isDemoModeEnabled())
            renderRotozoomer(
                    ((DataBufferInt) backbuffer.getRaster().getDataBuffer()).getData(),
                    now / 4e9 * 2.0 * Math.PI,
                    1.0,
                    0.0,
                    0.0);
        else
            renderRotozoomer(
                    ((DataBufferInt) backbuffer.getRaster().getDataBuffer()).getData(),
                    controller.getAngle(),
                    controller.getZoom(),
                    controller.getHorizontalOffset(),
                    controller.getVerticalOffset());

        updateFrameCounter(now);

        if (controller.isDemoModeEnabled())
            reportFPS();

        g.drawImage(backbuffer, 0, 0, this);

        if (controller.isDemoModeEnabled())
            repaint();
    }

    private void updateFrameCounter(long now) {
        frameCounter++;
        if (lastUpdated == Long.MIN_VALUE) {
            lastUpdated = now;
            frameCounter = 0;
        } else if (now > lastUpdated + FPS_COUNTER_INTERVAL_NANOS) {
            currentFPS = "FPS: " + (int) (frameCounter / FPS_COUNTER_INTERVAL_SECONDS);
            lastUpdated = now;
            frameCounter = 0;
        }
    }

    protected void renderRotozoomer(
            int[] pixels,
            double angleInRadians,
            double zoomFactor,
            double horizontalOffset,
            double verticalOffset)
    {
        double startu = -getWidth() / 2.0;
        double startv = -getHeight() / 2.0;
        double du1 = startu * Math.cos(angleInRadians) * zoomFactor / getWidth();
        double dv1 = startv * Math.sin(angleInRadians) * zoomFactor / getHeight();
        double du2 = startu * Math.cos(angleInRadians - Math.PI / 2.0) * zoomFactor / getWidth();
        double dv2 = startv * Math.sin(angleInRadians - Math.PI / 2.0) * zoomFactor / getHeight();

        int offset = 0;
        double lu = startu;
        double lv = startv;
        for (int y = 0; y < getHeight(); y++) {
            double u = lu;
            double v = lv;
            for (int x = 0; x < getWidth(); x++) {
                int atu = (int)(u + horizontalOffset) & (TEXTURE_W - 1);
                int atv = (int)(v + verticalOffset) & (TEXTURE_H - 1);
                pixels[offset] = texture[atv * 256 + atu];
                offset++;
                u += du1;
                v += dv1;
            }
            lu += du2;
            lv += dv2;
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public static void main(String[] args) {
        new NaiveRotozoomer();
    }
}
