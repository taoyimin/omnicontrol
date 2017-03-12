package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.widget.VolumeBar;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class AudioFragment extends Fragment{


    @BindView(R.id.bar)
    VolumeBar bar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_audio, container, false);
        ButterKnife.bind(this, v);

        bar.setProgress(50);
        return v;
    }

}
