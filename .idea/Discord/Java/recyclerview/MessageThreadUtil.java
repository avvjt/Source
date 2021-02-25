package androidx.recyclerview.widget;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.recyclerview.widget.ThreadUtil.BackgroundCallback;
import androidx.recyclerview.widget.ThreadUtil.MainThreadCallback;
import androidx.recyclerview.widget.TileList.Tile;
import f.d.b.a.a;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageThreadUtil<T> implements ThreadUtil<T> {

    public static class MessageQueue {
        private SyncQueueItem mRoot;

        public synchronized SyncQueueItem next() {
            SyncQueueItem syncQueueItem = this.mRoot;
            if (syncQueueItem == null) {
                return null;
            }
            this.mRoot = syncQueueItem.next;
            return syncQueueItem;
        }

        /* JADX WARNING: Removed duplicated region for block: B:8:0x0013  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void removeMessages(int i) {
            SyncQueueItem syncQueueItem;
            while (true) {
                syncQueueItem = this.mRoot;
                if (syncQueueItem != null && syncQueueItem.what == i) {
                    this.mRoot = syncQueueItem.next;
                    syncQueueItem.recycle();
                } else if (syncQueueItem != null) {
                    SyncQueueItem syncQueueItem2 = syncQueueItem.next;
                    while (syncQueueItem2 != null) {
                        SyncQueueItem syncQueueItem3 = syncQueueItem2.next;
                        if (syncQueueItem2.what == i) {
                            syncQueueItem.next = syncQueueItem3;
                            syncQueueItem2.recycle();
                        } else {
                            syncQueueItem = syncQueueItem2;
                        }
                        syncQueueItem2 = syncQueueItem3;
                    }
                }
            }
            if (syncQueueItem != null) {
            }
        }

        public synchronized void sendMessage(SyncQueueItem syncQueueItem) {
            SyncQueueItem syncQueueItem2 = this.mRoot;
            if (syncQueueItem2 == null) {
                this.mRoot = syncQueueItem;
                return;
            }
            while (true) {
                SyncQueueItem syncQueueItem3 = syncQueueItem2.next;
                if (syncQueueItem3 != null) {
                    syncQueueItem2 = syncQueueItem3;
                } else {
                    syncQueueItem2.next = syncQueueItem;
                    return;
                }
            }
        }

        public synchronized void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
            syncQueueItem.next = this.mRoot;
            this.mRoot = syncQueueItem;
        }
    }

    public static class SyncQueueItem {
        private static SyncQueueItem sPool;
        private static final Object sPoolLock = new Object();
        public int arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public int arg5;
        public Object data;
        public SyncQueueItem next;
        public int what;

        public static SyncQueueItem obtainMessage(int i, int i2, int i3, int i4, int i5, int i6, Object obj) {
            SyncQueueItem syncQueueItem;
            synchronized (sPoolLock) {
                syncQueueItem = sPool;
                if (syncQueueItem == null) {
                    syncQueueItem = new SyncQueueItem();
                } else {
                    sPool = syncQueueItem.next;
                    syncQueueItem.next = null;
                }
                syncQueueItem.what = i;
                syncQueueItem.arg1 = i2;
                syncQueueItem.arg2 = i3;
                syncQueueItem.arg3 = i4;
                syncQueueItem.arg4 = i5;
                syncQueueItem.arg5 = i6;
                syncQueueItem.data = obj;
            }
            return syncQueueItem;
        }

        public void recycle() {
            this.next = null;
            this.arg5 = 0;
            this.arg4 = 0;
            this.arg3 = 0;
            this.arg2 = 0;
            this.arg1 = 0;
            this.what = 0;
            this.data = null;
            synchronized (sPoolLock) {
                SyncQueueItem syncQueueItem = sPool;
                if (syncQueueItem != null) {
                    this.next = syncQueueItem;
                }
                sPool = this;
            }
        }

        public static SyncQueueItem obtainMessage(int i, int i2, int i3) {
            return obtainMessage(i, i2, i3, 0, 0, 0, null);
        }

        public static SyncQueueItem obtainMessage(int i, int i2, Object obj) {
            return obtainMessage(i, i2, 0, 0, 0, 0, obj);
        }
    }

    public BackgroundCallback<T> getBackgroundProxy(final BackgroundCallback<T> backgroundCallback) {
        return new BackgroundCallback<T>() {
            public static final int LOAD_TILE = 3;
            public static final int RECYCLE_TILE = 4;
            public static final int REFRESH = 1;
            public static final int UPDATE_RANGE = 2;
            private Runnable mBackgroundRunnable = new Runnable() {
                public void run() {
                    while (true) {
                        SyncQueueItem next = AnonymousClass2.this.mQueue.next();
                        if (next == null) {
                            AnonymousClass2.this.mBackgroundRunning.set(false);
                            return;
                        }
                        int i = next.what;
                        if (i == 1) {
                            AnonymousClass2.this.mQueue.removeMessages(1);
                            backgroundCallback.refresh(next.arg1);
                        } else if (i == 2) {
                            AnonymousClass2.this.mQueue.removeMessages(2);
                            AnonymousClass2.this.mQueue.removeMessages(3);
                            backgroundCallback.updateRange(next.arg1, next.arg2, next.arg3, next.arg4, next.arg5);
                        } else if (i == 3) {
                            backgroundCallback.loadTile(next.arg1, next.arg2);
                        } else if (i != 4) {
                            StringBuilder L = a.L("Unsupported message, what=");
                            L.append(next.what);
                            Log.e("ThreadUtil", L.toString());
                        } else {
                            backgroundCallback.recycleTile((Tile) next.data);
                        }
                    }
                }
            };
            public AtomicBoolean mBackgroundRunning = new AtomicBoolean(false);
            private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
            public final MessageQueue mQueue = new MessageQueue();

            private void maybeExecuteBackgroundRunnable() {
                if (this.mBackgroundRunning.compareAndSet(false, true)) {
                    this.mExecutor.execute(this.mBackgroundRunnable);
                }
            }

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                maybeExecuteBackgroundRunnable();
            }

            private void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessageAtFrontOfQueue(syncQueueItem);
                maybeExecuteBackgroundRunnable();
            }

            public void loadTile(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(3, i, i2));
            }

            public void recycleTile(Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(4, 0, (Object) tile));
            }

            public void refresh(int i) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(1, i, null));
            }

            public void updateRange(int i, int i2, int i3, int i4, int i5) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(2, i, i2, i3, i4, i5, null));
            }
        };
    }

    public MainThreadCallback<T> getMainThreadProxy(final MainThreadCallback<T> mainThreadCallback) {
        return new MainThreadCallback<T>() {
            public static final int ADD_TILE = 2;
            public static final int REMOVE_TILE = 3;
            public static final int UPDATE_ITEM_COUNT = 1;
            private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
            private Runnable mMainThreadRunnable = new Runnable() {
                public void run() {
                    SyncQueueItem next = AnonymousClass1.this.mQueue.next();
                    while (next != null) {
                        int i = next.what;
                        if (i == 1) {
                            mainThreadCallback.updateItemCount(next.arg1, next.arg2);
                        } else if (i == 2) {
                            mainThreadCallback.addTile(next.arg1, (Tile) next.data);
                        } else if (i != 3) {
                            StringBuilder L = a.L("Unsupported message, what=");
                            L.append(next.what);
                            Log.e("ThreadUtil", L.toString());
                        } else {
                            mainThreadCallback.removeTile(next.arg1, next.arg2);
                        }
                        next = AnonymousClass1.this.mQueue.next();
                    }
                }
            };
            public final MessageQueue mQueue = new MessageQueue();

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                this.mMainThreadHandler.post(this.mMainThreadRunnable);
            }

            public void addTile(int i, Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(2, i, (Object) tile));
            }

            public void removeTile(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(3, i, i2));
            }

            public void updateItemCount(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(1, i, i2));
            }
        };
    }
}
