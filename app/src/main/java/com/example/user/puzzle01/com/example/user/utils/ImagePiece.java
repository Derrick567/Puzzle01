package com.example.user.puzzle01.com.example.user.utils;

import android.graphics.Bitmap;

/**
 * Created by user on 2015/8/23.
 store every piece
 */

public class ImagePiece {
    private int index;
    private Bitmap bitmap;

    public ImagePiece() {
    }

    public ImagePiece(Bitmap bitmap, int index) {
        this.bitmap = bitmap;
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getIndex() {
        return index;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ImagePiece{" +
                "bitmap=" + bitmap +
                ", index=" + index +
                '}';
    }
}
