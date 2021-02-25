package androidx.transition;

import android.animation.TypeEvaluator;
import android.graphics.Rect;

public class RectEvaluator implements TypeEvaluator<Rect> {
    private Rect mRect;

    public RectEvaluator(Rect rect) {
        this.mRect = rect;
    }

    public Rect evaluate(float f, Rect rect, Rect rect2) {
        int i = rect.left;
        i += (int) (((float) (rect2.left - i)) * f);
        int i2 = rect.top;
        i2 += (int) (((float) (rect2.top - i2)) * f);
        int i3 = rect.right;
        i3 += (int) (((float) (rect2.right - i3)) * f);
        int i4 = rect.bottom;
        i4 += (int) (((float) (rect2.bottom - i4)) * f);
        Rect rect3 = this.mRect;
        if (rect3 == null) {
            return new Rect(i, i2, i3, i4);
        }
        rect3.set(i, i2, i3, i4);
        return this.mRect;
    }
}
