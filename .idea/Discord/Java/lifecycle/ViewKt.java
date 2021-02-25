package androidx.lifecycle;

import android.view.View;
import u.m.c.j;

/* compiled from: View.kt */
public final class ViewKt {
    public static final LifecycleOwner findViewTreeLifecycleOwner(View view) {
        j.checkNotNullParameter(view, "$this$findViewTreeLifecycleOwner");
        return ViewTreeLifecycleOwner.get(view);
    }
}
