package com.guohua.sdk.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-22
 * 具有EmptyView功能
 */
public class LocalRecyclerView extends RecyclerView {
    private View emptyView; /*空布局*/

    public LocalRecyclerView(Context context) {
        super(context);
    }

    public LocalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();
    }

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(VISIBLE);
                    setVisibility(GONE);
                } else {
                    emptyView.setVisibility(GONE);
                    setVisibility(VISIBLE);
                }
            }
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onChanged();
        }
    };

    public void setEmptyView(View view) {
        this.emptyView = view;
        //this.getRootView().addView(emptyView); //加入主界面布局
    }
}
