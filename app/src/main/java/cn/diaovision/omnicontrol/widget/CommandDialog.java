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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.util.ByteUtils;

/**
 * Created by TaoYimin on 2017/6/7.
 */

public class CommandDialog extends Dialog {
    @BindView(R.id.button_go_back)
    ImageView buttonGoBack;
    @BindView(R.id.alias_edit)
    EditText aliasEdit;
    @BindView(R.id.string_cmd_edit)
    EditText stringCmdEdit;
    @BindView(R.id.byte_cmd_edit)
    EditText byteCmdEdit;
    @BindView(R.id.dialog_button_confirm)
    Button buttonConfirm;
    @BindView(R.id.dialog_button_delete)
    Button buttonDelete;
    @BindView(R.id.empty_area)
    View emptyArea;

    Context context;
    Device.Command cmd;
    OnButtonClickListener onButtonClickListener;

    public CommandDialog(@NonNull Context context, Device.Command cmd) {
        this(context, R.style.dialog, cmd);
    }

    public CommandDialog(@NonNull Context context, @StyleRes int themeResId, Device.Command cmd) {
        super(context, themeResId);
        this.context = context;
        this.cmd = cmd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_command, null);
        this.setContentView(layout);
        ButterKnife.bind(this);
        if (cmd != null) {
            emptyArea.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            //设置指令名称编辑框默认文字
            aliasEdit.setText(cmd.getName());
            //设置指令的默认字符串
            if(TextUtils.isEmpty(cmd.getStringCmd())){
                stringCmdEdit.setText("");
            }else{
                stringCmdEdit.setText(cmd.getStringCmd());
            }
            //设置指令的字节数组
            if(cmd.getByteCmd()!=null&&cmd.getByteCmd().length!=0){
                byteCmdEdit.setText(ByteUtils.bytes2string(cmd.getByteCmd()));
            }else{
                byteCmdEdit.setText("");
            }
            buttonGoBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(aliasEdit.getText().toString())){
                        Toast.makeText(context,"指令名称不能为空！",Toast.LENGTH_SHORT).show();
                        return;
                    }else if(TextUtils.isEmpty(stringCmdEdit.getText().toString())&&TextUtils.isEmpty(byteCmdEdit.getText().toString())){
                        Toast.makeText(context,"字符指令和字节指令不能同时为空！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cmd.setName(aliasEdit.getText().toString());
                    cmd.setStringCmd(stringCmdEdit.getText().toString());
                    if(TextUtils.isEmpty(byteCmdEdit.getText().toString())){
                        cmd.setByteCmd(null);
                    }else{
                        cmd.setByteCmd(ByteUtils.string2bytes(byteCmdEdit.getText().toString()));
                    }
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
                    String stringCmd = stringCmdEdit.getText().toString();
                    String byteCmd = byteCmdEdit.getText().toString();
                    if (TextUtils.isEmpty(alias)) {
                        Toast.makeText(context, "指令名称不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(stringCmd)&&TextUtils.isEmpty(byteCmd)) {
                        Toast.makeText(context, "字符指令和字节指令不能同时为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cmd = new Device.Command(0,alias);
                    cmd.setStringCmd(stringCmd);
                    if(TextUtils.isEmpty(byteCmd)){
                        cmd.setByteCmd(null);
                    }else{
                        cmd.setByteCmd(ByteUtils.string2bytes(byteCmd));
                    }
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onAddDeviceClick(cmd);
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
        if (cmd != null) {
            params.height = (int) (display.getHeight() * 0.6);
        } else {
            params.height = (int) (display.getHeight() * 0.5);
        }
        window.setAttributes(params);
    }

    public interface OnButtonClickListener {
        void onConfirmClick();

        void onDeleteClick();

        void onAddDeviceClick(Device.Command cmd);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}
