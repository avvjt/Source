package androidx.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ClassesInfoCache.CallbackInfo;
import androidx.lifecycle.Lifecycle.Event;

public class ReflectiveGenericLifecycleObserver implements LifecycleEventObserver {
    private final CallbackInfo mInfo;
    private final Object mWrapped;

    public ReflectiveGenericLifecycleObserver(Object obj) {
        this.mWrapped = obj;
        this.mInfo = ClassesInfoCache.sInstance.getInfo(obj.getClass());
    }

    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
        this.mInfo.invokeCallbacks(lifecycleOwner, event, this.mWrapped);
    }
}
