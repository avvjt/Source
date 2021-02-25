package androidx.lifecycle;

import kotlin.coroutines.CoroutineContext.a.a;
import kotlinx.coroutines.CoroutineScope;
import u.m.c.j;
import v.a.b1;
import v.a.g0;
import v.a.h0;
import v.a.q;
import v.a.t1.l;
import v.a.x;

/* compiled from: ViewModel.kt */
public final class ViewModelKt {
    private static final String JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.JOB_KEY";

    public static final CoroutineScope getViewModelScope(ViewModel viewModel) {
        j.checkNotNullParameter(viewModel, "$this$viewModelScope");
        String str = JOB_KEY;
        CoroutineScope coroutineScope = (CoroutineScope) viewModel.getTag(str);
        if (coroutineScope != null) {
            return coroutineScope;
        }
        q a = g0.a(null, 1);
        x xVar = h0.a;
        Object tagIfAbsent = viewModel.setTagIfAbsent(str, new CloseableCoroutineScope(a.plus((b1) a, l.b.t())));
        j.checkNotNullExpressionValue(tagIfAbsent, "setTagIfAbsent(\n        …Main.immediate)\n        )");
        return (CoroutineScope) tagIfAbsent;
    }
}
