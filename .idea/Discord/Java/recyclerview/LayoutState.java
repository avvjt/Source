package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.State;
import f.d.b.a.a;

public class LayoutState {
    public static final int INVALID_LAYOUT = Integer.MIN_VALUE;
    public static final int ITEM_DIRECTION_HEAD = -1;
    public static final int ITEM_DIRECTION_TAIL = 1;
    public static final int LAYOUT_END = 1;
    public static final int LAYOUT_START = -1;
    public int mAvailable;
    public int mCurrentPosition;
    public int mEndLine = 0;
    public boolean mInfinite;
    public int mItemDirection;
    public int mLayoutDirection;
    public boolean mRecycle = true;
    public int mStartLine = 0;
    public boolean mStopInFocusable;

    public boolean hasMore(State state) {
        int i = this.mCurrentPosition;
        return i >= 0 && i < state.getItemCount();
    }

    public View next(Recycler recycler) {
        View viewForPosition = recycler.getViewForPosition(this.mCurrentPosition);
        this.mCurrentPosition += this.mItemDirection;
        return viewForPosition;
    }

    public String toString() {
        StringBuilder L = a.L("LayoutState{mAvailable=");
        L.append(this.mAvailable);
        L.append(", mCurrentPosition=");
        L.append(this.mCurrentPosition);
        L.append(", mItemDirection=");
        L.append(this.mItemDirection);
        L.append(", mLayoutDirection=");
        L.append(this.mLayoutDirection);
        L.append(", mStartLine=");
        L.append(this.mStartLine);
        L.append(", mEndLine=");
        return a.w(L, this.mEndLine, '}');
    }
}
