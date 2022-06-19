package com.wwdablu.soumya.extimageview.nanit;

import android.content.Context;
import android.util.TypedValue;

public class GeneralUtilities {

    public static int dpsToPixels(Context context, float value) {
        return (int) dpsToPixelsF(context, value);
    }

    public static float dpsToPixelsF(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

}
