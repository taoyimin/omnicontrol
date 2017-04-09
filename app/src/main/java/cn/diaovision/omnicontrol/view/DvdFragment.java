package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.OmniControlApplication;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.conn.UdpClient;
import cn.diaovision.omnicontrol.core.message.MatrixMessage;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class DvdFragment extends BaseFragment implements DvdContract.View{

    @BindView(R.id.ip)
    AppCompatEditText ipEdit;
    @BindView(R.id.port)
    AppCompatEditText portEdit;
    @BindView(R.id.btn_setip)
    AppCompatButton btnSetIp;

    @BindView(R.id.inport_idx)
    Spinner inportSpin;
    @BindView(R.id.outport_idx)
    Spinner outportSpin;
    @BindView(R.id.btn_switch)
    AppCompatButton btnSwitch;

    @BindView(R.id.cam_porto)
    Spinner camPortoSpin;
    @BindView(R.id.cam_port)
    Spinner camPortSpin;
    @BindView(R.id.cam_baudrate)
    Spinner camBaudRateSpin;
    @BindView(R.id.cam_proto)
    Spinner camProtoSpin;
    @BindView(R.id.btn_set_cam)
    AppCompatButton btnSetCam;

    @BindView(R.id.btn_up)
    Button btnGoUp;
    @BindView(R.id.btn_down)
    Button btnGoDown;
    @BindView(R.id.btn_left)
    Button btnGoLeft;
    @BindView(R.id.btn_right)
    Button btnGoRight;
    @BindView(R.id.btn_zoomin)
    Button btnZoomin;
    @BindView(R.id.btn_zoomout)
    Button btnZoomout;
    @BindView(R.id.btn_wide)
    Button btnWide;
    @BindView(R.id.btn_narrow)
    Button btnNarrow;

    @BindView(R.id.btn_preset_save)
    Button btnPresetSave;
    @BindView(R.id.btn_preset_load)
    Button btnPresetLoad;


    int inport;
    int outport;
    int camPorto;
    int camPortoIdx;
    int camPort;
    int baudrate;
    int baudrateIdx;
    int proto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dvd, container, false);
        ButterKnife.bind(this, v);

        List<Integer> portList = new ArrayList<>();
        for (int m = 0; m < 32; m ++){
            portList.add(m);
        }

        ipEdit.setText(getApp().getAppPreferences().getString("ip", "192.168.1.1"));
        portEdit.setText(String.valueOf(getApp().getAppPreferences().getInt("port", 5000)));

        inportSpin.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.spin_simple, R.id.txt, portList));
        inport = getApp().getAppPreferences().getInt("inport", 0);
        inportSpin.setSelection(inport);

        outportSpin.setAdapter(new ArrayAdapter<Integer>(this.getContext(), R.layout.spin_simple, R.id.txt, portList));
        outport = getApp().getAppPreferences().getInt("outport", 0);
        outportSpin.setSelection(outport);

        camPortoSpin.setAdapter(new ArrayAdapter<Integer>(this.getContext(), R.layout.spin_simple, R.id.txt, portList));
        camPorto = getApp().getAppPreferences().getInt("cam_porto", 0);
        camPortoSpin.setSelection(camPorto);

        camPortSpin.setAdapter(new ArrayAdapter<Integer>(this.getContext(), R.layout.spin_simple, R.id.txt, portList));
        camPort = getApp().getAppPreferences().getInt("cam_port", 0);
        camPortSpin.setSelection(camPort);

        camBaudRateSpin.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spin_simple, R.id.txt, getResources().getStringArray(R.array.cam_baudrate)));
        baudrate = getApp().getAppPreferences().getInt("cam_baudrate", 2400);
        int baudrateIdx = getApp().getAppPreferences().getInt("cam_baudrate_spin", 1);
        camBaudRateSpin.setSelection(baudrateIdx);

        camProtoSpin.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spin_simple, R.id.txt, getResources().getStringArray(R.array.cam_proto)));
        proto = getApp().getAppPreferences().getInt("cam_proto", MatrixMessage.CAM_PROTO_PELCO_D);
        int protoIdx = getApp().getAppPreferences().getInt("cam_proto_spin", 0);
        camProtoSpin.setSelection(protoIdx);


        btnGoUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startGo(MatrixMessage.CAM_UP, 63);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopGo();
                    default:
                        break;
                }
                return false;
            }
        });

        btnGoDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startGo(MatrixMessage.CAM_DOWN, 63);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopGo();
                    default:
                        break;
                }
                return false;
            }
        });

        btnGoLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startGo(MatrixMessage.CAM_LEFT, 63);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopGo();
                    default:
                        break;
                }
                return false;
            }
        });

        btnGoRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startGo(MatrixMessage.CAM_RIGHT, 63);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopGo();
                    default:
                        break;
                }
                return false;
            }
        });

        btnZoomin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startGo(MatrixMessage.CAM_ZOOMIN, 63);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopGo();
                    default:
                        break;
                }
                return false;
            }
        });

        btnZoomout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startGo(MatrixMessage.CAM_ZOOMOUT, 63);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopGo();
                    default:
                        break;
                }
                return false;
            }
        });

        return v;
    }

    @OnClick(R.id.btn_setip)
    void setIp(){
        final String newIp = ipEdit.getText().toString();
        final int newPort = Integer.parseInt(portEdit.getText().toString());
        Flowable.just("")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        UdpClient udpClient = new UdpClient(newIp, newPort);
                        byte[] bytes = MatrixMessage.buildGetIdMessage(0).toBytes();
                        byte[] recv = udpClient.send(bytes, bytes.length);
                        if (recv.length > 0){
                            return "success";
                        }
                        else {
                            return "failed";
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (s == "success"){
                    getApp().getMediaMatrix().setReachable(true);
                    getApp().getMediaMatrix().setId(1);
                    getApp().getMediaMatrix().setIp(newIp, newPort);
                    getApp().getMediaMatrix().setReachable(true);
                    Toast.makeText(getContext(), "IP set", Toast.LENGTH_SHORT).show();
                    getApp().saveAppPreference("ip", newIp);
                    getApp().saveAppPreference("port", newPort);
                }
                else if (s == "failed"){
                    getApp().getMediaMatrix().setReachable(false);
                    Toast.makeText(getContext(), "Matrix unreachable", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @OnClick(R.id.btn_switch)
    void switchChannel(){
        if (getApp().getMediaMatrix().isReachable()){

            final int in = inportSpin.getSelectedItemPosition();
            final int[] outs = {outportSpin.getSelectedItemPosition()};

            RxExecutor.getInstance().post(new RxReq() {
                @Override
                public RxMessage request() {
                    int res = getApp().getMediaMatrix().switchVideo(in, outs);
                    if (res >= 0) {
                        return new RxMessage("success");
                    }
                    else {
                        return new RxMessage("failed");
                    }
                }
            }, new Consumer<RxMessage>() {
                @Override
                public void accept(RxMessage msg) throws Exception {
                    if (msg.what.equals("success")){
                        Toast.makeText(getContext(), "Switch succeed", Toast.LENGTH_SHORT).show();
                    }
                    else if (msg.what.equals("failed")){
                        Toast.makeText(getContext(), "Switch failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
        }
    }

    @OnClick(R.id.btn_set_cam)
    void setCam(){
        int camportoIdx = camPortoSpin.getSelectedItemPosition();
        int camportIdx = camPortSpin.getSelectedItemPosition();

        int baudrateIdx = camBaudRateSpin.getSelectedItemPosition();
        int protoIdx = camProtoSpin.getSelectedItemPosition();

        int p;
        switch (protoIdx){
            case 0:
                p = MatrixMessage.CAM_PROTO_PELCO_D;
                break;
            case 1:
                p = MatrixMessage.CAM_PROTO_PELCO_P;
                break;
            case 2:
                p = MatrixMessage.CAM_PROTO_VISCA;
                break;
            default:
                return;
        }

        baudrate = Integer.parseInt(getApp().getResources().getStringArray(R.array.cam_baudrate)[baudrateIdx]);
        if (baudrate < 0 || baudrate > 115200)
            return;

        HiCamera camera = new HiCamera(camportoIdx, camportIdx, baudrate, p);
        getApp().getMediaMatrix().getCameras().put(camportoIdx, camera);
        getApp().saveAppPreference("cam_porto", camportoIdx);
        getApp().saveAppPreference("cam_port", camportIdx);
        getApp().saveAppPreference("cam_baudrate", baudrate);
        getApp().saveAppPreference("cam_baudrate_spin", baudrateIdx);
        getApp().saveAppPreference("cam_proto", p);
        getApp().saveAppPreference("cam_proto_spin", protoIdx);
    }

    void startGo(final int cmd, final int speed){
        final MediaMatrix mediaMatrix = getApp().getMediaMatrix();
        if (mediaMatrix.isReachable()){
            Flowable.just("")
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String s) throws Exception {
                            int camPorto = getApp().getAppPreferences().getInt("cam_porto", 0);
                            HiCamera camera = mediaMatrix.getCameras().get(camPorto);
                            if (camera != null){
                                int res = mediaMatrix.startCameraGo(camPorto, cmd, speed);
                                if (res >= 0){
                                    return "success";
                                }
                                else {
                                    return "failed";
                                }
                            }
                            else {
                                return "none";
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (s == "success"){
                            }
                            else if (s == "failed"){
                            }
                            else if (s == "none"){
                            }
                        }
                    });
        }
    }


    void stopGo(){
        final MediaMatrix mediaMatrix = getApp().getMediaMatrix();
        if (mediaMatrix.isReachable()){
            Flowable.just("")
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String s) throws Exception {
                            int camPorto = getApp().getAppPreferences().getInt("cam_porto", 0);
                            HiCamera camera = mediaMatrix.getCameras().get(camPorto);
                            if (camera != null){
                                int res = mediaMatrix.stopCameraGo(camPorto);
                                if (res >= 0){
                                    return "success";
                                }
                                else {
                                    return "failed";
                                }
                            }
                            else {
                                return "none";
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (s == "success"){
                            }
                            else if (s == "failed"){
                            }
                            else if (s == "none"){
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.btn_preset_save)
    void savePreset(){
        final MediaMatrix mediaMatrix = getApp().getMediaMatrix();
        if (mediaMatrix.isReachable()){
            Flowable.just("")
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String s) throws Exception {
                            int camPorto = getApp().getAppPreferences().getInt("cam_porto", 0);
                            HiCamera camera = mediaMatrix.getCameras().get(camPorto);
                            if (camera != null){
                                int res = mediaMatrix.setCameraPreset(camPorto, 1, "预置");
                                if (res >= 0){
                                    getApp().saveAppPreference("cam_preset_porto", camPorto);
                                    return "success";
                                }
                                else {
                                    return "failed";
                                }
                            }
                            else{
                                return "none";
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {

                        }
                    });

        }
    }

    @OnClick(R.id.btn_preset_load)
    void loadPreset(){
        final MediaMatrix mediaMatrix = getApp().getMediaMatrix();
        if (mediaMatrix.isReachable()){
            Flowable.just("")
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String s) throws Exception {
                            int camPorto = getApp().getAppPreferences().getInt("cam_porto", 0);
                            HiCamera camera = mediaMatrix.getCameras().get(camPorto);
                            if (camera != null){
                                int res = mediaMatrix.loadCameraPreset(camPorto, 1);
                                if (res >= 0){
                                    return "success";
                                }
                                else {
                                    return "failed";
                                }
                            }
                            else {
                                return "none";
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {

                        }
                    });
        }
    }

    @Override
    public void bindPresenter() {

    }
}
