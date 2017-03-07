package cn.diaovision.omnicontrol.widget;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.reflect.Field;

/**
 * Created by liulingfeng on 2017/3/6.
 */

public class AttributeAnimation extends Animation {
    View view;
    Field field;
    float fromFloatVal = 0;
    float toFloatVal = 0;
    int fromIntVal = 0;
    int toIntVal = 0;
    int type; //1. float val transit, 2. int val transit

    public static AttributeAnimation ofFloat(View view, String property, float fromVal, float toVal){
        return new AttributeAnimation(view, property, fromVal, toVal);
    }

    public static AttributeAnimation ofInt(View view, String property, int fromVal, int toVal){
        return new AttributeAnimation(view, property, fromVal, toVal);
    }

    private AttributeAnimation(View view, String property, float fromVal, float toVal){
        try {
            //reflect the field
            this.view = view;
            field = view.getClass().getField(property);
            this.fromFloatVal = fromVal;
            this.toFloatVal = toVal;
            type = 1;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private AttributeAnimation(View view, String property, int fromVal, int toVal) {
        try {
            //reflect the field
            this.view = view;
            field = view.getClass().getField(property);
            this.fromIntVal = fromVal;
            this.toIntVal = toVal;
            type = 2;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
            try {
                if (type == 1) {
                    field.set(view, fromFloatVal + interpolatedTime * (toFloatVal - fromFloatVal));
                    view.postInvalidate();
                }
                else if (type == 2) {
                    field.set(view, (int) (fromIntVal + interpolatedTime * (toIntVal - fromIntVal)));
                    view.postInvalidate();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
    }
}
