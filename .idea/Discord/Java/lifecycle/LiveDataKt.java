package androidx.lifecycle;

import androidx.annotation.MainThread;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import u.m.c.j;

/* compiled from: LiveData.kt */
public final class LiveDataKt {
    @MainThread
    public static final <T> Observer<T> observe(LiveData<T> liveData, LifecycleOwner lifecycleOwner, Function1<? super T, Unit> function1) {
        j.checkNotNullParameter(liveData, "$this$observe");
        j.checkNotNullParameter(lifecycleOwner, "owner");
        j.checkNotNullParameter(function1, "onChanged");
        LiveDataKt$observe$wrappedObserver$1 liveDataKt$observe$wrappedObserver$1 = new LiveDataKt$observe$wrappedObserver$1(function1);
        liveData.observe(lifecycleOwner, liveDataKt$observe$wrappedObserver$1);
        return liveDataKt$observe$wrappedObserver$1;
    }
}
