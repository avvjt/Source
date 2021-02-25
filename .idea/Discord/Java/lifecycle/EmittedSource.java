package androidx.lifecycle;

import androidx.annotation.MainThread;
import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import u.m.c.j;
import v.a.h0;
import v.a.i0;
import v.a.t1.l;
import v.a.x;

/* compiled from: CoroutineLiveData.kt */
public final class EmittedSource implements i0 {
    private boolean disposed;
    private final MediatorLiveData<?> mediator;
    private final LiveData<?> source;

    public EmittedSource(LiveData<?> liveData, MediatorLiveData<?> mediatorLiveData) {
        j.checkParameterIsNotNull(liveData, "source");
        j.checkParameterIsNotNull(mediatorLiveData, "mediator");
        this.source = liveData;
        this.mediator = mediatorLiveData;
    }

    @MainThread
    private final void removeSource() {
        if (!this.disposed) {
            this.mediator.removeSource(this.source);
            this.disposed = true;
        }
    }

    public void dispose() {
        x xVar = h0.a;
        g.N(g.a(l.b.t()), null, null, new EmittedSource$dispose$1(this, null), 3, null);
    }

    public final Object disposeNow(Continuation<? super Unit> continuation) {
        x xVar = h0.a;
        return g.j0(l.b.t(), new EmittedSource$disposeNow$2(this, null), continuation);
    }
}
