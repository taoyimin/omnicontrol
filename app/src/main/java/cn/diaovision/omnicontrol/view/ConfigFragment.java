package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;

import static cn.diaovision.omnicontrol.MainControlActivity.cfg;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class ConfigFragment extends BaseFragment implements ConfigContract.View {
    @BindView(R.id.matrix_ip)
    EditText matrixIp;
    @BindView(R.id.matrix_port)
    EditText matrixPort;
    @BindView(R.id.matrix_id)
    EditText matrixId;
    @BindView(R.id.config_username)
    EditText userName;
    @BindView(R.id.config_password)
    EditText passWord;
    @BindView(R.id.config_commit)
    Button commitButton;
    @BindView(R.id.config_reset)
    Button resetButton;

    ConfigPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_config, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        matrixIp.setText(cfg.getMatrixIp());
        matrixPort.setText(cfg.getMatrixUdpIpPort()+"");
        matrixId.setText(cfg.getMatrixId()+"");
        userName.setText(cfg.getMainName());
        passWord.setText(cfg.getMainPasswd());
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip=matrixIp.getText().toString();
                String port=matrixPort.getText().toString();
                String id=matrixId.getText().toString();
                String name=userName.getText().toString();
                String password=passWord.getText().toString();
                if(TextUtils.isEmpty(ip)||TextUtils.isEmpty(port)||TextUtils.isEmpty(id)||TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
                    showToast("配置项不能有空白项！");
                }else{
                    presenter.config(ip,port,id,name,password);
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matrixIp.setText("");
                matrixPort.setText("");
                matrixId.setText("");
                userName.setText("");
                passWord.setText("");
            }
        });
    }

    @Override
    public void bindPresenter() {
        presenter = new ConfigPresenter(this);
    }

    @Override
    public void showToast(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }
}
