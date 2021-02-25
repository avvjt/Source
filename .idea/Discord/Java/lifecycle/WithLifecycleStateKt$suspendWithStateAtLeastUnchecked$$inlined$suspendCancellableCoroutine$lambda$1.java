package androidx.lifecycle;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import v.a.g;
import v.a.x;

/* compiled from: WithLifecycleState.kt */
public final class WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1 implements LifecycleEventObserver {
    public final /* synthetic */ Function0 $block$inlined;
    public final /* synthetic */ g $co;
    public final /* synthetic */ boolean $dispatchNeeded$inlined;
    public final /* synthetic */ x $lifecycleDispatcher$inlined;
    public final /* synthetic */ State $state$inlined;
    public final /* synthetic */ Lifecycle $this_suspendWithStateAtLeastUnchecked$inlined;

    public WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1(g gVar, Lifecycle lifecycle, State state, Function0 function0, boolean z, x xVar) {
        this.$co = gVar;
        this.$this_suspendWithStateAtLeastUnchecked$inlined = lifecycle;
        this.$state$inlined = state;
        this.$block$inlined = function0;
        this.$dispatchNeeded$inlined = z;
        this.$lifecycleDispatcher$inlined = xVar;
    }

    public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
        j.checkNotNullParameter(lifecycleOwner, "source");
        j.checkNotNullParameter(event, NotificationCompat.CATEGORY_EVENT);
        if (event == Event.upTo(this.$state$inlined)) {
            Object invoke;
            this.$this_suspendWithStateAtLeastUnchecked$inlined.removeObserver(this);
            g gVar = this.$co;
            try {
                invoke = this.$block$inlined.invoke();
            } catch (Throwable th) {
                invoke = f.i.a.f.f.o.g.createFailure(th);
            }
            gVar.resumeWith(invoke);
        } else if (event == Event.ON_DESTROY) {
            this.$this_suspendWithStateAtLeastUnchecked$inlined.removeObserver(this);
            this.$co.resumeWith(f.i.a.f.f.o.g.createFailure(new LifecycleDestroyedException()));
        }
    }
}
