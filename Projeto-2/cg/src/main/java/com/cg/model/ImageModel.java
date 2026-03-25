package com.cg.model;

public class ImageModel {
    private int width;
    private int height;
    private int[][] pixels;

    public ImageModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[height][width];
    }

    public int getWidth() { 
        return width; 
    }

    public int getHeight() { 
        return height; 
    }

    public int[][] getPixels() { 
        return pixels; 
    }

    public void setPixels(int[][] pixels) { 
        this.pixels = pixels; 
    }
}