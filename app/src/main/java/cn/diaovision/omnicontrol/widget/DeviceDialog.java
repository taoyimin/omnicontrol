package cn.diaovision.omnicontrol.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.State;
import cn.diaovision.omnicontrol.core.model.device.common.BarcoProjector;
import cn.diaovision.omnicontrol.core.model.device.common.CommonDevice;
import cn.diaovision.omnicontrol.widget.adapter.CustomSpinnerAdapter;

/**
 * Created by TaoYimin on 2017/6/7.
 */

public class DeviceDialog extends Dialog {
    @BindView(R.id.category_spinner)
    Spinner categorySpinner;
    @BindView(R.id.button_go_back)
    ImageView buttonGoBack;
    @BindView(R.id.alias_edit)
    EditText aliasEdit;
    @BindView(R.id.ip_edit)
    EditText ipEdit;
    @BindView(R.id.port_edit)
    EditText portEdit;
    @BindView(R.id.dialog_button_confirm)
    Button buttonConfirm;
    @BindView(R.id.dialog_button_delete)
    Button buttonDelete;
    @BindView(R.id.empty_area)
    View emptyArea;

    Context context;
    CommonDevice device;
    CustomSpinnerAdapter categoryAdapter;

    OnButtonClickListener onButtonClickListener;

    public DeviceDialog(@NonNull Context context, CommonDevice device) {
        this(context, R.style.dialog, device);
    }

    public DeviceDialog(@NonNull Context context, @StyleRes int themeResId, CommonDevice device) {
        super(context, themeResId);
        this.context = context;
        this.device = device;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_device, null);
        this.setContentView(layout);
        ButterKnife.bind(this);
        String[] arrays = context.getResources().getStringArray(R.array.common_device_category);
        categoryAdapter = new CustomSpinnerAdapter(context, arrays, null);
        categorySpinner.setAdapter(categoryAdapter);
        if (device != null) {
            emptyArea.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            //设置设备类型的默认选中
            categorySpinner.setSelection(device.getType());
            //设置设备名称编辑框默认文字
            aliasEdit.setText(device.getName());
            //设置设备的默认IP地址
            ipEdit.setText(device.getIp());
            //设置设备的通信端口
            portEdit.setText(device.getPort() + "");
            buttonGoBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    device.setType(categorySpinner.getSelectedItemPosition());
                    device.setName(aliasEdit.getText().toString());
                    device.setIp(ipEdit.getText().toString());
                    device.setPort(Integer.parseInt(portEdit.getText().toString()));
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onConfirmClick();
                    }
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onDeleteClick();
                    }
                }
            });
        } else {
            emptyArea.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String alias = aliasEdit.getText().toString();
                    String ip = ipEdit.getText().toString();
                    String port = portEdit.getText().toString();
                    if (TextUtils.isEmpty(alias)) {
                        Toast.makeText(context, "设备名称不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(ip)) {
                        Toast.makeText(context, "网络地址不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(port)) {
                        Toast.makeText(context, "通信端口不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch (categorySpinner.getSelectedItemPosition()) {
                        case CommonDevice.TYPE.BARCO_PROJECTOR:
                            device = new BarcoProjector(alias, ip, Integer.parseInt(port));
                            device.setType(CommonDevice.TYPE.BARCO_PROJECTOR);
                            device.setState(State.OFF);
                            break;
                        default:
                            break;
                    }
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onAddDeviceClick(device);
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Display display = ((MainControlActivity) context).getWindowManager().getDefaultDisplay();
        params.width = (int) (display.getWidth() * 0.35);
        if (device != null) {
            params.height = (int) (display.getHeight() * 0.65);
        } else {
            params.height = (int) (display.getHeight() * 0.60);
        }
        window.setAttributes(params);
    }

    public interface OnButtonClickListener {
        void onConfirmClick();

        void onDeleteClick();

        void onAddDeviceClick(CommonDevice device);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}
