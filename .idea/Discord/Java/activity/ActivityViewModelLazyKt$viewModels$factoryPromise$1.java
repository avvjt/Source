package androidx.activity;

import androidx.lifecycle.ViewModelProvider.Factory;
import kotlin.jvm.functions.Function0;
import u.m.c.k;

/* compiled from: ActivityViewModelLazy.kt */
public final class ActivityViewModelLazyKt$viewModels$factoryPromise$1 extends k implements Function0<Factory> {
    public final /* synthetic */ ComponentActivity $this_viewModels;

    public ActivityViewModelLazyKt$viewModels$factoryPromise$1(ComponentActivity componentActivity) {
        this.$this_viewModels = componentActivity;
        super(0);
    }

    public final Factory invoke() {
        return this.$this_viewModels.getDefaultViewModelProviderFactory();
    }
}
