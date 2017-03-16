package cn.diaovision.omnicontrol.widget;

import android.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liulingfeng on 2017/3/9.
 */

public class RadioGroupViewLiasoner {
    PortRadioGroupView pViewA;
    PortRadioGroupView pViewB;

    Set<Pair<Integer, Integer>> pairSet;

    public RadioGroupViewLiasoner(PortRadioGroupView pViewA, PortRadioGroupView pViewB) {
        this.pViewA = pViewA;
        this.pViewB = pViewB;
        pairSet = new HashSet<>();

        this.pViewA.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                for (Pair<Integer, Integer> p : pairSet){
                    if (p.first == pos){
                        RadioGroupViewLiasoner.this.pViewB.select(p.second);
                    }
                    else if (p.second == pos){

                    }
                }
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        pViewB.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {

            }

            @Override
            public void onUnselected(int pos) {

            }
        });
    }

    public void liason(int posA, int posB){
        Pair<Integer, Integer> p = new Pair<>(posA, posB);
        if (!pairSet.contains(p)){
            pairSet.add(p);
        }
    }

    public void unliason(int posA, int posB){
        Pair<Integer, Integer> p = new Pair<>(posA, posB);
        pairSet.remove(p);
    }
}
