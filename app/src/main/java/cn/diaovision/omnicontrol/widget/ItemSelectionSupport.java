package cn.diaovision.omnicontrol.widget;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;

import java.util.ArrayList;
import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.util.PortHelper;

public class ItemSelectionSupport {
    public static final int INVALID_POSITION = -1;

    public enum ChoiceMode {
        NONE,
        SINGLE,
        MULTIPLE
    }

    public enum ChoiceColor{
        GREEN,
        YELLOW,
        BLUE,
        RED
    }

    private final RecyclerView mRecyclerView;
    private OnAllSelectedListener mAllSelectedListener;
    private OnItemStatueListener mOnItemStatueListener;
    private int checkedCount=0;
    private List<Integer> selects;

    private ChoiceMode mChoiceMode = ChoiceMode.NONE;
    private ChoiceColor choiceColor;
    private int choiceBadge=-1;
    //private CheckedStates mCheckedStates;
    private int lastPosition=-1;

    public ItemSelectionSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

/*    public int getCheckedItemCount() {
        return checkedCount;
    }

    public boolean isItemChecked(int position) {
        if (mChoiceMode != ChoiceMode.NONE && mCheckedStates != null) {
            return mCheckedStates.get(position);
        }

        return false;
    }

    public int getCheckedItemPosition() {
        if (mChoiceMode == ChoiceMode.SINGLE && mCheckedStates != null && checkedCount == 1) {
            return mCheckedStates.keyAt(0);
        }

        return INVALID_POSITION;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        if (mChoiceMode != ChoiceMode.NONE) {
            return mCheckedStates;
        }

        return null;
    }

    public void setItemChecked(int position, boolean checked){
        if(checked) {
            if(!mCheckedStates.get(position,false)) {
                //之前为未选中状态
                checkedCount++;
                if(mOnItemStatueListener!=null){
                    mOnItemStatueListener.onSelectCountChange(checkedCount);
                }
            }
        }else{
            if(mCheckedStates.get(position,false)) {
                //之前为选中状态
                checkedCount--;
                if(mOnItemStatueListener!=null){
                    mOnItemStatueListener.onSelectCountChange(checkedCount);
                }
            }
        }
        mCheckedStates.put(position,checked);
    }

    public void clearChoices() {
        if (mCheckedStates != null) {
            mCheckedStates.clear();
            checkedCount=0;
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onSelectCountChange(checkedCount);
            }
        }
    }

    public ChoiceMode getChoiceMode() {
        return mChoiceMode;
    }

    public void setChoiceModeMultiple(OnAllSelectedListener listener) {
        mAllSelectedListener = listener;
        setChoiceMode(ChoiceMode.MULTIPLE);
    }

    public void setChoiceMode(ChoiceMode choiceMode) {
        if (mChoiceMode == choiceMode) {
            return;
        }

        mChoiceMode = choiceMode;

        if (mChoiceMode != ChoiceMode.NONE) {
            if (mCheckedStates == null) {
                mCheckedStates = new CheckedStates();
            }
        }
    }

    private static class CheckedStates extends SparseBooleanArray implements Parcelable {
        private static final int FALSE = 0;
        private static final int TRUE = 1;

        public CheckedStates() {
            super();
        }

        private CheckedStates(Parcel in) {
            final int size = in.readInt();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    final int key = in.readInt();
                    final boolean value = (in.readInt() == TRUE);
                    put(key, value);
                }
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            final int size = size();
            parcel.writeInt(size);

            for (int i = 0; i < size; i++) {
                parcel.writeInt(keyAt(i));
                parcel.writeInt(valueAt(i) ? TRUE : FALSE);
            }
        }

        public static final Creator<CheckedStates> CREATOR
                = new Creator<CheckedStates>() {
            @Override
            public CheckedStates createFromParcel(Parcel in) {
                return new CheckedStates(in);
            }

            @Override
            public CheckedStates[] newArray(int size) {
                return new CheckedStates[size];
            }
        };
    }

    public interface OnAllSelectedListener {
        void onChanged(boolean allSelected);
    }

    public void itemClick(int position){
        final Adapter adapter = mRecyclerView.getAdapter();
        if (mChoiceMode == ChoiceMode.MULTIPLE) {
            //当前处于多选模式
            boolean checked = !mCheckedStates.get(position, false);
            //设置与之前相反的状态
            mCheckedStates.put(position, checked);
            adapter.notifyItemChanged(position);
            if(checked){
                //选中
                checkedCount++;
            }else{
                //取消选中
                checkedCount--;
            }
            lastPosition=-1;
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onSelectCountChange(checkedCount);
            }
        } else if (mChoiceMode == ChoiceMode.SINGLE) {
            //当前处于单选模式
            if(checkedCount>1){
                //之前有多个item被选中，先全部清空
                mCheckedStates.clear();
                adapter.notifyDataSetChanged();
                //选中当前
                mCheckedStates.put(position,true);
                adapter.notifyItemChanged(position);
                //记录本次选中的position
                lastPosition = position;
                checkedCount=1;
                if(mOnItemStatueListener!=null){
                    mOnItemStatueListener.onSelect(position);
                    mOnItemStatueListener.onSelectCountChange(checkedCount);
                }
            }else {
                //之前有0个或1个item被选中
                boolean checked = !mCheckedStates.get(position, false);
                if (checked) {
                    //之前为未选中状态，本次操作为选中当前
                    mCheckedStates.clear();
                    mCheckedStates.put(position, true);
                    if (lastPosition != -1) {
                        //刷新之前选中的item
                        adapter.notifyItemChanged(lastPosition);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                    //刷新现在选中的item
                    adapter.notifyItemChanged(position);
                    lastPosition = position;
                    checkedCount=1;
                    if(mOnItemStatueListener!=null){
                        mOnItemStatueListener.onSelect(position);
                        mOnItemStatueListener.onSelectCountChange(checkedCount);
                    }
                } else {
                    //之前为选中状态，本次操作为取消选中
                    mCheckedStates.clear();
                    adapter.notifyItemChanged(position);
                    lastPosition = -1;
                    checkedCount=0;
                    if(mOnItemStatueListener!=null){
                        mOnItemStatueListener.onUnSelect(position);
                        mOnItemStatueListener.onSelectCountChange(checkedCount);
                    }
                }
            }
        }
    }

    public void itemLongClick(int position){
        final Adapter adapter = mRecyclerView.getAdapter();
        if(mChoiceMode== ChoiceMode.SINGLE){
            //如果当前为单选模式，则进入多选模式
            mChoiceMode= ChoiceMode.MULTIPLE;
            selects.clear();
            adapter.notifyDataSetChanged();
            if(position!=-1) {
                //长按进入编辑模式
                mCheckedStates.put(position, true);
                adapter.notifyItemChanged(position);
                checkedCount = 1;
            }else{
                //按键进入编辑模式
                checkedCount=0;
            }
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onSelectCountChange(checkedCount);
            }
        }else if(mChoiceMode== ChoiceMode.MULTIPLE){
            //如果当前为多选模式，长按则弹出对话框
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onPopupDialog(position);
            }
        }
    }

    public interface OnItemStatueListener{
        void onSelect(int position);
        void onUnSelect(int position);
        void onPopupDialog(int position);
        void onSelectCountChange(int count);
    }

    public void setOnItemStatueListener(OnItemStatueListener mOnItemStatueListener) {
        this.mOnItemStatueListener = mOnItemStatueListener;
    }

    public int getLastPosition() {
        return lastPosition;
    }*/

    public int getCheckedItemCount() {
        return selects.size();
    }

    public boolean isItemChecked(int position) {
        if (mChoiceMode != ChoiceMode.NONE && selects != null) {
            return selects.contains(position);
        }

        return false;
    }

    public int getCheckedItemPosition() {
        if (mChoiceMode == ChoiceMode.SINGLE && selects != null && selects.size() == 1) {
            return selects.get(0);
        }

        return INVALID_POSITION;
    }

    public List<Integer> getCheckedItemPositions() {
        if (mChoiceMode != ChoiceMode.NONE) {
            return selects;
        }

        return null;
    }

    public void setItemChecked(int position, boolean checked){
        if(checked) {
            if(!selects.contains(position)) {
                //之前为未选中状态
                selects.add(position);
                if(mOnItemStatueListener!=null){
                    mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
                }
            }
        }else{
            if(selects.contains(position)) {
                //之前为选中状态
                selects.remove(selects.indexOf(position));
                if(mOnItemStatueListener!=null){
                    mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
                }
            }
        }
    }

    public void clearChoices() {
        if (selects != null) {
            selects.clear();
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
            }
        }
    }

    public ChoiceColor getChoiceColor() {
        return choiceColor;
    }

    public void setChoiceColor(ChoiceColor choiceColor) {
        this.choiceColor = choiceColor;
    }

    public int getChoiceBadge() {
        return choiceBadge;
    }

    public void setChoiceBadge(int choiceBadge) {
        this.choiceBadge = choiceBadge;
    }

    public ChoiceMode getChoiceMode() {
        return mChoiceMode;
    }

    public void setChoiceModeMultiple(OnAllSelectedListener listener) {
        mAllSelectedListener = listener;
        setChoiceMode(ChoiceMode.MULTIPLE);
    }

    public void setChoiceMode(ChoiceMode choiceMode) {
        if (mChoiceMode == choiceMode) {
            return;
        }

        mChoiceMode = choiceMode;

        if (mChoiceMode != ChoiceMode.NONE) {
            if (selects == null) {
                selects = new ArrayList<>();
            }
        }
    }

    public interface OnAllSelectedListener {
        void onChanged(boolean allSelected);
    }

    public void itemClick(int position){
        final Adapter adapter = mRecyclerView.getAdapter();
        if (mChoiceMode == ChoiceMode.MULTIPLE) {
            //当前处于多选模式
            boolean checked = !selects.contains(position);
            if(checked){
                //选中
                selects.add(position);
                mOnItemStatueListener.onSelectMultiple(position);
                lastPosition=position;
            }else{
                //取消选中
                selects.remove(selects.indexOf(position));
                mOnItemStatueListener.onUnSelectMultiple(position);
                lastPosition=-1;
            }
            adapter.notifyItemChanged(position);
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
            }
        } else if (mChoiceMode == ChoiceMode.SINGLE) {
            //当前处于单选模式
            if(getCheckedItemCount()>1){
                //之前有多个item被选中，先全部清空
                if(selects.contains(position)){
                    selects.clear();
                    adapter.notifyDataSetChanged();
                    lastPosition = -1;
                    if(mOnItemStatueListener!=null){
                        mOnItemStatueListener.onUnSelectSingle(position);
                        mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
                    }
                }else{
                    selects.clear();
                    adapter.notifyDataSetChanged();
                    //选中当前
                    selects.add(position);
                    adapter.notifyItemChanged(position);
                    //记录本次选中的position
                    lastPosition = position;
                    if(mOnItemStatueListener!=null){
                        mOnItemStatueListener.onSelectSingle(position);
                        mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
                    }
                }

            }else {
                //之前有0个或1个item被选中
                boolean checked = !selects.contains(position);
                if (checked) {
                    //之前为未选中状态，本次操作为选中当前
                    selects.clear();
                    selects.add(position);
                    if (lastPosition != -1) {
                        //刷新之前选中的item
                        adapter.notifyItemChanged(lastPosition);
                    }else{
                        //adapter.notifyDataSetChanged();
                    }
                    //刷新现在选中的item
                    adapter.notifyItemChanged(position);
                    lastPosition = position;
                    if(mOnItemStatueListener!=null){
                        mOnItemStatueListener.onSelectSingle(position);
                        mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
                    }
                } else {
                    //之前为选中状态，本次操作为取消选中
                    selects.clear();
                    adapter.notifyItemChanged(position);
                    lastPosition = -1;
                    if(mOnItemStatueListener!=null){
                        mOnItemStatueListener.onUnSelectSingle(position);
                        mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
                    }
                }
            }
        }
    }

    public void itemLongClick(int position){
        final Adapter adapter = mRecyclerView.getAdapter();
        if(mChoiceMode== ChoiceMode.SINGLE){
            //如果当前为单选模式，则进入多选模式
            mChoiceMode= ChoiceMode.MULTIPLE;
            selects.clear();
            adapter.notifyDataSetChanged();
            if(position!=-1) {
                //长按进入编辑模式
                selects.add(position);
                adapter.notifyItemChanged(position);
            }
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onSelectCountChange(getCheckedItemCount());
            }
        }else if(mChoiceMode== ChoiceMode.MULTIPLE){
            //如果当前为多选模式，长按则弹出对话框
            if(mOnItemStatueListener!=null){
                mOnItemStatueListener.onPopupDialog(position);
            }
        }
    }

    public interface OnItemStatueListener{
        void onSelectSingle(int position);
        void onUnSelectSingle(int position);
        void onSelectMultiple(int position);
        void onUnSelectMultiple(int position);
        void onPopupDialog(int position);
        void onSelectCountChange(int count);
    }

    public void setOnItemStatueListener(OnItemStatueListener mOnItemStatueListener) {
        this.mOnItemStatueListener = mOnItemStatueListener;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void initChoiceConfig(Port inputPort){
        if(inputPort==null){
            setChoiceColor(ChoiceColor.GREEN);
            setChoiceBadge(-1);
            return;
        }
        switch (inputPort.category){
            case Port.CATEGORY_CAMERA:
                setChoiceColor(ItemSelectionSupport.ChoiceColor.GREEN);
                break;
            case Port.CATEGORY_DESKTOP:
                setChoiceColor(ItemSelectionSupport.ChoiceColor.YELLOW);
                break;
            case Port.CATEGORY_VIDEO:
                setChoiceColor(ItemSelectionSupport.ChoiceColor.BLUE);
                break;
            case Port.CATEGORY_OUTPUT_RETURN:
                setChoiceColor(ItemSelectionSupport.ChoiceColor.RED);
                break;
            default:
                setChoiceColor(ItemSelectionSupport.ChoiceColor.GREEN);
                break;
        }
        setChoiceBadge(PortHelper.getInstance().getInputPortBadge(inputPort.idx));
    }
}
