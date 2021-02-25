package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import u.j.g.a;
import u.j.h.a.d;
import u.j.h.a.h;
import u.m.c.j;

@d(c = "androidx.lifecycle.LifecycleCoroutineScope$launchWhenCreated$1", f = "Lifecycle.kt", l = {74}, m = "invokeSuspend")
/* compiled from: Lifecycle.kt */
public final class LifecycleCoroutineScope$launchWhenCreated$1 extends h implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    public final /* synthetic */ Function2 $block;
    public int label;
    public final /* synthetic */ LifecycleCoroutineScope this$0;

    public LifecycleCoroutineScope$launchWhenCreated$1(LifecycleCoroutineScope lifecycleCoroutineScope, Function2 function2, Continuation continuation) {
        this.this$0 = lifecycleCoroutineScope;
        this.$block = function2;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkNotNullParameter(continuation, "completion");
        return new LifecycleCoroutineScope$launchWhenCreated$1(this.this$0, this.$block, continuation);
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((LifecycleCoroutineScope$launchWhenCreated$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        a aVar = a.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            g.throwOnFailure(obj);
            Lifecycle lifecycle$lifecycle_runtime_ktx_release = this.this$0.getLifecycle$lifecycle_runtime_ktx_release();
            Function2 function2 = this.$block;
            this.label = 1;
            if (PausingDispatcherKt.whenCreated(lifecycle$lifecycle_runtime_ktx_release, function2, (Continuation) this) == aVar) {
                return aVar;
            }
        } else if (i == 1) {
            g.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.a;
    }
}
