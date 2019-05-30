package com.example.davenguyen.lomoapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by Dave Nguyen on 17-Oct-17.
 */

public class GradientTransformation implements Transformation {

    int startColor =Color.TRANSPARENT;
    int endColor = Color.rgb(47,47,47) ;

    @Override public Bitmap transform(Bitmap source) {

        float height = source.getHeight();

        float startX = 0;
        float startY = 0;


        double angleInRadians = Math.toRadians(90);
        double length = height;

        float endX = (float)(Math.cos(angleInRadians) * length);
        float endY = (float)(Math.sin(angleInRadians) * length);

        Bitmap grandientBitmap = source.copy(source.getConfig(), true);
        Canvas canvas = new Canvas(grandientBitmap);
        //left-top == (0,0) , right-bottom(startX,startY);
        LinearGradient grad =
                new LinearGradient(startX, startY*2, endX, endY, startColor, endColor, Shader.TileMode.CLAMP);
        Paint p = new Paint(Paint.DITHER_FLAG);
        p.setShader(null);
        p.setDither(true);
        p.setFilterBitmap(true);
        p.setShader(grad);
        canvas.drawPaint(p);
        source.recycle();
        return grandientBitmap;
    }



    @Override public String key() {
        return "Gradient";
    }
}