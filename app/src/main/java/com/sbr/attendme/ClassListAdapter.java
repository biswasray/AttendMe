package com.sbr.attendme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

public class ClassListAdapter extends ArrayAdapter<Classs> {
    private final Activity context;
    private final ArrayList<Classs> classses;
    private ColorPicker colorPicker;
    public ClassListAdapter(@NonNull Activity context, int resource, ArrayList<Classs> classses) {
        super(context, resource,classses);
        this.context=  context;
        this.classses=classses;
        colorPicker=new ColorPicker(context);
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.classtitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.classicon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.classsubtitle);
        ImageView viewData = (ImageView) rowView.findViewById(R.id.popview);
        viewData.setOnClickListener(view1 -> {
            Intent intent=new Intent(context,ShowData.class);
            intent.putExtra("classs",classses.get(position));
            context.startActivity(intent);
        });
        //TextDrawable
        titleText.setText(classses.get(position).getSubject());

        imageView.setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(""+classses.get(position).getSubject().charAt(0),colorPicker.pickColor(classses.get(position).getSubject())));
        subtitleText.setText(classses.get(position).getStream()+" "+classses.get(position).getBranch()+" "+classses.get(position).getSession());
        return rowView;
    }
}
