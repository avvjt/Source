package androidx.lifecycle;

import kotlin.coroutines.CoroutineContext.a.a;
import u.m.c.j;
import v.a.b1;
import v.a.g0;
import v.a.h0;
import v.a.q;
import v.a.t1.l;
import v.a.x;

/* compiled from: Lifecycle.kt */
public final class LifecycleKt {
    public static final LifecycleCoroutineScope getCoroutineScope(Lifecycle lifecycle) {
        LifecycleCoroutineScopeImpl lifecycleCoroutineScopeImpl;
        j.checkNotNullParameter(lifecycle, "$this$coroutineScope");
        do {
            lifecycleCoroutineScopeImpl = (LifecycleCoroutineScopeImpl) lifecycle.mInternalScopeRef.get();
            if (lifecycleCoroutineScopeImpl != null) {
                return lifecycleCoroutineScopeImpl;
            }
            q a = g0.a(null, 1);
            x xVar = h0.a;
            lifecycleCoroutineScopeImpl = new LifecycleCoroutineScopeImpl(lifecycle, a.plus((b1) a, l.b.t()));
        } while (!lifecycle.mInternalScopeRef.compareAndSet(null, lifecycleCoroutineScopeImpl));
        lifecycleCoroutineScopeImpl.register();
        return lifecycleCoroutineScopeImpl;
    }
}
