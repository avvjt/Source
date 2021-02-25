package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager.AnchorInfo;
import androidx.recyclerview.widget.LinearLayoutManager.LayoutChunkResult;
import androidx.recyclerview.widget.LinearLayoutManager.LayoutState;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.State;
import f.d.b.a.a;
import java.util.Arrays;

public class GridLayoutManager extends LinearLayoutManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SPAN_COUNT = -1;
    private static final String TAG = "GridLayoutManager";
    public int[] mCachedBorders;
    public final Rect mDecorInsets = new Rect();
    public boolean mPendingSpanCountChange = false;
    public final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
    public final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
    public View[] mSet;
    public int mSpanCount = -1;
    public SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();
    private boolean mUsingSpansToEstimateScrollBarDimensions;

    public static abstract class SpanSizeLookup {
        private boolean mCacheSpanGroupIndices = false;
        private boolean mCacheSpanIndices = false;
        public final SparseIntArray mSpanGroupIndexCache = new SparseIntArray();
        public final SparseIntArray mSpanIndexCache = new SparseIntArray();

        public static int findFirstKeyLessThan(SparseIntArray sparseIntArray, int i) {
            int size = sparseIntArray.size() - 1;
            int i2 = 0;
            while (i2 <= size) {
                int i3 = (i2 + size) >>> 1;
                if (sparseIntArray.keyAt(i3) < i) {
                    i2 = i3 + 1;
                } else {
                    size = i3 - 1;
                }
            }
            i2--;
            return (i2 < 0 || i2 >= sparseIntArray.size()) ? -1 : sparseIntArray.keyAt(i2);
        }

        public int getCachedSpanGroupIndex(int i, int i2) {
            if (!this.mCacheSpanGroupIndices) {
                return getSpanGroupIndex(i, i2);
            }
            int i3 = this.mSpanGroupIndexCache.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            i2 = getSpanGroupIndex(i, i2);
            this.mSpanGroupIndexCache.put(i, i2);
            return i2;
        }

        public int getCachedSpanIndex(int i, int i2) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(i, i2);
            }
            int i3 = this.mSpanIndexCache.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            i2 = getSpanIndex(i, i2);
            this.mSpanIndexCache.put(i, i2);
            return i2;
        }

        /* JADX WARNING: Removed duplicated region for block: B:10:0x002e  */
        /* JADX WARNING: Removed duplicated region for block: B:24:? A:{SYNTHETIC, RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x0044  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getSpanGroupIndex(int i, int i2) {
            int findFirstKeyLessThan;
            int i3;
            int i4;
            int spanSize;
            if (this.mCacheSpanGroupIndices) {
                findFirstKeyLessThan = findFirstKeyLessThan(this.mSpanGroupIndexCache, i);
                if (findFirstKeyLessThan != -1) {
                    i3 = this.mSpanGroupIndexCache.get(findFirstKeyLessThan);
                    i4 = findFirstKeyLessThan + 1;
                    findFirstKeyLessThan = getSpanSize(findFirstKeyLessThan) + getCachedSpanIndex(findFirstKeyLessThan, i2);
                    if (findFirstKeyLessThan == i2) {
                        i3++;
                        findFirstKeyLessThan = 0;
                    }
                    spanSize = getSpanSize(i);
                    for (i4 = 
/*
Method generation error in method: androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup.getSpanGroupIndex(int, int):int, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r3_2 'i4' int) = (r3_0 'i4' int), (r3_0 'i4' int), (r3_1 'i4' int) binds: {(r3_0 'i4' int)=B:5:0x001f, (r3_0 'i4' int)=B:6:0x0021, (r3_1 'i4' int)=B:7:0x0025} in method: androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup.getSpanGroupIndex(int, int):int, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:234)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 28 more

*/

        /* JADX WARNING: Removed duplicated region for block: B:10:0x0024  */
        /* JADX WARNING: Removed duplicated region for block: B:10:0x0024  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0037 A:{RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x0036 A:{RETURN} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getSpanIndex(int i, int i2) {
            int spanSize = getSpanSize(i);
            if (spanSize == i2) {
                return 0;
            }
            int findFirstKeyLessThan;
            int spanSize2;
            if (this.mCacheSpanIndices) {
                findFirstKeyLessThan = findFirstKeyLessThan(this.mSpanIndexCache, i);
                if (findFirstKeyLessThan >= 0) {
                    spanSize2 = getSpanSize(findFirstKeyLessThan) + this.mSpanIndexCache.get(findFirstKeyLessThan);
                    findFirstKeyLessThan++;
                    if (findFirstKeyLessThan < i) {
                        int spanSize3 = getSpanSize(findFirstKeyLessThan);
                        spanSize2 += spanSize3;
                        if (spanSize2 == i2) {
                            spanSize2 = 0;
                        } else if (spanSize2 > i2) {
                            spanSize2 = spanSize3;
                        }
                        findFirstKeyLessThan++;
                        if (findFirstKeyLessThan < i) {
                        }
                    }
                    if (spanSize + spanSize2 > i2) {
                        return spanSize2;
                    }
                    return 0;
                }
            }
            findFirstKeyLessThan = 0;
            spanSize2 = 0;
            if (findFirstKeyLessThan < i) {
            }
            if (spanSize + spanSize2 > i2) {
            }
        }

        public abstract int getSpanSize(int i);

        public void invalidateSpanGroupIndexCache() {
            this.mSpanGroupIndexCache.clear();
        }

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanGroupIndexCacheEnabled() {
            return this.mCacheSpanGroupIndices;
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        public void setSpanGroupIndexCacheEnabled(boolean z) {
            if (!z) {
                this.mSpanGroupIndexCache.clear();
            }
            this.mCacheSpanGroupIndices = z;
        }

        public void setSpanIndexCacheEnabled(boolean z) {
            if (!z) {
                this.mSpanGroupIndexCache.clear();
            }
            this.mCacheSpanIndices = z;
        }
    }

    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        public int getSpanIndex(int i, int i2) {
            return i % i2;
        }

        public int getSpanSize(int i) {
            return 1;
        }
    }

    public static class LayoutParams extends androidx.recyclerview.widget.RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        public int mSpanIndex = -1;
        public int mSpanSize = 0;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public int getSpanIndex() {
            return this.mSpanIndex;
        }

        public int getSpanSize() {
            return this.mSpanSize;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(androidx.recyclerview.widget.RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    public GridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setSpanCount(LayoutManager.getProperties(context, attributeSet, i, i2).spanCount);
    }

    private void assignSpans(Recycler recycler, State state, int i, boolean z) {
        int i2;
        int i3 = 0;
        int i4 = -1;
        if (z) {
            i4 = i;
            i = 0;
            i2 = 1;
        } else {
            i--;
            i2 = -1;
        }
        while (i != i4) {
            View view = this.mSet[i];
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int spanSize = getSpanSize(recycler, state, getPosition(view));
            layoutParams.mSpanSize = spanSize;
            layoutParams.mSpanIndex = i3;
            i3 += spanSize;
            i += i2;
        }
    }

    private void cachePreLayoutSpanMapping() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
            int viewLayoutPosition = layoutParams.getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewLayoutPosition, layoutParams.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewLayoutPosition, layoutParams.getSpanIndex());
        }
    }

    private void calculateItemBorders(int i) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, i);
    }

    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private int computeScrollOffsetWithSpanInfo(State state) {
        if (!(getChildCount() == 0 || state.getItemCount() == 0)) {
            ensureLayoutState();
            boolean isSmoothScrollbarEnabled = isSmoothScrollbarEnabled();
            View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(isSmoothScrollbarEnabled ^ 1, true);
            View findFirstVisibleChildClosestToEnd = findFirstVisibleChildClosestToEnd(isSmoothScrollbarEnabled ^ 1, true);
            if (!(findFirstVisibleChildClosestToStart == null || findFirstVisibleChildClosestToEnd == null)) {
                int cachedSpanGroupIndex = this.mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(findFirstVisibleChildClosestToStart), this.mSpanCount);
                int cachedSpanGroupIndex2 = this.mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(findFirstVisibleChildClosestToEnd), this.mSpanCount);
                int min = Math.min(cachedSpanGroupIndex, cachedSpanGroupIndex2);
                cachedSpanGroupIndex = Math.max(cachedSpanGroupIndex, cachedSpanGroupIndex2);
                int cachedSpanGroupIndex3 = this.mSpanSizeLookup.getCachedSpanGroupIndex(state.getItemCount() - 1, this.mSpanCount) + 1;
                if (this.mShouldReverseLayout) {
                    cachedSpanGroupIndex3 = Math.max(0, (cachedSpanGroupIndex3 - cachedSpanGroupIndex) - 1);
                } else {
                    cachedSpanGroupIndex3 = Math.max(0, min);
                }
                if (!isSmoothScrollbarEnabled) {
                    return cachedSpanGroupIndex3;
                }
                return Math.round((((float) cachedSpanGroupIndex3) * (((float) Math.abs(this.mOrientationHelper.getDecoratedEnd(findFirstVisibleChildClosestToEnd) - this.mOrientationHelper.getDecoratedStart(findFirstVisibleChildClosestToStart))) / ((float) ((this.mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(findFirstVisibleChildClosestToEnd), this.mSpanCount) - this.mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(findFirstVisibleChildClosestToStart), this.mSpanCount)) + 1)))) + ((float) (this.mOrientationHelper.getStartAfterPadding() - this.mOrientationHelper.getDecoratedStart(findFirstVisibleChildClosestToStart))));
            }
        }
        return 0;
    }

    private int computeScrollRangeWithSpanInfo(State state) {
        if (!(getChildCount() == 0 || state.getItemCount() == 0)) {
            ensureLayoutState();
            View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(isSmoothScrollbarEnabled() ^ 1, true);
            View findFirstVisibleChildClosestToEnd = findFirstVisibleChildClosestToEnd(isSmoothScrollbarEnabled() ^ 1, true);
            if (!(findFirstVisibleChildClosestToStart == null || findFirstVisibleChildClosestToEnd == null)) {
                if (!isSmoothScrollbarEnabled()) {
                    return this.mSpanSizeLookup.getCachedSpanGroupIndex(state.getItemCount() - 1, this.mSpanCount) + 1;
                }
                int decoratedEnd = this.mOrientationHelper.getDecoratedEnd(findFirstVisibleChildClosestToEnd) - this.mOrientationHelper.getDecoratedStart(findFirstVisibleChildClosestToStart);
                int cachedSpanGroupIndex = this.mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(findFirstVisibleChildClosestToStart), this.mSpanCount);
                return (int) ((((float) decoratedEnd) / ((float) ((this.mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(findFirstVisibleChildClosestToEnd), this.mSpanCount) - cachedSpanGroupIndex) + 1))) * ((float) (this.mSpanSizeLookup.getCachedSpanGroupIndex(state.getItemCount() - 1, this.mSpanCount) + 1)));
            }
        }
        return 0;
    }

    private void ensureAnchorIsInCorrectSpan(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        Object obj = i == 1 ? 1 : null;
        int spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (obj != null) {
            while (spanIndex > 0) {
                i = anchorInfo.mPosition;
                if (i > 0) {
                    i--;
                    anchorInfo.mPosition = i;
                    spanIndex = getSpanIndex(recycler, state, i);
                } else {
                    return;
                }
            }
            return;
        }
        i = state.getItemCount() - 1;
        int i2 = anchorInfo.mPosition;
        while (i2 < i) {
            int i3 = i2 + 1;
            int spanIndex2 = getSpanIndex(recycler, state, i3);
            if (spanIndex2 <= spanIndex) {
                break;
            }
            i2 = i3;
            spanIndex = spanIndex2;
        }
        anchorInfo.mPosition = i2;
    }

    private void ensureViewSet() {
        View[] viewArr = this.mSet;
        if (viewArr == null || viewArr.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }

    private int getSpanGroupIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanGroupIndex(i, this.mSpanCount);
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getCachedSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        a.Y("Cannot find span size for pre layout position. ", i, TAG);
        return 0;
    }

    private int getSpanIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        int i2 = this.mPreLayoutSpanIndexCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getCachedSpanIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        a.Y("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:", i, TAG);
        return 0;
    }

    private int getSpanSize(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        int i2 = this.mPreLayoutSpanSizeCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanSize(convertPreLayoutPositionToPostLayout);
        }
        a.Y("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:", i, TAG);
        return 1;
    }

    private void guessMeasurement(float f, int i) {
        calculateItemBorders(Math.max(Math.round(f * ((float) this.mSpanCount)), i));
    }

    private void measureChild(View view, int i, boolean z) {
        int childMeasureSpec;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        Rect rect = layoutParams.mDecorInsets;
        int i2 = ((rect.top + rect.bottom) + layoutParams.topMargin) + layoutParams.bottomMargin;
        int i3 = ((rect.left + rect.right) + layoutParams.leftMargin) + layoutParams.rightMargin;
        int spaceForSpanRange = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
        if (this.mOrientation == 1) {
            i = LayoutManager.getChildMeasureSpec(spaceForSpanRange, i, i3, layoutParams.width, false);
            childMeasureSpec = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), i2, layoutParams.height, true);
        } else {
            i = LayoutManager.getChildMeasureSpec(spaceForSpanRange, i, i2, layoutParams.height, false);
            int childMeasureSpec2 = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), i3, layoutParams.width, true);
            childMeasureSpec = i;
            i = childMeasureSpec2;
        }
        measureChildWithDecorationsAndMargin(view, i, childMeasureSpec, z);
    }

    private void measureChildWithDecorationsAndMargin(View view, int i, int i2, boolean z) {
        androidx.recyclerview.widget.RecyclerView.LayoutParams layoutParams = (androidx.recyclerview.widget.RecyclerView.LayoutParams) view.getLayoutParams();
        if (z) {
            z = shouldReMeasureChild(view, i, i2, layoutParams);
        } else {
            z = shouldMeasureChild(view, i, i2, layoutParams);
        }
        if (z) {
            view.measure(i, i2);
        }
    }

    private void updateMeasurements() {
        int width;
        int paddingLeft;
        if (getOrientation() == 1) {
            width = getWidth() - getPaddingRight();
            paddingLeft = getPaddingLeft();
        } else {
            width = getHeight() - getPaddingBottom();
            paddingLeft = getPaddingTop();
        }
        calculateItemBorders(width - paddingLeft);
    }

    public boolean checkLayoutParams(androidx.recyclerview.widget.RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void collectPrefetchPositionsForLayoutState(State state, LayoutState layoutState, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i = this.mSpanCount;
        for (int i2 = 0; i2 < this.mSpanCount && layoutState.hasMore(state) && i > 0; i2++) {
            int i3 = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(i3, Math.max(0, layoutState.mScrollingOffset));
            i -= this.mSpanSizeLookup.getSpanSize(i3);
            layoutState.mCurrentPosition += layoutState.mItemDirection;
        }
    }

    public int computeHorizontalScrollOffset(State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return computeScrollOffsetWithSpanInfo(state);
        }
        return super.computeHorizontalScrollOffset(state);
    }

    public int computeHorizontalScrollRange(State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return computeScrollRangeWithSpanInfo(state);
        }
        return super.computeHorizontalScrollRange(state);
    }

    public int computeVerticalScrollOffset(State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return computeScrollOffsetWithSpanInfo(state);
        }
        return super.computeVerticalScrollOffset(state);
    }

    public int computeVerticalScrollRange(State state) {
        if (this.mUsingSpansToEstimateScrollBarDimensions) {
            return computeScrollRangeWithSpanInfo(state);
        }
        return super.computeVerticalScrollRange(state);
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
            if (position >= 0 && position < i3 && getSpanIndex(recycler, state, position) == 0) {
                if (((androidx.recyclerview.widget.RecyclerView.LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
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

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -1);
        }
        return new LayoutParams(-1, -2);
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public int getSpaceForSpanRange(int i, int i2) {
        int[] iArr;
        if (this.mOrientation == 1 && isLayoutRTL()) {
            iArr = this.mCachedBorders;
            int i3 = this.mSpanCount;
            return iArr[i3 - i] - iArr[(i3 - i) - i2];
        }
        iArr = this.mCachedBorders;
        return iArr[i2 + i] - iArr[i];
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    public boolean isUsingSpansToEstimateScrollbarDimensions() {
        return this.mUsingSpansToEstimateScrollBarDimensions;
    }

    public void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult layoutChunkResult) {
        int i;
        int spanSize;
        View next;
        Recycler recycler2 = recycler;
        State state2 = state;
        LayoutState layoutState2 = layoutState;
        LayoutChunkResult layoutChunkResult2 = layoutChunkResult;
        int modeInOther = this.mOrientationHelper.getModeInOther();
        int i2 = 0;
        Object obj = modeInOther != BasicMeasure.EXACTLY ? 1 : null;
        int i3 = getChildCount() > 0 ? this.mCachedBorders[this.mSpanCount] : 0;
        if (obj != null) {
            updateMeasurements();
        }
        boolean z = layoutState2.mItemDirection == 1;
        int i4 = this.mSpanCount;
        if (!z) {
            i4 = getSpanIndex(recycler2, state2, layoutState2.mCurrentPosition) + getSpanSize(recycler2, state2, layoutState2.mCurrentPosition);
        }
        int i5 = 0;
        while (i5 < this.mSpanCount && layoutState2.hasMore(state2) && i4 > 0) {
            i = layoutState2.mCurrentPosition;
            spanSize = getSpanSize(recycler2, state2, i);
            if (spanSize <= this.mSpanCount) {
                i4 -= spanSize;
                if (i4 < 0) {
                    break;
                }
                next = layoutState2.next(recycler2);
                if (next == null) {
                    break;
                }
                this.mSet[i5] = next;
                i5++;
            } else {
                throw new IllegalArgumentException(a.y(a.N("Item at position ", i, " requires ", spanSize, " spans but GridLayoutManager has only "), this.mSpanCount, " spans."));
            }
        }
        if (i5 == 0) {
            layoutChunkResult2.mFinished = true;
            return;
        }
        View view;
        int childMeasureSpec;
        int i6;
        float f = 0.0f;
        assignSpans(recycler2, state2, i5, z);
        int i7 = 0;
        int i8 = 0;
        while (i7 < i5) {
            next = this.mSet[i7];
            if (layoutState2.mScrapList == null) {
                if (z) {
                    addView(next);
                } else {
                    addView(next, i2);
                }
            } else if (z) {
                addDisappearingView(next);
            } else {
                addDisappearingView(next, i2);
            }
            calculateItemDecorationsForChild(next, this.mDecorInsets);
            measureChild(next, modeInOther, i2);
            i2 = this.mOrientationHelper.getDecoratedMeasurement(next);
            if (i2 > i8) {
                i8 = i2;
            }
            float decoratedMeasurementInOther = (((float) this.mOrientationHelper.getDecoratedMeasurementInOther(next)) * 1.0f) / ((float) ((LayoutParams) next.getLayoutParams()).mSpanSize);
            if (decoratedMeasurementInOther > f) {
                f = decoratedMeasurementInOther;
            }
            i7++;
            i2 = 0;
        }
        if (obj != null) {
            guessMeasurement(f, i3);
            i8 = 0;
            for (i7 = 0; i7 < i5; i7++) {
                view = this.mSet[i7];
                measureChild(view, BasicMeasure.EXACTLY, true);
                modeInOther = this.mOrientationHelper.getDecoratedMeasurement(view);
                if (modeInOther > i8) {
                    i8 = modeInOther;
                }
            }
        }
        for (i7 = 0; i7 < i5; i7++) {
            view = this.mSet[i7];
            if (this.mOrientationHelper.getDecoratedMeasurement(view) != i8) {
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                Rect rect = layoutParams.mDecorInsets;
                i3 = ((rect.top + rect.bottom) + layoutParams.topMargin) + layoutParams.bottomMargin;
                int i9 = ((rect.left + rect.right) + layoutParams.leftMargin) + layoutParams.rightMargin;
                i2 = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
                if (this.mOrientation == 1) {
                    childMeasureSpec = LayoutManager.getChildMeasureSpec(i2, BasicMeasure.EXACTLY, i9, layoutParams.width, false);
                    i2 = MeasureSpec.makeMeasureSpec(i8 - i3, BasicMeasure.EXACTLY);
                } else {
                    i9 = MeasureSpec.makeMeasureSpec(i8 - i9, BasicMeasure.EXACTLY);
                    i2 = LayoutManager.getChildMeasureSpec(i2, BasicMeasure.EXACTLY, i3, layoutParams.height, false);
                    childMeasureSpec = i9;
                }
                measureChildWithDecorationsAndMargin(view, childMeasureSpec, i2, true);
            }
        }
        layoutChunkResult2.mConsumed = i8;
        if (this.mOrientation == 1) {
            if (layoutState2.mLayoutDirection == -1) {
                i6 = layoutState2.mOffset;
                i8 = i6 - i8;
            } else {
                i6 = layoutState2.mOffset;
                int i10 = i6;
                i6 = i8 + i6;
                i8 = i10;
            }
            modeInOther = 0;
            childMeasureSpec = 0;
        } else if (layoutState2.mLayoutDirection == -1) {
            modeInOther = layoutState2.mOffset;
            childMeasureSpec = modeInOther - i8;
            i8 = 0;
            i6 = 0;
        } else {
            childMeasureSpec = layoutState2.mOffset;
            modeInOther = i8 + childMeasureSpec;
            i6 = 0;
            i8 = 0;
        }
        i3 = 0;
        while (i3 < i5) {
            int i11;
            int decoratedMeasurementInOther2;
            View view2 = this.mSet[i3];
            LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
            if (this.mOrientation != 1) {
                i8 = this.mCachedBorders[layoutParams2.mSpanIndex] + getPaddingTop();
                i6 = this.mOrientationHelper.getDecoratedMeasurementInOther(view2) + i8;
            } else if (isLayoutRTL()) {
                modeInOther = this.mCachedBorders[this.mSpanCount - layoutParams2.mSpanIndex] + getPaddingLeft();
                childMeasureSpec = modeInOther - this.mOrientationHelper.getDecoratedMeasurementInOther(view2);
            } else {
                i7 = getPaddingLeft() + this.mCachedBorders[layoutParams2.mSpanIndex];
                i11 = i7;
                i = i8;
                spanSize = i6;
                decoratedMeasurementInOther2 = this.mOrientationHelper.getDecoratedMeasurementInOther(view2) + i7;
                layoutDecoratedWithMargins(view2, i11, i, decoratedMeasurementInOther2, spanSize);
                if (!layoutParams2.isItemRemoved() || layoutParams2.isItemChanged()) {
                    layoutChunkResult2.mIgnoreConsumed = true;
                }
                layoutChunkResult2.mFocusable |= view2.hasFocusable();
                i3++;
                i8 = i;
                i6 = spanSize;
                modeInOther = decoratedMeasurementInOther2;
                childMeasureSpec = i11;
            }
            i = i8;
            spanSize = i6;
            decoratedMeasurementInOther2 = modeInOther;
            i11 = childMeasureSpec;
            layoutDecoratedWithMargins(view2, i11, i, decoratedMeasurementInOther2, spanSize);
            if (layoutParams2.isItemRemoved()) {
            }
            layoutChunkResult2.mIgnoreConsumed = true;
            layoutChunkResult2.mFocusable |= view2.hasFocusable();
            i3++;
            i8 = i;
            i6 = spanSize;
            modeInOther = decoratedMeasurementInOther2;
            childMeasureSpec = i11;
        }
        Arrays.fill(this.mSet, null);
    }

    public void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        super.onAnchorReady(recycler, state, anchorInfo, i);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, i);
        }
        ensureViewSet();
    }

    /* JADX WARNING: Removed duplicated region for block: B:71:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0107  */
    /* JADX WARNING: Missing block: B:54:0x00d6, code skipped:
            if (r13 == (r2 > r15)) goto L_0x00b0;
     */
    /* JADX WARNING: Missing block: B:65:0x00f6, code skipped:
            if (r13 == r11) goto L_0x00b8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
        Recycler recycler2 = recycler;
        State state2 = state;
        View findContainingItemView = findContainingItemView(view);
        View view2 = null;
        if (findContainingItemView == null) {
            return null;
        }
        LayoutParams layoutParams = (LayoutParams) findContainingItemView.getLayoutParams();
        int i2 = layoutParams.mSpanIndex;
        int i3 = layoutParams.mSpanSize + i2;
        if (super.onFocusSearchFailed(view, i, recycler, state) == null) {
            return null;
        }
        int childCount;
        int i4;
        int i5;
        View view3;
        if (((convertFocusDirectionToLayoutDirection(i) == 1) != this.mShouldReverseLayout ? 1 : null) != null) {
            childCount = getChildCount() - 1;
            i4 = -1;
            i5 = -1;
        } else {
            i4 = getChildCount();
            childCount = 0;
            i5 = 1;
        }
        boolean z = this.mOrientation == 1 && isLayoutRTL();
        int spanGroupIndex = getSpanGroupIndex(recycler2, state2, childCount);
        int i6 = childCount;
        int i7 = 0;
        int i8 = -1;
        int i9 = -1;
        int i10 = 0;
        View view4 = null;
        while (i6 != i4) {
            int spanGroupIndex2 = getSpanGroupIndex(recycler2, state2, i6);
            View childAt = getChildAt(i6);
            if (childAt == findContainingItemView) {
                break;
            }
            View view5;
            int i11;
            int i12;
            if (!childAt.hasFocusable() || spanGroupIndex2 == spanGroupIndex) {
                LayoutParams layoutParams2 = (LayoutParams) childAt.getLayoutParams();
                int i13 = layoutParams2.mSpanIndex;
                view5 = findContainingItemView;
                int i14 = layoutParams2.mSpanSize + i13;
                if (childAt.hasFocusable() && i13 == i2 && i14 == i3) {
                    return childAt;
                }
                boolean z2;
                if (!(childAt.hasFocusable() && view2 == null) && (childAt.hasFocusable() || view4 != null)) {
                    view3 = view4;
                    childCount = Math.min(i14, i3) - Math.max(i13, i2);
                    if (childAt.hasFocusable()) {
                        if (childCount <= i7) {
                            if (childCount == i7) {
                            }
                        }
                    } else if (view2 == null) {
                        i11 = i7;
                        i12 = i4;
                        z2 = true;
                        if (isViewPartiallyVisible(childAt, false, true)) {
                            i7 = i10;
                            if (childCount > i7) {
                                childCount = i9;
                                if (z2) {
                                    if (childAt.hasFocusable()) {
                                        i8 = layoutParams2.mSpanIndex;
                                        i9 = childCount;
                                        i10 = i7;
                                        view4 = view3;
                                        view2 = childAt;
                                        i7 = Math.min(i14, i3) - Math.max(i13, i2);
                                    } else {
                                        i10 = Math.min(i14, i3) - Math.max(i13, i2);
                                        i9 = layoutParams2.mSpanIndex;
                                        i7 = i11;
                                        view4 = childAt;
                                    }
                                    i6 += i5;
                                    recycler2 = recycler;
                                    state2 = state;
                                    findContainingItemView = view5;
                                    i4 = i12;
                                }
                            } else {
                                if (childCount == i7) {
                                    childCount = i9;
                                    if (i13 <= childCount) {
                                        z2 = false;
                                    }
                                } else {
                                    childCount = i9;
                                }
                                z2 = false;
                                if (z2) {
                                }
                            }
                        }
                        childCount = i9;
                        i7 = i10;
                        z2 = false;
                        if (z2) {
                        }
                    }
                    i11 = i7;
                    i12 = i4;
                    childCount = i9;
                    i7 = i10;
                    z2 = false;
                    if (z2) {
                    }
                } else {
                    view3 = view4;
                }
                i11 = i7;
                i12 = i4;
                childCount = i9;
                i7 = i10;
                z2 = true;
                if (z2) {
                }
            } else if (view2 != null) {
                break;
            } else {
                view5 = findContainingItemView;
                view3 = view4;
                i11 = i7;
                i12 = i4;
                childCount = i9;
                i7 = i10;
            }
            i9 = childCount;
            i10 = i7;
            i7 = i11;
            view4 = view3;
            i6 += i5;
            recycler2 = recycler;
            state2 = state;
            findContainingItemView = view5;
            i4 = i12;
        }
        view3 = view4;
        if (view2 == null) {
            view2 = view3;
        }
        return view2;
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            LayoutParams layoutParams2 = (LayoutParams) layoutParams;
            int spanGroupIndex = getSpanGroupIndex(recycler, state, layoutParams2.getViewLayoutPosition());
            if (this.mOrientation == 0) {
                accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), spanGroupIndex, 1, false, false));
            } else {
                accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanGroupIndex, 1, layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), false, false));
            }
            return;
        }
        super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
        this.mSpanSizeLookup.invalidateSpanGroupIndexCache();
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(i, recycler, state);
    }

    public void setMeasuredDimension(Rect rect, int i, int i2) {
        int chooseSize;
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(rect, i, i2);
        }
        int paddingRight = getPaddingRight() + getPaddingLeft();
        int paddingBottom = getPaddingBottom() + getPaddingTop();
        if (this.mOrientation == 1) {
            chooseSize = LayoutManager.chooseSize(i2, rect.height() + paddingBottom, getMinimumHeight());
            int[] iArr = this.mCachedBorders;
            i = LayoutManager.chooseSize(i, iArr[iArr.length - 1] + paddingRight, getMinimumWidth());
        } else {
            i = LayoutManager.chooseSize(i, rect.width() + paddingRight, getMinimumWidth());
            int[] iArr2 = this.mCachedBorders;
            chooseSize = LayoutManager.chooseSize(i2, iArr2[iArr2.length - 1] + paddingBottom, getMinimumHeight());
        }
        setMeasuredDimension(i, chooseSize);
    }

    public void setSpanCount(int i) {
        if (i != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (i >= 1) {
                this.mSpanCount = i;
                this.mSpanSizeLookup.invalidateSpanIndexCache();
                requestLayout();
                return;
            }
            throw new IllegalArgumentException(a.k("Span count should be at least 1. Provided ", i));
        }
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public void setStackFromEnd(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
        }
        super.setStackFromEnd(false);
    }

    public void setUsingSpansToEstimateScrollbarDimensions(boolean z) {
        this.mUsingSpansToEstimateScrollBarDimensions = z;
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }

    public static int[] calculateItemBorders(int[] iArr, int i, int i2) {
        int i3 = 1;
        if (!(iArr != null && iArr.length == i + 1 && iArr[iArr.length - 1] == i2)) {
            iArr = new int[(i + 1)];
        }
        int i4 = 0;
        iArr[0] = 0;
        int i5 = i2 / i;
        i2 %= i;
        int i6 = 0;
        while (i3 <= i) {
            int i7;
            i4 += i2;
            if (i4 <= 0 || i - i4 >= i2) {
                i7 = i5;
            } else {
                i7 = i5 + 1;
                i4 -= i;
            }
            i6 += i7;
            iArr[i3] = i6;
            i3++;
        }
        return iArr;
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    public GridLayoutManager(Context context, int i) {
        super(context);
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i2, z);
        setSpanCount(i);
    }
}
