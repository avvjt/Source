package androidx.recyclerview.widget;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.recyclerview.widget.ThreadUtil.BackgroundCallback;
import androidx.recyclerview.widget.ThreadUtil.MainThreadCallback;
import androidx.recyclerview.widget.TileList.Tile;
import f.d.b.a.a;

public class AsyncListUtil<T> {
    public static final boolean DEBUG = false;
    public static final String TAG = "AsyncListUtil";
    public boolean mAllowScrollHints;
    private final BackgroundCallback<T> mBackgroundCallback;
    public final BackgroundCallback<T> mBackgroundProxy;
    public final DataCallback<T> mDataCallback;
    public int mDisplayedGeneration = 0;
    public int mItemCount = 0;
    private final MainThreadCallback<T> mMainThreadCallback;
    public final MainThreadCallback<T> mMainThreadProxy;
    public final SparseIntArray mMissingPositions = new SparseIntArray();
    public final int[] mPrevRange = new int[2];
    public int mRequestedGeneration = 0;
    private int mScrollHint = 0;
    public final Class<T> mTClass;
    public final TileList<T> mTileList;
    public final int mTileSize;
    public final int[] mTmpRange = new int[2];
    public final int[] mTmpRangeExtended = new int[2];
    public final ViewCallback mViewCallback;

    public static abstract class DataCallback<T> {
        @WorkerThread
        public abstract void fillData(@NonNull T[] tArr, int i, int i2);

        @WorkerThread
        public int getMaxCachedTiles() {
            return 10;
        }

        @WorkerThread
        public void recycleData(@NonNull T[] tArr, int i) {
        }

        @WorkerThread
        public abstract int refreshData();
    }

    public static abstract class ViewCallback {
        public static final int HINT_SCROLL_ASC = 2;
        public static final int HINT_SCROLL_DESC = 1;
        public static final int HINT_SCROLL_NONE = 0;

        @UiThread
        public void extendRangeInto(@NonNull int[] iArr, @NonNull int[] iArr2, int i) {
            int i2 = (iArr[1] - iArr[0]) + 1;
            int i3 = i2 / 2;
            iArr2[0] = iArr[0] - (i == 1 ? i2 : i3);
            int i4 = iArr[1];
            if (i != 2) {
                i2 = i3;
            }
            iArr2[1] = i4 + i2;
        }

        @UiThread
        public abstract void getItemRangeInto(@NonNull int[] iArr);

        @UiThread
        public abstract void onDataRefresh();

        @UiThread
        public abstract void onItemLoaded(int i);
    }

    public AsyncListUtil(@NonNull Class<T> cls, int i, @NonNull DataCallback<T> dataCallback, @NonNull ViewCallback viewCallback) {
        AnonymousClass1 anonymousClass1 = new MainThreadCallback<T>() {
            private boolean isRequestedGeneration(int i) {
                return i == AsyncListUtil.this.mRequestedGeneration;
            }

            private void recycleAllTiles() {
                for (int i = 0; i < AsyncListUtil.this.mTileList.size(); i++) {
                    AsyncListUtil asyncListUtil = AsyncListUtil.this;
                    asyncListUtil.mBackgroundProxy.recycleTile(asyncListUtil.mTileList.getAtIndex(i));
                }
                AsyncListUtil.this.mTileList.clear();
            }

            public void addTile(int i, Tile<T> tile) {
                if (isRequestedGeneration(i)) {
                    Tile addOrReplace = AsyncListUtil.this.mTileList.addOrReplace(tile);
                    if (addOrReplace != null) {
                        StringBuilder L = a.L("duplicate tile @");
                        L.append(addOrReplace.mStartPosition);
                        Log.e(AsyncListUtil.TAG, L.toString());
                        AsyncListUtil.this.mBackgroundProxy.recycleTile(addOrReplace);
                    }
                    i = tile.mStartPosition + tile.mItemCount;
                    int i2 = 0;
                    while (i2 < AsyncListUtil.this.mMissingPositions.size()) {
                        int keyAt = AsyncListUtil.this.mMissingPositions.keyAt(i2);
                        if (tile.mStartPosition > keyAt || keyAt >= i) {
                            i2++;
                        } else {
                            AsyncListUtil.this.mMissingPositions.removeAt(i2);
                            AsyncListUtil.this.mViewCallback.onItemLoaded(keyAt);
                        }
                    }
                    return;
                }
                AsyncListUtil.this.mBackgroundProxy.recycleTile(tile);
            }

            public void removeTile(int i, int i2) {
                if (isRequestedGeneration(i)) {
                    Tile removeAtPos = AsyncListUtil.this.mTileList.removeAtPos(i2);
                    if (removeAtPos == null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("tile not found @");
                        stringBuilder.append(i2);
                        Log.e(AsyncListUtil.TAG, stringBuilder.toString());
                        return;
                    }
                    AsyncListUtil.this.mBackgroundProxy.recycleTile(removeAtPos);
                }
            }

            public void updateItemCount(int i, int i2) {
                if (isRequestedGeneration(i)) {
                    AsyncListUtil asyncListUtil = AsyncListUtil.this;
                    asyncListUtil.mItemCount = i2;
                    asyncListUtil.mViewCallback.onDataRefresh();
                    asyncListUtil = AsyncListUtil.this;
                    asyncListUtil.mDisplayedGeneration = asyncListUtil.mRequestedGeneration;
                    recycleAllTiles();
                    asyncListUtil = AsyncListUtil.this;
                    asyncListUtil.mAllowScrollHints = false;
                    asyncListUtil.updateRange();
                }
            }
        };
        this.mMainThreadCallback = anonymousClass1;
        AnonymousClass2 anonymousClass2 = new BackgroundCallback<T>() {
            private int mFirstRequiredTileStart;
            private int mGeneration;
            private int mItemCount;
            private int mLastRequiredTileStart;
            public final SparseBooleanArray mLoadedTiles = new SparseBooleanArray();
            private Tile<T> mRecycledRoot;

            private Tile<T> acquireTile() {
                Tile tile = this.mRecycledRoot;
                if (tile != null) {
                    this.mRecycledRoot = tile.mNext;
                    return tile;
                }
                AsyncListUtil asyncListUtil = AsyncListUtil.this;
                return new Tile(asyncListUtil.mTClass, asyncListUtil.mTileSize);
            }

            private void addTile(Tile<T> tile) {
                this.mLoadedTiles.put(tile.mStartPosition, true);
                AsyncListUtil.this.mMainThreadProxy.addTile(this.mGeneration, tile);
            }

            private void flushTileCache(int i) {
                int maxCachedTiles = AsyncListUtil.this.mDataCallback.getMaxCachedTiles();
                while (this.mLoadedTiles.size() >= maxCachedTiles) {
                    int keyAt = this.mLoadedTiles.keyAt(0);
                    SparseBooleanArray sparseBooleanArray = this.mLoadedTiles;
                    int keyAt2 = sparseBooleanArray.keyAt(sparseBooleanArray.size() - 1);
                    int i2 = this.mFirstRequiredTileStart - keyAt;
                    int i3 = keyAt2 - this.mLastRequiredTileStart;
                    if (i2 > 0 && (i2 >= i3 || i == 2)) {
                        removeTile(keyAt);
                    } else if (i3 <= 0) {
                        return;
                    } else {
                        if (i2 < i3 || i == 1) {
                            removeTile(keyAt2);
                        } else {
                            return;
                        }
                    }
                }
            }

            private int getTileStart(int i) {
                return i - (i % AsyncListUtil.this.mTileSize);
            }

            private boolean isTileLoaded(int i) {
                return this.mLoadedTiles.get(i);
            }

            private void log(String str, Object... objArr) {
                StringBuilder L = a.L("[BKGR] ");
                L.append(String.format(str, objArr));
                Log.d(AsyncListUtil.TAG, L.toString());
            }

            private void removeTile(int i) {
                this.mLoadedTiles.delete(i);
                AsyncListUtil.this.mMainThreadProxy.removeTile(this.mGeneration, i);
            }

            private void requestTiles(int i, int i2, int i3, boolean z) {
                int i4 = i;
                while (i4 <= i2) {
                    AsyncListUtil.this.mBackgroundProxy.loadTile(z ? (i2 + i) - i4 : i4, i3);
                    i4 += AsyncListUtil.this.mTileSize;
                }
            }

            public void loadTile(int i, int i2) {
                if (!isTileLoaded(i)) {
                    Tile acquireTile = acquireTile();
                    acquireTile.mStartPosition = i;
                    i = Math.min(AsyncListUtil.this.mTileSize, this.mItemCount - i);
                    acquireTile.mItemCount = i;
                    AsyncListUtil.this.mDataCallback.fillData(acquireTile.mItems, acquireTile.mStartPosition, i);
                    flushTileCache(i2);
                    addTile(acquireTile);
                }
            }

            public void recycleTile(Tile<T> tile) {
                AsyncListUtil.this.mDataCallback.recycleData(tile.mItems, tile.mItemCount);
                tile.mNext = this.mRecycledRoot;
                this.mRecycledRoot = tile;
            }

            public void refresh(int i) {
                this.mGeneration = i;
                this.mLoadedTiles.clear();
                i = AsyncListUtil.this.mDataCallback.refreshData();
                this.mItemCount = i;
                AsyncListUtil.this.mMainThreadProxy.updateItemCount(this.mGeneration, i);
            }

            public void updateRange(int i, int i2, int i3, int i4, int i5) {
                if (i <= i2) {
                    i = getTileStart(i);
                    i2 = getTileStart(i2);
                    this.mFirstRequiredTileStart = getTileStart(i3);
                    i3 = getTileStart(i4);
                    this.mLastRequiredTileStart = i3;
                    if (i5 == 1) {
                        requestTiles(this.mFirstRequiredTileStart, i2, i5, true);
                        requestTiles(i2 + AsyncListUtil.this.mTileSize, this.mLastRequiredTileStart, i5, false);
                    } else {
                        requestTiles(i, i3, i5, false);
                        requestTiles(this.mFirstRequiredTileStart, i - AsyncListUtil.this.mTileSize, i5, true);
                    }
                }
            }
        };
        this.mBackgroundCallback = anonymousClass2;
        this.mTClass = cls;
        this.mTileSize = i;
        this.mDataCallback = dataCallback;
        this.mViewCallback = viewCallback;
        this.mTileList = new TileList(i);
        MessageThreadUtil messageThreadUtil = new MessageThreadUtil();
        this.mMainThreadProxy = messageThreadUtil.getMainThreadProxy(anonymousClass1);
        this.mBackgroundProxy = messageThreadUtil.getBackgroundProxy(anonymousClass2);
        refresh();
    }

    private boolean isRefreshPending() {
        return this.mRequestedGeneration != this.mDisplayedGeneration;
    }

    @Nullable
    public T getItem(int i) {
        if (i < 0 || i >= this.mItemCount) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(i);
            stringBuilder.append(" is not within 0 and ");
            stringBuilder.append(this.mItemCount);
            throw new IndexOutOfBoundsException(stringBuilder.toString());
        }
        Object itemAt = this.mTileList.getItemAt(i);
        if (itemAt == null && !isRefreshPending()) {
            this.mMissingPositions.put(i, 0);
        }
        return itemAt;
    }

    public int getItemCount() {
        return this.mItemCount;
    }

    public void log(String str, Object... objArr) {
        StringBuilder L = a.L("[MAIN] ");
        L.append(String.format(str, objArr));
        Log.d(TAG, L.toString());
    }

    public void onRangeChanged() {
        if (!isRefreshPending()) {
            updateRange();
            this.mAllowScrollHints = true;
        }
    }

    public void refresh() {
        this.mMissingPositions.clear();
        BackgroundCallback backgroundCallback = this.mBackgroundProxy;
        int i = this.mRequestedGeneration + 1;
        this.mRequestedGeneration = i;
        backgroundCallback.refresh(i);
    }

    public void updateRange() {
        this.mViewCallback.getItemRangeInto(this.mTmpRange);
        int[] iArr = this.mTmpRange;
        if (iArr[0] <= iArr[1] && iArr[0] >= 0 && iArr[1] < this.mItemCount) {
            if (this.mAllowScrollHints) {
                int i = iArr[0];
                int[] iArr2 = this.mPrevRange;
                if (i > iArr2[1] || iArr2[0] > iArr[1]) {
                    this.mScrollHint = 0;
                } else if (iArr[0] < iArr2[0]) {
                    this.mScrollHint = 1;
                } else if (iArr[0] > iArr2[0]) {
                    this.mScrollHint = 2;
                }
            } else {
                this.mScrollHint = 0;
            }
            int[] iArr3 = this.mPrevRange;
            iArr3[0] = iArr[0];
            iArr3[1] = iArr[1];
            this.mViewCallback.extendRangeInto(iArr, this.mTmpRangeExtended, this.mScrollHint);
            iArr = this.mTmpRangeExtended;
            iArr[0] = Math.min(this.mTmpRange[0], Math.max(iArr[0], 0));
            iArr = this.mTmpRangeExtended;
            iArr[1] = Math.max(this.mTmpRange[1], Math.min(iArr[1], this.mItemCount - 1));
            BackgroundCallback backgroundCallback = this.mBackgroundProxy;
            iArr = this.mTmpRange;
            int i2 = iArr[0];
            int i3 = iArr[1];
            iArr = this.mTmpRangeExtended;
            backgroundCallback.updateRange(i2, i3, iArr[0], iArr[1], this.mScrollHint);
        }
    }
}
