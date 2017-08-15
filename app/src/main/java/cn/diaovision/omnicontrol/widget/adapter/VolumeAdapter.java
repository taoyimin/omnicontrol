package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.util.PortHelper;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;

/**
 * Created by TaoYimin on 2017/5/17.
 */

public class VolumeAdapter extends RecyclerView.Adapter<VolumeAdapter.SelectableViewHolder> {
    private List<Port> data;
    private ItemSelectionSupport mSelectionSupport;
    Context context;

    public VolumeAdapter(List<Port> data, ItemSelectionSupport selectionSupport) {
        this.data = data;
        mSelectionSupport = selectionSupport;
    }

    public ItemSelectionSupport getSelectionSupport() {
        return mSelectionSupport;
    }

    public List<Port> getData() {
        return data;
    }

    @Override
    public SelectableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SelectableViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_port2, parent, false));
    }

    @Override
    public void onBindViewHolder(final SelectableViewHolder holder, final int position) {
        if (data.get(position).dir == Port.DIR_IN) {
/*            switch (data.get(position).category) {
                case Port.CATEGORY_CAMERA:
                    holder.portBg.setBackgroundResource(R.drawable.button_green_selector);
                    holder.portImage.setImageResource(R.drawable.camera_green_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_green_selector));
                    break;
                case Port.CATEGORY_DESKTOP:
                    holder.portBg.setBackgroundResource(R.drawable.button_yellow_selector);
                    holder.portImage.setImageResource(R.drawable.desktop_yellow_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_yellow_selector));
                    break;
                case Port.CATEGORY_VIDEO:
                    holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                    holder.portImage.setImageResource(R.drawable.video_blue_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_blue_selector));
                    break;
                case Port.CATEGORY_OUTPUT_RETURN:
                    holder.portBg.setBackgroundResource(R.drawable.button_red_selector);
                    holder.portImage.setImageResource(R.drawable.output_return_red_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_red_selector));
                    break;
                default:
                    break;
            }*/
            int flag = position % 8;
            switch (flag) {
                case 0:
                    holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                    holder.portImage.setImageResource(R.drawable.voice_blue_green_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_blue_green_selector));
                    break;
                case 1:
                    holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                    holder.portImage.setImageResource(R.drawable.voice_blue_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_blue_selector));
                    break;
                case 2:
                    holder.portBg.setBackgroundResource(R.drawable.button_green_selector);
                    holder.portImage.setImageResource(R.drawable.voice_green_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_green_selector));
                    break;
                case 3:
                    holder.portBg.setBackgroundResource(R.drawable.button_yellow_selector);
                    holder.portImage.setImageResource(R.drawable.voice_orange_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_orange_selector));
                    break;
                case 4:
                    holder.portBg.setBackgroundResource(R.drawable.button_red_selector);
                    holder.portImage.setImageResource(R.drawable.voice_purple_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_purple_selector));
                    break;
                case 5:
                    holder.portBg.setBackgroundResource(R.drawable.button_red_selector);
                    holder.portImage.setImageResource(R.drawable.voice_red_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_red_selector));
                    break;
                case 6:
                    holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                    holder.portImage.setImageResource(R.drawable.voice_sky_blue_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_sky_blue_selector));
                    break;
                case 7:
                    holder.portBg.setBackgroundResource(R.drawable.button_yellow_selector);
                    holder.portImage.setImageResource(R.drawable.voice_yellow_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_yellow_selector));
                    break;
                default:
                    holder.portImage.setImageResource(R.mipmap.voice);
                    break;
            }
            if (PortHelper.getInstance().getInputList() != null && PortHelper.getInstance().getOutputList() != null && PortHelper.getInstance().getChannelSet() != null) {
                holder.portBadge.setText(data.get(position).idx + 1 + "");
            }
        } else if (data.get(position).dir == Port.DIR_OUT) {
            //int inputCategory = PortHelper.getInstance().getInputPortCategory(position);
            int flag=PortHelper.getInstance().getInputIdx(data.get(position).idx)%8;
            switch (flag) {
                case 0:
                    holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                    holder.portImage.setImageResource(R.drawable.voice_blue_green_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_blue_green_selector));
                    break;
                case 1:
                    holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                    holder.portImage.setImageResource(R.drawable.voice_blue_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_blue_selector));
                    break;
                case 2:
                    holder.portBg.setBackgroundResource(R.drawable.button_green_selector);
                    holder.portImage.setImageResource(R.drawable.voice_green_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_green_selector));
                    break;
                case 3:
                    holder.portBg.setBackgroundResource(R.drawable.button_yellow_selector);
                    holder.portImage.setImageResource(R.drawable.voice_orange_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_orange_selector));
                    break;
                case 4:
                    holder.portBg.setBackgroundResource(R.drawable.button_red_selector);
                    holder.portImage.setImageResource(R.drawable.voice_purple_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_purple_selector));
                    break;
                case 5:
                    holder.portBg.setBackgroundResource(R.drawable.button_red_selector);
                    holder.portImage.setImageResource(R.drawable.voice_red_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_red_selector));
                    break;
                case 6:
                    holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                    holder.portImage.setImageResource(R.drawable.voice_sky_blue_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_sky_blue_selector));
                    break;
                case 7:
                    holder.portBg.setBackgroundResource(R.drawable.button_yellow_selector);
                    holder.portImage.setImageResource(R.drawable.voice_yellow_selector);
                    holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_yellow_selector));
                    break;
                default:
                    holder.portImage.setImageResource(R.drawable.voice_green_selector);
                    break;
            }
            //设置输出端的badge
            int inputIdx = PortHelper.getInstance().getInputIdx(data.get(position).idx);
            if (inputIdx != -1) {
                holder.portBadge.setText(inputIdx+1 + "");
            } else {
                inputIdx = mSelectionSupport.getChoiceBadge();
                if (inputIdx != -1) {
                    holder.portBadge.setText(inputIdx+1 + "");
                } else {
                    holder.portBadge.setText("");
                }
            }
            //设置输出端的badge是否可见
            if (PortHelper.getInstance().outputPortIsUsed(position)) {
                holder.portBadge.setVisibility(View.VISIBLE);
            } else if (mSelectionSupport.isItemChecked(position)) {
                if (mSelectionSupport.getChoiceMode() == ItemSelectionSupport.ChoiceMode.MULTIPLE) {
                    holder.portBadge.setVisibility(View.VISIBLE);
                } else {
                    holder.portBadge.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.portBadge.setVisibility(View.INVISIBLE);
            }
        }
        //设置端口别名
        holder.portAlias.setText(data.get(position).alias);
        //设置item的选中状态
        boolean checked = getSelectionSupport().isItemChecked(position);
        holder.checkBox.setChecked(checked);
        if (checked) {
            holder.portBg.setBackgroundResource(R.drawable.button_checked_selector);
            holder.portBg.setSelected(true);
            holder.portImage.setSelected(true);
            holder.portBadge.setSelected(true);
        } else {
            holder.portBg.setSelected(false);
            holder.portImage.setSelected(false);
            holder.portBadge.setSelected(false);
        }
        //把系统配置好的端口全部点亮
        if (PortHelper.getInstance().getInputList() != null && PortHelper.getInstance().getOutputList() != null && PortHelper.getInstance().getChannelSet() != null) {
            if (data.get(position).dir == Port.DIR_IN) {
                if (PortHelper.getInstance().inputPortIsUsed(position)) {
                    holder.portBg.setSelected(true);
                    holder.portImage.setSelected(true);
                    holder.portBadge.setSelected(true);
                }
            } else if (data.get(position).dir == Port.DIR_OUT) {
                if (PortHelper.getInstance().outputPortIsUsed(position)) {
                    holder.portBg.setSelected(true);
                    holder.portImage.setSelected(true);
                    holder.portBadge.setSelected(true);
                }
            }
        }
/*        if (mSelectionSupport.getChoiceMode() == ItemSelectionSupport.ChoiceMode.MULTIPLE && data.get(position).dir == Port.DIR_OUT) {

            mSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
                @Override
                public void onSelectSingle(int position) {

                }

                @Override
                public void onUnSelectSingle(int position) {

                }

                @Override
                public void onSelectMultiple(int position) {
                    switch (data.get(position).category) {
                        case Port.CATEGORY_PROJECTOR:
                            if (getSelectionSupport().getChoiceColor() == ItemSelectionSupport.ChoiceColor.YELLOW) {
                                holder.portBg.setBackgroundResource(R.drawable.button_yellow_selector);
                                holder.portImage.setImageResource(R.drawable.projector_yellow_selector);
                                holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_yellow_selector));
                            } else if (getSelectionSupport().getChoiceColor() == ItemSelectionSupport.ChoiceColor.BLUE) {
                                holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
                                holder.portImage.setImageResource(R.drawable.projector_blue_selector);
                                holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_blue_selector));
                            } else if (getSelectionSupport().getChoiceColor() == ItemSelectionSupport.ChoiceColor.RED) {
                                holder.portBg.setBackgroundResource(R.drawable.button_red_selector);
                                holder.portImage.setImageResource(R.drawable.projector_red_selector);
                                holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_red_selector));
                            } else {
                                holder.portBg.setBackgroundResource(R.drawable.button_green_selector);
                                holder.portImage.setImageResource(R.drawable.projector_green_selector);
                                holder.portBadge.setTextColor(context.getResources().getColorStateList(R.color.port_badge_green_selector));
                            }
                            break;
                        case Port.CATEGORY_DISPLAY:
                            break;
                        case Port.CATEGORY_IP:
                            break;
                        case Port.CATEGORY_COMPUTER:
                            break;
                        case Port.CATEGORY_TV:
                            break;
                        case Port.CATEGORY_CONFERENCE:
                            break;
                        default:
                            break;
                    }
      *//*              int badge = mSelectionSupport.getChoiceBadge();
                    if(badge!=-1){
                        holder.portBadge.setText(badge + "");
                    }else{
                        holder.portBadge.setText("");
                    }*//*
                }

                @Override
                public void onUnSelectMultiple(int position) {
                    holder.portBg.setSelected(false);
                    holder.portImage.setSelected(false);
                    holder.portBadge.setSelected(false);
                    //holder.portBadge.setText("");
                }

                @Override
                public void onPopupDialog(int position) {

                }

                @Override
                public void onSelectCountChange(int count) {

                }
            });
        }*/

        //多选模式下隐藏checkbox
        //  if (mSelectionSupport.getChoiceMode() == ItemSelectionSupport.ChoiceMode.MULTIPLE) {
        //holder.checkBox.setVisibility(View.VISIBLE);
        //  } else {
        holder.checkBox.setVisibility(View.INVISIBLE);
        // }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SelectableViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.port_bg)
        RelativeLayout portBg;
        @BindView(R.id.port_checkbox)
        CheckBox checkBox;
        @BindView(R.id.port_alias)
        TextView portAlias;
        @BindView(R.id.port_image)
        ImageView portImage;
        @BindView(R.id.port_badge)
        TextView portBadge;

        public SelectableViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}