package ru.taxcom.mobile.android.searchlibrary.util.textview;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class FontCache {
    private static final HashMap<String, Typeface> FONT_CACHE = new HashMap<>();

    @Nullable
    public static Typeface getTypeface(@NonNull String fontName, Context context) {
        Typeface typeface = FONT_CACHE.get(fontName);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            } catch (Exception e) {
                return null;
            }
            FONT_CACHE.put(fontName, typeface);
        }
        return typeface;
    }

    private FontCache() {
    }
}
