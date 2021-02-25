package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import u.m.c.j;

/* compiled from: Lifecycle.kt */
public abstract class LifecycleCoroutineScope implements CoroutineScope {
    public abstract /* synthetic */ CoroutineContext getCoroutineContext();

    public abstract Lifecycle getLifecycle$lifecycle_runtime_ktx_release();

    public final Job launchWhenCreated(Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object> function2) {
        j.checkNotNullParameter(function2, "block");
        return g.N(this, null, null, new LifecycleCoroutineScope$launchWhenCreated$1(this, function2, null), 3, null);
    }

    public final Job launchWhenResumed(Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object> function2) {
        j.checkNotNullParameter(function2, "block");
        return g.N(this, null, null, new LifecycleCoroutineScope$launchWhenResumed$1(this, function2, null), 3, null);
    }

    public final Job launchWhenStarted(Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object> function2) {
        j.checkNotNullParameter(function2, "block");
        return g.N(this, null, null, new LifecycleCoroutineScope$launchWhenStarted$1(this, function2, null), 3, null);
    }
}
