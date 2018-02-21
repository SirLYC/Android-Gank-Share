//package com.lyc.gank.view;
//
//import android.content.Context;
//import android.support.annotation.StringRes;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.lyc.gank.R;
//
//import org.litepal.LitePalApplication;
//
//import butterknife.ButterKnife;
//
///**
// * 可复用的空列表提醒
// */
//
//public class EmptyView {
//    private View view;
//    private EmptyView(){}
//
//    public static class Builder{
//        private EmptyView emptyView;
//        private ViewGroup parent;
//        private String emptyHint;
//        private String buttonText;
//        private onClickListener mOnClickListener;
//
//        public Builder(){
//            emptyView = new EmptyView();
//        }
//
//        public Builder parent(ViewGroup parent){
//            this.parent = parent;
//            return this;
//        }
//        public Builder emptyHint(@StringRes int resId){
//            return emptyHint(LitePalApplication.getContext().getString(resId));
//        }
//
//        public Builder emptyHint(String emptyHint){
//            this.emptyHint = emptyHint;
//            return this;
//        }
//
//        public Builder buttonText(@StringRes int resId) {
//            return buttonText(LitePalApplication.getContext().getString(resId));
//        }
//
//        public Builder buttonText(String buttonText){
//            this.buttonText = buttonText;
//            return this;
//        }
//
//        public Builder listener(onClickListener listener){
//            this.mOnClickListener = listener;
//            return this;
//        }
//
//        public EmptyView build(){
//            if(parent == null)
//                throw new IllegalArgumentException("No parent viewGroup");
//
//            emptyView.view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.empty_view, parent, false);
//            TextView textView = ButterKnife.findById(emptyView.view, R.id.text_empty_hint);
//            textView.setText(emptyHint);
//            Button button = ButterKnife.findById(emptyView.view, R.id.btn_empty_view);
//            button.setText(buttonText);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mOnClickListener != null)
//                        mOnClickListener.onClick(v);
//                }
//            });
//            parent.addView(emptyView.view);
//            return emptyView;
//        }
//    }
//
//    public void show(boolean show){
//        if(show){
//            view.setVisibility(View.VISIBLE);
//        }else {
//            view.setVisibility(View.GONE);
//        }
//    }
//
//    public interface onClickListener{
//        void onClick(View v);
//    }
//}
