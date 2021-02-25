package androidx.lifecycle;

import androidx.annotation.RequiresApi;
import java.time.Duration;
import kotlin.coroutines.CoroutineContext;
import u.m.c.j;
import v.a.s1.b;
import v.a.s1.d;

/* compiled from: FlowLiveData.kt */
public final class FlowLiveDataConversions {
    public static final <T> b<T> asFlow(LiveData<T> liveData) {
        j.checkParameterIsNotNull(liveData, "$this$asFlow");
        return new d(new FlowLiveDataConversions$asFlow$1(liveData, null));
    }

    public static final <T> LiveData<T> asLiveData(b<? extends T> bVar) {
        return asLiveData$default((b) bVar, null, 0, 3, null);
    }

    public static final <T> LiveData<T> asLiveData(b<? extends T> bVar, CoroutineContext coroutineContext) {
        return asLiveData$default((b) bVar, coroutineContext, 0, 2, null);
    }

    public static final <T> LiveData<T> asLiveData(b<? extends T> bVar, CoroutineContext coroutineContext, long j) {
        j.checkParameterIsNotNull(bVar, "$this$asLiveData");
        j.checkParameterIsNotNull(coroutineContext, "context");
        return CoroutineLiveDataKt.liveData(coroutineContext, j, new FlowLiveDataConversions$asLiveData$1(bVar, null));
    }

    @RequiresApi(26)
    public static final <T> LiveData<T> asLiveData(b<? extends T> bVar, CoroutineContext coroutineContext, Duration duration) {
        j.checkParameterIsNotNull(bVar, "$this$asLiveData");
        j.checkParameterIsNotNull(coroutineContext, "context");
        j.checkParameterIsNotNull(duration, "timeout");
        return asLiveData((b) bVar, coroutineContext, duration.toMillis());
    }
}
