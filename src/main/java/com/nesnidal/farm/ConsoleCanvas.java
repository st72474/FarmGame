package com.nesnidal.farm;

public class ConsoleCanvas {
    int width, height;
    char[][] canvas;

    public ConsoleCanvas(int width, int height) {
        this.width = width;
        this.height = height;

        this.canvas = new char[width][height];
        clearCanvas();
    }

    public void drawPixel(char c, int x, int y, boolean redraw) {
        if (redraw || this.canvas[x][y] == ' ') this.canvas[x][y] = c;
    }

    public void drawHorizontalLine(char c, int y, boolean redraw) {
        if (y < this.height) {
            for (int x = 0; x < this.width; x++) {
                drawPixel(c, x, y, redraw);
            }
        }
    }

    public void drawHorizontalLine(char c, int x1, int x2, int y, boolean redraw) {
        if (y < this.height && x1 < this.width && x1 <= x2) {
            for (int x = x1; x < Math.min(this.width, x2 + 1); x++) {
                drawPixel(c, x, y, redraw);
            }
        }
    }

    public void drawVerticalLine(char c, int x, boolean redraw) {
        if (x < this.width) {
            for (int y = 0; y < this.height; y++) {
                drawPixel(c, x, y, redraw);
            }
        }
    }

    public void drawVerticalLine(char c, int x, int y1, int y2, boolean redraw) {
        if (x < this.width && y1 < this.height && y1 <= y2) {
            for (int y = y1; y < Math.min(this.height, y2 + 1); y++) {
                drawPixel(c, x, y, redraw);
            }
        }
    }

    public void drawString(String str, int x, int y, boolean redraw) {
        if (x < this.width && y < this.height) {
            for (int strX = 0; strX < str.length(); strX++) {
                if (x + strX < this.width && (redraw || this.canvas[x + strX][y] == ' ')) this.canvas[x + strX][y] = str.charAt(strX);
            }
        }
    }

    public void drawStringCentre(String str, int y, boolean redraw) {
        int x = (this.width - str.length()) / 2;
        if (x < this.width && y < this.height) {
            for (int strX = 0; strX < str.length(); strX++) {
                if (x + strX < this.width && (redraw || this.canvas[x + strX][y] == ' ')) this.canvas[x + strX][y] = str.charAt(strX);
            }
        }
    }

    public void clearCanvas() {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.canvas[x][y] = ' ';
            }
        }
    }

    public void printCanvas() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                System.out.print(this.canvas[x][y]);
            }
            System.out.println();
        }
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getLastX() {
        return width - 1;
    }
    public int getLastY() {
        return height - 1;
    }
}
