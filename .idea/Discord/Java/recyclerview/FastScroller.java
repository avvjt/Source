package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;

@VisibleForTesting
public class FastScroller extends ItemDecoration implements OnItemTouchListener {
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_FADING_OUT = 3;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_OUT = 0;
    private static final int DRAG_NONE = 0;
    private static final int DRAG_X = 1;
    private static final int DRAG_Y = 2;
    private static final int[] EMPTY_STATE_SET = new int[0];
    private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
    private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
    private static final int HIDE_DURATION_MS = 500;
    private static final int[] PRESSED_STATE_SET = new int[]{16842919};
    private static final int SCROLLBAR_FULL_OPAQUE = 255;
    private static final int SHOW_DURATION_MS = 500;
    private static final int STATE_DRAGGING = 2;
    private static final int STATE_HIDDEN = 0;
    private static final int STATE_VISIBLE = 1;
    public int mAnimationState;
    private int mDragState = 0;
    private final Runnable mHideRunnable;
    @VisibleForTesting
    public float mHorizontalDragX;
    private final int[] mHorizontalRange = new int[2];
    @VisibleForTesting
    public int mHorizontalThumbCenterX;
    private final StateListDrawable mHorizontalThumbDrawable;
    private final int mHorizontalThumbHeight;
    @VisibleForTesting
    public int mHorizontalThumbWidth;
    private final Drawable mHorizontalTrackDrawable;
    private final int mHorizontalTrackHeight;
    private final int mMargin;
    private boolean mNeedHorizontalScrollbar = false;
    private boolean mNeedVerticalScrollbar = false;
    private final OnScrollListener mOnScrollListener;
    private RecyclerView mRecyclerView;
    private int mRecyclerViewHeight = 0;
    private int mRecyclerViewWidth = 0;
    private final int mScrollbarMinimumRange;
    public final ValueAnimator mShowHideAnimator;
    private int mState = 0;
    @VisibleForTesting
    public float mVerticalDragY;
    private final int[] mVerticalRange = new int[2];
    @VisibleForTesting
    public int mVerticalThumbCenterY;
    public final StateListDrawable mVerticalThumbDrawable;
    @VisibleForTesting
    public int mVerticalThumbHeight;
    private final int mVerticalThumbWidth;
    public final Drawable mVerticalTrackDrawable;
    private final int mVerticalTrackWidth;

    public class AnimatorListener extends AnimatorListenerAdapter {
        private boolean mCanceled = false;

        public void onAnimationCancel(Animator animator) {
            this.mCanceled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.mCanceled) {
                this.mCanceled = false;
                return;
            }
            FastScroller fastScroller;
            if (((Float) FastScroller.this.mShowHideAnimator.getAnimatedValue()).floatValue() == 0.0f) {
                fastScroller = FastScroller.this;
                fastScroller.mAnimationState = 0;
                fastScroller.setState(0);
            } else {
                fastScroller = FastScroller.this;
                fastScroller.mAnimationState = 2;
                fastScroller.requestRedraw();
            }
        }
    }

    public class AnimatorUpdater implements AnimatorUpdateListener {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int floatValue = (int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f);
            FastScroller.this.mVerticalThumbDrawable.setAlpha(floatValue);
            FastScroller.this.mVerticalTrackDrawable.setAlpha(floatValue);
            FastScroller.this.requestRedraw();
        }
    }

    public FastScroller(RecyclerView recyclerView, StateListDrawable stateListDrawable, Drawable drawable, StateListDrawable stateListDrawable2, Drawable drawable2, int i, int i2, int i3) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mShowHideAnimator = ofFloat;
        this.mAnimationState = 0;
        this.mHideRunnable = new Runnable() {
            public void run() {
                FastScroller.this.hide(500);
            }
        };
        this.mOnScrollListener = new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                FastScroller.this.updateScrollPosition(recyclerView.computeHorizontalScrollOffset(), recyclerView.computeVerticalScrollOffset());
            }
        };
        this.mVerticalThumbDrawable = stateListDrawable;
        this.mVerticalTrackDrawable = drawable;
        this.mHorizontalThumbDrawable = stateListDrawable2;
        this.mHorizontalTrackDrawable = drawable2;
        this.mVerticalThumbWidth = Math.max(i, stateListDrawable.getIntrinsicWidth());
        this.mVerticalTrackWidth = Math.max(i, drawable.getIntrinsicWidth());
        this.mHorizontalThumbHeight = Math.max(i, stateListDrawable2.getIntrinsicWidth());
        this.mHorizontalTrackHeight = Math.max(i, drawable2.getIntrinsicWidth());
        this.mScrollbarMinimumRange = i2;
        this.mMargin = i3;
        stateListDrawable.setAlpha(255);
        drawable.setAlpha(255);
        ofFloat.addListener(new AnimatorListener());
        ofFloat.addUpdateListener(new AnimatorUpdater());
        attachToRecyclerView(recyclerView);
    }

    private void cancelHide() {
        this.mRecyclerView.removeCallbacks(this.mHideRunnable);
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this);
        this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
        cancelHide();
    }

    private void drawHorizontalScrollbar(Canvas canvas) {
        int i = this.mRecyclerViewHeight;
        int i2 = this.mHorizontalThumbHeight;
        i -= i2;
        int i3 = this.mHorizontalThumbCenterX;
        int i4 = this.mHorizontalThumbWidth;
        i3 -= i4 / 2;
        this.mHorizontalThumbDrawable.setBounds(0, 0, i4, i2);
        this.mHorizontalTrackDrawable.setBounds(0, 0, this.mRecyclerViewWidth, this.mHorizontalTrackHeight);
        canvas.translate(0.0f, (float) i);
        this.mHorizontalTrackDrawable.draw(canvas);
        canvas.translate((float) i3, 0.0f);
        this.mHorizontalThumbDrawable.draw(canvas);
        canvas.translate((float) (-i3), (float) (-i));
    }

    private void drawVerticalScrollbar(Canvas canvas) {
        int i = this.mRecyclerViewWidth;
        int i2 = this.mVerticalThumbWidth;
        i -= i2;
        int i3 = this.mVerticalThumbCenterY;
        int i4 = this.mVerticalThumbHeight;
        i3 -= i4 / 2;
        this.mVerticalThumbDrawable.setBounds(0, 0, i2, i4);
        this.mVerticalTrackDrawable.setBounds(0, 0, this.mVerticalTrackWidth, this.mRecyclerViewHeight);
        if (isLayoutRTL()) {
            this.mVerticalTrackDrawable.draw(canvas);
            canvas.translate((float) this.mVerticalThumbWidth, (float) i3);
            canvas.scale(-1.0f, 1.0f);
            this.mVerticalThumbDrawable.draw(canvas);
            canvas.scale(1.0f, 1.0f);
            canvas.translate((float) (-this.mVerticalThumbWidth), (float) (-i3));
            return;
        }
        canvas.translate((float) i, 0.0f);
        this.mVerticalTrackDrawable.draw(canvas);
        canvas.translate(0.0f, (float) i3);
        this.mVerticalThumbDrawable.draw(canvas);
        canvas.translate((float) (-i), (float) (-i3));
    }

    private int[] getHorizontalRange() {
        int[] iArr = this.mHorizontalRange;
        int i = this.mMargin;
        iArr[0] = i;
        iArr[1] = this.mRecyclerViewWidth - i;
        return iArr;
    }

    private int[] getVerticalRange() {
        int[] iArr = this.mVerticalRange;
        int i = this.mMargin;
        iArr[0] = i;
        iArr[1] = this.mRecyclerViewHeight - i;
        return iArr;
    }

    private void horizontalScrollTo(float f) {
        int[] horizontalRange = getHorizontalRange();
        f = Math.max((float) horizontalRange[0], Math.min((float) horizontalRange[1], f));
        if (Math.abs(((float) this.mHorizontalThumbCenterX) - f) >= 2.0f) {
            int scrollTo = scrollTo(this.mHorizontalDragX, f, horizontalRange, this.mRecyclerView.computeHorizontalScrollRange(), this.mRecyclerView.computeHorizontalScrollOffset(), this.mRecyclerViewWidth);
            if (scrollTo != 0) {
                this.mRecyclerView.scrollBy(scrollTo, 0);
            }
            this.mHorizontalDragX = f;
        }
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(this.mRecyclerView) == 1;
    }

    private void resetHideDelay(int i) {
        cancelHide();
        this.mRecyclerView.postDelayed(this.mHideRunnable, (long) i);
    }

    private int scrollTo(float f, float f2, int[] iArr, int i, int i2, int i3) {
        int i4 = iArr[1] - iArr[0];
        if (i4 == 0) {
            return 0;
        }
        i -= i3;
        int i5 = (int) (((f2 - f) / ((float) i4)) * ((float) i));
        i2 += i5;
        return (i2 >= i || i2 < 0) ? 0 : i5;
    }

    private void setupCallbacks() {
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this);
        this.mRecyclerView.addOnScrollListener(this.mOnScrollListener);
    }

    private void verticalScrollTo(float f) {
        int[] verticalRange = getVerticalRange();
        f = Math.max((float) verticalRange[0], Math.min((float) verticalRange[1], f));
        if (Math.abs(((float) this.mVerticalThumbCenterY) - f) >= 2.0f) {
            int scrollTo = scrollTo(this.mVerticalDragY, f, verticalRange, this.mRecyclerView.computeVerticalScrollRange(), this.mRecyclerView.computeVerticalScrollOffset(), this.mRecyclerViewHeight);
            if (scrollTo != 0) {
                this.mRecyclerView.scrollBy(0, scrollTo);
            }
            this.mVerticalDragY = f;
        }
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 != recyclerView) {
            if (recyclerView2 != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (recyclerView != null) {
                setupCallbacks();
            }
        }
    }

    @VisibleForTesting
    public Drawable getHorizontalThumbDrawable() {
        return this.mHorizontalThumbDrawable;
    }

    @VisibleForTesting
    public Drawable getHorizontalTrackDrawable() {
        return this.mHorizontalTrackDrawable;
    }

    @VisibleForTesting
    public Drawable getVerticalThumbDrawable() {
        return this.mVerticalThumbDrawable;
    }

    @VisibleForTesting
    public Drawable getVerticalTrackDrawable() {
        return this.mVerticalTrackDrawable;
    }

    @VisibleForTesting
    public void hide(int i) {
        int i2 = this.mAnimationState;
        if (i2 == 1) {
            this.mShowHideAnimator.cancel();
        } else if (i2 != 2) {
            return;
        }
        this.mAnimationState = 3;
        this.mShowHideAnimator.setFloatValues(new float[]{((Float) this.mShowHideAnimator.getAnimatedValue()).floatValue(), 0.0f});
        this.mShowHideAnimator.setDuration((long) i);
        this.mShowHideAnimator.start();
    }

    public boolean isDragging() {
        return this.mState == 2;
    }

    @VisibleForTesting
    public boolean isPointInsideHorizontalThumb(float f, float f2) {
        if (f2 >= ((float) (this.mRecyclerViewHeight - this.mHorizontalThumbHeight))) {
            int i = this.mHorizontalThumbCenterX;
            int i2 = this.mHorizontalThumbWidth;
            if (f >= ((float) (i - (i2 / 2))) && f <= ((float) ((i2 / 2) + i))) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    public boolean isPointInsideVerticalThumb(float f, float f2) {
        if (isLayoutRTL() ? f > ((float) (this.mVerticalThumbWidth / 2)) : f < ((float) (this.mRecyclerViewWidth - this.mVerticalThumbWidth))) {
            int i = this.mVerticalThumbCenterY;
            int i2 = this.mVerticalThumbHeight;
            if (f2 >= ((float) (i - (i2 / 2))) && f2 <= ((float) ((i2 / 2) + i))) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    public boolean isVisible() {
        return this.mState == 1;
    }

    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
        if (this.mRecyclerViewWidth == this.mRecyclerView.getWidth() && this.mRecyclerViewHeight == this.mRecyclerView.getHeight()) {
            if (this.mAnimationState != 0) {
                if (this.mNeedVerticalScrollbar) {
                    drawVerticalScrollbar(canvas);
                }
                if (this.mNeedHorizontalScrollbar) {
                    drawHorizontalScrollbar(canvas);
                }
            }
            return;
        }
        this.mRecyclerViewWidth = this.mRecyclerView.getWidth();
        this.mRecyclerViewHeight = this.mRecyclerView.getHeight();
        setState(0);
    }

    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        int i = this.mState;
        if (i == 1) {
            boolean isPointInsideVerticalThumb = isPointInsideVerticalThumb(motionEvent.getX(), motionEvent.getY());
            boolean isPointInsideHorizontalThumb = isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY());
            if (motionEvent.getAction() != 0) {
                return false;
            }
            if (!isPointInsideVerticalThumb && !isPointInsideHorizontalThumb) {
                return false;
            }
            if (isPointInsideHorizontalThumb) {
                this.mDragState = 1;
                this.mHorizontalDragX = (float) ((int) motionEvent.getX());
            } else if (isPointInsideVerticalThumb) {
                this.mDragState = 2;
                this.mVerticalDragY = (float) ((int) motionEvent.getY());
            }
            setState(2);
        } else if (i != 2) {
            return false;
        }
        return true;
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        if (this.mState != 0) {
            if (motionEvent.getAction() == 0) {
                boolean isPointInsideVerticalThumb = isPointInsideVerticalThumb(motionEvent.getX(), motionEvent.getY());
                boolean isPointInsideHorizontalThumb = isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY());
                if (isPointInsideVerticalThumb || isPointInsideHorizontalThumb) {
                    if (isPointInsideHorizontalThumb) {
                        this.mDragState = 1;
                        this.mHorizontalDragX = (float) ((int) motionEvent.getX());
                    } else if (isPointInsideVerticalThumb) {
                        this.mDragState = 2;
                        this.mVerticalDragY = (float) ((int) motionEvent.getY());
                    }
                    setState(2);
                }
            } else if (motionEvent.getAction() == 1 && this.mState == 2) {
                this.mVerticalDragY = 0.0f;
                this.mHorizontalDragX = 0.0f;
                setState(1);
                this.mDragState = 0;
            } else if (motionEvent.getAction() == 2 && this.mState == 2) {
                show();
                if (this.mDragState == 1) {
                    horizontalScrollTo(motionEvent.getX());
                }
                if (this.mDragState == 2) {
                    verticalScrollTo(motionEvent.getY());
                }
            }
        }
    }

    public void requestRedraw() {
        this.mRecyclerView.invalidate();
    }

    public void setState(int i) {
        if (i == 2 && this.mState != 2) {
            this.mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
            cancelHide();
        }
        if (i == 0) {
            requestRedraw();
        } else {
            show();
        }
        if (this.mState == 2 && i != 2) {
            this.mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS);
        } else if (i == 1) {
            resetHideDelay(HIDE_DELAY_AFTER_VISIBLE_MS);
        }
        this.mState = i;
    }

    public void show() {
        int i = this.mAnimationState;
        if (i != 0) {
            if (i == 3) {
                this.mShowHideAnimator.cancel();
            } else {
                return;
            }
        }
        this.mAnimationState = 1;
        this.mShowHideAnimator.setFloatValues(new float[]{((Float) this.mShowHideAnimator.getAnimatedValue()).floatValue(), 1.0f});
        this.mShowHideAnimator.setDuration(500);
        this.mShowHideAnimator.setStartDelay(0);
        this.mShowHideAnimator.start();
    }

    public void updateScrollPosition(int i, int i2) {
        int computeVerticalScrollRange = this.mRecyclerView.computeVerticalScrollRange();
        int i3 = this.mRecyclerViewHeight;
        boolean z = computeVerticalScrollRange - i3 > 0 && i3 >= this.mScrollbarMinimumRange;
        this.mNeedVerticalScrollbar = z;
        int computeHorizontalScrollRange = this.mRecyclerView.computeHorizontalScrollRange();
        int i4 = this.mRecyclerViewWidth;
        boolean z2 = computeHorizontalScrollRange - i4 > 0 && i4 >= this.mScrollbarMinimumRange;
        this.mNeedHorizontalScrollbar = z2;
        boolean z3 = this.mNeedVerticalScrollbar;
        if (z3 || z2) {
            if (z3) {
                float f = (float) i3;
                this.mVerticalThumbCenterY = (int) ((((f / 2.0f) + ((float) i2)) * f) / ((float) computeVerticalScrollRange));
                this.mVerticalThumbHeight = Math.min(i3, (i3 * i3) / computeVerticalScrollRange);
            }
            if (this.mNeedHorizontalScrollbar) {
                float f2 = (float) i4;
                this.mHorizontalThumbCenterX = (int) ((((f2 / 2.0f) + ((float) i)) * f2) / ((float) computeHorizontalScrollRange));
                this.mHorizontalThumbWidth = Math.min(i4, (i4 * i4) / computeHorizontalScrollRange);
            }
            i = this.mState;
            if (i == 0 || i == 1) {
                setState(1);
            }
            return;
        }
        if (this.mState != 0) {
            setState(0);
        }
    }
}
