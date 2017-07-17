package com.example.user.puzzle01.com.example.user.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/8/23.
 */
public class ImageSplitterUtil {

    /**
     *  將傳入的圖片 bitmap 切成 piece * piece 塊
     * @param bitmap 傳入的圖片
     * @param piece  切成 piece * piece 塊
     * @return  List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int piece) {

        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //因為拼圖為正方形，取圖片寬高的最小值作為方形的邊長
        int pieceWidth = Math.min(width, height) / piece;


        //切圖
        for (int i = 0; i < piece; i++) {
            for (int j = 0; j < piece; j++) {
                ImagePiece imagePiece = new ImagePiece();

                /* index , like:
                  0 1 2
                  3 4 5
                  7 8 9
                        */
                imagePiece.setIndex(j + i * piece);

                int x = j * pieceWidth;
                int y = i * pieceWidth;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));
                imagePieces.add(imagePiece);
            }
        }

        return imagePieces;
    }
}
