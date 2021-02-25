package androidx.transition;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import androidx.annotation.NonNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CanvasUtils {
    private static Method sInorderBarrierMethod;
    private static boolean sOrderMethodsFetched;
    private static Method sReorderBarrierMethod;

    private CanvasUtils() {
    }

    @SuppressLint({"SoonBlockedPrivateApi"})
    public static void enableZ(@NonNull Canvas canvas, boolean z) {
        int i = VERSION.SDK_INT;
        if (i >= 29) {
            if (z) {
                canvas.enableZ();
            } else {
                canvas.disableZ();
            }
        } else if (i != 28) {
            if (!sOrderMethodsFetched) {
                try {
                    Method declaredMethod = Canvas.class.getDeclaredMethod("insertReorderBarrier", new Class[0]);
                    sReorderBarrierMethod = declaredMethod;
                    declaredMethod.setAccessible(true);
                    declaredMethod = Canvas.class.getDeclaredMethod("insertInorderBarrier", new Class[0]);
                    sInorderBarrierMethod = declaredMethod;
                    declaredMethod.setAccessible(true);
                } catch (NoSuchMethodException unused) {
                }
                sOrderMethodsFetched = true;
            }
            if (z) {
                try {
                    Method method = sReorderBarrierMethod;
                    if (method != null) {
                        method.invoke(canvas, new Object[0]);
                    }
                } catch (IllegalAccessException unused2) {
                    return;
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
            if (!z) {
                Method method2 = sInorderBarrierMethod;
                if (method2 != null) {
                    method2.invoke(canvas, new Object[0]);
                }
            }
        } else {
            throw new IllegalStateException("This method doesn't work on Pie!");
        }
    }
}
