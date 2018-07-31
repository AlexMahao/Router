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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_list);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ArrayList<Item> items = new ArrayList<>();
        initListItem(items);
        recyclerView.setAdapter(new DebugListRecyclerAdapter(items, this));
    }

    /**
     * 初始化数据集合
     */
    public abstract void initListItem(List<Item> items);

    public static class Item {
        private String title;
        private String desc;

        public Item(String title, String desc) {
            this.title = title;
            this.desc = desc;
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
                    '}';
        }
    }
}
