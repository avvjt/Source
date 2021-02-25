package androidx.lifecycle;

import kotlin.jvm.functions.Function1;

/* compiled from: LiveData.kt */
public final class LiveDataKt$observe$wrappedObserver$1<T> implements Observer<T> {
    public final /* synthetic */ Function1 $onChanged;

    public LiveDataKt$observe$wrappedObserver$1(Function1 function1) {
        this.$onChanged = function1;
    }

    public final void onChanged(T t) {
        this.$onChanged.invoke(t);
    }
}
