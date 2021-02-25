package androidx.recyclerview.widget;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import f.d.b.a.a;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiffUtil {
    private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator<Snake>() {
        public int compare(Snake snake, Snake snake2) {
            int i = snake.x - snake2.x;
            return i == 0 ? snake.y - snake2.y : i;
        }
    };

    public static abstract class Callback {
        public abstract boolean areContentsTheSame(int i, int i2);

        public abstract boolean areItemsTheSame(int i, int i2);

        @Nullable
        public Object getChangePayload(int i, int i2) {
            return null;
        }

        public abstract int getNewListSize();

        public abstract int getOldListSize();
    }

    public static class DiffResult {
        private static final int FLAG_CHANGED = 2;
        private static final int FLAG_IGNORE = 16;
        private static final int FLAG_MASK = 31;
        private static final int FLAG_MOVED_CHANGED = 4;
        private static final int FLAG_MOVED_NOT_CHANGED = 8;
        private static final int FLAG_NOT_CHANGED = 1;
        private static final int FLAG_OFFSET = 5;
        public static final int NO_POSITION = -1;
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;
        private final List<Snake> mSnakes;

        public DiffResult(Callback callback, List<Snake> list, int[] iArr, int[] iArr2, boolean z) {
            this.mSnakes = list;
            this.mOldItemStatuses = iArr;
            this.mNewItemStatuses = iArr2;
            Arrays.fill(iArr, 0);
            Arrays.fill(iArr2, 0);
            this.mCallback = callback;
            this.mOldListSize = callback.getOldListSize();
            this.mNewListSize = callback.getNewListSize();
            this.mDetectMoves = z;
            addRootSnake();
            findMatchingItems();
        }

        private void addRootSnake() {
            Snake snake = this.mSnakes.isEmpty() ? null : (Snake) this.mSnakes.get(0);
            if (snake == null || snake.x != 0 || snake.y != 0) {
                snake = new Snake();
                snake.x = 0;
                snake.y = 0;
                snake.removal = false;
                snake.size = 0;
                snake.reverse = false;
                this.mSnakes.add(0, snake);
            }
        }

        private void dispatchAdditions(List<PostponedUpdate> list, ListUpdateCallback listUpdateCallback, int i, int i2, int i3) {
            if (this.mDetectMoves) {
                for (i2--; i2 >= 0; i2--) {
                    int[] iArr = this.mNewItemStatuses;
                    int i4 = i3 + i2;
                    int i5 = iArr[i4] & 31;
                    if (i5 == 0) {
                        listUpdateCallback.onInserted(i, 1);
                        for (PostponedUpdate postponedUpdate : list) {
                            postponedUpdate.currentPos++;
                        }
                    } else if (i5 == 4 || i5 == 8) {
                        int i6 = iArr[i4] >> 5;
                        listUpdateCallback.onMoved(removePostponedUpdate(list, i6, true).currentPos, i);
                        if (i5 == 4) {
                            listUpdateCallback.onChanged(i, 1, this.mCallback.getChangePayload(i6, i4));
                        }
                    } else if (i5 == 16) {
                        list.add(new PostponedUpdate(i4, i, false));
                    } else {
                        StringBuilder M = a.M("unknown flag for pos ", i4, " ");
                        M.append(Long.toBinaryString((long) i5));
                        throw new IllegalStateException(M.toString());
                    }
                }
                return;
            }
            listUpdateCallback.onInserted(i, i2);
        }

        private void dispatchRemovals(List<PostponedUpdate> list, ListUpdateCallback listUpdateCallback, int i, int i2, int i3) {
            if (this.mDetectMoves) {
                for (i2--; i2 >= 0; i2--) {
                    int[] iArr = this.mOldItemStatuses;
                    int i4 = i3 + i2;
                    int i5 = iArr[i4] & 31;
                    if (i5 == 0) {
                        listUpdateCallback.onRemoved(i + i2, 1);
                        for (PostponedUpdate postponedUpdate : list) {
                            postponedUpdate.currentPos--;
                        }
                    } else if (i5 == 4 || i5 == 8) {
                        int i6 = iArr[i4] >> 5;
                        PostponedUpdate removePostponedUpdate = removePostponedUpdate(list, i6, false);
                        listUpdateCallback.onMoved(i + i2, removePostponedUpdate.currentPos - 1);
                        if (i5 == 4) {
                            listUpdateCallback.onChanged(removePostponedUpdate.currentPos - 1, 1, this.mCallback.getChangePayload(i4, i6));
                        }
                    } else if (i5 == 16) {
                        list.add(new PostponedUpdate(i4, i + i2, true));
                    } else {
                        StringBuilder M = a.M("unknown flag for pos ", i4, " ");
                        M.append(Long.toBinaryString((long) i5));
                        throw new IllegalStateException(M.toString());
                    }
                }
                return;
            }
            listUpdateCallback.onRemoved(i, i2);
        }

        private void findAddition(int i, int i2, int i3) {
            if (this.mOldItemStatuses[i - 1] == 0) {
                findMatchingItem(i, i2, i3, false);
            }
        }

        private boolean findMatchingItem(int i, int i2, int i3, boolean z) {
            int i4;
            int i5;
            if (z) {
                i2--;
                i4 = i;
                i5 = i2;
            } else {
                i5 = i - 1;
                i4 = i5;
            }
            while (i3 >= 0) {
                Snake snake = (Snake) this.mSnakes.get(i3);
                int i6 = snake.x;
                int i7 = snake.size;
                i6 += i7;
                int i8 = snake.y + i7;
                i7 = 8;
                if (z) {
                    for (i4--; i4 >= i6; i4--) {
                        if (this.mCallback.areItemsTheSame(i4, i5)) {
                            if (!this.mCallback.areContentsTheSame(i4, i5)) {
                                i7 = 4;
                            }
                            this.mNewItemStatuses[i5] = (i4 << 5) | 16;
                            this.mOldItemStatuses[i4] = (i5 << 5) | i7;
                            return true;
                        }
                    }
                    continue;
                } else {
                    for (i2--; i2 >= i8; i2--) {
                        if (this.mCallback.areItemsTheSame(i5, i2)) {
                            if (!this.mCallback.areContentsTheSame(i5, i2)) {
                                i7 = 4;
                            }
                            i--;
                            this.mOldItemStatuses[i] = (i2 << 5) | 16;
                            this.mNewItemStatuses[i2] = (i << 5) | i7;
                            return true;
                        }
                    }
                    continue;
                }
                i4 = snake.x;
                i2 = snake.y;
                i3--;
            }
            return false;
        }

        private void findMatchingItems() {
            int i = this.mOldListSize;
            int i2 = this.mNewListSize;
            for (int size = this.mSnakes.size() - 1; size >= 0; size--) {
                Snake snake = (Snake) this.mSnakes.get(size);
                int i3 = snake.x;
                int i4 = snake.size;
                i3 += i4;
                int i5 = snake.y + i4;
                if (this.mDetectMoves) {
                    while (i > i3) {
                        findAddition(i, i2, size);
                        i--;
                    }
                    while (i2 > i5) {
                        findRemoval(i, i2, size);
                        i2--;
                    }
                }
                for (i = 0; i < snake.size; i++) {
                    i2 = snake.x + i;
                    i3 = snake.y + i;
                    i4 = this.mCallback.areContentsTheSame(i2, i3) ? 1 : 2;
                    this.mOldItemStatuses[i2] = (i3 << 5) | i4;
                    this.mNewItemStatuses[i3] = (i2 << 5) | i4;
                }
                i = snake.x;
                i2 = snake.y;
            }
        }

        private void findRemoval(int i, int i2, int i3) {
            if (this.mNewItemStatuses[i2 - 1] == 0) {
                findMatchingItem(i, i2, i3, true);
            }
        }

        private static PostponedUpdate removePostponedUpdate(List<PostponedUpdate> list, int i, boolean z) {
            int size = list.size() - 1;
            while (size >= 0) {
                PostponedUpdate postponedUpdate = (PostponedUpdate) list.get(size);
                if (postponedUpdate.posInOwnerList == i && postponedUpdate.removal == z) {
                    list.remove(size);
                    while (size < list.size()) {
                        PostponedUpdate postponedUpdate2 = (PostponedUpdate) list.get(size);
                        postponedUpdate2.currentPos += z ? 1 : -1;
                        size++;
                    }
                    return postponedUpdate;
                }
                size--;
            }
            return null;
        }

        public int convertNewPositionToOld(@IntRange(from = 0) int i) {
            if (i < 0 || i >= this.mNewListSize) {
                StringBuilder M = a.M("Index out of bounds - passed position = ", i, ", new list size = ");
                M.append(this.mNewListSize);
                throw new IndexOutOfBoundsException(M.toString());
            }
            i = this.mNewItemStatuses[i];
            return (i & 31) == 0 ? -1 : i >> 5;
        }

        public int convertOldPositionToNew(@IntRange(from = 0) int i) {
            if (i < 0 || i >= this.mOldListSize) {
                StringBuilder M = a.M("Index out of bounds - passed position = ", i, ", old list size = ");
                M.append(this.mOldListSize);
                throw new IndexOutOfBoundsException(M.toString());
            }
            i = this.mOldItemStatuses[i];
            return (i & 31) == 0 ? -1 : i >> 5;
        }

        public void dispatchUpdatesTo(@NonNull Adapter adapter) {
            dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }

        @VisibleForTesting
        public List<Snake> getSnakes() {
            return this.mSnakes;
        }

        public void dispatchUpdatesTo(@NonNull ListUpdateCallback listUpdateCallback) {
            BatchingListUpdateCallback batchingListUpdateCallback;
            if (listUpdateCallback instanceof BatchingListUpdateCallback) {
                batchingListUpdateCallback = (BatchingListUpdateCallback) listUpdateCallback;
            } else {
                batchingListUpdateCallback = new BatchingListUpdateCallback(listUpdateCallback);
            }
            ArrayList arrayList = new ArrayList();
            int i = this.mOldListSize;
            int i2 = this.mNewListSize;
            for (int size = this.mSnakes.size() - 1; size >= 0; size--) {
                Snake snake = (Snake) this.mSnakes.get(size);
                int i3 = snake.size;
                int i4 = snake.x + i3;
                int i5 = snake.y + i3;
                if (i4 < i) {
                    dispatchRemovals(arrayList, batchingListUpdateCallback, i4, i - i4, i4);
                }
                if (i5 < i2) {
                    dispatchAdditions(arrayList, batchingListUpdateCallback, i4, i2 - i5, i5);
                }
                for (i3--; i3 >= 0; i3--) {
                    int[] iArr = this.mOldItemStatuses;
                    int i6 = snake.x;
                    if ((iArr[i6 + i3] & 31) == 2) {
                        batchingListUpdateCallback.onChanged(i6 + i3, 1, this.mCallback.getChangePayload(i6 + i3, snake.y + i3));
                    }
                }
                i = snake.x;
                i2 = snake.y;
            }
            batchingListUpdateCallback.dispatchLastEvent();
        }
    }

    public static abstract class ItemCallback<T> {
        public abstract boolean areContentsTheSame(@NonNull T t, @NonNull T t2);

        public abstract boolean areItemsTheSame(@NonNull T t, @NonNull T t2);

        @Nullable
        public Object getChangePayload(@NonNull T t, @NonNull T t2) {
            return null;
        }
    }

    public static class PostponedUpdate {
        public int currentPos;
        public int posInOwnerList;
        public boolean removal;

        public PostponedUpdate(int i, int i2, boolean z) {
            this.posInOwnerList = i;
            this.currentPos = i2;
            this.removal = z;
        }
    }

    public static class Range {
        public int newListEnd;
        public int newListStart;
        public int oldListEnd;
        public int oldListStart;

        public Range(int i, int i2, int i3, int i4) {
            this.oldListStart = i;
            this.oldListEnd = i2;
            this.newListStart = i3;
            this.newListEnd = i4;
        }
    }

    public static class Snake {
        public boolean removal;
        public boolean reverse;
        public int size;
        public int x;
        public int y;
    }

    private DiffUtil() {
    }

    @NonNull
    public static DiffResult calculateDiff(@NonNull Callback callback) {
        return calculateDiff(callback, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:84:0x00ec A:{SYNTHETIC, EDGE_INSN: B:84:0x00ec->B:53:0x00ec ?: BREAK  , EDGE_INSN: B:84:0x00ec->B:53:0x00ec ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e1 A:{LOOP_END, LOOP:4: B:47:0x00cd->B:51:0x00e1} */
    /* JADX WARNING: Missing block: B:14:0x0042, code skipped:
            if (r1[r13 - 1] < r1[r13 + r5]) goto L_0x004d;
     */
    /* JADX WARNING: Missing block: B:41:0x00b8, code skipped:
            if (r2[r12 - 1] < r2[r12 + 1]) goto L_0x00c5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static Snake diffPartial(Callback callback, int i, int i2, int i3, int i4, int[] iArr, int[] iArr2, int i5) {
        Callback callback2 = callback;
        int[] iArr3 = iArr;
        int[] iArr4 = iArr2;
        int i6 = i2 - i;
        int i7 = i4 - i3;
        int i8 = 1;
        if (i6 < 1 || i7 < 1) {
            return null;
        }
        int i9 = i6 - i7;
        int i10 = ((i6 + i7) + 1) / 2;
        int i11 = (i5 - i10) - 1;
        int i12 = (i5 + i10) + 1;
        Arrays.fill(iArr3, i11, i12, 0);
        Arrays.fill(iArr4, i11 + i9, i12 + i9, i6);
        Object obj = i9 % 2 != 0 ? 1 : null;
        i12 = 0;
        while (i12 <= i10) {
            boolean z;
            int i13;
            Snake snake;
            int i14 = -i12;
            int i15 = i14;
            while (i15 <= i12) {
                int i16;
                if (i15 != i14) {
                    if (i15 != i12) {
                        i16 = i5 + i15;
                    }
                    i16 = iArr3[(i5 + i15) - i8] + i8;
                    z = true;
                    i13 = i16 - i15;
                    while (i16 < i6 && i13 < i7 && callback2.areItemsTheSame(i + i16, i3 + i13)) {
                        i16++;
                        i13++;
                    }
                    i8 = i5 + i15;
                    iArr3[i8] = i16;
                    if (obj != null || i15 < (i9 - i12) + 1 || i15 > (i9 + i12) - 1 || iArr3[i8] < iArr4[i8]) {
                        i15 += 2;
                        i8 = 1;
                    } else {
                        snake = new Snake();
                        i6 = iArr4[i8];
                        snake.x = i6;
                        snake.y = i6 - i15;
                        snake.size = iArr3[i8] - iArr4[i8];
                        snake.removal = z;
                        snake.reverse = false;
                        return snake;
                    }
                }
                i16 = iArr3[(i5 + i15) + i8];
                z = false;
                i13 = i16 - i15;
                while (i16 < i6) {
                    i16++;
                    i13++;
                }
                i8 = i5 + i15;
                iArr3[i8] = i16;
                if (obj != null) {
                }
                i15 += 2;
                i8 = 1;
            }
            i8 = i14;
            while (i8 <= i12) {
                int i17;
                int i18;
                int i19 = i8 + i9;
                if (i19 != i12 + i9) {
                    if (i19 != i14 + i9) {
                        i15 = i5 + i19;
                        i13 = 1;
                    } else {
                        i13 = 1;
                    }
                    i15 = iArr4[(i5 + i19) + i13] - i13;
                    z = true;
                    i17 = i15 - i19;
                    while (i15 > 0 && i17 > 0) {
                        i18 = i6;
                        if (callback2.areItemsTheSame((i + i15) - 1, (i3 + i17) - 1)) {
                            break;
                        }
                        i15--;
                        i17--;
                        i6 = i18;
                    }
                    i18 = i6;
                    i6 = i5 + i19;
                    iArr4[i6] = i15;
                    if (obj == null || i19 < i14 || i19 > i12 || iArr3[i6] < iArr4[i6]) {
                        i8 += 2;
                        i6 = i18;
                    } else {
                        snake = new Snake();
                        i7 = iArr4[i6];
                        snake.x = i7;
                        snake.y = i7 - i19;
                        snake.size = iArr3[i6] - iArr4[i6];
                        snake.removal = z;
                        snake.reverse = true;
                        return snake;
                    }
                }
                i13 = 1;
                i15 = iArr4[(i5 + i19) - i13];
                z = false;
                i17 = i15 - i19;
                while (i15 > 0) {
                    i18 = i6;
                    if (callback2.areItemsTheSame((i + i15) - 1, (i3 + i17) - 1)) {
                    }
                }
                i18 = i6;
                i6 = i5 + i19;
                iArr4[i6] = i15;
                if (obj == null) {
                }
                i8 += 2;
                i6 = i18;
            }
            i12++;
            i6 = i6;
            i8 = 1;
        }
        throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
    }

    @NonNull
    public static DiffResult calculateDiff(@NonNull Callback callback, boolean z) {
        int oldListSize = callback.getOldListSize();
        int newListSize = callback.getNewListSize();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new Range(0, oldListSize, 0, newListSize));
        oldListSize = Math.abs(oldListSize - newListSize) + (oldListSize + newListSize);
        newListSize = oldListSize * 2;
        int[] iArr = new int[newListSize];
        int[] iArr2 = new int[newListSize];
        ArrayList arrayList3 = new ArrayList();
        while (!arrayList2.isEmpty()) {
            Range range = (Range) arrayList2.remove(arrayList2.size() - 1);
            Snake diffPartial = diffPartial(callback, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, iArr, iArr2, oldListSize);
            if (diffPartial != null) {
                Range range2;
                if (diffPartial.size > 0) {
                    arrayList.add(diffPartial);
                }
                diffPartial.x += range.oldListStart;
                diffPartial.y += range.newListStart;
                if (arrayList3.isEmpty()) {
                    range2 = new Range();
                } else {
                    range2 = (Range) arrayList3.remove(arrayList3.size() - 1);
                }
                range2.oldListStart = range.oldListStart;
                range2.newListStart = range.newListStart;
                if (diffPartial.reverse) {
                    range2.oldListEnd = diffPartial.x;
                    range2.newListEnd = diffPartial.y;
                } else if (diffPartial.removal) {
                    range2.oldListEnd = diffPartial.x - 1;
                    range2.newListEnd = diffPartial.y;
                } else {
                    range2.oldListEnd = diffPartial.x;
                    range2.newListEnd = diffPartial.y - 1;
                }
                arrayList2.add(range2);
                int i;
                int i2;
                if (!diffPartial.reverse) {
                    i = diffPartial.x;
                    i2 = diffPartial.size;
                    range.oldListStart = i + i2;
                    range.newListStart = diffPartial.y + i2;
                } else if (diffPartial.removal) {
                    i = diffPartial.x;
                    i2 = diffPartial.size;
                    range.oldListStart = (i + i2) + 1;
                    range.newListStart = diffPartial.y + i2;
                } else {
                    i = diffPartial.x;
                    i2 = diffPartial.size;
                    range.oldListStart = i + i2;
                    range.newListStart = (diffPartial.y + i2) + 1;
                }
                arrayList2.add(range);
            } else {
                arrayList3.add(range);
            }
        }
        Collections.sort(arrayList, SNAKE_COMPARATOR);
        return new DiffResult(callback, arrayList, iArr, iArr2, z);
    }
}
