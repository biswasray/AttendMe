package com.sbr.attendme;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;

public class ColorPicker {
    private TypedArray sColors;
    private int sDefaultColor;
    private int sTileFontColor;
    private Context context;

    public ColorPicker(Context context) {
        this.context = context;
        if(sColors==null) {
            Resources res=context.getResources();
            sColors=res.obtainTypedArray(R.array.letter_tile_colors);
            sDefaultColor=res.getColor(R.color.letter_tile_default_color);
            sTileFontColor=res.getColor(R.color.letter_tile_font_color);
        }
    }
    public int pickColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, sDefaultColor);
    }
}
