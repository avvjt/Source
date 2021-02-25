package androidx.transition;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewGroupUtils {
    private static Method sGetChildDrawingOrderMethod = null;
    private static boolean sGetChildDrawingOrderMethodFetched = false;
    private static boolean sTryHiddenSuppressLayout = true;

    private ViewGroupUtils() {
    }

    public static int getChildDrawingOrder(@NonNull ViewGroup viewGroup, int i) {
        if (VERSION.SDK_INT >= 29) {
            return viewGroup.getChildDrawingOrder(i);
        }
        Method declaredMethod;
        if (!sGetChildDrawingOrderMethodFetched) {
            try {
                r5 = new Class[2];
                Class cls = Integer.TYPE;
                r5[0] = cls;
                r5[1] = cls;
                declaredMethod = ViewGroup.class.getDeclaredMethod("getChildDrawingOrder", r5);
                sGetChildDrawingOrderMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (NoSuchMethodException unused) {
            }
            sGetChildDrawingOrderMethodFetched = true;
        }
        declaredMethod = sGetChildDrawingOrderMethod;
        if (declaredMethod != null) {
            try {
                return ((Integer) declaredMethod.invoke(viewGroup, new Object[]{Integer.valueOf(viewGroup.getChildCount()), Integer.valueOf(i)})).intValue();
            } catch (IllegalAccessException | InvocationTargetException unused2) {
            }
        }
        return i;
    }

    public static ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup viewGroup) {
        return new ViewGroupOverlayApi18(viewGroup);
    }

    @RequiresApi(18)
    @SuppressLint({"NewApi"})
    private static void hiddenSuppressLayout(@NonNull ViewGroup viewGroup, boolean z) {
        if (sTryHiddenSuppressLayout) {
            try {
                viewGroup.suppressLayout(z);
            } catch (NoSuchMethodError unused) {
                sTryHiddenSuppressLayout = false;
            }
        }
    }

    public static void suppressLayout(@NonNull ViewGroup viewGroup, boolean z) {
        if (VERSION.SDK_INT >= 29) {
            viewGroup.suppressLayout(z);
        } else {
            hiddenSuppressLayout(viewGroup, z);
        }
    }
}
