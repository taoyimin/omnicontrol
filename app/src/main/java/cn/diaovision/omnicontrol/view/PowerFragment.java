package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;

/* *
 * 开关控制页面
 * Created by liulingfeng on 2017/2/24.
 * */

public class PowerFragment  extends BaseFragment implements PowerContract.View{

//    @BindView(R.id.btn_preset_save)
//    Button btnX;

    PowerContract.Presenter presenter;

    //TODO: 内部生成presenter，不通过外部指定
    @Override
    public void setPresenter() {
        this.presenter = new PowerPresenter(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //TODO: config view and presenter here
//        btnX.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                presenter.start();
//                presenter.func();
//
//            }
//        });



    }


    /* *********************************
     * presenter-view interactions
     * *********************************/
    @Override
    public void changeTitle() {

    }
}
