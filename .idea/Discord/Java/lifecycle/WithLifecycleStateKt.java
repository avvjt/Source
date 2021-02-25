package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.State;
import f.i.a.f.f.o.g;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import u.j.f;
import u.j.g.a;
import u.m.c.j;
import v.a.e1;
import v.a.h;
import v.a.h0;
import v.a.t1.l;
import v.a.x;

/* compiled from: WithLifecycleState.kt */
public final class WithLifecycleStateKt {
    public static final <R> Object suspendWithStateAtLeastUnchecked(Lifecycle lifecycle, State state, boolean z, x xVar, Function0<? extends R> function0, Continuation<? super R> continuation) {
        h hVar = new h(g.intercepted(continuation), 1);
        hVar.y();
        WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1 withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1 = new WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1(hVar, lifecycle, state, function0, z, xVar);
        if (z) {
            xVar.dispatch(f.f, new WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$2(withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1, lifecycle, state, function0, z, xVar));
            Lifecycle lifecycle2 = lifecycle;
        } else {
            x xVar2 = xVar;
            lifecycle.addObserver(withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1);
        }
        hVar.c(new WithLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$3(withLifecycleStateKt$suspendWithStateAtLeastUnchecked$$inlined$suspendCancellableCoroutine$lambda$1, lifecycle, state, function0, z, xVar));
        a s = hVar.s();
        if (s == a.COROUTINE_SUSPENDED) {
            j.checkNotNullParameter(continuation, "frame");
        }
        return s;
    }

    public static final <R> Object withCreated(Lifecycle lifecycle, Function0<? extends R> function0, Continuation<? super R> continuation) {
        State state = State.CREATED;
        x xVar = h0.a;
        e1 t = l.b.t();
        boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
        if (!isDispatchNeeded) {
            if (lifecycle.getCurrentState() == State.DESTROYED) {
                throw new LifecycleDestroyedException();
            } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                return function0.invoke();
            }
        }
        return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
    }

    private static final Object withCreated$$forInline(Lifecycle lifecycle, Function0 function0, Continuation continuation) {
        State state = State.CREATED;
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    public static final <R> Object withResumed(Lifecycle lifecycle, Function0<? extends R> function0, Continuation<? super R> continuation) {
        State state = State.RESUMED;
        x xVar = h0.a;
        e1 t = l.b.t();
        boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
        if (!isDispatchNeeded) {
            if (lifecycle.getCurrentState() == State.DESTROYED) {
                throw new LifecycleDestroyedException();
            } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                return function0.invoke();
            }
        }
        return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
    }

    private static final Object withResumed$$forInline(Lifecycle lifecycle, Function0 function0, Continuation continuation) {
        State state = State.RESUMED;
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    public static final <R> Object withStarted(Lifecycle lifecycle, Function0<? extends R> function0, Continuation<? super R> continuation) {
        State state = State.STARTED;
        x xVar = h0.a;
        e1 t = l.b.t();
        boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
        if (!isDispatchNeeded) {
            if (lifecycle.getCurrentState() == State.DESTROYED) {
                throw new LifecycleDestroyedException();
            } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                return function0.invoke();
            }
        }
        return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
    }

    private static final Object withStarted$$forInline(Lifecycle lifecycle, Function0 function0, Continuation continuation) {
        State state = State.STARTED;
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    public static final <R> Object withStateAtLeast(Lifecycle lifecycle, State state, Function0<? extends R> function0, Continuation<? super R> continuation) {
        if ((state.compareTo(State.CREATED) >= 0 ? 1 : null) != null) {
            x xVar = h0.a;
            e1 t = l.b.t();
            boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
            if (!isDispatchNeeded) {
                if (lifecycle.getCurrentState() == State.DESTROYED) {
                    throw new LifecycleDestroyedException();
                } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                    return function0.invoke();
                }
            }
            return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("target state must be CREATED or greater, found ");
        stringBuilder.append(state);
        throw new IllegalArgumentException(stringBuilder.toString().toString());
    }

    private static final Object withStateAtLeast$$forInline(Lifecycle lifecycle, State state, Function0 function0, Continuation continuation) {
        if ((state.compareTo(State.CREATED) >= 0 ? 1 : null) == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("target state must be CREATED or greater, found ");
            stringBuilder.append(state);
            throw new IllegalArgumentException(stringBuilder.toString().toString());
        }
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    public static final <R> Object withStateAtLeastUnchecked(Lifecycle lifecycle, State state, Function0<? extends R> function0, Continuation<? super R> continuation) {
        x xVar = h0.a;
        e1 t = l.b.t();
        boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
        if (!isDispatchNeeded) {
            if (lifecycle.getCurrentState() == State.DESTROYED) {
                throw new LifecycleDestroyedException();
            } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                return function0.invoke();
            }
        }
        return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
    }

    private static final Object withStateAtLeastUnchecked$$forInline(Lifecycle lifecycle, State state, Function0 function0, Continuation continuation) {
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    private static final Object withCreated$$forInline(LifecycleOwner lifecycleOwner, Function0 function0, Continuation continuation) {
        j.checkNotNullExpressionValue(lifecycleOwner.getLifecycle(), "lifecycle");
        State state = State.CREATED;
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    private static final Object withResumed$$forInline(LifecycleOwner lifecycleOwner, Function0 function0, Continuation continuation) {
        j.checkNotNullExpressionValue(lifecycleOwner.getLifecycle(), "lifecycle");
        State state = State.RESUMED;
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    private static final Object withStarted$$forInline(LifecycleOwner lifecycleOwner, Function0 function0, Continuation continuation) {
        j.checkNotNullExpressionValue(lifecycleOwner.getLifecycle(), "lifecycle");
        State state = State.STARTED;
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    private static final Object withStateAtLeast$$forInline(LifecycleOwner lifecycleOwner, State state, Function0 function0, Continuation continuation) {
        j.checkNotNullExpressionValue(lifecycleOwner.getLifecycle(), "lifecycle");
        if ((state.compareTo(State.CREATED) >= 0 ? 1 : null) == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("target state must be CREATED or greater, found ");
            stringBuilder.append(state);
            throw new IllegalArgumentException(stringBuilder.toString().toString());
        }
        x xVar = h0.a;
        l.b.t();
        throw null;
    }

    public static final <R> Object withCreated(LifecycleOwner lifecycleOwner, Function0<? extends R> function0, Continuation<? super R> continuation) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        State state = State.CREATED;
        x xVar = h0.a;
        e1 t = l.b.t();
        boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
        if (!isDispatchNeeded) {
            if (lifecycle.getCurrentState() == State.DESTROYED) {
                throw new LifecycleDestroyedException();
            } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                return function0.invoke();
            }
        }
        return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
    }

    public static final <R> Object withResumed(LifecycleOwner lifecycleOwner, Function0<? extends R> function0, Continuation<? super R> continuation) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        State state = State.RESUMED;
        x xVar = h0.a;
        e1 t = l.b.t();
        boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
        if (!isDispatchNeeded) {
            if (lifecycle.getCurrentState() == State.DESTROYED) {
                throw new LifecycleDestroyedException();
            } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                return function0.invoke();
            }
        }
        return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
    }

    public static final <R> Object withStarted(LifecycleOwner lifecycleOwner, Function0<? extends R> function0, Continuation<? super R> continuation) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        State state = State.STARTED;
        x xVar = h0.a;
        e1 t = l.b.t();
        boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
        if (!isDispatchNeeded) {
            if (lifecycle.getCurrentState() == State.DESTROYED) {
                throw new LifecycleDestroyedException();
            } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                return function0.invoke();
            }
        }
        return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
    }

    public static final <R> Object withStateAtLeast(LifecycleOwner lifecycleOwner, State state, Function0<? extends R> function0, Continuation<? super R> continuation) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        j.checkNotNullExpressionValue(lifecycle, "lifecycle");
        if ((state.compareTo(State.CREATED) >= 0 ? 1 : null) != null) {
            x xVar = h0.a;
            e1 t = l.b.t();
            boolean isDispatchNeeded = t.isDispatchNeeded(continuation.getContext());
            if (!isDispatchNeeded) {
                if (lifecycle.getCurrentState() == State.DESTROYED) {
                    throw new LifecycleDestroyedException();
                } else if (lifecycle.getCurrentState().compareTo(state) >= 0) {
                    return function0.invoke();
                }
            }
            return suspendWithStateAtLeastUnchecked(lifecycle, state, isDispatchNeeded, t, new WithLifecycleStateKt$withStateAtLeastUnchecked$2(function0), continuation);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("target state must be CREATED or greater, found ");
        stringBuilder.append(state);
        throw new IllegalArgumentException(stringBuilder.toString().toString());
    }
}
