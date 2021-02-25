package androidx.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.annotation.NonNull;
import androidx.transition.TransitionUtils.MatrixEvaluator;
import java.util.Map;

public class ChangeImageTransform extends Transition {
    private static final Property<ImageView, Matrix> ANIMATED_TRANSFORM_PROPERTY = new Property<ImageView, Matrix>(Matrix.class, "animatedTransform") {
        public Matrix get(ImageView imageView) {
            return null;
        }

        public void set(ImageView imageView, Matrix matrix) {
            ImageViewUtils.animateTransform(imageView, matrix);
        }
    };
    private static final TypeEvaluator<Matrix> NULL_MATRIX_EVALUATOR = new TypeEvaluator<Matrix>() {
        public Matrix evaluate(float f, Matrix matrix, Matrix matrix2) {
            return null;
        }
    };
    private static final String PROPNAME_BOUNDS = "android:changeImageTransform:bounds";
    private static final String PROPNAME_MATRIX = "android:changeImageTransform:matrix";
    private static final String[] sTransitionProperties = new String[]{PROPNAME_MATRIX, PROPNAME_BOUNDS};

    /* renamed from: androidx.transition.ChangeImageTransform$3 */
    public static /* synthetic */ class AnonymousClass3 {
        public static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;

        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Missing block: B:7:?, code skipped:
            return;
     */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static {
            int[] iArr = new int[ScaleType.values().length];
            $SwitchMap$android$widget$ImageView$ScaleType = iArr;
            iArr[ScaleType.FIT_XY.ordinal()] = 1;
            $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 2;
        }
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if ((view instanceof ImageView) && view.getVisibility() == 0) {
            ImageView imageView = (ImageView) view;
            if (imageView.getDrawable() != null) {
                Map map = transitionValues.values;
                map.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
                map.put(PROPNAME_MATRIX, copyImageMatrix(imageView));
            }
        }
    }

    private static Matrix centerCropMatrix(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        float width = (float) imageView.getWidth();
        float intrinsicWidth = (float) drawable.getIntrinsicWidth();
        float f = width / intrinsicWidth;
        float height = (float) imageView.getHeight();
        float intrinsicHeight = (float) drawable.getIntrinsicHeight();
        f = Math.max(f, height / intrinsicHeight);
        intrinsicHeight *= f;
        int round = Math.round((width - (intrinsicWidth * f)) / 2.0f);
        int round2 = Math.round((height - intrinsicHeight) / 2.0f);
        Matrix matrix = new Matrix();
        matrix.postScale(f, f);
        matrix.postTranslate((float) round, (float) round2);
        return matrix;
    }

    @NonNull
    private static Matrix copyImageMatrix(@NonNull ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
            int i = AnonymousClass3.$SwitchMap$android$widget$ImageView$ScaleType[imageView.getScaleType().ordinal()];
            if (i == 1) {
                return fitXYMatrix(imageView);
            }
            if (i == 2) {
                return centerCropMatrix(imageView);
            }
        }
        return new Matrix(imageView.getImageMatrix());
    }

    private ObjectAnimator createMatrixAnimator(ImageView imageView, Matrix matrix, Matrix matrix2) {
        return ObjectAnimator.ofObject(imageView, ANIMATED_TRANSFORM_PROPERTY, new MatrixEvaluator(), new Matrix[]{matrix, matrix2});
    }

    @NonNull
    private ObjectAnimator createNullAnimator(@NonNull ImageView imageView) {
        Property property = ANIMATED_TRANSFORM_PROPERTY;
        TypeEvaluator typeEvaluator = NULL_MATRIX_EVALUATOR;
        r2 = new Matrix[2];
        Matrix matrix = MatrixUtils.IDENTITY_MATRIX;
        r2[0] = matrix;
        r2[1] = matrix;
        return ObjectAnimator.ofObject(imageView, property, typeEvaluator, r2);
    }

    private static Matrix fitXYMatrix(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Matrix matrix = new Matrix();
        matrix.postScale(((float) imageView.getWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) imageView.getHeight()) / ((float) drawable.getIntrinsicHeight()));
        return matrix;
    }

    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public Animator createAnimator(@NonNull ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        Animator animator = null;
        if (!(transitionValues == null || transitionValues2 == null)) {
            Map map = transitionValues.values;
            String str = PROPNAME_BOUNDS;
            Rect rect = (Rect) map.get(str);
            Rect rect2 = (Rect) transitionValues2.values.get(str);
            if (!(rect == null || rect2 == null)) {
                Map map2 = transitionValues.values;
                String str2 = PROPNAME_MATRIX;
                Object obj = (Matrix) map2.get(str2);
                Matrix matrix = (Matrix) transitionValues2.values.get(str2);
                Object obj2 = (!(obj == null && matrix == null) && (obj == null || !obj.equals(matrix))) ? null : 1;
                if (rect.equals(rect2) && obj2 != null) {
                    return null;
                }
                ImageView imageView = (ImageView) transitionValues2.view;
                Drawable drawable = imageView.getDrawable();
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                    animator = createNullAnimator(imageView);
                } else {
                    if (obj == null) {
                        obj = MatrixUtils.IDENTITY_MATRIX;
                    }
                    if (matrix == null) {
                        matrix = MatrixUtils.IDENTITY_MATRIX;
                    }
                    ANIMATED_TRANSFORM_PROPERTY.set(imageView, obj);
                    animator = createMatrixAnimator(imageView, obj, matrix);
                }
            }
        }
        return animator;
    }

    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public ChangeImageTransform(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
