package com.sbr.attendme;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.sbr.attendme.databinding.ActivityListOfClassesBinding;

import java.util.ArrayList;

public class ListOfClasses extends AppCompatActivity implements CreateClass.DismissListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityListOfClassesBinding binding;
    private ListView classlist;
    private ClassListAdapter adapter;
    private ArrayList<Classs> arr;
    private CreateClass cc;
    public static int classCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListOfClassesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        cc=new CreateClass(MainActivity.db);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                cc.show(ListOfClasses.this.getSupportFragmentManager(),null);
            }
        });
        classlist=(ListView) findViewById(R.id.classlist);
        arr=new ArrayList<>();
        adapter=new ClassListAdapter(this, android.R.layout.simple_list_item_1,arr);
        fetch();
        classlist.setAdapter(adapter);
    }
    void fetch() {
        if(MainActivity.db.tableExists(DBHelper.CLASS_TABLE_NAME)) {
            arr.clear();
            for(Classs c:MainActivity.db.getClasses()) {
                arr.add(c);
            }
            classCount=arr.size();
            if(classCount!=0) {
                findViewById(R.id.noitemfound).setVisibility(View.INVISIBLE);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int i) {
        fetch();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int i) {

    }
}