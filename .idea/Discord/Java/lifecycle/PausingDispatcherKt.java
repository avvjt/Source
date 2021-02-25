package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.State;
import f.i.a.f.f.o.g;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import u.m.c.j;
import v.a.h0;
import v.a.t1.l;
import v.a.x;

/* compiled from: PausingDispatcher.kt */
public final class PausingDispatcherKt {
    public static final <T> Object whenCreated(LifecycleOwner lifecycleOwner, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        return whenCreated(lifecycle, (Function2) function2, (Continuation) continuation);
    }

    public static final <T> Object whenResumed(LifecycleOwner lifecycleOwner, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        return whenResumed(lifecycle, (Function2) function2, (Continuation) continuation);
    }

    public static final <T> Object whenStarted(LifecycleOwner lifecycleOwner, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        return whenStarted(lifecycle, (Function2) function2, (Continuation) continuation);
    }

    public static final <T> Object whenStateAtLeast(Lifecycle lifecycle, State state, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        x xVar = h0.a;
        return g.j0(l.b.t(), new PausingDispatcherKt$whenStateAtLeast$2(lifecycle, state, function2, null), continuation);
    }

    public static final <T> Object whenCreated(Lifecycle lifecycle, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        return whenStateAtLeast(lifecycle, State.CREATED, function2, continuation);
    }

    public static final <T> Object whenResumed(Lifecycle lifecycle, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        return whenStateAtLeast(lifecycle, State.RESUMED, function2, continuation);
    }

    public static final <T> Object whenStarted(Lifecycle lifecycle, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        return whenStateAtLeast(lifecycle, State.STARTED, function2, continuation);
    }
}
