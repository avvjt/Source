package androidx.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.Event;

public class SingleGeneratedAdapterObserver implements LifecycleEventObserver {
    private final GeneratedAdapter mGeneratedAdapter;

    public SingleGeneratedAdapterObserver(GeneratedAdapter generatedAdapter) {
        this.mGeneratedAdapter = generatedAdapter;
    }

    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
        this.mGeneratedAdapter.callMethods(lifecycleOwner, event, false, null);
        this.mGeneratedAdapter.callMethods(lifecycleOwner, event, true, null);
    }
}
