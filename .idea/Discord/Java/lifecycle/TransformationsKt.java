package androidx.lifecycle;

import kotlin.jvm.functions.Function1;
import u.m.c.j;

/* compiled from: Transformations.kt */
public final class TransformationsKt {
    public static final <X> LiveData<X> distinctUntilChanged(LiveData<X> liveData) {
        j.checkParameterIsNotNull(liveData, "$this$distinctUntilChanged");
        LiveData distinctUntilChanged = Transformations.distinctUntilChanged(liveData);
        j.checkExpressionValueIsNotNull(distinctUntilChanged, "Transformations.distinctUntilChanged(this)");
        return distinctUntilChanged;
    }

    public static final <X, Y> LiveData<Y> map(LiveData<X> liveData, Function1<? super X, ? extends Y> function1) {
        j.checkParameterIsNotNull(liveData, "$this$map");
        j.checkParameterIsNotNull(function1, "transform");
        LiveData map = Transformations.map(liveData, new TransformationsKt$map$1(function1));
        j.checkExpressionValueIsNotNull(map, "Transformations.map(this) { transform(it) }");
        return map;
    }

    public static final <X, Y> LiveData<Y> switchMap(LiveData<X> liveData, Function1<? super X, ? extends LiveData<Y>> function1) {
        j.checkParameterIsNotNull(liveData, "$this$switchMap");
        j.checkParameterIsNotNull(function1, "transform");
        LiveData switchMap = Transformations.switchMap(liveData, new TransformationsKt$switchMap$1(function1));
        j.checkExpressionValueIsNotNull(switchMap, "Transformations.switchMap(this) { transform(it) }");
        return switchMap;
    }
}
