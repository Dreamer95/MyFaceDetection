package com.google.android.gms.samples.vision.face.facetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dong on 07/04/17.
 */

public class EffectAdded extends Activity {

    private String path;
    private String effect_choosen;
    private ImageView changed;
    private Bitmap out;

    Button btnsavePicture;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_view);

        Intent i = getIntent();
        path = i.getStringExtra("path");
        effect_choosen = i.getStringExtra("effect");

        changed=(ImageView)findViewById(R.id.view);

        //Log.i("path of file in effects added.....................", path);
        //Log.i("effect chosen in effects added.....................", effect_choosen);

        Bitmap thumbnail = (BitmapFactory.decodeFile(path));

        if(effect_choosen.equalsIgnoreCase("Effect 1"))
        {
            out=addEffect(thumbnail,5,5.0,6.0,0.0);  //red,blue,no green
        }
        else if(effect_choosen.equalsIgnoreCase("Effect 2"))
        {
            out=addEffect(thumbnail,5,5.0,0.0,10.0);  //red,green,no blue
        }
        else if(effect_choosen.equalsIgnoreCase("Effect 3"))
        {
            out=addEffect(thumbnail,5,0.0,10.0,0.0);  //only green
        }
        else if(effect_choosen.equalsIgnoreCase("Effect 4"))
        {
            out=addEffect(thumbnail,15,5.0,0.0,10.0);  //red,green,no blue, depth increased
        }
        else if(effect_choosen.equalsIgnoreCase("Effect 5"))
        {
            out=addEffect(thumbnail,5,10.0,0.0,0.0);  //only red
        }

        changed.setImageBitmap(out);

        btnsavePicture = (Button) findViewById(R.id.btnSave);

        btnsavePicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                changed.setDrawingCacheEnabled(true);
                Bitmap bitmap = changed.getDrawingCache();
                File root = Environment.getExternalStorageDirectory();
                File file = new File(root.getAbsolutePath()+"/DCIM/Camera/img.jpg");
                try
                {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();

                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(EffectAdded.this);
                    dlgAlert.setMessage("Lưu thành công");
                    //dlgAlert.setTitle("App Title");
                    // dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
        });
    }

    public static Bitmap addEffect(Bitmap src, int depth, double red, double green, double blue) {

        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap finalBitmap = Bitmap.createBitmap(width, height, src.getConfig());

        final double grayScale_Red = 0.3;
        final double grayScale_Green = 0.59;
        final double grayScale_Blue = 0.11;

        int channel_aplha, channel_red, channel_green, channel_blue;
        int pixel;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {

                pixel = src.getPixel(x, y);
                channel_aplha = Color.alpha(pixel);
                channel_red = Color.red(pixel);
                channel_green = Color.green(pixel);
                channel_blue = Color.blue(pixel);

                channel_blue = channel_green = channel_red = (int)(grayScale_Red * channel_red + grayScale_Green * channel_green + grayScale_Blue * channel_blue);

                channel_red += (depth * red);
                if(channel_red > 255)
                {
                    channel_red = 255;
                }
                channel_green += (depth * green);
                if(channel_green > 255)
                {
                    channel_green = 255;
                }
                channel_blue += (depth * blue);
                if(channel_blue > 255)
                {
                    channel_blue = 255;
                }

                finalBitmap.setPixel(x, y, Color.argb(channel_aplha, channel_red, channel_green, channel_blue));
            }
        }
        return finalBitmap;
    }
}

