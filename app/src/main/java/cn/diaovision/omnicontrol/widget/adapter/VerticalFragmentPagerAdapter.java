package cn.diaovision.omnicontrol.widget.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.diaovision.omnicontrol.view.ViewPagerFragment;

/**
 * Created by TaoYimin on 2017/5/22.
 */
@Deprecated
public class VerticalFragmentPagerAdapter extends FragmentPagerAdapter {
    List<ViewPagerFragment> list;

    public VerticalFragmentPagerAdapter(List<ViewPagerFragment> list, FragmentManager fm) {
        super(fm);
        this.list = list;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return list.size();
    }
}
