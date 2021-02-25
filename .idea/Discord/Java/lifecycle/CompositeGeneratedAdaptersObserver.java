package androidx.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.Event;

public class CompositeGeneratedAdaptersObserver implements LifecycleEventObserver {
    private final GeneratedAdapter[] mGeneratedAdapters;

    public CompositeGeneratedAdaptersObserver(GeneratedAdapter[] generatedAdapterArr) {
        this.mGeneratedAdapters = generatedAdapterArr;
    }

    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
        MethodCallsLogger methodCallsLogger = new MethodCallsLogger();
        int i = 0;
        for (GeneratedAdapter callMethods : this.mGeneratedAdapters) {
            callMethods.callMethods(lifecycleOwner, event, false, methodCallsLogger);
        }
        GeneratedAdapter[] generatedAdapterArr = this.mGeneratedAdapters;
        int length = generatedAdapterArr.length;
        while (i < length) {
            generatedAdapterArr[i].callMethods(lifecycleOwner, event, true, methodCallsLogger);
            i++;
        }
    }
}
