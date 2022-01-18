package com.sbr.attendme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CreateStudent extends DialogFragment {
    DBHelper db;
    public CreateStudent(DBHelper db) {
        this.db=db;
    }
    public interface DismissListener {
        public void onDismiss(@NonNull DialogInterface dialog);
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    DismissListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=requireActivity().getLayoutInflater();
        View studentView=layoutInflater.inflate(R.layout.create_student,null);
        EditText studentname=(EditText) studentView.findViewById(R.id.studentname);
        EditText studentroll=(EditText) studentView.findViewById(R.id.studentroll);
        builder.setView(studentView)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(studentname.getText().toString().isEmpty()||studentroll.getText().toString().isEmpty()) {
                            Toast.makeText(CreateStudent.this.getActivity(),"Fields are mandatory",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(db.tableExists(DBHelper.STUDENT_TABLE_NAME)) {
                            db.insertStudent(studentname.getText().toString(),MainActivity.MY_ID,Integer.parseInt(studentroll.getText().toString()));
                        }
                        else {
                            db.createStudent();
                            db.insertStudent(studentname.getText().toString(),MainActivity.MY_ID,Integer.parseInt(studentroll.getText().toString()));
                        }
                        listener.onDialogPositiveClick(CreateStudent.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onCancel(dialogInterface);
                        listener.onDialogNegativeClick(CreateStudent.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener=(DismissListener) context;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onDismiss(dialog);
    }
}
