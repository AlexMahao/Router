package com.spearbothy.router;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * @author mahao
 * @date 2018/7/12 上午10:18
 */

public class DebugListRecyclerAdapter extends BaseRecyclerAdapter<BaseDebugListActivity.Item> {

    private OnItemClickListener mOnItemClickListener;

    public DebugListRecyclerAdapter(List<BaseDebugListActivity.Item> data, OnItemClickListener onItemClickListener) {
        super(data);
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getLayoutId(int itemType) {
        if (itemType == ITEM_TYPE_GROUP) {
            return R.layout.item_debug_group;
        }
        return R.layout.item_debug_list_desc;
    }

    @Override
    public int getItemType(int position) {
        if (getData().get(position).getType() == BaseDebugListActivity.Item.GROUP) {
            return ITEM_TYPE_GROUP;
        }
        return super.getItemType(position);
    }

    @Override
    protected void bindData(final BaseRecyclerAdapter.BaseViewHolder holder, final int position, int itemType) {
        if (itemType == ITEM_TYPE_GROUP) {
            ((TextView) holder.getView(R.id.title)).setText(getData().get(position).getTitle());
        } else {
            ((TextView) holder.getView(R.id.title)).setText(getData().get(position).getTitle());
            String desc = getData().get(position).getDesc();
            TextView descView = (TextView) holder.getView(R.id.desc);
            if (TextUtils.isEmpty(desc)) {
                descView.setVisibility(View.GONE);
            } else {
                descView.setVisibility(View.VISIBLE);
                descView.setText(desc);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder, position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BaseViewHolder holder, int position);
    }
}
