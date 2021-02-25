package androidx.lifecycle;

import kotlin.coroutines.CoroutineContext;
import u.m.c.j;
import v.a.h0;
import v.a.t1.l;
import v.a.x;

/* compiled from: PausingDispatcher.kt */
public final class PausingDispatcher extends x {
    public final DispatchQueue dispatchQueue = new DispatchQueue();

    public void dispatch(CoroutineContext coroutineContext, Runnable runnable) {
        j.checkNotNullParameter(coroutineContext, "context");
        j.checkNotNullParameter(runnable, "block");
        this.dispatchQueue.dispatchAndEnqueue(coroutineContext, runnable);
    }

    public boolean isDispatchNeeded(CoroutineContext coroutineContext) {
        j.checkNotNullParameter(coroutineContext, "context");
        x xVar = h0.a;
        if (l.b.t().isDispatchNeeded(coroutineContext)) {
            return true;
        }
        return this.dispatchQueue.canRun() ^ 1;
    }
}
