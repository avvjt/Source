package androidx.activity.result;

import kotlin.jvm.functions.Function1;

/* compiled from: ActivityResultCaller.kt */
public final class ActivityResultCallerKt$registerForActivityResult$resultLauncher$1<O> implements ActivityResultCallback<O> {
    public final /* synthetic */ Function1 $callback;

    public ActivityResultCallerKt$registerForActivityResult$resultLauncher$1(Function1 function1) {
        this.$callback = function1;
    }

    public final void onActivityResult(O o) {
        this.$callback.invoke(o);
    }
}
