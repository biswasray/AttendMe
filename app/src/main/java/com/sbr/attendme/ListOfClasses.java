package com.sbr.attendme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

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
    private ArrayList<Classs> arr,selectedItems;
    private boolean isLongPressed=false;
    private CreateClass cc;
    public static int classCount;
    private MenuItem deleteMenu;
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
        selectedItems=new ArrayList<>();
        adapter=new ClassListAdapter(this, android.R.layout.simple_list_item_multiple_choice,arr);
        classlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        classlist.setItemsCanFocus(false);
        fetch();
        classlist.setAdapter(adapter);
        classlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                isLongPressed=true;
                if(selectedItems.contains(arr.get(i))) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                    selectedItems.remove(arr.get(i));
                }
                else {
                    view.setBackgroundResource(R.color.selectedcolor);
                    selectedItems.add(arr.get(i));
                }
                if(selectedItems.size()==0) {
                    isLongPressed = false;
                }
                deleteMenu.setVisible(isLongPressed);
                return true;
            }
        });
        classlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(isLongPressed) {
                    if(selectedItems.contains(arr.get(i))) {
                        view.setBackgroundColor(Color.TRANSPARENT);
                        selectedItems.remove(arr.get(i));
                    }
                    else {
                        view.setBackgroundResource(R.color.selectedcolor);
                        selectedItems.add(arr.get(i));
                    }
                    if(selectedItems.size()==0) {
                        isLongPressed = false;
                    }
                    deleteMenu.setVisible(isLongPressed);
                }
                else {
                    Intent intent=new Intent(ListOfClasses.this,TeaDiscActivity.class);
                    intent.putExtra("classs",arr.get(i));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu,menu);
        deleteMenu=menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        for(Classs c:selectedItems) {
            MainActivity.db.dropTable(c.getDateTable());
            MainActivity.db.deleteClass(c);
            arr.remove(c);
        }
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
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