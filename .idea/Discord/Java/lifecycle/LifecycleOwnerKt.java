package androidx.lifecycle;

import u.m.c.j;

/* compiled from: LifecycleOwner.kt */
public final class LifecycleOwnerKt {
    public static final LifecycleCoroutineScope getLifecycleScope(LifecycleOwner lifecycleOwner) {
        j.checkNotNullParameter(lifecycleOwner, "$this$lifecycleScope");
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        return LifecycleKt.getCoroutineScope(lifecycle);
    }
}
