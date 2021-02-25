package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.State;
import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import u.j.h.a.d;
import u.j.h.a.h;
import u.m.c.j;
import v.a.g0;

@d(c = "androidx.lifecycle.LifecycleCoroutineScopeImpl$register$1", f = "Lifecycle.kt", l = {}, m = "invokeSuspend")
/* compiled from: Lifecycle.kt */
public final class LifecycleCoroutineScopeImpl$register$1 extends h implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    private /* synthetic */ Object L$0;
    public int label;
    public final /* synthetic */ LifecycleCoroutineScopeImpl this$0;

    public LifecycleCoroutineScopeImpl$register$1(LifecycleCoroutineScopeImpl lifecycleCoroutineScopeImpl, Continuation continuation) {
        this.this$0 = lifecycleCoroutineScopeImpl;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkNotNullParameter(continuation, "completion");
        LifecycleCoroutineScopeImpl$register$1 lifecycleCoroutineScopeImpl$register$1 = new LifecycleCoroutineScopeImpl$register$1(this.this$0, continuation);
        lifecycleCoroutineScopeImpl$register$1.L$0 = obj;
        return lifecycleCoroutineScopeImpl$register$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((LifecycleCoroutineScopeImpl$register$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        if (this.label == 0) {
            g.throwOnFailure(obj);
            CoroutineScope coroutineScope = (CoroutineScope) this.L$0;
            if (this.this$0.getLifecycle$lifecycle_runtime_ktx_release().getCurrentState().compareTo(State.INITIALIZED) >= 0) {
                this.this$0.getLifecycle$lifecycle_runtime_ktx_release().addObserver(this.this$0);
            } else {
                g0.f(coroutineScope.getCoroutineContext(), null, 1, null);
            }
            return Unit.a;
        }
        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
    }
}
