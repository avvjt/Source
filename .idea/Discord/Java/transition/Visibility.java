package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.core.content.res.TypedArrayUtils;
import androidx.transition.AnimatorUtils.AnimatorPauseListenerCompat;
import androidx.transition.Transition.TransitionListener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public abstract class Visibility extends Transition {
    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;
    private static final String PROPNAME_PARENT = "android:visibility:parent";
    private static final String PROPNAME_SCREEN_LOCATION = "android:visibility:screenLocation";
    public static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    private static final String[] sTransitionProperties = new String[]{PROPNAME_VISIBILITY, PROPNAME_PARENT};
    private int mMode = 3;

    @SuppressLint({"UniqueConstants"})
    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public static class VisibilityInfo {
        public ViewGroup mEndParent;
        public int mEndVisibility;
        public boolean mFadeIn;
        public ViewGroup mStartParent;
        public int mStartVisibility;
        public boolean mVisibilityChange;
    }

    public static class DisappearListener extends AnimatorListenerAdapter implements TransitionListener, AnimatorPauseListenerCompat {
        public boolean mCanceled = false;
        private final int mFinalVisibility;
        private boolean mLayoutSuppressed;
        private final ViewGroup mParent;
        private final boolean mSuppressLayout;
        private final View mView;

        public DisappearListener(View view, int i, boolean z) {
            this.mView = view;
            this.mFinalVisibility = i;
            this.mParent = (ViewGroup) view.getParent();
            this.mSuppressLayout = z;
            suppressLayout(true);
        }

        private void hideViewWhenNotCanceled() {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
                ViewGroup viewGroup = this.mParent;
                if (viewGroup != null) {
                    viewGroup.invalidate();
                }
            }
            suppressLayout(false);
        }

        private void suppressLayout(boolean z) {
            if (this.mSuppressLayout && this.mLayoutSuppressed != z) {
                ViewGroup viewGroup = this.mParent;
                if (viewGroup != null) {
                    this.mLayoutSuppressed = z;
                    ViewGroupUtils.suppressLayout(viewGroup, z);
                }
            }
        }

        public void onAnimationCancel(Animator animator) {
            this.mCanceled = true;
        }

        public void onAnimationEnd(Animator animator) {
            hideViewWhenNotCanceled();
        }

        public void onAnimationPause(Animator animator) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
            }
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationResume(Animator animator) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, 0);
            }
        }

        public void onAnimationStart(Animator animator) {
        }

        public void onTransitionCancel(@NonNull Transition transition) {
        }

        public void onTransitionEnd(@NonNull Transition transition) {
            hideViewWhenNotCanceled();
            transition.removeListener(this);
        }

        public void onTransitionPause(@NonNull Transition transition) {
            suppressLayout(false);
        }

        public void onTransitionResume(@NonNull Transition transition) {
            suppressLayout(true);
        }

        public void onTransitionStart(@NonNull Transition transition) {
        }
    }

    private void captureValues(TransitionValues transitionValues) {
        int visibility = transitionValues.view.getVisibility();
        transitionValues.values.put(PROPNAME_VISIBILITY, Integer.valueOf(visibility));
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
        int[] iArr = new int[2];
        transitionValues.view.getLocationOnScreen(iArr);
        transitionValues.values.put(PROPNAME_SCREEN_LOCATION, iArr);
    }

    private VisibilityInfo getVisibilityChangeInfo(TransitionValues transitionValues, TransitionValues transitionValues2) {
        VisibilityInfo visibilityInfo = new VisibilityInfo();
        visibilityInfo.mVisibilityChange = false;
        visibilityInfo.mFadeIn = false;
        String str = PROPNAME_PARENT;
        String str2 = PROPNAME_VISIBILITY;
        if (transitionValues == null || !transitionValues.values.containsKey(str2)) {
            visibilityInfo.mStartVisibility = -1;
            visibilityInfo.mStartParent = null;
        } else {
            visibilityInfo.mStartVisibility = ((Integer) transitionValues.values.get(str2)).intValue();
            visibilityInfo.mStartParent = (ViewGroup) transitionValues.values.get(str);
        }
        if (transitionValues2 == null || !transitionValues2.values.containsKey(str2)) {
            visibilityInfo.mEndVisibility = -1;
            visibilityInfo.mEndParent = null;
        } else {
            visibilityInfo.mEndVisibility = ((Integer) transitionValues2.values.get(str2)).intValue();
            visibilityInfo.mEndParent = (ViewGroup) transitionValues2.values.get(str);
        }
        if (transitionValues != null && transitionValues2 != null) {
            int i = visibilityInfo.mStartVisibility;
            int i2 = visibilityInfo.mEndVisibility;
            if (i == i2 && visibilityInfo.mStartParent == visibilityInfo.mEndParent) {
                return visibilityInfo;
            }
            if (i != i2) {
                if (i == 0) {
                    visibilityInfo.mFadeIn = false;
                    visibilityInfo.mVisibilityChange = true;
                } else if (i2 == 0) {
                    visibilityInfo.mFadeIn = true;
                    visibilityInfo.mVisibilityChange = true;
                }
            } else if (visibilityInfo.mEndParent == null) {
                visibilityInfo.mFadeIn = false;
                visibilityInfo.mVisibilityChange = true;
            } else if (visibilityInfo.mStartParent == null) {
                visibilityInfo.mFadeIn = true;
                visibilityInfo.mVisibilityChange = true;
            }
        } else if (transitionValues == null && visibilityInfo.mEndVisibility == 0) {
            visibilityInfo.mFadeIn = true;
            visibilityInfo.mVisibilityChange = true;
        } else if (transitionValues2 == null && visibilityInfo.mStartVisibility == 0) {
            visibilityInfo.mFadeIn = false;
            visibilityInfo.mVisibilityChange = true;
        }
        return visibilityInfo;
    }

    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Nullable
    public Animator createAnimator(@NonNull ViewGroup viewGroup, @Nullable TransitionValues transitionValues, @Nullable TransitionValues transitionValues2) {
        VisibilityInfo visibilityChangeInfo = getVisibilityChangeInfo(transitionValues, transitionValues2);
        if (!visibilityChangeInfo.mVisibilityChange || (visibilityChangeInfo.mStartParent == null && visibilityChangeInfo.mEndParent == null)) {
            return null;
        }
        if (visibilityChangeInfo.mFadeIn) {
            return onAppear(viewGroup, transitionValues, visibilityChangeInfo.mStartVisibility, transitionValues2, visibilityChangeInfo.mEndVisibility);
        }
        return onDisappear(viewGroup, transitionValues, visibilityChangeInfo.mStartVisibility, transitionValues2, visibilityChangeInfo.mEndVisibility);
    }

    public int getMode() {
        return this.mMode;
    }

    @Nullable
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public boolean isTransitionRequired(TransitionValues transitionValues, TransitionValues transitionValues2) {
        boolean z = false;
        if (transitionValues == null && transitionValues2 == null) {
            return false;
        }
        if (!(transitionValues == null || transitionValues2 == null)) {
            Map map = transitionValues2.values;
            String str = PROPNAME_VISIBILITY;
            if (map.containsKey(str) != transitionValues.values.containsKey(str)) {
                return false;
            }
        }
        VisibilityInfo visibilityChangeInfo = getVisibilityChangeInfo(transitionValues, transitionValues2);
        if (visibilityChangeInfo.mVisibilityChange && (visibilityChangeInfo.mStartVisibility == 0 || visibilityChangeInfo.mEndVisibility == 0)) {
            z = true;
        }
        return z;
    }

    public boolean isVisible(TransitionValues transitionValues) {
        boolean z = false;
        if (transitionValues == null) {
            return false;
        }
        int intValue = ((Integer) transitionValues.values.get(PROPNAME_VISIBILITY)).intValue();
        View view = (View) transitionValues.values.get(PROPNAME_PARENT);
        if (intValue == 0 && view != null) {
            z = true;
        }
        return z;
    }

    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        return null;
    }

    public Animator onAppear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        if ((this.mMode & 1) != 1 || transitionValues2 == null) {
            return null;
        }
        if (transitionValues == null) {
            View view = (View) transitionValues2.view.getParent();
            if (getVisibilityChangeInfo(getMatchedTransitionValues(view, false), getTransitionValues(view, false)).mVisibilityChange) {
                return null;
            }
        }
        return onAppear(viewGroup, transitionValues2.view, transitionValues, transitionValues2);
    }

    public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x004a  */
    /* JADX WARNING: Missing block: B:37:0x0089, code skipped:
            if (r0.mCanRemoveViews != false) goto L_0x008b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Animator onDisappear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        final ViewGroup viewGroup2 = viewGroup;
        TransitionValues transitionValues3 = transitionValues;
        TransitionValues transitionValues4 = transitionValues2;
        int i3 = i2;
        if ((this.mMode & 2) != 2 || transitionValues3 == null) {
            return null;
        }
        Object obj;
        final View view = transitionValues3.view;
        View view2 = transitionValues4 != null ? transitionValues4.view : null;
        int i4 = R.id.save_overlay_view;
        View view3 = (View) view.getTag(i4);
        if (view3 != null) {
            view2 = null;
            obj = 1;
        } else {
            View view4;
            if (view2 == null || view2.getParent() == null) {
                if (view2 != null) {
                    view3 = null;
                    obj = null;
                    if (obj != null) {
                        if (view.getParent() != null) {
                            if (view.getParent() instanceof View) {
                                View view5 = (View) view.getParent();
                                if (getVisibilityChangeInfo(getTransitionValues(view5, true), getMatchedTransitionValues(view5, true)).mVisibilityChange) {
                                    int id = view5.getId();
                                    if (view5.getParent() == null) {
                                        if (id != -1) {
                                            if (viewGroup2.findViewById(id) != null) {
                                            }
                                        }
                                    }
                                } else {
                                    view2 = TransitionUtils.copyViewImage(viewGroup2, view, view5);
                                }
                            }
                        }
                        view2 = view3;
                        obj = null;
                        view3 = view;
                    }
                    obj = null;
                    view4 = view3;
                    view3 = view2;
                    view2 = view4;
                }
            } else if (i3 == 4 || view == view2) {
                view3 = view2;
                obj = null;
                view2 = null;
                if (obj != null) {
                }
                obj = null;
                view4 = view3;
                view3 = view2;
                view2 = view4;
            }
            view2 = null;
            view3 = view2;
            obj = 1;
            if (obj != null) {
            }
            obj = null;
            view4 = view3;
            view3 = view2;
            view2 = view4;
        }
        if (view3 != null) {
            if (obj == null) {
                int[] iArr = (int[]) transitionValues3.values.get(PROPNAME_SCREEN_LOCATION);
                int i5 = iArr[0];
                i3 = iArr[1];
                int[] iArr2 = new int[2];
                viewGroup2.getLocationOnScreen(iArr2);
                view3.offsetLeftAndRight((i5 - iArr2[0]) - view3.getLeft());
                view3.offsetTopAndBottom((i3 - iArr2[1]) - view3.getTop());
                ViewGroupUtils.getOverlay(viewGroup).add(view3);
            }
            Animator onDisappear = onDisappear(viewGroup2, view3, transitionValues3, transitionValues4);
            if (obj == null) {
                if (onDisappear == null) {
                    ViewGroupUtils.getOverlay(viewGroup).remove(view3);
                } else {
                    view.setTag(i4, view3);
                    addListener(new TransitionListenerAdapter() {
                        public void onTransitionEnd(@NonNull Transition transition) {
                            view.setTag(R.id.save_overlay_view, null);
                            ViewGroupUtils.getOverlay(viewGroup2).remove(view3);
                            transition.removeListener(this);
                        }

                        public void onTransitionPause(@NonNull Transition transition) {
                            ViewGroupUtils.getOverlay(viewGroup2).remove(view3);
                        }

                        public void onTransitionResume(@NonNull Transition transition) {
                            if (view3.getParent() == null) {
                                ViewGroupUtils.getOverlay(viewGroup2).add(view3);
                            } else {
                                Visibility.this.cancel();
                            }
                        }
                    });
                }
            }
            return onDisappear;
        } else if (view2 == null) {
            return null;
        } else {
            int visibility = view2.getVisibility();
            ViewUtils.setTransitionVisibility(view2, 0);
            Animator onDisappear2 = onDisappear(viewGroup2, view2, transitionValues3, transitionValues4);
            if (onDisappear2 != null) {
                DisappearListener disappearListener = new DisappearListener(view2, i3, true);
                onDisappear2.addListener(disappearListener);
                AnimatorUtils.addPauseListener(onDisappear2, disappearListener);
                addListener(disappearListener);
            } else {
                ViewUtils.setTransitionVisibility(view2, visibility);
            }
            return onDisappear2;
        }
    }

    public void setMode(int i) {
        if ((i & -4) == 0) {
            this.mMode = i;
            return;
        }
        throw new IllegalArgumentException("Only MODE_IN and MODE_OUT flags are allowed");
    }

    @SuppressLint({"RestrictedApi"})
    public Visibility(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Styleable.VISIBILITY_TRANSITION);
        int namedInt = TypedArrayUtils.getNamedInt(obtainStyledAttributes, (XmlResourceParser) attributeSet, "transitionVisibilityMode", 0, 0);
        obtainStyledAttributes.recycle();
        if (namedInt != 0) {
            setMode(namedInt);
        }
    }
}
