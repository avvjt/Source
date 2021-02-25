package androidx.lifecycle;

import androidx.arch.core.util.Function;
import kotlin.jvm.functions.Function1;

/* compiled from: Transformations.kt */
public final class TransformationsKt$map$1<I, O> implements Function<X, Y> {
    public final /* synthetic */ Function1 $transform;

    public TransformationsKt$map$1(Function1 function1) {
        this.$transform = function1;
    }

    public final Y apply(X x) {
        return this.$transform.invoke(x);
    }
}
