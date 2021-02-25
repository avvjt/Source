package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.collection.ArrayMap;
import androidx.collection.LongSparseArray;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import f.d.b.a.a;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public abstract class Transition implements Cloneable {
    public static final boolean DBG = false;
    private static final int[] DEFAULT_MATCH_ORDER = new int[]{2, 1, 3, 4};
    private static final String LOG_TAG = "Transition";
    private static final int MATCH_FIRST = 1;
    public static final int MATCH_ID = 3;
    private static final String MATCH_ID_STR = "id";
    public static final int MATCH_INSTANCE = 1;
    private static final String MATCH_INSTANCE_STR = "instance";
    public static final int MATCH_ITEM_ID = 4;
    private static final String MATCH_ITEM_ID_STR = "itemId";
    private static final int MATCH_LAST = 4;
    public static final int MATCH_NAME = 2;
    private static final String MATCH_NAME_STR = "name";
    private static final PathMotion STRAIGHT_PATH_MOTION = new PathMotion() {
        public Path getPath(float f, float f2, float f3, float f4) {
            Path path = new Path();
            path.moveTo(f, f2);
            path.lineTo(f3, f4);
            return path;
        }
    };
    private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal();
    private ArrayList<Animator> mAnimators = new ArrayList();
    public boolean mCanRemoveViews = false;
    public ArrayList<Animator> mCurrentAnimators = new ArrayList();
    public long mDuration = -1;
    private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
    private ArrayList<TransitionValues> mEndValuesList;
    private boolean mEnded = false;
    private EpicenterCallback mEpicenterCallback;
    private TimeInterpolator mInterpolator = null;
    private ArrayList<TransitionListener> mListeners = null;
    private int[] mMatchOrder = DEFAULT_MATCH_ORDER;
    private String mName = getClass().getName();
    private ArrayMap<String, String> mNameOverrides;
    private int mNumInstances = 0;
    public TransitionSet mParent = null;
    private PathMotion mPathMotion = STRAIGHT_PATH_MOTION;
    private boolean mPaused = false;
    public TransitionPropagation mPropagation;
    private ViewGroup mSceneRoot = null;
    private long mStartDelay = -1;
    private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
    private ArrayList<TransitionValues> mStartValuesList;
    private ArrayList<View> mTargetChildExcludes = null;
    private ArrayList<View> mTargetExcludes = null;
    private ArrayList<Integer> mTargetIdChildExcludes = null;
    private ArrayList<Integer> mTargetIdExcludes = null;
    public ArrayList<Integer> mTargetIds = new ArrayList();
    private ArrayList<String> mTargetNameExcludes = null;
    private ArrayList<String> mTargetNames = null;
    private ArrayList<Class<?>> mTargetTypeChildExcludes = null;
    private ArrayList<Class<?>> mTargetTypeExcludes = null;
    private ArrayList<Class<?>> mTargetTypes = null;
    public ArrayList<View> mTargets = new ArrayList();

    public static class AnimationInfo {
        public String mName;
        public Transition mTransition;
        public TransitionValues mValues;
        public View mView;
        public WindowIdImpl mWindowId;

        public AnimationInfo(View view, String str, Transition transition, WindowIdImpl windowIdImpl, TransitionValues transitionValues) {
            this.mView = view;
            this.mName = str;
            this.mValues = transitionValues;
            this.mWindowId = windowIdImpl;
            this.mTransition = transition;
        }
    }

    public static class ArrayListManager {
        private ArrayListManager() {
        }

        public static <T> ArrayList<T> add(ArrayList<T> arrayList, T t) {
            ArrayList arrayList2;
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
            }
            if (!arrayList2.contains(t)) {
                arrayList2.add(t);
            }
            return arrayList2;
        }

        public static <T> ArrayList<T> remove(ArrayList<T> arrayList, T t) {
            if (arrayList == null) {
                return arrayList;
            }
            arrayList.remove(t);
            return arrayList.isEmpty() ? null : arrayList;
        }
    }

    public static abstract class EpicenterCallback {
        public abstract Rect onGetEpicenter(@NonNull Transition transition);
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MatchOrder {
    }

    public interface TransitionListener {
        void onTransitionCancel(@NonNull Transition transition);

        void onTransitionEnd(@NonNull Transition transition);

        void onTransitionPause(@NonNull Transition transition);

        void onTransitionResume(@NonNull Transition transition);

        void onTransitionStart(@NonNull Transition transition);
    }

    private void addUnmatched(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2) {
        for (int i = 0; i < arrayMap.size(); i++) {
            TransitionValues transitionValues = (TransitionValues) arrayMap.valueAt(i);
            if (isValidTarget(transitionValues.view)) {
                this.mStartValuesList.add(transitionValues);
                this.mEndValuesList.add(null);
            }
        }
        for (int i2 = 0; i2 < arrayMap2.size(); i2++) {
            TransitionValues transitionValues2 = (TransitionValues) arrayMap2.valueAt(i2);
            if (isValidTarget(transitionValues2.view)) {
                this.mEndValuesList.add(transitionValues2);
                this.mStartValuesList.add(null);
            }
        }
    }

    private static void addViewValues(TransitionValuesMaps transitionValuesMaps, View view, TransitionValues transitionValues) {
        transitionValuesMaps.mViewValues.put(view, transitionValues);
        int id = view.getId();
        if (id >= 0) {
            if (transitionValuesMaps.mIdValues.indexOfKey(id) >= 0) {
                transitionValuesMaps.mIdValues.put(id, null);
            } else {
                transitionValuesMaps.mIdValues.put(id, view);
            }
        }
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            if (transitionValuesMaps.mNameValues.containsKey(transitionName)) {
                transitionValuesMaps.mNameValues.put(transitionName, null);
            } else {
                transitionValuesMaps.mNameValues.put(transitionName, view);
            }
        }
        if (view.getParent() instanceof ListView) {
            ListView listView = (ListView) view.getParent();
            if (listView.getAdapter().hasStableIds()) {
                long itemIdAtPosition = listView.getItemIdAtPosition(listView.getPositionForView(view));
                if (transitionValuesMaps.mItemIdValues.indexOfKey(itemIdAtPosition) >= 0) {
                    view = (View) transitionValuesMaps.mItemIdValues.get(itemIdAtPosition);
                    if (view != null) {
                        ViewCompat.setHasTransientState(view, false);
                        transitionValuesMaps.mItemIdValues.put(itemIdAtPosition, null);
                        return;
                    }
                    return;
                }
                ViewCompat.setHasTransientState(view, true);
                transitionValuesMaps.mItemIdValues.put(itemIdAtPosition, view);
            }
        }
    }

    private static boolean alreadyContains(int[] iArr, int i) {
        int i2 = iArr[i];
        for (int i3 = 0; i3 < i; i3++) {
            if (iArr[i3] == i2) {
                return true;
            }
        }
        return false;
    }

    private void captureHierarchy(View view, boolean z) {
        if (view != null) {
            int id = view.getId();
            ArrayList arrayList = this.mTargetIdExcludes;
            if (arrayList == null || !arrayList.contains(Integer.valueOf(id))) {
                arrayList = this.mTargetExcludes;
                if (arrayList == null || !arrayList.contains(view)) {
                    int size;
                    arrayList = this.mTargetTypeExcludes;
                    if (arrayList != null) {
                        size = arrayList.size();
                        int i = 0;
                        while (i < size) {
                            if (!((Class) this.mTargetTypeExcludes.get(i)).isInstance(view)) {
                                i++;
                            } else {
                                return;
                            }
                        }
                    }
                    if (view.getParent() instanceof ViewGroup) {
                        TransitionValues transitionValues = new TransitionValues(view);
                        if (z) {
                            captureStartValues(transitionValues);
                        } else {
                            captureEndValues(transitionValues);
                        }
                        transitionValues.mTargetedTransitions.add(this);
                        capturePropagationValues(transitionValues);
                        if (z) {
                            addViewValues(this.mStartValues, view, transitionValues);
                        } else {
                            addViewValues(this.mEndValues, view, transitionValues);
                        }
                    }
                    if (view instanceof ViewGroup) {
                        arrayList = this.mTargetIdChildExcludes;
                        if (arrayList == null || !arrayList.contains(Integer.valueOf(id))) {
                            ArrayList arrayList2 = this.mTargetChildExcludes;
                            if (arrayList2 == null || !arrayList2.contains(view)) {
                                arrayList2 = this.mTargetTypeChildExcludes;
                                if (arrayList2 != null) {
                                    id = arrayList2.size();
                                    size = 0;
                                    while (size < id) {
                                        if (!((Class) this.mTargetTypeChildExcludes.get(size)).isInstance(view)) {
                                            size++;
                                        } else {
                                            return;
                                        }
                                    }
                                }
                                ViewGroup viewGroup = (ViewGroup) view;
                                for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                                    captureHierarchy(viewGroup.getChildAt(i2), z);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Integer> excludeId(ArrayList<Integer> arrayList, int i, boolean z) {
        if (i <= 0) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, Integer.valueOf(i));
        }
        return ArrayListManager.remove(arrayList, Integer.valueOf(i));
    }

    private static <T> ArrayList<T> excludeObject(ArrayList<T> arrayList, T t, boolean z) {
        if (t == null) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, t);
        }
        return ArrayListManager.remove(arrayList, t);
    }

    private ArrayList<Class<?>> excludeType(ArrayList<Class<?>> arrayList, Class<?> cls, boolean z) {
        if (cls == null) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, cls);
        }
        return ArrayListManager.remove(arrayList, cls);
    }

    private ArrayList<View> excludeView(ArrayList<View> arrayList, View view, boolean z) {
        if (view == null) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, view);
        }
        return ArrayListManager.remove(arrayList, view);
    }

    private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
        ArrayMap<Animator, AnimationInfo> arrayMap = (ArrayMap) sRunningAnimators.get();
        if (arrayMap != null) {
            return arrayMap;
        }
        ArrayMap arrayMap2 = new ArrayMap();
        sRunningAnimators.set(arrayMap2);
        return arrayMap2;
    }

    private static boolean isValidMatch(int i) {
        return i >= 1 && i <= 4;
    }

    private static boolean isValueChanged(TransitionValues transitionValues, TransitionValues transitionValues2, String str) {
        Object obj = transitionValues.values.get(str);
        Object obj2 = transitionValues2.values.get(str);
        if (obj == null && obj2 == null) {
            return false;
        }
        if (obj == null || obj2 == null) {
            return true;
        }
        return 1 ^ obj.equals(obj2);
    }

    private void matchIds(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2, SparseArray<View> sparseArray, SparseArray<View> sparseArray2) {
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            View view = (View) sparseArray.valueAt(i);
            if (view != null && isValidTarget(view)) {
                View view2 = (View) sparseArray2.get(sparseArray.keyAt(i));
                if (view2 != null && isValidTarget(view2)) {
                    TransitionValues transitionValues = (TransitionValues) arrayMap.get(view);
                    TransitionValues transitionValues2 = (TransitionValues) arrayMap2.get(view2);
                    if (!(transitionValues == null || transitionValues2 == null)) {
                        this.mStartValuesList.add(transitionValues);
                        this.mEndValuesList.add(transitionValues2);
                        arrayMap.remove(view);
                        arrayMap2.remove(view2);
                    }
                }
            }
        }
    }

    private void matchInstances(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            View view = (View) arrayMap.keyAt(size);
            if (view != null && isValidTarget(view)) {
                TransitionValues transitionValues = (TransitionValues) arrayMap2.remove(view);
                if (transitionValues != null && isValidTarget(transitionValues.view)) {
                    this.mStartValuesList.add((TransitionValues) arrayMap.removeAt(size));
                    this.mEndValuesList.add(transitionValues);
                }
            }
        }
    }

    private void matchItemIds(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2, LongSparseArray<View> longSparseArray, LongSparseArray<View> longSparseArray2) {
        int size = longSparseArray.size();
        for (int i = 0; i < size; i++) {
            View view = (View) longSparseArray.valueAt(i);
            if (view != null && isValidTarget(view)) {
                View view2 = (View) longSparseArray2.get(longSparseArray.keyAt(i));
                if (view2 != null && isValidTarget(view2)) {
                    TransitionValues transitionValues = (TransitionValues) arrayMap.get(view);
                    TransitionValues transitionValues2 = (TransitionValues) arrayMap2.get(view2);
                    if (!(transitionValues == null || transitionValues2 == null)) {
                        this.mStartValuesList.add(transitionValues);
                        this.mEndValuesList.add(transitionValues2);
                        arrayMap.remove(view);
                        arrayMap2.remove(view2);
                    }
                }
            }
        }
    }

    private void matchNames(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2, ArrayMap<String, View> arrayMap3, ArrayMap<String, View> arrayMap4) {
        int size = arrayMap3.size();
        for (int i = 0; i < size; i++) {
            View view = (View) arrayMap3.valueAt(i);
            if (view != null && isValidTarget(view)) {
                View view2 = (View) arrayMap4.get(arrayMap3.keyAt(i));
                if (view2 != null && isValidTarget(view2)) {
                    TransitionValues transitionValues = (TransitionValues) arrayMap.get(view);
                    TransitionValues transitionValues2 = (TransitionValues) arrayMap2.get(view2);
                    if (!(transitionValues == null || transitionValues2 == null)) {
                        this.mStartValuesList.add(transitionValues);
                        this.mEndValuesList.add(transitionValues2);
                        arrayMap.remove(view);
                        arrayMap2.remove(view2);
                    }
                }
            }
        }
    }

    private void matchStartAndEnd(TransitionValuesMaps transitionValuesMaps, TransitionValuesMaps transitionValuesMaps2) {
        ArrayMap arrayMap = new ArrayMap(transitionValuesMaps.mViewValues);
        ArrayMap arrayMap2 = new ArrayMap(transitionValuesMaps2.mViewValues);
        int i = 0;
        while (true) {
            int[] iArr = this.mMatchOrder;
            if (i < iArr.length) {
                int i2 = iArr[i];
                if (i2 == 1) {
                    matchInstances(arrayMap, arrayMap2);
                } else if (i2 == 2) {
                    matchNames(arrayMap, arrayMap2, transitionValuesMaps.mNameValues, transitionValuesMaps2.mNameValues);
                } else if (i2 == 3) {
                    matchIds(arrayMap, arrayMap2, transitionValuesMaps.mIdValues, transitionValuesMaps2.mIdValues);
                } else if (i2 == 4) {
                    matchItemIds(arrayMap, arrayMap2, transitionValuesMaps.mItemIdValues, transitionValuesMaps2.mItemIdValues);
                }
                i++;
            } else {
                addUnmatched(arrayMap, arrayMap2);
                return;
            }
        }
    }

    private static int[] parseMatchOrder(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        Object obj = new int[stringTokenizer.countTokens()];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String trim = stringTokenizer.nextToken().trim();
            if ("id".equalsIgnoreCase(trim)) {
                obj[i] = 3;
            } else if (MATCH_INSTANCE_STR.equalsIgnoreCase(trim)) {
                obj[i] = 1;
            } else if ("name".equalsIgnoreCase(trim)) {
                obj[i] = 2;
            } else if (MATCH_ITEM_ID_STR.equalsIgnoreCase(trim)) {
                obj[i] = 4;
            } else if (trim.isEmpty()) {
                Object obj2 = new int[(obj.length - 1)];
                System.arraycopy(obj, 0, obj2, 0, i);
                i--;
                obj = obj2;
            } else {
                throw new InflateException(a.u("Unknown match type in matchOrder: '", trim, "'"));
            }
            i++;
        }
        return obj;
    }

    private void runAnimator(Animator animator, final ArrayMap<Animator, AnimationInfo> arrayMap) {
        if (animator != null) {
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    arrayMap.remove(animator);
                    Transition.this.mCurrentAnimators.remove(animator);
                }

                public void onAnimationStart(Animator animator) {
                    Transition.this.mCurrentAnimators.add(animator);
                }
            });
            animate(animator);
        }
    }

    @NonNull
    public Transition addListener(@NonNull TransitionListener transitionListener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList();
        }
        this.mListeners.add(transitionListener);
        return this;
    }

    @NonNull
    public Transition addTarget(@NonNull View view) {
        this.mTargets.add(view);
        return this;
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void animate(Animator animator) {
        if (animator == null) {
            end();
            return;
        }
        if (getDuration() >= 0) {
            animator.setDuration(getDuration());
        }
        if (getStartDelay() >= 0) {
            animator.setStartDelay(animator.getStartDelay() + getStartDelay());
        }
        if (getInterpolator() != null) {
            animator.setInterpolator(getInterpolator());
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Transition.this.end();
                animator.removeListener(this);
            }
        });
        animator.start();
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void cancel() {
        for (int size = this.mCurrentAnimators.size() - 1; size >= 0; size--) {
            ((Animator) this.mCurrentAnimators.get(size)).cancel();
        }
        ArrayList arrayList = this.mListeners;
        if (arrayList != null && arrayList.size() > 0) {
            arrayList = (ArrayList) this.mListeners.clone();
            int size2 = arrayList.size();
            for (int i = 0; i < size2; i++) {
                ((TransitionListener) arrayList.get(i)).onTransitionCancel(this);
            }
        }
    }

    public abstract void captureEndValues(@NonNull TransitionValues transitionValues);

    public void capturePropagationValues(TransitionValues transitionValues) {
        if (!(this.mPropagation == null || transitionValues.values.isEmpty())) {
            String[] propagationProperties = this.mPropagation.getPropagationProperties();
            if (propagationProperties != null) {
                Object obj = null;
                for (Object containsKey : propagationProperties) {
                    if (!transitionValues.values.containsKey(containsKey)) {
                        break;
                    }
                }
                obj = 1;
                if (obj == null) {
                    this.mPropagation.captureValues(transitionValues);
                }
            }
        }
    }

    public abstract void captureStartValues(@NonNull TransitionValues transitionValues);

    /* JADX WARNING: Removed duplicated region for block: B:61:? A:{SYNTHETIC, RETURN, ORIG_RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void captureValues(ViewGroup viewGroup, boolean z) {
        clearValues(z);
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            ArrayList arrayList = this.mTargetNames;
            if (arrayList == null || arrayList.isEmpty()) {
                arrayList = this.mTargetTypes;
                if (arrayList == null || arrayList.isEmpty()) {
                    int i;
                    int i2;
                    View view;
                    for (i = 0; i < this.mTargetIds.size(); i++) {
                        View findViewById = viewGroup.findViewById(((Integer) this.mTargetIds.get(i)).intValue());
                        if (findViewById != null) {
                            TransitionValues transitionValues = new TransitionValues(findViewById);
                            if (z) {
                                captureStartValues(transitionValues);
                            } else {
                                captureEndValues(transitionValues);
                            }
                            transitionValues.mTargetedTransitions.add(this);
                            capturePropagationValues(transitionValues);
                            if (z) {
                                addViewValues(this.mStartValues, findViewById, transitionValues);
                            } else {
                                addViewValues(this.mEndValues, findViewById, transitionValues);
                            }
                        }
                    }
                    for (i2 = 0; i2 < this.mTargets.size(); i2++) {
                        view = (View) this.mTargets.get(i2);
                        TransitionValues transitionValues2 = new TransitionValues(view);
                        if (z) {
                            captureStartValues(transitionValues2);
                        } else {
                            captureEndValues(transitionValues2);
                        }
                        transitionValues2.mTargetedTransitions.add(this);
                        capturePropagationValues(transitionValues2);
                        if (z) {
                            addViewValues(this.mStartValues, view, transitionValues2);
                        } else {
                            addViewValues(this.mEndValues, view, transitionValues2);
                        }
                    }
                    if (z) {
                        ArrayMap arrayMap = this.mNameOverrides;
                        if (arrayMap != null) {
                            i2 = arrayMap.size();
                            ArrayList arrayList2 = new ArrayList(i2);
                            for (i = 0; i < i2; i++) {
                                arrayList2.add(this.mStartValues.mNameValues.remove((String) this.mNameOverrides.keyAt(i)));
                            }
                            for (int i3 = 0; i3 < i2; i3++) {
                                view = (View) arrayList2.get(i3);
                                if (view != null) {
                                    this.mStartValues.mNameValues.put((String) this.mNameOverrides.valueAt(i3), view);
                                }
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
        }
        captureHierarchy(viewGroup, z);
        if (z) {
        }
    }

    public void clearValues(boolean z) {
        if (z) {
            this.mStartValues.mViewValues.clear();
            this.mStartValues.mIdValues.clear();
            this.mStartValues.mItemIdValues.clear();
            return;
        }
        this.mEndValues.mViewValues.clear();
        this.mEndValues.mIdValues.clear();
        this.mEndValues.mItemIdValues.clear();
    }

    @Nullable
    public Animator createAnimator(@NonNull ViewGroup viewGroup, @Nullable TransitionValues transitionValues, @Nullable TransitionValues transitionValues2) {
        return null;
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void createAnimators(ViewGroup viewGroup, TransitionValuesMaps transitionValuesMaps, TransitionValuesMaps transitionValuesMaps2, ArrayList<TransitionValues> arrayList, ArrayList<TransitionValues> arrayList2) {
        ViewGroup viewGroup2 = viewGroup;
        ArrayMap runningAnimators = getRunningAnimators();
        SparseIntArray sparseIntArray = new SparseIntArray();
        int size = arrayList.size();
        long j = RecyclerView.FOREVER_NS;
        int i = 0;
        while (i < size) {
            int i2;
            int i3;
            TransitionValues transitionValues = (TransitionValues) arrayList.get(i);
            TransitionValues transitionValues2 = (TransitionValues) arrayList2.get(i);
            if (!(transitionValues == null || transitionValues.mTargetedTransitions.contains(this))) {
                transitionValues = null;
            }
            if (!(transitionValues2 == null || transitionValues2.mTargetedTransitions.contains(this))) {
                transitionValues2 = null;
            }
            if (!(transitionValues == null && transitionValues2 == null)) {
                Object obj = (transitionValues == null || transitionValues2 == null || isTransitionRequired(transitionValues, transitionValues2)) ? 1 : null;
                if (obj != null) {
                    Animator createAnimator = createAnimator(viewGroup2, transitionValues, transitionValues2);
                    if (createAnimator != null) {
                        View view;
                        TransitionValues transitionValues3;
                        Object obj2;
                        Animator animator;
                        if (transitionValues2 != null) {
                            Animator animator2;
                            TransitionValues transitionValues4;
                            view = transitionValues2.view;
                            String[] transitionProperties = getTransitionProperties();
                            if (transitionProperties == null || transitionProperties.length <= 0) {
                                i2 = size;
                                i3 = i;
                                animator2 = createAnimator;
                                transitionValues4 = null;
                            } else {
                                transitionValues4 = new TransitionValues(view);
                                animator = createAnimator;
                                i2 = size;
                                transitionValues3 = (TransitionValues) transitionValuesMaps2.mViewValues.get(view);
                                if (transitionValues3 != null) {
                                    size = 0;
                                    while (size < transitionProperties.length) {
                                        i3 = i;
                                        TransitionValues transitionValues5 = transitionValues3;
                                        transitionValues4.values.put(transitionProperties[size], transitionValues3.values.get(transitionProperties[size]));
                                        size++;
                                        ArrayList<TransitionValues> arrayList3 = arrayList2;
                                        i = i3;
                                        transitionValues3 = transitionValues5;
                                    }
                                }
                                i3 = i;
                                int size2 = runningAnimators.size();
                                for (int i4 = 0; i4 < size2; i4++) {
                                    AnimationInfo animationInfo = (AnimationInfo) runningAnimators.get((Animator) runningAnimators.keyAt(i4));
                                    if (animationInfo.mValues != null && animationInfo.mView == view && animationInfo.mName.equals(getName()) && animationInfo.mValues.equals(transitionValues4)) {
                                        animator2 = null;
                                        break;
                                    }
                                }
                                animator2 = animator;
                            }
                            obj2 = animator2;
                            transitionValues3 = transitionValues4;
                        } else {
                            animator = createAnimator;
                            i2 = size;
                            i3 = i;
                            view = transitionValues.view;
                            obj2 = animator;
                            transitionValues3 = null;
                        }
                        if (obj2 != null) {
                            TransitionPropagation transitionPropagation = this.mPropagation;
                            if (transitionPropagation != null) {
                                long startDelay = transitionPropagation.getStartDelay(viewGroup2, this, transitionValues, transitionValues2);
                                sparseIntArray.put(this.mAnimators.size(), (int) startDelay);
                                j = Math.min(startDelay, j);
                            }
                            long j2 = j;
                            runningAnimators.put(obj2, new AnimationInfo(view, getName(), this, ViewUtils.getWindowId(viewGroup), transitionValues3));
                            this.mAnimators.add(obj2);
                            j = j2;
                        }
                        i = i3 + 1;
                        size = i2;
                    }
                }
            }
            i2 = size;
            i3 = i;
            i = i3 + 1;
            size = i2;
        }
        if (sparseIntArray.size() != 0) {
            for (int i5 = 0; i5 < sparseIntArray.size(); i5++) {
                Animator animator3 = (Animator) this.mAnimators.get(sparseIntArray.keyAt(i5));
                animator3.setStartDelay(animator3.getStartDelay() + (((long) sparseIntArray.valueAt(i5)) - j));
            }
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void end() {
        int i = this.mNumInstances - 1;
        this.mNumInstances = i;
        if (i == 0) {
            View view;
            ArrayList arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                arrayList = (ArrayList) this.mListeners.clone();
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ((TransitionListener) arrayList.get(i2)).onTransitionEnd(this);
                }
            }
            for (i = 0; i < this.mStartValues.mItemIdValues.size(); i++) {
                view = (View) this.mStartValues.mItemIdValues.valueAt(i);
                if (view != null) {
                    ViewCompat.setHasTransientState(view, false);
                }
            }
            for (i = 0; i < this.mEndValues.mItemIdValues.size(); i++) {
                view = (View) this.mEndValues.mItemIdValues.valueAt(i);
                if (view != null) {
                    ViewCompat.setHasTransientState(view, false);
                }
            }
            this.mEnded = true;
        }
    }

    @NonNull
    public Transition excludeChildren(@NonNull View view, boolean z) {
        this.mTargetChildExcludes = excludeView(this.mTargetChildExcludes, view, z);
        return this;
    }

    @NonNull
    public Transition excludeTarget(@NonNull View view, boolean z) {
        this.mTargetExcludes = excludeView(this.mTargetExcludes, view, z);
        return this;
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void forceToEnd(ViewGroup viewGroup) {
        SimpleArrayMap runningAnimators = getRunningAnimators();
        int size = runningAnimators.size();
        if (viewGroup != null && size != 0) {
            WindowIdImpl windowId = ViewUtils.getWindowId(viewGroup);
            ArrayMap arrayMap = new ArrayMap(runningAnimators);
            runningAnimators.clear();
            for (size--; size >= 0; size--) {
                AnimationInfo animationInfo = (AnimationInfo) arrayMap.valueAt(size);
                if (!(animationInfo.mView == null || windowId == null || !windowId.equals(animationInfo.mWindowId))) {
                    ((Animator) arrayMap.keyAt(size)).end();
                }
            }
        }
    }

    public long getDuration() {
        return this.mDuration;
    }

    @Nullable
    public Rect getEpicenter() {
        EpicenterCallback epicenterCallback = this.mEpicenterCallback;
        if (epicenterCallback == null) {
            return null;
        }
        return epicenterCallback.onGetEpicenter(this);
    }

    @Nullable
    public EpicenterCallback getEpicenterCallback() {
        return this.mEpicenterCallback;
    }

    @Nullable
    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public TransitionValues getMatchedTransitionValues(View view, boolean z) {
        TransitionSet transitionSet = this.mParent;
        if (transitionSet != null) {
            return transitionSet.getMatchedTransitionValues(view, z);
        }
        ArrayList arrayList = z ? this.mStartValuesList : this.mEndValuesList;
        TransitionValues transitionValues = null;
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        int i = -1;
        for (int i2 = 0; i2 < size; i2++) {
            TransitionValues transitionValues2 = (TransitionValues) arrayList.get(i2);
            if (transitionValues2 == null) {
                return null;
            }
            if (transitionValues2.view == view) {
                i = i2;
                break;
            }
        }
        if (i >= 0) {
            transitionValues = (TransitionValues) (z ? this.mEndValuesList : this.mStartValuesList).get(i);
        }
        return transitionValues;
    }

    @NonNull
    public String getName() {
        return this.mName;
    }

    @NonNull
    public PathMotion getPathMotion() {
        return this.mPathMotion;
    }

    @Nullable
    public TransitionPropagation getPropagation() {
        return this.mPropagation;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    @NonNull
    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }

    @Nullable
    public List<String> getTargetNames() {
        return this.mTargetNames;
    }

    @Nullable
    public List<Class<?>> getTargetTypes() {
        return this.mTargetTypes;
    }

    @NonNull
    public List<View> getTargets() {
        return this.mTargets;
    }

    @Nullable
    public String[] getTransitionProperties() {
        return null;
    }

    @Nullable
    public TransitionValues getTransitionValues(@NonNull View view, boolean z) {
        TransitionSet transitionSet = this.mParent;
        if (transitionSet != null) {
            return transitionSet.getTransitionValues(view, z);
        }
        return (TransitionValues) (z ? this.mStartValues : this.mEndValues).mViewValues.get(view);
    }

    public boolean isTransitionRequired(@Nullable TransitionValues transitionValues, @Nullable TransitionValues transitionValues2) {
        if (transitionValues == null || transitionValues2 == null) {
            return false;
        }
        String[] transitionProperties = getTransitionProperties();
        if (transitionProperties != null) {
            int length = transitionProperties.length;
            int i = 0;
            while (i < length) {
                if (!isValueChanged(transitionValues, transitionValues2, transitionProperties[i])) {
                    i++;
                }
            }
            return false;
        }
        for (String isValueChanged : transitionValues.values.keySet()) {
            if (isValueChanged(transitionValues, transitionValues2, isValueChanged)) {
            }
        }
        return false;
        return true;
    }

    public boolean isValidTarget(View view) {
        int id = view.getId();
        ArrayList arrayList = this.mTargetIdExcludes;
        if (arrayList != null && arrayList.contains(Integer.valueOf(id))) {
            return false;
        }
        arrayList = this.mTargetExcludes;
        if (arrayList != null && arrayList.contains(view)) {
            return false;
        }
        arrayList = this.mTargetTypeExcludes;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                if (((Class) this.mTargetTypeExcludes.get(i)).isInstance(view)) {
                    return false;
                }
            }
        }
        if (this.mTargetNameExcludes != null && ViewCompat.getTransitionName(view) != null && this.mTargetNameExcludes.contains(ViewCompat.getTransitionName(view))) {
            return false;
        }
        if (this.mTargetIds.size() == 0 && this.mTargets.size() == 0) {
            arrayList = this.mTargetTypes;
            if (arrayList == null || arrayList.isEmpty()) {
                arrayList = this.mTargetNames;
                if (arrayList == null || arrayList.isEmpty()) {
                    return true;
                }
            }
        }
        if (this.mTargetIds.contains(Integer.valueOf(id)) || this.mTargets.contains(view)) {
            return true;
        }
        ArrayList arrayList2 = this.mTargetNames;
        if (arrayList2 != null && arrayList2.contains(ViewCompat.getTransitionName(view))) {
            return true;
        }
        if (this.mTargetTypes != null) {
            for (id = 0; id < this.mTargetTypes.size(); id++) {
                if (((Class) this.mTargetTypes.get(id)).isInstance(view)) {
                    return true;
                }
            }
        }
        return false;
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void pause(View view) {
        if (!this.mEnded) {
            ArrayMap runningAnimators = getRunningAnimators();
            int size = runningAnimators.size();
            WindowIdImpl windowId = ViewUtils.getWindowId(view);
            for (size--; size >= 0; size--) {
                AnimationInfo animationInfo = (AnimationInfo) runningAnimators.valueAt(size);
                if (animationInfo.mView != null && windowId.equals(animationInfo.mWindowId)) {
                    AnimatorUtils.pause((Animator) runningAnimators.keyAt(size));
                }
            }
            ArrayList arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                arrayList = (ArrayList) this.mListeners.clone();
                int size2 = arrayList.size();
                for (size = 0; size < size2; size++) {
                    ((TransitionListener) arrayList.get(size)).onTransitionPause(this);
                }
            }
            this.mPaused = true;
        }
    }

    public void playTransition(ViewGroup viewGroup) {
        this.mStartValuesList = new ArrayList();
        this.mEndValuesList = new ArrayList();
        matchStartAndEnd(this.mStartValues, this.mEndValues);
        ArrayMap runningAnimators = getRunningAnimators();
        int size = runningAnimators.size();
        WindowIdImpl windowId = ViewUtils.getWindowId(viewGroup);
        for (size--; size >= 0; size--) {
            Animator animator = (Animator) runningAnimators.keyAt(size);
            if (animator != null) {
                AnimationInfo animationInfo = (AnimationInfo) runningAnimators.get(animator);
                if (!(animationInfo == null || animationInfo.mView == null || !windowId.equals(animationInfo.mWindowId))) {
                    TransitionValues transitionValues = animationInfo.mValues;
                    View view = animationInfo.mView;
                    TransitionValues transitionValues2 = getTransitionValues(view, true);
                    TransitionValues matchedTransitionValues = getMatchedTransitionValues(view, true);
                    if (transitionValues2 == null && matchedTransitionValues == null) {
                        matchedTransitionValues = (TransitionValues) this.mEndValues.mViewValues.get(view);
                    }
                    Object obj = (!(transitionValues2 == null && matchedTransitionValues == null) && animationInfo.mTransition.isTransitionRequired(transitionValues, matchedTransitionValues)) ? 1 : null;
                    if (obj != null) {
                        if (animator.isRunning() || animator.isStarted()) {
                            animator.cancel();
                        } else {
                            runningAnimators.remove(animator);
                        }
                    }
                }
            }
        }
        createAnimators(viewGroup, this.mStartValues, this.mEndValues, this.mStartValuesList, this.mEndValuesList);
        runAnimators();
    }

    @NonNull
    public Transition removeListener(@NonNull TransitionListener transitionListener) {
        ArrayList arrayList = this.mListeners;
        if (arrayList == null) {
            return this;
        }
        arrayList.remove(transitionListener);
        if (this.mListeners.size() == 0) {
            this.mListeners = null;
        }
        return this;
    }

    @NonNull
    public Transition removeTarget(@NonNull View view) {
        this.mTargets.remove(view);
        return this;
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void resume(View view) {
        if (this.mPaused) {
            if (!this.mEnded) {
                ArrayMap runningAnimators = getRunningAnimators();
                int size = runningAnimators.size();
                WindowIdImpl windowId = ViewUtils.getWindowId(view);
                for (size--; size >= 0; size--) {
                    AnimationInfo animationInfo = (AnimationInfo) runningAnimators.valueAt(size);
                    if (animationInfo.mView != null && windowId.equals(animationInfo.mWindowId)) {
                        AnimatorUtils.resume((Animator) runningAnimators.keyAt(size));
                    }
                }
                ArrayList arrayList = this.mListeners;
                if (arrayList != null && arrayList.size() > 0) {
                    arrayList = (ArrayList) this.mListeners.clone();
                    int size2 = arrayList.size();
                    for (size = 0; size < size2; size++) {
                        ((TransitionListener) arrayList.get(size)).onTransitionResume(this);
                    }
                }
            }
            this.mPaused = false;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void runAnimators() {
        start();
        ArrayMap runningAnimators = getRunningAnimators();
        Iterator it = this.mAnimators.iterator();
        while (it.hasNext()) {
            Animator animator = (Animator) it.next();
            if (runningAnimators.containsKey(animator)) {
                start();
                runAnimator(animator, runningAnimators);
            }
        }
        this.mAnimators.clear();
        end();
    }

    public void setCanRemoveViews(boolean z) {
        this.mCanRemoveViews = z;
    }

    @NonNull
    public Transition setDuration(long j) {
        this.mDuration = j;
        return this;
    }

    public void setEpicenterCallback(@Nullable EpicenterCallback epicenterCallback) {
        this.mEpicenterCallback = epicenterCallback;
    }

    @NonNull
    public Transition setInterpolator(@Nullable TimeInterpolator timeInterpolator) {
        this.mInterpolator = timeInterpolator;
        return this;
    }

    public void setMatchOrder(int... iArr) {
        if (iArr == null || iArr.length == 0) {
            this.mMatchOrder = DEFAULT_MATCH_ORDER;
            return;
        }
        int i = 0;
        while (i < iArr.length) {
            if (!isValidMatch(iArr[i])) {
                throw new IllegalArgumentException("matches contains invalid value");
            } else if (alreadyContains(iArr, i)) {
                throw new IllegalArgumentException("matches contains a duplicate value");
            } else {
                i++;
            }
        }
        this.mMatchOrder = (int[]) iArr.clone();
    }

    public void setPathMotion(@Nullable PathMotion pathMotion) {
        if (pathMotion == null) {
            this.mPathMotion = STRAIGHT_PATH_MOTION;
        } else {
            this.mPathMotion = pathMotion;
        }
    }

    public void setPropagation(@Nullable TransitionPropagation transitionPropagation) {
        this.mPropagation = transitionPropagation;
    }

    public Transition setSceneRoot(ViewGroup viewGroup) {
        this.mSceneRoot = viewGroup;
        return this;
    }

    @NonNull
    public Transition setStartDelay(long j) {
        this.mStartDelay = j;
        return this;
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void start() {
        if (this.mNumInstances == 0) {
            ArrayList arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                arrayList = (ArrayList) this.mListeners.clone();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ((TransitionListener) arrayList.get(i)).onTransitionStart(this);
                }
            }
            this.mEnded = false;
        }
        this.mNumInstances++;
    }

    public String toString() {
        return toString("");
    }

    @NonNull
    public Transition addTarget(@IdRes int i) {
        if (i != 0) {
            this.mTargetIds.add(Integer.valueOf(i));
        }
        return this;
    }

    public Transition clone() {
        try {
            Transition transition = (Transition) super.clone();
            transition.mAnimators = new ArrayList();
            transition.mStartValues = new TransitionValuesMaps();
            transition.mEndValues = new TransitionValuesMaps();
            transition.mStartValuesList = null;
            transition.mEndValuesList = null;
            return transition;
        } catch (CloneNotSupportedException unused) {
            return null;
        }
    }

    @NonNull
    public Transition excludeChildren(@IdRes int i, boolean z) {
        this.mTargetIdChildExcludes = excludeId(this.mTargetIdChildExcludes, i, z);
        return this;
    }

    @NonNull
    public Transition excludeTarget(@IdRes int i, boolean z) {
        this.mTargetIdExcludes = excludeId(this.mTargetIdExcludes, i, z);
        return this;
    }

    @NonNull
    public Transition removeTarget(@IdRes int i) {
        if (i != 0) {
            this.mTargetIds.remove(Integer.valueOf(i));
        }
        return this;
    }

    public String toString(String str) {
        StringBuilder L = a.L(str);
        L.append(getClass().getSimpleName());
        L.append("@");
        L.append(Integer.toHexString(hashCode()));
        L.append(": ");
        str = L.toString();
        String str2 = ") ";
        if (this.mDuration != -1) {
            str = a.z(a.P(str, "dur("), this.mDuration, str2);
        }
        if (this.mStartDelay != -1) {
            str = a.z(a.P(str, "dly("), this.mStartDelay, str2);
        }
        if (this.mInterpolator != null) {
            L = a.P(str, "interp(");
            L.append(this.mInterpolator);
            L.append(str2);
            str = L.toString();
        }
        if (this.mTargetIds.size() <= 0 && this.mTargets.size() <= 0) {
            return str;
        }
        str = a.t(str, "tgts(");
        String str3 = ", ";
        if (this.mTargetIds.size() > 0) {
            for (int i = 0; i < this.mTargetIds.size(); i++) {
                if (i > 0) {
                    str = a.t(str, str3);
                }
                L = a.L(str);
                L.append(this.mTargetIds.get(i));
                str = L.toString();
            }
        }
        if (this.mTargets.size() > 0) {
            for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                if (i2 > 0) {
                    str = a.t(str, str3);
                }
                L = a.L(str);
                L.append(this.mTargets.get(i2));
                str = L.toString();
            }
        }
        return a.t(str, ")");
    }

    @NonNull
    public Transition addTarget(@NonNull String str) {
        if (this.mTargetNames == null) {
            this.mTargetNames = new ArrayList();
        }
        this.mTargetNames.add(str);
        return this;
    }

    @NonNull
    public Transition excludeChildren(@NonNull Class<?> cls, boolean z) {
        this.mTargetTypeChildExcludes = excludeType(this.mTargetTypeChildExcludes, cls, z);
        return this;
    }

    @NonNull
    public Transition excludeTarget(@NonNull String str, boolean z) {
        this.mTargetNameExcludes = excludeObject(this.mTargetNameExcludes, str, z);
        return this;
    }

    @NonNull
    public Transition removeTarget(@NonNull String str) {
        ArrayList arrayList = this.mTargetNames;
        if (arrayList != null) {
            arrayList.remove(str);
        }
        return this;
    }

    @NonNull
    public Transition excludeTarget(@NonNull Class<?> cls, boolean z) {
        this.mTargetTypeExcludes = excludeType(this.mTargetTypeExcludes, cls, z);
        return this;
    }

    @NonNull
    public Transition removeTarget(@NonNull Class<?> cls) {
        ArrayList arrayList = this.mTargetTypes;
        if (arrayList != null) {
            arrayList.remove(cls);
        }
        return this;
    }

    @NonNull
    public Transition addTarget(@NonNull Class<?> cls) {
        if (this.mTargetTypes == null) {
            this.mTargetTypes = new ArrayList();
        }
        this.mTargetTypes.add(cls);
        return this;
    }

    @SuppressLint({"RestrictedApi"})
    public Transition(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Styleable.TRANSITION);
        XmlResourceParser xmlResourceParser = (XmlResourceParser) attributeSet;
        long namedInt = (long) TypedArrayUtils.getNamedInt(obtainStyledAttributes, xmlResourceParser, "duration", 1, -1);
        if (namedInt >= 0) {
            setDuration(namedInt);
        }
        namedInt = (long) TypedArrayUtils.getNamedInt(obtainStyledAttributes, xmlResourceParser, "startDelay", 2, -1);
        if (namedInt > 0) {
            setStartDelay(namedInt);
        }
        int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlResourceParser, "interpolator", 0, 0);
        if (namedResourceId > 0) {
            setInterpolator(AnimationUtils.loadInterpolator(context, namedResourceId));
        }
        String namedString = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlResourceParser, "matchOrder", 3);
        if (namedString != null) {
            setMatchOrder(parseMatchOrder(namedString));
        }
        obtainStyledAttributes.recycle();
    }
}
