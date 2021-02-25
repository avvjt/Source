package androidx.activity;

import androidx.lifecycle.ViewModelStore;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import u.m.c.k;

/* compiled from: ActivityViewModelLazy.kt */
public final class ActivityViewModelLazyKt$viewModels$1 extends k implements Function0<ViewModelStore> {
    public final /* synthetic */ ComponentActivity $this_viewModels;

    public ActivityViewModelLazyKt$viewModels$1(ComponentActivity componentActivity) {
        this.$this_viewModels = componentActivity;
        super(0);
    }

    public final ViewModelStore invoke() {
        ViewModelStore viewModelStore = this.$this_viewModels.getViewModelStore();
        j.checkNotNullExpressionValue(viewModelStore, "viewModelStore");
        return viewModelStore;
    }
}
