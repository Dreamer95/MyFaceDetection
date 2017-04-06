package com.google.android.gms.samples.vision.face.facetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by dong on 07/04/17.
 */

public class ViewPicture extends AppCompatActivity {

    Button btnaddeffect;
    ImageView original;
    private Bitmap image;
    private String path1;

    @Override
    protected void onCreate(@Nullable Bundle icicle1) {
        super.onCreate(icicle1);
        setContentView(R.layout.view_picture);

        original = (ImageView) findViewById(R.id.view);

        Intent i=getIntent();

        path1=i.getStringExtra("image thumbnail path");
      //  Log.i("path of file in list of effects......", path1);
        image = (BitmapFactory.decodeFile(path1));

        original.setImageBitmap(image);


        btnaddeffect = (Button) findViewById(R.id.btnEffects);

        btnaddeffect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent listofeffect = new Intent(ViewPicture.this, ListOfEffects.class);
                listofeffect.putExtra("addeffect",path1);
                startActivity(listofeffect);
            }
        });



    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate (R.menu.menu, menu);;
        return true;
    }
    */
}
