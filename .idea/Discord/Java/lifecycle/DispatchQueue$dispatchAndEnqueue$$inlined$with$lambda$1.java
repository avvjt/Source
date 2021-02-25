package androidx.lifecycle;

import kotlin.coroutines.CoroutineContext;

/* compiled from: DispatchQueue.kt */
public final class DispatchQueue$dispatchAndEnqueue$$inlined$with$lambda$1 implements Runnable {
    public final /* synthetic */ CoroutineContext $context$inlined;
    public final /* synthetic */ Runnable $runnable$inlined;
    public final /* synthetic */ DispatchQueue this$0;

    public DispatchQueue$dispatchAndEnqueue$$inlined$with$lambda$1(DispatchQueue dispatchQueue, CoroutineContext coroutineContext, Runnable runnable) {
        this.this$0 = dispatchQueue;
        this.$context$inlined = coroutineContext;
        this.$runnable$inlined = runnable;
    }

    public final void run() {
        this.this$0.enqueue(this.$runnable$inlined);
    }
}
