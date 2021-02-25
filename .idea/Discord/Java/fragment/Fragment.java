package androidx.fragment.app;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.ActivityResultRegistryOwner;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.CallSuper;
import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.arch.core.util.Function;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.LayoutInflaterCompat;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.loader.app.LoaderManager;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;
import androidx.savedstate.ViewTreeSavedStateRegistryOwner;
import f.d.b.a.a;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Fragment implements ComponentCallbacks, OnCreateContextMenuListener, LifecycleOwner, ViewModelStoreOwner, HasDefaultViewModelProviderFactory, SavedStateRegistryOwner, ActivityResultCaller {
    public static final int ACTIVITY_CREATED = 4;
    public static final int ATTACHED = 0;
    public static final int AWAITING_ENTER_EFFECTS = 6;
    public static final int AWAITING_EXIT_EFFECTS = 3;
    public static final int CREATED = 1;
    public static final int INITIALIZING = -1;
    public static final int RESUMED = 7;
    public static final int STARTED = 5;
    public static final Object USE_DEFAULT_TRANSITION = new Object();
    public static final int VIEW_CREATED = 2;
    public boolean mAdded;
    public AnimationInfo mAnimationInfo;
    public Bundle mArguments;
    public int mBackStackNesting;
    private boolean mCalled;
    @NonNull
    public FragmentManager mChildFragmentManager;
    public ViewGroup mContainer;
    public int mContainerId;
    @LayoutRes
    private int mContentLayoutId;
    private Factory mDefaultFactory;
    public boolean mDeferStart;
    public boolean mDetached;
    public int mFragmentId;
    public FragmentManager mFragmentManager;
    public boolean mFromLayout;
    public boolean mHasMenu;
    public boolean mHidden;
    public boolean mHiddenChanged;
    public FragmentHostCallback<?> mHost;
    public boolean mInLayout;
    public boolean mIsCreated;
    public boolean mIsNewlyAdded;
    private Boolean mIsPrimaryNavigationFragment;
    public LayoutInflater mLayoutInflater;
    public LifecycleRegistry mLifecycleRegistry;
    public State mMaxState;
    public boolean mMenuVisible;
    private final AtomicInteger mNextLocalRequestCode;
    private final ArrayList<OnPreAttachedListener> mOnPreAttachedListeners;
    public Fragment mParentFragment;
    public boolean mPerformedCreateView;
    public float mPostponedAlpha;
    public Runnable mPostponedDurationRunnable;
    public boolean mRemoving;
    public boolean mRestored;
    public boolean mRetainInstance;
    public boolean mRetainInstanceChangedWhileDetached;
    public Bundle mSavedFragmentState;
    public SavedStateRegistryController mSavedStateRegistryController;
    @Nullable
    public Boolean mSavedUserVisibleHint;
    public Bundle mSavedViewRegistryState;
    public SparseArray<Parcelable> mSavedViewState;
    public int mState;
    public String mTag;
    public Fragment mTarget;
    public int mTargetRequestCode;
    public String mTargetWho;
    public boolean mUserVisibleHint;
    public View mView;
    @Nullable
    public FragmentViewLifecycleOwner mViewLifecycleOwner;
    public MutableLiveData<LifecycleOwner> mViewLifecycleOwnerLiveData;
    @NonNull
    public String mWho;

    public static class AnimationInfo {
        public Boolean mAllowEnterTransitionOverlap;
        public Boolean mAllowReturnTransitionOverlap;
        public View mAnimatingAway;
        public Animator mAnimator;
        public Object mEnterTransition = null;
        public SharedElementCallback mEnterTransitionCallback;
        public boolean mEnterTransitionPostponed;
        public Object mExitTransition;
        public SharedElementCallback mExitTransitionCallback;
        public View mFocusedView;
        public boolean mIsHideReplaced;
        public int mNextAnim;
        public int mNextTransition;
        public float mPostOnViewCreatedAlpha;
        public Object mReenterTransition;
        public Object mReturnTransition;
        public Object mSharedElementEnterTransition;
        public Object mSharedElementReturnTransition;
        public ArrayList<String> mSharedElementSourceNames;
        public ArrayList<String> mSharedElementTargetNames;
        public OnStartEnterTransitionListener mStartEnterTransitionListener;

        public AnimationInfo() {
            Object obj = Fragment.USE_DEFAULT_TRANSITION;
            this.mReturnTransition = obj;
            this.mExitTransition = null;
            this.mReenterTransition = obj;
            this.mSharedElementEnterTransition = null;
            this.mSharedElementReturnTransition = obj;
            this.mEnterTransitionCallback = null;
            this.mExitTransitionCallback = null;
            this.mPostOnViewCreatedAlpha = 1.0f;
            this.mFocusedView = null;
        }
    }

    public static class InstantiationException extends RuntimeException {
        public InstantiationException(@NonNull String str, @Nullable Exception exception) {
            super(str, exception);
        }
    }

    public static abstract class OnPreAttachedListener {
        private OnPreAttachedListener() {
        }

        public abstract void onPreAttached();

        public /* synthetic */ OnPreAttachedListener(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public interface OnStartEnterTransitionListener {
        void onStartEnterTransition();

        void startListening();
    }

    @SuppressLint({"BanParcelableUsage, ParcelClassLoader"})
    public static class SavedState implements Parcelable {
        @NonNull
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        };
        public final Bundle mState;

        public SavedState(Bundle bundle) {
            this.mState = bundle;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeBundle(this.mState);
        }

        public SavedState(@NonNull Parcel parcel, @Nullable ClassLoader classLoader) {
            Bundle readBundle = parcel.readBundle();
            this.mState = readBundle;
            if (classLoader != null && readBundle != null) {
                readBundle.setClassLoader(classLoader);
            }
        }
    }

    public Fragment() {
        this.mState = -1;
        this.mWho = UUID.randomUUID().toString();
        this.mTargetWho = null;
        this.mIsPrimaryNavigationFragment = null;
        this.mChildFragmentManager = new FragmentManagerImpl();
        this.mMenuVisible = true;
        this.mUserVisibleHint = true;
        this.mPostponedDurationRunnable = new Runnable() {
            public void run() {
                Fragment.this.startPostponedEnterTransition();
            }
        };
        this.mMaxState = State.RESUMED;
        this.mViewLifecycleOwnerLiveData = new MutableLiveData();
        this.mNextLocalRequestCode = new AtomicInteger();
        this.mOnPreAttachedListeners = new ArrayList();
        initLifecycle();
    }

    private AnimationInfo ensureAnimationInfo() {
        if (this.mAnimationInfo == null) {
            this.mAnimationInfo = new AnimationInfo();
        }
        return this.mAnimationInfo;
    }

    private int getMinimumMaxLifecycleState() {
        State state = this.mMaxState;
        if (state == State.INITIALIZED || this.mParentFragment == null) {
            return state.ordinal();
        }
        return Math.min(state.ordinal(), this.mParentFragment.getMinimumMaxLifecycleState());
    }

    private void initLifecycle() {
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mSavedStateRegistryController = SavedStateRegistryController.create(this);
    }

    @NonNull
    @Deprecated
    public static Fragment instantiate(@NonNull Context context, @NonNull String str) {
        return instantiate(context, str, null);
    }

    @NonNull
    private <I, O> ActivityResultLauncher<I> prepareCallInternal(@NonNull final ActivityResultContract<I, O> activityResultContract, @NonNull Function<Void, ActivityResultRegistry> function, @NonNull ActivityResultCallback<O> activityResultCallback) {
        if (this.mState <= 1) {
            final AtomicReference atomicReference = new AtomicReference();
            final Function<Void, ActivityResultRegistry> function2 = function;
            final AtomicReference atomicReference2 = atomicReference;
            final ActivityResultContract<I, O> activityResultContract2 = activityResultContract;
            final ActivityResultCallback<O> activityResultCallback2 = activityResultCallback;
            registerOnPreAttachListener(new OnPreAttachedListener() {
                public void onPreAttached() {
                    ActivityResultRegistry activityResultRegistry = (ActivityResultRegistry) function2.apply(null);
                    atomicReference2.set(activityResultRegistry.register(Fragment.this.generateActivityResultKey(), Fragment.this, activityResultContract2, activityResultCallback2));
                }
            });
            return new ActivityResultLauncher<I>() {
                @NonNull
                public ActivityResultContract<I, ?> getContract() {
                    return activityResultContract;
                }

                public void launch(I i, @Nullable ActivityOptionsCompat activityOptionsCompat) {
                    ActivityResultLauncher activityResultLauncher = (ActivityResultLauncher) atomicReference.get();
                    if (activityResultLauncher != null) {
                        activityResultLauncher.launch(i, activityOptionsCompat);
                        return;
                    }
                    throw new IllegalStateException("Operation cannot be started before fragment is in created state");
                }

                public void unregister() {
                    ActivityResultLauncher activityResultLauncher = (ActivityResultLauncher) atomicReference.getAndSet(null);
                    if (activityResultLauncher != null) {
                        activityResultLauncher.unregister();
                    }
                }
            };
        }
        throw new IllegalStateException(a.r("Fragment ", this, " is attempting to registerForActivityResult after being created. Fragments must call registerForActivityResult() before they are created (i.e. initialization, onAttach(), or onCreate())."));
    }

    private void registerOnPreAttachListener(@NonNull OnPreAttachedListener onPreAttachedListener) {
        if (this.mState >= 0) {
            onPreAttachedListener.onPreAttached();
        } else {
            this.mOnPreAttachedListeners.add(onPreAttachedListener);
        }
    }

    public void callStartTransitionListener(boolean z) {
        AnimationInfo animationInfo = this.mAnimationInfo;
        OnStartEnterTransitionListener onStartEnterTransitionListener = null;
        if (animationInfo != null) {
            animationInfo.mEnterTransitionPostponed = false;
            OnStartEnterTransitionListener onStartEnterTransitionListener2 = animationInfo.mStartEnterTransitionListener;
            animationInfo.mStartEnterTransitionListener = null;
            onStartEnterTransitionListener = onStartEnterTransitionListener2;
        }
        if (onStartEnterTransitionListener != null) {
            onStartEnterTransitionListener.onStartEnterTransition();
        } else if (FragmentManager.USE_STATE_MANAGER && this.mView != null) {
            ViewGroup viewGroup = this.mContainer;
            if (viewGroup != null) {
                FragmentManager fragmentManager = this.mFragmentManager;
                if (fragmentManager != null) {
                    final SpecialEffectsController orCreateController = SpecialEffectsController.getOrCreateController(viewGroup, fragmentManager);
                    orCreateController.markPostponedState();
                    if (z) {
                        this.mHost.getHandler().post(new Runnable() {
                            public void run() {
                                orCreateController.executePendingOperations();
                            }
                        });
                    } else {
                        orCreateController.executePendingOperations();
                    }
                }
            }
        }
    }

    @NonNull
    public FragmentContainer createFragmentContainer() {
        return new FragmentContainer() {
            @Nullable
            public View onFindViewById(int i) {
                View view = Fragment.this.mView;
                if (view != null) {
                    return view.findViewById(i);
                }
                StringBuilder L = a.L("Fragment ");
                L.append(Fragment.this);
                L.append(" does not have a view");
                throw new IllegalStateException(L.toString());
            }

            public boolean onHasView() {
                return Fragment.this.mView != null;
            }
        };
    }

    public void dump(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
        printWriter.print(str);
        printWriter.print("mFragmentId=#");
        printWriter.print(Integer.toHexString(this.mFragmentId));
        printWriter.print(" mContainerId=#");
        printWriter.print(Integer.toHexString(this.mContainerId));
        printWriter.print(" mTag=");
        printWriter.println(this.mTag);
        printWriter.print(str);
        printWriter.print("mState=");
        printWriter.print(this.mState);
        printWriter.print(" mWho=");
        printWriter.print(this.mWho);
        printWriter.print(" mBackStackNesting=");
        printWriter.println(this.mBackStackNesting);
        printWriter.print(str);
        printWriter.print("mAdded=");
        printWriter.print(this.mAdded);
        printWriter.print(" mRemoving=");
        printWriter.print(this.mRemoving);
        printWriter.print(" mFromLayout=");
        printWriter.print(this.mFromLayout);
        printWriter.print(" mInLayout=");
        printWriter.println(this.mInLayout);
        printWriter.print(str);
        printWriter.print("mHidden=");
        printWriter.print(this.mHidden);
        printWriter.print(" mDetached=");
        printWriter.print(this.mDetached);
        printWriter.print(" mMenuVisible=");
        printWriter.print(this.mMenuVisible);
        printWriter.print(" mHasMenu=");
        printWriter.println(this.mHasMenu);
        printWriter.print(str);
        printWriter.print("mRetainInstance=");
        printWriter.print(this.mRetainInstance);
        printWriter.print(" mUserVisibleHint=");
        printWriter.println(this.mUserVisibleHint);
        if (this.mFragmentManager != null) {
            printWriter.print(str);
            printWriter.print("mFragmentManager=");
            printWriter.println(this.mFragmentManager);
        }
        if (this.mHost != null) {
            printWriter.print(str);
            printWriter.print("mHost=");
            printWriter.println(this.mHost);
        }
        if (this.mParentFragment != null) {
            printWriter.print(str);
            printWriter.print("mParentFragment=");
            printWriter.println(this.mParentFragment);
        }
        if (this.mArguments != null) {
            printWriter.print(str);
            printWriter.print("mArguments=");
            printWriter.println(this.mArguments);
        }
        if (this.mSavedFragmentState != null) {
            printWriter.print(str);
            printWriter.print("mSavedFragmentState=");
            printWriter.println(this.mSavedFragmentState);
        }
        if (this.mSavedViewState != null) {
            printWriter.print(str);
            printWriter.print("mSavedViewState=");
            printWriter.println(this.mSavedViewState);
        }
        if (this.mSavedViewRegistryState != null) {
            printWriter.print(str);
            printWriter.print("mSavedViewRegistryState=");
            printWriter.println(this.mSavedViewRegistryState);
        }
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            printWriter.print(str);
            printWriter.print("mTarget=");
            printWriter.print(targetFragment);
            printWriter.print(" mTargetRequestCode=");
            printWriter.println(this.mTargetRequestCode);
        }
        if (getNextAnim() != 0) {
            printWriter.print(str);
            printWriter.print("mNextAnim=");
            printWriter.println(getNextAnim());
        }
        if (this.mContainer != null) {
            printWriter.print(str);
            printWriter.print("mContainer=");
            printWriter.println(this.mContainer);
        }
        if (this.mView != null) {
            printWriter.print(str);
            printWriter.print("mView=");
            printWriter.println(this.mView);
        }
        if (getAnimatingAway() != null) {
            printWriter.print(str);
            printWriter.print("mAnimatingAway=");
            printWriter.println(getAnimatingAway());
        }
        if (getContext() != null) {
            LoaderManager.getInstance(this).dump(str, fileDescriptor, printWriter, strArr);
        }
        printWriter.print(str);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Child ");
        stringBuilder.append(this.mChildFragmentManager);
        stringBuilder.append(":");
        printWriter.println(stringBuilder.toString());
        this.mChildFragmentManager.dump(a.t(str, "  "), fileDescriptor, printWriter, strArr);
    }

    public final boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Nullable
    public Fragment findFragmentByWho(@NonNull String str) {
        if (str.equals(this.mWho)) {
            return this;
        }
        return this.mChildFragmentManager.findFragmentByWho(str);
    }

    @NonNull
    public String generateActivityResultKey() {
        StringBuilder L = a.L("fragment_");
        L.append(this.mWho);
        L.append("_rq#");
        L.append(this.mNextLocalRequestCode.getAndIncrement());
        return L.toString();
    }

    @Nullable
    public final FragmentActivity getActivity() {
        FragmentHostCallback fragmentHostCallback = this.mHost;
        return fragmentHostCallback == null ? null : (FragmentActivity) fragmentHostCallback.getActivity();
    }

    public boolean getAllowEnterTransitionOverlap() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo != null) {
            Boolean bool = animationInfo.mAllowEnterTransitionOverlap;
            if (bool != null) {
                return bool.booleanValue();
            }
        }
        return true;
    }

    public boolean getAllowReturnTransitionOverlap() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo != null) {
            Boolean bool = animationInfo.mAllowReturnTransitionOverlap;
            if (bool != null) {
                return bool.booleanValue();
            }
        }
        return true;
    }

    public View getAnimatingAway() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mAnimatingAway;
    }

    public Animator getAnimator() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mAnimator;
    }

    @Nullable
    public final Bundle getArguments() {
        return this.mArguments;
    }

    @NonNull
    public final FragmentManager getChildFragmentManager() {
        if (this.mHost != null) {
            return this.mChildFragmentManager;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " has not been attached yet."));
    }

    @Nullable
    public Context getContext() {
        FragmentHostCallback fragmentHostCallback = this.mHost;
        return fragmentHostCallback == null ? null : fragmentHostCallback.getContext();
    }

    @NonNull
    public Factory getDefaultViewModelProviderFactory() {
        if (this.mFragmentManager != null) {
            if (this.mDefaultFactory == null) {
                Application application = null;
                for (Context applicationContext = requireContext().getApplicationContext(); applicationContext instanceof ContextWrapper; applicationContext = ((ContextWrapper) applicationContext).getBaseContext()) {
                    if (applicationContext instanceof Application) {
                        application = (Application) applicationContext;
                        break;
                    }
                }
                if (application == null && FragmentManager.isLoggingEnabled(3)) {
                    StringBuilder L = a.L("Could not find Application instance from Context ");
                    L.append(requireContext().getApplicationContext());
                    L.append(", you will not be able to use AndroidViewModel with the default ViewModelProvider.Factory");
                    Log.d(FragmentManager.TAG, L.toString());
                }
                this.mDefaultFactory = new SavedStateViewModelFactory(application, this, getArguments());
            }
            return this.mDefaultFactory;
        }
        throw new IllegalStateException("Can't access ViewModels from detached fragment");
    }

    @Nullable
    public Object getEnterTransition() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mEnterTransition;
    }

    public SharedElementCallback getEnterTransitionCallback() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mEnterTransitionCallback;
    }

    @Nullable
    public Object getExitTransition() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mExitTransition;
    }

    public SharedElementCallback getExitTransitionCallback() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mExitTransitionCallback;
    }

    public View getFocusedView() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mFocusedView;
    }

    @Deprecated
    @Nullable
    public final FragmentManager getFragmentManager() {
        return this.mFragmentManager;
    }

    @Nullable
    public final Object getHost() {
        FragmentHostCallback fragmentHostCallback = this.mHost;
        return fragmentHostCallback == null ? null : fragmentHostCallback.onGetHost();
    }

    public final int getId() {
        return this.mFragmentId;
    }

    @NonNull
    public final LayoutInflater getLayoutInflater() {
        LayoutInflater layoutInflater = this.mLayoutInflater;
        return layoutInflater == null ? performGetLayoutInflater(null) : layoutInflater;
    }

    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    @NonNull
    @Deprecated
    public LoaderManager getLoaderManager() {
        return LoaderManager.getInstance(this);
    }

    public int getNextAnim() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return 0;
        }
        return animationInfo.mNextAnim;
    }

    public int getNextTransition() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return 0;
        }
        return animationInfo.mNextTransition;
    }

    @Nullable
    public final Fragment getParentFragment() {
        return this.mParentFragment;
    }

    @NonNull
    public final FragmentManager getParentFragmentManager() {
        FragmentManager fragmentManager = this.mFragmentManager;
        if (fragmentManager != null) {
            return fragmentManager;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " not associated with a fragment manager."));
    }

    public float getPostOnViewCreatedAlpha() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return 1.0f;
        }
        return animationInfo.mPostOnViewCreatedAlpha;
    }

    @Nullable
    public Object getReenterTransition() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        Object obj = animationInfo.mReenterTransition;
        if (obj == USE_DEFAULT_TRANSITION) {
            obj = getExitTransition();
        }
        return obj;
    }

    @NonNull
    public final Resources getResources() {
        return requireContext().getResources();
    }

    @Deprecated
    public final boolean getRetainInstance() {
        return this.mRetainInstance;
    }

    @Nullable
    public Object getReturnTransition() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        Object obj = animationInfo.mReturnTransition;
        if (obj == USE_DEFAULT_TRANSITION) {
            obj = getEnterTransition();
        }
        return obj;
    }

    @NonNull
    public final SavedStateRegistry getSavedStateRegistry() {
        return this.mSavedStateRegistryController.getSavedStateRegistry();
    }

    @Nullable
    public Object getSharedElementEnterTransition() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        return animationInfo.mSharedElementEnterTransition;
    }

    @Nullable
    public Object getSharedElementReturnTransition() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return null;
        }
        Object obj = animationInfo.mSharedElementReturnTransition;
        if (obj == USE_DEFAULT_TRANSITION) {
            obj = getSharedElementEnterTransition();
        }
        return obj;
    }

    @NonNull
    public ArrayList<String> getSharedElementSourceNames() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo != null) {
            ArrayList arrayList = animationInfo.mSharedElementSourceNames;
            if (arrayList != null) {
                return arrayList;
            }
        }
        return new ArrayList();
    }

    @NonNull
    public ArrayList<String> getSharedElementTargetNames() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo != null) {
            ArrayList arrayList = animationInfo.mSharedElementTargetNames;
            if (arrayList != null) {
                return arrayList;
            }
        }
        return new ArrayList();
    }

    @NonNull
    public final String getString(@StringRes int i) {
        return getResources().getString(i);
    }

    @Nullable
    public final String getTag() {
        return this.mTag;
    }

    @Deprecated
    @Nullable
    public final Fragment getTargetFragment() {
        Fragment fragment = this.mTarget;
        if (fragment != null) {
            return fragment;
        }
        FragmentManager fragmentManager = this.mFragmentManager;
        if (fragmentManager != null) {
            String str = this.mTargetWho;
            if (str != null) {
                return fragmentManager.findActiveFragment(str);
            }
        }
        return null;
    }

    @Deprecated
    public final int getTargetRequestCode() {
        return this.mTargetRequestCode;
    }

    @NonNull
    public final CharSequence getText(@StringRes int i) {
        return getResources().getText(i);
    }

    @Deprecated
    public boolean getUserVisibleHint() {
        return this.mUserVisibleHint;
    }

    @Nullable
    public View getView() {
        return this.mView;
    }

    @MainThread
    @NonNull
    public LifecycleOwner getViewLifecycleOwner() {
        FragmentViewLifecycleOwner fragmentViewLifecycleOwner = this.mViewLifecycleOwner;
        if (fragmentViewLifecycleOwner != null) {
            return fragmentViewLifecycleOwner;
        }
        throw new IllegalStateException("Can't access the Fragment View's LifecycleOwner when getView() is null i.e., before onCreateView() or after onDestroyView()");
    }

    @NonNull
    public LiveData<LifecycleOwner> getViewLifecycleOwnerLiveData() {
        return this.mViewLifecycleOwnerLiveData;
    }

    @NonNull
    public ViewModelStore getViewModelStore() {
        if (this.mFragmentManager != null) {
            int minimumMaxLifecycleState = getMinimumMaxLifecycleState();
            State state = State.INITIALIZED;
            if (minimumMaxLifecycleState != 1) {
                return this.mFragmentManager.getViewModelStore(this);
            }
            throw new IllegalStateException("Calling getViewModelStore() before a Fragment reaches onCreate() when using setMaxLifecycle(INITIALIZED) is not supported");
        }
        throw new IllegalStateException("Can't access ViewModels from detached fragment");
    }

    @SuppressLint({"KotlinPropertyAccess"})
    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public final boolean hasOptionsMenu() {
        return this.mHasMenu;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public void initState() {
        initLifecycle();
        this.mWho = UUID.randomUUID().toString();
        this.mAdded = false;
        this.mRemoving = false;
        this.mFromLayout = false;
        this.mInLayout = false;
        this.mRestored = false;
        this.mBackStackNesting = 0;
        this.mFragmentManager = null;
        this.mChildFragmentManager = new FragmentManagerImpl();
        this.mHost = null;
        this.mFragmentId = 0;
        this.mContainerId = 0;
        this.mTag = null;
        this.mHidden = false;
        this.mDetached = false;
    }

    public final boolean isAdded() {
        return this.mHost != null && this.mAdded;
    }

    public final boolean isDetached() {
        return this.mDetached;
    }

    public final boolean isHidden() {
        return this.mHidden;
    }

    public boolean isHideReplaced() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return false;
        }
        return animationInfo.mIsHideReplaced;
    }

    public final boolean isInBackStack() {
        return this.mBackStackNesting > 0;
    }

    public final boolean isInLayout() {
        return this.mInLayout;
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public final boolean isMenuVisible() {
        if (this.mMenuVisible) {
            FragmentManager fragmentManager = this.mFragmentManager;
            if (fragmentManager == null || fragmentManager.isParentMenuVisible(this.mParentFragment)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPostponed() {
        AnimationInfo animationInfo = this.mAnimationInfo;
        if (animationInfo == null) {
            return false;
        }
        return animationInfo.mEnterTransitionPostponed;
    }

    public final boolean isRemoving() {
        return this.mRemoving;
    }

    public final boolean isRemovingParent() {
        Fragment parentFragment = getParentFragment();
        return parentFragment != null && (parentFragment.isRemoving() || parentFragment.isRemovingParent());
    }

    public final boolean isResumed() {
        return this.mState >= 7;
    }

    public final boolean isStateSaved() {
        FragmentManager fragmentManager = this.mFragmentManager;
        if (fragmentManager == null) {
            return false;
        }
        return fragmentManager.isStateSaved();
    }

    public final boolean isVisible() {
        if (isAdded() && !isHidden()) {
            View view = this.mView;
            if (!(view == null || view.getWindowToken() == null || this.mView.getVisibility() != 0)) {
                return true;
            }
        }
        return false;
    }

    public void noteStateNotSaved() {
        this.mChildFragmentManager.noteStateNotSaved();
    }

    @CallSuper
    @MainThread
    @Deprecated
    public void onActivityCreated(@Nullable Bundle bundle) {
        this.mCalled = true;
    }

    @Deprecated
    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fragment ");
            stringBuilder.append(this);
            stringBuilder.append(" received the following in onActivityResult(): requestCode: ");
            stringBuilder.append(i);
            stringBuilder.append(" resultCode: ");
            stringBuilder.append(i2);
            stringBuilder.append(" data: ");
            stringBuilder.append(intent);
            Log.v(FragmentManager.TAG, stringBuilder.toString());
        }
    }

    @CallSuper
    @MainThread
    public void onAttach(@NonNull Context context) {
        this.mCalled = true;
        FragmentHostCallback fragmentHostCallback = this.mHost;
        Activity activity = fragmentHostCallback == null ? null : fragmentHostCallback.getActivity();
        if (activity != null) {
            this.mCalled = false;
            onAttach(activity);
        }
    }

    @MainThread
    @Deprecated
    public void onAttachFragment(@NonNull Fragment fragment) {
    }

    @CallSuper
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        this.mCalled = true;
    }

    @MainThread
    public boolean onContextItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @CallSuper
    @MainThread
    public void onCreate(@Nullable Bundle bundle) {
        this.mCalled = true;
        restoreChildFragmentState(bundle);
        if (!this.mChildFragmentManager.isStateAtLeast(1)) {
            this.mChildFragmentManager.dispatchCreate();
        }
    }

    @MainThread
    @Nullable
    public Animation onCreateAnimation(int i, boolean z, int i2) {
        return null;
    }

    @MainThread
    @Nullable
    public Animator onCreateAnimator(int i, boolean z, int i2) {
        return null;
    }

    @MainThread
    public void onCreateContextMenu(@NonNull ContextMenu contextMenu, @NonNull View view, @Nullable ContextMenuInfo contextMenuInfo) {
        requireActivity().onCreateContextMenu(contextMenu, view, contextMenuInfo);
    }

    @MainThread
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
    }

    @MainThread
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        int i = this.mContentLayoutId;
        return i != 0 ? layoutInflater.inflate(i, viewGroup, false) : null;
    }

    @CallSuper
    @MainThread
    public void onDestroy() {
        this.mCalled = true;
    }

    @MainThread
    public void onDestroyOptionsMenu() {
    }

    @CallSuper
    @MainThread
    public void onDestroyView() {
        this.mCalled = true;
    }

    @CallSuper
    @MainThread
    public void onDetach() {
        this.mCalled = true;
    }

    @NonNull
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle bundle) {
        return getLayoutInflater(bundle);
    }

    @MainThread
    public void onHiddenChanged(boolean z) {
    }

    @CallSuper
    @UiThread
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attributeSet, @Nullable Bundle bundle) {
        this.mCalled = true;
        FragmentHostCallback fragmentHostCallback = this.mHost;
        Activity activity = fragmentHostCallback == null ? null : fragmentHostCallback.getActivity();
        if (activity != null) {
            this.mCalled = false;
            onInflate(activity, attributeSet, bundle);
        }
    }

    @CallSuper
    @MainThread
    public void onLowMemory() {
        this.mCalled = true;
    }

    public void onMultiWindowModeChanged(boolean z) {
    }

    @MainThread
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @MainThread
    public void onOptionsMenuClosed(@NonNull Menu menu) {
    }

    @CallSuper
    @MainThread
    public void onPause() {
        this.mCalled = true;
    }

    public void onPictureInPictureModeChanged(boolean z) {
    }

    @MainThread
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
    }

    @MainThread
    public void onPrimaryNavigationFragmentChanged(boolean z) {
    }

    @Deprecated
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
    }

    @CallSuper
    @MainThread
    public void onResume() {
        this.mCalled = true;
    }

    @MainThread
    public void onSaveInstanceState(@NonNull Bundle bundle) {
    }

    @CallSuper
    @MainThread
    public void onStart() {
        this.mCalled = true;
    }

    @CallSuper
    @MainThread
    public void onStop() {
        this.mCalled = true;
    }

    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
    }

    @CallSuper
    @MainThread
    public void onViewStateRestored(@Nullable Bundle bundle) {
        this.mCalled = true;
    }

    public void performActivityCreated(Bundle bundle) {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mState = 3;
        this.mCalled = false;
        onActivityCreated(bundle);
        if (this.mCalled) {
            restoreViewState();
            this.mChildFragmentManager.dispatchActivityCreated();
            return;
        }
        throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onActivityCreated()"));
    }

    public void performAttach() {
        Iterator it = this.mOnPreAttachedListeners.iterator();
        while (it.hasNext()) {
            ((OnPreAttachedListener) it.next()).onPreAttached();
        }
        this.mOnPreAttachedListeners.clear();
        this.mChildFragmentManager.attachController(this.mHost, createFragmentContainer(), this);
        this.mState = 0;
        this.mCalled = false;
        onAttach(this.mHost.getContext());
        if (this.mCalled) {
            this.mFragmentManager.dispatchOnAttachFragment(this);
            this.mChildFragmentManager.dispatchAttach();
            return;
        }
        throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onAttach()"));
    }

    public void performConfigurationChanged(@NonNull Configuration configuration) {
        onConfigurationChanged(configuration);
        this.mChildFragmentManager.dispatchConfigurationChanged(configuration);
    }

    public boolean performContextItemSelected(@NonNull MenuItem menuItem) {
        if (this.mHidden) {
            return false;
        }
        if (onContextItemSelected(menuItem)) {
            return true;
        }
        return this.mChildFragmentManager.dispatchContextItemSelected(menuItem);
    }

    public void performCreate(Bundle bundle) {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mState = 1;
        this.mCalled = false;
        this.mLifecycleRegistry.addObserver(new LifecycleEventObserver() {
            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
                if (event == Event.ON_STOP) {
                    View view = Fragment.this.mView;
                    if (view != null) {
                        view.cancelPendingInputEvents();
                    }
                }
            }
        });
        this.mSavedStateRegistryController.performRestore(bundle);
        onCreate(bundle);
        this.mIsCreated = true;
        if (this.mCalled) {
            this.mLifecycleRegistry.handleLifecycleEvent(Event.ON_CREATE);
            return;
        }
        throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onCreate()"));
    }

    public boolean performCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        int i = 0;
        if (this.mHidden) {
            return false;
        }
        if (this.mHasMenu && this.mMenuVisible) {
            i = 1;
            onCreateOptionsMenu(menu, menuInflater);
        }
        return i | this.mChildFragmentManager.dispatchCreateOptionsMenu(menu, menuInflater);
    }

    public void performCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mPerformedCreateView = true;
        this.mViewLifecycleOwner = new FragmentViewLifecycleOwner();
        View onCreateView = onCreateView(layoutInflater, viewGroup, bundle);
        this.mView = onCreateView;
        if (onCreateView != null) {
            this.mViewLifecycleOwner.initialize();
            ViewTreeLifecycleOwner.set(this.mView, this.mViewLifecycleOwner);
            ViewTreeViewModelStoreOwner.set(this.mView, this);
            ViewTreeSavedStateRegistryOwner.set(this.mView, this.mViewLifecycleOwner);
            this.mViewLifecycleOwnerLiveData.setValue(this.mViewLifecycleOwner);
        } else if (this.mViewLifecycleOwner.isInitialized()) {
            throw new IllegalStateException("Called getViewLifecycleOwner() but onCreateView() returned null");
        } else {
            this.mViewLifecycleOwner = null;
        }
    }

    public void performDestroy() {
        this.mChildFragmentManager.dispatchDestroy();
        this.mLifecycleRegistry.handleLifecycleEvent(Event.ON_DESTROY);
        this.mState = 0;
        this.mCalled = false;
        this.mIsCreated = false;
        onDestroy();
        if (!this.mCalled) {
            throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onDestroy()"));
        }
    }

    public void performDestroyView() {
        this.mChildFragmentManager.dispatchDestroyView();
        if (this.mView != null) {
            this.mViewLifecycleOwner.handleLifecycleEvent(Event.ON_DESTROY);
        }
        this.mState = 1;
        this.mCalled = false;
        onDestroyView();
        if (this.mCalled) {
            LoaderManager.getInstance(this).markForRedelivery();
            this.mPerformedCreateView = false;
            return;
        }
        throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onDestroyView()"));
    }

    public void performDetach() {
        this.mState = -1;
        this.mCalled = false;
        onDetach();
        this.mLayoutInflater = null;
        if (!this.mCalled) {
            throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onDetach()"));
        } else if (!this.mChildFragmentManager.isDestroyed()) {
            this.mChildFragmentManager.dispatchDestroy();
            this.mChildFragmentManager = new FragmentManagerImpl();
        }
    }

    @NonNull
    public LayoutInflater performGetLayoutInflater(@Nullable Bundle bundle) {
        LayoutInflater onGetLayoutInflater = onGetLayoutInflater(bundle);
        this.mLayoutInflater = onGetLayoutInflater;
        return onGetLayoutInflater;
    }

    public void performLowMemory() {
        onLowMemory();
        this.mChildFragmentManager.dispatchLowMemory();
    }

    public void performMultiWindowModeChanged(boolean z) {
        onMultiWindowModeChanged(z);
        this.mChildFragmentManager.dispatchMultiWindowModeChanged(z);
    }

    public boolean performOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (this.mHidden) {
            return false;
        }
        if (this.mHasMenu && this.mMenuVisible && onOptionsItemSelected(menuItem)) {
            return true;
        }
        return this.mChildFragmentManager.dispatchOptionsItemSelected(menuItem);
    }

    public void performOptionsMenuClosed(@NonNull Menu menu) {
        if (!this.mHidden) {
            if (this.mHasMenu && this.mMenuVisible) {
                onOptionsMenuClosed(menu);
            }
            this.mChildFragmentManager.dispatchOptionsMenuClosed(menu);
        }
    }

    public void performPause() {
        this.mChildFragmentManager.dispatchPause();
        if (this.mView != null) {
            this.mViewLifecycleOwner.handleLifecycleEvent(Event.ON_PAUSE);
        }
        this.mLifecycleRegistry.handleLifecycleEvent(Event.ON_PAUSE);
        this.mState = 6;
        this.mCalled = false;
        onPause();
        if (!this.mCalled) {
            throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onPause()"));
        }
    }

    public void performPictureInPictureModeChanged(boolean z) {
        onPictureInPictureModeChanged(z);
        this.mChildFragmentManager.dispatchPictureInPictureModeChanged(z);
    }

    public boolean performPrepareOptionsMenu(@NonNull Menu menu) {
        int i = 0;
        if (this.mHidden) {
            return false;
        }
        if (this.mHasMenu && this.mMenuVisible) {
            i = 1;
            onPrepareOptionsMenu(menu);
        }
        return i | this.mChildFragmentManager.dispatchPrepareOptionsMenu(menu);
    }

    public void performPrimaryNavigationFragmentChanged() {
        boolean isPrimaryNavigation = this.mFragmentManager.isPrimaryNavigation(this);
        Boolean bool = this.mIsPrimaryNavigationFragment;
        if (bool == null || bool.booleanValue() != isPrimaryNavigation) {
            this.mIsPrimaryNavigationFragment = Boolean.valueOf(isPrimaryNavigation);
            onPrimaryNavigationFragmentChanged(isPrimaryNavigation);
            this.mChildFragmentManager.dispatchPrimaryNavigationFragmentChanged();
        }
    }

    public void performResume() {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mChildFragmentManager.execPendingActions(true);
        this.mState = 7;
        this.mCalled = false;
        onResume();
        if (this.mCalled) {
            LifecycleRegistry lifecycleRegistry = this.mLifecycleRegistry;
            Event event = Event.ON_RESUME;
            lifecycleRegistry.handleLifecycleEvent(event);
            if (this.mView != null) {
                this.mViewLifecycleOwner.handleLifecycleEvent(event);
            }
            this.mChildFragmentManager.dispatchResume();
            return;
        }
        throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onResume()"));
    }

    public void performSaveInstanceState(Bundle bundle) {
        onSaveInstanceState(bundle);
        this.mSavedStateRegistryController.performSave(bundle);
        Parcelable saveAllState = this.mChildFragmentManager.saveAllState();
        if (saveAllState != null) {
            bundle.putParcelable(FragmentActivity.FRAGMENTS_TAG, saveAllState);
        }
    }

    public void performStart() {
        this.mChildFragmentManager.noteStateNotSaved();
        this.mChildFragmentManager.execPendingActions(true);
        this.mState = 5;
        this.mCalled = false;
        onStart();
        if (this.mCalled) {
            LifecycleRegistry lifecycleRegistry = this.mLifecycleRegistry;
            Event event = Event.ON_START;
            lifecycleRegistry.handleLifecycleEvent(event);
            if (this.mView != null) {
                this.mViewLifecycleOwner.handleLifecycleEvent(event);
            }
            this.mChildFragmentManager.dispatchStart();
            return;
        }
        throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onStart()"));
    }

    public void performStop() {
        this.mChildFragmentManager.dispatchStop();
        if (this.mView != null) {
            this.mViewLifecycleOwner.handleLifecycleEvent(Event.ON_STOP);
        }
        this.mLifecycleRegistry.handleLifecycleEvent(Event.ON_STOP);
        this.mState = 4;
        this.mCalled = false;
        onStop();
        if (!this.mCalled) {
            throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onStop()"));
        }
    }

    public void performViewCreated() {
        onViewCreated(this.mView, this.mSavedFragmentState);
        this.mChildFragmentManager.dispatchViewCreated();
    }

    public void postponeEnterTransition() {
        ensureAnimationInfo().mEnterTransitionPostponed = true;
    }

    @MainThread
    @NonNull
    public final <I, O> ActivityResultLauncher<I> registerForActivityResult(@NonNull ActivityResultContract<I, O> activityResultContract, @NonNull ActivityResultCallback<O> activityResultCallback) {
        return prepareCallInternal(activityResultContract, new Function<Void, ActivityResultRegistry>() {
            public ActivityResultRegistry apply(Void voidR) {
                Fragment fragment = Fragment.this;
                FragmentHostCallback fragmentHostCallback = fragment.mHost;
                if (fragmentHostCallback instanceof ActivityResultRegistryOwner) {
                    return ((ActivityResultRegistryOwner) fragmentHostCallback).getActivityResultRegistry();
                }
                return fragment.requireActivity().getActivityResultRegistry();
            }
        }, activityResultCallback);
    }

    public void registerForContextMenu(@NonNull View view) {
        view.setOnCreateContextMenuListener(this);
    }

    @Deprecated
    public final void requestPermissions(@NonNull String[] strArr, int i) {
        if (this.mHost != null) {
            getParentFragmentManager().launchRequestPermissions(this, strArr, i);
            return;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " not attached to Activity"));
    }

    @NonNull
    public final FragmentActivity requireActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return activity;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " not attached to an activity."));
    }

    @NonNull
    public final Bundle requireArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " does not have any arguments."));
    }

    @NonNull
    public final Context requireContext() {
        Context context = getContext();
        if (context != null) {
            return context;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " not attached to a context."));
    }

    @NonNull
    @Deprecated
    public final FragmentManager requireFragmentManager() {
        return getParentFragmentManager();
    }

    @NonNull
    public final Object requireHost() {
        Object host = getHost();
        if (host != null) {
            return host;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " not attached to a host."));
    }

    @NonNull
    public final Fragment requireParentFragment() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            return parentFragment;
        }
        String str = "Fragment ";
        if (getContext() == null) {
            throw new IllegalStateException(a.r(str, this, " is not attached to any Fragment or host"));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(this);
        stringBuilder.append(" is not a child Fragment, it is directly attached to ");
        stringBuilder.append(getContext());
        throw new IllegalStateException(stringBuilder.toString());
    }

    @NonNull
    public final View requireView() {
        View view = getView();
        if (view != null) {
            return view;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " did not return a View from onCreateView() or this was called before onCreateView()."));
    }

    public void restoreChildFragmentState(@Nullable Bundle bundle) {
        if (bundle != null) {
            Parcelable parcelable = bundle.getParcelable(FragmentActivity.FRAGMENTS_TAG);
            if (parcelable != null) {
                this.mChildFragmentManager.restoreSaveState(parcelable);
                this.mChildFragmentManager.dispatchCreate();
            }
        }
    }

    public final void restoreViewState(Bundle bundle) {
        SparseArray sparseArray = this.mSavedViewState;
        if (sparseArray != null) {
            this.mView.restoreHierarchyState(sparseArray);
            this.mSavedViewState = null;
        }
        if (this.mView != null) {
            this.mViewLifecycleOwner.performRestore(this.mSavedViewRegistryState);
            this.mSavedViewRegistryState = null;
        }
        this.mCalled = false;
        onViewStateRestored(bundle);
        if (!this.mCalled) {
            throw new SuperNotCalledException(a.r("Fragment ", this, " did not call through to super.onViewStateRestored()"));
        } else if (this.mView != null) {
            this.mViewLifecycleOwner.handleLifecycleEvent(Event.ON_CREATE);
        }
    }

    public void setAllowEnterTransitionOverlap(boolean z) {
        ensureAnimationInfo().mAllowEnterTransitionOverlap = Boolean.valueOf(z);
    }

    public void setAllowReturnTransitionOverlap(boolean z) {
        ensureAnimationInfo().mAllowReturnTransitionOverlap = Boolean.valueOf(z);
    }

    public void setAnimatingAway(View view) {
        ensureAnimationInfo().mAnimatingAway = view;
    }

    public void setAnimator(Animator animator) {
        ensureAnimationInfo().mAnimator = animator;
    }

    public void setArguments(@Nullable Bundle bundle) {
        if (this.mFragmentManager == null || !isStateSaved()) {
            this.mArguments = bundle;
            return;
        }
        throw new IllegalStateException("Fragment already added and state has been saved");
    }

    public void setEnterSharedElementCallback(@Nullable SharedElementCallback sharedElementCallback) {
        ensureAnimationInfo().mEnterTransitionCallback = sharedElementCallback;
    }

    public void setEnterTransition(@Nullable Object obj) {
        ensureAnimationInfo().mEnterTransition = obj;
    }

    public void setExitSharedElementCallback(@Nullable SharedElementCallback sharedElementCallback) {
        ensureAnimationInfo().mExitTransitionCallback = sharedElementCallback;
    }

    public void setExitTransition(@Nullable Object obj) {
        ensureAnimationInfo().mExitTransition = obj;
    }

    public void setFocusedView(View view) {
        ensureAnimationInfo().mFocusedView = view;
    }

    public void setHasOptionsMenu(boolean z) {
        if (this.mHasMenu != z) {
            this.mHasMenu = z;
            if (isAdded() && !isHidden()) {
                this.mHost.onSupportInvalidateOptionsMenu();
            }
        }
    }

    public void setHideReplaced(boolean z) {
        ensureAnimationInfo().mIsHideReplaced = z;
    }

    /* JADX WARNING: Missing block: B:4:0x0008, code skipped:
            if (r2 != null) goto L_0x000c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setInitialSavedState(@Nullable SavedState savedState) {
        if (this.mFragmentManager == null) {
            Bundle bundle;
            if (savedState != null) {
                bundle = savedState.mState;
            }
            bundle = null;
            this.mSavedFragmentState = bundle;
            return;
        }
        throw new IllegalStateException("Fragment already added");
    }

    public void setMenuVisibility(boolean z) {
        if (this.mMenuVisible != z) {
            this.mMenuVisible = z;
            if (this.mHasMenu && isAdded() && !isHidden()) {
                this.mHost.onSupportInvalidateOptionsMenu();
            }
        }
    }

    public void setNextAnim(int i) {
        if (this.mAnimationInfo != null || i != 0) {
            ensureAnimationInfo().mNextAnim = i;
        }
    }

    public void setNextTransition(int i) {
        if (this.mAnimationInfo != null || i != 0) {
            ensureAnimationInfo();
            this.mAnimationInfo.mNextTransition = i;
        }
    }

    public void setOnStartEnterTransitionListener(OnStartEnterTransitionListener onStartEnterTransitionListener) {
        ensureAnimationInfo();
        AnimationInfo animationInfo = this.mAnimationInfo;
        OnStartEnterTransitionListener onStartEnterTransitionListener2 = animationInfo.mStartEnterTransitionListener;
        if (onStartEnterTransitionListener != onStartEnterTransitionListener2) {
            if (onStartEnterTransitionListener == null || onStartEnterTransitionListener2 == null) {
                if (animationInfo.mEnterTransitionPostponed) {
                    animationInfo.mStartEnterTransitionListener = onStartEnterTransitionListener;
                }
                if (onStartEnterTransitionListener != null) {
                    onStartEnterTransitionListener.startListening();
                }
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Trying to set a replacement startPostponedEnterTransition on ");
            stringBuilder.append(this);
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    public void setPostOnViewCreatedAlpha(float f) {
        ensureAnimationInfo().mPostOnViewCreatedAlpha = f;
    }

    public void setReenterTransition(@Nullable Object obj) {
        ensureAnimationInfo().mReenterTransition = obj;
    }

    @Deprecated
    public void setRetainInstance(boolean z) {
        this.mRetainInstance = z;
        FragmentManager fragmentManager = this.mFragmentManager;
        if (fragmentManager == null) {
            this.mRetainInstanceChangedWhileDetached = true;
        } else if (z) {
            fragmentManager.addRetainedFragment(this);
        } else {
            fragmentManager.removeRetainedFragment(this);
        }
    }

    public void setReturnTransition(@Nullable Object obj) {
        ensureAnimationInfo().mReturnTransition = obj;
    }

    public void setSharedElementEnterTransition(@Nullable Object obj) {
        ensureAnimationInfo().mSharedElementEnterTransition = obj;
    }

    public void setSharedElementNames(@Nullable ArrayList<String> arrayList, @Nullable ArrayList<String> arrayList2) {
        ensureAnimationInfo();
        AnimationInfo animationInfo = this.mAnimationInfo;
        animationInfo.mSharedElementSourceNames = arrayList;
        animationInfo.mSharedElementTargetNames = arrayList2;
    }

    public void setSharedElementReturnTransition(@Nullable Object obj) {
        ensureAnimationInfo().mSharedElementReturnTransition = obj;
    }

    @Deprecated
    public void setTargetFragment(@Nullable Fragment fragment, int i) {
        FragmentManager fragmentManager = this.mFragmentManager;
        FragmentManager fragmentManager2 = fragment != null ? fragment.mFragmentManager : null;
        if (fragmentManager == null || fragmentManager2 == null || fragmentManager == fragmentManager2) {
            for (Fragment fragment2 = fragment; fragment2 != null; fragment2 = fragment2.getTargetFragment()) {
                if (fragment2.equals(this)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Setting ");
                    stringBuilder.append(fragment);
                    stringBuilder.append(" as the target of ");
                    stringBuilder.append(this);
                    stringBuilder.append(" would create a target cycle");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            }
            if (fragment == null) {
                this.mTargetWho = null;
                this.mTarget = null;
            } else if (this.mFragmentManager == null || fragment.mFragmentManager == null) {
                this.mTargetWho = null;
                this.mTarget = fragment;
            } else {
                this.mTargetWho = fragment.mWho;
                this.mTarget = null;
            }
            this.mTargetRequestCode = i;
            return;
        }
        throw new IllegalArgumentException(a.r("Fragment ", fragment, " must share the same FragmentManager to be set as a target fragment"));
    }

    @Deprecated
    public void setUserVisibleHint(boolean z) {
        if (!this.mUserVisibleHint && z && this.mState < 5 && this.mFragmentManager != null && isAdded() && this.mIsCreated) {
            FragmentManager fragmentManager = this.mFragmentManager;
            fragmentManager.performPendingDeferredStart(fragmentManager.createOrGetFragmentStateManager(this));
        }
        this.mUserVisibleHint = z;
        boolean z2 = this.mState < 5 && !z;
        this.mDeferStart = z2;
        if (this.mSavedFragmentState != null) {
            this.mSavedUserVisibleHint = Boolean.valueOf(z);
        }
    }

    public boolean shouldShowRequestPermissionRationale(@NonNull String str) {
        FragmentHostCallback fragmentHostCallback = this.mHost;
        return fragmentHostCallback != null ? fragmentHostCallback.onShouldShowRequestPermissionRationale(str) : false;
    }

    public void startActivity(@SuppressLint({"UnknownNullness"}) Intent intent) {
        startActivity(intent, null);
    }

    @Deprecated
    public void startActivityForResult(@SuppressLint({"UnknownNullness"}) Intent intent, int i) {
        startActivityForResult(intent, i, null);
    }

    @Deprecated
    public void startIntentSenderForResult(@SuppressLint({"UnknownNullness"}) IntentSender intentSender, int i, @Nullable Intent intent, int i2, int i3, int i4, @Nullable Bundle bundle) throws SendIntentException {
        String str = "Fragment ";
        if (this.mHost != null) {
            int i5;
            IntentSender intentSender2;
            Intent intent2;
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(this);
                stringBuilder.append(" received the following in startIntentSenderForResult() requestCode: ");
                i5 = i;
                stringBuilder.append(i);
                stringBuilder.append(" IntentSender: ");
                intentSender2 = intentSender;
                stringBuilder.append(intentSender);
                stringBuilder.append(" fillInIntent: ");
                intent2 = intent;
                stringBuilder.append(intent);
                stringBuilder.append(" options: ");
                stringBuilder.append(bundle);
                Log.v(FragmentManager.TAG, stringBuilder.toString());
            } else {
                intentSender2 = intentSender;
                i5 = i;
                intent2 = intent;
                Bundle bundle2 = bundle;
            }
            getParentFragmentManager().launchStartIntentSenderForResult(this, intentSender, i, intent, i2, i3, i4, bundle);
            return;
        }
        throw new IllegalStateException(a.r(str, this, " not attached to Activity"));
    }

    public void startPostponedEnterTransition() {
        if (this.mAnimationInfo != null && ensureAnimationInfo().mEnterTransitionPostponed) {
            if (this.mHost == null) {
                ensureAnimationInfo().mEnterTransitionPostponed = false;
            } else if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
                this.mHost.getHandler().postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        Fragment.this.callStartTransitionListener(false);
                    }
                });
            } else {
                callStartTransitionListener(true);
            }
        }
    }

    @NonNull
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append(getClass().getSimpleName());
        stringBuilder.append("{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append("}");
        stringBuilder.append(" (");
        stringBuilder.append(this.mWho);
        stringBuilder.append(")");
        if (this.mFragmentId != 0) {
            stringBuilder.append(" id=0x");
            stringBuilder.append(Integer.toHexString(this.mFragmentId));
        }
        if (this.mTag != null) {
            stringBuilder.append(" ");
            stringBuilder.append(this.mTag);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public void unregisterForContextMenu(@NonNull View view) {
        view.setOnCreateContextMenuListener(null);
    }

    @NonNull
    @Deprecated
    public static Fragment instantiate(@NonNull Context context, @NonNull String str, @Nullable Bundle bundle) {
        String str2 = ": make sure class name exists, is public, and has an empty constructor that is public";
        String str3 = "Unable to instantiate fragment ";
        try {
            Fragment fragment = (Fragment) FragmentFactory.loadFragmentClass(context.getClassLoader(), str).getConstructor(new Class[0]).newInstance(new Object[0]);
            if (bundle != null) {
                bundle.setClassLoader(fragment.getClass().getClassLoader());
                fragment.setArguments(bundle);
            }
            return fragment;
        } catch (InstantiationException e) {
            throw new InstantiationException(a.u(str3, str, str2), e);
        } catch (IllegalAccessException e2) {
            throw new InstantiationException(a.u(str3, str, str2), e2);
        } catch (NoSuchMethodException e3) {
            throw new InstantiationException(a.u(str3, str, ": could not find Fragment constructor"), e3);
        } catch (InvocationTargetException e4) {
            throw new InstantiationException(a.u(str3, str, ": calling Fragment constructor caused an exception"), e4);
        }
    }

    @NonNull
    public final String getString(@StringRes int i, @Nullable Object... objArr) {
        return getResources().getString(i, objArr);
    }

    public final void postponeEnterTransition(long j, @NonNull TimeUnit timeUnit) {
        Handler handler;
        ensureAnimationInfo().mEnterTransitionPostponed = true;
        FragmentManager fragmentManager = this.mFragmentManager;
        if (fragmentManager != null) {
            handler = fragmentManager.getHost().getHandler();
        } else {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.removeCallbacks(this.mPostponedDurationRunnable);
        handler.postDelayed(this.mPostponedDurationRunnable, timeUnit.toMillis(j));
    }

    @MainThread
    @NonNull
    public final <I, O> ActivityResultLauncher<I> registerForActivityResult(@NonNull ActivityResultContract<I, O> activityResultContract, @NonNull final ActivityResultRegistry activityResultRegistry, @NonNull ActivityResultCallback<O> activityResultCallback) {
        return prepareCallInternal(activityResultContract, new Function<Void, ActivityResultRegistry>() {
            public ActivityResultRegistry apply(Void voidR) {
                return activityResultRegistry;
            }
        }, activityResultCallback);
    }

    public void startActivity(@SuppressLint({"UnknownNullness"}) Intent intent, @Nullable Bundle bundle) {
        FragmentHostCallback fragmentHostCallback = this.mHost;
        if (fragmentHostCallback != null) {
            fragmentHostCallback.onStartActivityFromFragment(this, intent, -1, bundle);
            return;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " not attached to Activity"));
    }

    @Deprecated
    public void startActivityForResult(@SuppressLint({"UnknownNullness"}) Intent intent, int i, @Nullable Bundle bundle) {
        if (this.mHost != null) {
            getParentFragmentManager().launchStartActivityForResult(this, intent, i, bundle);
            return;
        }
        throw new IllegalStateException(a.r("Fragment ", this, " not attached to Activity"));
    }

    @NonNull
    @Deprecated
    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public LayoutInflater getLayoutInflater(@Nullable Bundle bundle) {
        FragmentHostCallback fragmentHostCallback = this.mHost;
        if (fragmentHostCallback != null) {
            LayoutInflater onGetLayoutInflater = fragmentHostCallback.onGetLayoutInflater();
            LayoutInflaterCompat.setFactory2(onGetLayoutInflater, this.mChildFragmentManager.getLayoutInflaterFactory());
            return onGetLayoutInflater;
        }
        throw new IllegalStateException("onGetLayoutInflater() cannot be executed until the Fragment is attached to the FragmentManager.");
    }

    @CallSuper
    @MainThread
    @Deprecated
    public void onAttach(@NonNull Activity activity) {
        this.mCalled = true;
    }

    @CallSuper
    @UiThread
    @Deprecated
    public void onInflate(@NonNull Activity activity, @NonNull AttributeSet attributeSet, @Nullable Bundle bundle) {
        this.mCalled = true;
    }

    private void restoreViewState() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("moveto RESTORE_VIEW_STATE: ");
            stringBuilder.append(this);
            Log.d(FragmentManager.TAG, stringBuilder.toString());
        }
        if (this.mView != null) {
            restoreViewState(this.mSavedFragmentState);
        }
        this.mSavedFragmentState = null;
    }

    @ContentView
    public Fragment(@LayoutRes int i) {
        this();
        this.mContentLayoutId = i;
    }
}
