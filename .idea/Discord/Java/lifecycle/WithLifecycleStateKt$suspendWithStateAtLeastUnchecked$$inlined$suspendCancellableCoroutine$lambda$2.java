package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.State;
import kotlin.jvm.functions.Function0;
import v.a.x;

/* compiled from: WithLifecycleState.kt */
public final class WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$2 implements Runnable {
    public final /* synthetic */ Function0 $block$inlined;
    public final /* synthetic */ boolean $dispatchNeeded$inlined;
    public final /* synthetic */ x $lifecycleDispatcher$inlined;
    public final /* synthetic */ WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1 $observer;
    public final /* synthetic */ State $state$inlined;
    public final /* synthetic */ Lifecycle $this_suspendWithStateAtLeastUnchecked$inlined;

    public WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$2(WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1 withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1, Lifecycle lifecycle, State state, Function0 function0, boolean z, x xVar) {
        this.$observer = withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1;
        this.$this_suspendWithStateAtLeastUnchecked$inlined = lifecycle;
        this.$state$inlined = state;
        this.$block$inlined = function0;
        this.$dispatchNeeded$inlined = z;
        this.$lifecycleDispatcher$inlined = xVar;
    }

    public final void run() {
        this.$this_suspendWithStateAtLeastUnchecked$inlined.addObserver(this.$observer);
    }
}
