package cn.diaovision.omnicontrol.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.common.Device;

/**
 * Created by TaoYimin on 2017/9/8.
 */

public class CommandAdapter extends RecyclerView.Adapter<CommandAdapter.CommandViewHolder> {
/*    List<Device.Command> cmds;

    public CommandAdapter(List<Device.Command> cmds) {
        this.cmds = cmds;
    }

    @Override
    public CommandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommandViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_command,parent,false));
    }

    @Override
    public void onBindViewHolder(CommandViewHolder holder, int position) {
        if(cmds==null){
            return;
        }
        Device.Command cmd=cmds.get(position);
        holder.commandAlias.setText(cmd.getName());
        holder.commandBg.setBackgroundResource(R.drawable.button_green_selector);
        holder.commandImage.setImageResource(R.drawable.command_green_selector);
        if(TextUtils.isEmpty(cmd.getCmd())&&TextUtils.isEmpty(cmd.getByteCmd())){
            holder.commandImage.setSelected(false);
            holder.commandBg.setSelected(false);
        }else{
            holder.commandImage.setSelected(true);
            holder.commandBg.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        if(cmds==null){
            return 0;
        }else {
            return cmds.size();
        }
    }

    public class CommandViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.command_bg)
        RelativeLayout commandBg;
        @BindView(R.id.command_alias)
        TextView commandAlias;
        @BindView(R.id.command_image)
        ImageView commandImage;

        public CommandViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setCmds(List<Device.Command> cmds) {
        this.cmds = cmds;
    }*/

    private List<Device.Command> cmds;

    public CommandAdapter(List<Device.Command> cmds) {
        this.cmds = cmds;
    }

    @Override
    public CommandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommandViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_command, parent, false));
    }

    @Override
    public void onBindViewHolder(final CommandViewHolder holder, int position) {
        if (cmds == null) {
            return;
        }
        if (position < cmds.size()) {
            Device.Command cmd = cmds.get(position);
            holder.commandAdd.setVisibility(View.GONE);
            holder.commandImage.setVisibility(View.VISIBLE);
            holder.commandAlias.setText(cmd.getName());
            holder.commandImage.setImageResource(R.drawable.command_green_selector);
            holder.commandBg.setBackgroundResource(R.drawable.button_green_selector);
            if (TextUtils.isEmpty(cmd.getStringCmd()) && cmd.getByteCmd() == null) {
                holder.commandBg.setSelected(false);
                holder.commandImage.setSelected(false);
            } else {
                holder.commandBg.setSelected(true);
                holder.commandImage.setSelected(true);
            }
        } else {
            holder.commandAdd.setVisibility(View.VISIBLE);
            holder.commandImage.setVisibility(View.GONE);
            holder.commandAlias.setText("添加");
        }
    }

    @Override
    public int getItemCount() {
        if (cmds == null) {
            return 0;
        } else {
            return cmds.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(cmds==null){
            return 1;
        }
        if (position == cmds.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    public class CommandViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.command_bg)
        RelativeLayout commandBg;
        @BindView(R.id.command_alias)
        TextView commandAlias;
        @BindView(R.id.command_image)
        ImageView commandImage;
        @BindView(R.id.command_add)
        TextView commandAdd;

        public CommandViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public List<Device.Command> getCmds() {
        return cmds;
    }

    public void setCmds(List<Device.Command> cmds) {
        this.cmds = cmds;
    }
}
