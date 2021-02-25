package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import u.j.g.a;
import u.j.h.a.d;
import u.j.h.a.h;
import u.m.c.j;
import v.a.s1.b;

@d(c = "androidx.lifecycle.FlowLiveDataConversions$asLiveData$1", f = "FlowLiveData.kt", l = {139}, m = "invokeSuspend")
/* compiled from: FlowLiveData.kt */
public final class FlowLiveDataConversions$asLiveData$1 extends h implements Function2<LiveDataScope<T>, Continuation<? super Unit>, Object> {
    public final /* synthetic */ b $this_asLiveData;
    public Object L$0;
    public Object L$1;
    public int label;
    private LiveDataScope p$;

    public FlowLiveDataConversions$asLiveData$1(b bVar, Continuation continuation) {
        this.$this_asLiveData = bVar;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkParameterIsNotNull(continuation, "completion");
        FlowLiveDataConversions$asLiveData$1 flowLiveDataConversions$asLiveData$1 = new FlowLiveDataConversions$asLiveData$1(this.$this_asLiveData, continuation);
        flowLiveDataConversions$asLiveData$1.p$ = (LiveDataScope) obj;
        return flowLiveDataConversions$asLiveData$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((FlowLiveDataConversions$asLiveData$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        a aVar = a.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            g.throwOnFailure(obj);
            LiveDataScope liveDataScope = this.p$;
            b bVar = this.$this_asLiveData;
            FlowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1 flowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1 = new FlowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1(liveDataScope);
            this.L$0 = liveDataScope;
            this.L$1 = bVar;
            this.label = 1;
            if (bVar.a(flowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1, this) == aVar) {
                return aVar;
            }
        } else if (i == 1) {
            b bVar2 = (b) this.L$1;
            LiveDataScope liveDataScope2 = (LiveDataScope) this.L$0;
            g.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.a;
    }
}
