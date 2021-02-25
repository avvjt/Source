package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import f.d.b.a.a;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class SortedList<T> {
    private static final int CAPACITY_GROWTH = 10;
    private static final int DELETION = 2;
    private static final int INSERTION = 1;
    public static final int INVALID_POSITION = -1;
    private static final int LOOKUP = 4;
    private static final int MIN_CAPACITY = 10;
    private BatchedCallback mBatchedCallback;
    private Callback mCallback;
    public T[] mData;
    private int mNewDataStart;
    private T[] mOldData;
    private int mOldDataSize;
    private int mOldDataStart;
    private int mSize;
    private final Class<T> mTClass;

    public static abstract class Callback<T2> implements Comparator<T2>, ListUpdateCallback {
        public abstract boolean areContentsTheSame(T2 t2, T2 t22);

        public abstract boolean areItemsTheSame(T2 t2, T2 t22);

        public abstract int compare(T2 t2, T2 t22);

        @Nullable
        public Object getChangePayload(T2 t2, T2 t22) {
            return null;
        }

        public abstract void onChanged(int i, int i2);

        public void onChanged(int i, int i2, Object obj) {
            onChanged(i, i2);
        }
    }

    public static class BatchedCallback<T2> extends Callback<T2> {
        private final BatchingListUpdateCallback mBatchingListUpdateCallback;
        public final Callback<T2> mWrappedCallback;

        public BatchedCallback(Callback<T2> callback) {
            this.mWrappedCallback = callback;
            this.mBatchingListUpdateCallback = new BatchingListUpdateCallback(callback);
        }

        public boolean areContentsTheSame(T2 t2, T2 t22) {
            return this.mWrappedCallback.areContentsTheSame(t2, t22);
        }

        public boolean areItemsTheSame(T2 t2, T2 t22) {
            return this.mWrappedCallback.areItemsTheSame(t2, t22);
        }

        public int compare(T2 t2, T2 t22) {
            return this.mWrappedCallback.compare(t2, t22);
        }

        public void dispatchLastEvent() {
            this.mBatchingListUpdateCallback.dispatchLastEvent();
        }

        @Nullable
        public Object getChangePayload(T2 t2, T2 t22) {
            return this.mWrappedCallback.getChangePayload(t2, t22);
        }

        public void onChanged(int i, int i2) {
            this.mBatchingListUpdateCallback.onChanged(i, i2, null);
        }

        public void onInserted(int i, int i2) {
            this.mBatchingListUpdateCallback.onInserted(i, i2);
        }

        public void onMoved(int i, int i2) {
            this.mBatchingListUpdateCallback.onMoved(i, i2);
        }

        public void onRemoved(int i, int i2) {
            this.mBatchingListUpdateCallback.onRemoved(i, i2);
        }

        public void onChanged(int i, int i2, Object obj) {
            this.mBatchingListUpdateCallback.onChanged(i, i2, obj);
        }
    }

    public SortedList(@NonNull Class<T> cls, @NonNull Callback<T> callback) {
        this(cls, callback, 10);
    }

    private void addAllInternal(T[] tArr) {
        if (tArr.length >= 1) {
            int sortAndDedup = sortAndDedup(tArr);
            if (this.mSize == 0) {
                this.mData = tArr;
                this.mSize = sortAndDedup;
                this.mCallback.onInserted(0, sortAndDedup);
            } else {
                merge(tArr, sortAndDedup);
            }
        }
    }

    private void addToData(int i, T t) {
        int i2 = this.mSize;
        if (i <= i2) {
            Object[] objArr = this.mData;
            if (i2 == objArr.length) {
                Object[] objArr2 = (Object[]) Array.newInstance(this.mTClass, objArr.length + 10);
                System.arraycopy(this.mData, 0, objArr2, 0, i);
                objArr2[i] = t;
                System.arraycopy(this.mData, i, objArr2, i + 1, this.mSize - i);
                this.mData = objArr2;
            } else {
                System.arraycopy(objArr, i, objArr, i + 1, i2 - i);
                this.mData[i] = t;
            }
            this.mSize++;
            return;
        }
        StringBuilder M = a.M("cannot add item to ", i, " because size is ");
        M.append(this.mSize);
        throw new IndexOutOfBoundsException(M.toString());
    }

    private T[] copyArray(T[] tArr) {
        Object[] objArr = (Object[]) Array.newInstance(this.mTClass, tArr.length);
        System.arraycopy(tArr, 0, objArr, 0, tArr.length);
        return objArr;
    }

    private int findIndexOf(T t, T[] tArr, int i, int i2, int i3) {
        while (i < i2) {
            int i4 = (i + i2) / 2;
            Object obj = tArr[i4];
            int compare = this.mCallback.compare(obj, t);
            if (compare < 0) {
                i = i4 + 1;
            } else if (compare != 0) {
                i2 = i4;
            } else if (this.mCallback.areItemsTheSame(obj, t)) {
                return i4;
            } else {
                int linearEqualitySearch = linearEqualitySearch(t, i4, i, i2);
                if (i3 != 1) {
                    return linearEqualitySearch;
                }
                if (linearEqualitySearch != -1) {
                    i4 = linearEqualitySearch;
                }
                return i4;
            }
        }
        if (i3 != 1) {
            i = -1;
        }
        return i;
    }

    private int findSameItem(T t, T[] tArr, int i, int i2) {
        while (i < i2) {
            if (this.mCallback.areItemsTheSame(tArr[i], t)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private int linearEqualitySearch(T t, int i, int i2, int i3) {
        int i4 = i - 1;
        while (i4 >= i2) {
            Object obj = this.mData[i4];
            if (this.mCallback.compare(obj, t) != 0) {
                break;
            } else if (this.mCallback.areItemsTheSame(obj, t)) {
                return i4;
            } else {
                i4--;
            }
        }
        Object obj2;
        do {
            i++;
            if (i < i3) {
                obj2 = this.mData[i];
                if (this.mCallback.compare(obj2, t) != 0) {
                }
            }
            return -1;
        } while (!this.mCallback.areItemsTheSame(obj2, t));
        return i;
    }

    private void merge(T[] tArr, int i) {
        int i2 = (this.mCallback instanceof BatchedCallback) ^ 1;
        if (i2 != 0) {
            beginBatchedUpdates();
        }
        this.mOldData = this.mData;
        int i3 = 0;
        this.mOldDataStart = 0;
        int i4 = this.mSize;
        this.mOldDataSize = i4;
        this.mData = (Object[]) Array.newInstance(this.mTClass, (i4 + i) + 10);
        this.mNewDataStart = 0;
        while (true) {
            i4 = this.mOldDataStart;
            int i5 = this.mOldDataSize;
            if (i4 >= i5 && i3 >= i) {
                break;
            } else if (i4 == i5) {
                i -= i3;
                System.arraycopy(tArr, i3, this.mData, this.mNewDataStart, i);
                int i6 = this.mNewDataStart + i;
                this.mNewDataStart = i6;
                this.mSize += i;
                this.mCallback.onInserted(i6 - i, i);
                break;
            } else if (i3 == i) {
                i5 -= i4;
                System.arraycopy(this.mOldData, i4, this.mData, this.mNewDataStart, i5);
                this.mNewDataStart += i5;
                break;
            } else {
                Object obj = this.mOldData[i4];
                Object obj2 = tArr[i3];
                int compare = this.mCallback.compare(obj, obj2);
                int i7;
                if (compare > 0) {
                    Object[] objArr = this.mData;
                    compare = this.mNewDataStart;
                    i7 = compare + 1;
                    this.mNewDataStart = i7;
                    objArr[compare] = obj2;
                    this.mSize++;
                    i3++;
                    this.mCallback.onInserted(i7 - 1, 1);
                } else if (compare == 0 && this.mCallback.areItemsTheSame(obj, obj2)) {
                    Object[] objArr2 = this.mData;
                    i7 = this.mNewDataStart;
                    this.mNewDataStart = i7 + 1;
                    objArr2[i7] = obj2;
                    i3++;
                    this.mOldDataStart++;
                    if (!this.mCallback.areContentsTheSame(obj, obj2)) {
                        Callback callback = this.mCallback;
                        callback.onChanged(this.mNewDataStart - 1, 1, callback.getChangePayload(obj, obj2));
                    }
                } else {
                    Object[] objArr3 = this.mData;
                    compare = this.mNewDataStart;
                    this.mNewDataStart = compare + 1;
                    objArr3[compare] = obj;
                    this.mOldDataStart++;
                }
            }
        }
        this.mOldData = null;
        if (i2 != 0) {
            endBatchedUpdates();
        }
    }

    private void removeItemAtIndex(int i, boolean z) {
        Object[] objArr = this.mData;
        System.arraycopy(objArr, i + 1, objArr, i, (this.mSize - i) - 1);
        int i2 = this.mSize - 1;
        this.mSize = i2;
        this.mData[i2] = null;
        if (z) {
            this.mCallback.onRemoved(i, 1);
        }
    }

    private void replaceAllInsert(T t) {
        Object[] objArr = this.mData;
        int i = this.mNewDataStart;
        objArr[i] = t;
        i++;
        this.mNewDataStart = i;
        this.mSize++;
        this.mCallback.onInserted(i - 1, 1);
    }

    private void replaceAllInternal(@NonNull T[] tArr) {
        int i = (this.mCallback instanceof BatchedCallback) ^ 1;
        if (i != 0) {
            beginBatchedUpdates();
        }
        this.mOldDataStart = 0;
        this.mOldDataSize = this.mSize;
        this.mOldData = this.mData;
        this.mNewDataStart = 0;
        int sortAndDedup = sortAndDedup(tArr);
        this.mData = (Object[]) Array.newInstance(this.mTClass, sortAndDedup);
        while (true) {
            int i2 = this.mNewDataStart;
            if (i2 >= sortAndDedup && this.mOldDataStart >= this.mOldDataSize) {
                break;
            }
            int i3 = this.mOldDataStart;
            int i4 = this.mOldDataSize;
            if (i3 >= i4) {
                sortAndDedup -= i2;
                System.arraycopy(tArr, i2, this.mData, i2, sortAndDedup);
                this.mNewDataStart += sortAndDedup;
                this.mSize += sortAndDedup;
                this.mCallback.onInserted(i2, sortAndDedup);
                break;
            } else if (i2 >= sortAndDedup) {
                i4 -= i3;
                this.mSize -= i4;
                this.mCallback.onRemoved(i2, i4);
                break;
            } else {
                Object obj = this.mOldData[i3];
                Object obj2 = tArr[i2];
                i4 = this.mCallback.compare(obj, obj2);
                if (i4 < 0) {
                    replaceAllRemove();
                } else if (i4 > 0) {
                    replaceAllInsert(obj2);
                } else if (this.mCallback.areItemsTheSame(obj, obj2)) {
                    Object[] objArr = this.mData;
                    int i5 = this.mNewDataStart;
                    objArr[i5] = obj2;
                    this.mOldDataStart++;
                    this.mNewDataStart = i5 + 1;
                    if (!this.mCallback.areContentsTheSame(obj, obj2)) {
                        Callback callback = this.mCallback;
                        callback.onChanged(this.mNewDataStart - 1, 1, callback.getChangePayload(obj, obj2));
                    }
                } else {
                    replaceAllRemove();
                    replaceAllInsert(obj2);
                }
            }
        }
        this.mOldData = null;
        if (i != 0) {
            endBatchedUpdates();
        }
    }

    private void replaceAllRemove() {
        this.mSize--;
        this.mOldDataStart++;
        this.mCallback.onRemoved(this.mNewDataStart, 1);
    }

    private int sortAndDedup(@NonNull T[] tArr) {
        if (tArr.length == 0) {
            return 0;
        }
        Arrays.sort(tArr, this.mCallback);
        int i = 1;
        int i2 = 0;
        for (int i3 = 1; i3 < tArr.length; i3++) {
            Object obj = tArr[i3];
            if (this.mCallback.compare(tArr[i2], obj) == 0) {
                int findSameItem = findSameItem(obj, tArr, i2, i);
                if (findSameItem != -1) {
                    tArr[findSameItem] = obj;
                } else {
                    if (i != i3) {
                        tArr[i] = obj;
                    }
                    i++;
                }
            } else {
                if (i != i3) {
                    tArr[i] = obj;
                }
                i2 = i;
                i++;
            }
        }
        return i;
    }

    private void throwIfInMutationOperation() {
        if (this.mOldData != null) {
            throw new IllegalStateException("Data cannot be mutated in the middle of a batch update operation such as addAll or replaceAll.");
        }
    }

    public int add(T t) {
        throwIfInMutationOperation();
        return add(t, true);
    }

    public void addAll(@NonNull T[] tArr, boolean z) {
        throwIfInMutationOperation();
        if (tArr.length != 0) {
            if (z) {
                addAllInternal(tArr);
            } else {
                addAllInternal(copyArray(tArr));
            }
        }
    }

    public void beginBatchedUpdates() {
        throwIfInMutationOperation();
        Callback callback = this.mCallback;
        if (!(callback instanceof BatchedCallback)) {
            if (this.mBatchedCallback == null) {
                this.mBatchedCallback = new BatchedCallback(callback);
            }
            this.mCallback = this.mBatchedCallback;
        }
    }

    public void clear() {
        throwIfInMutationOperation();
        int i = this.mSize;
        if (i != 0) {
            Arrays.fill(this.mData, 0, i, null);
            this.mSize = 0;
            this.mCallback.onRemoved(0, i);
        }
    }

    public void endBatchedUpdates() {
        throwIfInMutationOperation();
        Callback callback = this.mCallback;
        if (callback instanceof BatchedCallback) {
            ((BatchedCallback) callback).dispatchLastEvent();
        }
        callback = this.mCallback;
        Callback callback2 = this.mBatchedCallback;
        if (callback == callback2) {
            this.mCallback = callback2.mWrappedCallback;
        }
    }

    public T get(int i) throws IndexOutOfBoundsException {
        if (i >= this.mSize || i < 0) {
            StringBuilder M = a.M("Asked to get item at ", i, " but size is ");
            M.append(this.mSize);
            throw new IndexOutOfBoundsException(M.toString());
        }
        Object[] objArr = this.mOldData;
        if (objArr != null) {
            int i2 = this.mNewDataStart;
            if (i >= i2) {
                return objArr[(i - i2) + this.mOldDataStart];
            }
        }
        return this.mData[i];
    }

    public int indexOf(T t) {
        if (this.mOldData != null) {
            int findIndexOf = findIndexOf(t, this.mData, 0, this.mNewDataStart, 4);
            if (findIndexOf != -1) {
                return findIndexOf;
            }
            int findIndexOf2 = findIndexOf(t, this.mOldData, this.mOldDataStart, this.mOldDataSize, 4);
            if (findIndexOf2 != -1) {
                return (findIndexOf2 - this.mOldDataStart) + this.mNewDataStart;
            }
            return -1;
        }
        return findIndexOf(t, this.mData, 0, this.mSize, 4);
    }

    public void recalculatePositionOfItemAt(int i) {
        throwIfInMutationOperation();
        Object obj = get(i);
        removeItemAtIndex(i, false);
        int add = add(obj, false);
        if (i != add) {
            this.mCallback.onMoved(i, add);
        }
    }

    public boolean remove(T t) {
        throwIfInMutationOperation();
        return remove(t, true);
    }

    public T removeItemAt(int i) {
        throwIfInMutationOperation();
        Object obj = get(i);
        removeItemAtIndex(i, true);
        return obj;
    }

    public void replaceAll(@NonNull T[] tArr, boolean z) {
        throwIfInMutationOperation();
        if (z) {
            replaceAllInternal(tArr);
        } else {
            replaceAllInternal(copyArray(tArr));
        }
    }

    public int size() {
        return this.mSize;
    }

    public void updateItemAt(int i, T t) {
        throwIfInMutationOperation();
        T t2 = get(i);
        Object obj = (t2 == t || !this.mCallback.areContentsTheSame(t2, t)) ? 1 : null;
        if (t2 == t || this.mCallback.compare(t2, t) != 0) {
            if (obj != null) {
                Callback callback = this.mCallback;
                callback.onChanged(i, 1, callback.getChangePayload(t2, t));
            }
            removeItemAtIndex(i, false);
            int add = add(t, false);
            if (i != add) {
                this.mCallback.onMoved(i, add);
            }
            return;
        }
        this.mData[i] = t;
        if (obj != null) {
            Callback callback2 = this.mCallback;
            callback2.onChanged(i, 1, callback2.getChangePayload(t2, t));
        }
    }

    public SortedList(@NonNull Class<T> cls, @NonNull Callback<T> callback, int i) {
        this.mTClass = cls;
        this.mData = (Object[]) Array.newInstance(cls, i);
        this.mCallback = callback;
        this.mSize = 0;
    }

    private int add(T t, boolean z) {
        int findIndexOf = findIndexOf(t, this.mData, 0, this.mSize, 1);
        if (findIndexOf == -1) {
            findIndexOf = 0;
        } else if (findIndexOf < this.mSize) {
            Object obj = this.mData[findIndexOf];
            if (this.mCallback.areItemsTheSame(obj, t)) {
                if (this.mCallback.areContentsTheSame(obj, t)) {
                    this.mData[findIndexOf] = t;
                    return findIndexOf;
                }
                this.mData[findIndexOf] = t;
                Callback callback = this.mCallback;
                callback.onChanged(findIndexOf, 1, callback.getChangePayload(obj, t));
                return findIndexOf;
            }
        }
        addToData(findIndexOf, t);
        if (z) {
            this.mCallback.onInserted(findIndexOf, 1);
        }
        return findIndexOf;
    }

    private boolean remove(T t, boolean z) {
        int findIndexOf = findIndexOf(t, this.mData, 0, this.mSize, 2);
        if (findIndexOf == -1) {
            return false;
        }
        removeItemAtIndex(findIndexOf, z);
        return true;
    }

    public void replaceAll(@NonNull T... tArr) {
        replaceAll(tArr, false);
    }

    public void addAll(@NonNull T... tArr) {
        addAll(tArr, false);
    }

    public void replaceAll(@NonNull Collection<T> collection) {
        replaceAll(collection.toArray((Object[]) Array.newInstance(this.mTClass, collection.size())), true);
    }

    public void addAll(@NonNull Collection<T> collection) {
        addAll(collection.toArray((Object[]) Array.newInstance(this.mTClass, collection.size())), true);
    }
}
