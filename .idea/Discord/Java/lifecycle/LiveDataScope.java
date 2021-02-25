package androidx.lifecycle;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import v.a.i0;

/* compiled from: CoroutineLiveData.kt */
public interface LiveDataScope<T> {
    Object emit(T t, Continuation<? super Unit> continuation);

    Object emitSource(LiveData<T> liveData, Continuation<? super i0> continuation);

    T getLatestValue();
}
