package com.tomergabel.examples.cpu.caching;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Created by tomerga on 18/10/2016.
 */
public class NaiveRotozoomer extends Frame {
    private static int TEXTURE_W = 256;
    private static int TEXTURE_H = 256;

    private int[] texture;
    private BufferedImage backbuffer;
    private Graphics offscreen;

    NaiveRotozoomer() {
        super("Rotozoomer");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setSize(640, 480);

        texture = new int[TEXTURE_W * TEXTURE_H];
        int offset = 0;
        for (int y = 0; y < TEXTURE_H; y++)
            for (int x = 0; x < TEXTURE_W; x++) {
                int intensity = (x ^ y) & 0xff;
                texture[offset] = 0xff000000 + (intensity << 16) + (intensity << 8) + intensity;
                offset++;
            }

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        if (backbuffer == null) {
            backbuffer = (BufferedImage) createImage(getWidth(), getHeight());
            offscreen = backbuffer.getGraphics();
            offscreen.setColor(Color.BLACK);
            offscreen.fillRect(0, 0, getWidth(), getHeight());
        }

        final int[] pixels = ((DataBufferInt) backbuffer.getRaster().getDataBuffer()).getData();

        double angle = (360.0 - 25.0) * Math.PI / 180.0;
        double zoom = 1.0;
        double aspect = getWidth() / (double) getHeight();
        double tx = 0.0;
        double ty = 0.0;

        double startu = (tx - getWidth() / 2);
        double startv = (ty - getHeight() / 2);
        double du1 = startu * Math.cos(angle) * zoom / getWidth();
        double dv1 = startv * Math.sin(angle) * zoom / getHeight();
        double du2 = startu * Math.cos(angle - Math.PI / 2.0) * zoom / getWidth();
        double dv2 = startv * Math.sin(angle - Math.PI / 2.0) * zoom / getHeight();

        int offset = 0;
        double lu = startu;
        double lv = startv;
        for (int y = 0; y < getHeight(); y++) {
            double u = lu;
            double v = lv;
            for (int x = 0; x < getWidth(); x++) {
                int atu = ((int) u) & 0xff; // FIX
                int atv = ((int) v) & 0xff;
                pixels[offset] = texture[atv * 256 + atu];
                offset++;
                u += du1;
                v += dv1;
            }
            lu += du2;
            lv += dv2;
        }

        g.drawImage(backbuffer, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public static void main(String[] args) {
        new NaiveRotozoomer();
    }
}
