package androidx.fragment.app;

import androidx.lifecycle.ViewModelProvider.Factory;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import u.m.c.k;

/* compiled from: FragmentViewModelLazy.kt */
public final class FragmentViewModelLazyKt$activityViewModels$2 extends k implements Function0<Factory> {
    public final /* synthetic */ Fragment $this_activityViewModels;

    public FragmentViewModelLazyKt$activityViewModels$2(Fragment fragment) {
        this.$this_activityViewModels = fragment;
        super(0);
    }

    public final Factory invoke() {
        FragmentActivity requireActivity = this.$this_activityViewModels.requireActivity();
        j.checkNotNullExpressionValue(requireActivity, "requireActivity()");
        return requireActivity.getDefaultViewModelProviderFactory();
    }
}
