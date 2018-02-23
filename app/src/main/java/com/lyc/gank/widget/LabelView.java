package com.lyc.gank.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.lyc.gank.R;

import java.util.Arrays;

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */

public class LabelView extends FlexboxLayout {
    private int colorTextSelected;
    private int colorTextUnSelected;
    private int colorBgSelected;
    private int colorBgUnselected;

    private int labelPaddingVertical;
    private int labelPaddingHorizontal;
    private int labelMarginVertical;
    private int labelMarginHorizontal;
    private float labelTextSize;

    private int selectedPosition;
    private String[] labels;

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        colorTextSelected = ta.getColor(R.styleable.LabelView_textSelectedColor, Color.parseColor("#ffffff"));
        colorTextUnSelected = ta.getColor(R.styleable.LabelView_textUnselectedColor, Color.parseColor("#757575"));
        colorBgSelected = ta.getColor(R.styleable.LabelView_backgroundSelectedColor, context.getResources().getColor(R.color.colorPrimary));
        colorBgUnselected = ta.getColor(R.styleable.LabelView_backgroundUnselectedColor, Color.parseColor("#d3d3d3"));
        float ratio = context.getResources().getDisplayMetrics().density;
        labelPaddingVertical = ta.getDimensionPixelSize(R.styleable.LabelView_labelPaddingHorizontal, (int) (16 * ratio + 0.5f));
        labelPaddingHorizontal = ta.getDimensionPixelSize(R.styleable.LabelView_labelPaddingHorizontal, (int) (8 * ratio + 0.5f));
        labelMarginHorizontal = ta.getDimensionPixelSize(R.styleable.LabelView_labelMarginHorizontal, (int) (16 * ratio + 0.5f));
        labelMarginVertical = ta.getDimensionPixelSize(R.styleable.LabelView_labelMarginVertical, (int) (16 * ratio + 0.5f));
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        labelTextSize = ta.getDimension(R.styleable.LabelView_labelTextSize, 14 * scale);
        ta.recycle();
    }

    public void setLabels(String[] labels) {
        if (labels == null || labels.length <= 0) {
            return;
        }
        //reuse
        int oldLen = this.labels == null ? 0 : this.labels.length;
        if (oldLen > labels.length) {
            int more = oldLen - labels.length;
            for (int i = 0; i < more; i++) {
                removeViewAt(getChildCount() - 1);
            }
        } else if (oldLen < labels.length) {
            int more = labels.length - oldLen;
            for (int i = 0; i < more; i++) {
                TextView tv = newLabel();
                int finalI = i;
                tv.setOnClickListener(v -> {
                    unSelect((TextView) getChildAt(selectedPosition));
                    select(tv);
                    selectedPosition = finalI;
                });
            }
        }

        this.labels = Arrays.copyOf(labels, labels.length);
        reset();
    }

    private void reset() {
        final int count = getChildCount();
        selectedPosition = 0;
        for (int i = 1; i < count; i++) {
            unSelect((TextView) getChildAt(i));
        }
        select(selectedPosition);
    }

    private TextView newLabel() {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(labelTextSize);
        textView.setPadding(labelPaddingHorizontal, labelPaddingVertical, labelPaddingHorizontal, labelPaddingVertical);
        textView.setTextColor(colorTextUnSelected);
        textView.setBackgroundColor(colorBgUnselected);
        addView(textView);
        LayoutParams params = (LayoutParams) textView.getLayoutParams();
        params.setMargins(labelMarginHorizontal / 2, labelMarginVertical / 2, labelMarginHorizontal / 2, labelMarginVertical / 2);
        textView.setLayoutParams(params);
        return textView;
    }

    private void select(int position) {
        if (position == selectedPosition) {
            return;
        }
        select(position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private String getSelectedLabel() {
        return labels[selectedPosition];
    }

    private void select(TextView label) {
        label.setTextColor(colorTextSelected);
        label.setBackgroundColor(colorBgSelected);
    }

    private void unSelect(TextView label) {
        label.setTextColor(colorTextUnSelected);
        label.setBackgroundColor(colorBgUnselected);
    }
}
