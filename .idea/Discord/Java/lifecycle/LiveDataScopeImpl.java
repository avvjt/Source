package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import u.m.c.j;
import v.a.h0;
import v.a.i0;
import v.a.t1.l;
import v.a.x;

/* compiled from: CoroutineLiveData.kt */
public final class LiveDataScopeImpl<T> implements LiveDataScope<T> {
    private final CoroutineContext coroutineContext;
    private CoroutineLiveData<T> target;

    public LiveDataScopeImpl(CoroutineLiveData<T> coroutineLiveData, CoroutineContext coroutineContext) {
        j.checkParameterIsNotNull(coroutineLiveData, "target");
        j.checkParameterIsNotNull(coroutineContext, "context");
        this.target = coroutineLiveData;
        x xVar = h0.a;
        this.coroutineContext = coroutineContext.plus(l.b.t());
    }

    public Object emit(T t, Continuation<? super Unit> continuation) {
        return g.j0(this.coroutineContext, new LiveDataScopeImpl$emit$2(this, t, null), continuation);
    }

    public Object emitSource(LiveData<T> liveData, Continuation<? super i0> continuation) {
        return g.j0(this.coroutineContext, new LiveDataScopeImpl$emitSource$2(this, liveData, null), continuation);
    }

    public T getLatestValue() {
        return this.target.getValue();
    }

    public final CoroutineLiveData<T> getTarget$lifecycle_livedata_ktx_release() {
        return this.target;
    }

    public final void setTarget$lifecycle_livedata_ktx_release(CoroutineLiveData<T> coroutineLiveData) {
        j.checkParameterIsNotNull(coroutineLiveData, "<set-?>");
        this.target = coroutineLiveData;
    }
}
