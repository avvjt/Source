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

@d(c = "androidx.lifecycle.BlockRunner$maybeRun$1", f = "CoroutineLiveData.kt", l = {176}, m = "invokeSuspend")
/* compiled from: CoroutineLiveData.kt */
public final class BlockRunner$maybeRun$1 extends h implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    public Object L$0;
    public Object L$1;
    public int label;
    private CoroutineScope p$;
    public final /* synthetic */ BlockRunner this$0;

    public BlockRunner$maybeRun$1(BlockRunner blockRunner, Continuation continuation) {
        this.this$0 = blockRunner;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkParameterIsNotNull(continuation, "completion");
        BlockRunner$maybeRun$1 blockRunner$maybeRun$1 = new BlockRunner$maybeRun$1(this.this$0, continuation);
        blockRunner$maybeRun$1.p$ = (CoroutineScope) obj;
        return blockRunner$maybeRun$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((BlockRunner$maybeRun$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        a aVar = a.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            g.throwOnFailure(obj);
            CoroutineScope coroutineScope = this.p$;
            LiveDataScopeImpl liveDataScopeImpl = new LiveDataScopeImpl(this.this$0.liveData, coroutineScope.getCoroutineContext());
            Function2 access$getBlock$p = this.this$0.block;
            this.L$0 = coroutineScope;
            this.L$1 = liveDataScopeImpl;
            this.label = 1;
            if (access$getBlock$p.invoke(liveDataScopeImpl, this) == aVar) {
                return aVar;
            }
        } else if (i == 1) {
            LiveDataScopeImpl liveDataScopeImpl2 = (LiveDataScopeImpl) this.L$1;
            CoroutineScope coroutineScope2 = (CoroutineScope) this.L$0;
            g.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        this.this$0.onDone.invoke();
        return Unit.a;
    }
}
