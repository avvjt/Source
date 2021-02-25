package androidx.recyclerview.widget;

import androidx.recyclerview.widget.AdapterHelper.UpdateOp;
import java.util.List;

public class OpReorderer {
    public final Callback mCallback;

    public interface Callback {
        UpdateOp obtainUpdateOp(int i, int i2, int i3, Object obj);

        void recycleUpdateOp(UpdateOp updateOp);
    }

    public OpReorderer(Callback callback) {
        this.mCallback = callback;
    }

    private int getLastMoveOutOfOrder(List<UpdateOp> list) {
        Object obj = null;
        for (int size = list.size() - 1; size >= 0; size--) {
            if (((UpdateOp) list.get(size)).cmd != 8) {
                obj = 1;
            } else if (obj != null) {
                return size;
            }
        }
        return -1;
    }

    private void swapMoveAdd(List<UpdateOp> list, int i, UpdateOp updateOp, int i2, UpdateOp updateOp2) {
        int i3 = updateOp.itemCount;
        int i4 = updateOp2.positionStart;
        int i5 = i3 < i4 ? -1 : 0;
        int i6 = updateOp.positionStart;
        if (i6 < i4) {
            i5++;
        }
        if (i4 <= i6) {
            updateOp.positionStart = i6 + updateOp2.itemCount;
        }
        i4 = updateOp2.positionStart;
        if (i4 <= i3) {
            updateOp.itemCount = i3 + updateOp2.itemCount;
        }
        updateOp2.positionStart = i4 + i5;
        list.set(i, updateOp2);
        list.set(i2, updateOp);
    }

    private void swapMoveOp(List<UpdateOp> list, int i, int i2) {
        UpdateOp updateOp = (UpdateOp) list.get(i);
        UpdateOp updateOp2 = (UpdateOp) list.get(i2);
        int i3 = updateOp2.cmd;
        if (i3 == 1) {
            swapMoveAdd(list, i, updateOp, i2, updateOp2);
        } else if (i3 == 2) {
            swapMoveRemove(list, i, updateOp, i2, updateOp2);
        } else if (i3 == 4) {
            swapMoveUpdate(list, i, updateOp, i2, updateOp2);
        }
    }

    public void reorderOps(List<UpdateOp> list) {
        while (true) {
            int lastMoveOutOfOrder = getLastMoveOutOfOrder(list);
            if (lastMoveOutOfOrder != -1) {
                swapMoveOp(list, lastMoveOutOfOrder, lastMoveOutOfOrder + 1);
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x002a  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x002a  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0076  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void swapMoveRemove(List<UpdateOp> list, int i, UpdateOp updateOp, int i2, UpdateOp updateOp2) {
        int i3;
        UpdateOp updateOp3;
        int i4 = updateOp.positionStart;
        int i5 = updateOp.itemCount;
        Object obj = null;
        Object obj2;
        if (i4 < i5) {
            if (updateOp2.positionStart == i4 && updateOp2.itemCount == i5 - i4) {
                obj2 = null;
            } else {
                obj2 = null;
                i3 = updateOp2.positionStart;
                if (i5 < i3) {
                    updateOp2.positionStart = i3 - 1;
                } else {
                    int i6 = updateOp2.itemCount;
                    if (i5 < i3 + i6) {
                        updateOp2.itemCount = i6 - 1;
                        updateOp.cmd = 2;
                        updateOp.itemCount = 1;
                        if (updateOp2.itemCount == 0) {
                            list.remove(i2);
                            this.mCallback.recycleUpdateOp(updateOp2);
                        }
                        return;
                    }
                }
                i5 = updateOp.positionStart;
                i3 = updateOp2.positionStart;
                updateOp3 = null;
                if (i5 <= i3) {
                    updateOp2.positionStart = i3 + 1;
                } else {
                    int i7 = updateOp2.itemCount;
                    if (i5 < i3 + i7) {
                        updateOp3 = this.mCallback.obtainUpdateOp(2, i5 + 1, (i3 + i7) - i5, null);
                        updateOp2.itemCount = updateOp.positionStart - updateOp2.positionStart;
                    }
                }
                if (obj != null) {
                    list.set(i, updateOp2);
                    list.remove(i2);
                    this.mCallback.recycleUpdateOp(updateOp);
                    return;
                }
                if (obj2 != null) {
                    if (updateOp3 != null) {
                        i4 = updateOp.positionStart;
                        if (i4 > updateOp3.positionStart) {
                            updateOp.positionStart = i4 - updateOp3.itemCount;
                        }
                        i4 = updateOp.itemCount;
                        if (i4 > updateOp3.positionStart) {
                            updateOp.itemCount = i4 - updateOp3.itemCount;
                        }
                    }
                    i4 = updateOp.positionStart;
                    if (i4 > updateOp2.positionStart) {
                        updateOp.positionStart = i4 - updateOp2.itemCount;
                    }
                    i4 = updateOp.itemCount;
                    if (i4 > updateOp2.positionStart) {
                        updateOp.itemCount = i4 - updateOp2.itemCount;
                    }
                } else {
                    if (updateOp3 != null) {
                        i4 = updateOp.positionStart;
                        if (i4 >= updateOp3.positionStart) {
                            updateOp.positionStart = i4 - updateOp3.itemCount;
                        }
                        i4 = updateOp.itemCount;
                        if (i4 >= updateOp3.positionStart) {
                            updateOp.itemCount = i4 - updateOp3.itemCount;
                        }
                    }
                    i4 = updateOp.positionStart;
                    if (i4 >= updateOp2.positionStart) {
                        updateOp.positionStart = i4 - updateOp2.itemCount;
                    }
                    i4 = updateOp.itemCount;
                    if (i4 >= updateOp2.positionStart) {
                        updateOp.itemCount = i4 - updateOp2.itemCount;
                    }
                }
                list.set(i, updateOp2);
                if (updateOp.positionStart != updateOp.itemCount) {
                    list.set(i2, updateOp);
                } else {
                    list.remove(i2);
                }
                if (updateOp3 != null) {
                    list.add(i, updateOp3);
                }
                return;
            }
        } else if (updateOp2.positionStart == i5 + 1 && updateOp2.itemCount == i4 - i5) {
            obj2 = 1;
        } else {
            obj2 = 1;
            i3 = updateOp2.positionStart;
            if (i5 < i3) {
            }
            i5 = updateOp.positionStart;
            i3 = updateOp2.positionStart;
            updateOp3 = null;
            if (i5 <= i3) {
            }
            if (obj != null) {
            }
        }
        obj = 1;
        i3 = updateOp2.positionStart;
        if (i5 < i3) {
        }
        i5 = updateOp.positionStart;
        i3 = updateOp2.positionStart;
        updateOp3 = null;
        if (i5 <= i3) {
        }
        if (obj != null) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x005b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void swapMoveUpdate(List<UpdateOp> list, int i, UpdateOp updateOp, int i2, UpdateOp updateOp2) {
        int i3;
        Object obtainUpdateOp;
        int i4 = updateOp.itemCount;
        int i5 = updateOp2.positionStart;
        Object obj = null;
        if (i4 < i5) {
            updateOp2.positionStart = i5 - 1;
        } else {
            i3 = updateOp2.itemCount;
            if (i4 < i5 + i3) {
                updateOp2.itemCount = i3 - 1;
                obtainUpdateOp = this.mCallback.obtainUpdateOp(4, updateOp.positionStart, 1, updateOp2.payload);
                i5 = updateOp.positionStart;
                i3 = updateOp2.positionStart;
                if (i5 > i3) {
                    updateOp2.positionStart = i3 + 1;
                } else {
                    int i6 = updateOp2.itemCount;
                    if (i5 < i3 + i6) {
                        i3 = (i3 + i6) - i5;
                        obj = this.mCallback.obtainUpdateOp(4, i5 + 1, i3, updateOp2.payload);
                        updateOp2.itemCount -= i3;
                    }
                }
                list.set(i2, updateOp);
                if (updateOp2.itemCount <= 0) {
                    list.set(i, updateOp2);
                } else {
                    list.remove(i);
                    this.mCallback.recycleUpdateOp(updateOp2);
                }
                if (obtainUpdateOp != null) {
                    list.add(i, obtainUpdateOp);
                }
                if (obj == null) {
                    list.add(i, obj);
                    return;
                }
                return;
            }
        }
        obtainUpdateOp = null;
        i5 = updateOp.positionStart;
        i3 = updateOp2.positionStart;
        if (i5 > i3) {
        }
        list.set(i2, updateOp);
        if (updateOp2.itemCount <= 0) {
        }
        if (obtainUpdateOp != null) {
        }
        if (obj == null) {
        }
    }
}
