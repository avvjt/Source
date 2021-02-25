package androidx.lifecycle;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import f.i.a.f.f.o.g;
import kotlin.coroutines.CoroutineContext;
import u.m.c.j;
import v.a.g0;
import v.a.h0;
import v.a.t1.l;
import v.a.x;

/* compiled from: Lifecycle.kt */
public final class LifecycleCoroutineScopeImpl extends LifecycleCoroutineScope implements LifecycleEventObserver {
    private final CoroutineContext coroutineContext;
    private final Lifecycle lifecycle;

    public LifecycleCoroutineScopeImpl(Lifecycle lifecycle, CoroutineContext coroutineContext) {
        j.checkNotNullParameter(lifecycle, "lifecycle");
        j.checkNotNullParameter(coroutineContext, "coroutineContext");
        this.lifecycle = lifecycle;
        this.coroutineContext = coroutineContext;
        if (getLifecycle$lifecycle_runtime_ktx_release().getCurrentState() == State.DESTROYED) {
            g0.f(getCoroutineContext(), null, 1, null);
        }
    }

    public CoroutineContext getCoroutineContext() {
        return this.coroutineContext;
    }

    public Lifecycle getLifecycle$lifecycle_runtime_ktx_release() {
        return this.lifecycle;
    }

    public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
        j.checkNotNullParameter(lifecycleOwner, "source");
        j.checkNotNullParameter(event, NotificationCompat.CATEGORY_EVENT);
        if (getLifecycle$lifecycle_runtime_ktx_release().getCurrentState().compareTo(State.DESTROYED) <= 0) {
            getLifecycle$lifecycle_runtime_ktx_release().removeObserver(this);
            g0.f(getCoroutineContext(), null, 1, null);
        }
    }

    public final void register() {
        x xVar = h0.a;
        g.N(this, l.b.t(), null, new LifecycleCoroutineScopeImpl$register$1(this, null), 2, null);
    }
}
