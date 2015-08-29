package de.cofinpro.dojo.service;

/**
 * Created by mosdoba on 29.08.2015.
 */
public class InitGameRequest {
    private int width, height = 0;
    private float mineRatio = 0;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getMineRatio() {
        return mineRatio;
    }

    public void setMineRatio(float mineRatio) {
        this.mineRatio = mineRatio;
    }
}
