package androidx.transition;

import android.animation.LayoutTransition;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewGroupUtilsApi14 {
    private static final int LAYOUT_TRANSITION_CHANGING = 4;
    private static final String TAG = "ViewGroupUtilsApi14";
    private static Method sCancelMethod;
    private static boolean sCancelMethodFetched;
    private static LayoutTransition sEmptyLayoutTransition;
    private static Field sLayoutSuppressedField;
    private static boolean sLayoutSuppressedFieldFetched;

    private ViewGroupUtilsApi14() {
    }

    private static void cancelLayoutTransition(LayoutTransition layoutTransition) {
        boolean z = sCancelMethodFetched;
        String str = "Failed to access cancel method by reflection";
        String str2 = TAG;
        if (!z) {
            try {
                Method declaredMethod = LayoutTransition.class.getDeclaredMethod("cancel", new Class[0]);
                sCancelMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (NoSuchMethodException unused) {
                Log.i(str2, str);
            }
            sCancelMethodFetched = true;
        }
        Method method = sCancelMethod;
        if (method != null) {
            try {
                method.invoke(layoutTransition, new Object[0]);
            } catch (IllegalAccessException unused2) {
                Log.i(str2, str);
            } catch (InvocationTargetException unused3) {
                Log.i(str2, "Failed to invoke cancel method by reflection");
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0081  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void suppressLayout(@NonNull ViewGroup viewGroup, boolean z) {
        int i;
        LayoutTransition layoutTransition;
        int i2 = 0;
        if (sEmptyLayoutTransition == null) {
            AnonymousClass1 anonymousClass1 = new LayoutTransition() {
                public boolean isChangingLayout() {
                    return true;
                }
            };
            sEmptyLayoutTransition = anonymousClass1;
            anonymousClass1.setAnimator(2, null);
            sEmptyLayoutTransition.setAnimator(0, null);
            sEmptyLayoutTransition.setAnimator(1, null);
            sEmptyLayoutTransition.setAnimator(3, null);
            sEmptyLayoutTransition.setAnimator(4, null);
        }
        if (z) {
            LayoutTransition layoutTransition2 = viewGroup.getLayoutTransition();
            if (layoutTransition2 != null) {
                if (layoutTransition2.isRunning()) {
                    cancelLayoutTransition(layoutTransition2);
                }
                if (layoutTransition2 != sEmptyLayoutTransition) {
                    viewGroup.setTag(R.id.transition_layout_save, layoutTransition2);
                }
            }
            viewGroup.setLayoutTransition(sEmptyLayoutTransition);
            return;
        }
        Field declaredField;
        viewGroup.setLayoutTransition(null);
        z = sLayoutSuppressedFieldFetched;
        String str = TAG;
        if (!z) {
            try {
                declaredField = ViewGroup.class.getDeclaredField("mLayoutSuppressed");
                sLayoutSuppressedField = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException unused) {
                Log.i(str, "Failed to access mLayoutSuppressed field by reflection");
            }
            sLayoutSuppressedFieldFetched = true;
        }
        declaredField = sLayoutSuppressedField;
        if (declaredField != null) {
            try {
                z = declaredField.getBoolean(viewGroup);
                if (z) {
                    try {
                        sLayoutSuppressedField.setBoolean(viewGroup, false);
                    } catch (IllegalAccessException unused2) {
                        i2 = z;
                    }
                }
                i2 = z;
            } catch (IllegalAccessException unused3) {
                Log.i(str, "Failed to get mLayoutSuppressed field by reflection");
                if (i2 != 0) {
                }
                i = R.id.transition_layout_save;
                layoutTransition = (LayoutTransition) viewGroup.getTag(i);
                if (layoutTransition == null) {
                }
            }
        }
        if (i2 != 0) {
            viewGroup.requestLayout();
        }
        i = R.id.transition_layout_save;
        layoutTransition = (LayoutTransition) viewGroup.getTag(i);
        if (layoutTransition == null) {
            viewGroup.setTag(i, null);
            viewGroup.setLayoutTransition(layoutTransition);
        }
    }
}
