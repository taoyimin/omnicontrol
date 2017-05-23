package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.view.ViewPagerFragment;
import cn.diaovision.omnicontrol.widget.adapter.VerticalFragmentPagerAdapter;

/**
 * Created by TaoYimin on 2017/5/23.
 */

public class AssistDrawerLayout extends DrawerLayout{
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer)
    View drawer;
    @BindView(R.id.view_pager)
    VerticalViewPager viewPager;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    VerticalFragmentPagerAdapter adapter;

    public AssistDrawerLayout(Context context) {
        this(context,null);
    }

    public AssistDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AssistDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.layout_drawer, this);
        ButterKnife.bind(this, view);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        List<ViewPagerFragment> list=new ArrayList<>();
        list.add(new ViewPagerFragment());
        list.add(new ViewPagerFragment());
        list.add(new ViewPagerFragment());
        list.add(new ViewPagerFragment());
        adapter=new VerticalFragmentPagerAdapter(list,((MainControlActivity)context).getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_btn1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.radio_btn2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.radio_btn3:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.radio_btn4:
                        viewPager.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton)radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void openDrawer(){
        if(!drawerLayout.isDrawerOpen(drawer)){
            drawerLayout.openDrawer(drawer);
        }
    }

    public void closeDrawer(){
        if(drawerLayout.isDrawerOpen(drawer)){
            drawerLayout.closeDrawer(drawer);
        }
    }
}
