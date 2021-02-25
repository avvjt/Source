package androidx.fragment.app;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.EnvironmentCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.R;
import androidx.fragment.app.Fragment.SavedState;
import androidx.fragment.app.SpecialEffectsController.Operation;
import androidx.fragment.app.SpecialEffectsController.Operation.LifecycleImpact;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.ViewModelStoreOwner;
import f.d.b.a.a;

public class FragmentStateManager {
    private static final String TAG = "FragmentManager";
    private static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    private static final String TARGET_STATE_TAG = "android:target_state";
    private static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    private static final String VIEW_REGISTRY_STATE_TAG = "android:view_registry_state";
    private static final String VIEW_STATE_TAG = "android:view_state";
    private final FragmentLifecycleCallbacksDispatcher mDispatcher;
    @NonNull
    private final Fragment mFragment;
    private int mFragmentManagerState = -1;
    private final FragmentStore mFragmentStore;
    private boolean mMovingToState = false;

    /* renamed from: androidx.fragment.app.FragmentStateManager$2 */
    public static /* synthetic */ class AnonymousClass2 {
        public static final /* synthetic */ int[] $SwitchMap$androidx$lifecycle$Lifecycle$State;

        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x001c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0016 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|(2:1|2)|3|5|6|7|8|(3:9|10|12)) */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static {
            State state;
            State.values();
            int[] iArr = new int[5];
            $SwitchMap$androidx$lifecycle$Lifecycle$State = iArr;
            try {
                state = State.RESUMED;
                iArr[4] = 1;
            } catch (NoSuchFieldError unused) {
            }
            int[] iArr2 = $SwitchMap$androidx$lifecycle$Lifecycle$State;
            State state2 = State.STARTED;
            iArr2[3] = 2;
            iArr2 = $SwitchMap$androidx$lifecycle$Lifecycle$State;
            state2 = State.CREATED;
            iArr2[2] = 3;
            try {
                iArr = $SwitchMap$androidx$lifecycle$Lifecycle$State;
                state = State.INITIALIZED;
                iArr[1] = 4;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public FragmentStateManager(@NonNull FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, @NonNull FragmentStore fragmentStore, @NonNull Fragment fragment) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragment;
    }

    private boolean isFragmentViewChild(@NonNull View view) {
        if (view == this.mFragment.mView) {
            return true;
        }
        for (ViewParent parent = view.getParent(); parent != null; parent = parent.getParent()) {
            if (parent == this.mFragment.mView) {
                return true;
            }
        }
        return false;
    }

    private Bundle saveBasicState() {
        Bundle bundle = new Bundle();
        this.mFragment.performSaveInstanceState(bundle);
        this.mDispatcher.dispatchOnFragmentSaveInstanceState(this.mFragment, bundle, false);
        if (bundle.isEmpty()) {
            bundle = null;
        }
        if (this.mFragment.mView != null) {
            saveViewState();
        }
        if (this.mFragment.mSavedViewState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray(VIEW_STATE_TAG, this.mFragment.mSavedViewState);
        }
        if (this.mFragment.mSavedViewRegistryState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBundle(VIEW_REGISTRY_STATE_TAG, this.mFragment.mSavedViewRegistryState);
        }
        if (!this.mFragment.mUserVisibleHint) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean(USER_VISIBLE_HINT_TAG, this.mFragment.mUserVisibleHint);
        }
        return bundle;
    }

    public void activityCreated() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("moveto ACTIVITY_CREATED: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        Fragment fragment = this.mFragment;
        fragment.performActivityCreated(fragment.mSavedFragmentState);
        FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
        Fragment fragment2 = this.mFragment;
        fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentActivityCreated(fragment2, fragment2.mSavedFragmentState, false);
    }

    public void addViewToContainer() {
        int findFragmentIndexInContainer = this.mFragmentStore.findFragmentIndexInContainer(this.mFragment);
        Fragment fragment = this.mFragment;
        fragment.mContainer.addView(fragment.mView, findFragmentIndexInContainer);
    }

    public void attach() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("moveto ATTACHED: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        Fragment fragment = this.mFragment;
        Fragment fragment2 = fragment.mTarget;
        String str = " that does not belong to this FragmentManager!";
        String str2 = " declared target fragment ";
        String str3 = "Fragment ";
        FragmentStateManager fragmentStateManager = null;
        StringBuilder L2;
        if (fragment2 != null) {
            FragmentStateManager fragmentStateManager2 = this.mFragmentStore.getFragmentStateManager(fragment2.mWho);
            if (fragmentStateManager2 != null) {
                fragment2 = this.mFragment;
                fragment2.mTargetWho = fragment2.mTarget.mWho;
                fragment2.mTarget = null;
                fragmentStateManager = fragmentStateManager2;
            } else {
                L2 = a.L(str3);
                L2.append(this.mFragment);
                L2.append(str2);
                L2.append(this.mFragment.mTarget);
                L2.append(str);
                throw new IllegalStateException(L2.toString());
            }
        }
        String str4 = fragment.mTargetWho;
        if (str4 != null) {
            fragmentStateManager = this.mFragmentStore.getFragmentStateManager(str4);
            if (fragmentStateManager == null) {
                L2 = a.L(str3);
                L2.append(this.mFragment);
                L2.append(str2);
                throw new IllegalStateException(a.D(L2, this.mFragment.mTargetWho, str));
            }
        }
        if (fragmentStateManager != null && (FragmentManager.USE_STATE_MANAGER || fragmentStateManager.getFragment().mState < 1)) {
            fragmentStateManager.moveToExpectedState();
        }
        fragment = this.mFragment;
        fragment.mHost = fragment.mFragmentManager.getHost();
        fragment = this.mFragment;
        fragment.mParentFragment = fragment.mFragmentManager.getParent();
        this.mDispatcher.dispatchOnFragmentPreAttached(this.mFragment, false);
        this.mFragment.performAttach();
        this.mDispatcher.dispatchOnFragmentAttached(this.mFragment, false);
    }

    public int computeExpectedState() {
        Fragment fragment = this.mFragment;
        if (fragment.mFragmentManager == null) {
            return fragment.mState;
        }
        int i = this.mFragmentManagerState;
        int ordinal = fragment.mMaxState.ordinal();
        if (ordinal == 1) {
            i = Math.min(i, 0);
        } else if (ordinal == 2) {
            i = Math.min(i, 1);
        } else if (ordinal == 3) {
            i = Math.min(i, 5);
        } else if (ordinal != 4) {
            i = Math.min(i, -1);
        }
        fragment = this.mFragment;
        if (fragment.mFromLayout) {
            if (fragment.mInLayout) {
                i = Math.max(this.mFragmentManagerState, 2);
                View view = this.mFragment.mView;
                if (view != null && view.getParent() == null) {
                    i = Math.min(i, 2);
                }
            } else {
                i = this.mFragmentManagerState < 4 ? Math.min(i, fragment.mState) : Math.min(i, 1);
            }
        }
        if (!this.mFragment.mAdded) {
            i = Math.min(i, 1);
        }
        LifecycleImpact lifecycleImpact = null;
        if (FragmentManager.USE_STATE_MANAGER) {
            Fragment fragment2 = this.mFragment;
            ViewGroup viewGroup = fragment2.mContainer;
            if (viewGroup != null) {
                lifecycleImpact = SpecialEffectsController.getOrCreateController(viewGroup, fragment2.getParentFragmentManager()).getAwaitingCompletionLifecycleImpact(this);
            }
        }
        if (lifecycleImpact == LifecycleImpact.ADDING) {
            i = Math.min(i, 6);
        } else if (lifecycleImpact == LifecycleImpact.REMOVING) {
            i = Math.max(i, 3);
        } else {
            fragment = this.mFragment;
            if (fragment.mRemoving) {
                if (fragment.isInBackStack()) {
                    i = Math.min(i, 1);
                } else {
                    i = Math.min(i, -1);
                }
            }
        }
        fragment = this.mFragment;
        if (fragment.mDeferStart && fragment.mState < 5) {
            i = Math.min(i, 4);
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder M = a.M("computeExpectedState() of ", i, " for ");
            M.append(this.mFragment);
            Log.v("FragmentManager", M.toString());
        }
        return i;
    }

    public void create() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("moveto CREATED: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        Fragment fragment = this.mFragment;
        if (fragment.mIsCreated) {
            fragment.restoreChildFragmentState(fragment.mSavedFragmentState);
            this.mFragment.mState = 1;
            return;
        }
        this.mDispatcher.dispatchOnFragmentPreCreated(fragment, fragment.mSavedFragmentState, false);
        fragment = this.mFragment;
        fragment.performCreate(fragment.mSavedFragmentState);
        FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
        Fragment fragment2 = this.mFragment;
        fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentCreated(fragment2, fragment2.mSavedFragmentState, false);
    }

    public void createView() {
        if (!this.mFragment.mFromLayout) {
            StringBuilder L;
            String str = "FragmentManager";
            if (FragmentManager.isLoggingEnabled(3)) {
                StringBuilder L2 = a.L("moveto CREATE_VIEW: ");
                L2.append(this.mFragment);
                Log.d(str, L2.toString());
            }
            Fragment fragment = this.mFragment;
            LayoutInflater performGetLayoutInflater = fragment.performGetLayoutInflater(fragment.mSavedFragmentState);
            ViewGroup viewGroup = null;
            Fragment fragment2 = this.mFragment;
            ViewGroup viewGroup2 = fragment2.mContainer;
            if (viewGroup2 != null) {
                viewGroup = viewGroup2;
            } else {
                int i = fragment2.mContainerId;
                if (i != 0) {
                    if (i != -1) {
                        viewGroup = (ViewGroup) fragment2.mFragmentManager.getContainer().onFindViewById(this.mFragment.mContainerId);
                        if (viewGroup == null) {
                            fragment2 = this.mFragment;
                            if (!fragment2.mRestored) {
                                String resourceName;
                                try {
                                    resourceName = fragment2.getResources().getResourceName(this.mFragment.mContainerId);
                                } catch (NotFoundException unused) {
                                    resourceName = EnvironmentCompat.MEDIA_UNKNOWN;
                                }
                                L = a.L("No view found for id 0x");
                                L.append(Integer.toHexString(this.mFragment.mContainerId));
                                L.append(" (");
                                L.append(resourceName);
                                L.append(") for fragment ");
                                L.append(this.mFragment);
                                throw new IllegalArgumentException(L.toString());
                            }
                        }
                    }
                    StringBuilder L3 = a.L("Cannot create fragment ");
                    L3.append(this.mFragment);
                    L3.append(" for a container view with no id");
                    throw new IllegalArgumentException(L3.toString());
                }
            }
            fragment2 = this.mFragment;
            fragment2.mContainer = viewGroup;
            fragment2.performCreateView(performGetLayoutInflater, viewGroup, fragment2.mSavedFragmentState);
            View view = this.mFragment.mView;
            if (view != null) {
                boolean z = false;
                view.setSaveFromParentEnabled(false);
                fragment = this.mFragment;
                fragment.mView.setTag(R.id.fragment_container_view_tag, fragment);
                if (viewGroup != null) {
                    addViewToContainer();
                }
                fragment = this.mFragment;
                if (fragment.mHidden) {
                    fragment.mView.setVisibility(8);
                }
                if (ViewCompat.isAttachedToWindow(this.mFragment.mView)) {
                    ViewCompat.requestApplyInsets(this.mFragment.mView);
                } else {
                    view = this.mFragment.mView;
                    view.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
                        public void onViewAttachedToWindow(View view) {
                            view.removeOnAttachStateChangeListener(this);
                            ViewCompat.requestApplyInsets(view);
                        }

                        public void onViewDetachedFromWindow(View view) {
                        }
                    });
                }
                this.mFragment.performViewCreated();
                FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
                Fragment fragment3 = this.mFragment;
                fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentViewCreated(fragment3, fragment3.mView, fragment3.mSavedFragmentState, false);
                int visibility = this.mFragment.mView.getVisibility();
                float alpha = this.mFragment.mView.getAlpha();
                if (FragmentManager.USE_STATE_MANAGER) {
                    this.mFragment.setPostOnViewCreatedAlpha(alpha);
                    fragment3 = this.mFragment;
                    if (fragment3.mContainer != null && visibility == 0) {
                        view = fragment3.mView.findFocus();
                        if (view != null) {
                            this.mFragment.setFocusedView(view);
                            if (FragmentManager.isLoggingEnabled(2)) {
                                L = new StringBuilder();
                                L.append("requestFocus: Saved focused view ");
                                L.append(view);
                                L.append(" for Fragment ");
                                L.append(this.mFragment);
                                Log.v(str, L.toString());
                            }
                        }
                        this.mFragment.mView.setAlpha(0.0f);
                    }
                } else {
                    Fragment fragment4 = this.mFragment;
                    if (visibility == 0 && fragment4.mContainer != null) {
                        z = true;
                    }
                    fragment4.mIsNewlyAdded = z;
                }
            }
            this.mFragment.mState = 2;
        }
    }

    public void destroy() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("movefrom CREATED: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        Fragment fragment = this.mFragment;
        boolean z = true;
        Object obj = (!fragment.mRemoving || fragment.isInBackStack()) ? null : 1;
        Object obj2 = (obj != null || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) ? 1 : null;
        if (obj2 != null) {
            FragmentHostCallback fragmentHostCallback = this.mFragment.mHost;
            if (fragmentHostCallback instanceof ViewModelStoreOwner) {
                z = this.mFragmentStore.getNonConfig().isCleared();
            } else if (fragmentHostCallback.getContext() instanceof Activity) {
                z = true ^ ((Activity) fragmentHostCallback.getContext()).isChangingConfigurations();
            }
            if (obj != null || z) {
                this.mFragmentStore.getNonConfig().clearNonConfigState(this.mFragment);
            }
            this.mFragment.performDestroy();
            this.mDispatcher.dispatchOnFragmentDestroyed(this.mFragment, false);
            for (FragmentStateManager fragmentStateManager : this.mFragmentStore.getActiveFragmentStateManagers()) {
                if (fragmentStateManager != null) {
                    Fragment fragment2 = fragmentStateManager.getFragment();
                    if (this.mFragment.mWho.equals(fragment2.mTargetWho)) {
                        fragment2.mTarget = this.mFragment;
                        fragment2.mTargetWho = null;
                    }
                }
            }
            fragment = this.mFragment;
            String str = fragment.mTargetWho;
            if (str != null) {
                fragment.mTarget = this.mFragmentStore.findActiveFragment(str);
            }
            this.mFragmentStore.makeInactive(this);
            return;
        }
        String str2 = this.mFragment.mTargetWho;
        if (str2 != null) {
            fragment = this.mFragmentStore.findActiveFragment(str2);
            if (fragment != null && fragment.mRetainInstance) {
                this.mFragment.mTarget = fragment;
            }
        }
        this.mFragment.mState = 0;
    }

    public void destroyFragmentView() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("movefrom CREATE_VIEW: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        Fragment fragment = this.mFragment;
        ViewGroup viewGroup = fragment.mContainer;
        if (viewGroup != null) {
            View view = fragment.mView;
            if (view != null) {
                viewGroup.removeView(view);
            }
        }
        this.mFragment.performDestroyView();
        this.mDispatcher.dispatchOnFragmentViewDestroyed(this.mFragment, false);
        fragment = this.mFragment;
        fragment.mContainer = null;
        fragment.mView = null;
        fragment.mViewLifecycleOwner = null;
        fragment.mViewLifecycleOwnerLiveData.setValue(null);
        this.mFragment.mInLayout = false;
    }

    public void detach() {
        String str = "FragmentManager";
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("movefrom ATTACHED: ");
            L.append(this.mFragment);
            Log.d(str, L.toString());
        }
        this.mFragment.performDetach();
        boolean z = false;
        this.mDispatcher.dispatchOnFragmentDetached(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mState = -1;
        fragment.mHost = null;
        fragment.mParentFragment = null;
        fragment.mFragmentManager = null;
        if (fragment.mRemoving && !fragment.isInBackStack()) {
            z = true;
        }
        if (z || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
            if (FragmentManager.isLoggingEnabled(3)) {
                StringBuilder L2 = a.L("initState called for fragment: ");
                L2.append(this.mFragment);
                Log.d(str, L2.toString());
            }
            this.mFragment.initState();
        }
    }

    public void ensureInflatedView() {
        Fragment fragment = this.mFragment;
        if (fragment.mFromLayout && fragment.mInLayout && !fragment.mPerformedCreateView) {
            if (FragmentManager.isLoggingEnabled(3)) {
                StringBuilder L = a.L("moveto CREATE_VIEW: ");
                L.append(this.mFragment);
                Log.d("FragmentManager", L.toString());
            }
            fragment = this.mFragment;
            fragment.performCreateView(fragment.performGetLayoutInflater(fragment.mSavedFragmentState), null, this.mFragment.mSavedFragmentState);
            View view = this.mFragment.mView;
            if (view != null) {
                view.setSaveFromParentEnabled(false);
                fragment = this.mFragment;
                fragment.mView.setTag(R.id.fragment_container_view_tag, fragment);
                fragment = this.mFragment;
                if (fragment.mHidden) {
                    fragment.mView.setVisibility(8);
                }
                this.mFragment.performViewCreated();
                FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
                Fragment fragment2 = this.mFragment;
                fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentViewCreated(fragment2, fragment2.mView, fragment2.mSavedFragmentState, false);
                this.mFragment.mState = 2;
            }
        }
    }

    @NonNull
    public Fragment getFragment() {
        return this.mFragment;
    }

    public void moveToExpectedState() {
        String str = "FragmentManager";
        if (this.mMovingToState) {
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder L = a.L("Ignoring re-entrant call to moveToExpectedState() for ");
                L.append(getFragment());
                Log.v(str, L.toString());
            }
            return;
        }
        try {
            this.mMovingToState = true;
            while (true) {
                int computeExpectedState = computeExpectedState();
                Fragment fragment = this.mFragment;
                int i = fragment.mState;
                if (computeExpectedState != i) {
                    if (computeExpectedState <= i) {
                        switch (i - 1) {
                            case -1:
                                detach();
                                break;
                            case 0:
                                destroy();
                                break;
                            case 1:
                                destroyFragmentView();
                                this.mFragment.mState = 1;
                                break;
                            case 2:
                                fragment.mInLayout = false;
                                fragment.mState = 2;
                                break;
                            case 3:
                                if (FragmentManager.isLoggingEnabled(3)) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("movefrom ACTIVITY_CREATED: ");
                                    stringBuilder.append(this.mFragment);
                                    Log.d(str, stringBuilder.toString());
                                }
                                fragment = this.mFragment;
                                if (fragment.mView != null && fragment.mSavedViewState == null) {
                                    saveViewState();
                                }
                                fragment = this.mFragment;
                                if (fragment.mView != null) {
                                    ViewGroup viewGroup = fragment.mContainer;
                                    if (viewGroup != null) {
                                        SpecialEffectsController.getOrCreateController(viewGroup, fragment.getParentFragmentManager()).enqueueRemove(this);
                                    }
                                }
                                this.mFragment.mState = 3;
                                break;
                            case 4:
                                stop();
                                break;
                            case 5:
                                fragment.mState = 5;
                                break;
                            case 6:
                                pause();
                                break;
                            default:
                                break;
                        }
                    }
                    switch (i + 1) {
                        case 0:
                            attach();
                            break;
                        case 1:
                            create();
                            break;
                        case 2:
                            ensureInflatedView();
                            createView();
                            break;
                        case 3:
                            activityCreated();
                            break;
                        case 4:
                            if (fragment.mView != null) {
                                ViewGroup viewGroup2 = fragment.mContainer;
                                if (viewGroup2 != null) {
                                    SpecialEffectsController.getOrCreateController(viewGroup2, fragment.getParentFragmentManager()).enqueueAdd(Operation.State.from(this.mFragment.mView.getVisibility()), this);
                                }
                            }
                            this.mFragment.mState = 4;
                            break;
                        case 5:
                            start();
                            break;
                        case 6:
                            fragment.mState = 6;
                            break;
                        case 7:
                            resume();
                            break;
                        default:
                            break;
                    }
                }
                if (FragmentManager.USE_STATE_MANAGER && fragment.mHiddenChanged) {
                    if (fragment.mView != null) {
                        ViewGroup viewGroup3 = fragment.mContainer;
                        if (viewGroup3 != null) {
                            SpecialEffectsController orCreateController = SpecialEffectsController.getOrCreateController(viewGroup3, fragment.getParentFragmentManager());
                            if (this.mFragment.mHidden) {
                                orCreateController.enqueueHide(this);
                            } else {
                                orCreateController.enqueueShow(this);
                            }
                        }
                    }
                    Fragment fragment2 = this.mFragment;
                    fragment2.mHiddenChanged = false;
                    fragment2.onHiddenChanged(fragment2.mHidden);
                }
                this.mMovingToState = false;
                return;
            }
        } catch (Throwable th) {
            this.mMovingToState = false;
        }
    }

    public void pause() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("movefrom RESUMED: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        this.mFragment.performPause();
        this.mDispatcher.dispatchOnFragmentPaused(this.mFragment, false);
    }

    public void restoreState(@NonNull ClassLoader classLoader) {
        Bundle bundle = this.mFragment.mSavedFragmentState;
        if (bundle != null) {
            bundle.setClassLoader(classLoader);
            Fragment fragment = this.mFragment;
            fragment.mSavedViewState = fragment.mSavedFragmentState.getSparseParcelableArray(VIEW_STATE_TAG);
            fragment = this.mFragment;
            fragment.mSavedViewRegistryState = fragment.mSavedFragmentState.getBundle(VIEW_REGISTRY_STATE_TAG);
            fragment = this.mFragment;
            fragment.mTargetWho = fragment.mSavedFragmentState.getString(TARGET_STATE_TAG);
            fragment = this.mFragment;
            if (fragment.mTargetWho != null) {
                fragment.mTargetRequestCode = fragment.mSavedFragmentState.getInt(TARGET_REQUEST_CODE_STATE_TAG, 0);
            }
            fragment = this.mFragment;
            Boolean bool = fragment.mSavedUserVisibleHint;
            if (bool != null) {
                fragment.mUserVisibleHint = bool.booleanValue();
                this.mFragment.mSavedUserVisibleHint = null;
            } else {
                fragment.mUserVisibleHint = fragment.mSavedFragmentState.getBoolean(USER_VISIBLE_HINT_TAG, true);
            }
            fragment = this.mFragment;
            if (!fragment.mUserVisibleHint) {
                fragment.mDeferStart = true;
            }
        }
    }

    public void resume() {
        String str = "FragmentManager";
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("moveto RESUMED: ");
            L.append(this.mFragment);
            Log.d(str, L.toString());
        }
        View focusedView = this.mFragment.getFocusedView();
        if (focusedView != null && isFragmentViewChild(focusedView)) {
            boolean requestFocus = focusedView.requestFocus();
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("requestFocus: Restoring focused view ");
                stringBuilder.append(focusedView);
                stringBuilder.append(" ");
                stringBuilder.append(requestFocus ? "succeeded" : "failed");
                stringBuilder.append(" on Fragment ");
                stringBuilder.append(this.mFragment);
                stringBuilder.append(" resulting in focused view ");
                stringBuilder.append(this.mFragment.mView.findFocus());
                Log.v(str, stringBuilder.toString());
            }
        }
        this.mFragment.setFocusedView(null);
        this.mFragment.performResume();
        this.mDispatcher.dispatchOnFragmentResumed(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mSavedFragmentState = null;
        fragment.mSavedViewState = null;
        fragment.mSavedViewRegistryState = null;
    }

    @Nullable
    public SavedState saveInstanceState() {
        if (this.mFragment.mState <= -1) {
            return null;
        }
        Bundle saveBasicState = saveBasicState();
        if (saveBasicState != null) {
            return new SavedState(saveBasicState);
        }
        return null;
    }

    @NonNull
    public FragmentState saveState() {
        FragmentState fragmentState = new FragmentState(this.mFragment);
        Fragment fragment = this.mFragment;
        if (fragment.mState <= -1 || fragmentState.mSavedFragmentState != null) {
            fragmentState.mSavedFragmentState = fragment.mSavedFragmentState;
        } else {
            Bundle saveBasicState = saveBasicState();
            fragmentState.mSavedFragmentState = saveBasicState;
            if (this.mFragment.mTargetWho != null) {
                if (saveBasicState == null) {
                    fragmentState.mSavedFragmentState = new Bundle();
                }
                fragmentState.mSavedFragmentState.putString(TARGET_STATE_TAG, this.mFragment.mTargetWho);
                int i = this.mFragment.mTargetRequestCode;
                if (i != 0) {
                    fragmentState.mSavedFragmentState.putInt(TARGET_REQUEST_CODE_STATE_TAG, i);
                }
            }
        }
        return fragmentState;
    }

    public void saveViewState() {
        if (this.mFragment.mView != null) {
            SparseArray sparseArray = new SparseArray();
            this.mFragment.mView.saveHierarchyState(sparseArray);
            if (sparseArray.size() > 0) {
                this.mFragment.mSavedViewState = sparseArray;
            }
            Bundle bundle = new Bundle();
            this.mFragment.mViewLifecycleOwner.performSave(bundle);
            if (!bundle.isEmpty()) {
                this.mFragment.mSavedViewRegistryState = bundle;
            }
        }
    }

    public void setFragmentManagerState(int i) {
        this.mFragmentManagerState = i;
    }

    public void start() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("moveto STARTED: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        this.mFragment.performStart();
        this.mDispatcher.dispatchOnFragmentStarted(this.mFragment, false);
    }

    public void stop() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder L = a.L("movefrom STARTED: ");
            L.append(this.mFragment);
            Log.d("FragmentManager", L.toString());
        }
        this.mFragment.performStop();
        this.mDispatcher.dispatchOnFragmentStopped(this.mFragment, false);
    }

    public FragmentStateManager(@NonNull FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, @NonNull FragmentStore fragmentStore, @NonNull ClassLoader classLoader, @NonNull FragmentFactory fragmentFactory, @NonNull FragmentState fragmentState) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        Fragment instantiate = fragmentFactory.instantiate(classLoader, fragmentState.mClassName);
        this.mFragment = instantiate;
        Bundle bundle = fragmentState.mArguments;
        if (bundle != null) {
            bundle.setClassLoader(classLoader);
        }
        instantiate.setArguments(fragmentState.mArguments);
        instantiate.mWho = fragmentState.mWho;
        instantiate.mFromLayout = fragmentState.mFromLayout;
        instantiate.mRestored = true;
        instantiate.mFragmentId = fragmentState.mFragmentId;
        instantiate.mContainerId = fragmentState.mContainerId;
        instantiate.mTag = fragmentState.mTag;
        instantiate.mRetainInstance = fragmentState.mRetainInstance;
        instantiate.mRemoving = fragmentState.mRemoving;
        instantiate.mDetached = fragmentState.mDetached;
        instantiate.mHidden = fragmentState.mHidden;
        instantiate.mMaxState = State.values()[fragmentState.mMaxLifecycleState];
        bundle = fragmentState.mSavedFragmentState;
        if (bundle != null) {
            instantiate.mSavedFragmentState = bundle;
        } else {
            instantiate.mSavedFragmentState = new Bundle();
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Instantiated fragment ");
            stringBuilder.append(instantiate);
            Log.v("FragmentManager", stringBuilder.toString());
        }
    }

    public FragmentStateManager(@NonNull FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, @NonNull FragmentStore fragmentStore, @NonNull Fragment fragment, @NonNull FragmentState fragmentState) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragment;
        fragment.mSavedViewState = null;
        fragment.mSavedViewRegistryState = null;
        fragment.mBackStackNesting = 0;
        fragment.mInLayout = false;
        fragment.mAdded = false;
        Fragment fragment2 = fragment.mTarget;
        fragment.mTargetWho = fragment2 != null ? fragment2.mWho : null;
        fragment.mTarget = null;
        Bundle bundle = fragmentState.mSavedFragmentState;
        if (bundle != null) {
            fragment.mSavedFragmentState = bundle;
        } else {
            fragment.mSavedFragmentState = new Bundle();
        }
    }
}
