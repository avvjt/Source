package androidx.lifecycle;

import android.annotation.SuppressLint;
import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import java.util.ArrayDeque;
import java.util.Queue;
import kotlin.coroutines.CoroutineContext;
import u.m.c.j;
import v.a.e1;
import v.a.h0;
import v.a.t1.l;
import v.a.x;

/* compiled from: DispatchQueue.kt */
public final class DispatchQueue {
    private boolean finished;
    private boolean isDraining;
    private boolean paused = true;
    private final Queue<Runnable> queue = new ArrayDeque();

    @MainThread
    private final void enqueue(Runnable runnable) {
        if (this.queue.offer(runnable)) {
            drainQueue();
            return;
        }
        throw new IllegalStateException("cannot enqueue any more runnables".toString());
    }

    @MainThread
    public final boolean canRun() {
        return this.finished || !this.paused;
    }

    @SuppressLint({"WrongThread"})
    @AnyThread
    public final void dispatchAndEnqueue(CoroutineContext coroutineContext, Runnable runnable) {
        j.checkNotNullParameter(coroutineContext, "context");
        j.checkNotNullParameter(runnable, "runnable");
        x xVar = h0.a;
        e1 t = l.b.t();
        if (t.isDispatchNeeded(coroutineContext) || canRun()) {
            t.dispatch(coroutineContext, new DispatchQueue$dispatchAndEnqueue$$inlined$with$lambda$1(this, coroutineContext, runnable));
        } else {
            enqueue(runnable);
        }
    }

    @MainThread
    public final void drainQueue() {
        if (!this.isDraining) {
            try {
                this.isDraining = true;
                while ((this.queue.isEmpty() ^ 1) != 0) {
                    if (!canRun()) {
                        break;
                    }
                    Runnable runnable = (Runnable) this.queue.poll();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                this.isDraining = false;
            } catch (Throwable th) {
                this.isDraining = false;
            }
        }
    }

    @MainThread
    public final void finish() {
        this.finished = true;
        drainQueue();
    }

    @MainThread
    public final void pause() {
        this.paused = true;
    }

    @MainThread
    public final void resume() {
        if (!this.paused) {
            return;
        }
        if ((this.finished ^ 1) != 0) {
            this.paused = false;
            drainQueue();
            return;
        }
        throw new IllegalStateException("Cannot resume a finished dispatcher".toString());
    }
}
