package androidx.lifecycle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LifecycleService extends Service implements LifecycleOwner {
    private final ServiceLifecycleDispatcher mDispatcher = new ServiceLifecycleDispatcher(this);

    @NonNull
    public Lifecycle getLifecycle() {
        return this.mDispatcher.getLifecycle();
    }

    @CallSuper
    @Nullable
    public IBinder onBind(@NonNull Intent intent) {
        this.mDispatcher.onServicePreSuperOnBind();
        return null;
    }

    @CallSuper
    public void onCreate() {
        this.mDispatcher.onServicePreSuperOnCreate();
        super.onCreate();
    }

    @CallSuper
    public void onDestroy() {
        this.mDispatcher.onServicePreSuperOnDestroy();
        super.onDestroy();
    }

    @CallSuper
    public void onStart(@NonNull Intent intent, int i) {
        this.mDispatcher.onServicePreSuperOnStart();
        super.onStart(intent, i);
    }

    @CallSuper
    public int onStartCommand(@NonNull Intent intent, int i, int i2) {
        return super.onStartCommand(intent, i, i2);
    }
}
