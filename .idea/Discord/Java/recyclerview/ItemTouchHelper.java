package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.R;
import androidx.recyclerview.widget.RecyclerView.ChildDrawingOrderCallback;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import f.d.b.a.a;
import java.util.ArrayList;
import java.util.List;

public class ItemTouchHelper extends ItemDecoration implements OnChildAttachStateChangeListener {
    public static final int ACTION_MODE_DRAG_MASK = 16711680;
    private static final int ACTION_MODE_IDLE_MASK = 255;
    public static final int ACTION_MODE_SWIPE_MASK = 65280;
    public static final int ACTION_STATE_DRAG = 2;
    public static final int ACTION_STATE_IDLE = 0;
    public static final int ACTION_STATE_SWIPE = 1;
    private static final int ACTIVE_POINTER_ID_NONE = -1;
    public static final int ANIMATION_TYPE_DRAG = 8;
    public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
    public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
    private static final boolean DEBUG = false;
    public static final int DIRECTION_FLAG_COUNT = 8;
    public static final int DOWN = 2;
    public static final int END = 32;
    public static final int LEFT = 4;
    private static final int PIXELS_PER_SECOND = 1000;
    public static final int RIGHT = 8;
    public static final int START = 16;
    private static final String TAG = "ItemTouchHelper";
    public static final int UP = 1;
    private int mActionState = 0;
    public int mActivePointerId = -1;
    @NonNull
    public Callback mCallback;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
    private List<Integer> mDistances;
    private long mDragScrollStartTimeInMs;
    public float mDx;
    public float mDy;
    public GestureDetectorCompat mGestureDetector;
    public float mInitialTouchX;
    public float mInitialTouchY;
    private ItemTouchHelperGestureListener mItemTouchHelperGestureListener;
    private float mMaxSwipeVelocity;
    private final OnItemTouchListener mOnItemTouchListener = new OnItemTouchListener() {
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            ItemTouchHelper itemTouchHelper;
            if (actionMasked == 0) {
                ItemTouchHelper.this.mActivePointerId = motionEvent.getPointerId(0);
                ItemTouchHelper.this.mInitialTouchX = motionEvent.getX();
                ItemTouchHelper.this.mInitialTouchY = motionEvent.getY();
                ItemTouchHelper.this.obtainVelocityTracker();
                itemTouchHelper = ItemTouchHelper.this;
                if (itemTouchHelper.mSelected == null) {
                    RecoverAnimation findAnimation = itemTouchHelper.findAnimation(motionEvent);
                    if (findAnimation != null) {
                        ItemTouchHelper itemTouchHelper2 = ItemTouchHelper.this;
                        itemTouchHelper2.mInitialTouchX -= findAnimation.mX;
                        itemTouchHelper2.mInitialTouchY -= findAnimation.mY;
                        itemTouchHelper2.endRecoverAnimation(findAnimation.mViewHolder, true);
                        if (ItemTouchHelper.this.mPendingCleanup.remove(findAnimation.mViewHolder.itemView)) {
                            itemTouchHelper2 = ItemTouchHelper.this;
                            itemTouchHelper2.mCallback.clearView(itemTouchHelper2.mRecyclerView, findAnimation.mViewHolder);
                        }
                        ItemTouchHelper.this.select(findAnimation.mViewHolder, findAnimation.mActionState);
                        itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.updateDxDy(motionEvent, itemTouchHelper.mSelectedFlags, 0);
                    }
                }
            } else if (actionMasked == 3 || actionMasked == 1) {
                itemTouchHelper = ItemTouchHelper.this;
                itemTouchHelper.mActivePointerId = -1;
                itemTouchHelper.select(null, 0);
            } else {
                int i = ItemTouchHelper.this.mActivePointerId;
                if (i != -1) {
                    i = motionEvent.findPointerIndex(i);
                    if (i >= 0) {
                        ItemTouchHelper.this.checkSelectForSwipe(actionMasked, motionEvent, i);
                    }
                }
            }
            VelocityTracker velocityTracker = ItemTouchHelper.this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.addMovement(motionEvent);
            }
            if (ItemTouchHelper.this.mSelected != null) {
                return true;
            }
            return false;
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            if (z) {
                ItemTouchHelper.this.select(null, 0);
            }
        }

        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(motionEvent);
            VelocityTracker velocityTracker = ItemTouchHelper.this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.addMovement(motionEvent);
            }
            if (ItemTouchHelper.this.mActivePointerId != -1) {
                int actionMasked = motionEvent.getActionMasked();
                int findPointerIndex = motionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
                if (findPointerIndex >= 0) {
                    ItemTouchHelper.this.checkSelectForSwipe(actionMasked, motionEvent, findPointerIndex);
                }
                ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                ViewHolder viewHolder = itemTouchHelper.mSelected;
                if (viewHolder != null) {
                    int i = 0;
                    if (actionMasked != 1) {
                        if (actionMasked != 2) {
                            if (actionMasked == 3) {
                                velocityTracker = itemTouchHelper.mVelocityTracker;
                                if (velocityTracker != null) {
                                    velocityTracker.clear();
                                }
                            } else if (actionMasked == 6) {
                                actionMasked = motionEvent.getActionIndex();
                                int pointerId = motionEvent.getPointerId(actionMasked);
                                ItemTouchHelper itemTouchHelper2 = ItemTouchHelper.this;
                                if (pointerId == itemTouchHelper2.mActivePointerId) {
                                    if (actionMasked == 0) {
                                        i = 1;
                                    }
                                    itemTouchHelper2.mActivePointerId = motionEvent.getPointerId(i);
                                    ItemTouchHelper itemTouchHelper3 = ItemTouchHelper.this;
                                    itemTouchHelper3.updateDxDy(motionEvent, itemTouchHelper3.mSelectedFlags, actionMasked);
                                }
                            }
                        } else if (findPointerIndex >= 0) {
                            itemTouchHelper.updateDxDy(motionEvent, itemTouchHelper.mSelectedFlags, findPointerIndex);
                            ItemTouchHelper.this.moveIfNecessary(viewHolder);
                            ItemTouchHelper itemTouchHelper4 = ItemTouchHelper.this;
                            itemTouchHelper4.mRecyclerView.removeCallbacks(itemTouchHelper4.mScrollRunnable);
                            ItemTouchHelper.this.mScrollRunnable.run();
                            ItemTouchHelper.this.mRecyclerView.invalidate();
                        }
                    }
                    ItemTouchHelper.this.select(null, 0);
                    ItemTouchHelper.this.mActivePointerId = -1;
                }
            }
        }
    };
    public View mOverdrawChild = null;
    public int mOverdrawChildPosition = -1;
    public final List<View> mPendingCleanup = new ArrayList();
    public List<RecoverAnimation> mRecoverAnimations = new ArrayList();
    public RecyclerView mRecyclerView;
    public final Runnable mScrollRunnable = new Runnable() {
        public void run() {
            ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
            if (itemTouchHelper.mSelected != null && itemTouchHelper.scrollIfNecessary()) {
                itemTouchHelper = ItemTouchHelper.this;
                ViewHolder viewHolder = itemTouchHelper.mSelected;
                if (viewHolder != null) {
                    itemTouchHelper.moveIfNecessary(viewHolder);
                }
                itemTouchHelper = ItemTouchHelper.this;
                itemTouchHelper.mRecyclerView.removeCallbacks(itemTouchHelper.mScrollRunnable);
                ViewCompat.postOnAnimation(ItemTouchHelper.this.mRecyclerView, this);
            }
        }
    };
    public ViewHolder mSelected = null;
    public int mSelectedFlags;
    private float mSelectedStartX;
    private float mSelectedStartY;
    private int mSlop;
    private List<ViewHolder> mSwapTargets;
    private float mSwipeEscapeVelocity;
    private final float[] mTmpPosition = new float[2];
    private Rect mTmpRect;
    public VelocityTracker mVelocityTracker;

    public static abstract class Callback {
        private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
        public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
        public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
        private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;
        public static final int RELATIVE_DIR_FLAGS = 3158064;
        private static final Interpolator sDragScrollInterpolator = new Interpolator() {
            public float getInterpolation(float f) {
                return (((f * f) * f) * f) * f;
            }
        };
        private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
            public float getInterpolation(float f) {
                f -= 1.0f;
                return ((((f * f) * f) * f) * f) + 1.0f;
            }
        };
        private int mCachedMaxScrollSpeed = -1;

        public static int convertToRelativeDirection(int i, int i2) {
            int i3 = i & ABS_HORIZONTAL_DIR_FLAGS;
            if (i3 == 0) {
                return i;
            }
            i &= ~i3;
            if (i2 == 0) {
                i2 = i3 << 2;
            } else {
                i2 = i3 << 1;
                i |= -789517 & i2;
                i2 = (i2 & ABS_HORIZONTAL_DIR_FLAGS) << 2;
            }
            return i | i2;
        }

        @NonNull
        public static ItemTouchUIUtil getDefaultUIUtil() {
            return ItemTouchUIUtilImpl.INSTANCE;
        }

        private int getMaxDragScroll(RecyclerView recyclerView) {
            if (this.mCachedMaxScrollSpeed == -1) {
                this.mCachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(R.dimen.item_touch_helper_max_drag_scroll_per_frame);
            }
            return this.mCachedMaxScrollSpeed;
        }

        public static int makeFlag(int i, int i2) {
            return i2 << (i * 8);
        }

        public static int makeMovementFlags(int i, int i2) {
            return makeFlag(2, i) | (makeFlag(1, i2) | makeFlag(0, i2 | i));
        }

        public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder2) {
            return true;
        }

        public ViewHolder chooseDropTarget(@NonNull ViewHolder viewHolder, @NonNull List<ViewHolder> list, int i, int i2) {
            ViewHolder viewHolder2 = viewHolder;
            int width = viewHolder2.itemView.getWidth() + i;
            int height = viewHolder2.itemView.getHeight() + i2;
            int left = i - viewHolder2.itemView.getLeft();
            int top = i2 - viewHolder2.itemView.getTop();
            int size = list.size();
            ViewHolder viewHolder3 = null;
            int i3 = -1;
            for (int i4 = 0; i4 < size; i4++) {
                int right;
                ViewHolder viewHolder4 = (ViewHolder) list.get(i4);
                if (left > 0) {
                    right = viewHolder4.itemView.getRight() - width;
                    if (right < 0 && viewHolder4.itemView.getRight() > viewHolder2.itemView.getRight()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
                if (left < 0) {
                    right = viewHolder4.itemView.getLeft() - i;
                    if (right > 0 && viewHolder4.itemView.getLeft() < viewHolder2.itemView.getLeft()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
                if (top < 0) {
                    right = viewHolder4.itemView.getTop() - i2;
                    if (right > 0 && viewHolder4.itemView.getTop() < viewHolder2.itemView.getTop()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
                if (top > 0) {
                    right = viewHolder4.itemView.getBottom() - height;
                    if (right < 0 && viewHolder4.itemView.getBottom() > viewHolder2.itemView.getBottom()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
            }
            return viewHolder3;
        }

        public void clearView(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder) {
            ItemTouchUIUtilImpl.INSTANCE.clearView(viewHolder.itemView);
        }

        public int convertToAbsoluteDirection(int i, int i2) {
            int i3 = i & RELATIVE_DIR_FLAGS;
            if (i3 == 0) {
                return i;
            }
            i &= ~i3;
            if (i2 == 0) {
                i2 = i3 >> 2;
            } else {
                i2 = i3 >> 1;
                i |= -3158065 & i2;
                i2 = (i2 & RELATIVE_DIR_FLAGS) >> 2;
            }
            return i | i2;
        }

        public final int getAbsoluteMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return convertToAbsoluteDirection(getMovementFlags(recyclerView, viewHolder), ViewCompat.getLayoutDirection(recyclerView));
        }

        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int i, float f, float f2) {
            ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator == null) {
                return i == 8 ? 200 : 250;
            }
            long moveDuration;
            if (i == 8) {
                moveDuration = itemAnimator.getMoveDuration();
            } else {
                moveDuration = itemAnimator.getRemoveDuration();
            }
            return moveDuration;
        }

        public int getBoundingBoxMargin() {
            return 0;
        }

        public float getMoveThreshold(@NonNull ViewHolder viewHolder) {
            return 0.5f;
        }

        public abstract int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder);

        public float getSwipeEscapeVelocity(float f) {
            return f;
        }

        public float getSwipeThreshold(@NonNull ViewHolder viewHolder) {
            return 0.5f;
        }

        public float getSwipeVelocityThreshold(float f) {
            return f;
        }

        public boolean hasDragFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & ItemTouchHelper.ACTION_MODE_DRAG_MASK) != 0;
        }

        public boolean hasSwipeFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & 65280) != 0;
        }

        public int interpolateOutOfBoundsScroll(@NonNull RecyclerView recyclerView, int i, int i2, int i3, long j) {
            int signum = (int) Math.signum((float) i2);
            float f = 1.0f;
            int interpolation = (int) (sDragViewScrollCapInterpolator.getInterpolation(Math.min(1.0f, (((float) Math.abs(i2)) * 1.0f) / ((float) i))) * ((float) (signum * getMaxDragScroll(recyclerView))));
            if (j <= DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
                f = ((float) j) / 2000.0f;
            }
            interpolation = (int) (sDragScrollInterpolator.getInterpolation(f) * ((float) interpolation));
            if (interpolation == 0) {
                return i2 > 0 ? 1 : -1;
            } else {
                return interpolation;
            }
        }

        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            ItemTouchUIUtilImpl.INSTANCE.onDraw(canvas, recyclerView, viewHolder.itemView, f, f2, i, z);
        }

        public void onChildDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            ItemTouchUIUtilImpl.INSTANCE.onDrawOver(canvas, recyclerView, viewHolder.itemView, f, f2, i, z);
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, List<RecoverAnimation> list, int i, float f, float f2) {
            Canvas canvas2 = canvas;
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                RecoverAnimation recoverAnimation = (RecoverAnimation) list.get(i2);
                recoverAnimation.update();
                int save = canvas.save();
                onChildDraw(canvas, recyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
                canvas.restoreToCount(save);
            }
            if (viewHolder != null) {
                size = canvas.save();
                onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, true);
                canvas.restoreToCount(size);
            }
        }

        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, List<RecoverAnimation> list, int i, float f, float f2) {
            int i2;
            Canvas canvas2 = canvas;
            List<RecoverAnimation> list2 = list;
            int size = list.size();
            Object obj = null;
            for (i2 = 0; i2 < size; i2++) {
                RecoverAnimation recoverAnimation = (RecoverAnimation) list2.get(i2);
                int save = canvas.save();
                onChildDrawOver(canvas, recyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
                canvas.restoreToCount(save);
            }
            if (viewHolder != null) {
                i2 = canvas.save();
                onChildDrawOver(canvas, recyclerView, viewHolder, f, f2, i, true);
                canvas.restoreToCount(i2);
            }
            for (size--; size >= 0; size--) {
                RecoverAnimation recoverAnimation2 = (RecoverAnimation) list2.get(size);
                boolean z = recoverAnimation2.mEnded;
                if (z && !recoverAnimation2.mIsPendingCleanup) {
                    list2.remove(size);
                } else if (!z) {
                    obj = 1;
                }
            }
            if (obj != null) {
                recyclerView.invalidate();
            }
        }

        public abstract boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder2);

        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, int i, @NonNull ViewHolder viewHolder2, int i2, int i3, int i4) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof ViewDropHandler) {
                ((ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView, viewHolder2.itemView, i3, i4);
                return;
            }
            if (layoutManager.canScrollHorizontally()) {
                if (layoutManager.getDecoratedLeft(viewHolder2.itemView) <= recyclerView.getPaddingLeft()) {
                    recyclerView.scrollToPosition(i2);
                }
                if (layoutManager.getDecoratedRight(viewHolder2.itemView) >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                    recyclerView.scrollToPosition(i2);
                }
            }
            if (layoutManager.canScrollVertically()) {
                if (layoutManager.getDecoratedTop(viewHolder2.itemView) <= recyclerView.getPaddingTop()) {
                    recyclerView.scrollToPosition(i2);
                }
                if (layoutManager.getDecoratedBottom(viewHolder2.itemView) >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                    recyclerView.scrollToPosition(i2);
                }
            }
        }

        public void onSelectedChanged(@Nullable ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                ItemTouchUIUtilImpl.INSTANCE.onSelected(viewHolder.itemView);
            }
        }

        public abstract void onSwiped(@NonNull ViewHolder viewHolder, int i);
    }

    public class ItemTouchHelperGestureListener extends SimpleOnGestureListener {
        private boolean mShouldReactToLongPress = true;

        public void doNotReactToLongPress() {
            this.mShouldReactToLongPress = false;
        }

        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        public void onLongPress(MotionEvent motionEvent) {
            if (this.mShouldReactToLongPress) {
                View findChildView = ItemTouchHelper.this.findChildView(motionEvent);
                if (findChildView != null) {
                    ViewHolder childViewHolder = ItemTouchHelper.this.mRecyclerView.getChildViewHolder(findChildView);
                    if (childViewHolder != null) {
                        ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                        if (itemTouchHelper.mCallback.hasDragFlag(itemTouchHelper.mRecyclerView, childViewHolder)) {
                            int pointerId = motionEvent.getPointerId(0);
                            int i = ItemTouchHelper.this.mActivePointerId;
                            if (pointerId == i) {
                                pointerId = motionEvent.findPointerIndex(i);
                                float x = motionEvent.getX(pointerId);
                                float y = motionEvent.getY(pointerId);
                                itemTouchHelper = ItemTouchHelper.this;
                                itemTouchHelper.mInitialTouchX = x;
                                itemTouchHelper.mInitialTouchY = y;
                                itemTouchHelper.mDy = 0.0f;
                                itemTouchHelper.mDx = 0.0f;
                                if (itemTouchHelper.mCallback.isLongPressDragEnabled()) {
                                    ItemTouchHelper.this.select(childViewHolder, 2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class RecoverAnimation implements AnimatorListener {
        public final int mActionState;
        public final int mAnimationType;
        public boolean mEnded = false;
        private float mFraction;
        public boolean mIsPendingCleanup;
        public boolean mOverridden = false;
        public final float mStartDx;
        public final float mStartDy;
        public final float mTargetX;
        public final float mTargetY;
        private final ValueAnimator mValueAnimator;
        public final ViewHolder mViewHolder;
        public float mX;
        public float mY;

        public RecoverAnimation(ViewHolder viewHolder, int i, int i2, float f, float f2, float f3, float f4) {
            this.mActionState = i2;
            this.mAnimationType = i;
            this.mViewHolder = viewHolder;
            this.mStartDx = f;
            this.mStartDy = f2;
            this.mTargetX = f3;
            this.mTargetY = f4;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.mValueAnimator = ofFloat;
            ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    RecoverAnimation.this.setFraction(valueAnimator.getAnimatedFraction());
                }
            });
            ofFloat.setTarget(viewHolder.itemView);
            ofFloat.addListener(this);
            setFraction(0.0f);
        }

        public void cancel() {
            this.mValueAnimator.cancel();
        }

        public void onAnimationCancel(Animator animator) {
            setFraction(1.0f);
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mEnded) {
                this.mViewHolder.setIsRecyclable(true);
            }
            this.mEnded = true;
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }

        public void setDuration(long j) {
            this.mValueAnimator.setDuration(j);
        }

        public void setFraction(float f) {
            this.mFraction = f;
        }

        public void start() {
            this.mViewHolder.setIsRecyclable(false);
            this.mValueAnimator.start();
        }

        public void update() {
            float f = this.mStartDx;
            float f2 = this.mTargetX;
            if (f == f2) {
                this.mX = this.mViewHolder.itemView.getTranslationX();
            } else {
                this.mX = a.a(f2, f, this.mFraction, f);
            }
            f = this.mStartDy;
            f2 = this.mTargetY;
            if (f == f2) {
                this.mY = this.mViewHolder.itemView.getTranslationY();
            } else {
                this.mY = a.a(f2, f, this.mFraction, f);
            }
        }
    }

    public interface ViewDropHandler {
        void prepareForDrop(@NonNull View view, @NonNull View view2, int i, int i2);
    }

    public static abstract class SimpleCallback extends Callback {
        private int mDefaultDragDirs;
        private int mDefaultSwipeDirs;

        public SimpleCallback(int i, int i2) {
            this.mDefaultSwipeDirs = i2;
            this.mDefaultDragDirs = i;
        }

        public int getDragDirs(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder) {
            return this.mDefaultDragDirs;
        }

        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder) {
            return Callback.makeMovementFlags(getDragDirs(recyclerView, viewHolder), getSwipeDirs(recyclerView, viewHolder));
        }

        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder) {
            return this.mDefaultSwipeDirs;
        }

        public void setDefaultDragDirs(int i) {
            this.mDefaultDragDirs = i;
        }

        public void setDefaultSwipeDirs(int i) {
            this.mDefaultSwipeDirs = i;
        }
    }

    public ItemTouchHelper(@NonNull Callback callback) {
        this.mCallback = callback;
    }

    private void addChildDrawingOrderCallback() {
    }

    private int checkHorizontalSwipe(ViewHolder viewHolder, int i) {
        if ((i & 12) != 0) {
            int i2 = 8;
            int i3 = this.mDx > 0.0f ? 8 : 4;
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null && this.mActivePointerId > -1) {
                velocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (xVelocity <= 0.0f) {
                    i2 = 4;
                }
                float abs = Math.abs(xVelocity);
                if ((i2 & i) != 0 && i3 == i2 && abs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && abs > Math.abs(yVelocity)) {
                    return i2;
                }
            }
            float swipeThreshold = this.mCallback.getSwipeThreshold(viewHolder) * ((float) this.mRecyclerView.getWidth());
            if ((i & i3) != 0 && Math.abs(this.mDx) > swipeThreshold) {
                return i3;
            }
        }
        return 0;
    }

    private int checkVerticalSwipe(ViewHolder viewHolder, int i) {
        if ((i & 3) != 0) {
            int i2 = 2;
            int i3 = this.mDy > 0.0f ? 2 : 1;
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null && this.mActivePointerId > -1) {
                velocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (yVelocity <= 0.0f) {
                    i2 = 1;
                }
                float abs = Math.abs(yVelocity);
                if ((i2 & i) != 0 && i2 == i3 && abs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && abs > Math.abs(xVelocity)) {
                    return i2;
                }
            }
            float swipeThreshold = this.mCallback.getSwipeThreshold(viewHolder) * ((float) this.mRecyclerView.getHeight());
            if ((i & i3) != 0 && Math.abs(this.mDy) > swipeThreshold) {
                return i3;
            }
        }
        return 0;
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            this.mCallback.clearView(this.mRecyclerView, ((RecoverAnimation) this.mRecoverAnimations.get(0)).mViewHolder);
        }
        this.mRecoverAnimations.clear();
        this.mOverdrawChild = null;
        this.mOverdrawChildPosition = -1;
        releaseVelocityTracker();
        stopGestureDetection();
    }

    private List<ViewHolder> findSwapTargets(ViewHolder viewHolder) {
        ViewHolder viewHolder2 = viewHolder;
        List list = this.mSwapTargets;
        if (list == null) {
            this.mSwapTargets = new ArrayList();
            this.mDistances = new ArrayList();
        } else {
            list.clear();
            this.mDistances.clear();
        }
        int boundingBoxMargin = this.mCallback.getBoundingBoxMargin();
        int round = Math.round(this.mSelectedStartX + this.mDx) - boundingBoxMargin;
        int round2 = Math.round(this.mSelectedStartY + this.mDy) - boundingBoxMargin;
        boundingBoxMargin *= 2;
        int width = (viewHolder2.itemView.getWidth() + round) + boundingBoxMargin;
        int height = (viewHolder2.itemView.getHeight() + round2) + boundingBoxMargin;
        boundingBoxMargin = (round + width) / 2;
        int i = (round2 + height) / 2;
        LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        int childCount = layoutManager.getChildCount();
        int i2 = 0;
        while (i2 < childCount) {
            View childAt = layoutManager.getChildAt(i2);
            if (childAt != viewHolder2.itemView && childAt.getBottom() >= round2 && childAt.getTop() <= height && childAt.getRight() >= round && childAt.getLeft() <= width) {
                ViewHolder childViewHolder = this.mRecyclerView.getChildViewHolder(childAt);
                if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, childViewHolder)) {
                    int abs = Math.abs(boundingBoxMargin - ((childAt.getRight() + childAt.getLeft()) / 2));
                    int abs2 = Math.abs(i - ((childAt.getBottom() + childAt.getTop()) / 2));
                    abs2 = (abs2 * abs2) + (abs * abs);
                    abs = this.mSwapTargets.size();
                    int i3 = 0;
                    int i4 = 0;
                    while (i3 < abs && abs2 > ((Integer) this.mDistances.get(i3)).intValue()) {
                        i4++;
                        i3++;
                        viewHolder2 = viewHolder;
                    }
                    this.mSwapTargets.add(i4, childViewHolder);
                    this.mDistances.add(i4, Integer.valueOf(abs2));
                }
            }
            i2++;
            viewHolder2 = viewHolder;
        }
        return this.mSwapTargets;
    }

    private ViewHolder findSwipedView(MotionEvent motionEvent) {
        LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        int i = this.mActivePointerId;
        if (i == -1) {
            return null;
        }
        i = motionEvent.findPointerIndex(i);
        float x = motionEvent.getX(i) - this.mInitialTouchX;
        float y = motionEvent.getY(i) - this.mInitialTouchY;
        x = Math.abs(x);
        y = Math.abs(y);
        int i2 = this.mSlop;
        if (x < ((float) i2) && y < ((float) i2)) {
            return null;
        }
        if (x > y && layoutManager.canScrollHorizontally()) {
            return null;
        }
        if (y > x && layoutManager.canScrollVertically()) {
            return null;
        }
        View findChildView = findChildView(motionEvent);
        if (findChildView == null) {
            return null;
        }
        return this.mRecyclerView.getChildViewHolder(findChildView);
    }

    private void getSelectedDxDy(float[] fArr) {
        if ((this.mSelectedFlags & 12) != 0) {
            fArr[0] = (this.mSelectedStartX + this.mDx) - ((float) this.mSelected.itemView.getLeft());
        } else {
            fArr[0] = this.mSelected.itemView.getTranslationX();
        }
        if ((this.mSelectedFlags & 3) != 0) {
            fArr[1] = (this.mSelectedStartY + this.mDy) - ((float) this.mSelected.itemView.getTop());
        } else {
            fArr[1] = this.mSelected.itemView.getTranslationY();
        }
    }

    private static boolean hitTest(View view, float f, float f2, float f3, float f4) {
        return f >= f3 && f <= f3 + ((float) view.getWidth()) && f2 >= f4 && f2 <= f4 + ((float) view.getHeight());
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void setupCallbacks() {
        this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
        startGestureDetection();
    }

    private void startGestureDetection() {
        this.mItemTouchHelperGestureListener = new ItemTouchHelperGestureListener();
        this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), this.mItemTouchHelperGestureListener);
    }

    private void stopGestureDetection() {
        ItemTouchHelperGestureListener itemTouchHelperGestureListener = this.mItemTouchHelperGestureListener;
        if (itemTouchHelperGestureListener != null) {
            itemTouchHelperGestureListener.doNotReactToLongPress();
            this.mItemTouchHelperGestureListener = null;
        }
        if (this.mGestureDetector != null) {
            this.mGestureDetector = null;
        }
    }

    private int swipeIfNecessary(ViewHolder viewHolder) {
        if (this.mActionState == 2) {
            return 0;
        }
        int movementFlags = this.mCallback.getMovementFlags(this.mRecyclerView, viewHolder);
        int convertToAbsoluteDirection = (this.mCallback.convertToAbsoluteDirection(movementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView)) & 65280) >> 8;
        if (convertToAbsoluteDirection == 0) {
            return 0;
        }
        movementFlags = (movementFlags & 65280) >> 8;
        int checkHorizontalSwipe;
        int checkVerticalSwipe;
        if (Math.abs(this.mDx) > Math.abs(this.mDy)) {
            checkHorizontalSwipe = checkHorizontalSwipe(viewHolder, convertToAbsoluteDirection);
            if (checkHorizontalSwipe > 0) {
                return (movementFlags & checkHorizontalSwipe) == 0 ? Callback.convertToRelativeDirection(checkHorizontalSwipe, ViewCompat.getLayoutDirection(this.mRecyclerView)) : checkHorizontalSwipe;
            } else {
                checkVerticalSwipe = checkVerticalSwipe(viewHolder, convertToAbsoluteDirection);
                if (checkVerticalSwipe > 0) {
                    return checkVerticalSwipe;
                }
            }
        }
        checkHorizontalSwipe = checkVerticalSwipe(viewHolder, convertToAbsoluteDirection);
        if (checkHorizontalSwipe > 0) {
            return checkHorizontalSwipe;
        }
        checkVerticalSwipe = checkHorizontalSwipe(viewHolder, convertToAbsoluteDirection);
        if (checkVerticalSwipe > 0) {
            if ((movementFlags & checkVerticalSwipe) == 0) {
                checkVerticalSwipe = Callback.convertToRelativeDirection(checkVerticalSwipe, ViewCompat.getLayoutDirection(this.mRecyclerView));
            }
            return checkVerticalSwipe;
        }
        return 0;
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 != recyclerView) {
            if (recyclerView2 != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (recyclerView != null) {
                Resources resources = recyclerView.getResources();
                this.mSwipeEscapeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_velocity);
                this.mMaxSwipeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_max_velocity);
                setupCallbacks();
            }
        }
    }

    public void checkSelectForSwipe(int i, MotionEvent motionEvent, int i2) {
        if (this.mSelected == null && i == 2 && this.mActionState != 2 && this.mCallback.isItemViewSwipeEnabled() && this.mRecyclerView.getScrollState() != 1) {
            ViewHolder findSwipedView = findSwipedView(motionEvent);
            if (findSwipedView != null) {
                int absoluteMovementFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, findSwipedView) & 65280) >> 8;
                if (absoluteMovementFlags != 0) {
                    float x = motionEvent.getX(i2);
                    x -= this.mInitialTouchX;
                    float y = motionEvent.getY(i2) - this.mInitialTouchY;
                    float abs = Math.abs(x);
                    float abs2 = Math.abs(y);
                    int i3 = this.mSlop;
                    if (abs >= ((float) i3) || abs2 >= ((float) i3)) {
                        if (abs > abs2) {
                            if (x < 0.0f && (absoluteMovementFlags & 4) == 0) {
                                return;
                            }
                            if (x > 0.0f && (absoluteMovementFlags & 8) == 0) {
                                return;
                            }
                        } else if (y < 0.0f && (absoluteMovementFlags & 1) == 0) {
                            return;
                        } else {
                            if (y > 0.0f && (absoluteMovementFlags & 2) == 0) {
                                return;
                            }
                        }
                        this.mDy = 0.0f;
                        this.mDx = 0.0f;
                        this.mActivePointerId = motionEvent.getPointerId(0);
                        select(findSwipedView, 1);
                    }
                }
            }
        }
    }

    public void endRecoverAnimation(ViewHolder viewHolder, boolean z) {
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder == viewHolder) {
                recoverAnimation.mOverridden |= z;
                if (!recoverAnimation.mEnded) {
                    recoverAnimation.cancel();
                }
                this.mRecoverAnimations.remove(size);
                return;
            }
        }
    }

    public RecoverAnimation findAnimation(MotionEvent motionEvent) {
        if (this.mRecoverAnimations.isEmpty()) {
            return null;
        }
        View findChildView = findChildView(motionEvent);
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder.itemView == findChildView) {
                return recoverAnimation;
            }
        }
        return null;
    }

    public View findChildView(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        ViewHolder viewHolder = this.mSelected;
        if (viewHolder != null) {
            View view = viewHolder.itemView;
            if (hitTest(view, x, y, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
                return view;
            }
        }
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            View view2 = recoverAnimation.mViewHolder.itemView;
            if (hitTest(view2, x, y, recoverAnimation.mX, recoverAnimation.mY)) {
                return view2;
            }
        }
        return this.mRecyclerView.findChildViewUnder(x, y);
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        rect.setEmpty();
    }

    public boolean hasRunningRecoverAnim() {
        int size = this.mRecoverAnimations.size();
        for (int i = 0; i < size; i++) {
            if (!((RecoverAnimation) this.mRecoverAnimations.get(i)).mEnded) {
                return true;
            }
        }
        return false;
    }

    public void moveIfNecessary(ViewHolder viewHolder) {
        if (!this.mRecyclerView.isLayoutRequested() && this.mActionState == 2) {
            float moveThreshold = this.mCallback.getMoveThreshold(viewHolder);
            int i = (int) (this.mSelectedStartX + this.mDx);
            int i2 = (int) (this.mSelectedStartY + this.mDy);
            if (((float) Math.abs(i2 - viewHolder.itemView.getTop())) >= ((float) viewHolder.itemView.getHeight()) * moveThreshold || ((float) Math.abs(i - viewHolder.itemView.getLeft())) >= ((float) viewHolder.itemView.getWidth()) * moveThreshold) {
                List findSwapTargets = findSwapTargets(viewHolder);
                if (findSwapTargets.size() != 0) {
                    ViewHolder chooseDropTarget = this.mCallback.chooseDropTarget(viewHolder, findSwapTargets, i, i2);
                    if (chooseDropTarget == null) {
                        this.mSwapTargets.clear();
                        this.mDistances.clear();
                        return;
                    }
                    int adapterPosition = chooseDropTarget.getAdapterPosition();
                    int adapterPosition2 = viewHolder.getAdapterPosition();
                    if (this.mCallback.onMove(this.mRecyclerView, viewHolder, chooseDropTarget)) {
                        this.mCallback.onMoved(this.mRecyclerView, viewHolder, adapterPosition2, chooseDropTarget, adapterPosition, i, i2);
                    }
                }
            }
        }
    }

    public void obtainVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    public void onChildViewAttachedToWindow(@NonNull View view) {
    }

    public void onChildViewDetachedFromWindow(@NonNull View view) {
        removeChildDrawingOrderCallbackIfNecessary(view);
        ViewHolder childViewHolder = this.mRecyclerView.getChildViewHolder(view);
        if (childViewHolder != null) {
            ViewHolder viewHolder = this.mSelected;
            if (viewHolder == null || childViewHolder != viewHolder) {
                endRecoverAnimation(childViewHolder, false);
                if (this.mPendingCleanup.remove(childViewHolder.itemView)) {
                    this.mCallback.clearView(this.mRecyclerView, childViewHolder);
                }
            } else {
                select(null, 0);
            }
        }
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
        float f;
        float f2;
        this.mOverdrawChildPosition = -1;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float[] fArr = this.mTmpPosition;
            float f3 = fArr[0];
            f = fArr[1];
            f2 = f3;
        } else {
            f2 = 0.0f;
            f = 0.0f;
        }
        this.mCallback.onDraw(canvas, recyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f2, f);
    }

    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
        float f;
        float f2;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float[] fArr = this.mTmpPosition;
            float f3 = fArr[0];
            f = fArr[1];
            f2 = f3;
        } else {
            f2 = 0.0f;
            f = 0.0f;
        }
        this.mCallback.onDrawOver(canvas, recyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f2, f);
    }

    public void postDispatchSwipe(final RecoverAnimation recoverAnimation, final int i) {
        this.mRecyclerView.post(new Runnable() {
            public void run() {
                RecyclerView recyclerView = ItemTouchHelper.this.mRecyclerView;
                if (recyclerView != null && recyclerView.isAttachedToWindow()) {
                    RecoverAnimation recoverAnimation = recoverAnimation;
                    if (!recoverAnimation.mOverridden && recoverAnimation.mViewHolder.getAdapterPosition() != -1) {
                        ItemAnimator itemAnimator = ItemTouchHelper.this.mRecyclerView.getItemAnimator();
                        if ((itemAnimator == null || !itemAnimator.isRunning(null)) && !ItemTouchHelper.this.hasRunningRecoverAnim()) {
                            ItemTouchHelper.this.mCallback.onSwiped(recoverAnimation.mViewHolder, i);
                        } else {
                            ItemTouchHelper.this.mRecyclerView.post(this);
                        }
                    }
                }
            }
        });
    }

    public void removeChildDrawingOrderCallbackIfNecessary(View view) {
        if (view == this.mOverdrawChild) {
            this.mOverdrawChild = null;
            if (this.mChildDrawingOrderCallback != null) {
                this.mRecyclerView.setChildDrawingOrderCallback(null);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00fc  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00fc  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ff A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x010b  */
    /* JADX WARNING: Missing block: B:20:0x0079, code skipped:
            if (r11 > 0) goto L_0x0057;
     */
    /* JADX WARNING: Missing block: B:31:0x00c0, code skipped:
            if (r9 > 0) goto L_0x009e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean scrollIfNecessary() {
        if (this.mSelected == null) {
            this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
            return false;
        }
        int i;
        int i2;
        int i3;
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mDragScrollStartTimeInMs;
        j = j == Long.MIN_VALUE ? 0 : currentTimeMillis - j;
        LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        if (this.mTmpRect == null) {
            this.mTmpRect = new Rect();
        }
        layoutManager.calculateItemDecorationsForChild(this.mSelected.itemView, this.mTmpRect);
        if (layoutManager.canScrollHorizontally()) {
            int i4 = (int) (this.mSelectedStartX + this.mDx);
            int paddingLeft = (i4 - this.mTmpRect.left) - this.mRecyclerView.getPaddingLeft();
            float f = this.mDx;
            if (f >= 0.0f || paddingLeft >= 0) {
                if (f > 0.0f) {
                    paddingLeft = ((this.mSelected.itemView.getWidth() + i4) + this.mTmpRect.right) - (this.mRecyclerView.getWidth() - this.mRecyclerView.getPaddingRight());
                }
            }
            i = paddingLeft;
            if (layoutManager.canScrollVertically()) {
                i2 = (int) (this.mSelectedStartY + this.mDy);
                i4 = (i2 - this.mTmpRect.top) - this.mRecyclerView.getPaddingTop();
                float f2 = this.mDy;
                if (f2 >= 0.0f || i4 >= 0) {
                    if (f2 > 0.0f) {
                        i4 = ((this.mSelected.itemView.getHeight() + i2) + this.mTmpRect.bottom) - (this.mRecyclerView.getHeight() - this.mRecyclerView.getPaddingBottom());
                    }
                }
                i2 = i4;
                if (i != 0) {
                    i = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getWidth(), i, this.mRecyclerView.getWidth(), j);
                }
                i3 = i;
                if (i2 != 0) {
                    i = i2;
                    i2 = i3;
                    int interpolateOutOfBoundsScroll = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getHeight(), i, this.mRecyclerView.getHeight(), j);
                    i = i2;
                    i2 = interpolateOutOfBoundsScroll;
                } else {
                    i = i3;
                }
                if (i == 0 || i2 != 0) {
                    if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                        this.mDragScrollStartTimeInMs = currentTimeMillis;
                    }
                    this.mRecyclerView.scrollBy(i, i2);
                    return true;
                }
                this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                return false;
            }
            i2 = 0;
            if (i != 0) {
            }
            i3 = i;
            if (i2 != 0) {
            }
            if (i == 0) {
            }
            if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
            }
            this.mRecyclerView.scrollBy(i, i2);
            return true;
        }
        i = 0;
        if (layoutManager.canScrollVertically()) {
        }
        i2 = 0;
        if (i != 0) {
        }
        i3 = i;
        if (i2 != 0) {
        }
        if (i == 0) {
        }
        if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
        }
        this.mRecyclerView.scrollBy(i, i2);
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0137  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void select(@Nullable ViewHolder viewHolder, int i) {
        ViewHolder viewHolder2 = viewHolder;
        int i2 = i;
        if (viewHolder2 != this.mSelected || i2 != this.mActionState) {
            Object obj;
            ViewParent parent;
            this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
            int i3 = this.mActionState;
            endRecoverAnimation(viewHolder2, true);
            this.mActionState = i2;
            if (i2 == 2) {
                if (viewHolder2 != null) {
                    this.mOverdrawChild = viewHolder2.itemView;
                    addChildDrawingOrderCallback();
                } else {
                    throw new IllegalArgumentException("Must pass a ViewHolder when dragging");
                }
            }
            int i4 = (1 << ((i2 * 8) + 8)) - 1;
            ViewHolder viewHolder3 = this.mSelected;
            if (viewHolder3 != null) {
                if (viewHolder3.itemView.getParent() != null) {
                    int i5;
                    float signum;
                    float f;
                    if (i3 == 2) {
                        i5 = 0;
                    } else {
                        i5 = swipeIfNecessary(viewHolder3);
                    }
                    releaseVelocityTracker();
                    if (i5 == 1 || i5 == 2) {
                        signum = Math.signum(this.mDy) * ((float) this.mRecyclerView.getHeight());
                        f = 0.0f;
                    } else {
                        if (i5 == 4 || i5 == 8 || i5 == 16 || i5 == 32) {
                            f = Math.signum(this.mDx) * ((float) this.mRecyclerView.getWidth());
                        } else {
                            f = 0.0f;
                        }
                        signum = 0.0f;
                    }
                    int i6 = i3 == 2 ? 8 : i5 > 0 ? 2 : 4;
                    getSelectedDxDy(this.mTmpPosition);
                    float[] fArr = this.mTmpPosition;
                    float f2 = fArr[0];
                    float f3 = fArr[1];
                    RecoverAnimation recoverAnimation = r0;
                    int i7 = i6;
                    ViewHolder viewHolder4 = viewHolder3;
                    final int i8 = i5;
                    final ViewHolder viewHolder5 = viewHolder4;
                    RecoverAnimation anonymousClass3 = new RecoverAnimation(this, viewHolder3, i6, i3, f2, f3, f, signum) {
                        public final /* synthetic */ ItemTouchHelper this$0;

                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            if (!this.mOverridden) {
                                ItemTouchHelper itemTouchHelper;
                                if (i8 <= 0) {
                                    itemTouchHelper = this.this$0;
                                    itemTouchHelper.mCallback.clearView(itemTouchHelper.mRecyclerView, viewHolder5);
                                } else {
                                    this.this$0.mPendingCleanup.add(viewHolder5.itemView);
                                    this.mIsPendingCleanup = true;
                                    int i = i8;
                                    if (i > 0) {
                                        this.this$0.postDispatchSwipe(this, i);
                                    }
                                }
                                itemTouchHelper = this.this$0;
                                View view = itemTouchHelper.mOverdrawChild;
                                View view2 = viewHolder5.itemView;
                                if (view == view2) {
                                    itemTouchHelper.removeChildDrawingOrderCallbackIfNecessary(view2);
                                }
                            }
                        }
                    };
                    recoverAnimation.setDuration(this.mCallback.getAnimationDuration(this.mRecyclerView, i7, f - f2, signum - f3));
                    this.mRecoverAnimations.add(recoverAnimation);
                    recoverAnimation.start();
                    obj = 1;
                } else {
                    ViewHolder viewHolder6 = viewHolder3;
                    removeChildDrawingOrderCallbackIfNecessary(viewHolder6.itemView);
                    this.mCallback.clearView(this.mRecyclerView, viewHolder6);
                    obj = null;
                }
                this.mSelected = null;
            } else {
                obj = null;
            }
            if (viewHolder2 != null) {
                this.mSelectedFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, viewHolder2) & i4) >> (this.mActionState * 8);
                this.mSelectedStartX = (float) viewHolder2.itemView.getLeft();
                this.mSelectedStartY = (float) viewHolder2.itemView.getTop();
                this.mSelected = viewHolder2;
                if (i2 == 2) {
                    viewHolder2.itemView.performHapticFeedback(0);
                    parent = this.mRecyclerView.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(this.mSelected != null);
                    }
                    if (obj == null) {
                        this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
                    }
                    this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
                    this.mRecyclerView.invalidate();
                }
            }
            parent = this.mRecyclerView.getParent();
            if (parent != null) {
            }
            if (obj == null) {
            }
            this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
            this.mRecyclerView.invalidate();
        }
    }

    public void startDrag(@NonNull ViewHolder viewHolder) {
        boolean hasDragFlag = this.mCallback.hasDragFlag(this.mRecyclerView, viewHolder);
        String str = TAG;
        if (!hasDragFlag) {
            Log.e(str, "Start drag has been called but dragging is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(str, "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, 2);
        }
    }

    public void startSwipe(@NonNull ViewHolder viewHolder) {
        boolean hasSwipeFlag = this.mCallback.hasSwipeFlag(this.mRecyclerView, viewHolder);
        String str = TAG;
        if (!hasSwipeFlag) {
            Log.e(str, "Start swipe has been called but swiping is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(str, "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, 1);
        }
    }

    public void updateDxDy(MotionEvent motionEvent, int i, int i2) {
        float x = motionEvent.getX(i2);
        float y = motionEvent.getY(i2);
        x -= this.mInitialTouchX;
        this.mDx = x;
        this.mDy = y - this.mInitialTouchY;
        if ((i & 4) == 0) {
            this.mDx = Math.max(0.0f, x);
        }
        if ((i & 8) == 0) {
            this.mDx = Math.min(0.0f, this.mDx);
        }
        if ((i & 1) == 0) {
            this.mDy = Math.max(0.0f, this.mDy);
        }
        if ((i & 2) == 0) {
            this.mDy = Math.min(0.0f, this.mDy);
        }
    }
}
