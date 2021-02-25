package androidx.lifecycle;

import androidx.annotation.MainThread;
import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import u.m.c.j;
import v.a.g0;
import v.a.h0;
import v.a.t1.l;
import v.a.x;

/* compiled from: CoroutineLiveData.kt */
public final class BlockRunner<T> {
    private final Function2<LiveDataScope<T>, Continuation<? super Unit>, Object> block;
    private Job cancellationJob;
    private final CoroutineLiveData<T> liveData;
    private final Function0<Unit> onDone;
    private Job runningJob;
    private final CoroutineScope scope;
    private final long timeoutInMs;

    public BlockRunner(CoroutineLiveData<T> coroutineLiveData, Function2<? super LiveDataScope<T>, ? super Continuation<? super Unit>, ? extends Object> function2, long j, CoroutineScope coroutineScope, Function0<Unit> function0) {
        j.checkParameterIsNotNull(coroutineLiveData, "liveData");
        j.checkParameterIsNotNull(function2, "block");
        j.checkParameterIsNotNull(coroutineScope, "scope");
        j.checkParameterIsNotNull(function0, "onDone");
        this.liveData = coroutineLiveData;
        this.block = function2;
        this.timeoutInMs = j;
        this.scope = coroutineScope;
        this.onDone = function0;
    }

    @MainThread
    public final void cancel() {
        if (this.cancellationJob == null) {
            CoroutineScope coroutineScope = this.scope;
            x xVar = h0.a;
            this.cancellationJob = g.N(coroutineScope, l.b.t(), null, new BlockRunner$cancel$1(this, null), 2, null);
            return;
        }
        throw new IllegalStateException("Cancel call cannot happen without a maybeRun".toString());
    }

    @MainThread
    public final void maybeRun() {
        Job job = this.cancellationJob;
        if (job != null) {
            g0.g(job, null, 1, null);
        }
        this.cancellationJob = null;
        if (this.runningJob == null) {
            this.runningJob = g.N(this.scope, null, null, new BlockRunner$maybeRun$1(this, null), 3, null);
        }
    }
}
