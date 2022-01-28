package com.sbr.attendme;

import android.app.Activity;
import android.content.Context;
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
    private TypedArray sColors;
    private static int sDefaultColor;
    private static int sTileFontColor;
    public ClassListAdapter(@NonNull Context context, int resource, ArrayList<Classs> classses) {
        super(context, resource,classses);
        this.context= (Activity) context;
        this.classses=classses;
        if(sColors==null) {
            Resources res=context.getResources();
            sColors=res.obtainTypedArray(R.array.letter_tile_colors);
            sDefaultColor=res.getColor(R.color.letter_tile_default_color);
            sTileFontColor=res.getColor(R.color.letter_tile_font_color);
        }
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.classtitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.classicon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.classsubtitle);
        //TextDrawable
        titleText.setText(classses.get(position).getSubject());

        imageView.setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(""+classses.get(position).getSubject().charAt(0),pickColor(classses.get(position).getSubject())));
        subtitleText.setText(classses.get(position).getStream()+" "+classses.get(position).getBranch()+" "+classses.get(position).getSession());

        return rowView;

    }
    private int pickColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        // String.hashCode() implementation is not supposed to change across java versions, so
        // this should guarantee the same email address always maps to the same color.
        // The email should already have been normalized by the ContactRequest.
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, sDefaultColor);
    }
}
