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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.splicer.Scene;

/**
 * Created by TaoYimin on 2017/6/7.
 */

public class SceneDialog extends Dialog {
    @BindView(R.id.button_go_back)
    ImageView buttonGoBack;
    @BindView(R.id.alias_edit)
    EditText aliasEdit;
    @BindView(R.id.dialog_button_confirm)
    Button buttonConfirm;
    @BindView(R.id.dialog_button_cancel)
    Button buttonCancel;

    Context context;
    Scene scene;
    OnButtonClickListener onButtonClickListener;

    public SceneDialog(@NonNull Context context, Scene scene) {
        this(context, R.style.dialog, scene);
    }

    public SceneDialog(@NonNull Context context, @StyleRes int themeResId, Scene scene) {
        super(context, themeResId);
        this.context = context;
        this.scene = scene;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_scene, null);
        this.setContentView(layout);
        ButterKnife.bind(this);
        //设置场景名称编辑框默认文字
        aliasEdit.setText(scene.getName());
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(aliasEdit.getText().toString())) {
                    scene.setName(aliasEdit.getText().toString());
                }
                if(onButtonClickListener!=null){
                    onButtonClickListener.onConfirmClick();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onButtonClickListener!=null){
                    onButtonClickListener.onCancelClick();
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
        void onCancelClick();
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public String getEditText(){
        return aliasEdit.getText().toString();
    }
}
