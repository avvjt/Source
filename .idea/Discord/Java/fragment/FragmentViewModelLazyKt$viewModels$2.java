package androidx.fragment.app;

import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import u.m.c.k;

/* compiled from: FragmentViewModelLazy.kt */
public final class FragmentViewModelLazyKt$viewModels$2 extends k implements Function0<ViewModelStore> {
    public final /* synthetic */ Function0 $ownerProducer;

    public FragmentViewModelLazyKt$viewModels$2(Function0 function0) {
        this.$ownerProducer = function0;
        super(0);
    }

    public final ViewModelStore invoke() {
        ViewModelStore viewModelStore = ((ViewModelStoreOwner) this.$ownerProducer.invoke()).getViewModelStore();
        j.checkNotNullExpressionValue(viewModelStore, "ownerProducer().viewModelStore");
        return viewModelStore;
    }
}
