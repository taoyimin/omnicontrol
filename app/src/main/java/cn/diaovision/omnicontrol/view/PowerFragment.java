package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.widget.DirectionPad;

/* *
 * 开关控制页面
 * Created by liulingfeng on 2017/2/24.
 * */

public class PowerFragment  extends BaseFragment implements PowerContract.View{

    @BindView(R.id.pad_direction)
    DirectionPad padDirection;

    @BindView(R.id.viewstub)
    View viewStub;

    @BindView(R.id.x)
    TextView xV;

    @BindView(R.id.y)
    TextView yV;

    PowerContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_power, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    //TODO: 内部生成presenter，不通过外部指定
    @Override
    public void bindPresenter() {
        this.presenter = new PowerPresenter(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        padDirection.setOnMoveListener(new DirectionPad.OnMoveListener() {
            @Override
            public void onMove(int deg, int velo) {
                xV.setText("deg = " + deg);
                yV.setText("velo = " + velo);
            }
        });
    }

    /* *********************************
     * presenter-view interactions
     * *********************************/
}
