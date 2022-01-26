package com.sbr.attendme;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.sbr.attendme.databinding.ActivityListOfClassesBinding;

import java.util.ArrayList;

public class ListOfClasses extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityListOfClassesBinding binding;
    private ListView classlist;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListOfClassesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

            }
        });
        classlist=(ListView) findViewById(R.id.classlist);
        arr=new ArrayList<>();
        fetch();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);
        classlist.setAdapter(adapter);
    }
    void fetch() {
        if(MainActivity.db.tableExists(DBHelper.CLASS_TABLE_NAME)) {
            ArrayList<Classs> r=MainActivity.db.getClasses();
            for(Classs c:r) {
                arr.add(c.getId()+c.getBranch()+c.getStream()+c.getSession());
            }
        }
        else {
            arr.add("No class found yet");
        }
    }
}