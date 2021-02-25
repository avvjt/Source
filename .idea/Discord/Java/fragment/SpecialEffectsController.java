package androidx.fragment.app;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.CancellationSignal;
import androidx.core.os.CancellationSignal.OnCancelListener;
import androidx.fragment.R;
import f.d.b.a.a;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public abstract class SpecialEffectsController {
    private final ViewGroup mContainer;
    public boolean mIsContainerPostponed = false;
    public boolean mOperationDirectionIsPop = false;
    public final ArrayList<Operation> mPendingOperations = new ArrayList();
    public final ArrayList<Operation> mRunningOperations = new ArrayList();

    /* renamed from: androidx.fragment.app.SpecialEffectsController$3 */
    public static /* synthetic */ class AnonymousClass3 {
        public static final /* synthetic */ int[] $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$LifecycleImpact;
        public static final /* synthetic */ int[] $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;

        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x002d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0027 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0033 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|(2:1|2)|3|5|6|7|9|10|11|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Can't wrap try/catch for region: R(17:0|(2:1|2)|3|5|6|7|(2:9|10)|11|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Missing block: B:23:?, code skipped:
            return;
     */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static {
            int[] iArr;
            LifecycleImpact.values();
            int[] iArr2 = new int[3];
            $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$LifecycleImpact = iArr2;
            try {
                LifecycleImpact lifecycleImpact = LifecycleImpact.ADDING;
                iArr2[1] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$LifecycleImpact;
                LifecycleImpact lifecycleImpact2 = LifecycleImpact.REMOVING;
                iArr[2] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                int[] iArr3 = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$LifecycleImpact;
                LifecycleImpact lifecycleImpact3 = LifecycleImpact.NONE;
                iArr3[0] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            State.values();
            int[] iArr4 = new int[4];
            $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State = iArr4;
            State state = State.REMOVED;
            iArr4[0] = 1;
            iArr = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;
            State state2 = State.VISIBLE;
            iArr[1] = 2;
            int[] iArr5 = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;
            State state3 = State.GONE;
            iArr5[2] = 3;
            iArr2 = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;
            State state4 = State.INVISIBLE;
            iArr2[3] = 4;
        }
    }

    public static class Operation {
        @NonNull
        private final List<Runnable> mCompletionListeners = new ArrayList();
        @NonNull
        private State mFinalState;
        @NonNull
        private final Fragment mFragment;
        private boolean mIsCanceled = false;
        private boolean mIsComplete = false;
        @NonNull
        private LifecycleImpact mLifecycleImpact;
        @NonNull
        private final HashSet<CancellationSignal> mSpecialEffectsSignals = new HashSet();

        public enum LifecycleImpact {
            NONE,
            ADDING,
            REMOVING
        }

        public enum State {
            REMOVED,
            VISIBLE,
            GONE,
            INVISIBLE;

            @NonNull
            public static State from(@NonNull View view) {
                if (view.getAlpha() == 0.0f && view.getVisibility() == 0) {
                    return INVISIBLE;
                }
                return from(view.getVisibility());
            }

            public void applyState(@NonNull View view) {
                int ordinal = ordinal();
                String str = FragmentManager.TAG;
                if (ordinal != 0) {
                    String str2 = "SpecialEffectsController: Setting view ";
                    StringBuilder stringBuilder;
                    if (ordinal == 1) {
                        if (FragmentManager.isLoggingEnabled(2)) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append(view);
                            stringBuilder.append(" to VISIBLE");
                            Log.v(str, stringBuilder.toString());
                        }
                        view.setVisibility(0);
                        return;
                    } else if (ordinal == 2) {
                        if (FragmentManager.isLoggingEnabled(2)) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append(view);
                            stringBuilder.append(" to GONE");
                            Log.v(str, stringBuilder.toString());
                        }
                        view.setVisibility(8);
                        return;
                    } else if (ordinal == 3) {
                        if (FragmentManager.isLoggingEnabled(2)) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append(view);
                            stringBuilder.append(" to INVISIBLE");
                            Log.v(str, stringBuilder.toString());
                        }
                        view.setVisibility(4);
                        return;
                    } else {
                        return;
                    }
                }
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != null) {
                    if (FragmentManager.isLoggingEnabled(2)) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("SpecialEffectsController: Removing view ");
                        stringBuilder2.append(view);
                        stringBuilder2.append(" from container ");
                        stringBuilder2.append(viewGroup);
                        Log.v(str, stringBuilder2.toString());
                    }
                    viewGroup.removeView(view);
                }
            }

            @NonNull
            public static State from(int i) {
                if (i == 0) {
                    return VISIBLE;
                }
                if (i == 4) {
                    return INVISIBLE;
                }
                if (i == 8) {
                    return GONE;
                }
                throw new IllegalArgumentException(a.k("Unknown visibility ", i));
            }
        }

        public Operation(@NonNull State state, @NonNull LifecycleImpact lifecycleImpact, @NonNull Fragment fragment, @NonNull CancellationSignal cancellationSignal) {
            this.mFinalState = state;
            this.mLifecycleImpact = lifecycleImpact;
            this.mFragment = fragment;
            cancellationSignal.setOnCancelListener(new OnCancelListener() {
                public void onCancel() {
                    Operation.this.cancel();
                }
            });
        }

        public final void addCompletionListener(@NonNull Runnable runnable) {
            this.mCompletionListeners.add(runnable);
        }

        public final void cancel() {
            if (!isCanceled()) {
                this.mIsCanceled = true;
                if (this.mSpecialEffectsSignals.isEmpty()) {
                    complete();
                } else {
                    Iterator it = new ArrayList(this.mSpecialEffectsSignals).iterator();
                    while (it.hasNext()) {
                        ((CancellationSignal) it.next()).cancel();
                    }
                }
            }
        }

        @CallSuper
        public void complete() {
            if (!this.mIsComplete) {
                if (FragmentManager.isLoggingEnabled(2)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SpecialEffectsController: ");
                    stringBuilder.append(this);
                    stringBuilder.append(" has called complete.");
                    Log.v(FragmentManager.TAG, stringBuilder.toString());
                }
                this.mIsComplete = true;
                for (Runnable run : this.mCompletionListeners) {
                    run.run();
                }
            }
        }

        public final void completeSpecialEffect(@NonNull CancellationSignal cancellationSignal) {
            if (this.mSpecialEffectsSignals.remove(cancellationSignal) && this.mSpecialEffectsSignals.isEmpty()) {
                complete();
            }
        }

        @NonNull
        public State getFinalState() {
            return this.mFinalState;
        }

        @NonNull
        public final Fragment getFragment() {
            return this.mFragment;
        }

        @NonNull
        public LifecycleImpact getLifecycleImpact() {
            return this.mLifecycleImpact;
        }

        public final boolean isCanceled() {
            return this.mIsCanceled;
        }

        public final boolean isComplete() {
            return this.mIsComplete;
        }

        public final void markStartedSpecialEffect(@NonNull CancellationSignal cancellationSignal) {
            onStart();
            this.mSpecialEffectsSignals.add(cancellationSignal);
        }

        public final void mergeWith(@NonNull State state, @NonNull LifecycleImpact lifecycleImpact) {
            int ordinal = lifecycleImpact.ordinal();
            String str = " mFinalState = ";
            String str2 = "SpecialEffectsController: For fragment ";
            String str3 = FragmentManager.TAG;
            if (ordinal != 0) {
                StringBuilder L;
                if (ordinal != 1) {
                    if (ordinal == 2) {
                        if (FragmentManager.isLoggingEnabled(2)) {
                            L = a.L(str2);
                            L.append(this.mFragment);
                            L.append(str);
                            L.append(this.mFinalState);
                            L.append(" -> REMOVED. mLifecycleImpact  = ");
                            L.append(this.mLifecycleImpact);
                            L.append(" to REMOVING.");
                            Log.v(str3, L.toString());
                        }
                        this.mFinalState = State.REMOVED;
                        this.mLifecycleImpact = LifecycleImpact.REMOVING;
                    }
                } else if (this.mFinalState == State.REMOVED) {
                    if (FragmentManager.isLoggingEnabled(2)) {
                        L = a.L(str2);
                        L.append(this.mFragment);
                        L.append(" mFinalState = REMOVED -> VISIBLE. mLifecycleImpact = ");
                        L.append(this.mLifecycleImpact);
                        L.append(" to ADDING.");
                        Log.v(str3, L.toString());
                    }
                    this.mFinalState = State.VISIBLE;
                    this.mLifecycleImpact = LifecycleImpact.ADDING;
                }
            } else if (this.mFinalState != State.REMOVED) {
                if (FragmentManager.isLoggingEnabled(2)) {
                    StringBuilder L2 = a.L(str2);
                    L2.append(this.mFragment);
                    L2.append(str);
                    L2.append(this.mFinalState);
                    L2.append(" -> ");
                    L2.append(state);
                    L2.append(". ");
                    Log.v(str3, L2.toString());
                }
                this.mFinalState = state;
            }
        }

        public void onStart() {
        }

        @NonNull
        public String toString() {
            String str = "{";
            StringBuilder P = a.P("Operation ", str);
            P.append(Integer.toHexString(System.identityHashCode(this)));
            String str2 = "} ";
            P.append(str2);
            P.append(str);
            P.append("mFinalState = ");
            P.append(this.mFinalState);
            P.append(str2);
            P.append(str);
            P.append("mLifecycleImpact = ");
            P.append(this.mLifecycleImpact);
            P.append(str2);
            P.append(str);
            P.append("mFragment = ");
            P.append(this.mFragment);
            P.append("}");
            return P.toString();
        }
    }

    public static class FragmentStateManagerOperation extends Operation {
        @NonNull
        private final FragmentStateManager mFragmentStateManager;

        public FragmentStateManagerOperation(@NonNull State state, @NonNull LifecycleImpact lifecycleImpact, @NonNull FragmentStateManager fragmentStateManager, @NonNull CancellationSignal cancellationSignal) {
            super(state, lifecycleImpact, fragmentStateManager.getFragment(), cancellationSignal);
            this.mFragmentStateManager = fragmentStateManager;
        }

        public void complete() {
            super.complete();
            this.mFragmentStateManager.moveToExpectedState();
        }

        public void onStart() {
            Fragment fragment = this.mFragmentStateManager.getFragment();
            View findFocus = fragment.mView.findFocus();
            if (findFocus != null) {
                fragment.setFocusedView(findFocus);
                if (FragmentManager.isLoggingEnabled(2)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("requestFocus: Saved focused view ");
                    stringBuilder.append(findFocus);
                    stringBuilder.append(" for Fragment ");
                    stringBuilder.append(fragment);
                    Log.v(FragmentManager.TAG, stringBuilder.toString());
                }
            }
            if (getLifecycleImpact() == LifecycleImpact.ADDING) {
                findFocus = getFragment().requireView();
                if (findFocus.getParent() == null) {
                    this.mFragmentStateManager.addViewToContainer();
                    findFocus.setAlpha(0.0f);
                }
                if (findFocus.getAlpha() == 0.0f && findFocus.getVisibility() == 0) {
                    findFocus.setVisibility(4);
                }
                findFocus.setAlpha(fragment.getPostOnViewCreatedAlpha());
            }
        }
    }

    public SpecialEffectsController(@NonNull ViewGroup viewGroup) {
        this.mContainer = viewGroup;
    }

    private void enqueue(@NonNull State state, @NonNull LifecycleImpact lifecycleImpact, @NonNull FragmentStateManager fragmentStateManager) {
        synchronized (this.mPendingOperations) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            Operation findPendingOperation = findPendingOperation(fragmentStateManager.getFragment());
            if (findPendingOperation != null) {
                findPendingOperation.mergeWith(state, lifecycleImpact);
                return;
            }
            final FragmentStateManagerOperation fragmentStateManagerOperation = new FragmentStateManagerOperation(state, lifecycleImpact, fragmentStateManager, cancellationSignal);
            this.mPendingOperations.add(fragmentStateManagerOperation);
            fragmentStateManagerOperation.addCompletionListener(new Runnable() {
                public void run() {
                    if (SpecialEffectsController.this.mPendingOperations.contains(fragmentStateManagerOperation)) {
                        fragmentStateManagerOperation.getFinalState().applyState(fragmentStateManagerOperation.getFragment().mView);
                    }
                }
            });
            fragmentStateManagerOperation.addCompletionListener(new Runnable() {
                public void run() {
                    SpecialEffectsController.this.mPendingOperations.remove(fragmentStateManagerOperation);
                    SpecialEffectsController.this.mRunningOperations.remove(fragmentStateManagerOperation);
                }
            });
        }
    }

    @Nullable
    private Operation findPendingOperation(@NonNull Fragment fragment) {
        Iterator it = this.mPendingOperations.iterator();
        while (it.hasNext()) {
            Operation operation = (Operation) it.next();
            if (operation.getFragment().equals(fragment) && !operation.isCanceled()) {
                return operation;
            }
        }
        return null;
    }

    @Nullable
    private Operation findRunningOperation(@NonNull Fragment fragment) {
        Iterator it = this.mRunningOperations.iterator();
        while (it.hasNext()) {
            Operation operation = (Operation) it.next();
            if (operation.getFragment().equals(fragment) && !operation.isCanceled()) {
                return operation;
            }
        }
        return null;
    }

    @NonNull
    public static SpecialEffectsController getOrCreateController(@NonNull ViewGroup viewGroup, @NonNull FragmentManager fragmentManager) {
        return getOrCreateController(viewGroup, fragmentManager.getSpecialEffectsControllerFactory());
    }

    private void updateFinalState() {
        Iterator it = this.mPendingOperations.iterator();
        while (it.hasNext()) {
            Operation operation = (Operation) it.next();
            if (operation.getLifecycleImpact() == LifecycleImpact.ADDING) {
                operation.mergeWith(State.from(operation.getFragment().requireView().getVisibility()), LifecycleImpact.NONE);
            }
        }
    }

    public void enqueueAdd(@NonNull State state, @NonNull FragmentStateManager fragmentStateManager) {
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder L = a.L("SpecialEffectsController: Enqueuing add operation for fragment ");
            L.append(fragmentStateManager.getFragment());
            Log.v(FragmentManager.TAG, L.toString());
        }
        enqueue(state, LifecycleImpact.ADDING, fragmentStateManager);
    }

    public void enqueueHide(@NonNull FragmentStateManager fragmentStateManager) {
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder L = a.L("SpecialEffectsController: Enqueuing hide operation for fragment ");
            L.append(fragmentStateManager.getFragment());
            Log.v(FragmentManager.TAG, L.toString());
        }
        enqueue(State.GONE, LifecycleImpact.NONE, fragmentStateManager);
    }

    public void enqueueRemove(@NonNull FragmentStateManager fragmentStateManager) {
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder L = a.L("SpecialEffectsController: Enqueuing remove operation for fragment ");
            L.append(fragmentStateManager.getFragment());
            Log.v(FragmentManager.TAG, L.toString());
        }
        enqueue(State.REMOVED, LifecycleImpact.REMOVING, fragmentStateManager);
    }

    public void enqueueShow(@NonNull FragmentStateManager fragmentStateManager) {
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder L = a.L("SpecialEffectsController: Enqueuing show operation for fragment ");
            L.append(fragmentStateManager.getFragment());
            Log.v(FragmentManager.TAG, L.toString());
        }
        enqueue(State.VISIBLE, LifecycleImpact.NONE, fragmentStateManager);
    }

    public abstract void executeOperations(@NonNull List<Operation> list, boolean z);

    public void executePendingOperations() {
        if (!this.mIsContainerPostponed) {
            synchronized (this.mPendingOperations) {
                if (!this.mPendingOperations.isEmpty()) {
                    ArrayList arrayList = new ArrayList(this.mRunningOperations);
                    this.mRunningOperations.clear();
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        Operation operation = (Operation) it.next();
                        if (FragmentManager.isLoggingEnabled(2)) {
                            String str = FragmentManager.TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("SpecialEffectsController: Cancelling operation ");
                            stringBuilder.append(operation);
                            Log.v(str, stringBuilder.toString());
                        }
                        operation.cancel();
                        if (!operation.isComplete()) {
                            this.mRunningOperations.add(operation);
                        }
                    }
                    updateFinalState();
                    arrayList = new ArrayList(this.mPendingOperations);
                    this.mPendingOperations.clear();
                    this.mRunningOperations.addAll(arrayList);
                    Iterator it2 = arrayList.iterator();
                    while (it2.hasNext()) {
                        ((Operation) it2.next()).onStart();
                    }
                    executeOperations(arrayList, this.mOperationDirectionIsPop);
                    this.mOperationDirectionIsPop = false;
                }
            }
        }
    }

    public void forceCompleteAllOperations() {
        synchronized (this.mPendingOperations) {
            Operation operation;
            updateFinalState();
            Iterator it = this.mPendingOperations.iterator();
            while (it.hasNext()) {
                ((Operation) it.next()).onStart();
            }
            it = new ArrayList(this.mRunningOperations).iterator();
            while (it.hasNext()) {
                operation = (Operation) it.next();
                if (FragmentManager.isLoggingEnabled(2)) {
                    String str = FragmentManager.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SpecialEffectsController: Cancelling running operation ");
                    stringBuilder.append(operation);
                    Log.v(str, stringBuilder.toString());
                }
                operation.cancel();
            }
            it = new ArrayList(this.mPendingOperations).iterator();
            while (it.hasNext()) {
                operation = (Operation) it.next();
                if (FragmentManager.isLoggingEnabled(2)) {
                    String str2 = FragmentManager.TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("SpecialEffectsController: Cancelling pending operation ");
                    stringBuilder2.append(operation);
                    Log.v(str2, stringBuilder2.toString());
                }
                operation.cancel();
            }
        }
    }

    public void forcePostponedExecutePendingOperations() {
        if (this.mIsContainerPostponed) {
            this.mIsContainerPostponed = false;
            executePendingOperations();
        }
    }

    @Nullable
    public LifecycleImpact getAwaitingCompletionLifecycleImpact(@NonNull FragmentStateManager fragmentStateManager) {
        Operation findPendingOperation = findPendingOperation(fragmentStateManager.getFragment());
        if (findPendingOperation != null) {
            return findPendingOperation.getLifecycleImpact();
        }
        Operation findRunningOperation = findRunningOperation(fragmentStateManager.getFragment());
        return findRunningOperation != null ? findRunningOperation.getLifecycleImpact() : null;
    }

    @NonNull
    public ViewGroup getContainer() {
        return this.mContainer;
    }

    public void markPostponedState() {
        synchronized (this.mPendingOperations) {
            updateFinalState();
            this.mIsContainerPostponed = false;
            for (int size = this.mPendingOperations.size() - 1; size >= 0; size--) {
                Operation operation = (Operation) this.mPendingOperations.get(size);
                State from = State.from(operation.getFragment().mView);
                State finalState = operation.getFinalState();
                State state = State.VISIBLE;
                if (finalState == state && from != state) {
                    this.mIsContainerPostponed = operation.getFragment().isPostponed();
                    break;
                }
            }
        }
    }

    public void updateOperationDirection(boolean z) {
        this.mOperationDirectionIsPop = z;
    }

    @NonNull
    public static SpecialEffectsController getOrCreateController(@NonNull ViewGroup viewGroup, @NonNull SpecialEffectsControllerFactory specialEffectsControllerFactory) {
        int i = R.id.special_effects_controller_view_tag;
        Object tag = viewGroup.getTag(i);
        if (tag instanceof SpecialEffectsController) {
            return (SpecialEffectsController) tag;
        }
        SpecialEffectsController createController = specialEffectsControllerFactory.createController(viewGroup);
        viewGroup.setTag(i, createController);
        return createController;
    }
}
