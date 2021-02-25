package androidx.fragment.app;

import android.content.Context;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.app.SharedElementCallback;
import androidx.core.os.CancellationSignal;
import androidx.core.view.OneShotPreDrawListener;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction.Op;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FragmentTransition {
    private static final int[] INVERSE_OPS = new int[]{0, 3, 0, 1, 5, 4, 7, 6, 9, 8, 10};
    public static final FragmentTransitionImpl PLATFORM_IMPL = new FragmentTransitionCompat21();
    public static final FragmentTransitionImpl SUPPORT_IMPL = resolveSupportImpl();

    public interface Callback {
        void onComplete(@NonNull Fragment fragment, @NonNull CancellationSignal cancellationSignal);

        void onStart(@NonNull Fragment fragment, @NonNull CancellationSignal cancellationSignal);
    }

    public static class FragmentContainerTransition {
        public Fragment firstOut;
        public boolean firstOutIsPop;
        public BackStackRecord firstOutTransaction;
        public Fragment lastIn;
        public boolean lastInIsPop;
        public BackStackRecord lastInTransaction;
    }

    private FragmentTransition() {
    }

    private static void addSharedElementsWithMatchingNames(ArrayList<View> arrayList, ArrayMap<String, View> arrayMap, Collection<String> collection) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            View view = (View) arrayMap.valueAt(size);
            if (collection.contains(ViewCompat.getTransitionName(view))) {
                arrayList.add(view);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00b5  */
    /* JADX WARNING: Missing block: B:29:0x0039, code skipped:
            if (r0.mAdded != false) goto L_0x008c;
     */
    /* JADX WARNING: Missing block: B:50:0x006c, code skipped:
            if (r0.mPostponedAlpha >= 0.0f) goto L_0x006e;
     */
    /* JADX WARNING: Missing block: B:51:0x006e, code skipped:
            r9 = 1;
     */
    /* JADX WARNING: Missing block: B:56:0x0078, code skipped:
            if (r0.mHidden == false) goto L_0x006e;
     */
    /* JADX WARNING: Missing block: B:63:0x008a, code skipped:
            if (r0.mHidden == false) goto L_0x008c;
     */
    /* JADX WARNING: Missing block: B:64:0x008c, code skipped:
            r9 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void addToFirstInLastOut(BackStackRecord backStackRecord, Op op, SparseArray<FragmentContainerTransition> sparseArray, boolean z, boolean z2) {
        Fragment fragment = op.mFragment;
        if (fragment != null) {
            int i = fragment.mContainerId;
            if (i != 0) {
                Object obj;
                Object obj2;
                FragmentContainerTransition fragmentContainerTransition;
                boolean z3;
                int i2 = z ? INVERSE_OPS[op.mCmd] : op.mCmd;
                boolean z4 = false;
                Object obj3 = 1;
                if (i2 != 1) {
                    if (i2 != 3) {
                        if (i2 != 4) {
                            if (i2 != 5) {
                                if (i2 != 6) {
                                    if (i2 != 7) {
                                        obj = null;
                                        obj3 = null;
                                        obj2 = null;
                                        fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                                        if (z4) {
                                            fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                                            fragmentContainerTransition.lastIn = fragment;
                                            fragmentContainerTransition.lastInIsPop = z;
                                            fragmentContainerTransition.lastInTransaction = backStackRecord;
                                        }
                                        if (!(z2 || r3 == null)) {
                                            if (fragmentContainerTransition != null && fragmentContainerTransition.firstOut == fragment) {
                                                fragmentContainerTransition.firstOut = null;
                                            }
                                            if (!backStackRecord.mReorderingAllowed) {
                                                FragmentManager fragmentManager = backStackRecord.mManager;
                                                fragmentManager.getFragmentStore().makeActive(fragmentManager.createOrGetFragmentStateManager(fragment));
                                                fragmentManager.moveToState(fragment);
                                            }
                                        }
                                        if (obj2 != null && (fragmentContainerTransition == null || fragmentContainerTransition.firstOut == null)) {
                                            fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                                            fragmentContainerTransition.firstOut = fragment;
                                            fragmentContainerTransition.firstOutIsPop = z;
                                            fragmentContainerTransition.firstOutTransaction = backStackRecord;
                                        }
                                        if (!(z2 || r9 == null || fragmentContainerTransition == null || fragmentContainerTransition.lastIn != fragment)) {
                                            fragmentContainerTransition.lastIn = null;
                                        }
                                    }
                                }
                            } else if (z2) {
                                if (fragment.mHiddenChanged) {
                                    if (!fragment.mHidden) {
                                    }
                                }
                                z3 = false;
                                z4 = z3;
                                obj = null;
                                obj2 = null;
                                fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                                if (z4) {
                                }
                                fragmentContainerTransition.firstOut = null;
                                if (backStackRecord.mReorderingAllowed) {
                                }
                                fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                                fragmentContainerTransition.firstOut = fragment;
                                fragmentContainerTransition.firstOutIsPop = z;
                                fragmentContainerTransition.firstOutTransaction = backStackRecord;
                                fragmentContainerTransition.lastIn = null;
                            } else {
                                z3 = fragment.mHidden;
                                z4 = z3;
                                obj = null;
                                obj2 = null;
                                fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                                if (z4) {
                                }
                                fragmentContainerTransition.firstOut = null;
                                if (backStackRecord.mReorderingAllowed) {
                                }
                                fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                                fragmentContainerTransition.firstOut = fragment;
                                fragmentContainerTransition.firstOutIsPop = z;
                                fragmentContainerTransition.firstOutTransaction = backStackRecord;
                                fragmentContainerTransition.lastIn = null;
                            }
                        } else if (z2) {
                            obj = null;
                            obj2 = obj;
                            obj = 1;
                            obj3 = null;
                            fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                            if (z4) {
                            }
                            fragmentContainerTransition.firstOut = null;
                            if (backStackRecord.mReorderingAllowed) {
                            }
                            fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                            fragmentContainerTransition.firstOut = fragment;
                            fragmentContainerTransition.firstOutIsPop = z;
                            fragmentContainerTransition.firstOutTransaction = backStackRecord;
                            fragmentContainerTransition.lastIn = null;
                        } else {
                            obj = null;
                            obj2 = obj;
                            obj = 1;
                            obj3 = null;
                            fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                            if (z4) {
                            }
                            fragmentContainerTransition.firstOut = null;
                            if (backStackRecord.mReorderingAllowed) {
                            }
                            fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                            fragmentContainerTransition.firstOut = fragment;
                            fragmentContainerTransition.firstOutIsPop = z;
                            fragmentContainerTransition.firstOutTransaction = backStackRecord;
                            fragmentContainerTransition.lastIn = null;
                        }
                    }
                    if (z2) {
                        if (!fragment.mAdded) {
                            View view = fragment.mView;
                            if (view != null) {
                                if (view.getVisibility() == 0) {
                                }
                            }
                        }
                        obj = null;
                        obj2 = obj;
                        obj = 1;
                        obj3 = null;
                        fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                        if (z4) {
                        }
                        fragmentContainerTransition.firstOut = null;
                        if (backStackRecord.mReorderingAllowed) {
                        }
                        fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                        fragmentContainerTransition.firstOut = fragment;
                        fragmentContainerTransition.firstOutIsPop = z;
                        fragmentContainerTransition.firstOutTransaction = backStackRecord;
                        fragmentContainerTransition.lastIn = null;
                    }
                    if (fragment.mAdded) {
                    }
                    obj = null;
                    obj2 = obj;
                    obj = 1;
                    obj3 = null;
                    fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                    if (z4) {
                    }
                    fragmentContainerTransition.firstOut = null;
                    if (backStackRecord.mReorderingAllowed) {
                    }
                    fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                    fragmentContainerTransition.firstOut = fragment;
                    fragmentContainerTransition.firstOutIsPop = z;
                    fragmentContainerTransition.firstOutTransaction = backStackRecord;
                    fragmentContainerTransition.lastIn = null;
                }
                if (z2) {
                    z3 = fragment.mIsNewlyAdded;
                    z4 = z3;
                    obj = null;
                    obj2 = null;
                    fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                    if (z4) {
                    }
                    fragmentContainerTransition.firstOut = null;
                    if (backStackRecord.mReorderingAllowed) {
                    }
                    fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                    fragmentContainerTransition.firstOut = fragment;
                    fragmentContainerTransition.firstOutIsPop = z;
                    fragmentContainerTransition.firstOutTransaction = backStackRecord;
                    fragmentContainerTransition.lastIn = null;
                }
                if (!fragment.mAdded) {
                }
                z3 = false;
                z4 = z3;
                obj = null;
                obj2 = null;
                fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                if (z4) {
                }
                fragmentContainerTransition.firstOut = null;
                if (backStackRecord.mReorderingAllowed) {
                }
                fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray, i);
                fragmentContainerTransition.firstOut = fragment;
                fragmentContainerTransition.firstOutIsPop = z;
                fragmentContainerTransition.firstOutTransaction = backStackRecord;
                fragmentContainerTransition.lastIn = null;
            }
        }
    }

    public static void calculateFragments(BackStackRecord backStackRecord, SparseArray<FragmentContainerTransition> sparseArray, boolean z) {
        int size = backStackRecord.mOps.size();
        for (int i = 0; i < size; i++) {
            addToFirstInLastOut(backStackRecord, (Op) backStackRecord.mOps.get(i), sparseArray, false, z);
        }
    }

    private static ArrayMap<String, String> calculateNameOverrides(int i, ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i2, int i3) {
        ArrayMap arrayMap = new ArrayMap();
        for (i3--; i3 >= i2; i3--) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i3);
            if (backStackRecord.interactsWith(i)) {
                boolean booleanValue = ((Boolean) arrayList2.get(i3)).booleanValue();
                ArrayList arrayList3 = backStackRecord.mSharedElementSourceNames;
                if (arrayList3 != null) {
                    ArrayList arrayList4;
                    ArrayList arrayList5;
                    int size = arrayList3.size();
                    if (booleanValue) {
                        arrayList4 = backStackRecord.mSharedElementSourceNames;
                        arrayList5 = backStackRecord.mSharedElementTargetNames;
                    } else {
                        ArrayList arrayList6 = backStackRecord.mSharedElementSourceNames;
                        arrayList4 = backStackRecord.mSharedElementTargetNames;
                        arrayList5 = arrayList6;
                    }
                    for (int i4 = 0; i4 < size; i4++) {
                        String str = (String) arrayList5.get(i4);
                        String str2 = (String) arrayList4.get(i4);
                        String str3 = (String) arrayMap.remove(str2);
                        if (str3 != null) {
                            arrayMap.put(str, str3);
                        } else {
                            arrayMap.put(str, str2);
                        }
                    }
                }
            }
        }
        return arrayMap;
    }

    public static void calculatePopFragments(BackStackRecord backStackRecord, SparseArray<FragmentContainerTransition> sparseArray, boolean z) {
        if (backStackRecord.mManager.getContainer().onHasView()) {
            for (int size = backStackRecord.mOps.size() - 1; size >= 0; size--) {
                addToFirstInLastOut(backStackRecord, (Op) backStackRecord.mOps.get(size), sparseArray, true, z);
            }
        }
    }

    public static void callSharedElementStartEnd(Fragment fragment, Fragment fragment2, boolean z, ArrayMap<String, View> arrayMap, boolean z2) {
        SharedElementCallback enterTransitionCallback;
        if (z) {
            enterTransitionCallback = fragment2.getEnterTransitionCallback();
        } else {
            enterTransitionCallback = fragment.getEnterTransitionCallback();
        }
        if (enterTransitionCallback != null) {
            int i;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i2 = 0;
            if (arrayMap == null) {
                i = 0;
            } else {
                i = arrayMap.size();
            }
            while (i2 < i) {
                arrayList2.add(arrayMap.keyAt(i2));
                arrayList.add(arrayMap.valueAt(i2));
                i2++;
            }
            if (z2) {
                enterTransitionCallback.onSharedElementStart(arrayList2, arrayList, null);
            } else {
                enterTransitionCallback.onSharedElementEnd(arrayList2, arrayList, null);
            }
        }
    }

    private static boolean canHandleAll(FragmentTransitionImpl fragmentTransitionImpl, List<Object> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (!fragmentTransitionImpl.canHandle(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static ArrayMap<String, View> captureInSharedElements(FragmentTransitionImpl fragmentTransitionImpl, ArrayMap<String, String> arrayMap, Object obj, FragmentContainerTransition fragmentContainerTransition) {
        Fragment fragment = fragmentContainerTransition.lastIn;
        View view = fragment.getView();
        if (arrayMap.isEmpty() || obj == null || view == null) {
            arrayMap.clear();
            return null;
        }
        SharedElementCallback exitTransitionCallback;
        Collection collection;
        ArrayMap arrayMap2 = new ArrayMap();
        fragmentTransitionImpl.findNamedViews(arrayMap2, view);
        BackStackRecord backStackRecord = fragmentContainerTransition.lastInTransaction;
        if (fragmentContainerTransition.lastInIsPop) {
            exitTransitionCallback = fragment.getExitTransitionCallback();
            collection = backStackRecord.mSharedElementSourceNames;
        } else {
            exitTransitionCallback = fragment.getEnterTransitionCallback();
            collection = backStackRecord.mSharedElementTargetNames;
        }
        if (collection != null) {
            arrayMap2.retainAll(collection);
            arrayMap2.retainAll(arrayMap.values());
        }
        if (exitTransitionCallback != null) {
            exitTransitionCallback.onMapSharedElements(collection, arrayMap2);
            for (int size = collection.size() - 1; size >= 0; size--) {
                String str = (String) collection.get(size);
                view = (View) arrayMap2.get(str);
                if (view == null) {
                    str = findKeyForValue(arrayMap, str);
                    if (str != null) {
                        arrayMap.remove(str);
                    }
                } else if (!str.equals(ViewCompat.getTransitionName(view))) {
                    str = findKeyForValue(arrayMap, str);
                    if (str != null) {
                        arrayMap.put(str, ViewCompat.getTransitionName(view));
                    }
                }
            }
        } else {
            retainValues(arrayMap, arrayMap2);
        }
        return arrayMap2;
    }

    private static ArrayMap<String, View> captureOutSharedElements(FragmentTransitionImpl fragmentTransitionImpl, ArrayMap<String, String> arrayMap, Object obj, FragmentContainerTransition fragmentContainerTransition) {
        if (arrayMap.isEmpty() || obj == null) {
            arrayMap.clear();
            return null;
        }
        SharedElementCallback enterTransitionCallback;
        Collection collection;
        Fragment fragment = fragmentContainerTransition.firstOut;
        ArrayMap arrayMap2 = new ArrayMap();
        fragmentTransitionImpl.findNamedViews(arrayMap2, fragment.requireView());
        BackStackRecord backStackRecord = fragmentContainerTransition.firstOutTransaction;
        if (fragmentContainerTransition.firstOutIsPop) {
            enterTransitionCallback = fragment.getEnterTransitionCallback();
            collection = backStackRecord.mSharedElementTargetNames;
        } else {
            enterTransitionCallback = fragment.getExitTransitionCallback();
            collection = backStackRecord.mSharedElementSourceNames;
        }
        if (collection != null) {
            arrayMap2.retainAll(collection);
        }
        if (enterTransitionCallback != null) {
            enterTransitionCallback.onMapSharedElements(collection, arrayMap2);
            for (int size = collection.size() - 1; size >= 0; size--) {
                String str = (String) collection.get(size);
                View view = (View) arrayMap2.get(str);
                if (view == null) {
                    arrayMap.remove(str);
                } else if (!str.equals(ViewCompat.getTransitionName(view))) {
                    arrayMap.put(ViewCompat.getTransitionName(view), (String) arrayMap.remove(str));
                }
            }
        } else {
            arrayMap.retainAll(arrayMap2.keySet());
        }
        return arrayMap2;
    }

    private static FragmentTransitionImpl chooseImpl(Fragment fragment, Fragment fragment2) {
        Object sharedElementReturnTransition;
        ArrayList arrayList = new ArrayList();
        if (fragment != null) {
            Object exitTransition = fragment.getExitTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
            exitTransition = fragment.getReturnTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
            sharedElementReturnTransition = fragment.getSharedElementReturnTransition();
            if (sharedElementReturnTransition != null) {
                arrayList.add(sharedElementReturnTransition);
            }
        }
        if (fragment2 != null) {
            sharedElementReturnTransition = fragment2.getEnterTransition();
            if (sharedElementReturnTransition != null) {
                arrayList.add(sharedElementReturnTransition);
            }
            sharedElementReturnTransition = fragment2.getReenterTransition();
            if (sharedElementReturnTransition != null) {
                arrayList.add(sharedElementReturnTransition);
            }
            sharedElementReturnTransition = fragment2.getSharedElementEnterTransition();
            if (sharedElementReturnTransition != null) {
                arrayList.add(sharedElementReturnTransition);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        FragmentTransitionImpl fragmentTransitionImpl = PLATFORM_IMPL;
        if (fragmentTransitionImpl != null && canHandleAll(fragmentTransitionImpl, arrayList)) {
            return fragmentTransitionImpl;
        }
        FragmentTransitionImpl fragmentTransitionImpl2 = SUPPORT_IMPL;
        if (fragmentTransitionImpl2 != null && canHandleAll(fragmentTransitionImpl2, arrayList)) {
            return fragmentTransitionImpl2;
        }
        if (fragmentTransitionImpl == null && fragmentTransitionImpl2 == null) {
            return null;
        }
        throw new IllegalArgumentException("Invalid Transition types");
    }

    public static ArrayList<View> configureEnteringExitingViews(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Fragment fragment, ArrayList<View> arrayList, View view) {
        if (obj == null) {
            return null;
        }
        ArrayList<View> arrayList2 = new ArrayList();
        View view2 = fragment.getView();
        if (view2 != null) {
            fragmentTransitionImpl.captureTransitioningViews(arrayList2, view2);
        }
        if (arrayList != null) {
            arrayList2.removeAll(arrayList);
        }
        if (arrayList2.isEmpty()) {
            return arrayList2;
        }
        arrayList2.add(view);
        fragmentTransitionImpl.addTargets(obj, arrayList2);
        return arrayList2;
    }

    private static Object configureSharedElementsOrdered(FragmentTransitionImpl fragmentTransitionImpl, ViewGroup viewGroup, View view, ArrayMap<String, String> arrayMap, FragmentContainerTransition fragmentContainerTransition, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
        FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        ArrayList<View> arrayList3 = arrayList;
        Object obj3 = obj;
        Fragment fragment = fragmentContainerTransition2.lastIn;
        Fragment fragment2 = fragmentContainerTransition2.firstOut;
        if (fragment == null || fragment2 == null) {
            return null;
        }
        ArrayMap arrayMap2;
        Object obj4;
        Object obj5;
        boolean z = fragmentContainerTransition2.lastInIsPop;
        if (arrayMap.isEmpty()) {
            arrayMap2 = arrayMap;
            obj4 = null;
        } else {
            obj4 = getSharedElementTransition(fragmentTransitionImpl2, fragment, fragment2, z);
            arrayMap2 = arrayMap;
        }
        ArrayMap captureOutSharedElements = captureOutSharedElements(fragmentTransitionImpl2, arrayMap2, obj4, fragmentContainerTransition2);
        if (arrayMap.isEmpty()) {
            obj5 = null;
        } else {
            arrayList3.addAll(captureOutSharedElements.values());
            obj5 = obj4;
        }
        if (obj3 == null && obj2 == null && obj5 == null) {
            return null;
        }
        Rect rect;
        callSharedElementStartEnd(fragment, fragment2, z, captureOutSharedElements, true);
        if (obj5 != null) {
            rect = new Rect();
            fragmentTransitionImpl2.setSharedElementTargets(obj5, view, arrayList3);
            setOutEpicenter(fragmentTransitionImpl, obj5, obj2, captureOutSharedElements, fragmentContainerTransition2.firstOutIsPop, fragmentContainerTransition2.firstOutTransaction);
            if (obj3 != null) {
                fragmentTransitionImpl2.setEpicenter(obj3, rect);
            }
        } else {
            rect = null;
        }
        final FragmentTransitionImpl fragmentTransitionImpl3 = fragmentTransitionImpl;
        final ArrayMap<String, String> arrayMap3 = arrayMap;
        final Object obj6 = obj5;
        final FragmentContainerTransition fragmentContainerTransition3 = fragmentContainerTransition;
        AnonymousClass6 anonymousClass6 = r0;
        final ArrayList<View> arrayList4 = arrayList2;
        final View view2 = view;
        final Fragment fragment3 = fragment;
        fragment = fragment2;
        final boolean z2 = z;
        arrayList3 = arrayList;
        obj3 = obj;
        final Rect rect2 = rect;
        AnonymousClass6 anonymousClass62 = new Runnable() {
            public void run() {
                ArrayMap captureInSharedElements = FragmentTransition.captureInSharedElements(fragmentTransitionImpl3, arrayMap3, obj6, fragmentContainerTransition3);
                if (captureInSharedElements != null) {
                    arrayList4.addAll(captureInSharedElements.values());
                    arrayList4.add(view2);
                }
                FragmentTransition.callSharedElementStartEnd(fragment3, fragment, z2, captureInSharedElements, false);
                Object obj = obj6;
                if (obj != null) {
                    fragmentTransitionImpl3.swapSharedElementTargets(obj, arrayList3, arrayList4);
                    View inEpicenterView = FragmentTransition.getInEpicenterView(captureInSharedElements, fragmentContainerTransition3, obj3, z2);
                    if (inEpicenterView != null) {
                        fragmentTransitionImpl3.getBoundsOnScreen(inEpicenterView, rect2);
                    }
                }
            }
        };
        OneShotPreDrawListener.add(viewGroup, anonymousClass6);
        return obj5;
    }

    private static Object configureSharedElementsReordered(FragmentTransitionImpl fragmentTransitionImpl, ViewGroup viewGroup, View view, ArrayMap<String, String> arrayMap, FragmentContainerTransition fragmentContainerTransition, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
        View view2 = view;
        ArrayMap<String, String> arrayMap2 = arrayMap;
        FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        ArrayList<View> arrayList3 = arrayList;
        ArrayList<View> arrayList4 = arrayList2;
        Object obj3 = obj;
        Fragment fragment = fragmentContainerTransition2.lastIn;
        Fragment fragment2 = fragmentContainerTransition2.firstOut;
        if (fragment != null) {
            fragment.requireView().setVisibility(0);
        }
        if (fragment == null || fragment2 == null) {
            return null;
        }
        Object obj4;
        Object obj5;
        boolean z = fragmentContainerTransition2.lastInIsPop;
        if (arrayMap.isEmpty()) {
            obj4 = null;
        } else {
            obj4 = getSharedElementTransition(fragmentTransitionImpl, fragment, fragment2, z);
        }
        ArrayMap captureOutSharedElements = captureOutSharedElements(fragmentTransitionImpl, arrayMap2, obj4, fragmentContainerTransition2);
        ArrayMap captureInSharedElements = captureInSharedElements(fragmentTransitionImpl, arrayMap2, obj4, fragmentContainerTransition2);
        if (arrayMap.isEmpty()) {
            if (captureOutSharedElements != null) {
                captureOutSharedElements.clear();
            }
            if (captureInSharedElements != null) {
                captureInSharedElements.clear();
            }
            obj5 = null;
        } else {
            addSharedElementsWithMatchingNames(arrayList3, captureOutSharedElements, arrayMap.keySet());
            addSharedElementsWithMatchingNames(arrayList4, captureInSharedElements, arrayMap.values());
            obj5 = obj4;
        }
        if (obj3 == null && obj2 == null && obj5 == null) {
            return null;
        }
        Rect rect;
        View view3;
        callSharedElementStartEnd(fragment, fragment2, z, captureOutSharedElements, true);
        if (obj5 != null) {
            arrayList4.add(view2);
            fragmentTransitionImpl.setSharedElementTargets(obj5, view2, arrayList3);
            setOutEpicenter(fragmentTransitionImpl, obj5, obj2, captureOutSharedElements, fragmentContainerTransition2.firstOutIsPop, fragmentContainerTransition2.firstOutTransaction);
            Rect rect2 = new Rect();
            View inEpicenterView = getInEpicenterView(captureInSharedElements, fragmentContainerTransition2, obj3, z);
            if (inEpicenterView != null) {
                fragmentTransitionImpl.setEpicenter(obj3, rect2);
            }
            rect = rect2;
            view3 = inEpicenterView;
        } else {
            view3 = null;
            rect = view3;
        }
        final Fragment fragment3 = fragment;
        final Fragment fragment4 = fragment2;
        final boolean z2 = z;
        final ArrayMap arrayMap3 = captureInSharedElements;
        fragmentTransitionImpl2 = fragmentTransitionImpl;
        OneShotPreDrawListener.add(viewGroup, new Runnable() {
            public void run() {
                FragmentTransition.callSharedElementStartEnd(fragment3, fragment4, z2, arrayMap3, false);
                View view = view3;
                if (view != null) {
                    fragmentTransitionImpl2.getBoundsOnScreen(view, rect);
                }
            }
        });
        return obj5;
    }

    private static void configureTransitionsOrdered(@NonNull ViewGroup viewGroup, FragmentContainerTransition fragmentContainerTransition, View view, ArrayMap<String, String> arrayMap, Callback callback) {
        ViewGroup viewGroup2 = viewGroup;
        FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        View view2 = view;
        ArrayMap<String, String> arrayMap2 = arrayMap;
        final Callback callback2 = callback;
        Fragment fragment = fragmentContainerTransition2.lastIn;
        final Fragment fragment2 = fragmentContainerTransition2.firstOut;
        FragmentTransitionImpl chooseImpl = chooseImpl(fragment2, fragment);
        if (chooseImpl != null) {
            Object obj;
            boolean z = fragmentContainerTransition2.lastInIsPop;
            boolean z2 = fragmentContainerTransition2.firstOutIsPop;
            Object enterTransition = getEnterTransition(chooseImpl, fragment, z);
            Object exitTransition = getExitTransition(chooseImpl, fragment2, z2);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = arrayList;
            Object obj2 = exitTransition;
            Object obj3 = enterTransition;
            FragmentTransitionImpl fragmentTransitionImpl = chooseImpl;
            exitTransition = configureSharedElementsOrdered(chooseImpl, viewGroup, view, arrayMap, fragmentContainerTransition, arrayList, arrayList2, enterTransition, obj2);
            Object obj4 = obj3;
            if (obj4 == null && exitTransition == null) {
                obj = obj2;
                if (obj == null) {
                    return;
                }
            }
            obj = obj2;
            ArrayList arrayList4 = arrayList3;
            arrayList3 = configureEnteringExitingViews(fragmentTransitionImpl, obj, fragment2, arrayList4, view2);
            if (arrayList3 == null || arrayList3.isEmpty()) {
                obj = null;
            }
            obj2 = obj;
            fragmentTransitionImpl.addTarget(obj4, view2);
            Object mergeTransitions = mergeTransitions(fragmentTransitionImpl, obj4, obj2, exitTransition, fragment, fragmentContainerTransition2.lastInIsPop);
            if (!(fragment2 == null || arrayList3 == null || (arrayList3.size() <= 0 && arrayList4.size() <= 0))) {
                final CancellationSignal cancellationSignal = new CancellationSignal();
                callback2.onStart(fragment2, cancellationSignal);
                fragmentTransitionImpl.setListenerForTransitionEnd(fragment2, mergeTransitions, cancellationSignal, new Runnable() {
                    public void run() {
                        callback2.onComplete(fragment2, cancellationSignal);
                    }
                });
            }
            if (mergeTransitions != null) {
                ArrayList arrayList5 = new ArrayList();
                FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
                fragmentTransitionImpl2.scheduleRemoveTargets(mergeTransitions, obj4, arrayList5, obj2, arrayList3, exitTransition, arrayList2);
                scheduleTargetChange(fragmentTransitionImpl2, viewGroup, fragment, view, arrayList2, obj4, arrayList5, obj2, arrayList3);
                View view3 = viewGroup;
                FragmentTransitionImpl fragmentTransitionImpl3 = fragmentTransitionImpl;
                ArrayList arrayList6 = arrayList2;
                fragmentTransitionImpl3.setNameOverridesOrdered(view3, arrayList6, arrayMap2);
                fragmentTransitionImpl3.beginDelayedTransition(view3, mergeTransitions);
                fragmentTransitionImpl3.scheduleNameReset(view3, arrayList6, arrayMap2);
            }
        }
    }

    private static void configureTransitionsReordered(@NonNull ViewGroup viewGroup, FragmentContainerTransition fragmentContainerTransition, View view, ArrayMap<String, String> arrayMap, Callback callback) {
        FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        View view2 = view;
        Callback callback2 = callback;
        Fragment fragment = fragmentContainerTransition2.lastIn;
        final Fragment fragment2 = fragmentContainerTransition2.firstOut;
        FragmentTransitionImpl chooseImpl = chooseImpl(fragment2, fragment);
        if (chooseImpl != null) {
            boolean z = fragmentContainerTransition2.lastInIsPop;
            boolean z2 = fragmentContainerTransition2.firstOutIsPop;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Object enterTransition = getEnterTransition(chooseImpl, fragment, z);
            Object exitTransition = getExitTransition(chooseImpl, fragment2, z2);
            Object obj = enterTransition;
            ArrayList arrayList3 = arrayList2;
            Object configureSharedElementsReordered = configureSharedElementsReordered(chooseImpl, viewGroup, view, arrayMap, fragmentContainerTransition, arrayList2, arrayList, enterTransition, exitTransition);
            Object obj2 = obj;
            if (obj2 == null && configureSharedElementsReordered == null) {
                enterTransition = exitTransition;
                if (enterTransition == null) {
                    return;
                }
            }
            enterTransition = exitTransition;
            ArrayList configureEnteringExitingViews = configureEnteringExitingViews(chooseImpl, enterTransition, fragment2, arrayList3, view2);
            ArrayList configureEnteringExitingViews2 = configureEnteringExitingViews(chooseImpl, obj2, fragment, arrayList, view2);
            setViewVisibility(configureEnteringExitingViews2, 4);
            Fragment fragment3 = fragment;
            ArrayList arrayList4 = configureEnteringExitingViews;
            Object mergeTransitions = mergeTransitions(chooseImpl, obj2, enterTransition, configureSharedElementsReordered, fragment3, z);
            if (!(fragment2 == null || arrayList4 == null || (arrayList4.size() <= 0 && arrayList3.size() <= 0))) {
                final CancellationSignal cancellationSignal = new CancellationSignal();
                final Callback callback3 = callback;
                callback3.onStart(fragment2, cancellationSignal);
                chooseImpl.setListenerForTransitionEnd(fragment2, mergeTransitions, cancellationSignal, new Runnable() {
                    public void run() {
                        callback3.onComplete(fragment2, cancellationSignal);
                    }
                });
            }
            if (mergeTransitions != null) {
                replaceHide(chooseImpl, enterTransition, fragment2, arrayList4);
                ArrayList prepareSetNameOverridesReordered = chooseImpl.prepareSetNameOverridesReordered(arrayList);
                FragmentTransitionImpl fragmentTransitionImpl = chooseImpl;
                fragmentTransitionImpl.scheduleRemoveTargets(mergeTransitions, obj2, configureEnteringExitingViews2, enterTransition, arrayList4, configureSharedElementsReordered, arrayList);
                ViewGroup viewGroup2 = viewGroup;
                chooseImpl.beginDelayedTransition(viewGroup2, mergeTransitions);
                fragmentTransitionImpl.setNameOverridesReordered(viewGroup2, arrayList3, arrayList, prepareSetNameOverridesReordered, arrayMap);
                setViewVisibility(configureEnteringExitingViews2, 0);
                chooseImpl.swapSharedElementTargets(configureSharedElementsReordered, arrayList3, arrayList);
            }
        }
    }

    private static FragmentContainerTransition ensureContainer(FragmentContainerTransition fragmentContainerTransition, SparseArray<FragmentContainerTransition> sparseArray, int i) {
        if (fragmentContainerTransition != null) {
            return fragmentContainerTransition;
        }
        fragmentContainerTransition = new FragmentContainerTransition();
        sparseArray.put(i, fragmentContainerTransition);
        return fragmentContainerTransition;
    }

    public static String findKeyForValue(ArrayMap<String, String> arrayMap, String str) {
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            if (str.equals(arrayMap.valueAt(i))) {
                return (String) arrayMap.keyAt(i);
            }
        }
        return null;
    }

    private static Object getEnterTransition(FragmentTransitionImpl fragmentTransitionImpl, Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        Object reenterTransition;
        if (z) {
            reenterTransition = fragment.getReenterTransition();
        } else {
            reenterTransition = fragment.getEnterTransition();
        }
        return fragmentTransitionImpl.cloneTransition(reenterTransition);
    }

    private static Object getExitTransition(FragmentTransitionImpl fragmentTransitionImpl, Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        Object returnTransition;
        if (z) {
            returnTransition = fragment.getReturnTransition();
        } else {
            returnTransition = fragment.getExitTransition();
        }
        return fragmentTransitionImpl.cloneTransition(returnTransition);
    }

    public static View getInEpicenterView(ArrayMap<String, View> arrayMap, FragmentContainerTransition fragmentContainerTransition, Object obj, boolean z) {
        BackStackRecord backStackRecord = fragmentContainerTransition.lastInTransaction;
        if (!(obj == null || arrayMap == null)) {
            ArrayList arrayList = backStackRecord.mSharedElementSourceNames;
            if (!(arrayList == null || arrayList.isEmpty())) {
                Object obj2;
                if (z) {
                    obj2 = (String) backStackRecord.mSharedElementSourceNames.get(0);
                } else {
                    obj2 = (String) backStackRecord.mSharedElementTargetNames.get(0);
                }
                return (View) arrayMap.get(obj2);
            }
        }
        return null;
    }

    private static Object getSharedElementTransition(FragmentTransitionImpl fragmentTransitionImpl, Fragment fragment, Fragment fragment2, boolean z) {
        if (fragment == null || fragment2 == null) {
            return null;
        }
        Object sharedElementReturnTransition;
        if (z) {
            sharedElementReturnTransition = fragment2.getSharedElementReturnTransition();
        } else {
            sharedElementReturnTransition = fragment.getSharedElementEnterTransition();
        }
        return fragmentTransitionImpl.wrapTransitionInSet(fragmentTransitionImpl.cloneTransition(sharedElementReturnTransition));
    }

    private static Object mergeTransitions(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Object obj2, Object obj3, Fragment fragment, boolean z) {
        boolean allowReturnTransitionOverlap = (obj == null || obj2 == null || fragment == null) ? true : z ? fragment.getAllowReturnTransitionOverlap() : fragment.getAllowEnterTransitionOverlap();
        if (allowReturnTransitionOverlap) {
            return fragmentTransitionImpl.mergeTransitionsTogether(obj2, obj, obj3);
        }
        return fragmentTransitionImpl.mergeTransitionsInSequence(obj2, obj, obj3);
    }

    private static void replaceHide(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Fragment fragment, final ArrayList<View> arrayList) {
        if (fragment != null && obj != null && fragment.mAdded && fragment.mHidden && fragment.mHiddenChanged) {
            fragment.setHideReplaced(true);
            fragmentTransitionImpl.scheduleHideFragmentView(obj, fragment.getView(), arrayList);
            OneShotPreDrawListener.add(fragment.mContainer, new Runnable() {
                public void run() {
                    FragmentTransition.setViewVisibility(arrayList, 4);
                }
            });
        }
    }

    private static FragmentTransitionImpl resolveSupportImpl() {
        try {
            return (FragmentTransitionImpl) Class.forName("androidx.transition.FragmentTransitionSupport").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }

    public static void retainValues(@NonNull ArrayMap<String, String> arrayMap, @NonNull ArrayMap<String, View> arrayMap2) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            if (!arrayMap2.containsKey((String) arrayMap.valueAt(size))) {
                arrayMap.removeAt(size);
            }
        }
    }

    private static void scheduleTargetChange(FragmentTransitionImpl fragmentTransitionImpl, ViewGroup viewGroup, Fragment fragment, View view, ArrayList<View> arrayList, Object obj, ArrayList<View> arrayList2, Object obj2, ArrayList<View> arrayList3) {
        final Object obj3 = obj;
        final FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
        final View view2 = view;
        final Fragment fragment2 = fragment;
        final ArrayList<View> arrayList4 = arrayList;
        final ArrayList<View> arrayList5 = arrayList2;
        final ArrayList<View> arrayList6 = arrayList3;
        final Object obj4 = obj2;
        AnonymousClass4 anonymousClass4 = new Runnable() {
            public void run() {
                Object obj = obj3;
                if (obj != null) {
                    fragmentTransitionImpl2.removeTarget(obj, view2);
                    arrayList5.addAll(FragmentTransition.configureEnteringExitingViews(fragmentTransitionImpl2, obj3, fragment2, arrayList4, view2));
                }
                if (arrayList6 != null) {
                    if (obj4 != null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(view2);
                        fragmentTransitionImpl2.replaceTargets(obj4, arrayList6, arrayList);
                    }
                    arrayList6.clear();
                    arrayList6.add(view2);
                }
            }
        };
        ViewGroup viewGroup2 = viewGroup;
        OneShotPreDrawListener.add(viewGroup, anonymousClass4);
    }

    private static void setOutEpicenter(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Object obj2, ArrayMap<String, View> arrayMap, boolean z, BackStackRecord backStackRecord) {
        ArrayList arrayList = backStackRecord.mSharedElementSourceNames;
        if (arrayList != null && !arrayList.isEmpty()) {
            Object obj3;
            if (z) {
                obj3 = (String) backStackRecord.mSharedElementTargetNames.get(0);
            } else {
                obj3 = (String) backStackRecord.mSharedElementSourceNames.get(0);
            }
            View view = (View) arrayMap.get(obj3);
            fragmentTransitionImpl.setEpicenter(obj, view);
            if (obj2 != null) {
                fragmentTransitionImpl.setEpicenter(obj2, view);
            }
        }
    }

    public static void setViewVisibility(ArrayList<View> arrayList, int i) {
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((View) arrayList.get(size)).setVisibility(i);
            }
        }
    }

    public static void startTransitions(@NonNull Context context, @NonNull FragmentContainer fragmentContainer, ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2, boolean z, Callback callback) {
        SparseArray sparseArray = new SparseArray();
        for (int i3 = i; i3 < i2; i3++) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i3);
            if (((Boolean) arrayList2.get(i3)).booleanValue()) {
                calculatePopFragments(backStackRecord, sparseArray, z);
            } else {
                calculateFragments(backStackRecord, sparseArray, z);
            }
        }
        if (sparseArray.size() != 0) {
            View view = new View(context);
            int size = sparseArray.size();
            for (int i4 = 0; i4 < size; i4++) {
                int keyAt = sparseArray.keyAt(i4);
                ArrayMap calculateNameOverrides = calculateNameOverrides(keyAt, arrayList, arrayList2, i, i2);
                FragmentContainerTransition fragmentContainerTransition = (FragmentContainerTransition) sparseArray.valueAt(i4);
                if (fragmentContainer.onHasView()) {
                    ViewGroup viewGroup = (ViewGroup) fragmentContainer.onFindViewById(keyAt);
                    if (viewGroup != null) {
                        if (z) {
                            configureTransitionsReordered(viewGroup, fragmentContainerTransition, view, calculateNameOverrides, callback);
                        } else {
                            configureTransitionsOrdered(viewGroup, fragmentContainerTransition, view, calculateNameOverrides, callback);
                        }
                    }
                }
            }
        }
    }

    public static boolean supportsTransition() {
        return (PLATFORM_IMPL == null && SUPPORT_IMPL == null) ? false : true;
    }
}
