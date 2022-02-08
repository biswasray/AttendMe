package com.sbr.attendme;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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

public class DiscListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> objects;
    private final ArrayList<String> objects1;
    private TypedArray sColors;
    private static int sDefaultColor;
    private static int sTileFontColor;
    public DiscListAdapter(@NonNull Activity context, int resource, ArrayList<String> objects,ArrayList<String> objects1) {
        super(context, resource, objects);
        this.context=context;
        this.objects=objects;
        this.objects1=objects1;
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
        ImageView popView=(ImageView) rowView.findViewById(R.id.popview);
        popView.setVisibility(View.INVISIBLE);
        //TextDrawable
        titleText.setText(objects.get(position));

        imageView.setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(""+objects.get(position).charAt(0),pickColor(objects.get(position))));
        subtitleText.setText(objects1.get(position));

        return rowView;
    }
    private int pickColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, sDefaultColor);
    }
}
