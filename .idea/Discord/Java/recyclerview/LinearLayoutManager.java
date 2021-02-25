package androidx.recyclerview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper.ViewDropHandler;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import androidx.recyclerview.widget.RecyclerView.LayoutManager.Properties;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import f.d.b.a.a;
import java.util.List;

public class LinearLayoutManager extends LayoutManager implements ViewDropHandler, ScrollVectorProvider {
    public static final boolean DEBUG = false;
    public static final int HORIZONTAL = 0;
    public static final int INVALID_OFFSET = Integer.MIN_VALUE;
    private static final float MAX_SCROLL_FACTOR = 0.33333334f;
    private static final String TAG = "LinearLayoutManager";
    public static final int VERTICAL = 1;
    public final AnchorInfo mAnchorInfo;
    private int mInitialPrefetchItemCount;
    private boolean mLastStackFromEnd;
    private final LayoutChunkResult mLayoutChunkResult;
    private LayoutState mLayoutState;
    public int mOrientation;
    public OrientationHelper mOrientationHelper;
    public SavedState mPendingSavedState;
    public int mPendingScrollPosition;
    public int mPendingScrollPositionOffset;
    private boolean mRecycleChildrenOnDetach;
    private int[] mReusableIntPair;
    private boolean mReverseLayout;
    public boolean mShouldReverseLayout;
    private boolean mSmoothScrollbarEnabled;
    private boolean mStackFromEnd;

    public static class AnchorInfo {
        public int mCoordinate;
        public boolean mLayoutFromEnd;
        public OrientationHelper mOrientationHelper;
        public int mPosition;
        public boolean mValid;

        public AnchorInfo() {
            reset();
        }

        public void assignCoordinateFromPadding() {
            int endAfterPadding;
            if (this.mLayoutFromEnd) {
                endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
            } else {
                endAfterPadding = this.mOrientationHelper.getStartAfterPadding();
            }
            this.mCoordinate = endAfterPadding;
        }

        public void assignFromView(View view, int i) {
            if (this.mLayoutFromEnd) {
                this.mCoordinate = this.mOrientationHelper.getTotalSpaceChange() + this.mOrientationHelper.getDecoratedEnd(view);
            } else {
                this.mCoordinate = this.mOrientationHelper.getDecoratedStart(view);
            }
            this.mPosition = i;
        }

        public void assignFromViewAndKeepVisibleRect(View view, int i) {
            int totalSpaceChange = this.mOrientationHelper.getTotalSpaceChange();
            if (totalSpaceChange >= 0) {
                assignFromView(view, i);
                return;
            }
            this.mPosition = i;
            int decoratedMeasurement;
            if (this.mLayoutFromEnd) {
                i = (this.mOrientationHelper.getEndAfterPadding() - totalSpaceChange) - this.mOrientationHelper.getDecoratedEnd(view);
                this.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - i;
                if (i > 0) {
                    decoratedMeasurement = this.mCoordinate - this.mOrientationHelper.getDecoratedMeasurement(view);
                    totalSpaceChange = this.mOrientationHelper.getStartAfterPadding();
                    decoratedMeasurement -= Math.min(this.mOrientationHelper.getDecoratedStart(view) - totalSpaceChange, 0) + totalSpaceChange;
                    if (decoratedMeasurement < 0) {
                        this.mCoordinate = Math.min(i, -decoratedMeasurement) + this.mCoordinate;
                    }
                }
            } else {
                i = this.mOrientationHelper.getDecoratedStart(view);
                decoratedMeasurement = i - this.mOrientationHelper.getStartAfterPadding();
                this.mCoordinate = i;
                if (decoratedMeasurement > 0) {
                    int endAfterPadding = (this.mOrientationHelper.getEndAfterPadding() - Math.min(0, (this.mOrientationHelper.getEndAfterPadding() - totalSpaceChange) - this.mOrientationHelper.getDecoratedEnd(view))) - (this.mOrientationHelper.getDecoratedMeasurement(view) + i);
                    if (endAfterPadding < 0) {
                        this.mCoordinate -= Math.min(decoratedMeasurement, -endAfterPadding);
                    }
                }
            }
        }

        public boolean isViewValidAsAnchor(View view, State state) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            return !layoutParams.isItemRemoved() && layoutParams.getViewLayoutPosition() >= 0 && layoutParams.getViewLayoutPosition() < state.getItemCount();
        }

        public void reset() {
            this.mPosition = -1;
            this.mCoordinate = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mValid = false;
        }

        public String toString() {
            StringBuilder L = a.L("AnchorInfo{mPosition=");
            L.append(this.mPosition);
            L.append(", mCoordinate=");
            L.append(this.mCoordinate);
            L.append(", mLayoutFromEnd=");
            L.append(this.mLayoutFromEnd);
            L.append(", mValid=");
            L.append(this.mValid);
            L.append('}');
            return L.toString();
        }
    }

    public static class LayoutChunkResult {
        public int mConsumed;
        public boolean mFinished;
        public boolean mFocusable;
        public boolean mIgnoreConsumed;

        public void resetInternal() {
            this.mConsumed = 0;
            this.mFinished = false;
            this.mIgnoreConsumed = false;
            this.mFocusable = false;
        }
    }

    public static class LayoutState {
        public static final int INVALID_LAYOUT = Integer.MIN_VALUE;
        public static final int ITEM_DIRECTION_HEAD = -1;
        public static final int ITEM_DIRECTION_TAIL = 1;
        public static final int LAYOUT_END = 1;
        public static final int LAYOUT_START = -1;
        public static final int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;
        public static final String TAG = "LLM#LayoutState";
        public int mAvailable;
        public int mCurrentPosition;
        public int mExtraFillSpace = 0;
        public boolean mInfinite;
        public boolean mIsPreLayout = false;
        public int mItemDirection;
        public int mLastScrollDelta;
        public int mLayoutDirection;
        public int mNoRecycleSpace = 0;
        public int mOffset;
        public boolean mRecycle = true;
        public List<ViewHolder> mScrapList = null;
        public int mScrollingOffset;

        private View nextViewFromScrapList() {
            int size = this.mScrapList.size();
            for (int i = 0; i < size; i++) {
                View view = ((ViewHolder) this.mScrapList.get(i)).itemView;
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                if (!layoutParams.isItemRemoved() && this.mCurrentPosition == layoutParams.getViewLayoutPosition()) {
                    assignPositionFromScrapList(view);
                    return view;
                }
            }
            return null;
        }

        public void assignPositionFromScrapList() {
            assignPositionFromScrapList(null);
        }

        public boolean hasMore(State state) {
            int i = this.mCurrentPosition;
            return i >= 0 && i < state.getItemCount();
        }

        public void log() {
            StringBuilder L = a.L("avail:");
            L.append(this.mAvailable);
            L.append(", ind:");
            L.append(this.mCurrentPosition);
            L.append(", dir:");
            L.append(this.mItemDirection);
            L.append(", offset:");
            L.append(this.mOffset);
            L.append(", layoutDir:");
            L.append(this.mLayoutDirection);
            Log.d(TAG, L.toString());
        }

        public View next(Recycler recycler) {
            if (this.mScrapList != null) {
                return nextViewFromScrapList();
            }
            View viewForPosition = recycler.getViewForPosition(this.mCurrentPosition);
            this.mCurrentPosition += this.mItemDirection;
            return viewForPosition;
        }

        public View nextViewInLimitedList(View view) {
            int size = this.mScrapList.size();
            View view2 = null;
            int i = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < size; i2++) {
                View view3 = ((ViewHolder) this.mScrapList.get(i2)).itemView;
                LayoutParams layoutParams = (LayoutParams) view3.getLayoutParams();
                if (!(view3 == view || layoutParams.isItemRemoved())) {
                    int viewLayoutPosition = (layoutParams.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
                    if (viewLayoutPosition >= 0 && viewLayoutPosition < i) {
                        view2 = view3;
                        if (viewLayoutPosition == 0) {
                            break;
                        }
                        i = viewLayoutPosition;
                    }
                }
            }
            return view2;
        }

        public void assignPositionFromScrapList(View view) {
            view = nextViewInLimitedList(view);
            if (view == null) {
                this.mCurrentPosition = -1;
            } else {
                this.mCurrentPosition = ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
            }
        }
    }

    @SuppressLint({"BanParcelableUsage"})
    @RestrictTo({Scope.LIBRARY})
    public static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public boolean mAnchorLayoutFromEnd;
        public int mAnchorOffset;
        public int mAnchorPosition;

        public int describeContents() {
            return 0;
        }

        public boolean hasValidAnchor() {
            return this.mAnchorPosition >= 0;
        }

        public void invalidateAnchor() {
            this.mAnchorPosition = -1;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mAnchorOffset);
            parcel.writeInt(this.mAnchorLayoutFromEnd);
        }

        public SavedState(Parcel parcel) {
            this.mAnchorPosition = parcel.readInt();
            this.mAnchorOffset = parcel.readInt();
            boolean z = true;
            if (parcel.readInt() != 1) {
                z = false;
            }
            this.mAnchorLayoutFromEnd = z;
        }

        public SavedState(SavedState savedState) {
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mAnchorOffset = savedState.mAnchorOffset;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
        }
    }

    public LinearLayoutManager(Context context) {
        this(context, 1, false);
    }

    private int computeScrollExtent(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        ensureLayoutState();
        OrientationHelper orientationHelper = this.mOrientationHelper;
        View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ 1, true);
        return ScrollbarHelper.computeScrollExtent(state, orientationHelper, findFirstVisibleChildClosestToStart, findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ 1, true), this, this.mSmoothScrollbarEnabled);
    }

    private int computeScrollOffset(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        ensureLayoutState();
        OrientationHelper orientationHelper = this.mOrientationHelper;
        View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ 1, true);
        return ScrollbarHelper.computeScrollOffset(state, orientationHelper, findFirstVisibleChildClosestToStart, findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ 1, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }

    private int computeScrollRange(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        ensureLayoutState();
        OrientationHelper orientationHelper = this.mOrientationHelper;
        View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ 1, true);
        return ScrollbarHelper.computeScrollRange(state, orientationHelper, findFirstVisibleChildClosestToStart, findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ 1, true), this, this.mSmoothScrollbarEnabled);
    }

    private View findFirstPartiallyOrCompletelyInvisibleChild() {
        return findOnePartiallyOrCompletelyInvisibleChild(0, getChildCount());
    }

    private View findFirstReferenceChild(Recycler recycler, State state) {
        return findReferenceChild(recycler, state, 0, getChildCount(), state.getItemCount());
    }

    private View findLastPartiallyOrCompletelyInvisibleChild() {
        return findOnePartiallyOrCompletelyInvisibleChild(getChildCount() - 1, -1);
    }

    private View findLastReferenceChild(Recycler recycler, State state) {
        return findReferenceChild(recycler, state, getChildCount() - 1, -1, state.getItemCount());
    }

    private View findPartiallyOrCompletelyInvisibleChildClosestToEnd() {
        if (this.mShouldReverseLayout) {
            return findFirstPartiallyOrCompletelyInvisibleChild();
        }
        return findLastPartiallyOrCompletelyInvisibleChild();
    }

    private View findPartiallyOrCompletelyInvisibleChildClosestToStart() {
        if (this.mShouldReverseLayout) {
            return findLastPartiallyOrCompletelyInvisibleChild();
        }
        return findFirstPartiallyOrCompletelyInvisibleChild();
    }

    private View findReferenceChildClosestToEnd(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findFirstReferenceChild(recycler, state);
        }
        return findLastReferenceChild(recycler, state);
    }

    private View findReferenceChildClosestToStart(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findLastReferenceChild(recycler, state);
        }
        return findFirstReferenceChild(recycler, state);
    }

    private int fixLayoutEndGap(int i, Recycler recycler, State state, boolean z) {
        int endAfterPadding = this.mOrientationHelper.getEndAfterPadding() - i;
        if (endAfterPadding <= 0) {
            return 0;
        }
        int i2 = -scrollBy(-endAfterPadding, recycler, state);
        i += i2;
        if (z) {
            int endAfterPadding2 = this.mOrientationHelper.getEndAfterPadding() - i;
            if (endAfterPadding2 > 0) {
                this.mOrientationHelper.offsetChildren(endAfterPadding2);
                return endAfterPadding2 + i2;
            }
        }
        return i2;
    }

    private int fixLayoutStartGap(int i, Recycler recycler, State state, boolean z) {
        int startAfterPadding = i - this.mOrientationHelper.getStartAfterPadding();
        if (startAfterPadding <= 0) {
            return 0;
        }
        int i2 = -scrollBy(startAfterPadding, recycler, state);
        i += i2;
        if (z) {
            i -= this.mOrientationHelper.getStartAfterPadding();
            if (i > 0) {
                this.mOrientationHelper.offsetChildren(-i);
                i2 -= i;
            }
        }
        return i2;
    }

    private View getChildClosestToEnd() {
        return getChildAt(this.mShouldReverseLayout ? 0 : getChildCount() - 1);
    }

    private View getChildClosestToStart() {
        return getChildAt(this.mShouldReverseLayout ? getChildCount() - 1 : 0);
    }

    private void layoutForPredictiveAnimations(Recycler recycler, State state, int i, int i2) {
        Recycler recycler2 = recycler;
        State state2 = state;
        if (state.willRunPredictiveAnimations() && getChildCount() != 0 && !state.isPreLayout() && supportsPredictiveItemAnimations()) {
            LayoutState layoutState;
            List scrapList = recycler.getScrapList();
            int size = scrapList.size();
            int position = getPosition(getChildAt(0));
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < size; i5++) {
                ViewHolder viewHolder = (ViewHolder) scrapList.get(i5);
                if (!viewHolder.isRemoved()) {
                    int i6 = 1;
                    if ((viewHolder.getLayoutPosition() < position) != this.mShouldReverseLayout) {
                        i6 = -1;
                    }
                    if (i6 == -1) {
                        i3 += this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView);
                    } else {
                        i4 += this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView);
                    }
                }
            }
            this.mLayoutState.mScrapList = scrapList;
            if (i3 > 0) {
                updateLayoutStateToFillStart(getPosition(getChildClosestToStart()), i);
                layoutState = this.mLayoutState;
                layoutState.mExtraFillSpace = i3;
                layoutState.mAvailable = 0;
                layoutState.assignPositionFromScrapList();
                fill(recycler2, this.mLayoutState, state2, false);
            }
            if (i4 > 0) {
                updateLayoutStateToFillEnd(getPosition(getChildClosestToEnd()), i2);
                layoutState = this.mLayoutState;
                layoutState.mExtraFillSpace = i4;
                layoutState.mAvailable = 0;
                layoutState.assignPositionFromScrapList();
                fill(recycler2, this.mLayoutState, state2, false);
            }
            this.mLayoutState.mScrapList = null;
        }
    }

    private void logChildren() {
        String str = TAG;
        Log.d(str, "internal representation of views on the screen");
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            StringBuilder L = a.L("item ");
            L.append(getPosition(childAt));
            L.append(", coord:");
            L.append(this.mOrientationHelper.getDecoratedStart(childAt));
            Log.d(str, L.toString());
        }
        Log.d(str, "==============");
    }

    private void recycleByLayoutState(Recycler recycler, LayoutState layoutState) {
        if (layoutState.mRecycle && !layoutState.mInfinite) {
            int i = layoutState.mScrollingOffset;
            int i2 = layoutState.mNoRecycleSpace;
            if (layoutState.mLayoutDirection == -1) {
                recycleViewsFromEnd(recycler, i, i2);
            } else {
                recycleViewsFromStart(recycler, i, i2);
            }
        }
    }

    private void recycleChildren(Recycler recycler, int i, int i2) {
        if (i != i2) {
            if (i2 > i) {
                for (i2--; i2 >= i; i2--) {
                    removeAndRecycleViewAt(i2, recycler);
                }
            } else {
                while (i > i2) {
                    removeAndRecycleViewAt(i, recycler);
                    i--;
                }
            }
        }
    }

    private void recycleViewsFromEnd(Recycler recycler, int i, int i2) {
        int childCount = getChildCount();
        if (i >= 0) {
            int end = (this.mOrientationHelper.getEnd() - i) + i2;
            if (this.mShouldReverseLayout) {
                for (i2 = 0; i2 < childCount; i2++) {
                    View childAt = getChildAt(i2);
                    if (this.mOrientationHelper.getDecoratedStart(childAt) < end || this.mOrientationHelper.getTransformedStartWithDecoration(childAt) < end) {
                        recycleChildren(recycler, 0, i2);
                        return;
                    }
                }
            } else {
                childCount--;
                for (i = childCount; i >= 0; i--) {
                    View childAt2 = getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedStart(childAt2) < end || this.mOrientationHelper.getTransformedStartWithDecoration(childAt2) < end) {
                        recycleChildren(recycler, childCount, i);
                        break;
                    }
                }
            }
        }
    }

    private void recycleViewsFromStart(Recycler recycler, int i, int i2) {
        if (i >= 0) {
            i -= i2;
            i2 = getChildCount();
            if (this.mShouldReverseLayout) {
                i2--;
                for (int i3 = i2; i3 >= 0; i3--) {
                    View childAt = getChildAt(i3);
                    if (this.mOrientationHelper.getDecoratedEnd(childAt) > i || this.mOrientationHelper.getTransformedEndWithDecoration(childAt) > i) {
                        recycleChildren(recycler, i2, i3);
                        return;
                    }
                }
            } else {
                for (int i4 = 0; i4 < i2; i4++) {
                    View childAt2 = getChildAt(i4);
                    if (this.mOrientationHelper.getDecoratedEnd(childAt2) > i || this.mOrientationHelper.getTransformedEndWithDecoration(childAt2) > i) {
                        recycleChildren(recycler, 0, i4);
                        break;
                    }
                }
            }
        }
    }

    private void resolveShouldLayoutReverse() {
        if (this.mOrientation == 1 || !isLayoutRTL()) {
            this.mShouldReverseLayout = this.mReverseLayout;
        } else {
            this.mShouldReverseLayout = this.mReverseLayout ^ 1;
        }
    }

    private boolean updateAnchorFromChildren(Recycler recycler, State state, AnchorInfo anchorInfo) {
        boolean z = false;
        if (getChildCount() == 0) {
            return false;
        }
        View focusedChild = getFocusedChild();
        if (focusedChild != null && anchorInfo.isViewValidAsAnchor(focusedChild, state)) {
            anchorInfo.assignFromViewAndKeepVisibleRect(focusedChild, getPosition(focusedChild));
            return true;
        } else if (this.mLastStackFromEnd != this.mStackFromEnd) {
            return false;
        } else {
            View findReferenceChildClosestToEnd;
            if (anchorInfo.mLayoutFromEnd) {
                findReferenceChildClosestToEnd = findReferenceChildClosestToEnd(recycler, state);
            } else {
                findReferenceChildClosestToEnd = findReferenceChildClosestToStart(recycler, state);
            }
            if (findReferenceChildClosestToEnd == null) {
                return false;
            }
            anchorInfo.assignFromView(findReferenceChildClosestToEnd, getPosition(findReferenceChildClosestToEnd));
            if (!state.isPreLayout() && supportsPredictiveItemAnimations()) {
                if (this.mOrientationHelper.getDecoratedStart(findReferenceChildClosestToEnd) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(findReferenceChildClosestToEnd) < this.mOrientationHelper.getStartAfterPadding()) {
                    z = true;
                }
                if (z) {
                    int endAfterPadding;
                    if (anchorInfo.mLayoutFromEnd) {
                        endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
                    } else {
                        endAfterPadding = this.mOrientationHelper.getStartAfterPadding();
                    }
                    anchorInfo.mCoordinate = endAfterPadding;
                }
            }
            return true;
        }
    }

    private boolean updateAnchorFromPendingData(State state, AnchorInfo anchorInfo) {
        boolean z = false;
        if (!state.isPreLayout()) {
            int i = this.mPendingScrollPosition;
            if (i != -1) {
                if (i < 0 || i >= state.getItemCount()) {
                    this.mPendingScrollPosition = -1;
                    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
                } else {
                    anchorInfo.mPosition = this.mPendingScrollPosition;
                    SavedState savedState = this.mPendingSavedState;
                    boolean z2;
                    if (savedState != null && savedState.hasValidAnchor()) {
                        z2 = this.mPendingSavedState.mAnchorLayoutFromEnd;
                        anchorInfo.mLayoutFromEnd = z2;
                        if (z2) {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset;
                        } else {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset;
                        }
                        return true;
                    } else if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
                        View findViewByPosition = findViewByPosition(this.mPendingScrollPosition);
                        if (findViewByPosition == null) {
                            if (getChildCount() > 0) {
                                if ((this.mPendingScrollPosition < getPosition(getChildAt(0))) == this.mShouldReverseLayout) {
                                    z = true;
                                }
                                anchorInfo.mLayoutFromEnd = z;
                            }
                            anchorInfo.assignCoordinateFromPadding();
                        } else if (this.mOrientationHelper.getDecoratedMeasurement(findViewByPosition) > this.mOrientationHelper.getTotalSpace()) {
                            anchorInfo.assignCoordinateFromPadding();
                            return true;
                        } else if (this.mOrientationHelper.getDecoratedStart(findViewByPosition) - this.mOrientationHelper.getStartAfterPadding() < 0) {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
                            anchorInfo.mLayoutFromEnd = false;
                            return true;
                        } else if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(findViewByPosition) < 0) {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
                            anchorInfo.mLayoutFromEnd = true;
                            return true;
                        } else {
                            int totalSpaceChange;
                            if (anchorInfo.mLayoutFromEnd) {
                                totalSpaceChange = this.mOrientationHelper.getTotalSpaceChange() + this.mOrientationHelper.getDecoratedEnd(findViewByPosition);
                            } else {
                                totalSpaceChange = this.mOrientationHelper.getDecoratedStart(findViewByPosition);
                            }
                            anchorInfo.mCoordinate = totalSpaceChange;
                        }
                        return true;
                    } else {
                        z2 = this.mShouldReverseLayout;
                        anchorInfo.mLayoutFromEnd = z2;
                        if (z2) {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset;
                        } else {
                            anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void updateAnchorInfoForLayout(Recycler recycler, State state, AnchorInfo anchorInfo) {
        if (!updateAnchorFromPendingData(state, anchorInfo) && !updateAnchorFromChildren(recycler, state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            anchorInfo.mPosition = this.mStackFromEnd ? state.getItemCount() - 1 : 0;
        }
    }

    private void updateLayoutState(int i, int i2, boolean z, State state) {
        this.mLayoutState.mInfinite = resolveIsInfinite();
        this.mLayoutState.mLayoutDirection = i;
        int[] iArr = this.mReusableIntPair;
        int i3 = 0;
        iArr[0] = 0;
        int i4 = 1;
        iArr[1] = 0;
        calculateExtraLayoutSpace(state, iArr);
        int max = Math.max(0, this.mReusableIntPair[0]);
        int max2 = Math.max(0, this.mReusableIntPair[1]);
        if (i == 1) {
            i3 = 1;
        }
        LayoutState layoutState = this.mLayoutState;
        int i5 = i3 != 0 ? max2 : max;
        layoutState.mExtraFillSpace = i5;
        if (i3 == 0) {
            max = max2;
        }
        layoutState.mNoRecycleSpace = max;
        View childClosestToEnd;
        LayoutState layoutState2;
        LayoutState layoutState3;
        if (i3 != 0) {
            layoutState.mExtraFillSpace = this.mOrientationHelper.getEndPadding() + i5;
            childClosestToEnd = getChildClosestToEnd();
            layoutState2 = this.mLayoutState;
            if (this.mShouldReverseLayout) {
                i4 = -1;
            }
            layoutState2.mItemDirection = i4;
            max = getPosition(childClosestToEnd);
            layoutState3 = this.mLayoutState;
            layoutState2.mCurrentPosition = max + layoutState3.mItemDirection;
            layoutState3.mOffset = this.mOrientationHelper.getDecoratedEnd(childClosestToEnd);
            i = this.mOrientationHelper.getDecoratedEnd(childClosestToEnd) - this.mOrientationHelper.getEndAfterPadding();
        } else {
            childClosestToEnd = getChildClosestToStart();
            layoutState2 = this.mLayoutState;
            layoutState2.mExtraFillSpace = this.mOrientationHelper.getStartAfterPadding() + layoutState2.mExtraFillSpace;
            layoutState2 = this.mLayoutState;
            if (!this.mShouldReverseLayout) {
                i4 = -1;
            }
            layoutState2.mItemDirection = i4;
            max = getPosition(childClosestToEnd);
            layoutState3 = this.mLayoutState;
            layoutState2.mCurrentPosition = max + layoutState3.mItemDirection;
            layoutState3.mOffset = this.mOrientationHelper.getDecoratedStart(childClosestToEnd);
            i = (-this.mOrientationHelper.getDecoratedStart(childClosestToEnd)) + this.mOrientationHelper.getStartAfterPadding();
        }
        LayoutState layoutState4 = this.mLayoutState;
        layoutState4.mAvailable = i2;
        if (z) {
            layoutState4.mAvailable = i2 - i;
        }
        layoutState4.mScrollingOffset = i;
    }

    private void updateLayoutStateToFillEnd(AnchorInfo anchorInfo) {
        updateLayoutStateToFillEnd(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }

    private void updateLayoutStateToFillStart(AnchorInfo anchorInfo) {
        updateLayoutStateToFillStart(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }

    public void assertNotInLayoutOrScroll(String str) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(str);
        }
    }

    public void calculateExtraLayoutSpace(@NonNull State state, @NonNull int[] iArr) {
        int i;
        int extraLayoutSpace = getExtraLayoutSpace(state);
        if (this.mLayoutState.mLayoutDirection == -1) {
            i = 0;
        } else {
            i = extraLayoutSpace;
            extraLayoutSpace = 0;
        }
        iArr[0] = extraLayoutSpace;
        iArr[1] = i;
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == 1;
    }

    public void collectAdjacentPrefetchPositions(int i, int i2, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        if (this.mOrientation != 0) {
            i = i2;
        }
        if (getChildCount() != 0 && i != 0) {
            ensureLayoutState();
            updateLayoutState(i > 0 ? 1 : -1, Math.abs(i), true, state);
            collectPrefetchPositionsForLayoutState(state, this.mLayoutState, layoutPrefetchRegistry);
        }
    }

    public void collectInitialPrefetchPositions(int i, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        boolean z;
        int i2;
        SavedState savedState = this.mPendingSavedState;
        int i3 = -1;
        if (savedState == null || !savedState.hasValidAnchor()) {
            resolveShouldLayoutReverse();
            z = this.mShouldReverseLayout;
            i2 = this.mPendingScrollPosition;
            if (i2 == -1) {
                i2 = z ? i - 1 : 0;
            }
        } else {
            savedState = this.mPendingSavedState;
            z = savedState.mAnchorLayoutFromEnd;
            i2 = savedState.mAnchorPosition;
        }
        if (!z) {
            i3 = 1;
        }
        for (int i4 = 0; i4 < this.mInitialPrefetchItemCount && i2 >= 0 && i2 < i; i4++) {
            layoutPrefetchRegistry.addPosition(i2, 0);
            i2 += i3;
        }
    }

    public void collectPrefetchPositionsForLayoutState(State state, LayoutState layoutState, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i = layoutState.mCurrentPosition;
        if (i >= 0 && i < state.getItemCount()) {
            layoutPrefetchRegistry.addPosition(i, Math.max(0, layoutState.mScrollingOffset));
        }
    }

    public int computeHorizontalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeHorizontalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeHorizontalScrollRange(State state) {
        return computeScrollRange(state);
    }

    public PointF computeScrollVectorForPosition(int i) {
        if (getChildCount() == 0) {
            return null;
        }
        boolean z = false;
        int i2 = 1;
        if (i < getPosition(getChildAt(0))) {
            z = true;
        }
        if (z != this.mShouldReverseLayout) {
            i2 = -1;
        }
        if (this.mOrientation == 0) {
            return new PointF((float) i2, 0.0f);
        }
        return new PointF(0.0f, (float) i2);
    }

    public int computeVerticalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeVerticalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeVerticalScrollRange(State state) {
        return computeScrollRange(state);
    }

    public int convertFocusDirectionToLayoutDirection(int i) {
        int i2 = -1;
        int i3 = 1;
        if (i == 1) {
            return (this.mOrientation != 1 && isLayoutRTL()) ? 1 : -1;
        } else {
            if (i == 2) {
                return (this.mOrientation != 1 && isLayoutRTL()) ? -1 : 1;
            } else {
                if (i == 17) {
                    if (this.mOrientation != 0) {
                        i2 = Integer.MIN_VALUE;
                    }
                    return i2;
                } else if (i == 33) {
                    if (this.mOrientation != 1) {
                        i2 = Integer.MIN_VALUE;
                    }
                    return i2;
                } else if (i == 66) {
                    if (this.mOrientation != 0) {
                        i3 = Integer.MIN_VALUE;
                    }
                    return i3;
                } else if (i != 130) {
                    return Integer.MIN_VALUE;
                } else {
                    if (this.mOrientation != 1) {
                        i3 = Integer.MIN_VALUE;
                    }
                    return i3;
                }
            }
        }
    }

    public LayoutState createLayoutState() {
        return new LayoutState();
    }

    public void ensureLayoutState() {
        if (this.mLayoutState == null) {
            this.mLayoutState = createLayoutState();
        }
    }

    public int fill(Recycler recycler, LayoutState layoutState, State state, boolean z) {
        int i = layoutState.mAvailable;
        int i2 = layoutState.mScrollingOffset;
        if (i2 != Integer.MIN_VALUE) {
            if (i < 0) {
                layoutState.mScrollingOffset = i2 + i;
            }
            recycleByLayoutState(recycler, layoutState);
        }
        i2 = layoutState.mAvailable + layoutState.mExtraFillSpace;
        LayoutChunkResult layoutChunkResult = this.mLayoutChunkResult;
        while (true) {
            if ((!layoutState.mInfinite && i2 <= 0) || !layoutState.hasMore(state)) {
                break;
            }
            layoutChunkResult.resetInternal();
            layoutChunk(recycler, state, layoutState, layoutChunkResult);
            if (!layoutChunkResult.mFinished) {
                int i3;
                int i4;
                layoutState.mOffset = (layoutChunkResult.mConsumed * layoutState.mLayoutDirection) + layoutState.mOffset;
                if (!(layoutChunkResult.mIgnoreConsumed && layoutState.mScrapList == null && state.isPreLayout())) {
                    i3 = layoutState.mAvailable;
                    i4 = layoutChunkResult.mConsumed;
                    layoutState.mAvailable = i3 - i4;
                    i2 -= i4;
                }
                i3 = layoutState.mScrollingOffset;
                if (i3 != Integer.MIN_VALUE) {
                    i3 += layoutChunkResult.mConsumed;
                    layoutState.mScrollingOffset = i3;
                    i4 = layoutState.mAvailable;
                    if (i4 < 0) {
                        layoutState.mScrollingOffset = i3 + i4;
                    }
                    recycleByLayoutState(recycler, layoutState);
                }
                if (z && layoutChunkResult.mFocusable) {
                    break;
                }
            } else {
                break;
            }
        }
        return i - layoutState.mAvailable;
    }

    public int findFirstCompletelyVisibleItemPosition() {
        View findOneVisibleChild = findOneVisibleChild(0, getChildCount(), true, false);
        if (findOneVisibleChild == null) {
            return -1;
        }
        return getPosition(findOneVisibleChild);
    }

    public View findFirstVisibleChildClosestToEnd(boolean z, boolean z2) {
        if (this.mShouldReverseLayout) {
            return findOneVisibleChild(0, getChildCount(), z, z2);
        }
        return findOneVisibleChild(getChildCount() - 1, -1, z, z2);
    }

    public View findFirstVisibleChildClosestToStart(boolean z, boolean z2) {
        if (this.mShouldReverseLayout) {
            return findOneVisibleChild(getChildCount() - 1, -1, z, z2);
        }
        return findOneVisibleChild(0, getChildCount(), z, z2);
    }

    public int findFirstVisibleItemPosition() {
        View findOneVisibleChild = findOneVisibleChild(0, getChildCount(), false, true);
        if (findOneVisibleChild == null) {
            return -1;
        }
        return getPosition(findOneVisibleChild);
    }

    public int findLastCompletelyVisibleItemPosition() {
        View findOneVisibleChild = findOneVisibleChild(getChildCount() - 1, -1, true, false);
        if (findOneVisibleChild == null) {
            return -1;
        }
        return getPosition(findOneVisibleChild);
    }

    public int findLastVisibleItemPosition() {
        View findOneVisibleChild = findOneVisibleChild(getChildCount() - 1, -1, false, true);
        if (findOneVisibleChild == null) {
            return -1;
        }
        return getPosition(findOneVisibleChild);
    }

    public View findOnePartiallyOrCompletelyInvisibleChild(int i, int i2) {
        ensureLayoutState();
        Object obj = i2 > i ? 1 : i2 < i ? -1 : null;
        if (obj == null) {
            return getChildAt(i);
        }
        int i3;
        int i4;
        View findOneViewWithinBoundFlags;
        if (this.mOrientationHelper.getDecoratedStart(getChildAt(i)) < this.mOrientationHelper.getStartAfterPadding()) {
            i3 = 16644;
            i4 = 16388;
        } else {
            i3 = 4161;
            i4 = FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
        }
        if (this.mOrientation == 0) {
            findOneViewWithinBoundFlags = this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(i, i2, i3, i4);
        } else {
            findOneViewWithinBoundFlags = this.mVerticalBoundCheck.findOneViewWithinBoundFlags(i, i2, i3, i4);
        }
        return findOneViewWithinBoundFlags;
    }

    public View findOneVisibleChild(int i, int i2, boolean z, boolean z2) {
        ensureLayoutState();
        int i3 = 320;
        int i4 = z ? 24579 : 320;
        if (!z2) {
            i3 = 0;
        }
        if (this.mOrientation == 0) {
            return this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(i, i2, i4, i3);
        }
        return this.mVerticalBoundCheck.findOneViewWithinBoundFlags(i, i2, i4, i3);
    }

    public View findReferenceChild(Recycler recycler, State state, int i, int i2, int i3) {
        ensureLayoutState();
        int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
        int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
        int i4 = i2 > i ? 1 : -1;
        View view = null;
        View view2 = null;
        while (i != i2) {
            View childAt = getChildAt(i);
            int position = getPosition(childAt);
            if (position >= 0 && position < i3) {
                if (((LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
                    if (view2 == null) {
                        view2 = childAt;
                    }
                } else if (this.mOrientationHelper.getDecoratedStart(childAt) < endAfterPadding && this.mOrientationHelper.getDecoratedEnd(childAt) >= startAfterPadding) {
                    return childAt;
                } else {
                    if (view == null) {
                        view = childAt;
                    }
                }
            }
            i += i4;
        }
        if (view == null) {
            view = view2;
        }
        return view;
    }

    public View findViewByPosition(int i) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        int position = i - getPosition(getChildAt(0));
        if (position >= 0 && position < childCount) {
            View childAt = getChildAt(position);
            if (getPosition(childAt) == i) {
                return childAt;
            }
        }
        return super.findViewByPosition(i);
    }

    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Deprecated
    public int getExtraLayoutSpace(State state) {
        return state.hasTargetScrollPosition() ? this.mOrientationHelper.getTotalSpace() : 0;
    }

    public int getInitialPrefetchItemCount() {
        return this.mInitialPrefetchItemCount;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public boolean getRecycleChildrenOnDetach() {
        return this.mRecycleChildrenOnDetach;
    }

    public boolean getReverseLayout() {
        return this.mReverseLayout;
    }

    public boolean getStackFromEnd() {
        return this.mStackFromEnd;
    }

    public boolean isAutoMeasureEnabled() {
        return true;
    }

    public boolean isLayoutRTL() {
        return getLayoutDirection() == 1;
    }

    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    public void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult layoutChunkResult) {
        View next = layoutState.next(recycler);
        if (next == null) {
            layoutChunkResult.mFinished = true;
            return;
        }
        int decoratedMeasurementInOther;
        int i;
        int i2;
        int i3;
        LayoutParams layoutParams = (LayoutParams) next.getLayoutParams();
        if (layoutState.mScrapList == null) {
            if (this.mShouldReverseLayout == (layoutState.mLayoutDirection == -1)) {
                addView(next);
            } else {
                addView(next, 0);
            }
        } else {
            if (this.mShouldReverseLayout == (layoutState.mLayoutDirection == -1)) {
                addDisappearingView(next);
            } else {
                addDisappearingView(next, 0);
            }
        }
        measureChildWithMargins(next, 0, 0);
        layoutChunkResult.mConsumed = this.mOrientationHelper.getDecoratedMeasurement(next);
        int width;
        int i4;
        if (this.mOrientation == 1) {
            if (isLayoutRTL()) {
                width = getWidth() - getPaddingRight();
                decoratedMeasurementInOther = width - this.mOrientationHelper.getDecoratedMeasurementInOther(next);
            } else {
                decoratedMeasurementInOther = getPaddingLeft();
                width = this.mOrientationHelper.getDecoratedMeasurementInOther(next) + decoratedMeasurementInOther;
            }
            if (layoutState.mLayoutDirection == -1) {
                i4 = layoutState.mOffset;
                i = i4;
                i2 = width;
                i3 = i4 - layoutChunkResult.mConsumed;
            } else {
                i4 = layoutState.mOffset;
                i3 = i4;
                i2 = width;
                i = layoutChunkResult.mConsumed + i4;
            }
        } else {
            width = getPaddingTop();
            decoratedMeasurementInOther = this.mOrientationHelper.getDecoratedMeasurementInOther(next) + width;
            if (layoutState.mLayoutDirection == -1) {
                i4 = layoutState.mOffset;
                i2 = i4;
                i3 = width;
                i = decoratedMeasurementInOther;
                decoratedMeasurementInOther = i4 - layoutChunkResult.mConsumed;
            } else {
                i4 = layoutState.mOffset;
                i3 = width;
                i2 = layoutChunkResult.mConsumed + i4;
                i = decoratedMeasurementInOther;
                decoratedMeasurementInOther = i4;
            }
        }
        layoutDecoratedWithMargins(next, decoratedMeasurementInOther, i3, i2, i);
        if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
            layoutChunkResult.mIgnoreConsumed = true;
        }
        layoutChunkResult.mFocusable = next.hasFocusable();
    }

    public void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
    }

    public void onDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        if (this.mRecycleChildrenOnDetach) {
            removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }

    public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
        resolveShouldLayoutReverse();
        if (getChildCount() == 0) {
            return null;
        }
        int convertFocusDirectionToLayoutDirection = convertFocusDirectionToLayoutDirection(i);
        if (convertFocusDirectionToLayoutDirection == Integer.MIN_VALUE) {
            return null;
        }
        View findPartiallyOrCompletelyInvisibleChildClosestToStart;
        ensureLayoutState();
        updateLayoutState(convertFocusDirectionToLayoutDirection, (int) (((float) this.mOrientationHelper.getTotalSpace()) * MAX_SCROLL_FACTOR), false, state);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mScrollingOffset = Integer.MIN_VALUE;
        layoutState.mRecycle = false;
        fill(recycler, layoutState, state, true);
        if (convertFocusDirectionToLayoutDirection == -1) {
            findPartiallyOrCompletelyInvisibleChildClosestToStart = findPartiallyOrCompletelyInvisibleChildClosestToStart();
        } else {
            findPartiallyOrCompletelyInvisibleChildClosestToStart = findPartiallyOrCompletelyInvisibleChildClosestToEnd();
        }
        if (convertFocusDirectionToLayoutDirection == -1) {
            view = getChildClosestToStart();
        } else {
            view = getChildClosestToEnd();
        }
        if (!view.hasFocusable()) {
            return findPartiallyOrCompletelyInvisibleChildClosestToStart;
        }
        if (findPartiallyOrCompletelyInvisibleChildClosestToStart == null) {
            return null;
        }
        return view;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (getChildCount() > 0) {
            accessibilityEvent.setFromIndex(findFirstVisibleItemPosition());
            accessibilityEvent.setToIndex(findLastVisibleItemPosition());
        }
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        int i = -1;
        if (!(this.mPendingSavedState == null && this.mPendingScrollPosition == -1) && state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        AnchorInfo anchorInfo;
        int i2;
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null && savedState.hasValidAnchor()) {
            this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
        }
        ensureLayoutState();
        this.mLayoutState.mRecycle = false;
        resolveShouldLayoutReverse();
        View focusedChild = getFocusedChild();
        AnchorInfo anchorInfo2 = this.mAnchorInfo;
        if (!anchorInfo2.mValid || this.mPendingScrollPosition != -1 || this.mPendingSavedState != null) {
            anchorInfo2.reset();
            anchorInfo = this.mAnchorInfo;
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout ^ this.mStackFromEnd;
            updateAnchorInfoForLayout(recycler, state, anchorInfo);
            this.mAnchorInfo.mValid = true;
        } else if (focusedChild != null && (this.mOrientationHelper.getDecoratedStart(focusedChild) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(focusedChild) <= this.mOrientationHelper.getStartAfterPadding())) {
            this.mAnchorInfo.assignFromViewAndKeepVisibleRect(focusedChild, getPosition(focusedChild));
        }
        LayoutState layoutState = this.mLayoutState;
        layoutState.mLayoutDirection = layoutState.mLastScrollDelta >= 0 ? 1 : -1;
        int[] iArr = this.mReusableIntPair;
        iArr[0] = 0;
        iArr[1] = 0;
        calculateExtraLayoutSpace(state, iArr);
        int startAfterPadding = this.mOrientationHelper.getStartAfterPadding() + Math.max(0, this.mReusableIntPair[0]);
        int endPadding = this.mOrientationHelper.getEndPadding() + Math.max(0, this.mReusableIntPair[1]);
        if (state.isPreLayout()) {
            i2 = this.mPendingScrollPosition;
            if (!(i2 == -1 || this.mPendingScrollPositionOffset == Integer.MIN_VALUE)) {
                focusedChild = findViewByPosition(i2);
                if (focusedChild != null) {
                    int endAfterPadding;
                    if (this.mShouldReverseLayout) {
                        endAfterPadding = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(focusedChild);
                        i2 = this.mPendingScrollPositionOffset;
                    } else {
                        i2 = this.mOrientationHelper.getDecoratedStart(focusedChild) - this.mOrientationHelper.getStartAfterPadding();
                        endAfterPadding = this.mPendingScrollPositionOffset;
                    }
                    endAfterPadding -= i2;
                    if (endAfterPadding > 0) {
                        startAfterPadding += endAfterPadding;
                    } else {
                        endPadding -= endAfterPadding;
                    }
                }
            }
        }
        anchorInfo = this.mAnchorInfo;
        if (anchorInfo.mLayoutFromEnd ? this.mShouldReverseLayout : !this.mShouldReverseLayout) {
            i = 1;
        }
        onAnchorReady(recycler, state, anchorInfo, i);
        detachAndScrapAttachedViews(recycler);
        this.mLayoutState.mInfinite = resolveIsInfinite();
        this.mLayoutState.mIsPreLayout = state.isPreLayout();
        this.mLayoutState.mNoRecycleSpace = 0;
        anchorInfo = this.mAnchorInfo;
        LayoutState layoutState2;
        if (anchorInfo.mLayoutFromEnd) {
            updateLayoutStateToFillStart(anchorInfo);
            layoutState = this.mLayoutState;
            layoutState.mExtraFillSpace = startAfterPadding;
            fill(recycler, layoutState, state, false);
            layoutState = this.mLayoutState;
            i = layoutState.mOffset;
            startAfterPadding = layoutState.mCurrentPosition;
            i2 = layoutState.mAvailable;
            if (i2 > 0) {
                endPadding += i2;
            }
            updateLayoutStateToFillEnd(this.mAnchorInfo);
            layoutState = this.mLayoutState;
            layoutState.mExtraFillSpace = endPadding;
            layoutState.mCurrentPosition += layoutState.mItemDirection;
            fill(recycler, layoutState, state, false);
            layoutState = this.mLayoutState;
            endPadding = layoutState.mOffset;
            i2 = layoutState.mAvailable;
            if (i2 > 0) {
                updateLayoutStateToFillStart(startAfterPadding, i);
                layoutState2 = this.mLayoutState;
                layoutState2.mExtraFillSpace = i2;
                fill(recycler, layoutState2, state, false);
                i = this.mLayoutState.mOffset;
            }
        } else {
            updateLayoutStateToFillEnd(anchorInfo);
            layoutState = this.mLayoutState;
            layoutState.mExtraFillSpace = endPadding;
            fill(recycler, layoutState, state, false);
            layoutState = this.mLayoutState;
            endPadding = layoutState.mOffset;
            i = layoutState.mCurrentPosition;
            i2 = layoutState.mAvailable;
            if (i2 > 0) {
                startAfterPadding += i2;
            }
            updateLayoutStateToFillStart(this.mAnchorInfo);
            layoutState = this.mLayoutState;
            layoutState.mExtraFillSpace = startAfterPadding;
            layoutState.mCurrentPosition += layoutState.mItemDirection;
            fill(recycler, layoutState, state, false);
            layoutState = this.mLayoutState;
            startAfterPadding = layoutState.mOffset;
            i2 = layoutState.mAvailable;
            if (i2 > 0) {
                updateLayoutStateToFillEnd(i, endPadding);
                layoutState2 = this.mLayoutState;
                layoutState2.mExtraFillSpace = i2;
                fill(recycler, layoutState2, state, false);
                endPadding = this.mLayoutState.mOffset;
            }
            i = startAfterPadding;
        }
        if (getChildCount() > 0) {
            if ((this.mShouldReverseLayout ^ this.mStackFromEnd) != 0) {
                i2 = fixLayoutEndGap(endPadding, recycler, state, true);
                i += i2;
                endPadding += i2;
                i2 = fixLayoutStartGap(i, recycler, state, false);
            } else {
                i2 = fixLayoutStartGap(i, recycler, state, true);
                i += i2;
                endPadding += i2;
                i2 = fixLayoutEndGap(endPadding, recycler, state, false);
            }
            i += i2;
            endPadding += i2;
        }
        layoutForPredictiveAnimations(recycler, state, i, endPadding);
        if (state.isPreLayout()) {
            this.mAnchorInfo.reset();
        } else {
            this.mOrientationHelper.onLayoutComplete();
        }
        this.mLastStackFromEnd = this.mStackFromEnd;
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingSavedState = null;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mAnchorInfo.reset();
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.mPendingSavedState = (SavedState) parcelable;
            requestLayout();
        }
    }

    public Parcelable onSaveInstanceState() {
        if (this.mPendingSavedState != null) {
            return new SavedState(this.mPendingSavedState);
        }
        SavedState savedState = new SavedState();
        if (getChildCount() > 0) {
            ensureLayoutState();
            int i = this.mLastStackFromEnd ^ this.mShouldReverseLayout;
            savedState.mAnchorLayoutFromEnd = i;
            View childClosestToEnd;
            if (i != 0) {
                childClosestToEnd = getChildClosestToEnd();
                savedState.mAnchorOffset = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(childClosestToEnd);
                savedState.mAnchorPosition = getPosition(childClosestToEnd);
            } else {
                childClosestToEnd = getChildClosestToStart();
                savedState.mAnchorPosition = getPosition(childClosestToEnd);
                savedState.mAnchorOffset = this.mOrientationHelper.getDecoratedStart(childClosestToEnd) - this.mOrientationHelper.getStartAfterPadding();
            }
        } else {
            savedState.invalidateAnchor();
        }
        return savedState;
    }

    public void prepareForDrop(@NonNull View view, @NonNull View view2, int i, int i2) {
        assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
        ensureLayoutState();
        resolveShouldLayoutReverse();
        i = getPosition(view);
        i2 = getPosition(view2);
        i = i < i2 ? 1 : -1;
        if (this.mShouldReverseLayout) {
            if (i == 1) {
                scrollToPositionWithOffset(i2, this.mOrientationHelper.getEndAfterPadding() - (this.mOrientationHelper.getDecoratedMeasurement(view) + this.mOrientationHelper.getDecoratedStart(view2)));
            } else {
                scrollToPositionWithOffset(i2, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(view2));
            }
        } else if (i == -1) {
            scrollToPositionWithOffset(i2, this.mOrientationHelper.getDecoratedStart(view2));
        } else {
            scrollToPositionWithOffset(i2, this.mOrientationHelper.getDecoratedEnd(view2) - this.mOrientationHelper.getDecoratedMeasurement(view));
        }
    }

    public boolean resolveIsInfinite() {
        return this.mOrientationHelper.getMode() == 0 && this.mOrientationHelper.getEnd() == 0;
    }

    public int scrollBy(int i, Recycler recycler, State state) {
        if (getChildCount() == 0 || i == 0) {
            return 0;
        }
        ensureLayoutState();
        this.mLayoutState.mRecycle = true;
        int i2 = i > 0 ? 1 : -1;
        int abs = Math.abs(i);
        updateLayoutState(i2, abs, true, state);
        LayoutState layoutState = this.mLayoutState;
        int fill = fill(recycler, layoutState, state, false) + layoutState.mScrollingOffset;
        if (fill < 0) {
            return 0;
        }
        if (abs > fill) {
            i = i2 * fill;
        }
        this.mOrientationHelper.offsetChildren(-i);
        this.mLayoutState.mLastScrollDelta = i;
        return i;
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return 0;
        }
        return scrollBy(i, recycler, state);
    }

    public void scrollToPosition(int i) {
        this.mPendingScrollPosition = i;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            savedState.invalidateAnchor();
        }
        requestLayout();
    }

    public void scrollToPositionWithOffset(int i, int i2) {
        this.mPendingScrollPosition = i;
        this.mPendingScrollPositionOffset = i2;
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            savedState.invalidateAnchor();
        }
        requestLayout();
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return 0;
        }
        return scrollBy(i, recycler, state);
    }

    public void setInitialPrefetchItemCount(int i) {
        this.mInitialPrefetchItemCount = i;
    }

    public void setOrientation(int i) {
        if (i == 0 || i == 1) {
            assertNotInLayoutOrScroll(null);
            if (i != this.mOrientation || this.mOrientationHelper == null) {
                OrientationHelper createOrientationHelper = OrientationHelper.createOrientationHelper(this, i);
                this.mOrientationHelper = createOrientationHelper;
                this.mAnchorInfo.mOrientationHelper = createOrientationHelper;
                this.mOrientation = i;
                requestLayout();
                return;
            }
            return;
        }
        throw new IllegalArgumentException(a.k("invalid orientation:", i));
    }

    public void setRecycleChildrenOnDetach(boolean z) {
        this.mRecycleChildrenOnDetach = z;
    }

    public void setReverseLayout(boolean z) {
        assertNotInLayoutOrScroll(null);
        if (z != this.mReverseLayout) {
            this.mReverseLayout = z;
            requestLayout();
        }
    }

    public void setSmoothScrollbarEnabled(boolean z) {
        this.mSmoothScrollbarEnabled = z;
    }

    public void setStackFromEnd(boolean z) {
        assertNotInLayoutOrScroll(null);
        if (this.mStackFromEnd != z) {
            this.mStackFromEnd = z;
            requestLayout();
        }
    }

    public boolean shouldMeasureTwice() {
        return (getHeightMode() == BasicMeasure.EXACTLY || getWidthMode() == BasicMeasure.EXACTLY || !hasFlexibleChildInBothOrientations()) ? false : true;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(i);
        startSmoothScroll(linearSmoothScroller);
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && this.mLastStackFromEnd == this.mStackFromEnd;
    }

    public void validateChildOrder() {
        StringBuilder L = a.L("validating child count ");
        L.append(getChildCount());
        Log.d(TAG, L.toString());
        boolean z = true;
        if (getChildCount() >= 1) {
            int position = getPosition(getChildAt(0));
            int decoratedStart = this.mOrientationHelper.getDecoratedStart(getChildAt(0));
            String str = "detected invalid location";
            String str2 = "detected invalid position. loc invalid? ";
            int i;
            View childAt;
            int position2;
            int decoratedStart2;
            StringBuilder L2;
            if (this.mShouldReverseLayout) {
                i = 1;
                while (i < getChildCount()) {
                    childAt = getChildAt(i);
                    position2 = getPosition(childAt);
                    decoratedStart2 = this.mOrientationHelper.getDecoratedStart(childAt);
                    if (position2 < position) {
                        logChildren();
                        L2 = a.L(str2);
                        if (decoratedStart2 >= decoratedStart) {
                            z = false;
                        }
                        L2.append(z);
                        throw new RuntimeException(L2.toString());
                    } else if (decoratedStart2 <= decoratedStart) {
                        i++;
                    } else {
                        logChildren();
                        throw new RuntimeException(str);
                    }
                }
            }
            i = 1;
            while (i < getChildCount()) {
                childAt = getChildAt(i);
                position2 = getPosition(childAt);
                decoratedStart2 = this.mOrientationHelper.getDecoratedStart(childAt);
                if (position2 < position) {
                    logChildren();
                    L2 = a.L(str2);
                    if (decoratedStart2 >= decoratedStart) {
                        z = false;
                    }
                    L2.append(z);
                    throw new RuntimeException(L2.toString());
                } else if (decoratedStart2 >= decoratedStart) {
                    i++;
                } else {
                    logChildren();
                    throw new RuntimeException(str);
                }
            }
        }
    }

    public LinearLayoutManager(Context context, int i, boolean z) {
        this.mOrientation = 1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mStackFromEnd = false;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialPrefetchItemCount = 2;
        this.mReusableIntPair = new int[2];
        setOrientation(i);
        setReverseLayout(z);
    }

    private void updateLayoutStateToFillEnd(int i, int i2) {
        this.mLayoutState.mAvailable = this.mOrientationHelper.getEndAfterPadding() - i2;
        LayoutState layoutState = this.mLayoutState;
        layoutState.mItemDirection = this.mShouldReverseLayout ? -1 : 1;
        layoutState.mCurrentPosition = i;
        layoutState.mLayoutDirection = 1;
        layoutState.mOffset = i2;
        layoutState.mScrollingOffset = Integer.MIN_VALUE;
    }

    private void updateLayoutStateToFillStart(int i, int i2) {
        this.mLayoutState.mAvailable = i2 - this.mOrientationHelper.getStartAfterPadding();
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition = i;
        layoutState.mItemDirection = this.mShouldReverseLayout ? 1 : -1;
        layoutState.mLayoutDirection = -1;
        layoutState.mOffset = i2;
        layoutState.mScrollingOffset = Integer.MIN_VALUE;
    }

    public LinearLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mOrientation = 1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mStackFromEnd = false;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialPrefetchItemCount = 2;
        this.mReusableIntPair = new int[2];
        Properties properties = LayoutManager.getProperties(context, attributeSet, i, i2);
        setOrientation(properties.orientation);
        setReverseLayout(properties.reverseLayout);
        setStackFromEnd(properties.stackFromEnd);
    }
}
