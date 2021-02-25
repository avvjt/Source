package androidx.viewpager.widget;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.Scroller;
import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.view.AbsSavedState;
import f.d.b.a.a;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPager extends ViewGroup {
    private static final int CLOSE_ENOUGH = 2;
    private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>() {
        public int compare(ItemInfo itemInfo, ItemInfo itemInfo2) {
            return itemInfo.position - itemInfo2.position;
        }
    };
    private static final boolean DEBUG = false;
    private static final int DEFAULT_GUTTER_SIZE = 16;
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private static final int INVALID_POINTER = -1;
    public static final int[] LAYOUT_ATTRS = new int[]{16842931};
    private static final int MAX_SETTLE_DURATION = 600;
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int MIN_FLING_VELOCITY = 400;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "ViewPager";
    private static final boolean USE_CACHE = false;
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float f) {
            f -= 1.0f;
            return ((((f * f) * f) * f) * f) + 1.0f;
        }
    };
    private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
    private int mActivePointerId = -1;
    public PagerAdapter mAdapter;
    private List<OnAdapterChangeListener> mAdapterChangeListeners;
    private int mBottomPageBounds;
    private boolean mCalledSuper;
    private int mChildHeightMeasureSpec;
    private int mChildWidthMeasureSpec;
    private int mCloseEnough;
    public int mCurItem;
    private int mDecorChildCount;
    private int mDefaultGutterSize;
    private int mDrawingOrder;
    private ArrayList<View> mDrawingOrderedChildren;
    private final Runnable mEndScrollRunnable = new Runnable() {
        public void run() {
            ViewPager.this.setScrollState(0);
            ViewPager.this.populate();
        }
    };
    private int mExpectedAdapterCount;
    private long mFakeDragBeginTime;
    private boolean mFakeDragging;
    private boolean mFirstLayout = true;
    private float mFirstOffset = -3.4028235E38f;
    private int mFlingDistance;
    private int mGutterSize;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsScrollStarted;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems = new ArrayList();
    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastOffset = Float.MAX_VALUE;
    private EdgeEffect mLeftEdge;
    private Drawable mMarginDrawable;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private boolean mNeedCalculatePageOffsets = false;
    private PagerObserver mObserver;
    private int mOffscreenPageLimit = 1;
    private OnPageChangeListener mOnPageChangeListener;
    private List<OnPageChangeListener> mOnPageChangeListeners;
    private int mPageMargin;
    private PageTransformer mPageTransformer;
    private int mPageTransformerLayerType;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState = null;
    private ClassLoader mRestoredClassLoader = null;
    private int mRestoredCurItem = -1;
    private EdgeEffect mRightEdge;
    private int mScrollState = 0;
    private Scroller mScroller;
    private boolean mScrollingCacheEnabled;
    private final ItemInfo mTempItem = new ItemInfo();
    private final Rect mTempRect = new Rect();
    private int mTopPageBounds;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    @Inherited
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DecorView {
    }

    public static class ItemInfo {
        public Object object;
        public float offset;
        public int position;
        public boolean scrolling;
        public float widthFactor;
    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        public int childIndex;
        public int gravity;
        public boolean isDecor;
        public boolean needsMeasure;
        public int position;
        public float widthFactor = 0.0f;

        public LayoutParams() {
            super(-1, -1);
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ViewPager.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInteger(0, 48);
            obtainStyledAttributes.recycle();
        }
    }

    public interface OnAdapterChangeListener {
        void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter pagerAdapter, @Nullable PagerAdapter pagerAdapter2);
    }

    public interface OnPageChangeListener {
        void onPageScrollStateChanged(int i);

        void onPageScrolled(int i, float f, @Px int i2);

        void onPageSelected(int i);
    }

    public interface PageTransformer {
        void transformPage(@NonNull View view, float f);
    }

    public class PagerObserver extends DataSetObserver {
        public void onChanged() {
            ViewPager.this.dataSetChanged();
        }

        public void onInvalidated() {
            ViewPager.this.dataSetChanged();
        }
    }

    public static class ViewPositionComparator implements Comparator<View> {
        public int compare(View view, View view2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
            boolean z = layoutParams.isDecor;
            if (z == layoutParams2.isDecor) {
                return layoutParams.position - layoutParams2.position;
            }
            return z ? 1 : -1;
        }
    }

    public class MyAccessibilityDelegate extends AccessibilityDelegateCompat {
        private boolean canScroll() {
            PagerAdapter pagerAdapter = ViewPager.this.mAdapter;
            return pagerAdapter != null && pagerAdapter.getCount() > 1;
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(ViewPager.class.getName());
            accessibilityEvent.setScrollable(canScroll());
            if (accessibilityEvent.getEventType() == 4096) {
                PagerAdapter pagerAdapter = ViewPager.this.mAdapter;
                if (pagerAdapter != null) {
                    accessibilityEvent.setItemCount(pagerAdapter.getCount());
                    accessibilityEvent.setFromIndex(ViewPager.this.mCurItem);
                    accessibilityEvent.setToIndex(ViewPager.this.mCurItem);
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
            accessibilityNodeInfoCompat.setScrollable(canScroll());
            if (ViewPager.this.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
            }
            if (ViewPager.this.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
            }
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            ViewPager viewPager;
            if (i != 4096) {
                if (i != 8192 || !ViewPager.this.canScrollHorizontally(-1)) {
                    return false;
                }
                viewPager = ViewPager.this;
                viewPager.setCurrentItem(viewPager.mCurItem - 1);
                return true;
            } else if (!ViewPager.this.canScrollHorizontally(1)) {
                return false;
            } else {
                viewPager = ViewPager.this;
                viewPager.setCurrentItem(viewPager.mCurItem + 1);
                return true;
            }
        }
    }

    public static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }
        };
        public Parcelable adapterState;
        public ClassLoader loader;
        public int position;

        public SavedState(@NonNull Parcelable parcelable) {
            super(parcelable);
        }

        public String toString() {
            StringBuilder L = a.L("FragmentPager.SavedState{");
            L.append(Integer.toHexString(System.identityHashCode(this)));
            L.append(" position=");
            return a.y(L, this.position, "}");
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.position);
            parcel.writeParcelable(this.adapterState, i);
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            if (classLoader == null) {
                classLoader = getClass().getClassLoader();
            }
            this.position = parcel.readInt();
            this.adapterState = parcel.readParcelable(classLoader);
            this.loader = classLoader;
        }
    }

    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
        }
    }

    public ViewPager(@NonNull Context context) {
        super(context);
        initViewPager();
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0056 A:{LOOP_END, LOOP:2: B:19:0x0052->B:21:0x0056} */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x009b A:{LOOP_END, LOOP:5: B:33:0x0097->B:35:0x009b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void calculatePageOffsets(ItemInfo itemInfo, int i, ItemInfo itemInfo2) {
        int i2;
        int i3;
        int count = this.mAdapter.getCount();
        int clientWidth = getClientWidth();
        float f = clientWidth > 0 ? ((float) this.mPageMargin) / ((float) clientWidth) : 0.0f;
        if (itemInfo2 != null) {
            int i4 = itemInfo2.position;
            i2 = itemInfo.position;
            Object obj;
            ItemInfo itemInfo3;
            if (i4 < i2) {
                float f2 = (itemInfo2.offset + itemInfo2.widthFactor) + f;
                i4++;
                i3 = 0;
                while (i4 <= itemInfo.position && i3 < this.mItems.size()) {
                    obj = this.mItems.get(i3);
                    while (true) {
                        itemInfo3 = (ItemInfo) obj;
                        if (i4 <= itemInfo3.position || i3 >= this.mItems.size() - 1) {
                            while (i4 < itemInfo3.position) {
                                f2 += this.mAdapter.getPageWidth(i4) + f;
                                i4++;
                            }
                        } else {
                            i3++;
                            obj = this.mItems.get(i3);
                        }
                    }
                    while (i4 < itemInfo3.position) {
                    }
                    itemInfo3.offset = f2;
                    f2 += itemInfo3.widthFactor + f;
                    i4++;
                }
            } else if (i4 > i2) {
                i2 = this.mItems.size() - 1;
                float f3 = itemInfo2.offset;
                while (true) {
                    i4--;
                    if (i4 < itemInfo.position || i2 < 0) {
                        break;
                    }
                    obj = this.mItems.get(i2);
                    while (true) {
                        itemInfo3 = (ItemInfo) obj;
                        if (i4 >= itemInfo3.position || i2 <= 0) {
                            while (i4 > itemInfo3.position) {
                                f3 -= this.mAdapter.getPageWidth(i4) + f;
                                i4--;
                            }
                        } else {
                            i2--;
                            obj = this.mItems.get(i2);
                        }
                    }
                    while (i4 > itemInfo3.position) {
                    }
                    f3 -= itemInfo3.widthFactor + f;
                    itemInfo3.offset = f3;
                }
            }
        }
        i3 = this.mItems.size();
        float f4 = itemInfo.offset;
        i2 = itemInfo.position;
        int i5 = i2 - 1;
        this.mFirstOffset = i2 == 0 ? f4 : -3.4028235E38f;
        count--;
        this.mLastOffset = i2 == count ? (itemInfo.widthFactor + f4) - 1.0f : Float.MAX_VALUE;
        i2 = i - 1;
        while (i2 >= 0) {
            int i6;
            ItemInfo itemInfo4 = (ItemInfo) this.mItems.get(i2);
            while (true) {
                i6 = itemInfo4.position;
                if (i5 <= i6) {
                    break;
                }
                f4 -= this.mAdapter.getPageWidth(i5) + f;
                i5--;
            }
            f4 -= itemInfo4.widthFactor + f;
            itemInfo4.offset = f4;
            if (i6 == 0) {
                this.mFirstOffset = f4;
            }
            i2--;
            i5--;
        }
        f4 = (itemInfo.offset + itemInfo.widthFactor) + f;
        int i7 = itemInfo.position + 1;
        i++;
        while (i < i3) {
            ItemInfo itemInfo5 = (ItemInfo) this.mItems.get(i);
            while (true) {
                i5 = itemInfo5.position;
                if (i7 >= i5) {
                    break;
                }
                f4 += this.mAdapter.getPageWidth(i7) + f;
                i7++;
            }
            if (i5 == count) {
                this.mLastOffset = (itemInfo5.widthFactor + f4) - 1.0f;
            }
            itemInfo5.offset = f4;
            f4 += itemInfo5.widthFactor + f;
            i++;
            i7++;
        }
        this.mNeedCalculatePageOffsets = false;
    }

    private void completeScroll(boolean z) {
        int scrollX;
        Object obj = this.mScrollState == 2 ? 1 : null;
        if (obj != null) {
            setScrollingCacheEnabled(false);
            if ((this.mScroller.isFinished() ^ 1) != 0) {
                this.mScroller.abortAnimation();
                scrollX = getScrollX();
                int scrollY = getScrollY();
                int currX = this.mScroller.getCurrX();
                int currY = this.mScroller.getCurrY();
                if (!(scrollX == currX && scrollY == currY)) {
                    scrollTo(currX, currY);
                    if (currX != scrollX) {
                        pageScrolled(currX);
                    }
                }
            }
        }
        this.mPopulatePending = false;
        for (scrollX = 0; scrollX < this.mItems.size(); scrollX++) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(scrollX);
            if (itemInfo.scrolling) {
                itemInfo.scrolling = false;
                obj = 1;
            }
        }
        if (obj == null) {
            return;
        }
        if (z) {
            ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
        } else {
            this.mEndScrollRunnable.run();
        }
    }

    private int determineTargetPage(int i, float f, int i2, int i3) {
        if (Math.abs(i3) <= this.mFlingDistance || Math.abs(i2) <= this.mMinimumVelocity) {
            i += (int) (f + (i >= this.mCurItem ? 0.4f : 0.6f));
        } else if (i2 <= 0) {
            i++;
        }
        if (this.mItems.size() <= 0) {
            return i;
        }
        ItemInfo itemInfo = (ItemInfo) this.mItems.get(0);
        ArrayList arrayList = this.mItems;
        return Math.max(itemInfo.position, Math.min(i, ((ItemInfo) arrayList.get(arrayList.size() - 1)).position));
    }

    private void dispatchOnPageScrolled(int i, float f, int i2) {
        OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(i, f, i2);
        }
        List list = this.mOnPageChangeListeners;
        if (list != null) {
            int size = list.size();
            for (int i3 = 0; i3 < size; i3++) {
                OnPageChangeListener onPageChangeListener2 = (OnPageChangeListener) this.mOnPageChangeListeners.get(i3);
                if (onPageChangeListener2 != null) {
                    onPageChangeListener2.onPageScrolled(i, f, i2);
                }
            }
        }
        onPageChangeListener = this.mInternalPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(i, f, i2);
        }
    }

    private void dispatchOnPageSelected(int i) {
        OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(i);
        }
        List list = this.mOnPageChangeListeners;
        if (list != null) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                OnPageChangeListener onPageChangeListener2 = (OnPageChangeListener) this.mOnPageChangeListeners.get(i2);
                if (onPageChangeListener2 != null) {
                    onPageChangeListener2.onPageSelected(i);
                }
            }
        }
        onPageChangeListener = this.mInternalPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(i);
        }
    }

    private void dispatchOnScrollStateChanged(int i) {
        OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(i);
        }
        List list = this.mOnPageChangeListeners;
        if (list != null) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                OnPageChangeListener onPageChangeListener2 = (OnPageChangeListener) this.mOnPageChangeListeners.get(i2);
                if (onPageChangeListener2 != null) {
                    onPageChangeListener2.onPageScrollStateChanged(i);
                }
            }
        }
        onPageChangeListener = this.mInternalPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(i);
        }
    }

    private void enableLayers(boolean z) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setLayerType(z ? this.mPageTransformerLayerType : 0, null);
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private Rect getChildRectInPagerCoordinates(Rect rect, View view) {
        if (rect == null) {
            rect = new Rect();
        }
        if (view == null) {
            rect.set(0, 0, 0, 0);
            return rect;
        }
        rect.left = view.getLeft();
        rect.right = view.getRight();
        rect.top = view.getTop();
        rect.bottom = view.getBottom();
        ViewPager parent = view.getParent();
        while ((parent instanceof ViewGroup) && parent != this) {
            ViewGroup viewGroup = parent;
            rect.left = viewGroup.getLeft() + rect.left;
            rect.right = viewGroup.getRight() + rect.right;
            rect.top = viewGroup.getTop() + rect.top;
            rect.bottom = viewGroup.getBottom() + rect.bottom;
            parent = viewGroup.getParent();
        }
        return rect;
    }

    private int getClientWidth() {
        return (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private ItemInfo infoForCurrentScrollPosition() {
        int clientWidth = getClientWidth();
        float f = 0.0f;
        float scrollX = clientWidth > 0 ? ((float) getScrollX()) / ((float) clientWidth) : 0.0f;
        float f2 = clientWidth > 0 ? ((float) this.mPageMargin) / ((float) clientWidth) : 0.0f;
        ItemInfo itemInfo = null;
        float f3 = 0.0f;
        int i = -1;
        int i2 = 0;
        Object obj = 1;
        while (i2 < this.mItems.size()) {
            ItemInfo itemInfo2 = (ItemInfo) this.mItems.get(i2);
            if (obj == null) {
                i++;
                if (itemInfo2.position != i) {
                    itemInfo2 = this.mTempItem;
                    itemInfo2.offset = (f + f3) + f2;
                    itemInfo2.position = i;
                    itemInfo2.widthFactor = this.mAdapter.getPageWidth(i);
                    i2--;
                }
            }
            f = itemInfo2.offset;
            f3 = (itemInfo2.widthFactor + f) + f2;
            if (obj == null && scrollX < f) {
                return itemInfo;
            }
            if (scrollX < f3 || i2 == this.mItems.size() - 1) {
                return itemInfo2;
            }
            i = itemInfo2.position;
            f3 = itemInfo2.widthFactor;
            i2++;
            itemInfo = itemInfo2;
            obj = null;
        }
        return itemInfo;
    }

    private static boolean isDecorView(@NonNull View view) {
        return view.getClass().getAnnotation(DecorView.class) != null;
    }

    private boolean isGutterDrag(float f, float f2) {
        return (f < ((float) this.mGutterSize) && f2 > 0.0f) || (f > ((float) (getWidth() - this.mGutterSize)) && f2 < 0.0f);
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            actionIndex = actionIndex == 0 ? 1 : 0;
            this.mLastMotionX = motionEvent.getX(actionIndex);
            this.mActivePointerId = motionEvent.getPointerId(actionIndex);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    private boolean pageScrolled(int i) {
        String str = "onPageScrolled did not call superclass implementation";
        if (this.mItems.size() != 0) {
            ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
            int clientWidth = getClientWidth();
            int i2 = this.mPageMargin;
            int i3 = clientWidth + i2;
            float f = (float) clientWidth;
            float f2 = ((float) i2) / f;
            int i4 = infoForCurrentScrollPosition.position;
            float f3 = ((((float) i) / f) - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + f2);
            int i5 = (int) (((float) i3) * f3);
            this.mCalledSuper = false;
            onPageScrolled(i4, f3, i5);
            if (this.mCalledSuper) {
                return true;
            }
            throw new IllegalStateException(str);
        } else if (this.mFirstLayout) {
            return false;
        } else {
            this.mCalledSuper = false;
            onPageScrolled(0, 0.0f, 0);
            if (this.mCalledSuper) {
                return false;
            }
            throw new IllegalStateException(str);
        }
    }

    private boolean performDrag(float f) {
        Object obj;
        Object obj2;
        float f2 = this.mLastMotionX - f;
        this.mLastMotionX = f;
        f = ((float) getScrollX()) + f2;
        f2 = (float) getClientWidth();
        float f3 = this.mFirstOffset * f2;
        float f4 = this.mLastOffset * f2;
        boolean z = false;
        ItemInfo itemInfo = (ItemInfo) this.mItems.get(0);
        ArrayList arrayList = this.mItems;
        ItemInfo itemInfo2 = (ItemInfo) arrayList.get(arrayList.size() - 1);
        if (itemInfo.position != 0) {
            f3 = itemInfo.offset * f2;
            obj = null;
        } else {
            obj = 1;
        }
        if (itemInfo2.position != this.mAdapter.getCount() - 1) {
            f4 = itemInfo2.offset * f2;
            obj2 = null;
        } else {
            obj2 = 1;
        }
        if (f < f3) {
            if (obj != null) {
                this.mLeftEdge.onPull(Math.abs(f3 - f) / f2);
                z = true;
            }
            f = f3;
        } else if (f > f4) {
            if (obj2 != null) {
                this.mRightEdge.onPull(Math.abs(f - f4) / f2);
                z = true;
            }
            f = f4;
        }
        int i = (int) f;
        this.mLastMotionX = (f - ((float) i)) + this.mLastMotionX;
        scrollTo(i, getScrollY());
        pageScrolled(i);
        return z;
    }

    private void recomputeScrollPosition(int i, int i2, int i3, int i4) {
        if (i2 <= 0 || this.mItems.isEmpty()) {
            ItemInfo infoForPosition = infoForPosition(this.mCurItem);
            i = (int) ((infoForPosition != null ? Math.min(infoForPosition.offset, this.mLastOffset) : 0.0f) * ((float) ((i - getPaddingLeft()) - getPaddingRight())));
            if (i != getScrollX()) {
                completeScroll(false);
                scrollTo(i, getScrollY());
            }
        } else if (this.mScroller.isFinished()) {
            scrollTo((int) ((((float) getScrollX()) / ((float) (((i2 - getPaddingLeft()) - getPaddingRight()) + i4))) * ((float) (((i - getPaddingLeft()) - getPaddingRight()) + i3))), getScrollY());
        } else {
            this.mScroller.setFinalX(getCurrentItem() * getClientWidth());
        }
    }

    private void removeNonDecorViews() {
        int i = 0;
        while (i < getChildCount()) {
            if (!((LayoutParams) getChildAt(i).getLayoutParams()).isDecor) {
                removeViewAt(i);
                i--;
            }
            i++;
        }
    }

    private void requestParentDisallowInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    private boolean resetTouch() {
        this.mActivePointerId = -1;
        endDrag();
        this.mLeftEdge.onRelease();
        this.mRightEdge.onRelease();
        return this.mLeftEdge.isFinished() || this.mRightEdge.isFinished();
    }

    private void scrollToItem(int i, boolean z, int i2, boolean z2) {
        int max;
        ItemInfo infoForPosition = infoForPosition(i);
        if (infoForPosition != null) {
            max = (int) (Math.max(this.mFirstOffset, Math.min(infoForPosition.offset, this.mLastOffset)) * ((float) getClientWidth()));
        } else {
            max = 0;
        }
        if (z) {
            smoothScrollTo(max, 0, i2);
            if (z2) {
                dispatchOnPageSelected(i);
                return;
            }
            return;
        }
        if (z2) {
            dispatchOnPageSelected(i);
        }
        completeScroll(false);
        scrollTo(max, 0);
        pageScrolled(max);
    }

    private void setScrollingCacheEnabled(boolean z) {
        if (this.mScrollingCacheEnabled != z) {
            this.mScrollingCacheEnabled = z;
        }
    }

    private void sortChildDrawingOrder() {
        if (this.mDrawingOrder != 0) {
            ArrayList arrayList = this.mDrawingOrderedChildren;
            if (arrayList == null) {
                this.mDrawingOrderedChildren = new ArrayList();
            } else {
                arrayList.clear();
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                this.mDrawingOrderedChildren.add(getChildAt(i));
            }
            Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
        }
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        int size = arrayList.size();
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() == 0) {
                    ItemInfo infoForChild = infoForChild(childAt);
                    if (infoForChild != null && infoForChild.position == this.mCurItem) {
                        childAt.addFocusables(arrayList, i, i2);
                    }
                }
            }
        }
        if ((descendantFocusability == 262144 && size != arrayList.size()) || !isFocusable()) {
            return;
        }
        if ((i2 & 1) != 1 || !isInTouchMode() || isFocusableInTouchMode()) {
            arrayList.add(this);
        }
    }

    public ItemInfo addNewItem(int i, int i2) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.position = i;
        itemInfo.object = this.mAdapter.instantiateItem((ViewGroup) this, i);
        itemInfo.widthFactor = this.mAdapter.getPageWidth(i);
        if (i2 < 0 || i2 >= this.mItems.size()) {
            this.mItems.add(itemInfo);
        } else {
            this.mItems.add(i2, itemInfo);
        }
        return itemInfo;
    }

    public void addOnAdapterChangeListener(@NonNull OnAdapterChangeListener onAdapterChangeListener) {
        if (this.mAdapterChangeListeners == null) {
            this.mAdapterChangeListeners = new ArrayList();
        }
        this.mAdapterChangeListeners.add(onAdapterChangeListener);
    }

    public void addOnPageChangeListener(@NonNull OnPageChangeListener onPageChangeListener) {
        if (this.mOnPageChangeListeners == null) {
            this.mOnPageChangeListeners = new ArrayList();
        }
        this.mOnPageChangeListeners.add(onPageChangeListener);
    }

    public void addTouchables(ArrayList<View> arrayList) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == 0) {
                ItemInfo infoForChild = infoForChild(childAt);
                if (infoForChild != null && infoForChild.position == this.mCurItem) {
                    childAt.addTouchables(arrayList);
                }
            }
        }
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutParams) {
        if (!checkLayoutParams(layoutParams)) {
            layoutParams = generateLayoutParams(layoutParams);
        }
        LayoutParams layoutParams2 = (LayoutParams) layoutParams;
        int isDecorView = layoutParams2.isDecor | isDecorView(view);
        layoutParams2.isDecor = isDecorView;
        if (!this.mInLayout) {
            super.addView(view, i, layoutParams);
        } else if (isDecorView == 0) {
            layoutParams2.needsMeasure = true;
            addViewInLayout(view, i, layoutParams);
        } else {
            throw new IllegalStateException("Cannot add pager decor view during layout");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ca  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean arrowScroll(int i) {
        View findNextFocus;
        View findFocus = findFocus();
        boolean z = false;
        if (findFocus != this) {
            if (findFocus != null) {
                Object obj;
                for (ViewPager parent = findFocus.getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
                    if (parent == this) {
                        obj = 1;
                        break;
                    }
                }
                obj = null;
                if (obj == null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(findFocus.getClass().getSimpleName());
                    for (Object parent2 = findFocus.getParent(); parent2 instanceof ViewGroup; parent2 = parent2.getParent()) {
                        stringBuilder.append(" => ");
                        stringBuilder.append(parent2.getClass().getSimpleName());
                    }
                    StringBuilder L = a.L("arrowScroll tried to find focus based on non-child current focused view ");
                    L.append(stringBuilder.toString());
                    Log.e(TAG, L.toString());
                }
            }
            findNextFocus = FocusFinder.getInstance().findNextFocus(this, findFocus, i);
            if (findNextFocus == null && findNextFocus != findFocus) {
                boolean requestFocus;
                int i2;
                int i3;
                if (i == 17) {
                    i2 = getChildRectInPagerCoordinates(this.mTempRect, findNextFocus).left;
                    i3 = getChildRectInPagerCoordinates(this.mTempRect, findFocus).left;
                    if (findFocus == null || i2 < i3) {
                        requestFocus = findNextFocus.requestFocus();
                    } else {
                        requestFocus = pageLeft();
                    }
                } else if (i == 66) {
                    i2 = getChildRectInPagerCoordinates(this.mTempRect, findNextFocus).left;
                    i3 = getChildRectInPagerCoordinates(this.mTempRect, findFocus).left;
                    if (findFocus == null || i2 > i3) {
                        requestFocus = findNextFocus.requestFocus();
                    } else {
                        requestFocus = pageRight();
                    }
                }
                z = requestFocus;
            } else if (i != 17 || i == 1) {
                z = pageLeft();
            } else if (i == 66 || i == 2) {
                z = pageRight();
            }
            if (z) {
                playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
            }
            return z;
        }
        findFocus = null;
        findNextFocus = FocusFinder.getInstance().findNextFocus(this, findFocus, i);
        if (findNextFocus == null) {
        }
        if (i != 17) {
        }
        z = pageLeft();
        if (z) {
        }
        return z;
    }

    public boolean beginFakeDrag() {
        if (this.mIsBeingDragged) {
            return false;
        }
        this.mFakeDragging = true;
        setScrollState(1);
        this.mLastMotionX = 0.0f;
        this.mInitialMotionX = 0.0f;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, 0.0f, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
        this.mFakeDragBeginTime = uptimeMillis;
        return true;
    }

    public boolean canScroll(View view, boolean z, int i, int i2, int i3) {
        View view2 = view;
        boolean z2 = true;
        if (view2 instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view2;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                int i4 = i2 + scrollX;
                if (i4 >= childAt.getLeft() && i4 < childAt.getRight()) {
                    int i5 = i3 + scrollY;
                    if (i5 >= childAt.getTop() && i5 < childAt.getBottom()) {
                        if (canScroll(childAt, true, i, i4 - childAt.getLeft(), i5 - childAt.getTop())) {
                            return true;
                        }
                    }
                }
            }
        }
        if (!(z && view.canScrollHorizontally(-i))) {
            z2 = false;
        }
        return z2;
    }

    public boolean canScrollHorizontally(int i) {
        boolean z = false;
        if (this.mAdapter == null) {
            return false;
        }
        int clientWidth = getClientWidth();
        int scrollX = getScrollX();
        if (i < 0) {
            if (scrollX > ((int) (((float) clientWidth) * this.mFirstOffset))) {
                z = true;
            }
            return z;
        }
        if (i > 0 && scrollX < ((int) (((float) clientWidth) * this.mLastOffset))) {
            z = true;
        }
        return z;
    }

    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams) && super.checkLayoutParams(layoutParams);
    }

    public void clearOnPageChangeListeners() {
        List list = this.mOnPageChangeListeners;
        if (list != null) {
            list.clear();
        }
    }

    public void computeScroll() {
        this.mIsScrollStarted = true;
        if (this.mScroller.isFinished() || !this.mScroller.computeScrollOffset()) {
            completeScroll(true);
            return;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int currX = this.mScroller.getCurrX();
        int currY = this.mScroller.getCurrY();
        if (!(scrollX == currX && scrollY == currY)) {
            scrollTo(currX, currY);
            if (!pageScrolled(currX)) {
                this.mScroller.abortAnimation();
                scrollTo(0, currY);
            }
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void dataSetChanged() {
        int i;
        int count = this.mAdapter.getCount();
        this.mExpectedAdapterCount = count;
        Object obj = (this.mItems.size() >= (this.mOffscreenPageLimit * 2) + 1 || this.mItems.size() >= count) ? null : 1;
        int i2 = this.mCurItem;
        int i3 = 0;
        Object obj2 = null;
        while (i3 < this.mItems.size()) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(i3);
            int itemPosition = this.mAdapter.getItemPosition(itemInfo.object);
            if (itemPosition != -1) {
                if (itemPosition == -2) {
                    this.mItems.remove(i3);
                    i3--;
                    if (obj2 == null) {
                        this.mAdapter.startUpdate((ViewGroup) this);
                        obj2 = 1;
                    }
                    this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
                    i = this.mCurItem;
                    if (i == itemInfo.position) {
                        i2 = Math.max(0, Math.min(i, count - 1));
                    }
                } else {
                    int i4 = itemInfo.position;
                    if (i4 != itemPosition) {
                        if (i4 == this.mCurItem) {
                            i2 = itemPosition;
                        }
                        itemInfo.position = itemPosition;
                    }
                }
                obj = 1;
            }
            i3++;
        }
        if (obj2 != null) {
            this.mAdapter.finishUpdate((ViewGroup) this);
        }
        Collections.sort(this.mItems, COMPARATOR);
        if (obj != null) {
            count = getChildCount();
            for (i = 0; i < count; i++) {
                LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
                if (!layoutParams.isDecor) {
                    layoutParams.widthFactor = 0.0f;
                }
            }
            setCurrentItemInternal(i2, false, true);
            requestLayout();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || executeKeyEvent(keyEvent);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == 0) {
                ItemInfo infoForChild = infoForChild(childAt);
                if (infoForChild != null && infoForChild.position == this.mCurItem && childAt.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                    return true;
                }
            }
        }
        return false;
    }

    public float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00ab  */
    /* JADX WARNING: Missing block: B:7:0x0015, code skipped:
            if (r0.getCount() > 1) goto L_0x0024;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(Canvas canvas) {
        int height;
        int width;
        super.draw(canvas);
        int overScrollMode = getOverScrollMode();
        int i = 0;
        if (overScrollMode != 0) {
            if (overScrollMode == 1) {
                PagerAdapter pagerAdapter = this.mAdapter;
                if (pagerAdapter != null) {
                }
            }
            this.mLeftEdge.finish();
            this.mRightEdge.finish();
            if (i == 0) {
                ViewCompat.postInvalidateOnAnimation(this);
                return;
            }
            return;
        }
        if (!this.mLeftEdge.isFinished()) {
            overScrollMode = canvas.save();
            height = (getHeight() - getPaddingTop()) - getPaddingBottom();
            width = getWidth();
            canvas.rotate(270.0f);
            canvas.translate((float) (getPaddingTop() + (-height)), this.mFirstOffset * ((float) width));
            this.mLeftEdge.setSize(height, width);
            i = 0 | this.mLeftEdge.draw(canvas);
            canvas.restoreToCount(overScrollMode);
        }
        if (!this.mRightEdge.isFinished()) {
            overScrollMode = canvas.save();
            height = getWidth();
            width = (getHeight() - getPaddingTop()) - getPaddingBottom();
            canvas.rotate(90.0f);
            canvas.translate((float) (-getPaddingTop()), (-(this.mLastOffset + 1.0f)) * ((float) height));
            this.mRightEdge.setSize(width, height);
            i |= this.mRightEdge.draw(canvas);
            canvas.restoreToCount(overScrollMode);
        }
        if (i == 0) {
        }
    }

    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mMarginDrawable;
        if (drawable != null && drawable.isStateful()) {
            drawable.setState(getDrawableState());
        }
    }

    public void endFakeDrag() {
        if (this.mFakeDragging) {
            if (this.mAdapter != null) {
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                int xVelocity = (int) velocityTracker.getXVelocity(this.mActivePointerId);
                this.mPopulatePending = true;
                int clientWidth = getClientWidth();
                int scrollX = getScrollX();
                ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
                setCurrentItemInternal(determineTargetPage(infoForCurrentScrollPosition.position, ((((float) scrollX) / ((float) clientWidth)) - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, xVelocity, (int) (this.mLastMotionX - this.mInitialMotionX)), true, true, xVelocity);
            }
            endDrag();
            this.mFakeDragging = false;
            return;
        }
        throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }

    public boolean executeKeyEvent(@NonNull KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode != 21) {
                if (keyCode != 22) {
                    if (keyCode == 61) {
                        if (keyEvent.hasNoModifiers()) {
                            return arrowScroll(2);
                        }
                        if (keyEvent.hasModifiers(1)) {
                            return arrowScroll(1);
                        }
                    }
                } else if (keyEvent.hasModifiers(2)) {
                    return pageRight();
                } else {
                    return arrowScroll(66);
                }
            } else if (keyEvent.hasModifiers(2)) {
                return pageLeft();
            } else {
                return arrowScroll(17);
            }
        }
        return false;
    }

    public void fakeDragBy(float f) {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        } else if (this.mAdapter != null) {
            this.mLastMotionX += f;
            float scrollX = ((float) getScrollX()) - f;
            f = (float) getClientWidth();
            float f2 = this.mFirstOffset * f;
            float f3 = this.mLastOffset * f;
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(0);
            ArrayList arrayList = this.mItems;
            ItemInfo itemInfo2 = (ItemInfo) arrayList.get(arrayList.size() - 1);
            if (itemInfo.position != 0) {
                f2 = itemInfo.offset * f;
            }
            if (itemInfo2.position != this.mAdapter.getCount() - 1) {
                f3 = itemInfo2.offset * f;
            }
            if (scrollX < f2) {
                scrollX = f2;
            } else if (scrollX > f3) {
                scrollX = f3;
            }
            int i = (int) scrollX;
            this.mLastMotionX = (scrollX - ((float) i)) + this.mLastMotionX;
            scrollTo(i, getScrollY());
            pageScrolled(i);
            MotionEvent obtain = MotionEvent.obtain(this.mFakeDragBeginTime, SystemClock.uptimeMillis(), 2, this.mLastMotionX, 0.0f, 0);
            this.mVelocityTracker.addMovement(obtain);
            obtain.recycle();
        }
    }

    public android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return generateDefaultLayoutParams();
    }

    @Nullable
    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }

    public int getChildDrawingOrder(int i, int i2) {
        if (this.mDrawingOrder == 2) {
            i2 = (i - 1) - i2;
        }
        return ((LayoutParams) ((View) this.mDrawingOrderedChildren.get(i2)).getLayoutParams()).childIndex;
    }

    public int getCurrentItem() {
        return this.mCurItem;
    }

    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }

    public int getPageMargin() {
        return this.mPageMargin;
    }

    public ItemInfo infoForAnyChild(View view) {
        while (true) {
            ViewPager parent = view.getParent();
            if (parent == this) {
                return infoForChild(view);
            }
            if (parent != null && (parent instanceof View)) {
                view = parent;
            }
        }
        return null;
    }

    public ItemInfo infoForChild(View view) {
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(view, itemInfo.object)) {
                return itemInfo;
            }
        }
        return null;
    }

    public ItemInfo infoForPosition(int i) {
        for (int i2 = 0; i2 < this.mItems.size(); i2++) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(i2);
            if (itemInfo.position == i) {
                return itemInfo;
            }
        }
        return null;
    }

    public void initViewPager() {
        setWillNotDraw(false);
        setDescendantFocusability(262144);
        setFocusable(true);
        Context context = getContext();
        this.mScroller = new Scroller(context, sInterpolator);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        float f = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
        this.mMinimumVelocity = (int) (400.0f * f);
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mLeftEdge = new EdgeEffect(context);
        this.mRightEdge = new EdgeEffect(context);
        this.mFlingDistance = (int) (25.0f * f);
        this.mCloseEnough = (int) (2.0f * f);
        this.mDefaultGutterSize = (int) (f * 16.0f);
        ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() {
            private final Rect mTempRect = new Rect();

            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                WindowInsetsCompat onApplyWindowInsets = ViewCompat.onApplyWindowInsets(view, windowInsetsCompat);
                if (onApplyWindowInsets.isConsumed()) {
                    return onApplyWindowInsets;
                }
                Rect rect = this.mTempRect;
                rect.left = onApplyWindowInsets.getSystemWindowInsetLeft();
                rect.top = onApplyWindowInsets.getSystemWindowInsetTop();
                rect.right = onApplyWindowInsets.getSystemWindowInsetRight();
                rect.bottom = onApplyWindowInsets.getSystemWindowInsetBottom();
                int childCount = ViewPager.this.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    WindowInsetsCompat dispatchApplyWindowInsets = ViewCompat.dispatchApplyWindowInsets(ViewPager.this.getChildAt(i), onApplyWindowInsets);
                    rect.left = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetLeft(), rect.left);
                    rect.top = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetTop(), rect.top);
                    rect.right = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetRight(), rect.right);
                    rect.bottom = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetBottom(), rect.bottom);
                }
                return onApplyWindowInsets.replaceSystemWindowInsets(rect.left, rect.top, rect.right, rect.bottom);
            }
        });
    }

    public boolean isFakeDragging() {
        return this.mFakeDragging;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    public void onDetachedFromWindow() {
        removeCallbacks(this.mEndScrollRunnable);
        Scroller scroller = this.mScroller;
        if (!(scroller == null || scroller.isFinished())) {
            this.mScroller.abortAnimation();
        }
        super.onDetachedFromWindow();
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x005a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPageMargin > 0 && this.mMarginDrawable != null && this.mItems.size() > 0 && this.mAdapter != null) {
            int scrollX = getScrollX();
            int width = getWidth();
            float f = (float) width;
            float f2 = ((float) this.mPageMargin) / f;
            int i = 0;
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(0);
            float f3 = itemInfo.offset;
            int size = this.mItems.size();
            int i2 = itemInfo.position;
            int i3 = ((ItemInfo) this.mItems.get(size - 1)).position;
            while (i2 < i3) {
                int i4;
                float f4;
                float f5;
                while (true) {
                    i4 = itemInfo.position;
                    float f6;
                    if (i2 > i4 && i < size) {
                        i++;
                        itemInfo = (ItemInfo) this.mItems.get(i);
                    } else if (i2 != i4) {
                        f3 = itemInfo.offset;
                        f6 = itemInfo.widthFactor;
                        f4 = (f3 + f6) * f;
                        f3 = (f3 + f6) + f2;
                    } else {
                        f6 = this.mAdapter.getPageWidth(i2);
                        f4 = (f3 + f6) * f;
                        f3 = (f6 + f2) + f3;
                    }
                }
                if (i2 != i4) {
                }
                if (((float) this.mPageMargin) + f4 > ((float) scrollX)) {
                    f5 = f2;
                    this.mMarginDrawable.setBounds(Math.round(f4), this.mTopPageBounds, Math.round(((float) this.mPageMargin) + f4), this.mBottomPageBounds);
                    this.mMarginDrawable.draw(canvas);
                } else {
                    Canvas canvas2 = canvas;
                    f5 = f2;
                }
                if (f4 <= ((float) (scrollX + width))) {
                    i2++;
                    f2 = f5;
                } else {
                    return;
                }
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        MotionEvent motionEvent2 = motionEvent;
        int action = motionEvent.getAction() & 255;
        if (action == 3 || action == 1) {
            resetTouch();
            return false;
        }
        if (action != 0) {
            if (this.mIsBeingDragged) {
                return true;
            }
            if (this.mIsUnableToDrag) {
                return false;
            }
        }
        float x;
        if (action == 0) {
            x = motionEvent.getX();
            this.mInitialMotionX = x;
            this.mLastMotionX = x;
            x = motionEvent.getY();
            this.mInitialMotionY = x;
            this.mLastMotionY = x;
            this.mActivePointerId = motionEvent2.getPointerId(0);
            this.mIsUnableToDrag = false;
            this.mIsScrollStarted = true;
            this.mScroller.computeScrollOffset();
            if (this.mScrollState != 2 || Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) <= this.mCloseEnough) {
                completeScroll(false);
                this.mIsBeingDragged = false;
            } else {
                this.mScroller.abortAnimation();
                this.mPopulatePending = false;
                populate();
                this.mIsBeingDragged = true;
                requestParentDisallowInterceptTouchEvent(true);
                setScrollState(1);
            }
        } else if (action == 2) {
            action = this.mActivePointerId;
            if (action != -1) {
                action = motionEvent2.findPointerIndex(action);
                float x2 = motionEvent2.getX(action);
                float f = x2 - this.mLastMotionX;
                float abs = Math.abs(f);
                float y = motionEvent2.getY(action);
                float abs2 = Math.abs(y - this.mInitialMotionY);
                int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
                if (!(i == 0 || isGutterDrag(this.mLastMotionX, f))) {
                    if (canScroll(this, false, (int) f, (int) x2, (int) y)) {
                        this.mLastMotionX = x2;
                        this.mLastMotionY = y;
                        this.mIsUnableToDrag = true;
                        return false;
                    }
                }
                action = this.mTouchSlop;
                if (abs > ((float) action) && abs * 0.5f > abs2) {
                    this.mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                    x = this.mInitialMotionX;
                    f = (float) this.mTouchSlop;
                    this.mLastMotionX = i > 0 ? x + f : x - f;
                    this.mLastMotionY = y;
                    setScrollingCacheEnabled(true);
                } else if (abs2 > ((float) action)) {
                    this.mIsUnableToDrag = true;
                }
                if (this.mIsBeingDragged && performDrag(x2)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }
        } else if (action == 6) {
            onSecondaryPointerUp(motionEvent);
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent2);
        return this.mIsBeingDragged;
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0071  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        boolean z2;
        int childCount = getChildCount();
        int i6 = i3 - i;
        int i7 = i4 - i2;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int scrollX = getScrollX();
        int i8 = 0;
        for (i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isDecor) {
                    int i9;
                    int i10 = layoutParams.gravity;
                    int i11 = i10 & 7;
                    i10 &= 112;
                    if (i11 != 1) {
                        if (i11 == 3) {
                            i11 = childAt.getMeasuredWidth() + paddingLeft;
                        } else if (i11 != 5) {
                            i11 = paddingLeft;
                        } else {
                            i11 = (i6 - paddingRight) - childAt.getMeasuredWidth();
                            paddingRight += childAt.getMeasuredWidth();
                        }
                        if (i10 == 16) {
                            if (i10 == 48) {
                                i10 = childAt.getMeasuredHeight() + paddingTop;
                            } else if (i10 != 80) {
                                i10 = paddingTop;
                            } else {
                                i10 = (i7 - paddingBottom) - childAt.getMeasuredHeight();
                                paddingBottom += childAt.getMeasuredHeight();
                            }
                            paddingLeft += scrollX;
                            childAt.layout(paddingLeft, paddingTop, childAt.getMeasuredWidth() + paddingLeft, childAt.getMeasuredHeight() + paddingTop);
                            i8++;
                            paddingTop = i10;
                            paddingLeft = i11;
                        } else {
                            i10 = Math.max((i7 - childAt.getMeasuredHeight()) / 2, paddingTop);
                        }
                        i9 = i10;
                        i10 = paddingTop;
                        paddingTop = i9;
                        paddingLeft += scrollX;
                        childAt.layout(paddingLeft, paddingTop, childAt.getMeasuredWidth() + paddingLeft, childAt.getMeasuredHeight() + paddingTop);
                        i8++;
                        paddingTop = i10;
                        paddingLeft = i11;
                    } else {
                        i11 = Math.max((i6 - childAt.getMeasuredWidth()) / 2, paddingLeft);
                    }
                    i9 = i11;
                    i11 = paddingLeft;
                    paddingLeft = i9;
                    if (i10 == 16) {
                    }
                    i9 = i10;
                    i10 = paddingTop;
                    paddingTop = i9;
                    paddingLeft += scrollX;
                    childAt.layout(paddingLeft, paddingTop, childAt.getMeasuredWidth() + paddingLeft, childAt.getMeasuredHeight() + paddingTop);
                    i8++;
                    paddingTop = i10;
                    paddingLeft = i11;
                }
            }
        }
        i6 = (i6 - paddingLeft) - paddingRight;
        for (paddingRight = 0; paddingRight < childCount; paddingRight++) {
            View childAt2 = getChildAt(paddingRight);
            if (childAt2.getVisibility() != 8) {
                LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                if (!layoutParams2.isDecor) {
                    ItemInfo infoForChild = infoForChild(childAt2);
                    if (infoForChild != null) {
                        float f = (float) i6;
                        i5 = ((int) (infoForChild.offset * f)) + paddingLeft;
                        if (layoutParams2.needsMeasure) {
                            layoutParams2.needsMeasure = false;
                            childAt2.measure(MeasureSpec.makeMeasureSpec((int) (f * layoutParams2.widthFactor), BasicMeasure.EXACTLY), MeasureSpec.makeMeasureSpec((i7 - paddingTop) - paddingBottom, BasicMeasure.EXACTLY));
                        }
                        childAt2.layout(i5, paddingTop, childAt2.getMeasuredWidth() + i5, childAt2.getMeasuredHeight() + paddingTop);
                    }
                }
            }
        }
        this.mTopPageBounds = paddingTop;
        this.mBottomPageBounds = i7 - paddingBottom;
        this.mDecorChildCount = i8;
        if (this.mFirstLayout) {
            z2 = false;
            scrollToItem(this.mCurItem, false, 0, false);
        } else {
            z2 = false;
        }
        this.mFirstLayout = z2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00a2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int i, int i2) {
        int i3 = 0;
        setMeasuredDimension(ViewGroup.getDefaultSize(0, i), ViewGroup.getDefaultSize(0, i2));
        i = getMeasuredWidth();
        this.mGutterSize = Math.min(i / 10, this.mDefaultGutterSize);
        i = (i - getPaddingLeft()) - getPaddingRight();
        i2 = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        int childCount = getChildCount();
        int i4 = 0;
        while (true) {
            boolean z = true;
            int i5 = BasicMeasure.EXACTLY;
            if (i4 >= childCount) {
                break;
            }
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams != null && layoutParams.isDecor) {
                    int i6;
                    int i7;
                    int i8;
                    int i9 = layoutParams.gravity;
                    int i10 = i9 & 7;
                    i9 &= 112;
                    Object obj = (i9 == 48 || i9 == 80) ? 1 : null;
                    if (!(i10 == 3 || i10 == 5)) {
                        z = false;
                    }
                    i10 = Integer.MIN_VALUE;
                    if (obj != null) {
                        i10 = BasicMeasure.EXACTLY;
                    } else if (z) {
                        i6 = BasicMeasure.EXACTLY;
                        i7 = layoutParams.width;
                        if (i7 == -2) {
                            if (i7 == -1) {
                                i7 = i;
                            }
                            i10 = BasicMeasure.EXACTLY;
                        } else {
                            i7 = i;
                        }
                        i8 = layoutParams.height;
                        if (i8 != -2) {
                            i8 = i2;
                            i5 = i6;
                        } else if (i8 == -1) {
                            i8 = i2;
                        }
                        childAt.measure(MeasureSpec.makeMeasureSpec(i7, i10), MeasureSpec.makeMeasureSpec(i8, i5));
                        if (obj == null) {
                            i2 -= childAt.getMeasuredHeight();
                        } else if (z) {
                            i -= childAt.getMeasuredWidth();
                        }
                    }
                    i6 = Integer.MIN_VALUE;
                    i7 = layoutParams.width;
                    if (i7 == -2) {
                    }
                    i8 = layoutParams.height;
                    if (i8 != -2) {
                    }
                    childAt.measure(MeasureSpec.makeMeasureSpec(i7, i10), MeasureSpec.makeMeasureSpec(i8, i5));
                    if (obj == null) {
                    }
                }
            }
            i4++;
        }
        this.mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY);
        this.mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(i2, BasicMeasure.EXACTLY);
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        i2 = getChildCount();
        while (i3 < i2) {
            View childAt2 = getChildAt(i3);
            if (childAt2.getVisibility() != 8) {
                LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                if (layoutParams2 == null || !layoutParams2.isDecor) {
                    childAt2.measure(MeasureSpec.makeMeasureSpec((int) (((float) i) * layoutParams2.widthFactor), BasicMeasure.EXACTLY), this.mChildHeightMeasureSpec);
                }
            }
            i3++;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0064  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @CallSuper
    public void onPageScrolled(int i, float f, int i2) {
        if (this.mDecorChildCount > 0) {
            int scrollX = getScrollX();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int width = getWidth();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isDecor) {
                    int i4 = layoutParams.gravity & 7;
                    if (i4 != 1) {
                        if (i4 == 3) {
                            i4 = childAt.getWidth() + paddingLeft;
                        } else if (i4 != 5) {
                            i4 = paddingLeft;
                        } else {
                            i4 = (width - paddingRight) - childAt.getMeasuredWidth();
                            paddingRight += childAt.getMeasuredWidth();
                        }
                        paddingLeft = (paddingLeft + scrollX) - childAt.getLeft();
                        if (paddingLeft != 0) {
                            childAt.offsetLeftAndRight(paddingLeft);
                        }
                        paddingLeft = i4;
                    } else {
                        i4 = Math.max((width - childAt.getMeasuredWidth()) / 2, paddingLeft);
                    }
                    int i5 = i4;
                    i4 = paddingLeft;
                    paddingLeft = i5;
                    paddingLeft = (paddingLeft + scrollX) - childAt.getLeft();
                    if (paddingLeft != 0) {
                    }
                    paddingLeft = i4;
                }
            }
        }
        dispatchOnPageScrolled(i, f, i2);
        if (this.mPageTransformer != null) {
            i = getScrollX();
            int childCount2 = getChildCount();
            for (int i6 = 0; i6 < childCount2; i6++) {
                View childAt2 = getChildAt(i6);
                if (!((LayoutParams) childAt2.getLayoutParams()).isDecor) {
                    this.mPageTransformer.transformPage(childAt2, ((float) (childAt2.getLeft() - i)) / ((float) getClientWidth()));
                }
            }
        }
        this.mCalledSuper = true;
    }

    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        int i2;
        int childCount = getChildCount();
        int i3 = -1;
        if ((i & 2) != 0) {
            i3 = childCount;
            childCount = 0;
            i2 = 1;
        } else {
            childCount--;
            i2 = -1;
        }
        while (childCount != i3) {
            View childAt = getChildAt(childCount);
            if (childAt.getVisibility() == 0) {
                ItemInfo infoForChild = infoForChild(childAt);
                if (infoForChild != null && infoForChild.position == this.mCurItem && childAt.requestFocus(i, rect)) {
                    return true;
                }
            }
            childCount += i2;
        }
        return false;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            PagerAdapter pagerAdapter = this.mAdapter;
            if (pagerAdapter != null) {
                pagerAdapter.restoreState(savedState.adapterState, savedState.loader);
                setCurrentItemInternal(savedState.position, false, true);
            } else {
                this.mRestoredCurItem = savedState.position;
                this.mRestoredAdapterState = savedState.adapterState;
                this.mRestoredClassLoader = savedState.loader;
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.mCurItem;
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter != null) {
            savedState.adapterState = pagerAdapter.saveState();
        }
        return savedState;
    }

    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3) {
            i2 = this.mPageMargin;
            recomputeScrollPosition(i, i3, i2, i2);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mFakeDragging) {
            return true;
        }
        boolean z = false;
        if (motionEvent.getAction() == 0 && motionEvent.getEdgeFlags() != 0) {
            return false;
        }
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter == null || pagerAdapter.getCount() == 0) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int action = motionEvent.getAction() & 255;
        float x;
        if (action == 0) {
            this.mScroller.abortAnimation();
            this.mPopulatePending = false;
            populate();
            x = motionEvent.getX();
            this.mInitialMotionX = x;
            this.mLastMotionX = x;
            x = motionEvent.getY();
            this.mInitialMotionY = x;
            this.mLastMotionY = x;
            this.mActivePointerId = motionEvent.getPointerId(0);
        } else if (action != 1) {
            if (action == 2) {
                if (!this.mIsBeingDragged) {
                    action = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (action == -1) {
                        z = resetTouch();
                    } else {
                        float x2 = motionEvent.getX(action);
                        float abs = Math.abs(x2 - this.mLastMotionX);
                        x = motionEvent.getY(action);
                        float abs2 = Math.abs(x - this.mLastMotionY);
                        if (abs > ((float) this.mTouchSlop) && abs > abs2) {
                            this.mIsBeingDragged = true;
                            requestParentDisallowInterceptTouchEvent(true);
                            abs = this.mInitialMotionX;
                            this.mLastMotionX = x2 - abs > 0.0f ? abs + ((float) this.mTouchSlop) : abs - ((float) this.mTouchSlop);
                            this.mLastMotionY = x;
                            setScrollState(1);
                            setScrollingCacheEnabled(true);
                            ViewParent parent = getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }
                if (this.mIsBeingDragged) {
                    z = false | performDrag(motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId)));
                }
            } else if (action != 3) {
                if (action == 5) {
                    action = motionEvent.getActionIndex();
                    this.mLastMotionX = motionEvent.getX(action);
                    this.mActivePointerId = motionEvent.getPointerId(action);
                } else if (action == 6) {
                    onSecondaryPointerUp(motionEvent);
                    this.mLastMotionX = motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId));
                }
            } else if (this.mIsBeingDragged) {
                scrollToItem(this.mCurItem, true, 0, false);
                z = resetTouch();
            }
        } else if (this.mIsBeingDragged) {
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
            action = (int) velocityTracker.getXVelocity(this.mActivePointerId);
            this.mPopulatePending = true;
            int clientWidth = getClientWidth();
            int scrollX = getScrollX();
            ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
            float f = (float) clientWidth;
            setCurrentItemInternal(determineTargetPage(infoForCurrentScrollPosition.position, ((((float) scrollX) / f) - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + (((float) this.mPageMargin) / f)), action, (int) (motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialMotionX)), true, true, action);
            z = resetTouch();
        }
        if (z) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        return true;
    }

    public boolean pageLeft() {
        int i = this.mCurItem;
        if (i <= 0) {
            return false;
        }
        setCurrentItem(i - 1, true);
        return true;
    }

    public boolean pageRight() {
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter == null || this.mCurItem >= pagerAdapter.getCount() - 1) {
            return false;
        }
        setCurrentItem(this.mCurItem + 1, true);
        return true;
    }

    public void populate() {
        populate(this.mCurItem);
    }

    public void removeOnAdapterChangeListener(@NonNull OnAdapterChangeListener onAdapterChangeListener) {
        List list = this.mAdapterChangeListeners;
        if (list != null) {
            list.remove(onAdapterChangeListener);
        }
    }

    public void removeOnPageChangeListener(@NonNull OnPageChangeListener onPageChangeListener) {
        List list = this.mOnPageChangeListeners;
        if (list != null) {
            list.remove(onPageChangeListener);
        }
    }

    public void removeView(View view) {
        if (this.mInLayout) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    public void setAdapter(@Nullable PagerAdapter pagerAdapter) {
        PagerAdapter pagerAdapter2 = this.mAdapter;
        int i = 0;
        if (pagerAdapter2 != null) {
            pagerAdapter2.setViewPagerObserver(null);
            this.mAdapter.startUpdate((ViewGroup) this);
            for (int i2 = 0; i2 < this.mItems.size(); i2++) {
                ItemInfo itemInfo = (ItemInfo) this.mItems.get(i2);
                this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
            }
            this.mAdapter.finishUpdate((ViewGroup) this);
            this.mItems.clear();
            removeNonDecorViews();
            this.mCurItem = 0;
            scrollTo(0, 0);
        }
        pagerAdapter2 = this.mAdapter;
        this.mAdapter = pagerAdapter;
        this.mExpectedAdapterCount = 0;
        if (pagerAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.setViewPagerObserver(this.mObserver);
            this.mPopulatePending = false;
            boolean z = this.mFirstLayout;
            this.mFirstLayout = true;
            this.mExpectedAdapterCount = this.mAdapter.getCount();
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            } else if (z) {
                requestLayout();
            } else {
                populate();
            }
        }
        List list = this.mAdapterChangeListeners;
        if (list != null && !list.isEmpty()) {
            int size = this.mAdapterChangeListeners.size();
            while (i < size) {
                ((OnAdapterChangeListener) this.mAdapterChangeListeners.get(i)).onAdapterChanged(this, pagerAdapter2, pagerAdapter);
                i++;
            }
        }
    }

    public void setCurrentItem(int i) {
        this.mPopulatePending = false;
        setCurrentItemInternal(i, this.mFirstLayout ^ 1, false);
    }

    public void setCurrentItemInternal(int i, boolean z, boolean z2) {
        setCurrentItemInternal(i, z, z2, 0);
    }

    public OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener onPageChangeListener) {
        OnPageChangeListener onPageChangeListener2 = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = onPageChangeListener;
        return onPageChangeListener2;
    }

    public void setOffscreenPageLimit(int i) {
        if (i < 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Requested offscreen page limit ");
            stringBuilder.append(i);
            stringBuilder.append(" too small; defaulting to ");
            stringBuilder.append(1);
            Log.w(TAG, stringBuilder.toString());
            i = 1;
        }
        if (i != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = i;
            populate();
        }
    }

    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    public void setPageMargin(int i) {
        int i2 = this.mPageMargin;
        this.mPageMargin = i;
        int width = getWidth();
        recomputeScrollPosition(width, width, i, i2);
        requestLayout();
    }

    public void setPageMarginDrawable(@Nullable Drawable drawable) {
        this.mMarginDrawable = drawable;
        if (drawable != null) {
            refreshDrawableState();
        }
        setWillNotDraw(drawable == null);
        invalidate();
    }

    public void setPageTransformer(boolean z, @Nullable PageTransformer pageTransformer) {
        setPageTransformer(z, pageTransformer, 2);
    }

    public void setScrollState(int i) {
        if (this.mScrollState != i) {
            this.mScrollState = i;
            if (this.mPageTransformer != null) {
                enableLayers(i != 0);
            }
            dispatchOnScrollStateChanged(i);
        }
    }

    public void smoothScrollTo(int i, int i2) {
        smoothScrollTo(i, i2, 0);
    }

    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mMarginDrawable;
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX WARNING: Missing block: B:22:0x005c, code skipped:
            if (r5 == r6) goto L_0x0063;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void populate(int i) {
        ItemInfo infoForPosition;
        int i2 = this.mCurItem;
        if (i2 != i) {
            infoForPosition = infoForPosition(i2);
            this.mCurItem = i;
        } else {
            infoForPosition = null;
        }
        if (this.mAdapter == null) {
            sortChildDrawingOrder();
        } else if (this.mPopulatePending) {
            sortChildDrawingOrder();
        } else if (getWindowToken() != null) {
            this.mAdapter.startUpdate((ViewGroup) this);
            i = this.mOffscreenPageLimit;
            int max = Math.max(0, this.mCurItem - i);
            int count = this.mAdapter.getCount();
            i = Math.min(count - 1, this.mCurItem + i);
            if (count == this.mExpectedAdapterCount) {
                ItemInfo itemInfo;
                int i3;
                ItemInfo infoForChild;
                int i4 = 0;
                while (i4 < this.mItems.size()) {
                    itemInfo = (ItemInfo) this.mItems.get(i4);
                    int i5 = itemInfo.position;
                    i3 = this.mCurItem;
                    if (i5 < i3) {
                        i4++;
                    }
                }
                itemInfo = null;
                if (itemInfo == null && count > 0) {
                    itemInfo = addNewItem(this.mCurItem, i4);
                }
                if (itemInfo != null) {
                    float f;
                    i3 = i4 - 1;
                    ItemInfo itemInfo2 = i3 >= 0 ? (ItemInfo) this.mItems.get(i3) : null;
                    int clientWidth = getClientWidth();
                    if (clientWidth <= 0) {
                        f = 0.0f;
                    } else {
                        f = (2.0f - itemInfo.widthFactor) + (((float) getPaddingLeft()) / ((float) clientWidth));
                    }
                    int i6 = this.mCurItem - 1;
                    float f2 = 0.0f;
                    while (i6 >= 0) {
                        if (f2 < f || i6 >= max) {
                            if (itemInfo2 == null || i6 != itemInfo2.position) {
                                f2 += addNewItem(i6, i3 + 1).widthFactor;
                                i4++;
                                if (i3 >= 0) {
                                    itemInfo2 = (ItemInfo) this.mItems.get(i3);
                                    i6--;
                                }
                            } else {
                                f2 += itemInfo2.widthFactor;
                                i3--;
                                if (i3 >= 0) {
                                    itemInfo2 = (ItemInfo) this.mItems.get(i3);
                                    i6--;
                                }
                            }
                        } else if (itemInfo2 == null) {
                            break;
                        } else {
                            if (i6 == itemInfo2.position && !itemInfo2.scrolling) {
                                this.mItems.remove(i3);
                                this.mAdapter.destroyItem((ViewGroup) this, i6, itemInfo2.object);
                                i3--;
                                i4--;
                                if (i3 >= 0) {
                                    itemInfo2 = (ItemInfo) this.mItems.get(i3);
                                }
                            }
                            i6--;
                        }
                        itemInfo2 = null;
                        i6--;
                    }
                    float f3 = itemInfo.widthFactor;
                    i3 = i4 + 1;
                    if (f3 < 2.0f) {
                        float f4;
                        itemInfo2 = i3 < this.mItems.size() ? (ItemInfo) this.mItems.get(i3) : null;
                        if (clientWidth <= 0) {
                            f4 = 0.0f;
                        } else {
                            f4 = (((float) getPaddingRight()) / ((float) clientWidth)) + 2.0f;
                        }
                        int i7 = this.mCurItem;
                        while (true) {
                            i7++;
                            if (i7 >= count) {
                                break;
                            }
                            if (f3 < f4 || i7 <= i) {
                                if (itemInfo2 == null || i7 != itemInfo2.position) {
                                    itemInfo2 = addNewItem(i7, i3);
                                    i3++;
                                    f3 += itemInfo2.widthFactor;
                                    if (i3 < this.mItems.size()) {
                                        itemInfo2 = (ItemInfo) this.mItems.get(i3);
                                    }
                                } else {
                                    f3 += itemInfo2.widthFactor;
                                    i3++;
                                    if (i3 < this.mItems.size()) {
                                        itemInfo2 = (ItemInfo) this.mItems.get(i3);
                                    }
                                }
                            } else if (itemInfo2 == null) {
                                break;
                            } else if (i7 == itemInfo2.position && !itemInfo2.scrolling) {
                                this.mItems.remove(i3);
                                this.mAdapter.destroyItem((ViewGroup) this, i7, itemInfo2.object);
                                if (i3 < this.mItems.size()) {
                                    itemInfo2 = (ItemInfo) this.mItems.get(i3);
                                }
                            }
                            itemInfo2 = null;
                        }
                    }
                    calculatePageOffsets(itemInfo, i4, infoForPosition);
                    this.mAdapter.setPrimaryItem((ViewGroup) this, this.mCurItem, itemInfo.object);
                }
                this.mAdapter.finishUpdate((ViewGroup) this);
                i = getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    View childAt = getChildAt(i2);
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    layoutParams.childIndex = i2;
                    if (!layoutParams.isDecor && layoutParams.widthFactor == 0.0f) {
                        infoForChild = infoForChild(childAt);
                        if (infoForChild != null) {
                            layoutParams.widthFactor = infoForChild.widthFactor;
                            layoutParams.position = infoForChild.position;
                        }
                    }
                }
                sortChildDrawingOrder();
                if (hasFocus()) {
                    View findFocus = findFocus();
                    ItemInfo infoForAnyChild = findFocus != null ? infoForAnyChild(findFocus) : null;
                    if (infoForAnyChild == null || infoForAnyChild.position != this.mCurItem) {
                        for (i = 0; i < getChildCount(); i++) {
                            View childAt2 = getChildAt(i);
                            infoForChild = infoForChild(childAt2);
                            if (infoForChild != null && infoForChild.position == this.mCurItem && childAt2.requestFocus(2)) {
                                break;
                            }
                        }
                    }
                }
                return;
            }
            String resourceName;
            try {
                resourceName = getResources().getResourceName(getId());
            } catch (NotFoundException unused) {
                resourceName = Integer.toHexString(getId());
            }
            StringBuilder L = a.L("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: ");
            L.append(this.mExpectedAdapterCount);
            L.append(", found: ");
            L.append(count);
            L.append(" Pager id: ");
            L.append(resourceName);
            L.append(" Pager class: ");
            L.append(getClass());
            L.append(" Problematic adapter: ");
            L.append(this.mAdapter.getClass());
            throw new IllegalStateException(L.toString());
        }
    }

    public void setCurrentItemInternal(int i, boolean z, boolean z2, int i2) {
        PagerAdapter pagerAdapter = this.mAdapter;
        boolean z3 = false;
        if (pagerAdapter == null || pagerAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(false);
        } else if (z2 || this.mCurItem != i || this.mItems.size() == 0) {
            if (i < 0) {
                i = 0;
            } else if (i >= this.mAdapter.getCount()) {
                i = this.mAdapter.getCount() - 1;
            }
            int i3 = this.mOffscreenPageLimit;
            int i4 = this.mCurItem;
            if (i > i4 + i3 || i < i4 - i3) {
                for (i3 = 0; i3 < this.mItems.size(); i3++) {
                    ((ItemInfo) this.mItems.get(i3)).scrolling = true;
                }
            }
            if (this.mCurItem != i) {
                z3 = true;
            }
            if (this.mFirstLayout) {
                this.mCurItem = i;
                if (z3) {
                    dispatchOnPageSelected(i);
                }
                requestLayout();
            } else {
                populate(i);
                scrollToItem(i, z, i2, z3);
            }
        } else {
            setScrollingCacheEnabled(false);
        }
    }

    public void setPageTransformer(boolean z, @Nullable PageTransformer pageTransformer, int i) {
        int i2 = 1;
        boolean z2 = pageTransformer != null;
        Object obj = z2 != (this.mPageTransformer != null) ? 1 : null;
        this.mPageTransformer = pageTransformer;
        setChildrenDrawingOrderEnabled(z2);
        if (z2) {
            if (z) {
                i2 = 2;
            }
            this.mDrawingOrder = i2;
            this.mPageTransformerLayerType = i;
        } else {
            this.mDrawingOrder = 0;
        }
        if (obj != null) {
            populate();
        }
    }

    public void smoothScrollTo(int i, int i2, int i3) {
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        int currX;
        Scroller scroller = this.mScroller;
        Object obj = (scroller == null || scroller.isFinished()) ? null : 1;
        if (obj != null) {
            currX = this.mIsScrollStarted ? this.mScroller.getCurrX() : this.mScroller.getStartX();
            this.mScroller.abortAnimation();
            setScrollingCacheEnabled(false);
        } else {
            currX = getScrollX();
        }
        int i4 = currX;
        int scrollY = getScrollY();
        int i5 = i - i4;
        int i6 = i2 - scrollY;
        if (i5 == 0 && i6 == 0) {
            completeScroll(false);
            populate();
            setScrollState(0);
            return;
        }
        setScrollingCacheEnabled(true);
        setScrollState(2);
        i = getClientWidth();
        i2 = i / 2;
        float f = (float) i;
        float f2 = (float) i2;
        float distanceInfluenceForSnapDuration = (distanceInfluenceForSnapDuration(Math.min(1.0f, (((float) Math.abs(i5)) * 1.0f) / f)) * f2) + f2;
        i2 = Math.abs(i3);
        if (i2 > 0) {
            i = Math.round(Math.abs(distanceInfluenceForSnapDuration / ((float) i2)) * 1000.0f) * 4;
        } else {
            i = (int) (((((float) Math.abs(i5)) / ((this.mAdapter.getPageWidth(this.mCurItem) * f) + ((float) this.mPageMargin))) + 1.0f) * 100.0f);
        }
        int min = Math.min(i, MAX_SETTLE_DURATION);
        this.mIsScrollStarted = false;
        this.mScroller.startScroll(i4, scrollY, i5, i6, min);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setCurrentItem(int i, boolean z) {
        this.mPopulatePending = false;
        setCurrentItemInternal(i, z, false);
    }

    public void setPageMarginDrawable(@DrawableRes int i) {
        setPageMarginDrawable(ContextCompat.getDrawable(getContext(), i));
    }

    public ViewPager(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        initViewPager();
    }
}
