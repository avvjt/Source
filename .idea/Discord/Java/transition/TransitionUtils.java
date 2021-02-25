package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;

public class TransitionUtils {
    private static final boolean HAS_IS_ATTACHED_TO_WINDOW = true;
    private static final boolean HAS_OVERLAY = true;
    private static final boolean HAS_PICTURE_BITMAP;
    private static final int MAX_IMAGE_SIZE = 1048576;

    public static class MatrixEvaluator implements TypeEvaluator<Matrix> {
        public final float[] mTempEndValues = new float[9];
        public final Matrix mTempMatrix = new Matrix();
        public final float[] mTempStartValues = new float[9];

        public Matrix evaluate(float f, Matrix matrix, Matrix matrix2) {
            matrix.getValues(this.mTempStartValues);
            matrix2.getValues(this.mTempEndValues);
            for (int i = 0; i < 9; i++) {
                float[] fArr = this.mTempEndValues;
                float f2 = fArr[i];
                float[] fArr2 = this.mTempStartValues;
                fArr[i] = ((f2 - fArr2[i]) * f) + fArr2[i];
            }
            this.mTempMatrix.setValues(this.mTempEndValues);
            return this.mTempMatrix;
        }
    }

    static {
        int i = VERSION.SDK_INT;
        boolean z = true;
        if (i < 28) {
            z = false;
        }
        HAS_PICTURE_BITMAP = z;
    }

    private TransitionUtils() {
    }

    public static View copyViewImage(ViewGroup viewGroup, View view, View view2) {
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) (-view2.getScrollX()), (float) (-view2.getScrollY()));
        ViewUtils.transformMatrixToGlobal(view, matrix);
        ViewUtils.transformMatrixToLocal(viewGroup, matrix);
        RectF rectF = new RectF(0.0f, 0.0f, (float) view.getWidth(), (float) view.getHeight());
        matrix.mapRect(rectF);
        int round = Math.round(rectF.left);
        int round2 = Math.round(rectF.top);
        int round3 = Math.round(rectF.right);
        int round4 = Math.round(rectF.bottom);
        ImageView imageView = new ImageView(view.getContext());
        imageView.setScaleType(ScaleType.CENTER_CROP);
        Bitmap createViewBitmap = createViewBitmap(view, matrix, rectF, viewGroup);
        if (createViewBitmap != null) {
            imageView.setImageBitmap(createViewBitmap);
        }
        imageView.measure(MeasureSpec.makeMeasureSpec(round3 - round, BasicMeasure.EXACTLY), MeasureSpec.makeMeasureSpec(round4 - round2, BasicMeasure.EXACTLY));
        imageView.layout(round, round2, round3, round4);
        return imageView;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0071  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static Bitmap createViewBitmap(View view, Matrix matrix, RectF rectF, ViewGroup viewGroup) {
        boolean isAttachedToWindow;
        boolean z;
        Bitmap bitmap;
        ViewGroup viewGroup2;
        int i;
        int round;
        int round2;
        float min;
        int isAttachedToWindow2;
        if (HAS_IS_ATTACHED_TO_WINDOW) {
            isAttachedToWindow2 = view.isAttachedToWindow() ^ 1;
            if (viewGroup != null) {
                isAttachedToWindow = viewGroup.isAttachedToWindow();
                z = HAS_OVERLAY;
                bitmap = null;
                if (z || isAttachedToWindow2 == 0) {
                    viewGroup2 = null;
                    i = 0;
                } else if (!isAttachedToWindow) {
                    return null;
                } else {
                    viewGroup2 = (ViewGroup) view.getParent();
                    i = viewGroup2.indexOfChild(view);
                    viewGroup.getOverlay().add(view);
                }
                round = Math.round(rectF.width());
                round2 = Math.round(rectF.height());
                if (round > 0 && round2 > 0) {
                    min = Math.min(1.0f, 1048576.0f / ((float) (round * round2)));
                    round = Math.round(((float) round) * min);
                    round2 = Math.round(((float) round2) * min);
                    matrix.postTranslate(-rectF.left, -rectF.top);
                    matrix.postScale(min, min);
                    if (HAS_PICTURE_BITMAP) {
                        bitmap = Bitmap.createBitmap(round, round2, Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.concat(matrix);
                        view.draw(canvas);
                    } else {
                        Picture picture = new Picture();
                        Canvas beginRecording = picture.beginRecording(round, round2);
                        beginRecording.concat(matrix);
                        view.draw(beginRecording);
                        picture.endRecording();
                        bitmap = Bitmap.createBitmap(picture);
                    }
                }
                if (z && isAttachedToWindow2 != 0) {
                    viewGroup.getOverlay().remove(view);
                    viewGroup2.addView(view, i);
                }
                return bitmap;
            }
        }
        isAttachedToWindow2 = 0;
        isAttachedToWindow = false;
        z = HAS_OVERLAY;
        bitmap = null;
        if (z) {
        }
        viewGroup2 = null;
        i = 0;
        round = Math.round(rectF.width());
        round2 = Math.round(rectF.height());
        min = Math.min(1.0f, 1048576.0f / ((float) (round * round2)));
        round = Math.round(((float) round) * min);
        round2 = Math.round(((float) round2) * min);
        matrix.postTranslate(-rectF.left, -rectF.top);
        matrix.postScale(min, min);
        if (HAS_PICTURE_BITMAP) {
        }
        viewGroup.getOverlay().remove(view);
        viewGroup2.addView(view, i);
        return bitmap;
    }

    public static Animator mergeAnimators(Animator animator, Animator animator2) {
        if (animator == null) {
            return animator2;
        }
        if (animator2 == null) {
            return animator;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{animator, animator2});
        return animatorSet;
    }
}
