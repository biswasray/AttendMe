package com.sbr.attendme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateClass extends DialogFragment{
    DBHelper db;

    public CreateClass(DBHelper db) {
        this.db = db;
    }
    public interface DismissListener {
        public void onDismiss(@NonNull DialogInterface dialog);
        public void onDialogPositiveClick(DialogFragment dialog, int i);
        public void onDialogNegativeClick(DialogFragment dialog,int i);
    }
    CreateClass.DismissListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=requireActivity().getLayoutInflater();
        View classView=layoutInflater.inflate(R.layout.create_class,null);
        EditText subject=(EditText) classView.findViewById(R.id.subjectname);
        Spinner stream=(Spinner)classView.findViewById(R.id.spinnerStream);
        Spinner branch=(Spinner)classView.findViewById(R.id.spinnerBranch);
        Spinner session=(Spinner)classView.findViewById(R.id.spinnerSession);
        ArrayList<String> yearList=new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i=thisYear-4;i<thisYear+10;i++)
            yearList.add(Integer.toString(i));
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item,yearList);
        session.setAdapter(adapter);
        builder.setView(classView)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(subject.getText().toString().isEmpty()) {
                            Toast.makeText(CreateClass.this.getActivity(),"Fields are mandatory",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(db.tableExists(DBHelper.CLASS_TABLE_NAME)) {
                            Classs temp=new Classs(ListOfClasses.classCount,subject.getText().toString(),branch.getSelectedItem().toString(),stream.getSelectedItem().toString(),DBHelper.DATE_TABLE+ListOfClasses.classCount,Integer.parseInt(session.getSelectedItem().toString()));
                            db.insertClass(temp);
                        }
                        else {
                            db.createClass();
                            Classs temp=new Classs(ListOfClasses.classCount,subject.getText().toString(),branch.getSelectedItem().toString(),stream.getSelectedItem().toString(),DBHelper.DATE_TABLE+ListOfClasses.classCount,Integer.parseInt(session.getSelectedItem().toString()));
                            db.insertClass(temp);
                        }
                        listener.onDialogPositiveClick(CreateClass.this,2);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onCancel(dialogInterface);
                        listener.onDialogNegativeClick(CreateClass.this,2);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener=(CreateClass.DismissListener) context;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onDismiss(dialog);
    }
}
