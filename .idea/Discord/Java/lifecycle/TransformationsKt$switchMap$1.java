package androidx.lifecycle;

import androidx.arch.core.util.Function;
import kotlin.jvm.functions.Function1;

/* compiled from: Transformations.kt */
public final class TransformationsKt$switchMap$1<I, O> implements Function<X, LiveData<Y>> {
    public final /* synthetic */ Function1 $transform;

    public TransformationsKt$switchMap$1(Function1 function1) {
        this.$transform = function1;
    }

    public final LiveData<Y> apply(X x) {
        return (LiveData) this.$transform.invoke(x);
    }
}
