package androidx.lifecycle;

import android.annotation.SuppressLint;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.internal.FastSafeIterableMap;
import androidx.arch.core.internal.SafeIterableMap.IteratorWithAdditions;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import f.d.b.a.a;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class LifecycleRegistry extends Lifecycle {
    private int mAddingObserverCounter;
    private final boolean mEnforceMainThread;
    private boolean mHandlingEvent;
    private final WeakReference<LifecycleOwner> mLifecycleOwner;
    private boolean mNewEventOccurred;
    private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap;
    private ArrayList<State> mParentStates;
    private State mState;

    public static class ObserverWithState {
        public LifecycleEventObserver mLifecycleObserver;
        public State mState;

        public ObserverWithState(LifecycleObserver lifecycleObserver, State state) {
            this.mLifecycleObserver = Lifecycling.lifecycleEventObserver(lifecycleObserver);
            this.mState = state;
        }

        public void dispatchEvent(LifecycleOwner lifecycleOwner, Event event) {
            State targetState = event.getTargetState();
            this.mState = LifecycleRegistry.min(this.mState, targetState);
            this.mLifecycleObserver.onStateChanged(lifecycleOwner, event);
            this.mState = targetState;
        }
    }

    public LifecycleRegistry(@NonNull LifecycleOwner lifecycleOwner) {
        this(lifecycleOwner, true);
    }

    private void backwardPass(LifecycleOwner lifecycleOwner) {
        Iterator descendingIterator = this.mObserverMap.descendingIterator();
        while (descendingIterator.hasNext() && !this.mNewEventOccurred) {
            Entry entry = (Entry) descendingIterator.next();
            ObserverWithState observerWithState = (ObserverWithState) entry.getValue();
            while (observerWithState.mState.compareTo(this.mState) > 0 && !this.mNewEventOccurred && this.mObserverMap.contains(entry.getKey())) {
                Event downFrom = Event.downFrom(observerWithState.mState);
                if (downFrom != null) {
                    pushParentState(downFrom.getTargetState());
                    observerWithState.dispatchEvent(lifecycleOwner, downFrom);
                    popParentState();
                } else {
                    StringBuilder L = a.L("no event down from ");
                    L.append(observerWithState.mState);
                    throw new IllegalStateException(L.toString());
                }
            }
        }
    }

    private State calculateTargetState(LifecycleObserver lifecycleObserver) {
        Entry ceil = this.mObserverMap.ceil(lifecycleObserver);
        State state = null;
        State state2 = ceil != null ? ((ObserverWithState) ceil.getValue()).mState : null;
        if (!this.mParentStates.isEmpty()) {
            ArrayList arrayList = this.mParentStates;
            state = (State) arrayList.get(arrayList.size() - 1);
        }
        return min(min(this.mState, state2), state);
    }

    @VisibleForTesting
    @NonNull
    public static LifecycleRegistry createUnsafe(@NonNull LifecycleOwner lifecycleOwner) {
        return new LifecycleRegistry(lifecycleOwner, false);
    }

    @SuppressLint({"RestrictedApi"})
    private void enforceMainThreadIfNeeded(String str) {
        if (this.mEnforceMainThread && !ArchTaskExecutor.getInstance().isMainThread()) {
            throw new IllegalStateException(a.u("Method ", str, " must be called on the main thread"));
        }
    }

    private void forwardPass(LifecycleOwner lifecycleOwner) {
        IteratorWithAdditions iteratorWithAdditions = this.mObserverMap.iteratorWithAdditions();
        while (iteratorWithAdditions.hasNext() && !this.mNewEventOccurred) {
            Entry entry = (Entry) iteratorWithAdditions.next();
            ObserverWithState observerWithState = (ObserverWithState) entry.getValue();
            while (observerWithState.mState.compareTo(this.mState) < 0 && !this.mNewEventOccurred && this.mObserverMap.contains(entry.getKey())) {
                pushParentState(observerWithState.mState);
                Event upFrom = Event.upFrom(observerWithState.mState);
                if (upFrom != null) {
                    observerWithState.dispatchEvent(lifecycleOwner, upFrom);
                    popParentState();
                } else {
                    StringBuilder L = a.L("no event up from ");
                    L.append(observerWithState.mState);
                    throw new IllegalStateException(L.toString());
                }
            }
        }
    }

    private boolean isSynced() {
        boolean z = true;
        if (this.mObserverMap.size() == 0) {
            return true;
        }
        State state = ((ObserverWithState) this.mObserverMap.eldest().getValue()).mState;
        State state2 = ((ObserverWithState) this.mObserverMap.newest().getValue()).mState;
        if (!(state == state2 && this.mState == state2)) {
            z = false;
        }
        return z;
    }

    public static State min(@NonNull State state, @Nullable State state2) {
        return (state2 == null || state2.compareTo(state) >= 0) ? state : state2;
    }

    private void moveToState(State state) {
        if (this.mState != state) {
            this.mState = state;
            if (this.mHandlingEvent || this.mAddingObserverCounter != 0) {
                this.mNewEventOccurred = true;
                return;
            }
            this.mHandlingEvent = true;
            sync();
            this.mHandlingEvent = false;
        }
    }

    private void popParentState() {
        ArrayList arrayList = this.mParentStates;
        arrayList.remove(arrayList.size() - 1);
    }

    private void pushParentState(State state) {
        this.mParentStates.add(state);
    }

    private void sync() {
        LifecycleOwner lifecycleOwner = (LifecycleOwner) this.mLifecycleOwner.get();
        if (lifecycleOwner != null) {
            while (!isSynced()) {
                this.mNewEventOccurred = false;
                if (this.mState.compareTo(((ObserverWithState) this.mObserverMap.eldest().getValue()).mState) < 0) {
                    backwardPass(lifecycleOwner);
                }
                Entry newest = this.mObserverMap.newest();
                if (!(this.mNewEventOccurred || newest == null || this.mState.compareTo(((ObserverWithState) newest.getValue()).mState) <= 0)) {
                    forwardPass(lifecycleOwner);
                }
            }
            this.mNewEventOccurred = false;
            return;
        }
        throw new IllegalStateException("LifecycleOwner of this LifecycleRegistry is alreadygarbage collected. It is too late to change lifecycle state.");
    }

    public void addObserver(@NonNull LifecycleObserver lifecycleObserver) {
        enforceMainThreadIfNeeded("addObserver");
        State state = this.mState;
        State state2 = State.DESTROYED;
        if (state != state2) {
            state2 = State.INITIALIZED;
        }
        ObserverWithState observerWithState = new ObserverWithState(lifecycleObserver, state2);
        if (((ObserverWithState) this.mObserverMap.putIfAbsent(lifecycleObserver, observerWithState)) == null) {
            LifecycleOwner lifecycleOwner = (LifecycleOwner) this.mLifecycleOwner.get();
            if (lifecycleOwner != null) {
                Object obj = (this.mAddingObserverCounter != 0 || this.mHandlingEvent) ? 1 : null;
                Enum calculateTargetState = calculateTargetState(lifecycleObserver);
                this.mAddingObserverCounter++;
                while (observerWithState.mState.compareTo(calculateTargetState) < 0 && this.mObserverMap.contains(lifecycleObserver)) {
                    pushParentState(observerWithState.mState);
                    Event upFrom = Event.upFrom(observerWithState.mState);
                    if (upFrom != null) {
                        observerWithState.dispatchEvent(lifecycleOwner, upFrom);
                        popParentState();
                        calculateTargetState = calculateTargetState(lifecycleObserver);
                    } else {
                        StringBuilder L = a.L("no event up from ");
                        L.append(observerWithState.mState);
                        throw new IllegalStateException(L.toString());
                    }
                }
                if (obj == null) {
                    sync();
                }
                this.mAddingObserverCounter--;
            }
        }
    }

    @NonNull
    public State getCurrentState() {
        return this.mState;
    }

    public int getObserverCount() {
        enforceMainThreadIfNeeded("getObserverCount");
        return this.mObserverMap.size();
    }

    public void handleLifecycleEvent(@NonNull Event event) {
        enforceMainThreadIfNeeded("handleLifecycleEvent");
        moveToState(event.getTargetState());
    }

    @MainThread
    @Deprecated
    public void markState(@NonNull State state) {
        enforceMainThreadIfNeeded("markState");
        setCurrentState(state);
    }

    public void removeObserver(@NonNull LifecycleObserver lifecycleObserver) {
        enforceMainThreadIfNeeded("removeObserver");
        this.mObserverMap.remove(lifecycleObserver);
    }

    @MainThread
    public void setCurrentState(@NonNull State state) {
        enforceMainThreadIfNeeded("setCurrentState");
        moveToState(state);
    }

    private LifecycleRegistry(@NonNull LifecycleOwner lifecycleOwner, boolean z) {
        this.mObserverMap = new FastSafeIterableMap();
        this.mAddingObserverCounter = 0;
        this.mHandlingEvent = false;
        this.mNewEventOccurred = false;
        this.mParentStates = new ArrayList();
        this.mLifecycleOwner = new WeakReference(lifecycleOwner);
        this.mState = State.INITIALIZED;
        this.mEnforceMainThread = z;
    }
}
