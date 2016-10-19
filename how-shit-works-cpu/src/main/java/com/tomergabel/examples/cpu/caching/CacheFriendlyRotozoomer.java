package com.tomergabel.examples.cpu.caching;

public class CacheFriendlyRotozoomer extends NaiveRotozoomer {

    private CacheFriendlyRotozoomer() {
        super("Cache-friendly Rotozoomer");
    }

    public static void main(String[] args) {
        new CacheFriendlyRotozoomer();
    }
}
