package cn.diaovision.omnicontrol.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;

/**
 * Created by TaoYimin on 2017/6/7.
 */

public class PresetDialog extends Dialog {
    @BindView(R.id.button_go_back)
    ImageView buttonGoBack;
    @BindView(R.id.alias_edit)
    EditText aliasEdit;
    @BindView(R.id.dialog_button_confirm)
    Button buttonConfirm;
    @BindView(R.id.dialog_button_delete)
    Button buttonDelete;

    Context context;
    HiCamera.Preset preset;
    OnButtonClickListener onButtonClickListener;

    public PresetDialog(@NonNull Context context, HiCamera.Preset preset) {
        this(context, R.style.dialog, preset);
    }

    public PresetDialog(@NonNull Context context, @StyleRes int themeResId, HiCamera.Preset preset) {
        super(context, themeResId);
        this.context = context;
        this.preset = preset;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_preset, null);
        this.setContentView(layout);
        ButterKnife.bind(this);
        //设置端口名称编辑框默认文字
        aliasEdit.setText(preset.getName());
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preset.setName(aliasEdit.getText().toString());
                if(onButtonClickListener!=null){
                    onButtonClickListener.onConfirmClick();
                }
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onButtonClickListener!=null){
                    onButtonClickListener.onDeleteClick();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Display display = ((MainControlActivity) context).getWindowManager().getDefaultDisplay();
        params.width = (int) (display.getWidth() * 0.35);
        params.height = (int) (display.getHeight() * 0.45);
        window.setAttributes(params);
    }

    public interface OnButtonClickListener{
        void onConfirmClick();
        void onDeleteClick();
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}
