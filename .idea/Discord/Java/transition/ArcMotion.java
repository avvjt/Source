package androidx.transition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;
import f.d.b.a.a;
import org.xmlpull.v1.XmlPullParser;

public class ArcMotion extends PathMotion {
    private static final float DEFAULT_MAX_ANGLE_DEGREES = 70.0f;
    private static final float DEFAULT_MAX_TANGENT = ((float) Math.tan(Math.toRadians(35.0d)));
    private static final float DEFAULT_MIN_ANGLE_DEGREES = 0.0f;
    private float mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES;
    private float mMaximumTangent = DEFAULT_MAX_TANGENT;
    private float mMinimumHorizontalAngle = 0.0f;
    private float mMinimumHorizontalTangent = 0.0f;
    private float mMinimumVerticalAngle = 0.0f;
    private float mMinimumVerticalTangent = 0.0f;

    private static float toTangent(float f) {
        if (f >= 0.0f && f <= 90.0f) {
            return (float) Math.tan(Math.toRadians((double) (f / 2.0f)));
        }
        throw new IllegalArgumentException("Arc must be between 0 and 90 degrees");
    }

    public float getMaximumAngle() {
        return this.mMaximumAngle;
    }

    public float getMinimumHorizontalAngle() {
        return this.mMinimumHorizontalAngle;
    }

    public float getMinimumVerticalAngle() {
        return this.mMinimumVerticalAngle;
    }

    public Path getPath(float f, float f2, float f3, float f4) {
        Path path = new Path();
        path.moveTo(f, f2);
        float f5 = f3 - f;
        float f6 = f4 - f2;
        float f7 = (f6 * f6) + (f5 * f5);
        float f8 = (f + f3) / 2.0f;
        float f9 = (f2 + f4) / 2.0f;
        float f10 = 0.25f * f7;
        Object obj = f2 > f4 ? 1 : null;
        if (Math.abs(f5) < Math.abs(f6)) {
            f5 = Math.abs(f7 / (f6 * 2.0f));
            if (obj != null) {
                f5 += f4;
                f6 = f3;
            } else {
                f5 += f2;
                f6 = f;
            }
            f7 = this.mMinimumVerticalTangent;
        } else {
            f7 /= f5 * 2.0f;
            if (obj != null) {
                f5 = f2;
                f6 = f7 + f;
            } else {
                f6 = f3 - f7;
                f5 = f4;
            }
            f7 = this.mMinimumHorizontalTangent;
        }
        float f11 = (f10 * f7) * f7;
        f7 = f8 - f6;
        float f12 = f9 - f5;
        f12 = (f12 * f12) + (f7 * f7);
        f7 = this.mMaximumTangent;
        f10 = (f10 * f7) * f7;
        if (f12 >= f11) {
            f11 = f12 > f10 ? f10 : 0.0f;
        }
        if (f11 != 0.0f) {
            f7 = (float) Math.sqrt((double) (f11 / f12));
            f6 = a.a(f6, f8, f7, f8);
            f5 = a.a(f5, f9, f7, f9);
        }
        path.cubicTo((f + f6) / 2.0f, (f2 + f5) / 2.0f, (f6 + f3) / 2.0f, (f5 + f4) / 2.0f, f3, f4);
        return path;
    }

    public void setMaximumAngle(float f) {
        this.mMaximumAngle = f;
        this.mMaximumTangent = toTangent(f);
    }

    public void setMinimumHorizontalAngle(float f) {
        this.mMinimumHorizontalAngle = f;
        this.mMinimumHorizontalTangent = toTangent(f);
    }

    public void setMinimumVerticalAngle(float f) {
        this.mMinimumVerticalAngle = f;
        this.mMinimumVerticalTangent = toTangent(f);
    }

    @SuppressLint({"RestrictedApi"})
    public ArcMotion(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Styleable.ARC_MOTION);
        XmlPullParser xmlPullParser = (XmlPullParser) attributeSet;
        setMinimumVerticalAngle(TypedArrayUtils.getNamedFloat(obtainStyledAttributes, xmlPullParser, "minimumVerticalAngle", 1, 0.0f));
        setMinimumHorizontalAngle(TypedArrayUtils.getNamedFloat(obtainStyledAttributes, xmlPullParser, "minimumHorizontalAngle", 0, 0.0f));
        setMaximumAngle(TypedArrayUtils.getNamedFloat(obtainStyledAttributes, xmlPullParser, "maximumAngle", 2, DEFAULT_MAX_ANGLE_DEGREES));
        obtainStyledAttributes.recycle();
    }
}
