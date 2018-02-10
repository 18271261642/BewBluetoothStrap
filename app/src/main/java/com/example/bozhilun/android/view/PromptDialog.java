package com.example.bozhilun.android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.bozhilun.android.R;


/**
 * 提示对话dialog
 * 
 * @author ljb
 * 
 */
public class PromptDialog extends Dialog implements View.OnClickListener {

    public PromptDialog(Context context) {
        super(context);
    }
    
    /**
     * 监听器，用于事件回调
     */
    private OnPromptDialogListener listener;
    /**
     * 内容
     */
    private TextView content;
    /**
     * 内容
     */
    private TextView contentMsg;
    /**
     * 确定
     */
    private Button btnok;
    /**
     * 取消
     */
    private Button btnno;
    /**
     * 标记来源
     */
    private int code;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert);
        initView();
    }
    
    private void initView() {
        content = (TextView) findViewById(R.id.dialog_prompt_content);
        contentMsg = (TextView) findViewById(R.id.dialog_prompt_content1);
        btnok = (Button) findViewById(R.id.dialog_ok);
        btnno = (Button) findViewById(R.id.dialog_no);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        btnok.setOnClickListener(this);
        btnno.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ok:
                if (listener != null) {
                    listener.leftClick(code);
                }
                cancel();
                break;
            case R.id.dialog_no:
                if (listener != null) {
                    listener.rightClick(code);
                }
                cancel();
                break;
            default:
                break;
        }
    }
    
    public interface OnPromptDialogListener {
        
        public void leftClick(int code);
        
        public void rightClick(int code);
    }
    
    public void setListener(OnPromptDialogListener listener) {
        this.listener = listener;
    }
    
    public void setTitle(String str) {
        content.setText(str);
    }

    public void setContent(String msg){
        contentMsg.setText(msg);
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public void setNoButtom(boolean bool) {
        if (bool) {
            btnno.setVisibility(View.VISIBLE);
        } else {
            btnno.setVisibility(View.GONE);
        }
    }
    
    /**
     * 
     * 方法名: setleftText 描述作用: 设置左边的文字
     * 
     * @param text
     * @return void
     * @throws
     */
    public void setleftText(String text) {
        btnok.setText(text);
    }
    
    /**
     * 
     * 方法名: setrightText 描述作用: 设置右边的文字
     * 
     * @param text
     * @return void
     * @throws
     */
    public void setrightText(String text) {
        btnno.setText(text);
    }
}
