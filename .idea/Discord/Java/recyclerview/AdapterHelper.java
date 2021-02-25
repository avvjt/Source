package androidx.recyclerview.widget;

import androidx.core.util.Pools.Pool;
import androidx.core.util.Pools.SimplePool;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterHelper implements androidx.recyclerview.widget.OpReorderer.Callback {
    private static final boolean DEBUG = false;
    public static final int POSITION_TYPE_INVISIBLE = 0;
    public static final int POSITION_TYPE_NEW_OR_LAID_OUT = 1;
    private static final String TAG = "AHT";
    public final Callback mCallback;
    public final boolean mDisableRecycler;
    private int mExistingUpdateTypes;
    public Runnable mOnItemProcessedCallback;
    public final OpReorderer mOpReorderer;
    public final ArrayList<UpdateOp> mPendingUpdates;
    public final ArrayList<UpdateOp> mPostponedList;
    private Pool<UpdateOp> mUpdateOpPool;

    public interface Callback {
        ViewHolder findViewHolder(int i);

        void markViewHoldersUpdated(int i, int i2, Object obj);

        void offsetPositionsForAdd(int i, int i2);

        void offsetPositionsForMove(int i, int i2);

        void offsetPositionsForRemovingInvisible(int i, int i2);

        void offsetPositionsForRemovingLaidOutOrNewView(int i, int i2);

        void onDispatchFirstPass(UpdateOp updateOp);

        void onDispatchSecondPass(UpdateOp updateOp);
    }

    public static class UpdateOp {
        public static final int ADD = 1;
        public static final int MOVE = 8;
        public static final int POOL_SIZE = 30;
        public static final int REMOVE = 2;
        public static final int UPDATE = 4;
        public int cmd;
        public int itemCount;
        public Object payload;
        public int positionStart;

        public UpdateOp(int i, int i2, int i3, Object obj) {
            this.cmd = i;
            this.positionStart = i2;
            this.itemCount = i3;
            this.payload = obj;
        }

        public String cmdToString() {
            int i = this.cmd;
            if (i == 1) {
                return "add";
            }
            if (i == 2) {
                return "rm";
            }
            if (i != 4) {
                return i != 8 ? "??" : "mv";
            } else {
                return "up";
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            UpdateOp updateOp = (UpdateOp) obj;
            int i = this.cmd;
            if (i != updateOp.cmd) {
                return false;
            }
            if (i == 8 && Math.abs(this.itemCount - this.positionStart) == 1 && this.itemCount == updateOp.positionStart && this.positionStart == updateOp.itemCount) {
                return true;
            }
            if (this.itemCount != updateOp.itemCount || this.positionStart != updateOp.positionStart) {
                return false;
            }
            Object obj2 = this.payload;
            if (obj2 != null) {
                if (!obj2.equals(updateOp.payload)) {
                    return false;
                }
            } else if (updateOp.payload != null) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (((this.cmd * 31) + this.positionStart) * 31) + this.itemCount;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
            stringBuilder.append("[");
            stringBuilder.append(cmdToString());
            stringBuilder.append(",s:");
            stringBuilder.append(this.positionStart);
            stringBuilder.append("c:");
            stringBuilder.append(this.itemCount);
            stringBuilder.append(",p:");
            stringBuilder.append(this.payload);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    public AdapterHelper(Callback callback) {
        this(callback, false);
    }

    private void applyAdd(UpdateOp updateOp) {
        postponeAndUpdateViewHolders(updateOp);
    }

    private void applyMove(UpdateOp updateOp) {
        postponeAndUpdateViewHolders(updateOp);
    }

    private void applyRemove(UpdateOp updateOp) {
        int i = updateOp.positionStart;
        int i2 = updateOp.itemCount + i;
        Object obj = -1;
        int i3 = i;
        int i4 = 0;
        while (i3 < i2) {
            Object obj2;
            if (this.mCallback.findViewHolder(i3) != null || canFindInPreLayout(i3)) {
                if (obj == null) {
                    dispatchAndUpdateViewHolders(obtainUpdateOp(2, i, i4, null));
                    obj = 1;
                } else {
                    obj = null;
                }
                obj2 = 1;
            } else {
                if (obj == 1) {
                    postponeAndUpdateViewHolders(obtainUpdateOp(2, i, i4, null));
                    obj = 1;
                } else {
                    obj = null;
                }
                obj2 = null;
            }
            if (obj != null) {
                i3 -= i4;
                i2 -= i4;
                i4 = 1;
            } else {
                i4++;
            }
            i3++;
            obj = obj2;
        }
        if (i4 != updateOp.itemCount) {
            recycleUpdateOp(updateOp);
            updateOp = obtainUpdateOp(2, i, i4, null);
        }
        if (obj == null) {
            dispatchAndUpdateViewHolders(updateOp);
        } else {
            postponeAndUpdateViewHolders(updateOp);
        }
    }

    private void applyUpdate(UpdateOp updateOp) {
        int i = updateOp.positionStart;
        int i2 = updateOp.itemCount + i;
        int i3 = i;
        Object obj = -1;
        int i4 = 0;
        while (i < i2) {
            if (this.mCallback.findViewHolder(i) != null || canFindInPreLayout(i)) {
                if (obj == null) {
                    dispatchAndUpdateViewHolders(obtainUpdateOp(4, i3, i4, updateOp.payload));
                    i3 = i;
                    i4 = 0;
                }
                obj = 1;
            } else {
                if (obj == 1) {
                    postponeAndUpdateViewHolders(obtainUpdateOp(4, i3, i4, updateOp.payload));
                    i3 = i;
                    i4 = 0;
                }
                obj = null;
            }
            i4++;
            i++;
        }
        if (i4 != updateOp.itemCount) {
            Object obj2 = updateOp.payload;
            recycleUpdateOp(updateOp);
            updateOp = obtainUpdateOp(4, i3, i4, obj2);
        }
        if (obj == null) {
            dispatchAndUpdateViewHolders(updateOp);
        } else {
            postponeAndUpdateViewHolders(updateOp);
        }
    }

    private boolean canFindInPreLayout(int i) {
        int size = this.mPostponedList.size();
        for (int i2 = 0; i2 < size; i2++) {
            UpdateOp updateOp = (UpdateOp) this.mPostponedList.get(i2);
            int i3 = updateOp.cmd;
            if (i3 == 8) {
                if (findPositionOffset(updateOp.itemCount, i2 + 1) == i) {
                    return true;
                }
            } else if (i3 == 1) {
                i3 = updateOp.positionStart;
                int i4 = updateOp.itemCount + i3;
                while (i3 < i4) {
                    if (findPositionOffset(i3, i2 + 1) == i) {
                        return true;
                    }
                    i3++;
                }
                continue;
            } else {
                continue;
            }
        }
        return false;
    }

    private void dispatchAndUpdateViewHolders(UpdateOp updateOp) {
        int i = updateOp.cmd;
        if (i == 1 || i == 8) {
            throw new IllegalArgumentException("should not dispatch add or move for pre layout");
        }
        i = updatePositionWithPostponed(updateOp.positionStart, i);
        int i2 = updateOp.positionStart;
        int i3 = updateOp.cmd;
        if (i3 == 2) {
            i3 = 0;
        } else if (i3 == 4) {
            i3 = 1;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("op should be remove or update.");
            stringBuilder.append(updateOp);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        int i4 = 1;
        for (int i5 = 1; i5 < updateOp.itemCount; i5++) {
            int updatePositionWithPostponed = updatePositionWithPostponed((i3 * i5) + updateOp.positionStart, updateOp.cmd);
            int i6 = updateOp.cmd;
            Object obj = (i6 == 2 ? updatePositionWithPostponed != i : !(i6 == 4 && updatePositionWithPostponed == i + 1)) ? null : 1;
            if (obj != null) {
                i4++;
            } else {
                UpdateOp obtainUpdateOp = obtainUpdateOp(i6, i, i4, updateOp.payload);
                dispatchFirstPassAndUpdateViewHolders(obtainUpdateOp, i2);
                recycleUpdateOp(obtainUpdateOp);
                if (updateOp.cmd == 4) {
                    i2 += i4;
                }
                i = updatePositionWithPostponed;
                i4 = 1;
            }
        }
        Object obj2 = updateOp.payload;
        recycleUpdateOp(updateOp);
        if (i4 > 0) {
            updateOp = obtainUpdateOp(updateOp.cmd, i, i4, obj2);
            dispatchFirstPassAndUpdateViewHolders(updateOp, i2);
            recycleUpdateOp(updateOp);
        }
    }

    private void postponeAndUpdateViewHolders(UpdateOp updateOp) {
        this.mPostponedList.add(updateOp);
        int i = updateOp.cmd;
        if (i == 1) {
            this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
        } else if (i == 2) {
            this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(updateOp.positionStart, updateOp.itemCount);
        } else if (i == 4) {
            this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
        } else if (i == 8) {
            this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown update op type for ");
            stringBuilder.append(updateOp);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private int updatePositionWithPostponed(int i, int i2) {
        for (int size = this.mPostponedList.size() - 1; size >= 0; size--) {
            UpdateOp updateOp = (UpdateOp) this.mPostponedList.get(size);
            int i3 = updateOp.cmd;
            int i4;
            if (i3 == 8) {
                int i5;
                i4 = updateOp.positionStart;
                i3 = updateOp.itemCount;
                int i6;
                if (i4 < i3) {
                    i5 = i4;
                    i6 = i3;
                } else {
                    i6 = i4;
                    i5 = i3;
                }
                if (i < i5 || i > i6) {
                    if (i < i4) {
                        if (i2 == 1) {
                            updateOp.positionStart = i4 + 1;
                            updateOp.itemCount = i3 + 1;
                        } else if (i2 == 2) {
                            updateOp.positionStart = i4 - 1;
                            updateOp.itemCount = i3 - 1;
                        }
                    }
                } else if (i5 == i4) {
                    if (i2 == 1) {
                        updateOp.itemCount = i3 + 1;
                    } else if (i2 == 2) {
                        updateOp.itemCount = i3 - 1;
                    }
                    i++;
                } else {
                    if (i2 == 1) {
                        updateOp.positionStart = i4 + 1;
                    } else if (i2 == 2) {
                        updateOp.positionStart = i4 - 1;
                    }
                    i--;
                }
            } else {
                i4 = updateOp.positionStart;
                if (i4 <= i) {
                    if (i3 == 1) {
                        i -= updateOp.itemCount;
                    } else if (i3 == 2) {
                        i += updateOp.itemCount;
                    }
                } else if (i2 == 1) {
                    updateOp.positionStart = i4 + 1;
                } else if (i2 == 2) {
                    updateOp.positionStart = i4 - 1;
                }
            }
        }
        for (i2 = this.mPostponedList.size() - 1; i2 >= 0; i2--) {
            UpdateOp updateOp2 = (UpdateOp) this.mPostponedList.get(i2);
            if (updateOp2.cmd == 8) {
                int i7 = updateOp2.itemCount;
                if (i7 == updateOp2.positionStart || i7 < 0) {
                    this.mPostponedList.remove(i2);
                    recycleUpdateOp(updateOp2);
                }
            } else if (updateOp2.itemCount <= 0) {
                this.mPostponedList.remove(i2);
                recycleUpdateOp(updateOp2);
            }
        }
        return i;
    }

    public AdapterHelper addUpdateOp(UpdateOp... updateOpArr) {
        Collections.addAll(this.mPendingUpdates, updateOpArr);
        return this;
    }

    public int applyPendingUpdatesToPosition(int i) {
        int size = this.mPendingUpdates.size();
        for (int i2 = 0; i2 < size; i2++) {
            UpdateOp updateOp = (UpdateOp) this.mPendingUpdates.get(i2);
            int i3 = updateOp.cmd;
            if (i3 != 1) {
                if (i3 == 2) {
                    i3 = updateOp.positionStart;
                    if (i3 <= i) {
                        int i4 = updateOp.itemCount;
                        if (i3 + i4 > i) {
                            return -1;
                        }
                        i -= i4;
                    } else {
                        continue;
                    }
                } else if (i3 == 8) {
                    i3 = updateOp.positionStart;
                    if (i3 == i) {
                        i = updateOp.itemCount;
                    } else {
                        if (i3 < i) {
                            i--;
                        }
                        if (updateOp.itemCount <= i) {
                            i++;
                        }
                    }
                }
            } else if (updateOp.positionStart <= i) {
                i += updateOp.itemCount;
            }
        }
        return i;
    }

    public void consumePostponedUpdates() {
        int size = this.mPostponedList.size();
        for (int i = 0; i < size; i++) {
            this.mCallback.onDispatchSecondPass((UpdateOp) this.mPostponedList.get(i));
        }
        recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }

    public void consumeUpdatesInOnePass() {
        consumePostponedUpdates();
        int size = this.mPendingUpdates.size();
        for (int i = 0; i < size; i++) {
            UpdateOp updateOp = (UpdateOp) this.mPendingUpdates.get(i);
            int i2 = updateOp.cmd;
            if (i2 == 1) {
                this.mCallback.onDispatchSecondPass(updateOp);
                this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
            } else if (i2 == 2) {
                this.mCallback.onDispatchSecondPass(updateOp);
                this.mCallback.offsetPositionsForRemovingInvisible(updateOp.positionStart, updateOp.itemCount);
            } else if (i2 == 4) {
                this.mCallback.onDispatchSecondPass(updateOp);
                this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
            } else if (i2 == 8) {
                this.mCallback.onDispatchSecondPass(updateOp);
                this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
            }
            Runnable runnable = this.mOnItemProcessedCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
        recycleUpdateOpsAndClearList(this.mPendingUpdates);
        this.mExistingUpdateTypes = 0;
    }

    public void dispatchFirstPassAndUpdateViewHolders(UpdateOp updateOp, int i) {
        this.mCallback.onDispatchFirstPass(updateOp);
        int i2 = updateOp.cmd;
        if (i2 == 2) {
            this.mCallback.offsetPositionsForRemovingInvisible(i, updateOp.itemCount);
        } else if (i2 == 4) {
            this.mCallback.markViewHoldersUpdated(i, updateOp.itemCount, updateOp.payload);
        } else {
            throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
        }
    }

    public int findPositionOffset(int i) {
        return findPositionOffset(i, 0);
    }

    public boolean hasAnyUpdateTypes(int i) {
        return (i & this.mExistingUpdateTypes) != 0;
    }

    public boolean hasPendingUpdates() {
        return this.mPendingUpdates.size() > 0;
    }

    public boolean hasUpdates() {
        return (this.mPostponedList.isEmpty() || this.mPendingUpdates.isEmpty()) ? false : true;
    }

    public UpdateOp obtainUpdateOp(int i, int i2, int i3, Object obj) {
        UpdateOp updateOp = (UpdateOp) this.mUpdateOpPool.acquire();
        if (updateOp == null) {
            return new UpdateOp(i, i2, i3, obj);
        }
        updateOp.cmd = i;
        updateOp.positionStart = i2;
        updateOp.itemCount = i3;
        updateOp.payload = obj;
        return updateOp;
    }

    public boolean onItemRangeChanged(int i, int i2, Object obj) {
        boolean z = false;
        if (i2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(4, i, i2, obj));
        this.mExistingUpdateTypes |= 4;
        if (this.mPendingUpdates.size() == 1) {
            z = true;
        }
        return z;
    }

    public boolean onItemRangeInserted(int i, int i2) {
        boolean z = false;
        if (i2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(1, i, i2, null));
        this.mExistingUpdateTypes |= 1;
        if (this.mPendingUpdates.size() == 1) {
            z = true;
        }
        return z;
    }

    public boolean onItemRangeMoved(int i, int i2, int i3) {
        boolean z = false;
        if (i == i2) {
            return false;
        }
        if (i3 == 1) {
            this.mPendingUpdates.add(obtainUpdateOp(8, i, i2, null));
            this.mExistingUpdateTypes |= 8;
            if (this.mPendingUpdates.size() == 1) {
                z = true;
            }
            return z;
        }
        throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
    }

    public boolean onItemRangeRemoved(int i, int i2) {
        boolean z = false;
        if (i2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(obtainUpdateOp(2, i, i2, null));
        this.mExistingUpdateTypes |= 2;
        if (this.mPendingUpdates.size() == 1) {
            z = true;
        }
        return z;
    }

    public void preProcess() {
        this.mOpReorderer.reorderOps(this.mPendingUpdates);
        int size = this.mPendingUpdates.size();
        for (int i = 0; i < size; i++) {
            UpdateOp updateOp = (UpdateOp) this.mPendingUpdates.get(i);
            int i2 = updateOp.cmd;
            if (i2 == 1) {
                applyAdd(updateOp);
            } else if (i2 == 2) {
                applyRemove(updateOp);
            } else if (i2 == 4) {
                applyUpdate(updateOp);
            } else if (i2 == 8) {
                applyMove(updateOp);
            }
            Runnable runnable = this.mOnItemProcessedCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
        this.mPendingUpdates.clear();
    }

    public void recycleUpdateOp(UpdateOp updateOp) {
        if (!this.mDisableRecycler) {
            updateOp.payload = null;
            this.mUpdateOpPool.release(updateOp);
        }
    }

    public void recycleUpdateOpsAndClearList(List<UpdateOp> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            recycleUpdateOp((UpdateOp) list.get(i));
        }
        list.clear();
    }

    public void reset() {
        recycleUpdateOpsAndClearList(this.mPendingUpdates);
        recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }

    public AdapterHelper(Callback callback, boolean z) {
        this.mUpdateOpPool = new SimplePool(30);
        this.mPendingUpdates = new ArrayList();
        this.mPostponedList = new ArrayList();
        this.mExistingUpdateTypes = 0;
        this.mCallback = callback;
        this.mDisableRecycler = z;
        this.mOpReorderer = new OpReorderer(this);
    }

    public int findPositionOffset(int i, int i2) {
        int size = this.mPostponedList.size();
        while (i2 < size) {
            UpdateOp updateOp = (UpdateOp) this.mPostponedList.get(i2);
            int i3 = updateOp.cmd;
            if (i3 == 8) {
                i3 = updateOp.positionStart;
                if (i3 == i) {
                    i = updateOp.itemCount;
                } else {
                    if (i3 < i) {
                        i--;
                    }
                    if (updateOp.itemCount <= i) {
                        i++;
                    }
                }
            } else {
                int i4 = updateOp.positionStart;
                if (i4 > i) {
                    continue;
                } else if (i3 == 2) {
                    int i5 = updateOp.itemCount;
                    if (i < i4 + i5) {
                        return -1;
                    }
                    i -= i5;
                } else if (i3 == 1) {
                    i += updateOp.itemCount;
                }
            }
            i2++;
        }
        return i;
    }
}
