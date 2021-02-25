package androidx.activity.result;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import f.d.b.a.a;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public abstract class ActivityResultRegistry {
    private static final int INITIAL_REQUEST_CODE_VALUE = 65536;
    private static final String KEY_COMPONENT_ACTIVITY_PENDING_RESULTS = "KEY_COMPONENT_ACTIVITY_PENDING_RESULT";
    private static final String KEY_COMPONENT_ACTIVITY_RANDOM_OBJECT = "KEY_COMPONENT_ACTIVITY_RANDOM_OBJECT";
    private static final String KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS = "KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS";
    private static final String KEY_COMPONENT_ACTIVITY_REGISTERED_RCS = "KEY_COMPONENT_ACTIVITY_REGISTERED_RCS";
    private static final String LOG_TAG = "ActivityResultRegistry";
    public final transient Map<String, CallbackAndContract<?>> mKeyToCallback = new HashMap();
    private final Map<String, LifecycleContainer> mKeyToLifecycleContainers = new HashMap();
    private final Map<String, Integer> mKeyToRc = new HashMap();
    public final Map<String, Object> mParsedPendingResults = new HashMap();
    public final Bundle mPendingResults = new Bundle();
    private Random mRandom = new Random();
    private final Map<Integer, String> mRcToKey = new HashMap();

    public static class CallbackAndContract<O> {
        public final ActivityResultCallback<O> mCallback;
        public final ActivityResultContract<?, O> mContract;

        public CallbackAndContract(ActivityResultCallback<O> activityResultCallback, ActivityResultContract<?, O> activityResultContract) {
            this.mCallback = activityResultCallback;
            this.mContract = activityResultContract;
        }
    }

    public static class LifecycleContainer {
        public final Lifecycle mLifecycle;
        private final ArrayList<LifecycleEventObserver> mObservers = new ArrayList();

        public LifecycleContainer(@NonNull Lifecycle lifecycle) {
            this.mLifecycle = lifecycle;
        }

        public void addObserver(@NonNull LifecycleEventObserver lifecycleEventObserver) {
            this.mLifecycle.addObserver(lifecycleEventObserver);
            this.mObservers.add(lifecycleEventObserver);
        }

        public void clearObservers() {
            Iterator it = this.mObservers.iterator();
            while (it.hasNext()) {
                this.mLifecycle.removeObserver((LifecycleEventObserver) it.next());
            }
            this.mObservers.clear();
        }
    }

    private void bindRcKey(int i, String str) {
        this.mRcToKey.put(Integer.valueOf(i), str);
        this.mKeyToRc.put(str, Integer.valueOf(i));
    }

    private <O> void doDispatch(String str, int i, @Nullable Intent intent, @Nullable CallbackAndContract<O> callbackAndContract) {
        if (callbackAndContract != null) {
            ActivityResultCallback activityResultCallback = callbackAndContract.mCallback;
            if (activityResultCallback != null) {
                activityResultCallback.onActivityResult(callbackAndContract.mContract.parseResult(i, intent));
                return;
            }
        }
        this.mParsedPendingResults.remove(str);
        this.mPendingResults.putParcelable(str, new ActivityResult(i, intent));
    }

    private int generateRandomNumber() {
        int nextInt = this.mRandom.nextInt(2147418112);
        while (true) {
            nextInt += 65536;
            if (!this.mRcToKey.containsKey(Integer.valueOf(nextInt))) {
                return nextInt;
            }
            nextInt = this.mRandom.nextInt(2147418112);
        }
    }

    private int registerKey(String str) {
        Integer num = (Integer) this.mKeyToRc.get(str);
        if (num != null) {
            return num.intValue();
        }
        int generateRandomNumber = generateRandomNumber();
        bindRcKey(generateRandomNumber, str);
        return generateRandomNumber;
    }

    @MainThread
    public final boolean dispatchResult(int i, int i2, @Nullable Intent intent) {
        String str = (String) this.mRcToKey.get(Integer.valueOf(i));
        if (str == null) {
            return false;
        }
        doDispatch(str, i2, intent, (CallbackAndContract) this.mKeyToCallback.get(str));
        return true;
    }

    @MainThread
    public abstract <I, O> void onLaunch(int i, @NonNull ActivityResultContract<I, O> activityResultContract, @SuppressLint({"UnknownNullness"}) I i2, @Nullable ActivityOptionsCompat activityOptionsCompat);

    public final void onRestoreInstanceState(@Nullable Bundle bundle) {
        if (bundle != null) {
            ArrayList integerArrayList = bundle.getIntegerArrayList(KEY_COMPONENT_ACTIVITY_REGISTERED_RCS);
            ArrayList stringArrayList = bundle.getStringArrayList(KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS);
            if (!(stringArrayList == null || integerArrayList == null)) {
                int size = stringArrayList.size();
                for (int i = 0; i < size; i++) {
                    bindRcKey(((Integer) integerArrayList.get(i)).intValue(), (String) stringArrayList.get(i));
                }
                this.mRandom = (Random) bundle.getSerializable(KEY_COMPONENT_ACTIVITY_RANDOM_OBJECT);
                this.mPendingResults.putAll(bundle.getBundle(KEY_COMPONENT_ACTIVITY_PENDING_RESULTS));
            }
        }
    }

    public final void onSaveInstanceState(@NonNull Bundle bundle) {
        bundle.putIntegerArrayList(KEY_COMPONENT_ACTIVITY_REGISTERED_RCS, new ArrayList(this.mRcToKey.keySet()));
        bundle.putStringArrayList(KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS, new ArrayList(this.mRcToKey.values()));
        bundle.putBundle(KEY_COMPONENT_ACTIVITY_PENDING_RESULTS, (Bundle) this.mPendingResults.clone());
        bundle.putSerializable(KEY_COMPONENT_ACTIVITY_RANDOM_OBJECT, this.mRandom);
    }

    @NonNull
    public final <I, O> ActivityResultLauncher<I> register(@NonNull final String str, @NonNull LifecycleOwner lifecycleOwner, @NonNull final ActivityResultContract<I, O> activityResultContract, @NonNull final ActivityResultCallback<O> activityResultCallback) {
        Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        if (lifecycle.getCurrentState().isAtLeast(State.STARTED)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LifecycleOwner ");
            stringBuilder.append(lifecycleOwner);
            stringBuilder.append(" is attempting to register while current state is ");
            stringBuilder.append(lifecycle.getCurrentState());
            stringBuilder.append(". LifecycleOwners must call register before they are STARTED.");
            throw new IllegalStateException(stringBuilder.toString());
        }
        final int registerKey = registerKey(str);
        LifecycleContainer lifecycleContainer = (LifecycleContainer) this.mKeyToLifecycleContainers.get(str);
        if (lifecycleContainer == null) {
            lifecycleContainer = new LifecycleContainer(lifecycle);
        }
        lifecycleContainer.addObserver(new LifecycleEventObserver() {
            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
                if (Event.ON_START.equals(event)) {
                    ActivityResultRegistry.this.mKeyToCallback.put(str, new CallbackAndContract(activityResultCallback, activityResultContract));
                    if (ActivityResultRegistry.this.mParsedPendingResults.containsKey(str)) {
                        Object obj = ActivityResultRegistry.this.mParsedPendingResults.get(str);
                        ActivityResultRegistry.this.mParsedPendingResults.remove(str);
                        activityResultCallback.onActivityResult(obj);
                    }
                    ActivityResult activityResult = (ActivityResult) ActivityResultRegistry.this.mPendingResults.getParcelable(str);
                    if (activityResult != null) {
                        ActivityResultRegistry.this.mPendingResults.remove(str);
                        activityResultCallback.onActivityResult(activityResultContract.parseResult(activityResult.getResultCode(), activityResult.getData()));
                    }
                } else if (Event.ON_STOP.equals(event)) {
                    ActivityResultRegistry.this.mKeyToCallback.remove(str);
                } else if (Event.ON_DESTROY.equals(event)) {
                    ActivityResultRegistry.this.unregister(str);
                }
            }
        });
        this.mKeyToLifecycleContainers.put(str, lifecycleContainer);
        return new ActivityResultLauncher<I>() {
            @NonNull
            public ActivityResultContract<I, ?> getContract() {
                return activityResultContract;
            }

            public void launch(I i, @Nullable ActivityOptionsCompat activityOptionsCompat) {
                ActivityResultRegistry.this.onLaunch(registerKey, activityResultContract, i, activityOptionsCompat);
            }

            public void unregister() {
                ActivityResultRegistry.this.unregister(str);
            }
        };
    }

    @MainThread
    public final void unregister(@NonNull String str) {
        StringBuilder Q;
        Integer num = (Integer) this.mKeyToRc.remove(str);
        if (num != null) {
            this.mRcToKey.remove(num);
        }
        this.mKeyToCallback.remove(str);
        boolean containsKey = this.mParsedPendingResults.containsKey(str);
        String str2 = ": ";
        String str3 = "Dropping pending result for request ";
        String str4 = LOG_TAG;
        if (containsKey) {
            Q = a.Q(str3, str, str2);
            Q.append(this.mParsedPendingResults.get(str));
            Log.w(str4, Q.toString());
            this.mParsedPendingResults.remove(str);
        }
        if (this.mPendingResults.containsKey(str)) {
            Q = a.Q(str3, str, str2);
            Q.append(this.mPendingResults.getParcelable(str));
            Log.w(str4, Q.toString());
            this.mPendingResults.remove(str);
        }
        LifecycleContainer lifecycleContainer = (LifecycleContainer) this.mKeyToLifecycleContainers.get(str);
        if (lifecycleContainer != null) {
            lifecycleContainer.clearObservers();
            this.mKeyToLifecycleContainers.remove(str);
        }
    }

    @MainThread
    public final <O> boolean dispatchResult(int i, @SuppressLint({"UnknownNullness"}) O o) {
        String str = (String) this.mRcToKey.get(Integer.valueOf(i));
        if (str == null) {
            return false;
        }
        CallbackAndContract callbackAndContract = (CallbackAndContract) this.mKeyToCallback.get(str);
        if (callbackAndContract != null) {
            ActivityResultCallback activityResultCallback = callbackAndContract.mCallback;
            if (activityResultCallback != null) {
                activityResultCallback.onActivityResult(o);
                return true;
            }
        }
        this.mPendingResults.remove(str);
        this.mParsedPendingResults.put(str, o);
        return true;
    }

    @NonNull
    public final <I, O> ActivityResultLauncher<I> register(@NonNull final String str, @NonNull final ActivityResultContract<I, O> activityResultContract, @NonNull ActivityResultCallback<O> activityResultCallback) {
        final int registerKey = registerKey(str);
        this.mKeyToCallback.put(str, new CallbackAndContract(activityResultCallback, activityResultContract));
        if (this.mParsedPendingResults.containsKey(str)) {
            Object obj = this.mParsedPendingResults.get(str);
            this.mParsedPendingResults.remove(str);
            activityResultCallback.onActivityResult(obj);
        }
        ActivityResult activityResult = (ActivityResult) this.mPendingResults.getParcelable(str);
        if (activityResult != null) {
            this.mPendingResults.remove(str);
            activityResultCallback.onActivityResult(activityResultContract.parseResult(activityResult.getResultCode(), activityResult.getData()));
        }
        return new ActivityResultLauncher<I>() {
            @NonNull
            public ActivityResultContract<I, ?> getContract() {
                return activityResultContract;
            }

            public void launch(I i, @Nullable ActivityOptionsCompat activityOptionsCompat) {
                ActivityResultRegistry.this.onLaunch(registerKey, activityResultContract, i, activityOptionsCompat);
            }

            public void unregister() {
                ActivityResultRegistry.this.unregister(str);
            }
        };
    }
}
