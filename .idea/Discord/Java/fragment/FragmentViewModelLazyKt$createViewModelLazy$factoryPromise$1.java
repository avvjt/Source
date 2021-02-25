package androidx.fragment.app;

import androidx.lifecycle.ViewModelProvider.Factory;
import kotlin.jvm.functions.Function0;
import u.m.c.k;

/* compiled from: FragmentViewModelLazy.kt */
public final class FragmentViewModelLazyKt$createViewModelLazy$factoryPromise$1 extends k implements Function0<Factory> {
    public final /* synthetic */ Fragment $this_createViewModelLazy;

    public FragmentViewModelLazyKt$createViewModelLazy$factoryPromise$1(Fragment fragment) {
        this.$this_createViewModelLazy = fragment;
        super(0);
    }

    public final Factory invoke() {
        return this.$this_createViewModelLazy.getDefaultViewModelProviderFactory();
    }
}
