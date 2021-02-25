package androidx.fragment.app;

import androidx.annotation.MainThread;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelLazy;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import kotlin.Lazy;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import u.q.b;

/* compiled from: FragmentViewModelLazy.kt */
public final class FragmentViewModelLazyKt {
    @MainThread
    public static final /* synthetic */ <VM extends ViewModel> Lazy<VM> activityViewModels(Fragment fragment, Function0<? extends Factory> function0) {
        j.checkNotNullParameter(fragment, "$this$activityViewModels");
        j.reifiedOperationMarker();
        throw null;
    }

    public static /* synthetic */ Lazy activityViewModels$default(Fragment fragment, Function0 function0, int i, Object obj) {
        int i2 = i & 1;
        j.checkNotNullParameter(fragment, "$this$activityViewModels");
        j.reifiedOperationMarker();
        throw null;
    }

    @MainThread
    public static final <VM extends ViewModel> Lazy<VM> createViewModelLazy(Fragment fragment, b<VM> bVar, Function0<? extends ViewModelStore> function0, Function0<? extends Factory> function02) {
        Function0 function022;
        j.checkNotNullParameter(fragment, "$this$createViewModelLazy");
        j.checkNotNullParameter(bVar, "viewModelClass");
        j.checkNotNullParameter(function0, "storeProducer");
        if (function022 == null) {
            function022 = new FragmentViewModelLazyKt$createViewModelLazy$factoryPromise$1(fragment);
        }
        return new ViewModelLazy(bVar, function0, function022);
    }

    public static /* synthetic */ Lazy createViewModelLazy$default(Fragment fragment, b bVar, Function0 function0, Function0 function02, int i, Object obj) {
        if ((i & 4) != 0) {
            function02 = null;
        }
        return createViewModelLazy(fragment, bVar, function0, function02);
    }

    @MainThread
    public static final /* synthetic */ <VM extends ViewModel> Lazy<VM> viewModels(Fragment fragment, Function0<? extends ViewModelStoreOwner> function0, Function0<? extends Factory> function02) {
        j.checkNotNullParameter(fragment, "$this$viewModels");
        j.checkNotNullParameter(function0, "ownerProducer");
        j.reifiedOperationMarker();
        throw null;
    }

    public static /* synthetic */ Lazy viewModels$default(Fragment fragment, Function0 function0, Function0 function02, int i, Object obj) {
        Object function03;
        if ((i & 1) != 0) {
            function03 = new FragmentViewModelLazyKt$viewModels$1(fragment);
        }
        int i2 = i & 2;
        j.checkNotNullParameter(fragment, "$this$viewModels");
        j.checkNotNullParameter(function03, "ownerProducer");
        j.reifiedOperationMarker();
        throw null;
    }
}
