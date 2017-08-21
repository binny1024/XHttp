package com.dragon.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dragon.R;

import static com.dragon.util.UtilWidget.setViewAlphaAnimation;

/**
 * author xander on  2017/8/18.
 * function
 */

public class ModifyDialog extends Dialog {
    public interface OnModifyDialogListener{
        void onSure(final String name, final String password, final String telephone);
        void onCancel(final Dialog dialog);
    }
    private Activity mActivity;
    private EditText mName;
    private EditText mPhone;
    private EditText mNewPassword;
    private Button mCancelBtn;
    private Button mSureBtn;
    private OnModifyDialogListener mListener;
    private ModifyDialog mDialog;

    public ModifyDialog(final Activity context) {
//        super(context);
        super(context, R.style.no_frame_transparent);
        mActivity = context;
        mDialog = this;

    }

    public ModifyDialog setOnModifyDialogListener( OnModifyDialogListener listener){
        mListener = listener;
        return this;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_modify_password);
        initView();
        setListener();
    }

    private void setListener() {
        mSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setViewAlphaAnimation(mSureBtn);
                String name = mName.getText().toString();
                String telephone = mPhone.getText().toString();
                String password = mNewPassword.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(mActivity, "请输入账号！", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(mActivity, "请输入新密码！", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(telephone)) {
                    Toast.makeText(mActivity, "请输入手机号！", Toast.LENGTH_SHORT).show();
                }
                else {
                    mListener.onSure(name,password,telephone);
                }

            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setViewAlphaAnimation(mCancelBtn);
                mListener.onCancel(mDialog);
            }
        });
    }

    private void initView() {

        mName = (EditText) findViewById(R.id.modify_name);
        mPhone = (EditText) findViewById(R.id.modify_phone);
        mNewPassword = (EditText) findViewById(R.id.modify_password);
        mCancelBtn = (Button) findViewById(R.id.modify_cancle);
        mSureBtn = (Button) findViewById(R.id.modify_sure);

    }
}
