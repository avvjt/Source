package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.activity.contextaware.OnContextAvailableListener;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.ActivityResultRegistryOwner;
import androidx.annotation.CallSuper;
import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.app.ActivityCompat.RequestPermissionsRequestCodeValidator;
import androidx.core.app.SharedElementCallback;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.loader.app.LoaderManager;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistry.SavedStateProvider;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class FragmentActivity extends ComponentActivity implements OnRequestPermissionsResultCallback, RequestPermissionsRequestCodeValidator {
    public static final String FRAGMENTS_TAG = "android:support:fragments";
    public boolean mCreated;
    public final LifecycleRegistry mFragmentLifecycleRegistry = new LifecycleRegistry(this);
    public final FragmentController mFragments = FragmentController.createController(new HostCallbacks());
    public boolean mResumed;
    public boolean mStopped = true;

    public class HostCallbacks extends FragmentHostCallback<FragmentActivity> implements ViewModelStoreOwner, OnBackPressedDispatcherOwner, ActivityResultRegistryOwner, FragmentOnAttachListener {
        public HostCallbacks() {
            super(FragmentActivity.this);
        }

        @NonNull
        public ActivityResultRegistry getActivityResultRegistry() {
            return FragmentActivity.this.getActivityResultRegistry();
        }

        @NonNull
        public Lifecycle getLifecycle() {
            return FragmentActivity.this.mFragmentLifecycleRegistry;
        }

        @NonNull
        public OnBackPressedDispatcher getOnBackPressedDispatcher() {
            return FragmentActivity.this.getOnBackPressedDispatcher();
        }

        @NonNull
        public ViewModelStore getViewModelStore() {
            return FragmentActivity.this.getViewModelStore();
        }

        public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            FragmentActivity.this.onAttachFragment(fragment);
        }

        public void onDump(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
            FragmentActivity.this.dump(str, fileDescriptor, printWriter, strArr);
        }

        @Nullable
        public View onFindViewById(int i) {
            return FragmentActivity.this.findViewById(i);
        }

        @NonNull
        public LayoutInflater onGetLayoutInflater() {
            return FragmentActivity.this.getLayoutInflater().cloneInContext(FragmentActivity.this);
        }

        public int onGetWindowAnimations() {
            Window window = FragmentActivity.this.getWindow();
            if (window == null) {
                return 0;
            }
            return window.getAttributes().windowAnimations;
        }

        public boolean onHasView() {
            Window window = FragmentActivity.this.getWindow();
            return (window == null || window.peekDecorView() == null) ? false : true;
        }

        public boolean onHasWindowAnimations() {
            return FragmentActivity.this.getWindow() != null;
        }

        public boolean onShouldSaveFragmentState(@NonNull Fragment fragment) {
            return FragmentActivity.this.isFinishing() ^ 1;
        }

        public boolean onShouldShowRequestPermissionRationale(@NonNull String str) {
            return ActivityCompat.shouldShowRequestPermissionRationale(FragmentActivity.this, str);
        }

        public void onSupportInvalidateOptionsMenu() {
            FragmentActivity.this.supportInvalidateOptionsMenu();
        }

        public FragmentActivity onGetHost() {
            return FragmentActivity.this;
        }
    }

    public FragmentActivity() {
        init();
    }

    private void init() {
        getSavedStateRegistry().registerSavedStateProvider(FRAGMENTS_TAG, new SavedStateProvider() {
            @NonNull
            public Bundle saveState() {
                Bundle bundle = new Bundle();
                FragmentActivity.this.markFragmentsCreated();
                FragmentActivity.this.mFragmentLifecycleRegistry.handleLifecycleEvent(Event.ON_STOP);
                Parcelable saveAllState = FragmentActivity.this.mFragments.saveAllState();
                if (saveAllState != null) {
                    bundle.putParcelable(FragmentActivity.FRAGMENTS_TAG, saveAllState);
                }
                return bundle;
            }
        });
        addOnContextAvailableListener(new OnContextAvailableListener() {
            public void onContextAvailable(@NonNull Context context) {
                FragmentActivity.this.mFragments.attachHost(null);
                SavedStateRegistry savedStateRegistry = FragmentActivity.this.getSavedStateRegistry();
                String str = FragmentActivity.FRAGMENTS_TAG;
                Bundle consumeRestoredStateForKey = savedStateRegistry.consumeRestoredStateForKey(str);
                if (consumeRestoredStateForKey != null) {
                    FragmentActivity.this.mFragments.restoreSaveState(consumeRestoredStateForKey.getParcelable(str));
                }
            }
        });
    }

    private static boolean markState(FragmentManager fragmentManager, State state) {
        int i = 0;
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null) {
                if (fragment.getHost() != null) {
                    i |= markState(fragment.getChildFragmentManager(), state);
                }
                FragmentViewLifecycleOwner fragmentViewLifecycleOwner = fragment.mViewLifecycleOwner;
                if (fragmentViewLifecycleOwner != null && fragmentViewLifecycleOwner.getLifecycle().getCurrentState().isAtLeast(State.STARTED)) {
                    fragment.mViewLifecycleOwner.setCurrentState(state);
                    i = 1;
                }
                if (fragment.mLifecycleRegistry.getCurrentState().isAtLeast(State.STARTED)) {
                    fragment.mLifecycleRegistry.setCurrentState(state);
                    i = 1;
                }
            }
        }
        return i;
    }

    @Nullable
    public final View dispatchFragmentsOnCreateView(@Nullable View view, @NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        return this.mFragments.onCreateView(view, str, context, attributeSet);
    }

    public void dump(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        printWriter.print(str);
        printWriter.print("Local FragmentActivity ");
        printWriter.print(Integer.toHexString(System.identityHashCode(this)));
        printWriter.println(" State:");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append("  ");
        String stringBuilder2 = stringBuilder.toString();
        printWriter.print(stringBuilder2);
        printWriter.print("mCreated=");
        printWriter.print(this.mCreated);
        printWriter.print(" mResumed=");
        printWriter.print(this.mResumed);
        printWriter.print(" mStopped=");
        printWriter.print(this.mStopped);
        if (getApplication() != null) {
            LoaderManager.getInstance(this).dump(stringBuilder2, fileDescriptor, printWriter, strArr);
        }
        this.mFragments.getSupportFragmentManager().dump(str, fileDescriptor, printWriter, strArr);
    }

    @NonNull
    public FragmentManager getSupportFragmentManager() {
        return this.mFragments.getSupportFragmentManager();
    }

    @NonNull
    @Deprecated
    public LoaderManager getSupportLoaderManager() {
        return LoaderManager.getInstance(this);
    }

    public void markFragmentsCreated() {
        do {
        } while (markState(getSupportFragmentManager(), State.CREATED));
    }

    @CallSuper
    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        this.mFragments.noteStateNotSaved();
        super.onActivityResult(i, i2, intent);
    }

    @MainThread
    @Deprecated
    public void onAttachFragment(@NonNull Fragment fragment) {
    }

    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFragments.noteStateNotSaved();
        this.mFragments.dispatchConfigurationChanged(configuration);
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Event.ON_CREATE);
        this.mFragments.dispatchCreate();
    }

    public boolean onCreatePanelMenu(int i, @NonNull Menu menu) {
        if (i == 0) {
            return super.onCreatePanelMenu(i, menu) | this.mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
        }
        return super.onCreatePanelMenu(i, menu);
    }

    @Nullable
    public View onCreateView(@Nullable View view, @NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        View dispatchFragmentsOnCreateView = dispatchFragmentsOnCreateView(view, str, context, attributeSet);
        return dispatchFragmentsOnCreateView == null ? super.onCreateView(view, str, context, attributeSet) : dispatchFragmentsOnCreateView;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mFragments.dispatchDestroy();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Event.ON_DESTROY);
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.mFragments.dispatchLowMemory();
    }

    public boolean onMenuItemSelected(int i, @NonNull MenuItem menuItem) {
        if (super.onMenuItemSelected(i, menuItem)) {
            return true;
        }
        if (i == 0) {
            return this.mFragments.dispatchOptionsItemSelected(menuItem);
        }
        if (i != 6) {
            return false;
        }
        return this.mFragments.dispatchContextItemSelected(menuItem);
    }

    @CallSuper
    public void onMultiWindowModeChanged(boolean z) {
        this.mFragments.dispatchMultiWindowModeChanged(z);
    }

    @CallSuper
    public void onNewIntent(@SuppressLint({"UnknownNullness"}) Intent intent) {
        super.onNewIntent(intent);
        this.mFragments.noteStateNotSaved();
    }

    public void onPanelClosed(int i, @NonNull Menu menu) {
        if (i == 0) {
            this.mFragments.dispatchOptionsMenuClosed(menu);
        }
        super.onPanelClosed(i, menu);
    }

    public void onPause() {
        super.onPause();
        this.mResumed = false;
        this.mFragments.dispatchPause();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Event.ON_PAUSE);
    }

    @CallSuper
    public void onPictureInPictureModeChanged(boolean z) {
        this.mFragments.dispatchPictureInPictureModeChanged(z);
    }

    public void onPostResume() {
        super.onPostResume();
        onResumeFragments();
    }

    @Deprecated
    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public boolean onPrepareOptionsPanel(@Nullable View view, @NonNull Menu menu) {
        return super.onPreparePanel(0, view, menu);
    }

    public boolean onPreparePanel(int i, @Nullable View view, @NonNull Menu menu) {
        if (i == 0) {
            return onPrepareOptionsPanel(view, menu) | this.mFragments.dispatchPrepareOptionsMenu(menu);
        }
        return super.onPreparePanel(i, view, menu);
    }

    @CallSuper
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        this.mFragments.noteStateNotSaved();
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    public void onResume() {
        super.onResume();
        this.mResumed = true;
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
    }

    public void onResumeFragments() {
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Event.ON_RESUME);
        this.mFragments.dispatchResume();
    }

    public void onStart() {
        super.onStart();
        this.mStopped = false;
        if (!this.mCreated) {
            this.mCreated = true;
            this.mFragments.dispatchActivityCreated();
        }
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Event.ON_START);
        this.mFragments.dispatchStart();
    }

    public void onStateNotSaved() {
        this.mFragments.noteStateNotSaved();
    }

    public void onStop() {
        super.onStop();
        this.mStopped = true;
        markFragmentsCreated();
        this.mFragments.dispatchStop();
        this.mFragmentLifecycleRegistry.handleLifecycleEvent(Event.ON_STOP);
    }

    public void setEnterSharedElementCallback(@Nullable SharedElementCallback sharedElementCallback) {
        ActivityCompat.setEnterSharedElementCallback(this, sharedElementCallback);
    }

    public void setExitSharedElementCallback(@Nullable SharedElementCallback sharedElementCallback) {
        ActivityCompat.setExitSharedElementCallback(this, sharedElementCallback);
    }

    public void startActivityFromFragment(@NonNull Fragment fragment, @SuppressLint({"UnknownNullness"}) Intent intent, int i) {
        startActivityFromFragment(fragment, intent, i, null);
    }

    @Deprecated
    public void startIntentSenderFromFragment(@NonNull Fragment fragment, @SuppressLint({"UnknownNullness"}) IntentSender intentSender, int i, @Nullable Intent intent, int i2, int i3, int i4, @Nullable Bundle bundle) throws SendIntentException {
        if (i == -1) {
            ActivityCompat.startIntentSenderForResult(this, intentSender, i, intent, i2, i3, i4, bundle);
        } else {
            fragment.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4, bundle);
        }
    }

    public void supportFinishAfterTransition() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Deprecated
    public void supportInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }

    public void supportPostponeEnterTransition() {
        ActivityCompat.postponeEnterTransition(this);
    }

    public void supportStartPostponedEnterTransition() {
        ActivityCompat.startPostponedEnterTransition(this);
    }

    @Deprecated
    public final void validateRequestPermissionsRequestCode(int i) {
    }

    public void startActivityFromFragment(@NonNull Fragment fragment, @SuppressLint({"UnknownNullness"}) Intent intent, int i, @Nullable Bundle bundle) {
        if (i == -1) {
            ActivityCompat.startActivityForResult(this, intent, -1, bundle);
        } else {
            fragment.startActivityForResult(intent, i, bundle);
        }
    }

    @Nullable
    public View onCreateView(@NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        View dispatchFragmentsOnCreateView = dispatchFragmentsOnCreateView(null, str, context, attributeSet);
        return dispatchFragmentsOnCreateView == null ? super.onCreateView(str, context, attributeSet) : dispatchFragmentsOnCreateView;
    }

    @ContentView
    public FragmentActivity(@LayoutRes int i) {
        super(i);
        init();
    }
}
