package androidx.lifecycle;

import androidx.annotation.MainThread;
import androidx.lifecycle.Lifecycle.State;
import kotlinx.coroutines.Job;
import u.m.c.j;
import v.a.g0;

@MainThread
/* compiled from: LifecycleController.kt */
public final class LifecycleController {
    private final DispatchQueue dispatchQueue;
    private final Lifecycle lifecycle;
    private final State minState;
    private final LifecycleEventObserver observer;

    public LifecycleController(Lifecycle lifecycle, State state, DispatchQueue dispatchQueue, Job job) {
        j.checkNotNullParameter(lifecycle, "lifecycle");
        j.checkNotNullParameter(state, "minState");
        j.checkNotNullParameter(dispatchQueue, "dispatchQueue");
        j.checkNotNullParameter(job, "parentJob");
        this.lifecycle = lifecycle;
        this.minState = state;
        this.dispatchQueue = dispatchQueue;
        LifecycleController$observer$1 lifecycleController$observer$1 = new LifecycleController$observer$1(this, job);
        this.observer = lifecycleController$observer$1;
        if (lifecycle.getCurrentState() == State.DESTROYED) {
            g0.g(job, null, 1, null);
            finish();
            return;
        }
        lifecycle.addObserver(lifecycleController$observer$1);
    }

    private final void handleDestroy(Job job) {
        g0.g(job, null, 1, null);
        finish();
    }

    @MainThread
    public final void finish() {
        this.lifecycle.removeObserver(this.observer);
        this.dispatchQueue.finish();
    }
}
