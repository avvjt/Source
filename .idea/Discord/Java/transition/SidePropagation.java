package androidx.transition;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

public class SidePropagation extends VisibilityPropagation {
    private float mPropagationSpeed = 3.0f;
    private int mSide = 80;

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002b  */
    /* JADX WARNING: Missing block: B:5:0x0013, code skipped:
            if (r4 != null) goto L_0x0015;
     */
    /* JADX WARNING: Missing block: B:7:0x0017, code skipped:
            r0 = 3;
     */
    /* JADX WARNING: Missing block: B:13:0x0026, code skipped:
            if (r4 != null) goto L_0x0017;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int distance(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        int i9 = this.mSide;
        Object obj = 1;
        if (i9 != GravityCompat.START) {
            if (i9 == GravityCompat.END) {
                if (ViewCompat.getLayoutDirection(view) != 1) {
                    obj = null;
                }
            }
            if (i9 != 3) {
                return Math.abs(i4 - i2) + (i7 - i);
            } else if (i9 == 5) {
                return Math.abs(i4 - i2) + (i - i5);
            } else if (i9 == 48) {
                return Math.abs(i3 - i) + (i8 - i2);
            } else if (i9 != 80) {
                return 0;
            } else {
                return Math.abs(i3 - i) + (i2 - i6);
            }
        } else if (ViewCompat.getLayoutDirection(view) != 1) {
            obj = null;
        }
        i9 = 5;
        if (i9 != 3) {
        }
    }

    private int getMaxDistance(ViewGroup viewGroup) {
        int i = this.mSide;
        if (i == 3 || i == 5 || i == GravityCompat.START || i == GravityCompat.END) {
            return viewGroup.getWidth();
        }
        return viewGroup.getHeight();
    }

    public long getStartDelay(ViewGroup viewGroup, Transition transition, TransitionValues transitionValues, TransitionValues transitionValues2) {
        TransitionValues transitionValues3 = transitionValues;
        if (transitionValues3 == null && transitionValues2 == null) {
            return 0;
        }
        int i;
        int centerX;
        int centerY;
        Rect epicenter = transition.getEpicenter();
        if (transitionValues2 == null || getViewVisibility(transitionValues3) == 0) {
            i = -1;
        } else {
            transitionValues3 = transitionValues2;
            i = 1;
        }
        int viewX = getViewX(transitionValues3);
        int viewY = getViewY(transitionValues3);
        int[] iArr = new int[2];
        viewGroup.getLocationOnScreen(iArr);
        int round = iArr[0] + Math.round(viewGroup.getTranslationX());
        int round2 = Math.round(viewGroup.getTranslationY()) + iArr[1];
        int width = viewGroup.getWidth() + round;
        int height = viewGroup.getHeight() + round2;
        if (epicenter != null) {
            centerX = epicenter.centerX();
            centerY = epicenter.centerY();
        } else {
            centerX = (round + width) / 2;
            centerY = (round2 + height) / 2;
        }
        float distance = ((float) distance(viewGroup, viewX, viewY, centerX, centerY, round, round2, width, height)) / ((float) getMaxDistance(viewGroup));
        long duration = transition.getDuration();
        if (duration < 0) {
            duration = 300;
        }
        return (long) Math.round((((float) (duration * ((long) i))) / this.mPropagationSpeed) * distance);
    }

    public void setPropagationSpeed(float f) {
        if (f != 0.0f) {
            this.mPropagationSpeed = f;
            return;
        }
        throw new IllegalArgumentException("propagationSpeed may not be 0");
    }

    public void setSide(int i) {
        this.mSide = i;
    }
}
