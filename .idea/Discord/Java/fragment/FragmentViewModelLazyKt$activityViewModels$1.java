package androidx.fragment.app;

import androidx.lifecycle.ViewModelStore;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import u.m.c.k;

/* compiled from: FragmentViewModelLazy.kt */
public final class FragmentViewModelLazyKt$activityViewModels$1 extends k implements Function0<ViewModelStore> {
    public final /* synthetic */ Fragment $this_activityViewModels;

    public FragmentViewModelLazyKt$activityViewModels$1(Fragment fragment) {
        this.$this_activityViewModels = fragment;
        super(0);
    }

    public final ViewModelStore invoke() {
        FragmentActivity requireActivity = this.$this_activityViewModels.requireActivity();
        j.checkNotNullExpressionValue(requireActivity, "requireActivity()");
        ViewModelStore viewModelStore = requireActivity.getViewModelStore();
        j.checkNotNullExpressionValue(viewModelStore, "requireActivity().viewModelStore");
        return viewModelStore;
    }
}
