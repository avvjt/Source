package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.State;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import u.j.f;
import u.m.c.k;
import v.a.x;

/* compiled from: WithLifecycleState.kt */
public final class WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3 extends k implements Function1<Throwable, Unit> {
    public final /* synthetic */ Function0 $block$inlined;
    public final /* synthetic */ boolean $dispatchNeeded$inlined;
    public final /* synthetic */ x $lifecycleDispatcher$inlined;
    public final /* synthetic */ WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1 $observer;
    public final /* synthetic */ State $state$inlined;
    public final /* synthetic */ Lifecycle $this_suspendWithStateAtLeastUnchecked$inlined;

    /* compiled from: WithLifecycleState.kt */
    /* renamed from: androidx.lifecycle.WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3$1 */
    public static final class AnonymousClass1 implements Runnable {
        public final /* synthetic */ WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3 this$0;

        public AnonymousClass1(WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3 withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3) {
            this.this$0 = withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3;
        }

        public final void run() {
            WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3 withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3 = this.this$0;
            withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3.$this_suspendWithStateAtLeastUnchecked$inlined.removeObserver(withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3.$observer);
        }
    }

    public WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3(WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1 withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1, Lifecycle lifecycle, State state, Function0 function0, boolean z, x xVar) {
        this.$observer = withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1;
        this.$this_suspendWithStateAtLeastUnchecked$inlined = lifecycle;
        this.$state$inlined = state;
        this.$block$inlined = function0;
        this.$dispatchNeeded$inlined = z;
        this.$lifecycleDispatcher$inlined = xVar;
        super(1);
    }

    public final void invoke(Throwable th) {
        x xVar = this.$lifecycleDispatcher$inlined;
        f fVar = f.f;
        if (xVar.isDispatchNeeded(fVar)) {
            this.$lifecycleDispatcher$inlined.dispatch(fVar, new AnonymousClass1(this));
        } else {
            this.$this_suspendWithStateAtLeastUnchecked$inlined.removeObserver(this.$observer);
        }
    }
}
