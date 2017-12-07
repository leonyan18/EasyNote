package com.example.acm_yan.easynote;


import android.widget.EditText;
import android.widget.TextView;

import android.widget.EditText;
import android.widget.TextView.BufferType;

public class Writer {


    private final EditText mEditText;

    public Writer(EditText edittext) {
        mEditText = edittext;//写入文字
    }

    public void setContent(String content) {
        mEditText.setText(content, TextView.BufferType.EDITABLE);//设置可编辑
    }


    public String getTitle() {
        String content = mEditText.getText().toString();//设置题目
        if("".equals(content)) {
            return "";
        }
        int end = content.indexOf("\n");
        return content.substring(0,end==-1?content.length():end);//取得标题
    }
    public String getContent() {
        return mEditText.getText().toString();//得到文字
    }

}
