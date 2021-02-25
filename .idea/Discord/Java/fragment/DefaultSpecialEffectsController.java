package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import androidx.core.app.SharedElementCallback;
import androidx.core.os.CancellationSignal;
import androidx.core.os.CancellationSignal.OnCancelListener;
import androidx.core.util.Preconditions;
import androidx.core.view.OneShotPreDrawListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.FragmentAnim.AnimationOrAnimator;
import androidx.fragment.app.FragmentAnim.EndViewTransitionAnimation;
import androidx.fragment.app.SpecialEffectsController.Operation;
import androidx.fragment.app.SpecialEffectsController.Operation.State;
import f.d.b.a.a;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultSpecialEffectsController extends SpecialEffectsController {

    /* renamed from: androidx.fragment.app.DefaultSpecialEffectsController$10 */
    public static /* synthetic */ class AnonymousClass10 {
        public static final /* synthetic */ int[] $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;

        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x001c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0015 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(9:0|1|2|3|5|6|7|8|(3:9|10|12)) */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static {
            State state;
            State.values();
            int[] iArr = new int[4];
            $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State = iArr;
            try {
                state = State.GONE;
                iArr[2] = 1;
            } catch (NoSuchFieldError unused) {
            }
            int[] iArr2 = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;
            State state2 = State.INVISIBLE;
            iArr2[3] = 2;
            int[] iArr3 = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;
            state = State.REMOVED;
            iArr3[0] = 3;
            try {
                iArr = $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;
                State state3 = State.VISIBLE;
                iArr[1] = 4;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public static class SpecialEffectsInfo {
        @NonNull
        private final Operation mOperation;
        @NonNull
        private final CancellationSignal mSignal;

        public SpecialEffectsInfo(@NonNull Operation operation, @NonNull CancellationSignal cancellationSignal) {
            this.mOperation = operation;
            this.mSignal = cancellationSignal;
        }

        public void completeSpecialEffect() {
            this.mOperation.completeSpecialEffect(this.mSignal);
        }

        @NonNull
        public Operation getOperation() {
            return this.mOperation;
        }

        @NonNull
        public CancellationSignal getSignal() {
            return this.mSignal;
        }

        public boolean isVisibilityUnchanged() {
            State from = State.from(this.mOperation.getFragment().mView);
            State finalState = this.mOperation.getFinalState();
            if (from != finalState) {
                State state = State.VISIBLE;
                if (from == state || finalState == state) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class AnimationInfo extends SpecialEffectsInfo {
        @Nullable
        private AnimationOrAnimator mAnimation;
        private boolean mLoadedAnim = false;

        public AnimationInfo(@NonNull Operation operation, @NonNull CancellationSignal cancellationSignal) {
            super(operation, cancellationSignal);
        }

        @Nullable
        public AnimationOrAnimator getAnimation(@NonNull Context context) {
            if (this.mLoadedAnim) {
                return this.mAnimation;
            }
            AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(context, getOperation().getFragment(), getOperation().getFinalState() == State.VISIBLE);
            this.mAnimation = loadAnimation;
            this.mLoadedAnim = true;
            return loadAnimation;
        }
    }

    public static class TransitionInfo extends SpecialEffectsInfo {
        private final boolean mOverlapAllowed;
        @Nullable
        private final Object mSharedElementTransition;
        @Nullable
        private final Object mTransition;

        public TransitionInfo(@NonNull Operation operation, @NonNull CancellationSignal cancellationSignal, boolean z, boolean z2) {
            super(operation, cancellationSignal);
            Object reenterTransition;
            if (operation.getFinalState() == State.VISIBLE) {
                boolean allowReturnTransitionOverlap;
                if (z) {
                    reenterTransition = operation.getFragment().getReenterTransition();
                } else {
                    reenterTransition = operation.getFragment().getEnterTransition();
                }
                this.mTransition = reenterTransition;
                if (z) {
                    allowReturnTransitionOverlap = operation.getFragment().getAllowReturnTransitionOverlap();
                } else {
                    allowReturnTransitionOverlap = operation.getFragment().getAllowEnterTransitionOverlap();
                }
                this.mOverlapAllowed = allowReturnTransitionOverlap;
            } else {
                if (z) {
                    reenterTransition = operation.getFragment().getReturnTransition();
                } else {
                    reenterTransition = operation.getFragment().getExitTransition();
                }
                this.mTransition = reenterTransition;
                this.mOverlapAllowed = true;
            }
            if (!z2) {
                this.mSharedElementTransition = null;
            } else if (z) {
                this.mSharedElementTransition = operation.getFragment().getSharedElementReturnTransition();
            } else {
                this.mSharedElementTransition = operation.getFragment().getSharedElementEnterTransition();
            }
        }

        @Nullable
        public FragmentTransitionImpl getHandlingImpl() {
            FragmentTransitionImpl handlingImpl = getHandlingImpl(this.mTransition);
            FragmentTransitionImpl handlingImpl2 = getHandlingImpl(this.mSharedElementTransition);
            if (handlingImpl == null || handlingImpl2 == null || handlingImpl == handlingImpl2) {
                return handlingImpl != null ? handlingImpl : handlingImpl2;
            } else {
                StringBuilder L = a.L("Mixing framework transitions and AndroidX transitions is not allowed. Fragment ");
                L.append(getOperation().getFragment());
                L.append(" returned Transition ");
                L.append(this.mTransition);
                L.append(" which uses a different Transition  type than its shared element transition ");
                L.append(this.mSharedElementTransition);
                throw new IllegalArgumentException(L.toString());
            }
        }

        @Nullable
        public Object getSharedElementTransition() {
            return this.mSharedElementTransition;
        }

        @Nullable
        public Object getTransition() {
            return this.mTransition;
        }

        public boolean hasSharedElementTransition() {
            return this.mSharedElementTransition != null;
        }

        public boolean isOverlapAllowed() {
            return this.mOverlapAllowed;
        }

        @Nullable
        private FragmentTransitionImpl getHandlingImpl(Object obj) {
            if (obj == null) {
                return null;
            }
            FragmentTransitionImpl fragmentTransitionImpl = FragmentTransition.PLATFORM_IMPL;
            if (fragmentTransitionImpl != null && fragmentTransitionImpl.canHandle(obj)) {
                return fragmentTransitionImpl;
            }
            fragmentTransitionImpl = FragmentTransition.SUPPORT_IMPL;
            if (fragmentTransitionImpl != null && fragmentTransitionImpl.canHandle(obj)) {
                return fragmentTransitionImpl;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Transition ");
            stringBuilder.append(obj);
            stringBuilder.append(" for fragment ");
            stringBuilder.append(getOperation().getFragment());
            stringBuilder.append(" is not a valid framework Transition or AndroidX Transition");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public DefaultSpecialEffectsController(@NonNull ViewGroup viewGroup) {
        super(viewGroup);
    }

    private void startAnimations(@NonNull List<AnimationInfo> list, @NonNull List<Operation> list2, boolean z, @NonNull Map<Operation, Boolean> map) {
        String str;
        final Operation operation;
        final ViewGroup container = getContainer();
        Context context = container.getContext();
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        Object obj = null;
        while (true) {
            boolean hasNext = it.hasNext();
            str = FragmentManager.TAG;
            if (!hasNext) {
                break;
            }
            AnimationInfo animationInfo = (AnimationInfo) it.next();
            if (animationInfo.isVisibilityUnchanged()) {
                animationInfo.completeSpecialEffect();
            } else {
                AnimationOrAnimator animation = animationInfo.getAnimation(context);
                if (animation == null) {
                    animationInfo.completeSpecialEffect();
                } else {
                    final Animator animator = animation.animator;
                    if (animator == null) {
                        arrayList.add(animationInfo);
                    } else {
                        operation = animationInfo.getOperation();
                        Fragment fragment = operation.getFragment();
                        if (Boolean.TRUE.equals(map.get(operation))) {
                            if (FragmentManager.isLoggingEnabled(2)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Ignoring Animator set on ");
                                stringBuilder.append(fragment);
                                stringBuilder.append(" as this Fragment was involved in a Transition.");
                                Log.v(str, stringBuilder.toString());
                            }
                            animationInfo.completeSpecialEffect();
                        } else {
                            final boolean z2 = operation.getFinalState() == State.GONE;
                            List<Operation> list3 = list2;
                            if (z2) {
                                list3.remove(operation);
                            }
                            final View view = fragment.mView;
                            container.startViewTransition(view);
                            AnonymousClass2 anonymousClass2 = r0;
                            final ViewGroup viewGroup = container;
                            View view2 = view;
                            final AnimationInfo animationInfo2 = animationInfo;
                            AnonymousClass2 anonymousClass22 = new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    viewGroup.endViewTransition(view);
                                    if (z2) {
                                        operation.getFinalState().applyState(view);
                                    }
                                    animationInfo2.completeSpecialEffect();
                                }
                            };
                            animator.addListener(anonymousClass2);
                            animator.setTarget(view2);
                            animator.start();
                            animationInfo.getSignal().setOnCancelListener(new OnCancelListener() {
                                public void onCancel() {
                                    animator.end();
                                }
                            });
                            obj = 1;
                        }
                    }
                }
            }
            Map<Operation, Boolean> map2 = map;
        }
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            final AnimationInfo animationInfo3 = (AnimationInfo) it2.next();
            operation = animationInfo3.getOperation();
            Fragment fragment2 = operation.getFragment();
            String str2 = "Ignoring Animation set on ";
            StringBuilder stringBuilder2;
            if (z) {
                if (FragmentManager.isLoggingEnabled(2)) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str2);
                    stringBuilder2.append(fragment2);
                    stringBuilder2.append(" as Animations cannot run alongside Transitions.");
                    Log.v(str, stringBuilder2.toString());
                }
                animationInfo3.completeSpecialEffect();
            } else if (obj != null) {
                if (FragmentManager.isLoggingEnabled(2)) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str2);
                    stringBuilder2.append(fragment2);
                    stringBuilder2.append(" as Animations cannot run alongside Animators.");
                    Log.v(str, stringBuilder2.toString());
                }
                animationInfo3.completeSpecialEffect();
            } else {
                final View view3 = fragment2.mView;
                Animation animation2 = (Animation) Preconditions.checkNotNull(((AnimationOrAnimator) Preconditions.checkNotNull(animationInfo3.getAnimation(context))).animation);
                if (operation.getFinalState() == State.VISIBLE) {
                    view3.startAnimation(animation2);
                    animationInfo3.completeSpecialEffect();
                } else {
                    container.startViewTransition(view3);
                    EndViewTransitionAnimation endViewTransitionAnimation = new EndViewTransitionAnimation(animation2, container, view3);
                    endViewTransitionAnimation.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation animation) {
                            container.post(new Runnable() {
                                public void run() {
                                    AnonymousClass4 anonymousClass4 = AnonymousClass4.this;
                                    container.endViewTransition(view3);
                                    animationInfo3.completeSpecialEffect();
                                }
                            });
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    view3.startAnimation(endViewTransitionAnimation);
                }
                animationInfo3.getSignal().setOnCancelListener(new OnCancelListener() {
                    public void onCancel() {
                        view3.clearAnimation();
                        container.endViewTransition(view3);
                        animationInfo3.completeSpecialEffect();
                    }
                });
            }
        }
    }

    @NonNull
    private Map<Operation, Boolean> startTransitions(@NonNull List<TransitionInfo> list, boolean z, @Nullable Operation operation, @Nullable Operation operation2) {
        boolean z2 = z;
        Boolean bool = Boolean.TRUE;
        Boolean bool2 = Boolean.FALSE;
        HashMap hashMap = new HashMap();
        FragmentTransitionImpl fragmentTransitionImpl = null;
        for (TransitionInfo transitionInfo : list) {
            if (!transitionInfo.isVisibilityUnchanged()) {
                FragmentTransitionImpl handlingImpl = transitionInfo.getHandlingImpl();
                if (fragmentTransitionImpl == null) {
                    fragmentTransitionImpl = handlingImpl;
                } else if (handlingImpl == null) {
                    continue;
                } else if (fragmentTransitionImpl != handlingImpl) {
                    StringBuilder L = a.L("Mixing framework transitions and AndroidX transitions is not allowed. Fragment ");
                    L.append(transitionInfo.getOperation().getFragment());
                    L.append(" returned Transition ");
                    L.append(transitionInfo.getTransition());
                    L.append(" which uses a different Transition  type than other Fragments.");
                    throw new IllegalArgumentException(L.toString());
                }
            }
        }
        if (fragmentTransitionImpl == null) {
            for (TransitionInfo transitionInfo2 : list) {
                hashMap.put(transitionInfo2.getOperation(), bool2);
                transitionInfo2.completeSpecialEffect();
            }
            return hashMap;
        }
        SimpleArrayMap simpleArrayMap;
        Boolean bool3;
        Rect rect;
        FragmentTransitionImpl fragmentTransitionImpl2;
        Operation operation3;
        Boolean bool4;
        View view;
        HashMap hashMap2;
        ArrayList sharedElementTargetNames;
        Boolean bool5;
        View view2;
        View view3;
        Operation operation4;
        Operation operation5;
        Collection collection;
        Collection collection2;
        HashMap hashMap3;
        Operation operation6;
        ArrayList arrayList;
        Object obj;
        View view4 = new View(getContainer().getContext());
        Rect rect2 = new Rect();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        SimpleArrayMap arrayMap = new ArrayMap();
        Object obj2 = null;
        Operation operation7 = operation2;
        View view5 = null;
        Object obj3 = null;
        Operation operation8 = operation;
        for (TransitionInfo transitionInfo3 : list) {
            ArrayList arrayList4;
            ArrayList arrayList5;
            Operation operation9;
            if (!transitionInfo3.hasSharedElementTransition() || r1 == null || r2 == null) {
                simpleArrayMap = arrayMap;
                bool3 = bool2;
                arrayList4 = arrayList3;
                arrayList5 = arrayList2;
                rect = rect2;
                fragmentTransitionImpl2 = fragmentTransitionImpl;
                operation3 = operation;
                operation9 = operation2;
                bool4 = bool;
                view = view4;
                hashMap2 = hashMap;
                view5 = view5;
            } else {
                ArrayList arrayList6;
                SharedElementCallback enterTransitionCallback;
                SharedElementCallback exitTransitionCallback;
                int size;
                Object wrapTransitionInSet = fragmentTransitionImpl.wrapTransitionInSet(fragmentTransitionImpl.cloneTransition(transitionInfo3.getSharedElementTransition()));
                ArrayList sharedElementSourceNames = operation2.getFragment().getSharedElementSourceNames();
                ArrayList sharedElementSourceNames2 = operation.getFragment().getSharedElementSourceNames();
                ArrayList sharedElementTargetNames2 = operation.getFragment().getSharedElementTargetNames();
                Object obj4 = wrapTransitionInSet;
                View view6 = view5;
                int i = 0;
                while (i < sharedElementTargetNames2.size()) {
                    int indexOf = sharedElementSourceNames.indexOf(sharedElementTargetNames2.get(i));
                    arrayList6 = sharedElementTargetNames2;
                    if (indexOf != -1) {
                        sharedElementSourceNames.set(indexOf, sharedElementSourceNames2.get(i));
                    }
                    i++;
                    sharedElementTargetNames2 = arrayList6;
                }
                sharedElementTargetNames = operation2.getFragment().getSharedElementTargetNames();
                if (z2) {
                    enterTransitionCallback = operation.getFragment().getEnterTransitionCallback();
                    exitTransitionCallback = operation2.getFragment().getExitTransitionCallback();
                } else {
                    enterTransitionCallback = operation.getFragment().getExitTransitionCallback();
                    exitTransitionCallback = operation2.getFragment().getEnterTransitionCallback();
                }
                i = sharedElementSourceNames.size();
                bool3 = bool2;
                int i2 = 0;
                while (i2 < i) {
                    int i3 = i;
                    bool5 = bool;
                    arrayMap.put((String) sharedElementSourceNames.get(i2), (String) sharedElementTargetNames.get(i2));
                    i2++;
                    i = i3;
                    bool = bool5;
                }
                bool5 = bool;
                ArrayMap arrayMap2 = new ArrayMap();
                findNamedViews(arrayMap2, operation.getFragment().mView);
                arrayMap2.retainAll(sharedElementSourceNames);
                if (enterTransitionCallback != null) {
                    enterTransitionCallback.onMapSharedElements(sharedElementSourceNames, arrayMap2);
                    size = sharedElementSourceNames.size() - 1;
                    while (size >= 0) {
                        String str = (String) sharedElementSourceNames.get(size);
                        View view7 = (View) arrayMap2.get(str);
                        if (view7 == null) {
                            arrayMap.remove(str);
                            arrayList6 = sharedElementSourceNames;
                        } else {
                            arrayList6 = sharedElementSourceNames;
                            if (!str.equals(ViewCompat.getTransitionName(view7))) {
                                arrayMap.put(ViewCompat.getTransitionName(view7), (String) arrayMap.remove(str));
                            }
                        }
                        size--;
                        sharedElementSourceNames = arrayList6;
                    }
                    arrayList6 = sharedElementSourceNames;
                } else {
                    arrayList6 = sharedElementSourceNames;
                    arrayMap.retainAll(arrayMap2.keySet());
                }
                ArrayMap arrayMap3 = new ArrayMap();
                findNamedViews(arrayMap3, operation2.getFragment().mView);
                arrayMap3.retainAll(sharedElementTargetNames);
                arrayMap3.retainAll(arrayMap.values());
                if (exitTransitionCallback != null) {
                    exitTransitionCallback.onMapSharedElements(sharedElementTargetNames, arrayMap3);
                    for (size = sharedElementTargetNames.size() - 1; size >= 0; size--) {
                        String str2 = (String) sharedElementTargetNames.get(size);
                        View view8 = (View) arrayMap3.get(str2);
                        if (view8 == null) {
                            str2 = FragmentTransition.findKeyForValue(arrayMap, str2);
                            if (str2 != null) {
                                arrayMap.remove(str2);
                            }
                        } else if (!str2.equals(ViewCompat.getTransitionName(view8))) {
                            str2 = FragmentTransition.findKeyForValue(arrayMap, str2);
                            if (str2 != null) {
                                arrayMap.put(str2, ViewCompat.getTransitionName(view8));
                            }
                        }
                    }
                } else {
                    FragmentTransition.retainValues(arrayMap, arrayMap3);
                }
                retainMatchingViews(arrayMap2, arrayMap.keySet());
                retainMatchingViews(arrayMap3, arrayMap.values());
                if (arrayMap.isEmpty()) {
                    arrayList2.clear();
                    arrayList3.clear();
                    obj2 = null;
                    operation8 = operation;
                    operation7 = operation2;
                    simpleArrayMap = arrayMap;
                    arrayList4 = arrayList3;
                    arrayList5 = arrayList2;
                    rect = rect2;
                    view = view4;
                    fragmentTransitionImpl2 = fragmentTransitionImpl;
                    view5 = view6;
                    bool4 = bool5;
                    operation3 = operation8;
                    operation9 = operation7;
                    hashMap2 = hashMap;
                } else {
                    final View view9;
                    FragmentTransition.callSharedElementStartEnd(operation2.getFragment(), operation.getFragment(), z2, arrayMap2, true);
                    AnonymousClass6 anonymousClass6 = r0;
                    ArrayList arrayList7 = arrayList6;
                    operation7 = operation2;
                    HashMap hashMap4 = hashMap;
                    Object obj5 = obj4;
                    View view10 = view4;
                    view4 = getContainer();
                    final Operation operation10 = operation;
                    view2 = view6;
                    ArrayList arrayList8 = sharedElementTargetNames;
                    final boolean z3 = z;
                    simpleArrayMap = arrayMap;
                    final ArrayMap arrayMap4 = arrayMap3;
                    AnonymousClass6 anonymousClass62 = new Runnable() {
                        public void run() {
                            FragmentTransition.callSharedElementStartEnd(operation7.getFragment(), operation10.getFragment(), z3, arrayMap4, false);
                        }
                    };
                    OneShotPreDrawListener.add(view4, anonymousClass6);
                    for (View view32 : arrayMap2.values()) {
                        captureTransitioningViews(arrayList2, view32);
                    }
                    if (arrayList7.isEmpty()) {
                        view5 = view2;
                    } else {
                        view5 = (View) arrayMap2.get((String) arrayList7.get(0));
                        fragmentTransitionImpl.setEpicenter(obj5, view5);
                    }
                    for (View view322 : arrayMap3.values()) {
                        captureTransitioningViews(arrayList3, view322);
                    }
                    if (!arrayList8.isEmpty()) {
                        view9 = (View) arrayMap3.get((String) arrayList8.get(0));
                        if (view9 != null) {
                            OneShotPreDrawListener.add(getContainer(), new Runnable() {
                                public void run() {
                                    fragmentTransitionImpl.getBoundsOnScreen(view9, rect2);
                                }
                            });
                            obj3 = 1;
                        }
                    }
                    view9 = view10;
                    fragmentTransitionImpl.setSharedElementTargets(obj5, view9, arrayList2);
                    arrayList4 = arrayList3;
                    arrayList5 = arrayList2;
                    rect = rect2;
                    view = view9;
                    fragmentTransitionImpl2 = fragmentTransitionImpl;
                    fragmentTransitionImpl.scheduleRemoveTargets(obj5, null, null, null, null, obj5, arrayList4);
                    operation3 = operation;
                    bool4 = bool5;
                    hashMap2 = hashMap4;
                    hashMap2.put(operation3, bool4);
                    operation9 = operation2;
                    hashMap2.put(operation9, bool4);
                    obj2 = obj5;
                    operation8 = operation3;
                    operation7 = operation9;
                }
            }
            arrayList3 = arrayList4;
            arrayList2 = arrayList5;
            rect2 = rect;
            hashMap = hashMap2;
            arrayMap = simpleArrayMap;
            z2 = z;
            view4 = view;
            bool = bool4;
            fragmentTransitionImpl = fragmentTransitionImpl2;
            bool2 = bool3;
        }
        view2 = view5;
        simpleArrayMap = arrayMap;
        bool3 = bool2;
        Collection collection3 = arrayList3;
        Collection collection4 = arrayList2;
        rect = rect2;
        fragmentTransitionImpl2 = fragmentTransitionImpl;
        bool4 = bool;
        view = view4;
        hashMap2 = hashMap;
        sharedElementTargetNames = new ArrayList();
        Iterator it = list.iterator();
        Object obj6 = null;
        Object obj7 = null;
        while (it.hasNext()) {
            TransitionInfo transitionInfo4 = (TransitionInfo) it.next();
            if (transitionInfo4.isVisibilityUnchanged()) {
                bool5 = bool4;
                hashMap2.put(transitionInfo4.getOperation(), bool3);
                transitionInfo4.completeSpecialEffect();
                bool4 = bool5;
            } else {
                View view11;
                Boolean bool6;
                bool5 = bool4;
                bool4 = bool3;
                Object cloneTransition = fragmentTransitionImpl2.cloneTransition(transitionInfo4.getTransition());
                Iterator it2 = it;
                operation4 = transitionInfo4.getOperation();
                Object obj8 = (obj2 == null || !(operation4 == operation8 || operation4 == operation7)) ? null : 1;
                if (cloneTransition == null) {
                    if (obj8 == null) {
                        hashMap2.put(operation4, bool4);
                        transitionInfo4.completeSpecialEffect();
                    }
                    operation5 = operation7;
                    collection = collection3;
                    collection2 = collection4;
                    view11 = view;
                    hashMap3 = hashMap2;
                    bool3 = bool4;
                    bool6 = bool5;
                    operation6 = operation8;
                    view322 = view2;
                } else {
                    Object obj9;
                    Collection collection5;
                    bool3 = bool4;
                    arrayList = new ArrayList();
                    Object obj10 = obj6;
                    captureTransitioningViews(arrayList, operation4.getFragment().mView);
                    if (obj8 != null) {
                        if (operation4 == operation8) {
                            arrayList.removeAll(collection4);
                        } else {
                            arrayList.removeAll(collection3);
                        }
                    }
                    if (arrayList.isEmpty()) {
                        fragmentTransitionImpl2.addTarget(cloneTransition, view);
                        operation5 = operation7;
                        collection = collection3;
                        collection2 = collection4;
                        view11 = view;
                        obj6 = cloneTransition;
                        obj9 = obj7;
                        hashMap3 = hashMap2;
                        bool6 = bool5;
                        obj = obj10;
                        operation6 = operation8;
                        collection5 = arrayList;
                    } else {
                        fragmentTransitionImpl2.addTargets(cloneTransition, arrayList);
                        obj6 = cloneTransition;
                        view11 = view;
                        obj = obj10;
                        obj10 = obj6;
                        collection2 = collection4;
                        obj9 = obj7;
                        collection = collection3;
                        hashMap3 = hashMap2;
                        operation5 = operation7;
                        bool6 = bool5;
                        operation6 = operation8;
                        collection5 = arrayList;
                        fragmentTransitionImpl2.scheduleRemoveTargets(obj6, obj10, arrayList, null, null, null, null);
                        if (operation4.getFinalState() == State.GONE) {
                            obj6 = obj10;
                            fragmentTransitionImpl2.scheduleHideFragmentView(obj6, operation4.getFragment().mView, collection5);
                            OneShotPreDrawListener.add(getContainer(), new Runnable() {
                                public void run() {
                                    FragmentTransition.setViewVisibility(collection5, 4);
                                }
                            });
                        } else {
                            obj6 = obj10;
                        }
                    }
                    if (operation4.getFinalState() == State.VISIBLE) {
                        sharedElementTargetNames.addAll(collection5);
                        if (obj3 != null) {
                            fragmentTransitionImpl2.setEpicenter(obj6, rect);
                        }
                        view322 = view2;
                    } else {
                        view322 = view2;
                        fragmentTransitionImpl2.setEpicenter(obj6, view322);
                    }
                    hashMap3.put(operation4, bool6);
                    if (transitionInfo4.isOverlapAllowed()) {
                        obj7 = fragmentTransitionImpl2.mergeTransitionsTogether(obj9, obj6, null);
                        obj6 = obj;
                    } else {
                        obj6 = fragmentTransitionImpl2.mergeTransitionsTogether(obj, obj6, null);
                        obj7 = obj9;
                    }
                }
                it = it2;
                view2 = view322;
                bool4 = bool6;
                hashMap2 = hashMap3;
                operation8 = operation6;
                view = view11;
                collection4 = collection2;
                collection3 = collection;
                operation7 = operation5;
            }
        }
        operation6 = operation8;
        operation5 = operation7;
        collection = collection3;
        collection2 = collection4;
        hashMap3 = hashMap2;
        Object mergeTransitionsInSequence = fragmentTransitionImpl2.mergeTransitionsInSequence(obj7, obj6, obj2);
        for (final TransitionInfo transitionInfo5 : list) {
            if (!transitionInfo5.isVisibilityUnchanged()) {
                Object transition = transitionInfo5.getTransition();
                Operation operation11 = transitionInfo5.getOperation();
                operation4 = operation6;
                operation3 = operation5;
                obj = (obj2 == null || !(operation11 == operation4 || operation11 == operation3)) ? null : 1;
                if (!(transition == null && obj == null)) {
                    fragmentTransitionImpl2.setListenerForTransitionEnd(transitionInfo5.getOperation().getFragment(), mergeTransitionsInSequence, transitionInfo5.getSignal(), new Runnable() {
                        public void run() {
                            transitionInfo5.completeSpecialEffect();
                        }
                    });
                }
                operation6 = operation4;
                operation5 = operation3;
            }
        }
        FragmentTransition.setViewVisibility(sharedElementTargetNames, 4);
        Collection collection6 = collection;
        arrayList = fragmentTransitionImpl2.prepareSetNameOverridesReordered(collection6);
        fragmentTransitionImpl2.beginDelayedTransition(getContainer(), mergeTransitionsInSequence);
        fragmentTransitionImpl2.setNameOverridesReordered(getContainer(), collection2, collection6, arrayList, simpleArrayMap);
        FragmentTransition.setViewVisibility(sharedElementTargetNames, 0);
        fragmentTransitionImpl2.swapSharedElementTargets(obj2, collection2, collection6);
        return hashMap3;
    }

    public void applyContainerChanges(@NonNull Operation operation) {
        operation.getFinalState().applyState(operation.getFragment().mView);
    }

    public void captureTransitioningViews(ArrayList<View> arrayList, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (ViewGroupCompat.isTransitionGroup(viewGroup)) {
                arrayList.add(viewGroup);
                return;
            }
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    captureTransitioningViews(arrayList, childAt);
                }
            }
            return;
        }
        arrayList.add(view);
    }

    public void executeOperations(@NonNull List<Operation> list, boolean z) {
        Operation operation = null;
        Operation operation2 = null;
        for (Operation operation3 : list) {
            State from = State.from(operation3.getFragment().mView);
            int ordinal = operation3.getFinalState().ordinal();
            if (ordinal != 0) {
                if (ordinal != 1) {
                    if (!(ordinal == 2 || ordinal == 3)) {
                    }
                } else if (from != State.VISIBLE) {
                    operation2 = operation3;
                }
            }
            if (from == State.VISIBLE && operation == null) {
                operation = operation3;
            }
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList(list);
        for (final Operation operation4 : list) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            operation4.markStartedSpecialEffect(cancellationSignal);
            arrayList.add(new AnimationInfo(operation4, cancellationSignal));
            cancellationSignal = new CancellationSignal();
            operation4.markStartedSpecialEffect(cancellationSignal);
            boolean z2 = false;
            if (z) {
                if (operation4 != operation) {
                    arrayList2.add(new TransitionInfo(operation4, cancellationSignal, z, z2));
                    operation4.addCompletionListener(new Runnable() {
                        public void run() {
                            if (arrayList3.contains(operation4)) {
                                arrayList3.remove(operation4);
                                DefaultSpecialEffectsController.this.applyContainerChanges(operation4);
                            }
                        }
                    });
                }
            } else if (operation4 != operation2) {
                arrayList2.add(new TransitionInfo(operation4, cancellationSignal, z, z2));
                operation4.addCompletionListener(/* anonymous class already generated */);
            }
            z2 = true;
            arrayList2.add(new TransitionInfo(operation4, cancellationSignal, z, z2));
            operation4.addCompletionListener(/* anonymous class already generated */);
        }
        Map startTransitions = startTransitions(arrayList2, z, operation, operation2);
        startAnimations(arrayList, arrayList3, startTransitions.containsValue(Boolean.TRUE), startTransitions);
        Iterator it = arrayList3.iterator();
        while (it.hasNext()) {
            applyContainerChanges((Operation) it.next());
        }
        arrayList3.clear();
    }

    public void findNamedViews(Map<String, View> map, @NonNull View view) {
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            map.put(transitionName, view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    findNamedViews(map, childAt);
                }
            }
        }
    }

    public void retainMatchingViews(@NonNull ArrayMap<String, View> arrayMap, @NonNull Collection<String> collection) {
        Iterator it = arrayMap.entrySet().iterator();
        while (it.hasNext()) {
            if (!collection.contains(ViewCompat.getTransitionName((View) ((Entry) it.next()).getValue()))) {
                it.remove();
            }
        }
    }
}
