package com.android.jdrd.robotclient.dialog;

/*
 * Created by Administrator on 2017/2/16.
 * text for Map
 */

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.jdrd.robotclient.R;


/**
 * 自定义对话框
 * 2013-10-28
 * 下午12:37:43
 *  Tom.Cai
 */
public class DeleteDialog extends Dialog {
    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title;
    private TextView title_template;

    public DeleteDialog(Context context) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_delete_dialog,null);
        title = (TextView) mView.findViewById(R.id.title);
        title_template = (TextView) mView.findViewById(R.id.title_template);
        editText = (EditText) mView.findViewById(R.id.editText);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
    }

    public View getEditText(){
        return editText;
    }
    public TextView getTextView(){
        return title;
    }
    public TextView getTemplate(){
        return title_template;
    }
    public View getNegative(){
        return negativeButton;
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     *
     */
    public void setOnPositiveListener(View.OnClickListener listener){
        positiveButton.setOnClickListener(listener);
    }
    /**
     * 取消键监听器
     *
     */
    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }

}