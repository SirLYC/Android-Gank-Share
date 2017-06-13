package com.lyc.gank.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lyc.gank.Adapter.CollectRecyclerAdapter;
import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.Database.Item;
import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.SinglePhotoActivity;
import com.lyc.gank.WebActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 972694341@qq.com on 2017/6/8.
 */

public class CollectFragment extends Fragment {

    @BindView(R.id.tool_bar_collect)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_collect)
    RecyclerView mRecyclerView;

    @BindView(R.id.fab_delete_select)
    FloatingActionButton fab;

    List<Item> mItemList = new ArrayList<>();

    CollectRecyclerAdapter adapter;
    private Set<Integer> posSet = new HashSet<>();

    public boolean onSelect = false;

    private boolean isSelectAll = false;

    private boolean isPrepared = false;
    Runnable r = new Runnable() {
        @Override
        public void run() {
            mItemList.clear();
            for(Item i: DataSupport.findAll(Item.class)){
                mItemList.add(i);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    Log.e("Collect", mItemList.size() + "");
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        ButterKnife.bind(this, view);

        initToolbar();

        adapter = new CollectRecyclerAdapter(mItemList, getContext());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        isPrepared = true;
        new Thread(r).start();
        return view;
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelect){
                    endSelect();
                }else {
                    ((MainActivity)getActivity()).openOrCloseDrawer(true);
                }
            }
        });
        ((MainActivity)getActivity()).initToolbar(toolbar, "收藏列表", R.menu.menu_collect);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
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
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser && isPrepared){
            new Thread(r).start();
        }
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
                        Intent intent = new Intent(getContext(), SinglePhotoActivity.class);
                        intent.putExtra("url", item.url);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getContext(), WebActivity.class);
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

    public void startSelect(){
        if(mItemList.size() == 0)
            return;
        onSelect = true;
        isSelectAll = false;
        adapter.setOnSelect(true);
        adapter.notifyDataSetChanged();
        toolbar.setTitle("选择");
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
    }

    public void endSelect(){
        onSelect = false;
        isSelectAll = false;
        for(int p: posSet){
            unSelect(p);
        }
        posSet.clear();
        adapter.setOnSelect(false);
        adapter.notifyDataSetChanged();
        fab.hide();
        toolbar.setTitle("收藏列表");
        toolbar.setNavigationIcon(R.drawable.ic_menu);
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
            unSelect(pos);
        }else {
            posSet.add(pos);
            select(pos);
            if(posSet.size() == mItemList.size()){
                isSelectAll = true;
            }
            if(fab.getVisibility() != View.VISIBLE){
                fab.show();
            }
        }
    }

    private void unSelect(int pos) {
        ((ImageView)mRecyclerView.getChildAt(pos)
                .findViewById(R.id.check_img)).setImageResource(R.drawable.ic_unchecked);
    }

    private void select(int pos) {
        ((ImageView)mRecyclerView.getChildAt(pos)
                .findViewById(R.id.check_img)).setImageResource(R.drawable.ic_checked);
    }

    private void selectAllOrUnSelectAll(){
        if(isSelectAll){
            for (Integer pos : posSet) {
                unSelect(pos);
            }
            posSet.clear();
            fab.hide();
            isSelectAll = false;
        }else {
            for (int i = 0; i < mItemList.size(); i++){
                posSet.add(i);
                select(i);
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
}
