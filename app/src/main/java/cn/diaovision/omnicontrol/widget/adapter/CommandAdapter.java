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

public class CommandAdapter extends RecyclerView.Adapter<CommandAdapter.CommandViewHolder>{
    List<Device.Command> cmds;

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
    }
}
