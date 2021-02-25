package androidx.lifecycle;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistry.AutoRecreated;
import androidx.savedstate.SavedStateRegistryOwner;

public final class SavedStateHandleController implements LifecycleEventObserver {
    public static final String TAG_SAVED_STATE_HANDLE_CONTROLLER = "androidx.lifecycle.savedstate.vm.tag";
    private final SavedStateHandle mHandle;
    private boolean mIsAttached = false;
    private final String mKey;

    public static final class OnRecreation implements AutoRecreated {
        public void onRecreated(@NonNull SavedStateRegistryOwner savedStateRegistryOwner) {
            if (savedStateRegistryOwner instanceof ViewModelStoreOwner) {
                ViewModelStore viewModelStore = ((ViewModelStoreOwner) savedStateRegistryOwner).getViewModelStore();
                SavedStateRegistry savedStateRegistry = savedStateRegistryOwner.getSavedStateRegistry();
                for (String str : viewModelStore.keys()) {
                    SavedStateHandleController.attachHandleIfNeeded(viewModelStore.get(str), savedStateRegistry, savedStateRegistryOwner.getLifecycle());
                }
                if (!viewModelStore.keys().isEmpty()) {
                    savedStateRegistry.runOnNextRecreation(OnRecreation.class);
                    return;
                }
                return;
            }
            throw new IllegalStateException("Internal error: OnRecreation should be registered only on componentsthat implement ViewModelStoreOwner");
        }
    }

    public SavedStateHandleController(String str, SavedStateHandle savedStateHandle) {
        this.mKey = str;
        this.mHandle = savedStateHandle;
    }

    public static void attachHandleIfNeeded(ViewModel viewModel, SavedStateRegistry savedStateRegistry, Lifecycle lifecycle) {
        SavedStateHandleController savedStateHandleController = (SavedStateHandleController) viewModel.getTag("androidx.lifecycle.savedstate.vm.tag");
        if (savedStateHandleController != null && !savedStateHandleController.isAttached()) {
            savedStateHandleController.attachToLifecycle(savedStateRegistry, lifecycle);
            tryToAddRecreator(savedStateRegistry, lifecycle);
        }
    }

    public static SavedStateHandleController create(SavedStateRegistry savedStateRegistry, Lifecycle lifecycle, String str, Bundle bundle) {
        SavedStateHandleController savedStateHandleController = new SavedStateHandleController(str, SavedStateHandle.createHandle(savedStateRegistry.consumeRestoredStateForKey(str), bundle));
        savedStateHandleController.attachToLifecycle(savedStateRegistry, lifecycle);
        tryToAddRecreator(savedStateRegistry, lifecycle);
        return savedStateHandleController;
    }

    private static void tryToAddRecreator(final SavedStateRegistry savedStateRegistry, final Lifecycle lifecycle) {
        State currentState = lifecycle.getCurrentState();
        if (currentState == State.INITIALIZED || currentState.isAtLeast(State.STARTED)) {
            savedStateRegistry.runOnNextRecreation(OnRecreation.class);
        } else {
            lifecycle.addObserver(new LifecycleEventObserver() {
                public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
                    if (event == Event.ON_START) {
                        lifecycle.removeObserver(this);
                        savedStateRegistry.runOnNextRecreation(OnRecreation.class);
                    }
                }
            });
        }
    }

    public void attachToLifecycle(SavedStateRegistry savedStateRegistry, Lifecycle lifecycle) {
        if (this.mIsAttached) {
            throw new IllegalStateException("Already attached to lifecycleOwner");
        }
        this.mIsAttached = true;
        lifecycle.addObserver(this);
        savedStateRegistry.registerSavedStateProvider(this.mKey, this.mHandle.savedStateProvider());
    }

    public SavedStateHandle getHandle() {
        return this.mHandle;
    }

    public boolean isAttached() {
        return this.mIsAttached;
    }

    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
        if (event == Event.ON_DESTROY) {
            this.mIsAttached = false;
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
    }
}
