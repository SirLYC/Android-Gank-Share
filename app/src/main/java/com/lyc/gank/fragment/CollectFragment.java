package com.lyc.gank.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.SinglePhotoActivity;
import com.lyc.gank.WebActivity;
import com.lyc.gank.adapter.BaseRecyclerAdapter;
import com.lyc.gank.adapter.CollectRecyclerAdapter;
import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.database.Item;
import com.lyc.gank.view.EmptyView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 972694341@qq.com on 2017/6/8.
 */

public class CollectFragment extends BaseFragment {

    @BindView(R.id.tool_bar_collect)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_collect)
    RecyclerView mRecyclerView;

    @BindView(R.id.fab_delete_select)
    FloatingActionButton fab;

    @BindView(R.id.coordinator_layout_collect)
    ViewGroup parent;

    List<Item> mItemList = new ArrayList<>();

    CollectRecyclerAdapter adapter;

    private Set<Integer> posSet = new HashSet<>();

    public boolean onSelect = false;

    private boolean isSelectAll = false;

    private boolean isPrepared = false;

    private EmptyView emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        ButterKnife.bind(this, view);
        initToolbar();
        adapter = new CollectRecyclerAdapter(mItemList, getContext());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setOnItemListener();
        isPrepared = true;
        loadData();
        return view;
    }

    @OnClick(R.id.fab_delete_select)
    public void delete(){
        fab.hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setMessage(R.string.sure_to_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeSelectedItems();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fab.show();
            }
        });
        builder.show();
    }

    private void showEmptyView(boolean show){
        if(emptyView == null){
            EmptyView.Builder builder = new EmptyView.Builder();
            emptyView = builder.with(mActivity)
                    .parent(parent)
                    .emptyHint(getString(R.string.empty_hint_collect))
                    .buttonText(getString(R.string.go_to_category))
                    .listener(new EmptyView.onClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((MainActivity)mActivity).gotoFragment(0);
                        }
                    }).build();
        }
        emptyView.show(show);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelect){
                    endSelect();
                }else {
                    ((MainActivity)mActivity).openOrCloseDrawer(true);
                }
            }
        });
        ((MainActivity)mActivity).initToolbar(toolbar, "收藏列表", R.menu.menu_collect);
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
            loadData();
        }
    }

    private void loadData(){
        Observable.create(new ObservableOnSubscribe<List<Item>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Item>> e) throws Exception {
                mItemList.clear();
                e.onNext(DataSupport.findAll(Item.class));
            }
        }).map(new Function<List<Item>, Boolean>() {
            @Override
            public Boolean apply(List<Item> items) throws Exception {
                return items != null && items.size() > 0
                        && mItemList.addAll(items);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        showEmptyView(!aBoolean);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void setOnItemListener(){
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos, View v) {
                ResultItem item = mItemList.get(pos).toResultItem();
                if(onSelect){
                    addOrRemove(pos);
                }else {
                    Intent intent = new Intent();
                    switch (item.type){
                        case "休息视频":
                            intent.setAction("android.intent.action.VIEW");
                            Uri uri = Uri.parse(item.url);
                            intent.setData(uri);
                            startActivity(intent);
                            break;
                        case "福利":
                            intent.setClass(mActivity, SinglePhotoActivity.class);
                            intent.putExtra("url", item.url);
                            startActivity(intent);
                            break;
                        default:
                            intent.setClass(mActivity, WebActivity.class);
                            intent.putExtra("item", item);
                            startActivity(intent);
                            break;
                    }
                }
            }

            @Override
            public boolean onLongClick(int pos, View v) {
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
        if(isSelectAll){
            showEmptyView(true);
        }
        endSelect();
    }
}
