package com.spearbothy.router;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mahao
 * @date 2018/7/12 上午11:04
 */

public abstract class BaseDebugListActivity extends AppCompatActivity implements DebugListRecyclerAdapter.OnItemClickListener {

    private ArrayList<Item> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_list);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        items = new ArrayList<>();
        initListItem(items);
        recyclerView.setAdapter(new DebugListRecyclerAdapter(items, this));
    }

    /**
     * 初始化数据集合
     */
    public abstract void initListItem(List<Item> items);

    /**
     * 点击的回调
     */
    public abstract void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, Item item, String id);

    @Override
    public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, int position) {
        onItemClick(holder, items.get(position), items.get(position).id);
    }

    public static class Item {

        public static final int ITEM = 1;
        public static final int GROUP = 2;

        private String title;
        private String desc;
        private String id;
        private int type; // item  group

        public Item(String title, String desc, String id) {
            this(title, desc, id, ITEM);
        }

        public Item(String title, String desc, String id, int type) {
            this.title = title;
            this.desc = desc;
            this.id = id;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "title='" + title + '\'' +
                    ", desc='" + desc + '\'' +
                    ", id='" + id + '\'' +
                    ", type=" + type +
                    '}';
        }
    }
}
