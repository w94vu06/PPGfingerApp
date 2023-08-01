package com.example.myapplication.Util;

import android.graphics.Bitmap;
import android.view.TextureView;

public class GetPixelUtil {

    public void getPixel(TextureView CameraView) {
        Bitmap bmp = CameraView.getBitmap();
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int[] pixels = new int[height * width];
        int[] pixelsFullScreen = new int[height * width];

        bmp.getPixels(pixelsFullScreen, 0, width, 0, 0, width, height);//get full screen and main to detect
        bmp.getPixels(pixels, 0, width, width / 2, height / 2,
                width / 10, height / 10);//get small screen

        int redThreshold = 0;
        int greenThreshold = 0;
        int blueThreshold = 0;//小畫面的紅綠藍

        int fullScreenRed = 0;
        int fullScreenGreen = 0;
        int fullScreenBlue = 0;//整個畫面的紅綠藍

        for (int i = 0; i < height * width; i++) {
            //RED
            int red = (pixels[i] >> 16) & 0xFF;
            int redFull = (pixelsFullScreen[i] >> 16) & 0xFF;
            fullScreenRed += redFull;
            redThreshold += red;
            //GREEN
            int green = (pixels[i] >> 8) & 0xFF;
            int greenFull = (pixelsFullScreen[i] >> 8) & 0xFF;
            fullScreenGreen += greenFull;
            greenThreshold += green;
            //BLUE
            int blue = pixels[i] & 0xFF;
            int blueFull = pixelsFullScreen[i] & 0xFF;
            fullScreenBlue += blueFull;
            blueThreshold += blue;
        }
        //小畫面平均值
        int averageRedThreshold = redThreshold / (height * width);// 0 1 2
        int averageGreenThreshold = greenThreshold / (height * width);
        int averageBlueThreshold = blueThreshold / (height * width);

    }
}
