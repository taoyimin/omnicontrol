package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.widget.adapter.CustomSpinnerAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class ConfigFragment extends BaseFragment implements ConfigContract.View{
    @BindView(R.id.config_type)
    Spinner typeSpinner;

    CustomSpinnerAdapter adapter;
    ConfigContract.Presenter presenter;

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
        String[] types = getResources().getStringArray(R.array.config_type);
        adapter = new CustomSpinnerAdapter(getContext(),  types,null);
        typeSpinner.setAdapter(adapter);
    }

    @Override
    public void changeTitle() {

    }

    @Override
    public void bindPresenter() {
        presenter = new ConfigPresenter(this);
    }
}
