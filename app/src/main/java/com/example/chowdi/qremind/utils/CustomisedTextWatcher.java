package com.example.chowdi.qremind.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by anton on 2/4/2016.
 */
public class CustomisedTextWatcher implements TextWatcher, View.OnFocusChangeListener {

    private TextView textView;
    private EditText editText;
    private int hintId;

    public CustomisedTextWatcher(EditText editText, TextView textView, int hintId)
    {
        this.textView = textView;
        this.editText = editText;
        this.hintId = hintId;

        editText.setOnFocusChangeListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!editText.getText().toString().isEmpty())
        {
            textView.setVisibility(View.VISIBLE);
        }
        editText.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            textView.setVisibility(View.VISIBLE);
            if (hintId > 0)
                editText.setHint("");
        }
        else
        {
            if(editText.getText().toString().isEmpty())
            {
                textView.setVisibility(View.INVISIBLE);
                editText.setHint(hintId);
            }
            else
            {
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}
