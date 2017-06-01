package com.lyc.gank;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lyc.gank.Adapter.CollectRecyclerAdapter;
import com.lyc.gank.Database.Item;
import com.lyc.gank.Bean.ResultItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<Item> mItemList;
    CollectRecyclerAdapter adapter;
    FloatingActionButton fab;
    private Set<Integer> posSet = new HashSet<>();
    private boolean onSelect = false;
    private boolean isSelectAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        mItemList = DataSupport.findAll(Item.class);
        toolbar = (Toolbar)findViewById(R.id.collect_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.collect_recycler);
        adapter = new CollectRecyclerAdapter(mItemList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = (FloatingActionButton)findViewById(R.id.delete_select);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(CollectActivity.this);
                builder.setCancelable(false);
                builder.setMessage("确定删除所选项？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeSelectedItems();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fab.show();
                    }
                });
                builder.show();
            }
        });
        setOnItemListener();
    }

    private void setOnItemListener(){
        adapter.setOnItemSelectedListener(new CollectRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onSelected(int pos, View v) {
                ResultItem item = mItemList.get(pos).toResultItem();
                if(onSelect){
                    addOrRemove(pos);
                 }else {
                    if(item.type.equals("休息视频")){
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(item.url);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    else if(item.type.equals("福利")){
                        Intent intent = new Intent(CollectActivity.this, SinglePhotoActivity.class);
                        intent.putExtra("url", item.url);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(CollectActivity.this, WebActivity.class);
                        intent.putExtra("item", item);
                        startActivity(intent);
                    }
                }
            }
        });

        adapter.setOnItemLongClickListener(new CollectRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClickedListener(int pos, View v) {
                if(!onSelect) {
                    addOrRemove(pos);
                    startSelect();
                    fab.show();
                    return true;
                }
                return false;
            }
        });
    }

    private void startSelect(){
        if(mItemList.size() == 0)
            return;
        onSelect = true;
        isSelectAll = false;
        adapter.setOnSelect(true);
        adapter.notifyDataSetChanged();
    }

    private void endSelect(){
        onSelect = false;
        isSelectAll = false;
        posSet.clear();
        adapter.setOnSelect(false);
        adapter.notifyDataSetChanged();
        fab.hide();
    }

    private void addOrRemove(int pos){
        if(posSet.contains(pos)){
            if(posSet.size() == mItemList.size()){
                isSelectAll = false;
            }
            if(posSet.size() == 1){
                fab.hide();
            }
            posSet.remove(pos);
            ((ImageView)recyclerView.getChildAt(pos)
                    .findViewById(R.id.check_img)).setImageResource(R.drawable.ic_unchecked);
        }else {
            posSet.add(pos);
            ((ImageView)recyclerView.getChildAt(pos)
                    .findViewById(R.id.check_img)).setImageResource(R.drawable.ic_checked);
            if(posSet.size() == mItemList.size()){
                isSelectAll = true;
            }
            if(fab.getVisibility() != View.VISIBLE){
                fab.show();
            }
        }
    }

    private void selectAllOrUnSelectAll(){
        if(isSelectAll){
            for (Integer pos : posSet) {
                ((ImageView)recyclerView.getChildAt(pos)
                        .findViewById(R.id.check_img)).setImageResource(R.drawable.ic_unchecked);
            }
            posSet.clear();
            fab.hide();
            isSelectAll = false;
        }else {
            for (int i = 0; i < mItemList.size(); i++){
                posSet.add(i);
                ((ImageView)recyclerView.getChildAt(i)
                        .findViewById(R.id.check_img)).setImageResource(R.drawable.ic_checked);
            }
            if(fab.getVisibility() != View.VISIBLE){
                fab.show();
            }
            isSelectAll = true;
        }
    }

    private void removeSelectedItems(){
        List<Item> selectItems = new ArrayList<>();
        for (Integer pos : posSet) {
            selectItems.add(mItemList.get(pos));
        }
        for (Item item : selectItems) {
            DataSupport.delete(Item.class, item.getId());
            mItemList.remove(item);
        }
        endSelect();
    }

    @Override
    public void onBackPressed() {
        if(onSelect){
            endSelect();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.collect_select:
                if(onSelect){
                    selectAllOrUnSelectAll();
                }else {
                    startSelect();
                }
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toolbar.setTitle("收藏列表");
    }
}
