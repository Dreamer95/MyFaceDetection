package com.google.android.gms.samples.vision.face.facetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by dong on 07/04/17.
 */

public class ListOfEffects extends AppCompatActivity {

    ArrayList<String> effects= new ArrayList<String>();
    private String path;
    private String effect_choosen;

    private ListView lv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        effects.add("Effect 1");
        effects.add("Effect 2");
        effects.add("Effect 3");
        effects.add("Effect 4");
        effects.add("Effect 5");

        setContentView(R.layout.list_of_effects);

        Intent i = getIntent();
        path = i.getStringExtra("addeffect");

        Log.i("path of file in list of effects......", path);

        lv= (ListView) findViewById(R.id.lvPhoneNumber);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,effects);

        lv.setTextFilterEnabled(true);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                effect_choosen = effects.get(i);
                //Log.i("Effect choosen................",effect_choosen);
                applyEffects();
            }
        });

    }
    private void applyEffects()
    {
        Intent i = new Intent(ListOfEffects.this, EffectAdded.class);
        i.putExtra("path",path);
        i.putExtra("effect",effect_choosen);
        startActivity(i);
    }
}