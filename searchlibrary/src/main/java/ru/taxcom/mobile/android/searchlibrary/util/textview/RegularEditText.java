package ru.taxcom.mobile.android.searchlibrary.util.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class RegularEditText extends AppCompatEditText {
    public interface OnBackListener {
        void onBackListener();
    }

    private OnBackListener mOnBackListener;

    public RegularEditText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        mOnBackListener = onBackListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOnBackListener != null) {
                mOnBackListener.onBackListener();
                return true;
            }
        }
        return false;
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/Roboto-Regular.ttf", context);
        setTypeface(customFont);
    }
}