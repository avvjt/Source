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

@d(c = "androidx.lifecycle.LiveDataScopeImpl$emit$2", f = "CoroutineLiveData.kt", l = {98}, m = "invokeSuspend")
/* compiled from: CoroutineLiveData.kt */
public final class LiveDataScopeImpl$emit$2 extends h implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    public final /* synthetic */ Object $value;
    public Object L$0;
    public int label;
    private CoroutineScope p$;
    public final /* synthetic */ LiveDataScopeImpl this$0;

    public LiveDataScopeImpl$emit$2(LiveDataScopeImpl liveDataScopeImpl, Object obj, Continuation continuation) {
        this.this$0 = liveDataScopeImpl;
        this.$value = obj;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkParameterIsNotNull(continuation, "completion");
        LiveDataScopeImpl$emit$2 liveDataScopeImpl$emit$2 = new LiveDataScopeImpl$emit$2(this.this$0, this.$value, continuation);
        liveDataScopeImpl$emit$2.p$ = (CoroutineScope) obj;
        return liveDataScopeImpl$emit$2;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((LiveDataScopeImpl$emit$2) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        a aVar = a.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            g.throwOnFailure(obj);
            CoroutineScope coroutineScope = this.p$;
            CoroutineLiveData target$lifecycle_livedata_ktx_release = this.this$0.getTarget$lifecycle_livedata_ktx_release();
            this.L$0 = coroutineScope;
            this.label = 1;
            if (target$lifecycle_livedata_ktx_release.clearSource$lifecycle_livedata_ktx_release(this) == aVar) {
                return aVar;
            }
        } else if (i == 1) {
            CoroutineScope coroutineScope2 = (CoroutineScope) this.L$0;
            g.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        this.this$0.getTarget$lifecycle_livedata_ktx_release().setValue(this.$value);
        return Unit.a;
    }
}
