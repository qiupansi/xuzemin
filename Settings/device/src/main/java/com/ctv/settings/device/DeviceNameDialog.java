
package com.ctv.settings.device;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.ctv.settings.device.listener.DeviceChangeListener;
import com.ctv.settings.security.R;
import com.cultraview.tv.utils.CtvCommonUtils;


public class DeviceNameDialog extends Dialog implements OnFocusChangeListener, OnClickListener {

    private static final String TAG = "DeviceNameDialog";

    private EditText device_name_et;

    private Button device_name_save;

    private Button device_name_cancel;

    private final Context ctvContext;

    private String deviceName;
    private DeviceChangeListener deviceChangeListener;

    public DeviceNameDialog(Context ctvContext, DeviceChangeListener deviceChangeListener) {
        super(ctvContext);
        this.ctvContext = ctvContext;
        this.deviceChangeListener = deviceChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.device_name);
        setWindowStyle();
        findViews();
    }

    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = ctvContext.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparency_bg);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.y = (int) (-36 * scale + 0.5f);
        lp.width = (int) (680 * scale + 0.5f);
        lp.height = (int) (408 * scale + 0.5f);
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        w.setDimAmount(0.0f);
        w.setAttributes(lp);
    }

    /**
     * findViews(The function of the method)
     *
     * @Title: findViews
     * @Description: TODO
     */
    private void findViews() {
        device_name_et = (EditText) findViewById(R.id.device_name_et);
        device_name_save = (Button) findViewById(R.id.device_name_save);
        device_name_cancel = (Button) findViewById(R.id.device_name_cancel);
        device_name_et.setOnFocusChangeListener(this);
        device_name_save.setOnFocusChangeListener(this);
        device_name_cancel.setOnFocusChangeListener(this);
        device_name_save.setOnClickListener(this);
        device_name_cancel.setOnClickListener(this);
        /*String deviceName = (String)SPUtil.getData(ctvContext, Constants.DEVICE_TITLE, "DavinciBoard");
        this.deviceName = deviceName;//DataTool.getDeviceName(ctvContext);*/
        //设备名称
        String deviceName = CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_SoftwareVersion", "DeviceName");
        device_name_et.setText(deviceName);
        device_name_et.selectAll();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.device_name_save) {
            saveDate();
        } else if (id == R.id.device_name_cancel) {
            dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (getCurrentFocus().equals(device_name_et)) {
                device_name_save.requestFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveDate() {

        String name = device_name_et.getText().toString();
        if (TextUtils.isEmpty(name)) {
            device_name_et.setHint(R.string.correct_device_name);
            device_name_et.requestFocus();
        } else {
            setDeviceName(name);
            deviceChangeListener.deviceNameChange(name);
            dismiss();
        }

    }

    @SuppressLint("NewApi")
    private void setDeviceName(String name) {
        CtvCommonUtils.setCultraviewProjectInfo(ctvContext, "tbl_SoftwareVersion", "DeviceName",
                device_name_et.getText().toString().trim());
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int id = view.getId();
        if (id == R.id.device_name_et) {
            if (has_focus) {
                device_name_et.setSelected(true);
                device_name_et.selectAll();
            } else {
                device_name_et.setSelected(false);
            }
        } else if (id == R.id.device_name_save) {
            if (has_focus) {
                device_name_save.setSelected(true);
            } else {
                device_name_save.setSelected(false);
            }
        } else if (id == R.id.device_name_cancel) {
            if (has_focus) {
                device_name_cancel.setSelected(true);
            } else {
                device_name_cancel.setSelected(false);
            }
        }
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
//        AboutTvViewHolder.aboutTvHandler.sendEmptyMessage(Constants.DIALOG_DISMISS);
    }

}
