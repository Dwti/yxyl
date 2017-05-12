package com.yanxiu.gphone.student.login.utils;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/10 11:25.
 * Function :
 */

public class EditTextManger implements TextWatcher {

    public interface onTextLengthChangedListener {
        void onChanged(EditText view, String value, boolean isEmpty);
    }

    private interface GetTextChangedListener {
        void setListener(onTextLengthChangedListener listener);
    }

    private interface GetEditTextListener {
        void setEditText(EditText editText);
    }

    private static final String INPUT_ONLY_NUMBER = "0123456789";
    private static final String INPUT_ONLY_LETTER_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String INPUT_ONLY_LETTER_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private WeakReference<onTextLengthChangedListener> reference_listener;
    private WeakReference<EditText> reference_view;

    public static EditTextManger getManager(EditText editText) {
        return new EditTextManger(editText);
    }

    private EditTextManger(EditText editText) {
        reference_view = new WeakReference<>(editText);
    }

    public void setTextChangedListener(onTextLengthChangedListener listener) {
        reference_listener = new WeakReference<>(listener);
        getEditText(editText -> editText.addTextChangedListener(this));
    }

    public EditTextManger setInputOnlyNumber() {
        getEditText(editText -> setKeyType(editText, INPUT_ONLY_NUMBER));
        return this;
    }

    public EditTextManger setInputOnlyLetterLowercase() {
        getEditText(editText -> setKeyType(editText, INPUT_ONLY_LETTER_LOWERCASE));
        return this;
    }

    public EditTextManger setInputOnlyLetterUppercase() {
        getEditText(editText -> setKeyType(editText, INPUT_ONLY_LETTER_UPPERCASE));
        return this;
    }

    public EditTextManger setInputNumberAndLetterLowercase() {
        getEditText(editText -> setKeyType(editText, INPUT_ONLY_NUMBER + INPUT_ONLY_LETTER_LOWERCASE));
        return this;
    }

    public EditTextManger setInputNumberAndLetterUppercase() {
        getEditText(editText -> setKeyType(editText, INPUT_ONLY_NUMBER + INPUT_ONLY_LETTER_UPPERCASE));
        return this;
    }

    public EditTextManger setInputNumberAndLetter() {
        getEditText(editText -> setKeyType(editText, INPUT_ONLY_NUMBER + INPUT_ONLY_LETTER_LOWERCASE + INPUT_ONLY_LETTER_UPPERCASE));
        return this;
    }

    private void getTextChangedListener(GetTextChangedListener listener) {
        if (reference_listener != null) {
            onTextLengthChangedListener mTextLengthChangedListener = reference_listener.get();
            if (mTextLengthChangedListener != null) {
                listener.setListener(mTextLengthChangedListener);
            }
        }
    }

    private void getEditText(GetEditTextListener listener) {
        if (reference_view != null) {
            EditText mEditText = reference_view.get();
            if (mEditText != null) {
                listener.setEditText(mEditText);
            }
        }
    }

    private void setKeyType(EditText editText, String chars) {
        int inputType = editText.getInputType();
        editText.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return inputType == InputType.TYPE_NULL ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : inputType;
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();
        if (value.length() > 0) {
            setChanged(value, false);
        } else {
            setChanged(value, true);
        }
    }

    private void setChanged(String value, boolean isEmpty) {
        getTextChangedListener(listener -> getEditText(editText -> listener.onChanged(editText, value, isEmpty)));
    }
}
