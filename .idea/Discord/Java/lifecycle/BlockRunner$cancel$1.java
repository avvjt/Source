package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import u.j.g.a;
import u.j.h.a.d;
import u.j.h.a.h;
import u.m.c.j;
import v.a.g0;

@d(c = "androidx.lifecycle.BlockRunner$cancel$1", f = "CoroutineLiveData.kt", l = {187}, m = "invokeSuspend")
/* compiled from: CoroutineLiveData.kt */
public final class BlockRunner$cancel$1 extends h implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    public Object L$0;
    public int label;
    private CoroutineScope p$;
    public final /* synthetic */ BlockRunner this$0;

    public BlockRunner$cancel$1(BlockRunner blockRunner, Continuation continuation) {
        this.this$0 = blockRunner;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkParameterIsNotNull(continuation, "completion");
        BlockRunner$cancel$1 blockRunner$cancel$1 = new BlockRunner$cancel$1(this.this$0, continuation);
        blockRunner$cancel$1.p$ = (CoroutineScope) obj;
        return blockRunner$cancel$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((BlockRunner$cancel$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        a aVar = a.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            g.throwOnFailure(obj);
            CoroutineScope coroutineScope = this.p$;
            long access$getTimeoutInMs$p = this.this$0.timeoutInMs;
            this.L$0 = coroutineScope;
            this.label = 1;
            if (g.k(access$getTimeoutInMs$p, this) == aVar) {
                return aVar;
            }
        } else if (i == 1) {
            CoroutineScope coroutineScope2 = (CoroutineScope) this.L$0;
            g.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        if (!this.this$0.liveData.hasActiveObservers()) {
            Job access$getRunningJob$p = this.this$0.runningJob;
            if (access$getRunningJob$p != null) {
                g0.g(access$getRunningJob$p, null, 1, null);
            }
            this.this$0.runningJob = null;
        }
        return Unit.a;
    }
}
