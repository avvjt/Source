package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import u.j.g.a;
import v.a.s1.c;

/* compiled from: Collect.kt */
public final class FlowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1 implements c<T> {
    public final /* synthetic */ LiveDataScope $this_liveData$inlined;

    public FlowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1(LiveDataScope liveDataScope) {
        this.$this_liveData$inlined = liveDataScope;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0021  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Object emit(Object obj, Continuation continuation) {
        AnonymousClass1 anonymousClass1;
        Object obj2;
        a aVar;
        int i;
        if (continuation instanceof AnonymousClass1) {
            anonymousClass1 = (AnonymousClass1) continuation;
            int i2 = anonymousClass1.label;
            if ((i2 & Integer.MIN_VALUE) != 0) {
                anonymousClass1.label = i2 - Integer.MIN_VALUE;
                obj2 = anonymousClass1.result;
                aVar = a.COROUTINE_SUSPENDED;
                i = anonymousClass1.label;
                if (i != 0) {
                    g.throwOnFailure(obj2);
                    LiveDataScope liveDataScope = this.$this_liveData$inlined;
                    anonymousClass1.L$0 = this;
                    anonymousClass1.L$1 = obj;
                    anonymousClass1.L$2 = anonymousClass1;
                    anonymousClass1.L$3 = obj;
                    anonymousClass1.label = 1;
                    if (liveDataScope.emit(obj, anonymousClass1) == aVar) {
                        return aVar;
                    }
                } else if (i == 1) {
                    Continuation continuation2 = (Continuation) anonymousClass1.L$2;
                    FlowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1 flowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1 = (FlowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1) anonymousClass1.L$0;
                    g.throwOnFailure(obj2);
                } else {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                return Unit.a;
            }
        }
        anonymousClass1 = new u.j.h.a.c(continuation) {
            public Object L$0;
            public Object L$1;
            public Object L$2;
            public Object L$3;
            public int label;
            public /* synthetic */ Object result;

            public final Object invokeSuspend(Object obj) {
                this.result = obj;
                this.label |= Integer.MIN_VALUE;
                return FlowLiveDataConversions$asLiveData$1$invokeSuspend$$inlined$collect$1.this.emit(null, this);
            }
        };
        obj2 = anonymousClass1.result;
        aVar = a.COROUTINE_SUSPENDED;
        i = anonymousClass1.label;
        if (i != 0) {
        }
        return Unit.a;
    }
}
