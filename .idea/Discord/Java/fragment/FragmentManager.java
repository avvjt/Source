package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.ActivityResultRegistryOwner;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.IntentSenderRequest.Builder;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;
import androidx.annotation.IdRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.annotation.StringRes;
import androidx.collection.ArraySet;
import androidx.core.os.CancellationSignal;
import androidx.fragment.R;
import androidx.fragment.app.Fragment.OnStartEnterTransitionListener;
import androidx.fragment.app.Fragment.SavedState;
import androidx.fragment.app.FragmentAnim.AnimationOrAnimator;
import androidx.fragment.app.FragmentTransaction.Op;
import androidx.fragment.app.FragmentTransition.Callback;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import f.d.b.a.a;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FragmentManager implements FragmentResultOwner {
    private static boolean DEBUG = false;
    private static final String EXTRA_CREATED_FILLIN_INTENT = "androidx.fragment.extra.ACTIVITY_OPTIONS_BUNDLE";
    public static final int POP_BACK_STACK_INCLUSIVE = 1;
    public static final String TAG = "FragmentManager";
    public static boolean USE_STATE_MANAGER = true;
    public ArrayList<BackStackRecord> mBackStack;
    private ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    private final AtomicInteger mBackStackIndex = new AtomicInteger();
    private FragmentContainer mContainer;
    private ArrayList<Fragment> mCreatedMenus;
    public int mCurState = -1;
    private SpecialEffectsControllerFactory mDefaultSpecialEffectsControllerFactory = new SpecialEffectsControllerFactory() {
        @NonNull
        public SpecialEffectsController createController(@NonNull ViewGroup viewGroup) {
            return new DefaultSpecialEffectsController(viewGroup);
        }
    };
    private boolean mDestroyed;
    private Runnable mExecCommit = new Runnable() {
        public void run() {
            FragmentManager.this.execPendingActions(true);
        }
    };
    private boolean mExecutingActions;
    private Map<Fragment, HashSet<CancellationSignal>> mExitAnimationCancellationSignals = Collections.synchronizedMap(new HashMap());
    private FragmentFactory mFragmentFactory = null;
    private final FragmentStore mFragmentStore = new FragmentStore();
    private final Callback mFragmentTransitionCallback = new Callback() {
        public void onComplete(@NonNull Fragment fragment, @NonNull CancellationSignal cancellationSignal) {
            if (!cancellationSignal.isCanceled()) {
                FragmentManager.this.removeCancellationSignal(fragment, cancellationSignal);
            }
        }

        public void onStart(@NonNull Fragment fragment, @NonNull CancellationSignal cancellationSignal) {
            FragmentManager.this.addCancellationSignal(fragment, cancellationSignal);
        }
    };
    private boolean mHavePendingDeferredStart;
    private FragmentHostCallback<?> mHost;
    private FragmentFactory mHostFragmentFactory = new FragmentFactory() {
        @NonNull
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String str) {
            return FragmentManager.this.getHost().instantiate(FragmentManager.this.getHost().getContext(), str, null);
        }
    };
    public ArrayDeque<LaunchedFragmentInfo> mLaunchedFragments = new ArrayDeque();
    private final FragmentLayoutInflaterFactory mLayoutInflaterFactory = new FragmentLayoutInflaterFactory(this);
    private final FragmentLifecycleCallbacksDispatcher mLifecycleCallbacksDispatcher = new FragmentLifecycleCallbacksDispatcher(this);
    private boolean mNeedMenuInvalidate;
    private FragmentManagerViewModel mNonConfig;
    private final CopyOnWriteArrayList<FragmentOnAttachListener> mOnAttachListeners = new CopyOnWriteArrayList();
    private final OnBackPressedCallback mOnBackPressedCallback = new OnBackPressedCallback(false) {
        public void handleOnBackPressed() {
            FragmentManager.this.handleOnBackPressed();
        }
    };
    private OnBackPressedDispatcher mOnBackPressedDispatcher;
    private Fragment mParent;
    private final ArrayList<OpGenerator> mPendingActions = new ArrayList();
    private ArrayList<StartEnterTransitionListener> mPostponedTransactions;
    @Nullable
    public Fragment mPrimaryNav;
    private ActivityResultLauncher<String[]> mRequestPermissions;
    private final Map<String, LifecycleAwareResultListener> mResultListeners = Collections.synchronizedMap(new HashMap());
    private final Map<String, Bundle> mResults = Collections.synchronizedMap(new HashMap());
    private SpecialEffectsControllerFactory mSpecialEffectsControllerFactory = null;
    private ActivityResultLauncher<Intent> mStartActivityForResult;
    private ActivityResultLauncher<IntentSenderRequest> mStartIntentSenderForResult;
    private boolean mStateSaved;
    private boolean mStopped;
    private ArrayList<Fragment> mTmpAddedFragments;
    private ArrayList<Boolean> mTmpIsPop;
    private ArrayList<BackStackRecord> mTmpRecords;

    public interface BackStackEntry {
        @Deprecated
        @Nullable
        CharSequence getBreadCrumbShortTitle();

        @StringRes
        @Deprecated
        int getBreadCrumbShortTitleRes();

        @Deprecated
        @Nullable
        CharSequence getBreadCrumbTitle();

        @StringRes
        @Deprecated
        int getBreadCrumbTitleRes();

        int getId();

        @Nullable
        String getName();
    }

    public static abstract class FragmentLifecycleCallbacks {
        @Deprecated
        public void onFragmentActivityCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @Nullable Bundle bundle) {
        }

        public void onFragmentAttached(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull Context context) {
        }

        public void onFragmentCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @Nullable Bundle bundle) {
        }

        public void onFragmentDestroyed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        }

        public void onFragmentDetached(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        }

        public void onFragmentPaused(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        }

        public void onFragmentPreAttached(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull Context context) {
        }

        public void onFragmentPreCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @Nullable Bundle bundle) {
        }

        public void onFragmentResumed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        }

        public void onFragmentSaveInstanceState(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull Bundle bundle) {
        }

        public void onFragmentStarted(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        }

        public void onFragmentStopped(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        }

        public void onFragmentViewCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull View view, @Nullable Bundle bundle) {
        }

        public void onFragmentViewDestroyed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        }
    }

    @SuppressLint({"BanParcelableUsage"})
    public static class LaunchedFragmentInfo implements Parcelable {
        public static final Creator<LaunchedFragmentInfo> CREATOR = new Creator<LaunchedFragmentInfo>() {
            public LaunchedFragmentInfo createFromParcel(Parcel parcel) {
                return new LaunchedFragmentInfo(parcel);
            }

            public LaunchedFragmentInfo[] newArray(int i) {
                return new LaunchedFragmentInfo[i];
            }
        };
        public int mRequestCode;
        public String mWho;

        public LaunchedFragmentInfo(@NonNull String str, int i) {
            this.mWho = str;
            this.mRequestCode = i;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mWho);
            parcel.writeInt(this.mRequestCode);
        }

        public LaunchedFragmentInfo(@NonNull Parcel parcel) {
            this.mWho = parcel.readString();
            this.mRequestCode = parcel.readInt();
        }
    }

    public interface OnBackStackChangedListener {
        @MainThread
        void onBackStackChanged();
    }

    public interface OpGenerator {
        boolean generateOps(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2);
    }

    public static class FragmentIntentSenderContract extends ActivityResultContract<IntentSenderRequest, ActivityResult> {
        @NonNull
        public Intent createIntent(@NonNull Context context, IntentSenderRequest intentSenderRequest) {
            Parcelable intentSenderRequest2;
            Intent intent = new Intent(StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST);
            Intent fillInIntent = intentSenderRequest2.getFillInIntent();
            if (fillInIntent != null) {
                String str = StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE;
                Bundle bundleExtra = fillInIntent.getBundleExtra(str);
                if (bundleExtra != null) {
                    intent.putExtra(str, bundleExtra);
                    fillInIntent.removeExtra(str);
                    if (fillInIntent.getBooleanExtra(FragmentManager.EXTRA_CREATED_FILLIN_INTENT, false)) {
                        intentSenderRequest2 = new Builder(intentSenderRequest2.getIntentSender()).setFillInIntent(null).setFlags(intentSenderRequest2.getFlagsValues(), intentSenderRequest2.getFlagsMask()).build();
                    }
                }
            }
            intent.putExtra(StartIntentSenderForResult.EXTRA_INTENT_SENDER_REQUEST, intentSenderRequest2);
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("CreateIntent created the following intent: ");
                stringBuilder.append(intent);
                Log.v(FragmentManager.TAG, stringBuilder.toString());
            }
            return intent;
        }

        @NonNull
        public ActivityResult parseResult(int i, @Nullable Intent intent) {
            return new ActivityResult(i, intent);
        }
    }

    public static class LifecycleAwareResultListener implements FragmentResultListener {
        private final Lifecycle mLifecycle;
        private final FragmentResultListener mListener;
        private final LifecycleEventObserver mObserver;

        public LifecycleAwareResultListener(@NonNull Lifecycle lifecycle, @NonNull FragmentResultListener fragmentResultListener, @NonNull LifecycleEventObserver lifecycleEventObserver) {
            this.mLifecycle = lifecycle;
            this.mListener = fragmentResultListener;
            this.mObserver = lifecycleEventObserver;
        }

        public boolean isAtLeast(State state) {
            return this.mLifecycle.getCurrentState().isAtLeast(state);
        }

        public void onFragmentResult(@NonNull String str, @NonNull Bundle bundle) {
            this.mListener.onFragmentResult(str, bundle);
        }

        public void removeObserver() {
            this.mLifecycle.removeObserver(this.mObserver);
        }
    }

    public class PopBackStackState implements OpGenerator {
        public final int mFlags;
        public final int mId;
        public final String mName;

        public PopBackStackState(@Nullable String str, int i, int i2) {
            this.mName = str;
            this.mId = i;
            this.mFlags = i2;
        }

        public boolean generateOps(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2) {
            Fragment fragment = FragmentManager.this.mPrimaryNav;
            if (fragment != null && this.mId < 0 && this.mName == null && fragment.getChildFragmentManager().popBackStackImmediate()) {
                return false;
            }
            return FragmentManager.this.popBackStackState(arrayList, arrayList2, this.mName, this.mId, this.mFlags);
        }
    }

    public static class StartEnterTransitionListener implements OnStartEnterTransitionListener {
        public final boolean mIsBack;
        private int mNumPostponed;
        public final BackStackRecord mRecord;

        public StartEnterTransitionListener(@NonNull BackStackRecord backStackRecord, boolean z) {
            this.mIsBack = z;
            this.mRecord = backStackRecord;
        }

        public void cancelTransaction() {
            BackStackRecord backStackRecord = this.mRecord;
            backStackRecord.mManager.completeExecute(backStackRecord, this.mIsBack, false, false);
        }

        public void completeTransaction() {
            int i = this.mNumPostponed > 0 ? 1 : 0;
            for (Fragment fragment : this.mRecord.mManager.getFragments()) {
                fragment.setOnStartEnterTransitionListener(null);
                if (i != 0 && fragment.isPostponed()) {
                    fragment.startPostponedEnterTransition();
                }
            }
            BackStackRecord backStackRecord = this.mRecord;
            backStackRecord.mManager.completeExecute(backStackRecord, this.mIsBack, i ^ 1, true);
        }

        public boolean isReady() {
            return this.mNumPostponed == 0;
        }

        public void onStartEnterTransition() {
            int i = this.mNumPostponed - 1;
            this.mNumPostponed = i;
            if (i == 0) {
                this.mRecord.mManager.scheduleCommit();
            }
        }

        public void startListening() {
            this.mNumPostponed++;
        }
    }

    private void addAddedFragments(@NonNull ArraySet<Fragment> arraySet) {
        int i = this.mCurState;
        if (i >= 1) {
            i = Math.min(i, 5);
            for (Fragment fragment : this.mFragmentStore.getFragments()) {
                if (fragment.mState < i) {
                    moveToState(fragment, i);
                    if (!(fragment.mView == null || fragment.mHidden || !fragment.mIsNewlyAdded)) {
                        arraySet.add(fragment);
                    }
                }
            }
        }
    }

    private void cancelExitAnimation(@NonNull Fragment fragment) {
        HashSet hashSet = (HashSet) this.mExitAnimationCancellationSignals.get(fragment);
        if (hashSet != null) {
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                ((CancellationSignal) it.next()).cancel();
            }
            hashSet.clear();
            destroyFragmentView(fragment);
            this.mExitAnimationCancellationSignals.remove(fragment);
        }
    }

    private void checkStateLoss() {
        if (isStateSaved()) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        }
    }

    private void cleanupExec() {
        this.mExecutingActions = false;
        this.mTmpIsPop.clear();
        this.mTmpRecords.clear();
    }

    private Set<SpecialEffectsController> collectAllSpecialEffectsController() {
        HashSet hashSet = new HashSet();
        for (FragmentStateManager fragment : this.mFragmentStore.getActiveFragmentStateManagers()) {
            ViewGroup viewGroup = fragment.getFragment().mContainer;
            if (viewGroup != null) {
                hashSet.add(SpecialEffectsController.getOrCreateController(viewGroup, getSpecialEffectsControllerFactory()));
            }
        }
        return hashSet;
    }

    private Set<SpecialEffectsController> collectChangedControllers(@NonNull ArrayList<BackStackRecord> arrayList, int i, int i2) {
        HashSet hashSet = new HashSet();
        while (i < i2) {
            Iterator it = ((BackStackRecord) arrayList.get(i)).mOps.iterator();
            while (it.hasNext()) {
                Fragment fragment = ((Op) it.next()).mFragment;
                if (fragment != null) {
                    ViewGroup viewGroup = fragment.mContainer;
                    if (viewGroup != null) {
                        hashSet.add(SpecialEffectsController.getOrCreateController(viewGroup, this));
                    }
                }
            }
            i++;
        }
        return hashSet;
    }

    private void completeShowHideFragment(@NonNull final Fragment fragment) {
        if (fragment.mView != null) {
            AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(this.mHost.getContext(), fragment, fragment.mHidden ^ 1);
            if (loadAnimation != null) {
                Animator animator = loadAnimation.animator;
                if (animator != null) {
                    animator.setTarget(fragment.mView);
                    if (!fragment.mHidden) {
                        fragment.mView.setVisibility(0);
                    } else if (fragment.isHideReplaced()) {
                        fragment.setHideReplaced(false);
                    } else {
                        final ViewGroup viewGroup = fragment.mContainer;
                        final View view = fragment.mView;
                        viewGroup.startViewTransition(view);
                        loadAnimation.animator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                viewGroup.endViewTransition(view);
                                animator.removeListener(this);
                                Fragment fragment = fragment;
                                View view = fragment.mView;
                                if (view != null && fragment.mHidden) {
                                    view.setVisibility(8);
                                }
                            }
                        });
                    }
                    loadAnimation.animator.start();
                }
            }
            if (loadAnimation != null) {
                fragment.mView.startAnimation(loadAnimation.animation);
                loadAnimation.animation.start();
            }
            int i = (!fragment.mHidden || fragment.isHideReplaced()) ? 0 : 8;
            fragment.mView.setVisibility(i);
            if (fragment.isHideReplaced()) {
                fragment.setHideReplaced(false);
            }
        }
        if (fragment.mAdded && isMenuAvailable(fragment)) {
            this.mNeedMenuInvalidate = true;
        }
        fragment.mHiddenChanged = false;
        fragment.onHiddenChanged(fragment.mHidden);
    }

    private void destroyFragmentView(@NonNull Fragment fragment) {
        fragment.performDestroyView();
        this.mLifecycleCallbacksDispatcher.dispatchOnFragmentViewDestroyed(fragment, false);
        fragment.mContainer = null;
        fragment.mView = null;
        fragment.mViewLifecycleOwner = null;
        fragment.mViewLifecycleOwnerLiveData.setValue(null);
        fragment.mInLayout = false;
    }

    private void dispatchParentPrimaryNavigationFragmentChanged(@Nullable Fragment fragment) {
        if (fragment != null && fragment.equals(findActiveFragment(fragment.mWho))) {
            fragment.performPrimaryNavigationFragmentChanged();
        }
    }

    private void dispatchStateChange(int i) {
        try {
            this.mExecutingActions = true;
            this.mFragmentStore.dispatchStateChange(i);
            moveToState(i, false);
            if (USE_STATE_MANAGER) {
                for (SpecialEffectsController forceCompleteAllOperations : collectAllSpecialEffectsController()) {
                    forceCompleteAllOperations.forceCompleteAllOperations();
                }
            }
            this.mExecutingActions = false;
            execPendingActions(true);
        } catch (Throwable th) {
            this.mExecutingActions = false;
        }
    }

    private void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            this.mHavePendingDeferredStart = false;
            startPendingDeferredFragments();
        }
    }

    @Deprecated
    public static void enableDebugLogging(boolean z) {
        DEBUG = z;
    }

    @FragmentStateManagerControl
    public static void enableNewStateManager(boolean z) {
        USE_STATE_MANAGER = z;
    }

    private void endAnimatingAwayFragments() {
        if (USE_STATE_MANAGER) {
            for (SpecialEffectsController forceCompleteAllOperations : collectAllSpecialEffectsController()) {
                forceCompleteAllOperations.forceCompleteAllOperations();
            }
        } else if (!this.mExitAnimationCancellationSignals.isEmpty()) {
            for (Fragment fragment : this.mExitAnimationCancellationSignals.keySet()) {
                cancelExitAnimation(fragment);
                moveToState(fragment);
            }
        }
    }

    private void ensureExecReady(boolean z) {
        if (this.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        } else if (this.mHost == null) {
            if (this.mDestroyed) {
                throw new IllegalStateException("FragmentManager has been destroyed");
            }
            throw new IllegalStateException("FragmentManager has not been attached to a host.");
        } else if (Looper.myLooper() == this.mHost.getHandler().getLooper()) {
            if (!z) {
                checkStateLoss();
            }
            if (this.mTmpRecords == null) {
                this.mTmpRecords = new ArrayList();
                this.mTmpIsPop = new ArrayList();
            }
            this.mExecutingActions = true;
            try {
                executePostponedTransaction(null, null);
            } finally {
                this.mExecutingActions = false;
            }
        } else {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        }
    }

    private static void executeOps(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2, int i, int i2) {
        while (i < i2) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i);
            boolean z = true;
            if (((Boolean) arrayList2.get(i)).booleanValue()) {
                backStackRecord.bumpBackStackNesting(-1);
                if (i != i2 - 1) {
                    z = false;
                }
                backStackRecord.executePopOps(z);
            } else {
                backStackRecord.bumpBackStackNesting(1);
                backStackRecord.executeOps();
            }
            i++;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:104:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01be  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void executeOpsTogether(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2, int i, int i2) {
        int i3;
        boolean z;
        int i4;
        ArrayList<BackStackRecord> arrayList3 = arrayList;
        ArrayList<Boolean> arrayList4 = arrayList2;
        int i5 = i;
        int i6 = i2;
        boolean z2 = ((BackStackRecord) arrayList3.get(i5)).mReorderingAllowed;
        ArrayList arrayList5 = this.mTmpAddedFragments;
        if (arrayList5 == null) {
            this.mTmpAddedFragments = new ArrayList();
        } else {
            arrayList5.clear();
        }
        this.mTmpAddedFragments.addAll(this.mFragmentStore.getFragments());
        Fragment primaryNavigationFragment = getPrimaryNavigationFragment();
        Object obj = null;
        for (int i7 = i5; i7 < i6; i7++) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList3.get(i7);
            if (((Boolean) arrayList4.get(i7)).booleanValue()) {
                primaryNavigationFragment = backStackRecord.trackAddedFragmentsInPop(this.mTmpAddedFragments, primaryNavigationFragment);
            } else {
                primaryNavigationFragment = backStackRecord.expandOps(this.mTmpAddedFragments, primaryNavigationFragment);
            }
            obj = (obj != null || backStackRecord.mAddToBackStack) ? 1 : null;
        }
        this.mTmpAddedFragments.clear();
        if (!z2 && this.mCurState >= 1) {
            Iterator it;
            if (USE_STATE_MANAGER) {
                for (i3 = i5; i3 < i6; i3++) {
                    it = ((BackStackRecord) arrayList3.get(i3)).mOps.iterator();
                    while (it.hasNext()) {
                        Fragment fragment = ((Op) it.next()).mFragment;
                        if (!(fragment == null || fragment.mFragmentManager == null)) {
                            this.mFragmentStore.makeActive(createOrGetFragmentStateManager(fragment));
                        }
                    }
                }
            } else {
                ArrayList arrayList6;
                z = true;
                FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, arrayList, arrayList2, i, i2, false, this.mFragmentTransitionCallback);
                executeOps(arrayList, arrayList2, i, i2);
                if (USE_STATE_MANAGER) {
                    int i8;
                    int i9;
                    int i10;
                    ArrayList<Boolean> arrayList7;
                    boolean z3;
                    if (z2) {
                        ArraySet arraySet = new ArraySet();
                        addAddedFragments(arraySet);
                        i8 = 1;
                        z3 = z2;
                        i9 = i6;
                        i10 = i5;
                        arrayList7 = arrayList4;
                        i3 = postponePostponableTransactions(arrayList, arrayList2, i, i2, arraySet);
                        makeRemovedFragmentsInvisible(arraySet);
                    } else {
                        z3 = z2;
                        i9 = i6;
                        i10 = i5;
                        arrayList7 = arrayList4;
                        i8 = 1;
                        i3 = i9;
                    }
                    if (i3 == i10 || !z3) {
                        arrayList6 = arrayList7;
                        i3 = i9;
                    } else {
                        if (this.mCurState >= i8) {
                            arrayList6 = arrayList7;
                            i10 = i3;
                            i3 = i9;
                            z = true;
                            FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, arrayList, arrayList2, i, i10, true, this.mFragmentTransitionCallback);
                        } else {
                            arrayList6 = arrayList7;
                            i3 = i9;
                            z = true;
                        }
                        moveToState(this.mCurState, z);
                    }
                } else {
                    boolean booleanValue = ((Boolean) arrayList4.get(i6 - 1)).booleanValue();
                    for (int i11 = i5; i11 < i6; i11++) {
                        BackStackRecord backStackRecord2 = (BackStackRecord) arrayList3.get(i11);
                        if (booleanValue) {
                            for (int size = backStackRecord2.mOps.size() - z; size >= 0; size--) {
                                Fragment fragment2 = ((Op) backStackRecord2.mOps.get(size)).mFragment;
                                if (fragment2 != null) {
                                    createOrGetFragmentStateManager(fragment2).moveToExpectedState();
                                }
                            }
                        } else {
                            Iterator it2 = backStackRecord2.mOps.iterator();
                            while (it2.hasNext()) {
                                Fragment fragment3 = ((Op) it2.next()).mFragment;
                                if (fragment3 != null) {
                                    createOrGetFragmentStateManager(fragment3).moveToExpectedState();
                                }
                            }
                        }
                    }
                    moveToState(this.mCurState, z);
                    for (SpecialEffectsController specialEffectsController : collectChangedControllers(arrayList3, i5, i6)) {
                        specialEffectsController.updateOperationDirection(booleanValue);
                        specialEffectsController.markPostponedState();
                        specialEffectsController.executePendingOperations();
                    }
                    i3 = i6;
                    arrayList6 = arrayList4;
                }
                for (i4 = i; i4 < i3; i4++) {
                    BackStackRecord backStackRecord3 = (BackStackRecord) arrayList3.get(i4);
                    if (((Boolean) arrayList6.get(i4)).booleanValue() && backStackRecord3.mIndex >= 0) {
                        backStackRecord3.mIndex = -1;
                    }
                    backStackRecord3.runOnCommitRunnables();
                }
                if (obj == null) {
                    reportBackStackChanged();
                    return;
                }
                return;
            }
        }
        z = true;
        executeOps(arrayList, arrayList2, i, i2);
        if (USE_STATE_MANAGER) {
        }
        while (i4 < i3) {
        }
        if (obj == null) {
        }
    }

    private void executePostponedTransaction(@Nullable ArrayList<BackStackRecord> arrayList, @Nullable ArrayList<Boolean> arrayList2) {
        ArrayList arrayList3 = this.mPostponedTransactions;
        int size = arrayList3 == null ? 0 : arrayList3.size();
        int i = 0;
        while (i < size) {
            int indexOf;
            StartEnterTransitionListener startEnterTransitionListener = (StartEnterTransitionListener) this.mPostponedTransactions.get(i);
            if (!(arrayList == null || startEnterTransitionListener.mIsBack)) {
                indexOf = arrayList.indexOf(startEnterTransitionListener.mRecord);
                if (!(indexOf == -1 || arrayList2 == null || !((Boolean) arrayList2.get(indexOf)).booleanValue())) {
                    this.mPostponedTransactions.remove(i);
                    i--;
                    size--;
                    startEnterTransitionListener.cancelTransaction();
                    i++;
                }
            }
            if (startEnterTransitionListener.isReady() || (arrayList != null && startEnterTransitionListener.mRecord.interactsWith(arrayList, 0, arrayList.size()))) {
                this.mPostponedTransactions.remove(i);
                i--;
                size--;
                if (!(arrayList == null || startEnterTransitionListener.mIsBack)) {
                    indexOf = arrayList.indexOf(startEnterTransitionListener.mRecord);
                    if (!(indexOf == -1 || arrayList2 == null || !((Boolean) arrayList2.get(indexOf)).booleanValue())) {
                        startEnterTransitionListener.cancelTransaction();
                    }
                }
                startEnterTransitionListener.completeTransaction();
            }
            i++;
        }
    }

    @NonNull
    public static <F extends Fragment> F findFragment(@NonNull View view) {
        Fragment findViewFragment = findViewFragment(view);
        if (findViewFragment != null) {
            return findViewFragment;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("View ");
        stringBuilder.append(view);
        stringBuilder.append(" does not have a Fragment set");
        throw new IllegalStateException(stringBuilder.toString());
    }

    @NonNull
    public static FragmentManager findFragmentManager(@NonNull View view) {
        Fragment findViewFragment = findViewFragment(view);
        if (findViewFragment == null) {
            FragmentActivity fragmentActivity = null;
            for (Context context = view.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
                if (context instanceof FragmentActivity) {
                    fragmentActivity = (FragmentActivity) context;
                    break;
                }
            }
            if (fragmentActivity != null) {
                return fragmentActivity.getSupportFragmentManager();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("View ");
            stringBuilder.append(view);
            stringBuilder.append(" is not within a subclass of FragmentActivity.");
            throw new IllegalStateException(stringBuilder.toString());
        } else if (findViewFragment.isAdded()) {
            return findViewFragment.getChildFragmentManager();
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("The Fragment ");
            stringBuilder2.append(findViewFragment);
            stringBuilder2.append(" that owns View ");
            stringBuilder2.append(view);
            stringBuilder2.append(" has already been destroyed. Nested fragments should always use the child FragmentManager.");
            throw new IllegalStateException(stringBuilder2.toString());
        }
    }

    @Nullable
    private static Fragment findViewFragment(@NonNull View view) {
        while (view != null) {
            Fragment viewFragment = getViewFragment(view);
            if (viewFragment != null) {
                return viewFragment;
            }
            ViewParent parent = view.getParent();
            view = parent instanceof View ? (View) parent : null;
        }
        return null;
    }

    private void forcePostponedTransactions() {
        if (USE_STATE_MANAGER) {
            for (SpecialEffectsController forcePostponedExecutePendingOperations : collectAllSpecialEffectsController()) {
                forcePostponedExecutePendingOperations.forcePostponedExecutePendingOperations();
            }
        } else if (this.mPostponedTransactions != null) {
            while (!this.mPostponedTransactions.isEmpty()) {
                ((StartEnterTransitionListener) this.mPostponedTransactions.remove(0)).completeTransaction();
            }
        }
    }

    private boolean generateOpsForPendingActions(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2) {
        synchronized (this.mPendingActions) {
            int i = 0;
            if (this.mPendingActions.isEmpty()) {
                return false;
            }
            int i2 = 0;
            while (i < this.mPendingActions.size()) {
                i2 |= ((OpGenerator) this.mPendingActions.get(i)).generateOps(arrayList, arrayList2);
                i++;
            }
            this.mPendingActions.clear();
            this.mHost.getHandler().removeCallbacks(this.mExecCommit);
            return i2;
        }
    }

    @NonNull
    private FragmentManagerViewModel getChildNonConfig(@NonNull Fragment fragment) {
        return this.mNonConfig.getChildNonConfig(fragment);
    }

    private ViewGroup getFragmentContainer(@NonNull Fragment fragment) {
        ViewGroup viewGroup = fragment.mContainer;
        if (viewGroup != null) {
            return viewGroup;
        }
        if (fragment.mContainerId > 0 && this.mContainer.onHasView()) {
            View onFindViewById = this.mContainer.onFindViewById(fragment.mContainerId);
            if (onFindViewById instanceof ViewGroup) {
                return (ViewGroup) onFindViewById;
            }
        }
        return null;
    }

    @Nullable
    public static Fragment getViewFragment(@NonNull View view) {
        Object tag = view.getTag(R.id.fragment_container_view_tag);
        return tag instanceof Fragment ? (Fragment) tag : null;
    }

    public static boolean isLoggingEnabled(int i) {
        return DEBUG || Log.isLoggable(TAG, i);
    }

    private boolean isMenuAvailable(@NonNull Fragment fragment) {
        return (fragment.mHasMenu && fragment.mMenuVisible) || fragment.mChildFragmentManager.checkForMenus();
    }

    private void makeRemovedFragmentsInvisible(@NonNull ArraySet<Fragment> arraySet) {
        int size = arraySet.size();
        for (int i = 0; i < size; i++) {
            Fragment fragment = (Fragment) arraySet.valueAt(i);
            if (!fragment.mAdded) {
                View requireView = fragment.requireView();
                fragment.mPostponedAlpha = requireView.getAlpha();
                requireView.setAlpha(0.0f);
            }
        }
    }

    private int postponePostponableTransactions(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2, int i, int i2, @NonNull ArraySet<Fragment> arraySet) {
        int i3 = i2;
        for (int i4 = i2 - 1; i4 >= i; i4--) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i4);
            boolean booleanValue = ((Boolean) arrayList2.get(i4)).booleanValue();
            Object obj = (!backStackRecord.isPostponed() || backStackRecord.interactsWith(arrayList, i4 + 1, i2)) ? null : 1;
            if (obj != null) {
                if (this.mPostponedTransactions == null) {
                    this.mPostponedTransactions = new ArrayList();
                }
                StartEnterTransitionListener startEnterTransitionListener = new StartEnterTransitionListener(backStackRecord, booleanValue);
                this.mPostponedTransactions.add(startEnterTransitionListener);
                backStackRecord.setOnStartPostponedListener(startEnterTransitionListener);
                if (booleanValue) {
                    backStackRecord.executeOps();
                } else {
                    backStackRecord.executePopOps(false);
                }
                i3--;
                if (i4 != i3) {
                    arrayList.remove(i4);
                    arrayList.add(i3, backStackRecord);
                }
                addAddedFragments(arraySet);
            }
        }
        return i3;
    }

    private void removeRedundantOperationsAndExecute(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2) {
        if (!arrayList.isEmpty()) {
            if (arrayList.size() == arrayList2.size()) {
                executePostponedTransaction(arrayList, arrayList2);
                int size = arrayList.size();
                int i = 0;
                int i2 = 0;
                while (i < size) {
                    if (!((BackStackRecord) arrayList.get(i)).mReorderingAllowed) {
                        if (i2 != i) {
                            executeOpsTogether(arrayList, arrayList2, i2, i);
                        }
                        i2 = i + 1;
                        if (((Boolean) arrayList2.get(i)).booleanValue()) {
                            while (i2 < size && ((Boolean) arrayList2.get(i2)).booleanValue() && !((BackStackRecord) arrayList.get(i2)).mReorderingAllowed) {
                                i2++;
                            }
                        }
                        executeOpsTogether(arrayList, arrayList2, i, i2);
                        i = i2 - 1;
                    }
                    i++;
                }
                if (i2 != size) {
                    executeOpsTogether(arrayList, arrayList2, i2, size);
                }
                return;
            }
            throw new IllegalStateException("Internal error with the back stack records");
        }
    }

    private void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i++) {
                ((OnBackStackChangedListener) this.mBackStackChangeListeners.get(i)).onBackStackChanged();
            }
        }
    }

    public static int reverseTransit(int i) {
        return i != FragmentTransaction.TRANSIT_FRAGMENT_OPEN ? i != FragmentTransaction.TRANSIT_FRAGMENT_FADE ? i != 8194 ? 0 : FragmentTransaction.TRANSIT_FRAGMENT_OPEN : FragmentTransaction.TRANSIT_FRAGMENT_FADE : 8194;
    }

    private void setVisibleRemovingFragment(@NonNull Fragment fragment) {
        ViewGroup fragmentContainer = getFragmentContainer(fragment);
        if (fragmentContainer != null && fragment.getNextAnim() > 0) {
            int i = R.id.visible_removing_fragment_view_tag;
            if (fragmentContainer.getTag(i) == null) {
                fragmentContainer.setTag(i, fragment);
            }
            ((Fragment) fragmentContainer.getTag(i)).setNextAnim(fragment.getNextAnim());
        }
    }

    private void startPendingDeferredFragments() {
        for (FragmentStateManager performPendingDeferredStart : this.mFragmentStore.getActiveFragmentStateManagers()) {
            performPendingDeferredStart(performPendingDeferredStart);
        }
    }

    private void throwException(RuntimeException runtimeException) {
        String message = runtimeException.getMessage();
        String str = TAG;
        Log.e(str, message);
        Log.e(str, "Activity state:");
        PrintWriter printWriter = new PrintWriter(new LogWriter(str));
        FragmentHostCallback fragmentHostCallback = this.mHost;
        String str2 = "Failed dumping state";
        String str3 = "  ";
        if (fragmentHostCallback != null) {
            try {
                fragmentHostCallback.onDump(str3, null, printWriter, new String[0]);
            } catch (Exception e) {
                Log.e(str, str2, e);
            }
        } else {
            try {
                dump(str3, null, printWriter, new String[0]);
            } catch (Exception e2) {
                Log.e(str, str2, e2);
            }
        }
        throw runtimeException;
    }

    /* JADX WARNING: Missing block: B:9:0x0014, code skipped:
            r0 = r3.mOnBackPressedCallback;
     */
    /* JADX WARNING: Missing block: B:10:0x001a, code skipped:
            if (getBackStackEntryCount() <= 0) goto L_0x0025;
     */
    /* JADX WARNING: Missing block: B:12:0x0022, code skipped:
            if (isPrimaryNavigation(r3.mParent) == false) goto L_0x0025;
     */
    /* JADX WARNING: Missing block: B:13:0x0025, code skipped:
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:14:0x0026, code skipped:
            r0.setEnabled(r2);
     */
    /* JADX WARNING: Missing block: B:15:0x0029, code skipped:
            return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateOnBackPressedCallbackEnabled() {
        synchronized (this.mPendingActions) {
            boolean z = true;
            if (!this.mPendingActions.isEmpty()) {
                this.mOnBackPressedCallback.setEnabled(true);
            }
        }
    }

    public void addBackStackState(BackStackRecord backStackRecord) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList();
        }
        this.mBackStack.add(backStackRecord);
    }

    public void addCancellationSignal(@NonNull Fragment fragment, @NonNull CancellationSignal cancellationSignal) {
        if (this.mExitAnimationCancellationSignals.get(fragment) == null) {
            this.mExitAnimationCancellationSignals.put(fragment, new HashSet());
        }
        ((HashSet) this.mExitAnimationCancellationSignals.get(fragment)).add(cancellationSignal);
    }

    public void addFragment(@NonNull Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("add: ");
            stringBuilder.append(fragment);
            Log.v(TAG, stringBuilder.toString());
        }
        FragmentStateManager createOrGetFragmentStateManager = createOrGetFragmentStateManager(fragment);
        fragment.mFragmentManager = this;
        this.mFragmentStore.makeActive(createOrGetFragmentStateManager);
        if (!fragment.mDetached) {
            this.mFragmentStore.addFragment(fragment);
            fragment.mRemoving = false;
            if (fragment.mView == null) {
                fragment.mHiddenChanged = false;
            }
            if (isMenuAvailable(fragment)) {
                this.mNeedMenuInvalidate = true;
            }
        }
    }

    public void addFragmentOnAttachListener(@NonNull FragmentOnAttachListener fragmentOnAttachListener) {
        this.mOnAttachListeners.add(fragmentOnAttachListener);
    }

    public void addOnBackStackChangedListener(@NonNull OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList();
        }
        this.mBackStackChangeListeners.add(onBackStackChangedListener);
    }

    public void addRetainedFragment(@NonNull Fragment fragment) {
        this.mNonConfig.addRetainedFragment(fragment);
    }

    public int allocBackStackIndex() {
        return this.mBackStackIndex.getAndIncrement();
    }

    @SuppressLint({"SyntheticAccessor"})
    public void attachController(@NonNull FragmentHostCallback<?> fragmentHostCallback, @NonNull FragmentContainer fragmentContainer, @Nullable final Fragment fragment) {
        if (this.mHost == null) {
            this.mHost = fragmentHostCallback;
            this.mContainer = fragmentContainer;
            this.mParent = fragment;
            if (fragment != null) {
                addFragmentOnAttachListener(new FragmentOnAttachListener() {
                    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
                        fragment.onAttachFragment(fragment);
                    }
                });
            } else if (fragmentHostCallback instanceof FragmentOnAttachListener) {
                addFragmentOnAttachListener((FragmentOnAttachListener) fragmentHostCallback);
            }
            if (this.mParent != null) {
                updateOnBackPressedCallbackEnabled();
            }
            if (fragmentHostCallback instanceof OnBackPressedDispatcherOwner) {
                LifecycleOwner lifecycleOwner = (OnBackPressedDispatcherOwner) fragmentHostCallback;
                OnBackPressedDispatcher onBackPressedDispatcher = lifecycleOwner.getOnBackPressedDispatcher();
                this.mOnBackPressedDispatcher = onBackPressedDispatcher;
                if (fragment != null) {
                    lifecycleOwner = fragment;
                }
                onBackPressedDispatcher.addCallback(lifecycleOwner, this.mOnBackPressedCallback);
            }
            if (fragment != null) {
                this.mNonConfig = fragment.mFragmentManager.getChildNonConfig(fragment);
            } else if (fragmentHostCallback instanceof ViewModelStoreOwner) {
                this.mNonConfig = FragmentManagerViewModel.getInstance(((ViewModelStoreOwner) fragmentHostCallback).getViewModelStore());
            } else {
                this.mNonConfig = new FragmentManagerViewModel(false);
            }
            this.mNonConfig.setIsStateSaved(isStateSaved());
            this.mFragmentStore.setNonConfig(this.mNonConfig);
            FragmentHostCallback fragmentHostCallback2 = this.mHost;
            if (fragmentHostCallback2 instanceof ActivityResultRegistryOwner) {
                ActivityResultRegistry activityResultRegistry = ((ActivityResultRegistryOwner) fragmentHostCallback2).getActivityResultRegistry();
                String t = a.t("FragmentManager:", fragment != null ? a.D(new StringBuilder(), fragment.mWho, ":") : "");
                this.mStartActivityForResult = activityResultRegistry.register(a.t(t, "StartActivityForResult"), new StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    public void onActivityResult(ActivityResult activityResult) {
                        LaunchedFragmentInfo launchedFragmentInfo = (LaunchedFragmentInfo) FragmentManager.this.mLaunchedFragments.pollFirst();
                        String str = FragmentManager.TAG;
                        if (launchedFragmentInfo == null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("No Activities were started for result for ");
                            stringBuilder.append(this);
                            Log.w(str, stringBuilder.toString());
                            return;
                        }
                        String str2 = launchedFragmentInfo.mWho;
                        int i = launchedFragmentInfo.mRequestCode;
                        Fragment findFragmentByWho = FragmentManager.this.mFragmentStore.findFragmentByWho(str2);
                        if (findFragmentByWho == null) {
                            a.Z("Activity result delivered for unknown Fragment ", str2, str);
                        } else {
                            findFragmentByWho.onActivityResult(i, activityResult.getResultCode(), activityResult.getData());
                        }
                    }
                });
                this.mStartIntentSenderForResult = activityResultRegistry.register(a.t(t, "StartIntentSenderForResult"), new FragmentIntentSenderContract(), new ActivityResultCallback<ActivityResult>() {
                    public void onActivityResult(ActivityResult activityResult) {
                        LaunchedFragmentInfo launchedFragmentInfo = (LaunchedFragmentInfo) FragmentManager.this.mLaunchedFragments.pollFirst();
                        String str = FragmentManager.TAG;
                        if (launchedFragmentInfo == null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("No IntentSenders were started for ");
                            stringBuilder.append(this);
                            Log.w(str, stringBuilder.toString());
                            return;
                        }
                        String str2 = launchedFragmentInfo.mWho;
                        int i = launchedFragmentInfo.mRequestCode;
                        Fragment findFragmentByWho = FragmentManager.this.mFragmentStore.findFragmentByWho(str2);
                        if (findFragmentByWho == null) {
                            a.Z("Intent Sender result delivered for unknown Fragment ", str2, str);
                        } else {
                            findFragmentByWho.onActivityResult(i, activityResult.getResultCode(), activityResult.getData());
                        }
                    }
                });
                this.mRequestPermissions = activityResultRegistry.register(a.t(t, "RequestPermissions"), new RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @SuppressLint({"SyntheticAccessor"})
                    public void onActivityResult(Map<String, Boolean> map) {
                        String[] strArr = (String[]) map.keySet().toArray(new String[0]);
                        ArrayList arrayList = new ArrayList(map.values());
                        int[] iArr = new int[arrayList.size()];
                        for (int i = 0; i < arrayList.size(); i++) {
                            iArr[i] = ((Boolean) arrayList.get(i)).booleanValue() ? 0 : -1;
                        }
                        LaunchedFragmentInfo launchedFragmentInfo = (LaunchedFragmentInfo) FragmentManager.this.mLaunchedFragments.pollFirst();
                        String str = FragmentManager.TAG;
                        if (launchedFragmentInfo == null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("No permissions were requested for ");
                            stringBuilder.append(this);
                            Log.w(str, stringBuilder.toString());
                            return;
                        }
                        String str2 = launchedFragmentInfo.mWho;
                        int i2 = launchedFragmentInfo.mRequestCode;
                        Fragment findFragmentByWho = FragmentManager.this.mFragmentStore.findFragmentByWho(str2);
                        if (findFragmentByWho == null) {
                            a.Z("Permission request result delivered for unknown Fragment ", str2, str);
                        } else {
                            findFragmentByWho.onRequestPermissionsResult(i2, strArr, iArr);
                        }
                    }
                });
                return;
            }
            return;
        }
        throw new IllegalStateException("Already attached");
    }

    public void attachFragment(@NonNull Fragment fragment) {
        boolean isLoggingEnabled = isLoggingEnabled(2);
        String str = TAG;
        if (isLoggingEnabled) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("attach: ");
            stringBuilder.append(fragment);
            Log.v(str, stringBuilder.toString());
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (!fragment.mAdded) {
                this.mFragmentStore.addFragment(fragment);
                if (isLoggingEnabled(2)) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("add from attach: ");
                    stringBuilder2.append(fragment);
                    Log.v(str, stringBuilder2.toString());
                }
                if (isMenuAvailable(fragment)) {
                    this.mNeedMenuInvalidate = true;
                }
            }
        }
    }

    @NonNull
    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    public boolean checkForMenus() {
        boolean z = false;
        for (Fragment fragment : this.mFragmentStore.getActiveFragments()) {
            if (fragment != null) {
                z = isMenuAvailable(fragment);
                continue;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public final void clearFragmentResult(@NonNull String str) {
        this.mResults.remove(str);
    }

    public final void clearFragmentResultListener(@NonNull String str) {
        LifecycleAwareResultListener lifecycleAwareResultListener = (LifecycleAwareResultListener) this.mResultListeners.remove(str);
        if (lifecycleAwareResultListener != null) {
            lifecycleAwareResultListener.removeObserver();
        }
    }

    public void completeExecute(@NonNull BackStackRecord backStackRecord, boolean z, boolean z2, boolean z3) {
        if (z) {
            backStackRecord.executePopOps(z3);
        } else {
            backStackRecord.executeOps();
        }
        ArrayList arrayList = new ArrayList(1);
        ArrayList arrayList2 = new ArrayList(1);
        arrayList.add(backStackRecord);
        arrayList2.add(Boolean.valueOf(z));
        if (z2 && this.mCurState >= 1) {
            FragmentTransition.startTransitions(this.mHost.getContext(), this.mContainer, arrayList, arrayList2, 0, 1, true, this.mFragmentTransitionCallback);
        }
        if (z3) {
            moveToState(this.mCurState, true);
        }
        for (Fragment fragment : this.mFragmentStore.getActiveFragments()) {
            if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && backStackRecord.interactsWith(fragment.mContainerId)) {
                float f = fragment.mPostponedAlpha;
                if (f > 0.0f) {
                    fragment.mView.setAlpha(f);
                }
                if (z3) {
                    fragment.mPostponedAlpha = 0.0f;
                } else {
                    fragment.mPostponedAlpha = -1.0f;
                    fragment.mIsNewlyAdded = false;
                }
            }
        }
    }

    @NonNull
    public FragmentStateManager createOrGetFragmentStateManager(@NonNull Fragment fragment) {
        FragmentStateManager fragmentStateManager = this.mFragmentStore.getFragmentStateManager(fragment.mWho);
        if (fragmentStateManager != null) {
            return fragmentStateManager;
        }
        fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, fragment);
        fragmentStateManager.restoreState(this.mHost.getContext().getClassLoader());
        fragmentStateManager.setFragmentManagerState(this.mCurState);
        return fragmentStateManager;
    }

    public void detachFragment(@NonNull Fragment fragment) {
        boolean isLoggingEnabled = isLoggingEnabled(2);
        String str = TAG;
        if (isLoggingEnabled) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("detach: ");
            stringBuilder.append(fragment);
            Log.v(str, stringBuilder.toString());
        }
        if (!fragment.mDetached) {
            fragment.mDetached = true;
            if (fragment.mAdded) {
                if (isLoggingEnabled(2)) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("remove from detach: ");
                    stringBuilder2.append(fragment);
                    Log.v(str, stringBuilder2.toString());
                }
                this.mFragmentStore.removeFragment(fragment);
                if (isMenuAvailable(fragment)) {
                    this.mNeedMenuInvalidate = true;
                }
                setVisibleRemovingFragment(fragment);
            }
        }
    }

    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(4);
    }

    public void dispatchAttach() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(0);
    }

    public void dispatchConfigurationChanged(@NonNull Configuration configuration) {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performConfigurationChanged(configuration);
            }
        }
    }

    public boolean dispatchContextItemSelected(@NonNull MenuItem menuItem) {
        if (this.mCurState < 1) {
            return false;
        }
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }

    public void dispatchCreate() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(1);
    }

    public boolean dispatchCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        int i = 0;
        if (this.mCurState < 1) {
            return false;
        }
        ArrayList arrayList = null;
        boolean z = false;
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && isParentMenuVisible(fragment) && fragment.performCreateOptionsMenu(menu, menuInflater)) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(fragment);
                z = true;
            }
        }
        if (this.mCreatedMenus != null) {
            while (i < this.mCreatedMenus.size()) {
                Fragment fragment2 = (Fragment) this.mCreatedMenus.get(i);
                if (arrayList == null || !arrayList.contains(fragment2)) {
                    fragment2.onDestroyOptionsMenu();
                }
                i++;
            }
        }
        this.mCreatedMenus = arrayList;
        return z;
    }

    public void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions(true);
        endAnimatingAwayFragments();
        dispatchStateChange(-1);
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
        if (this.mOnBackPressedDispatcher != null) {
            this.mOnBackPressedCallback.remove();
            this.mOnBackPressedDispatcher = null;
        }
        ActivityResultLauncher activityResultLauncher = this.mStartActivityForResult;
        if (activityResultLauncher != null) {
            activityResultLauncher.unregister();
            this.mStartIntentSenderForResult.unregister();
            this.mRequestPermissions.unregister();
        }
    }

    public void dispatchDestroyView() {
        dispatchStateChange(1);
    }

    public void dispatchLowMemory() {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performLowMemory();
            }
        }
    }

    public void dispatchMultiWindowModeChanged(boolean z) {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performMultiWindowModeChanged(z);
            }
        }
    }

    public void dispatchOnAttachFragment(@NonNull Fragment fragment) {
        Iterator it = this.mOnAttachListeners.iterator();
        while (it.hasNext()) {
            ((FragmentOnAttachListener) it.next()).onAttachFragment(this, fragment);
        }
    }

    public boolean dispatchOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (this.mCurState < 1) {
            return false;
        }
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }

    public void dispatchOptionsMenuClosed(@NonNull Menu menu) {
        if (this.mCurState >= 1) {
            for (Fragment fragment : this.mFragmentStore.getFragments()) {
                if (fragment != null) {
                    fragment.performOptionsMenuClosed(menu);
                }
            }
        }
    }

    public void dispatchPause() {
        dispatchStateChange(5);
    }

    public void dispatchPictureInPictureModeChanged(boolean z) {
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null) {
                fragment.performPictureInPictureModeChanged(z);
            }
        }
    }

    public boolean dispatchPrepareOptionsMenu(@NonNull Menu menu) {
        boolean z = false;
        if (this.mCurState < 1) {
            return false;
        }
        for (Fragment fragment : this.mFragmentStore.getFragments()) {
            if (fragment != null && isParentMenuVisible(fragment) && fragment.performPrepareOptionsMenu(menu)) {
                z = true;
            }
        }
        return z;
    }

    public void dispatchPrimaryNavigationFragmentChanged() {
        updateOnBackPressedCallbackEnabled();
        dispatchParentPrimaryNavigationFragmentChanged(this.mPrimaryNav);
    }

    public void dispatchResume() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(7);
    }

    public void dispatchStart() {
        this.mStateSaved = false;
        this.mStopped = false;
        this.mNonConfig.setIsStateSaved(false);
        dispatchStateChange(5);
    }

    public void dispatchStop() {
        this.mStopped = true;
        this.mNonConfig.setIsStateSaved(true);
        dispatchStateChange(4);
    }

    public void dispatchViewCreated() {
        dispatchStateChange(2);
    }

    public void dump(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
        int size;
        int i;
        String t = a.t(str, "    ");
        this.mFragmentStore.dump(str, fileDescriptor, printWriter, strArr);
        ArrayList arrayList = this.mCreatedMenus;
        if (arrayList != null) {
            size = arrayList.size();
            if (size > 0) {
                printWriter.print(str);
                printWriter.println("Fragments Created Menus:");
                for (i = 0; i < size; i++) {
                    Fragment fragment = (Fragment) this.mCreatedMenus.get(i);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(fragment.toString());
                }
            }
        }
        arrayList = this.mBackStack;
        if (arrayList != null) {
            size = arrayList.size();
            if (size > 0) {
                printWriter.print(str);
                printWriter.println("Back Stack:");
                for (i = 0; i < size; i++) {
                    BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.get(i);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(backStackRecord.toString());
                    backStackRecord.dump(t, printWriter);
                }
            }
        }
        printWriter.print(str);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Back Stack Index: ");
        stringBuilder.append(this.mBackStackIndex.get());
        printWriter.println(stringBuilder.toString());
        synchronized (this.mPendingActions) {
            int size2 = this.mPendingActions.size();
            if (size2 > 0) {
                printWriter.print(str);
                printWriter.println("Pending Actions:");
                for (int i2 = 0; i2 < size2; i2++) {
                    OpGenerator opGenerator = (OpGenerator) this.mPendingActions.get(i2);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i2);
                    printWriter.print(": ");
                    printWriter.println(opGenerator);
                }
            }
        }
        printWriter.print(str);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(str);
        printWriter.print("  mHost=");
        printWriter.println(this.mHost);
        printWriter.print(str);
        printWriter.print("  mContainer=");
        printWriter.println(this.mContainer);
        if (this.mParent != null) {
            printWriter.print(str);
            printWriter.print("  mParent=");
            printWriter.println(this.mParent);
        }
        printWriter.print(str);
        printWriter.print("  mCurState=");
        printWriter.print(this.mCurState);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.mStateSaved);
        printWriter.print(" mStopped=");
        printWriter.print(this.mStopped);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            printWriter.print(str);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.mNeedMenuInvalidate);
        }
    }

    public void enqueueAction(@NonNull OpGenerator opGenerator, boolean z) {
        if (!z) {
            if (this.mHost != null) {
                checkStateLoss();
            } else if (this.mDestroyed) {
                throw new IllegalStateException("FragmentManager has been destroyed");
            } else {
                throw new IllegalStateException("FragmentManager has not been attached to a host.");
            }
        }
        synchronized (this.mPendingActions) {
            if (this.mHost != null) {
                this.mPendingActions.add(opGenerator);
                scheduleCommit();
            } else if (z) {
            } else {
                throw new IllegalStateException("Activity has been destroyed");
            }
        }
    }

    public boolean execPendingActions(boolean z) {
        ensureExecReady(z);
        boolean z2 = false;
        while (generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                cleanupExec();
                z2 = true;
            } catch (Throwable th) {
                cleanupExec();
                throw th;
            }
        }
        updateOnBackPressedCallbackEnabled();
        doPendingDeferredStart();
        this.mFragmentStore.burpActive();
        return z2;
    }

    public void execSingleAction(@NonNull OpGenerator opGenerator, boolean z) {
        if (!z || (this.mHost != null && !this.mDestroyed)) {
            ensureExecReady(z);
            if (opGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
                this.mExecutingActions = true;
                try {
                    removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                } finally {
                    cleanupExec();
                }
            }
            updateOnBackPressedCallbackEnabled();
            doPendingDeferredStart();
            this.mFragmentStore.burpActive();
        }
    }

    public boolean executePendingTransactions() {
        boolean execPendingActions = execPendingActions(true);
        forcePostponedTransactions();
        return execPendingActions;
    }

    @Nullable
    public Fragment findActiveFragment(@NonNull String str) {
        return this.mFragmentStore.findActiveFragment(str);
    }

    @Nullable
    public Fragment findFragmentById(@IdRes int i) {
        return this.mFragmentStore.findFragmentById(i);
    }

    @Nullable
    public Fragment findFragmentByTag(@Nullable String str) {
        return this.mFragmentStore.findFragmentByTag(str);
    }

    public Fragment findFragmentByWho(@NonNull String str) {
        return this.mFragmentStore.findFragmentByWho(str);
    }

    public int getActiveFragmentCount() {
        return this.mFragmentStore.getActiveFragmentCount();
    }

    @NonNull
    public List<Fragment> getActiveFragments() {
        return this.mFragmentStore.getActiveFragments();
    }

    @NonNull
    public BackStackEntry getBackStackEntryAt(int i) {
        return (BackStackEntry) this.mBackStack.get(i);
    }

    public int getBackStackEntryCount() {
        ArrayList arrayList = this.mBackStack;
        return arrayList != null ? arrayList.size() : 0;
    }

    @NonNull
    public FragmentContainer getContainer() {
        return this.mContainer;
    }

    @Nullable
    public Fragment getFragment(@NonNull Bundle bundle, @NonNull String str) {
        String string = bundle.getString(str);
        if (string == null) {
            return null;
        }
        Fragment findActiveFragment = findActiveFragment(string);
        if (findActiveFragment == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fragment no longer exists for key ");
            stringBuilder.append(str);
            stringBuilder.append(": unique id ");
            stringBuilder.append(string);
            throwException(new IllegalStateException(stringBuilder.toString()));
        }
        return findActiveFragment;
    }

    @NonNull
    public FragmentFactory getFragmentFactory() {
        FragmentFactory fragmentFactory = this.mFragmentFactory;
        if (fragmentFactory != null) {
            return fragmentFactory;
        }
        Fragment fragment = this.mParent;
        if (fragment != null) {
            return fragment.mFragmentManager.getFragmentFactory();
        }
        return this.mHostFragmentFactory;
    }

    @NonNull
    public FragmentStore getFragmentStore() {
        return this.mFragmentStore;
    }

    @NonNull
    public List<Fragment> getFragments() {
        return this.mFragmentStore.getFragments();
    }

    @NonNull
    public FragmentHostCallback<?> getHost() {
        return this.mHost;
    }

    @NonNull
    public Factory2 getLayoutInflaterFactory() {
        return this.mLayoutInflaterFactory;
    }

    @NonNull
    public FragmentLifecycleCallbacksDispatcher getLifecycleCallbacksDispatcher() {
        return this.mLifecycleCallbacksDispatcher;
    }

    @Nullable
    public Fragment getParent() {
        return this.mParent;
    }

    @Nullable
    public Fragment getPrimaryNavigationFragment() {
        return this.mPrimaryNav;
    }

    @NonNull
    public SpecialEffectsControllerFactory getSpecialEffectsControllerFactory() {
        SpecialEffectsControllerFactory specialEffectsControllerFactory = this.mSpecialEffectsControllerFactory;
        if (specialEffectsControllerFactory != null) {
            return specialEffectsControllerFactory;
        }
        Fragment fragment = this.mParent;
        if (fragment != null) {
            return fragment.mFragmentManager.getSpecialEffectsControllerFactory();
        }
        return this.mDefaultSpecialEffectsControllerFactory;
    }

    @NonNull
    public ViewModelStore getViewModelStore(@NonNull Fragment fragment) {
        return this.mNonConfig.getViewModelStore(fragment);
    }

    public void handleOnBackPressed() {
        execPendingActions(true);
        if (this.mOnBackPressedCallback.isEnabled()) {
            popBackStackImmediate();
        } else {
            this.mOnBackPressedDispatcher.onBackPressed();
        }
    }

    public void hideFragment(@NonNull Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("hide: ");
            stringBuilder.append(fragment);
            Log.v(TAG, stringBuilder.toString());
        }
        if (!fragment.mHidden) {
            fragment.mHidden = true;
            fragment.mHiddenChanged = 1 ^ fragment.mHiddenChanged;
            setVisibleRemovingFragment(fragment);
        }
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public boolean isParentMenuVisible(@Nullable Fragment fragment) {
        return fragment == null ? true : fragment.isMenuVisible();
    }

    public boolean isPrimaryNavigation(@Nullable Fragment fragment) {
        boolean z = true;
        if (fragment == null) {
            return true;
        }
        FragmentManager fragmentManager = fragment.mFragmentManager;
        if (!(fragment.equals(fragmentManager.getPrimaryNavigationFragment()) && isPrimaryNavigation(fragmentManager.mParent))) {
            z = false;
        }
        return z;
    }

    public boolean isStateAtLeast(int i) {
        return this.mCurState >= i;
    }

    public boolean isStateSaved() {
        return this.mStateSaved || this.mStopped;
    }

    public void launchRequestPermissions(@NonNull Fragment fragment, @NonNull String[] strArr, int i) {
        if (this.mRequestPermissions != null) {
            this.mLaunchedFragments.addLast(new LaunchedFragmentInfo(fragment.mWho, i));
            this.mRequestPermissions.launch(strArr);
            return;
        }
        this.mHost.onRequestPermissionsFromFragment(fragment, strArr, i);
    }

    public void launchStartActivityForResult(@NonNull Fragment fragment, @SuppressLint({"UnknownNullness"}) Intent intent, int i, @Nullable Bundle bundle) {
        if (this.mStartActivityForResult != null) {
            this.mLaunchedFragments.addLast(new LaunchedFragmentInfo(fragment.mWho, i));
            if (!(intent == null || bundle == null)) {
                intent.putExtra(StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE, bundle);
            }
            this.mStartActivityForResult.launch(intent);
            return;
        }
        this.mHost.onStartActivityFromFragment(fragment, intent, i, bundle);
    }

    public void launchStartIntentSenderForResult(@NonNull Fragment fragment, @SuppressLint({"UnknownNullness"}) IntentSender intentSender, int i, @Nullable Intent intent, int i2, int i3, int i4, @Nullable Bundle bundle) throws SendIntentException {
        Fragment fragment2 = fragment;
        Bundle bundle2 = bundle;
        IntentSender intentSender2;
        int i5;
        if (this.mStartIntentSenderForResult != null) {
            Intent intent2;
            String str = TAG;
            if (bundle2 != null) {
                if (intent == null) {
                    intent2 = new Intent();
                    intent2.putExtra(EXTRA_CREATED_FILLIN_INTENT, true);
                } else {
                    intent2 = intent;
                }
                if (isLoggingEnabled(2)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ActivityOptions ");
                    stringBuilder.append(bundle2);
                    stringBuilder.append(" were added to fillInIntent ");
                    stringBuilder.append(intent2);
                    stringBuilder.append(" for fragment ");
                    stringBuilder.append(fragment);
                    Log.v(str, stringBuilder.toString());
                }
                intent2.putExtra(StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE, bundle2);
            } else {
                intent2 = intent;
            }
            intentSender2 = intentSender;
            IntentSenderRequest build = new Builder(intentSender).setFillInIntent(intent2).setFlags(i3, i2).build();
            i5 = i;
            this.mLaunchedFragments.addLast(new LaunchedFragmentInfo(fragment2.mWho, i));
            if (isLoggingEnabled(2)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Fragment ");
                stringBuilder2.append(fragment);
                stringBuilder2.append("is launching an IntentSender for result ");
                Log.v(str, stringBuilder2.toString());
            }
            this.mStartIntentSenderForResult.launch(build);
            return;
        }
        intentSender2 = intentSender;
        i5 = i;
        int i6 = i2;
        int i7 = i3;
        this.mHost.onStartIntentSenderFromFragment(fragment, intentSender, i, intent, i2, i3, i4, bundle);
    }

    public void moveFragmentToExpectedState(@NonNull Fragment fragment) {
        if (this.mFragmentStore.containsActiveFragment(fragment.mWho)) {
            moveToState(fragment);
            View view = fragment.mView;
            if (!(view == null || !fragment.mIsNewlyAdded || fragment.mContainer == null)) {
                float f = fragment.mPostponedAlpha;
                if (f > 0.0f) {
                    view.setAlpha(f);
                }
                fragment.mPostponedAlpha = 0.0f;
                fragment.mIsNewlyAdded = false;
                AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(this.mHost.getContext(), fragment, true);
                if (loadAnimation != null) {
                    Animation animation = loadAnimation.animation;
                    if (animation != null) {
                        fragment.mView.startAnimation(animation);
                    } else {
                        loadAnimation.animator.setTarget(fragment.mView);
                        loadAnimation.animator.start();
                    }
                }
            }
            if (fragment.mHiddenChanged) {
                completeShowHideFragment(fragment);
            }
            return;
        }
        if (isLoggingEnabled(3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring moving ");
            stringBuilder.append(fragment);
            stringBuilder.append(" to state ");
            stringBuilder.append(this.mCurState);
            stringBuilder.append("since it is not added to ");
            stringBuilder.append(this);
            Log.d(TAG, stringBuilder.toString());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x015c  */
    /* JADX WARNING: Missing block: B:22:0x0053, code skipped:
            if (r2 != 5) goto L_0x0160;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void moveToState(@NonNull Fragment fragment, int i) {
        FragmentStateManager fragmentStateManager = this.mFragmentStore.getFragmentStateManager(fragment.mWho);
        int i2 = 1;
        if (fragmentStateManager == null) {
            fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, fragment);
            fragmentStateManager.setFragmentManagerState(1);
        }
        if (fragment.mFromLayout && fragment.mInLayout && fragment.mState == 2) {
            i = Math.max(i, 2);
        }
        i = Math.min(i, fragmentStateManager.computeExpectedState());
        int i3 = fragment.mState;
        String str = TAG;
        if (i3 <= i) {
            if (i3 < i && !this.mExitAnimationCancellationSignals.isEmpty()) {
                cancelExitAnimation(fragment);
            }
            i3 = fragment.mState;
            if (i3 != -1) {
                if (i3 != 0) {
                    if (i3 != 1) {
                        if (i3 != 2) {
                            if (i3 != 4) {
                            }
                            if (i > 4) {
                                fragmentStateManager.start();
                            }
                            if (i > 5) {
                                fragmentStateManager.resume();
                            }
                        }
                        if (i > 2) {
                            fragmentStateManager.activityCreated();
                        }
                        if (i > 4) {
                        }
                        if (i > 5) {
                        }
                    }
                    if (i > -1) {
                        fragmentStateManager.ensureInflatedView();
                    }
                    if (i > 1) {
                        fragmentStateManager.createView();
                    }
                    if (i > 2) {
                    }
                    if (i > 4) {
                    }
                    if (i > 5) {
                    }
                }
            } else if (i > -1) {
                fragmentStateManager.attach();
            }
            if (i > 0) {
                fragmentStateManager.create();
            }
            if (i > -1) {
            }
            if (i > 1) {
            }
            if (i > 2) {
            }
            if (i > 4) {
            }
            if (i > 5) {
            }
        } else if (i3 > i) {
            if (i3 != 0) {
                if (i3 != 1) {
                    StringBuilder stringBuilder;
                    if (i3 != 2) {
                        if (i3 != 4) {
                            if (i3 != 5) {
                                if (i3 == 7) {
                                    if (i < 7) {
                                        fragmentStateManager.pause();
                                    }
                                }
                            }
                            if (i < 5) {
                                fragmentStateManager.stop();
                            }
                        }
                        if (i < 4) {
                            if (isLoggingEnabled(3)) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("movefrom ACTIVITY_CREATED: ");
                                stringBuilder.append(fragment);
                                Log.d(str, stringBuilder.toString());
                            }
                            if (fragment.mView != null && this.mHost.onShouldSaveFragmentState(fragment) && fragment.mSavedViewState == null) {
                                fragmentStateManager.saveViewState();
                            }
                        }
                    }
                    if (i < 2) {
                        AnimationOrAnimator animationOrAnimator = null;
                        View view = fragment.mView;
                        if (view != null) {
                            ViewGroup viewGroup = fragment.mContainer;
                            if (viewGroup != null) {
                                viewGroup.endViewTransition(view);
                                fragment.mView.clearAnimation();
                                if (!fragment.isRemovingParent()) {
                                    if (this.mCurState > -1 && !this.mDestroyed && fragment.mView.getVisibility() == 0 && fragment.mPostponedAlpha >= 0.0f) {
                                        animationOrAnimator = FragmentAnim.loadAnimation(this.mHost.getContext(), fragment, false);
                                    }
                                    fragment.mPostponedAlpha = 0.0f;
                                    ViewGroup viewGroup2 = fragment.mContainer;
                                    view = fragment.mView;
                                    if (animationOrAnimator != null) {
                                        FragmentAnim.animateRemoveFragment(fragment, animationOrAnimator, this.mFragmentTransitionCallback);
                                    }
                                    viewGroup2.removeView(view);
                                    if (isLoggingEnabled(2)) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("Removing view ");
                                        stringBuilder.append(view);
                                        stringBuilder.append(" for fragment ");
                                        stringBuilder.append(fragment);
                                        stringBuilder.append(" from container ");
                                        stringBuilder.append(viewGroup2);
                                        Log.v(str, stringBuilder.toString());
                                    }
                                    if (viewGroup2 != fragment.mContainer) {
                                        return;
                                    }
                                }
                            }
                        }
                        if (this.mExitAnimationCancellationSignals.get(fragment) == null) {
                            fragmentStateManager.destroyFragmentView();
                        }
                    }
                }
                if (i < 1) {
                    if (this.mExitAnimationCancellationSignals.get(fragment) == null) {
                        fragmentStateManager.destroy();
                    }
                    if (i2 < 0) {
                        fragmentStateManager.detach();
                    }
                    i = i2;
                }
            }
            i2 = i;
            if (i2 < 0) {
            }
            i = i2;
        }
        if (fragment.mState != i) {
            if (isLoggingEnabled(3)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("moveToState: Fragment state for ");
                stringBuilder2.append(fragment);
                stringBuilder2.append(" not updated inline; expected state ");
                stringBuilder2.append(i);
                stringBuilder2.append(" found ");
                stringBuilder2.append(fragment.mState);
                Log.d(str, stringBuilder2.toString());
            }
            fragment.mState = i;
        }
    }

    public void noteStateNotSaved() {
        if (this.mHost != null) {
            this.mStateSaved = false;
            this.mStopped = false;
            this.mNonConfig.setIsStateSaved(false);
            for (Fragment fragment : this.mFragmentStore.getFragments()) {
                if (fragment != null) {
                    fragment.noteStateNotSaved();
                }
            }
        }
    }

    @NonNull
    @Deprecated
    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public FragmentTransaction openTransaction() {
        return beginTransaction();
    }

    public void performPendingDeferredStart(@NonNull FragmentStateManager fragmentStateManager) {
        Fragment fragment = fragmentStateManager.getFragment();
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
                return;
            }
            fragment.mDeferStart = false;
            if (USE_STATE_MANAGER) {
                fragmentStateManager.moveToExpectedState();
            } else {
                moveToState(fragment);
            }
        }
    }

    public void popBackStack() {
        enqueueAction(new PopBackStackState(null, -1, 0), false);
    }

    public boolean popBackStackImmediate() {
        return popBackStackImmediate(null, -1, 0);
    }

    public boolean popBackStackState(@NonNull ArrayList<BackStackRecord> arrayList, @NonNull ArrayList<Boolean> arrayList2, @Nullable String str, int i, int i2) {
        Boolean bool = Boolean.TRUE;
        ArrayList arrayList3 = this.mBackStack;
        if (arrayList3 == null) {
            return false;
        }
        int size;
        if (str == null && i < 0 && (i2 & 1) == 0) {
            size = arrayList3.size() - 1;
            if (size < 0) {
                return false;
            }
            arrayList.add(this.mBackStack.remove(size));
            arrayList2.add(bool);
        } else {
            if (str != null || i >= 0) {
                int size2 = arrayList3.size() - 1;
                while (size2 >= 0) {
                    BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.get(size2);
                    if ((str != null && str.equals(backStackRecord.getName())) || (i >= 0 && i == backStackRecord.mIndex)) {
                        break;
                    }
                    size2--;
                }
                if (size2 < 0) {
                    return false;
                }
                if ((i2 & 1) != 0) {
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        BackStackRecord backStackRecord2 = (BackStackRecord) this.mBackStack.get(size2);
                        if (str == null || !str.equals(backStackRecord2.getName())) {
                            if (i < 0 || i != backStackRecord2.mIndex) {
                                break;
                            }
                        }
                    }
                }
                size = size2;
            } else {
                size = -1;
            }
            if (size == this.mBackStack.size() - 1) {
                return false;
            }
            for (i = this.mBackStack.size() - 1; i > size; i--) {
                arrayList.add(this.mBackStack.remove(i));
                arrayList2.add(bool);
            }
        }
        return true;
    }

    public void putFragment(@NonNull Bundle bundle, @NonNull String str, @NonNull Fragment fragment) {
        if (fragment.mFragmentManager != this) {
            throwException(new IllegalStateException(a.r("Fragment ", fragment, " is not currently in the FragmentManager")));
        }
        bundle.putString(str, fragment.mWho);
    }

    public void registerFragmentLifecycleCallbacks(@NonNull FragmentLifecycleCallbacks fragmentLifecycleCallbacks, boolean z) {
        this.mLifecycleCallbacksDispatcher.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, z);
    }

    public void removeCancellationSignal(@NonNull Fragment fragment, @NonNull CancellationSignal cancellationSignal) {
        HashSet hashSet = (HashSet) this.mExitAnimationCancellationSignals.get(fragment);
        if (hashSet != null && hashSet.remove(cancellationSignal) && hashSet.isEmpty()) {
            this.mExitAnimationCancellationSignals.remove(fragment);
            if (fragment.mState < 5) {
                destroyFragmentView(fragment);
                moveToState(fragment);
            }
        }
    }

    public void removeFragment(@NonNull Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("remove: ");
            stringBuilder.append(fragment);
            stringBuilder.append(" nesting=");
            stringBuilder.append(fragment.mBackStackNesting);
            Log.v(TAG, stringBuilder.toString());
        }
        int isInBackStack = fragment.isInBackStack() ^ 1;
        if (!fragment.mDetached || isInBackStack != 0) {
            this.mFragmentStore.removeFragment(fragment);
            if (isMenuAvailable(fragment)) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mRemoving = true;
            setVisibleRemovingFragment(fragment);
        }
    }

    public void removeFragmentOnAttachListener(@NonNull FragmentOnAttachListener fragmentOnAttachListener) {
        this.mOnAttachListeners.remove(fragmentOnAttachListener);
    }

    public void removeOnBackStackChangedListener(@NonNull OnBackStackChangedListener onBackStackChangedListener) {
        ArrayList arrayList = this.mBackStackChangeListeners;
        if (arrayList != null) {
            arrayList.remove(onBackStackChangedListener);
        }
    }

    public void removeRetainedFragment(@NonNull Fragment fragment) {
        this.mNonConfig.removeRetainedFragment(fragment);
    }

    public void restoreAllState(@Nullable Parcelable parcelable, @Nullable FragmentManagerNonConfig fragmentManagerNonConfig) {
        if (this.mHost instanceof ViewModelStoreOwner) {
            throwException(new IllegalStateException("You must use restoreSaveState when your FragmentHostCallback implements ViewModelStoreOwner"));
        }
        this.mNonConfig.restoreFromSnapshot(fragmentManagerNonConfig);
        restoreSaveState(parcelable);
    }

    public void restoreSaveState(@Nullable Parcelable parcelable) {
        if (parcelable != null) {
            FragmentManagerState fragmentManagerState = (FragmentManagerState) parcelable;
            if (fragmentManagerState.mActive != null) {
                String str;
                String str2;
                Fragment findRetainedFragmentByWho;
                StringBuilder stringBuilder;
                FragmentStateManager fragmentStateManager;
                this.mFragmentStore.resetActiveFragments();
                Iterator it = fragmentManagerState.mActive.iterator();
                while (true) {
                    boolean hasNext = it.hasNext();
                    str = "): ";
                    str2 = TAG;
                    if (!hasNext) {
                        break;
                    }
                    FragmentState fragmentState = (FragmentState) it.next();
                    if (fragmentState != null) {
                        findRetainedFragmentByWho = this.mNonConfig.findRetainedFragmentByWho(fragmentState.mWho);
                        if (findRetainedFragmentByWho != null) {
                            if (isLoggingEnabled(2)) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("restoreSaveState: re-attaching retained ");
                                stringBuilder.append(findRetainedFragmentByWho);
                                Log.v(str2, stringBuilder.toString());
                            }
                            fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, findRetainedFragmentByWho, fragmentState);
                        } else {
                            fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, this.mHost.getContext().getClassLoader(), getFragmentFactory(), fragmentState);
                        }
                        findRetainedFragmentByWho = fragmentStateManager.getFragment();
                        findRetainedFragmentByWho.mFragmentManager = this;
                        if (isLoggingEnabled(2)) {
                            StringBuilder L = a.L("restoreSaveState: active (");
                            L.append(findRetainedFragmentByWho.mWho);
                            L.append(str);
                            L.append(findRetainedFragmentByWho);
                            Log.v(str2, L.toString());
                        }
                        fragmentStateManager.restoreState(this.mHost.getContext().getClassLoader());
                        this.mFragmentStore.makeActive(fragmentStateManager);
                        fragmentStateManager.setFragmentManagerState(this.mCurState);
                    }
                }
                for (Fragment findRetainedFragmentByWho2 : this.mNonConfig.getRetainedFragments()) {
                    if (!this.mFragmentStore.containsActiveFragment(findRetainedFragmentByWho2.mWho)) {
                        if (isLoggingEnabled(2)) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Discarding retained Fragment ");
                            stringBuilder.append(findRetainedFragmentByWho2);
                            stringBuilder.append(" that was not found in the set of active Fragments ");
                            stringBuilder.append(fragmentManagerState.mActive);
                            Log.v(str2, stringBuilder.toString());
                        }
                        this.mNonConfig.removeRetainedFragment(findRetainedFragmentByWho2);
                        findRetainedFragmentByWho2.mFragmentManager = this;
                        fragmentStateManager = new FragmentStateManager(this.mLifecycleCallbacksDispatcher, this.mFragmentStore, findRetainedFragmentByWho2);
                        fragmentStateManager.setFragmentManagerState(1);
                        fragmentStateManager.moveToExpectedState();
                        findRetainedFragmentByWho2.mRemoving = true;
                        fragmentStateManager.moveToExpectedState();
                    }
                }
                this.mFragmentStore.restoreAddedFragments(fragmentManagerState.mAdded);
                int i = 0;
                if (fragmentManagerState.mBackStack != null) {
                    this.mBackStack = new ArrayList(fragmentManagerState.mBackStack.length);
                    int i2 = 0;
                    while (true) {
                        BackStackState[] backStackStateArr = fragmentManagerState.mBackStack;
                        if (i2 >= backStackStateArr.length) {
                            break;
                        }
                        BackStackRecord instantiate = backStackStateArr[i2].instantiate(this);
                        if (isLoggingEnabled(2)) {
                            StringBuilder M = a.M("restoreAllState: back stack #", i2, " (index ");
                            M.append(instantiate.mIndex);
                            M.append(str);
                            M.append(instantiate);
                            Log.v(str2, M.toString());
                            PrintWriter printWriter = new PrintWriter(new LogWriter(str2));
                            instantiate.dump("  ", printWriter, false);
                            printWriter.close();
                        }
                        this.mBackStack.add(instantiate);
                        i2++;
                    }
                } else {
                    this.mBackStack = null;
                }
                this.mBackStackIndex.set(fragmentManagerState.mBackStackIndex);
                String str3 = fragmentManagerState.mPrimaryNavActiveWho;
                if (str3 != null) {
                    Fragment findActiveFragment = findActiveFragment(str3);
                    this.mPrimaryNav = findActiveFragment;
                    dispatchParentPrimaryNavigationFragmentChanged(findActiveFragment);
                }
                ArrayList arrayList = fragmentManagerState.mResultKeys;
                if (arrayList != null) {
                    while (i < arrayList.size()) {
                        this.mResults.put(arrayList.get(i), fragmentManagerState.mResults.get(i));
                        i++;
                    }
                }
                this.mLaunchedFragments = new ArrayDeque(fragmentManagerState.mLaunchedFragments);
            }
        }
    }

    @Deprecated
    public FragmentManagerNonConfig retainNonConfig() {
        if (this.mHost instanceof ViewModelStoreOwner) {
            throwException(new IllegalStateException("You cannot use retainNonConfig when your FragmentHostCallback implements ViewModelStoreOwner."));
        }
        return this.mNonConfig.getSnapshot();
    }

    public Parcelable saveAllState() {
        forcePostponedTransactions();
        endAnimatingAwayFragments();
        execPendingActions(true);
        this.mStateSaved = true;
        this.mNonConfig.setIsStateSaved(true);
        ArrayList saveActiveFragments = this.mFragmentStore.saveActiveFragments();
        boolean isEmpty = saveActiveFragments.isEmpty();
        String str = TAG;
        BackStackState[] backStackStateArr = null;
        if (isEmpty) {
            if (isLoggingEnabled(2)) {
                Log.v(str, "saveAllState: no fragments!");
            }
            return null;
        }
        ArrayList saveAddedFragments = this.mFragmentStore.saveAddedFragments();
        ArrayList arrayList = this.mBackStack;
        if (arrayList != null) {
            int size = arrayList.size();
            if (size > 0) {
                backStackStateArr = new BackStackState[size];
                for (int i = 0; i < size; i++) {
                    backStackStateArr[i] = new BackStackState((BackStackRecord) this.mBackStack.get(i));
                    if (isLoggingEnabled(2)) {
                        StringBuilder M = a.M("saveAllState: adding back stack #", i, ": ");
                        M.append(this.mBackStack.get(i));
                        Log.v(str, M.toString());
                    }
                }
            }
        }
        FragmentManagerState fragmentManagerState = new FragmentManagerState();
        fragmentManagerState.mActive = saveActiveFragments;
        fragmentManagerState.mAdded = saveAddedFragments;
        fragmentManagerState.mBackStack = backStackStateArr;
        fragmentManagerState.mBackStackIndex = this.mBackStackIndex.get();
        Fragment fragment = this.mPrimaryNav;
        if (fragment != null) {
            fragmentManagerState.mPrimaryNavActiveWho = fragment.mWho;
        }
        fragmentManagerState.mResultKeys.addAll(this.mResults.keySet());
        fragmentManagerState.mResults.addAll(this.mResults.values());
        fragmentManagerState.mLaunchedFragments = new ArrayList(this.mLaunchedFragments);
        return fragmentManagerState;
    }

    @Nullable
    public SavedState saveFragmentInstanceState(@NonNull Fragment fragment) {
        FragmentStateManager fragmentStateManager = this.mFragmentStore.getFragmentStateManager(fragment.mWho);
        if (fragmentStateManager == null || !fragmentStateManager.getFragment().equals(fragment)) {
            throwException(new IllegalStateException(a.r("Fragment ", fragment, " is not currently in the FragmentManager")));
        }
        return fragmentStateManager.saveInstanceState();
    }

    public void scheduleCommit() {
        synchronized (this.mPendingActions) {
            ArrayList arrayList = this.mPostponedTransactions;
            Object obj = null;
            Object obj2 = (arrayList == null || arrayList.isEmpty()) ? null : 1;
            if (this.mPendingActions.size() == 1) {
                obj = 1;
            }
            if (!(obj2 == null && obj == null)) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
                updateOnBackPressedCallbackEnabled();
            }
        }
    }

    public void setExitAnimationOrder(@NonNull Fragment fragment, boolean z) {
        ViewGroup fragmentContainer = getFragmentContainer(fragment);
        if (fragmentContainer != null && (fragmentContainer instanceof FragmentContainerView)) {
            ((FragmentContainerView) fragmentContainer).setDrawDisappearingViewsLast(z ^ 1);
        }
    }

    public void setFragmentFactory(@NonNull FragmentFactory fragmentFactory) {
        this.mFragmentFactory = fragmentFactory;
    }

    public final void setFragmentResult(@NonNull String str, @NonNull Bundle bundle) {
        LifecycleAwareResultListener lifecycleAwareResultListener = (LifecycleAwareResultListener) this.mResultListeners.get(str);
        if (lifecycleAwareResultListener == null || !lifecycleAwareResultListener.isAtLeast(State.STARTED)) {
            this.mResults.put(str, bundle);
        } else {
            lifecycleAwareResultListener.onFragmentResult(str, bundle);
        }
    }

    @SuppressLint({"SyntheticAccessor"})
    public final void setFragmentResultListener(@NonNull final String str, @NonNull LifecycleOwner lifecycleOwner, @NonNull final FragmentResultListener fragmentResultListener) {
        final Lifecycle lifecycle = lifecycleOwner.getLifecycle();
        if (lifecycle.getCurrentState() != State.DESTROYED) {
            AnonymousClass6 anonymousClass6 = new LifecycleEventObserver() {
                public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
                    if (event == Event.ON_START) {
                        Bundle bundle = (Bundle) FragmentManager.this.mResults.get(str);
                        if (bundle != null) {
                            fragmentResultListener.onFragmentResult(str, bundle);
                            FragmentManager.this.clearFragmentResult(str);
                        }
                    }
                    if (event == Event.ON_DESTROY) {
                        lifecycle.removeObserver(this);
                        FragmentManager.this.mResultListeners.remove(str);
                    }
                }
            };
            lifecycle.addObserver(anonymousClass6);
            LifecycleAwareResultListener lifecycleAwareResultListener = (LifecycleAwareResultListener) this.mResultListeners.put(str, new LifecycleAwareResultListener(lifecycle, fragmentResultListener, anonymousClass6));
            if (lifecycleAwareResultListener != null) {
                lifecycleAwareResultListener.removeObserver();
            }
        }
    }

    public void setMaxLifecycle(@NonNull Fragment fragment, @NonNull State state) {
        if (fragment.equals(findActiveFragment(fragment.mWho)) && (fragment.mHost == null || fragment.mFragmentManager == this)) {
            fragment.mMaxState = state;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Fragment ");
        stringBuilder.append(fragment);
        stringBuilder.append(" is not an active fragment of FragmentManager ");
        stringBuilder.append(this);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void setPrimaryNavigationFragment(@Nullable Fragment fragment) {
        if (fragment == null || (fragment.equals(findActiveFragment(fragment.mWho)) && (fragment.mHost == null || fragment.mFragmentManager == this))) {
            Fragment fragment2 = this.mPrimaryNav;
            this.mPrimaryNav = fragment;
            dispatchParentPrimaryNavigationFragmentChanged(fragment2);
            dispatchParentPrimaryNavigationFragmentChanged(this.mPrimaryNav);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Fragment ");
        stringBuilder.append(fragment);
        stringBuilder.append(" is not an active fragment of FragmentManager ");
        stringBuilder.append(this);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void setSpecialEffectsControllerFactory(@NonNull SpecialEffectsControllerFactory specialEffectsControllerFactory) {
        this.mSpecialEffectsControllerFactory = specialEffectsControllerFactory;
    }

    public void showFragment(@NonNull Fragment fragment) {
        if (isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("show: ");
            stringBuilder.append(fragment);
            Log.v(TAG, stringBuilder.toString());
        }
        if (fragment.mHidden) {
            fragment.mHidden = false;
            fragment.mHiddenChanged ^= 1;
        }
    }

    @NonNull
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append("FragmentManager{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(" in ");
        Fragment fragment = this.mParent;
        String str = "}";
        String str2 = "{";
        if (fragment != null) {
            stringBuilder.append(fragment.getClass().getSimpleName());
            stringBuilder.append(str2);
            stringBuilder.append(Integer.toHexString(System.identityHashCode(this.mParent)));
            stringBuilder.append(str);
        } else {
            FragmentHostCallback fragmentHostCallback = this.mHost;
            if (fragmentHostCallback != null) {
                stringBuilder.append(fragmentHostCallback.getClass().getSimpleName());
                stringBuilder.append(str2);
                stringBuilder.append(Integer.toHexString(System.identityHashCode(this.mHost)));
                stringBuilder.append(str);
            } else {
                stringBuilder.append("null");
            }
        }
        stringBuilder.append("}}");
        return stringBuilder.toString();
    }

    public void unregisterFragmentLifecycleCallbacks(@NonNull FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {
        this.mLifecycleCallbacksDispatcher.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
    }

    public void popBackStack(@Nullable String str, int i) {
        enqueueAction(new PopBackStackState(str, -1, i), false);
    }

    public boolean popBackStackImmediate(@Nullable String str, int i) {
        return popBackStackImmediate(str, -1, i);
    }

    public void popBackStack(int i, int i2) {
        if (i >= 0) {
            enqueueAction(new PopBackStackState(null, i, i2), false);
            return;
        }
        throw new IllegalArgumentException(a.k("Bad id: ", i));
    }

    public boolean popBackStackImmediate(int i, int i2) {
        if (i >= 0) {
            return popBackStackImmediate(null, i, i2);
        }
        throw new IllegalArgumentException(a.k("Bad id: ", i));
    }

    private boolean popBackStackImmediate(@Nullable String str, int i, int i2) {
        execPendingActions(false);
        ensureExecReady(true);
        Fragment fragment = this.mPrimaryNav;
        if (fragment != null && i < 0 && str == null && fragment.getChildFragmentManager().popBackStackImmediate()) {
            return true;
        }
        boolean popBackStackState = popBackStackState(this.mTmpRecords, this.mTmpIsPop, str, i, i2);
        if (popBackStackState) {
            this.mExecutingActions = true;
            try {
                removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
            } finally {
                cleanupExec();
            }
        }
        updateOnBackPressedCallbackEnabled();
        doPendingDeferredStart();
        this.mFragmentStore.burpActive();
        return popBackStackState;
    }

    public void moveToState(@NonNull Fragment fragment) {
        moveToState(fragment, this.mCurState);
    }

    public void moveToState(int i, boolean z) {
        if (this.mHost == null && i != -1) {
            throw new IllegalStateException("No activity");
        } else if (z || i != this.mCurState) {
            this.mCurState = i;
            if (USE_STATE_MANAGER) {
                this.mFragmentStore.moveToExpectedState();
            } else {
                for (Fragment moveFragmentToExpectedState : this.mFragmentStore.getFragments()) {
                    moveFragmentToExpectedState(moveFragmentToExpectedState);
                }
                for (FragmentStateManager fragmentStateManager : this.mFragmentStore.getActiveFragmentStateManagers()) {
                    Fragment fragment = fragmentStateManager.getFragment();
                    if (!fragment.mIsNewlyAdded) {
                        moveFragmentToExpectedState(fragment);
                    }
                    Object obj = (!fragment.mRemoving || fragment.isInBackStack()) ? null : 1;
                    if (obj != null) {
                        this.mFragmentStore.makeInactive(fragmentStateManager);
                    }
                }
            }
            startPendingDeferredFragments();
            if (this.mNeedMenuInvalidate) {
                FragmentHostCallback fragmentHostCallback = this.mHost;
                if (fragmentHostCallback != null && this.mCurState == 7) {
                    fragmentHostCallback.onSupportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = false;
                }
            }
        }
    }
}
