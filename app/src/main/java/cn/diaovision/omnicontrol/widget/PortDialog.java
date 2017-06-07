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
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.adapter.AdapterForSpinner;

/**
 * Created by TaoYimin on 2017/6/7.
 */

public class PortDialog extends Dialog {
    @BindView(R.id.category_spinner)
    Spinner categorySpinner;
    @BindView(R.id.attribute_spinner)
    Spinner attributeSpinner;
    @BindView(R.id.button_go_back)
    ImageView buttonGoBack;
    @BindView(R.id.alias_edit)
    EditText aliasEdit;
    @BindView(R.id.dialog_button_confirm)
    Button buttonConfirm;

    Context context;
    Port port;
    AdapterForSpinner categoryAdapter;
    AdapterForSpinner attributeAdapter;
    boolean flag;

    public PortDialog(@NonNull Context context, Port port) {
        this(context, R.style.dialog, port);
    }

    public PortDialog(@NonNull Context context, @StyleRes int themeResId, Port port) {
        super(context, themeResId);
        this.context = context;
        this.port = port;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_port, null);
        this.setContentView(layout);
        ButterKnife.bind(this);
        flag=false;
        //初始化图标种类spinner
        if(port.dir==Port.DIR_IN){
            String[] arrays=context.getResources().getStringArray(R.array.input_port_category);
            int[] images=new int[]{R.mipmap.camera_small,R.mipmap.desktop_small,R.mipmap.video_small,R.mipmap.output_return_small};
            categoryAdapter=new AdapterForSpinner(context,arrays,images);
            categorySpinner.setAdapter(categoryAdapter);
            switch (port.category){
                case Port.CATEGORY_CAMERA:
                    categorySpinner.setSelection(0,true);
                    break;
                case Port.CATEGORY_DESKTOP:
                    categorySpinner.setSelection(1,true);
                    break;
                case Port.CATEGORY_VIDEO:
                    categorySpinner.setSelection(2,true);
                    break;
                case Port.CATEGORY_OUTPUT_RETURN:
                    categorySpinner.setSelection(3,true);
                    break;
                default:
                    break;
            }
        }else if(port.dir==Port.DIR_OUT){
            String[] arrays=context.getResources().getStringArray(R.array.output_port_category);
            int[] images=new int[]{R.mipmap.projector_small,R.mipmap.display_screen_small,R.mipmap.ip_small,R.mipmap.computer_small,R.mipmap.tv_small,R.mipmap.conference_small};
            categoryAdapter=new AdapterForSpinner(context,arrays,images);
            categorySpinner.setAdapter(categoryAdapter);
            switch (port.category){
                case Port.CATEGORY_PROJECTOR:
                    categorySpinner.setSelection(0,true);
                    break;
                case Port.CATEGORY_DISPLAY:
                    categorySpinner.setSelection(1,true);
                    break;
                case Port.CATEGORY_IP:
                    categorySpinner.setSelection(2,true);
                    break;
                case Port.CATEGORY_COMPUTER:
                    categorySpinner.setSelection(3,true);
                    break;
                case Port.CATEGORY_TV:
                    categorySpinner.setSelection(4,true);
                    break;
                case Port.CATEGORY_CONFERENCE:
                    categorySpinner.setSelection(5,true);
                    break;
                default:
                    break;
            }
        }
        //初始化附加属性spinner
        attributeAdapter=new AdapterForSpinner(context,new String[3],new int[0]);
        attributeSpinner.setAdapter(attributeAdapter);
        //设置端口名称编辑框默认文字
        aliasEdit.setText(port.alias);
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(port.dir==Port.DIR_IN){
                    switch (categorySpinner.getSelectedItemPosition()){
                        case 0:
                            port.category=Port.CATEGORY_CAMERA;
                            break;
                        case 1:
                            port.category=Port.CATEGORY_DESKTOP;
                            break;
                        case 2:
                            port.category=Port.CATEGORY_VIDEO;
                            break;
                        case 3:
                            port.category=Port.CATEGORY_OUTPUT_RETURN;
                            break;
                        default:
                            break;
                    }
                }else if(port.dir==port.DIR_OUT){
                    switch (categorySpinner.getSelectedItemPosition()){
                        case 0:
                            port.category=Port.CATEGORY_PROJECTOR;
                            break;
                        case 1:
                            port.category=Port.CATEGORY_DISPLAY;
                            break;
                        case 2:
                            port.category=Port.CATEGORY_IP;
                            break;
                        case 3:
                            port.category=Port.CATEGORY_COMPUTER;
                            break;
                        case 4:
                            port.category=Port.CATEGORY_TV;
                            break;
                        case 5:
                            port.category=Port.CATEGORY_CONFERENCE;
                            break;
                        default:
                            break;
                    }
                }
                port.alias=aliasEdit.getText().toString();
                flag=true;
                dismiss();
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
        params.height = (int) (display.getHeight() * 0.55);
        window.setAttributes(params);
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
