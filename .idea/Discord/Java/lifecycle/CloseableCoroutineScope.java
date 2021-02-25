package androidx.lifecycle;

import java.io.Closeable;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import u.m.c.j;
import v.a.g0;

/* compiled from: ViewModel.kt */
public final class CloseableCoroutineScope implements Closeable, CoroutineScope {
    private final CoroutineContext coroutineContext;

    public CloseableCoroutineScope(CoroutineContext coroutineContext) {
        j.checkNotNullParameter(coroutineContext, "context");
        this.coroutineContext = coroutineContext;
    }

    public void close() {
        g0.f(getCoroutineContext(), null, 1, null);
    }

    public CoroutineContext getCoroutineContext() {
        return this.coroutineContext;
    }
}
