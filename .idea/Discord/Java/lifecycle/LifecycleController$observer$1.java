package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import kotlinx.coroutines.Job;
import u.m.c.j;
import v.a.g0;

/* compiled from: LifecycleController.kt */
public final class LifecycleController$observer$1 implements LifecycleEventObserver {
    public final /* synthetic */ Job $parentJob;
    public final /* synthetic */ LifecycleController this$0;

    public LifecycleController$observer$1(LifecycleController lifecycleController, Job job) {
        this.this$0 = lifecycleController;
        this.$parentJob = job;
    }

    public final void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
        j.checkNotNullParameter(lifecycleOwner, "source");
        j.checkNotNullParameter(event, "<anonymous parameter 1>");
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        String str = "source.lifecycle";
        j.checkNotNullExpressionValue(lifecycle, str);
        if (lifecycle.getCurrentState() == State.DESTROYED) {
            LifecycleController lifecycleController = this.this$0;
            g0.g(this.$parentJob, null, 1, null);
            lifecycleController.finish();
            return;
        }
        Lifecycle lifecycle2 = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle2, str);
        if (lifecycle2.getCurrentState().compareTo(this.this$0.minState) < 0) {
            this.this$0.dispatchQueue.pause();
        } else {
            this.this$0.dispatchQueue.resume();
        }
    }
}
