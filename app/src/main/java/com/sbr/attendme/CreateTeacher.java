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

public class CreateTeacher extends DialogFragment {
    DBHelper db;
    public CreateTeacher(DBHelper db) {
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
        View teacherView=layoutInflater.inflate(R.layout.create_teacher,null);
        EditText teachername=(EditText) teacherView.findViewById(R.id.teachername);
        builder.setView(teacherView)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(teachername.getText().toString().isEmpty()) {
                            Toast.makeText(CreateTeacher.this.getActivity(),"Enter a name",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(db.tableExists(DBHelper.TEACHER_TABLE_NAME)) {
                            db.insertTeacher(teachername.getText().toString(),MainActivity.MY_ID);
                        }
                        else {
                            db.createTeacher();
                            db.insertTeacher(teachername.getText().toString(),MainActivity.MY_ID);
                        }
                        listener.onDialogPositiveClick(CreateTeacher.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onCancel(dialogInterface);
                        listener.onDialogNegativeClick(CreateTeacher.this);
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
