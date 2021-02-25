package androidx.lifecycle;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.Event;

public class ServiceLifecycleDispatcher {
    private final Handler mHandler = new Handler();
    private DispatchRunnable mLastDispatchRunnable;
    private final LifecycleRegistry mRegistry;

    public static class DispatchRunnable implements Runnable {
        public final Event mEvent;
        private final LifecycleRegistry mRegistry;
        private boolean mWasExecuted = false;

        public DispatchRunnable(@NonNull LifecycleRegistry lifecycleRegistry, Event event) {
            this.mRegistry = lifecycleRegistry;
            this.mEvent = event;
        }

        public void run() {
            if (!this.mWasExecuted) {
                this.mRegistry.handleLifecycleEvent(this.mEvent);
                this.mWasExecuted = true;
            }
        }
    }

    public ServiceLifecycleDispatcher(@NonNull LifecycleOwner lifecycleOwner) {
        this.mRegistry = new LifecycleRegistry(lifecycleOwner);
    }

    private void postDispatchRunnable(Event event) {
        DispatchRunnable dispatchRunnable = this.mLastDispatchRunnable;
        if (dispatchRunnable != null) {
            dispatchRunnable.run();
        }
        dispatchRunnable = new DispatchRunnable(this.mRegistry, event);
        this.mLastDispatchRunnable = dispatchRunnable;
        this.mHandler.postAtFrontOfQueue(dispatchRunnable);
    }

    @NonNull
    public Lifecycle getLifecycle() {
        return this.mRegistry;
    }

    public void onServicePreSuperOnBind() {
        postDispatchRunnable(Event.ON_START);
    }

    public void onServicePreSuperOnCreate() {
        postDispatchRunnable(Event.ON_CREATE);
    }

    public void onServicePreSuperOnDestroy() {
        postDispatchRunnable(Event.ON_STOP);
        postDispatchRunnable(Event.ON_DESTROY);
    }

    public void onServicePreSuperOnStart() {
        postDispatchRunnable(Event.ON_START);
    }
}
