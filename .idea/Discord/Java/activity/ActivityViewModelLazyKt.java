package androidx.activity;

import androidx.annotation.MainThread;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;
import kotlin.Lazy;
import kotlin.jvm.functions.Function0;
import u.m.c.j;

/* compiled from: ActivityViewModelLazy.kt */
public final class ActivityViewModelLazyKt {
    @MainThread
    public static final /* synthetic */ <VM extends ViewModel> Lazy<VM> viewModels(ComponentActivity componentActivity, Function0<? extends Factory> function0) {
        j.checkNotNullParameter(componentActivity, "$this$viewModels");
        if (function0 == null) {
            ActivityViewModelLazyKt$viewModels$factoryPromise$1 activityViewModelLazyKt$viewModels$factoryPromise$1 = new ActivityViewModelLazyKt$viewModels$factoryPromise$1(componentActivity);
        }
        j.reifiedOperationMarker();
        throw null;
    }

    public static /* synthetic */ Lazy viewModels$default(ComponentActivity componentActivity, Function0 function0, int i, Object obj) {
        if ((i & 1) != 0) {
            function0 = null;
        }
        j.checkNotNullParameter(componentActivity, "$this$viewModels");
        if (function0 == null) {
            ActivityViewModelLazyKt$viewModels$factoryPromise$1 activityViewModelLazyKt$viewModels$factoryPromise$1 = new ActivityViewModelLazyKt$viewModels$factoryPromise$1(componentActivity);
        }
        j.reifiedOperationMarker();
        throw null;
    }
}
