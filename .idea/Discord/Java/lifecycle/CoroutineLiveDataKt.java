package androidx.lifecycle;

import androidx.annotation.RequiresApi;
import f.i.a.f.f.o.g;
import java.time.Duration;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import u.m.c.j;
import v.a.h0;
import v.a.t1.l;
import v.a.x;

/* compiled from: CoroutineLiveData.kt */
public final class CoroutineLiveDataKt {
    public static final long DEFAULT_TIMEOUT = 5000;

    public static final <T> Object addDisposableSource(MediatorLiveData<T> mediatorLiveData, LiveData<T> liveData, Continuation<? super EmittedSource> continuation) {
        x xVar = h0.a;
        return g.j0(l.b.t(), new CoroutineLiveDataKt$addDisposableSource$2(mediatorLiveData, liveData, null), continuation);
    }

    public static final <T> LiveData<T> liveData(CoroutineContext coroutineContext, long j, Function2<? super LiveDataScope<T>, ? super Continuation<? super Unit>, ? extends Object> function2) {
        j.checkParameterIsNotNull(coroutineContext, "context");
        j.checkParameterIsNotNull(function2, "block");
        return new CoroutineLiveData(coroutineContext, j, function2);
    }

    @RequiresApi(26)
    public static final <T> LiveData<T> liveData(CoroutineContext coroutineContext, Duration duration, Function2<? super LiveDataScope<T>, ? super Continuation<? super Unit>, ? extends Object> function2) {
        j.checkParameterIsNotNull(coroutineContext, "context");
        j.checkParameterIsNotNull(duration, "timeout");
        j.checkParameterIsNotNull(function2, "block");
        return new CoroutineLiveData(coroutineContext, duration.toMillis(), function2);
    }
}
