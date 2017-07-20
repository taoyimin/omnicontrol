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
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.widget.adapter.CustomSpinnerAdapter;

/**
 * Created by TaoYimin on 2017/7/19.
 */

public class CameraDialog extends Dialog {
    @BindView(R.id.baudrate_spinner)
    Spinner baudrateSpinner;
    @BindView(R.id.address_spinner)
    Spinner addressSpinner;
    @BindView(R.id.proto_spinner)
    Spinner protoSpinner;
    @BindView(R.id.button_go_back)
    ImageView buttonGoBack;
    @BindView(R.id.alias_edit)
    EditText aliasEdit;
    @BindView(R.id.dialog_button_confirm)
    Button buttonConfirm;

    Context context;
    HiCamera camera;
    CustomSpinnerAdapter baudrateAdapter;
    CustomSpinnerAdapter addressAdapter;
    CustomSpinnerAdapter protoAdapter;

    OnButtonClickListener onButtonClickListener;

    public CameraDialog(@NonNull Context context, HiCamera camera) {
        this(context, R.style.dialog, camera);
    }

    public CameraDialog(@NonNull Context context, @StyleRes int themeResId, HiCamera camera) {
        super(context, themeResId);
        this.context = context;
        this.camera = camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_camera, null);
        this.setContentView(layout);
        ButterKnife.bind(this);
        //初始化相机地址Spinner
        final String[] address=context.getResources().getStringArray(R.array.cam_address);
        addressAdapter=new CustomSpinnerAdapter(context,address,null);
        addressSpinner.setAdapter(addressAdapter);
        switch (camera.getIdx()){
            case 0:
                addressSpinner.setSelection(0,true);
                break;
            case 1:
                addressSpinner.setSelection(1,true);
                break;
            case 2:
                addressSpinner.setSelection(2,true);
                break;
            case 3:
                addressSpinner.setSelection(3,true);
                break;
            case 4:
                addressSpinner.setSelection(4,true);
                break;
            case 5:
                addressSpinner.setSelection(5,true);
                break;
            case 6:
                addressSpinner.setSelection(6,true);
                break;
            case 7:
                addressSpinner.setSelection(7,true);
                break;
            default:
                break;
        }
        //初始化波特率Spinner
        final String[] baudrates = context.getResources().getStringArray(R.array.cam_baudrate);
        baudrateAdapter = new CustomSpinnerAdapter(context, baudrates, null);
        baudrateSpinner.setAdapter(baudrateAdapter);
        switch (camera.getBaudrate()) {
            case 1200:
                baudrateSpinner.setSelection(0,true);
                break;
            case 2400:
                baudrateSpinner.setSelection(1,true);
                break;
            case 4800:
                baudrateSpinner.setSelection(2,true);
                break;
            case 9600:
                baudrateSpinner.setSelection(3,true);
                break;
            case 19200:
                baudrateSpinner.setSelection(4,true);
                break;
            case 38400:
                baudrateSpinner.setSelection(5,true);
                break;
            case 57600:
                baudrateSpinner.setSelection(6,true);
                break;
            case 115200:
                baudrateSpinner.setSelection(7,true);
                break;
            default:
                break;
        }
        //初始化相机协议Spinner
        final String[] protos=context.getResources().getStringArray(R.array.cam_proto);
        protoAdapter=new CustomSpinnerAdapter(context,protos,null);
        protoSpinner.setAdapter(protoAdapter);
        switch (camera.getProto()){
            case HiCamera.PROTO_FELICA_D:
                protoSpinner.setSelection(0,true);
                break;
            case HiCamera.PROTO_FELICA_A:
                protoSpinner.setSelection(1,true);
                break;
            case HiCamera.PROTO_PILSA:
                protoSpinner.setSelection(2,true);
                break;
            default:
                break;
        }
        //设置端口名称编辑框默认文字
        aliasEdit.setText(camera.getAlias());
        //返回键的点击事件
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //确认键的点击事件
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.setAlias(aliasEdit.getText().toString());
                camera.setBaudrate(Integer.parseInt(baudrates[baudrateSpinner.getSelectedItemPosition()]));
                camera.setIdx(Integer.parseInt(address[addressSpinner.getSelectedItemPosition()]));
                String proto=protos[protoSpinner.getSelectedItemPosition()];
                if(proto.equals("PELCO-D")){
                    camera.setProto(HiCamera.PROTO_FELICA_D);
                }else if(proto.equals("PELCO-P")){
                    camera.setProto(HiCamera.PROTO_FELICA_A);
                }else if(proto.equals("VISCA")){
                    camera.setProto(HiCamera.PROTO_PILSA);
                }
                if(onButtonClickListener!=null){
                    onButtonClickListener.onConfirmClick();
                }
            }
        });
        //初始化图标种类spinner
/*        if(port.dir==Port.DIR_IN){
            String[] arrays=context.getResources().getStringArray(R.array.input_port_category);
            int[] images=new int[]{R.mipmap.camera_small,R.mipmap.desktop_small,R.mipmap.video_small,R.mipmap.output_return_small};
            categoryAdapter=new CustomSpinnerAdapter(context,arrays,images);
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
            categoryAdapter=new CustomSpinnerAdapter(context,arrays,images);
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
        attributeAdapter=new CustomSpinnerAdapter(context,new String[3],new int[0]);
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
                if(onButtonClickListener!=null){
                    onButtonClickListener.onConfirmClick();
                }
            }
        });*/
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

    public interface OnButtonClickListener {
        void onConfirmClick();
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}
