package androidx.lifecycle;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ViewModel {
    @Nullable
    private final Map<String, Object> mBagOfTags = new HashMap();
    private volatile boolean mCleared = false;

    private static void closeWithRuntimeException(Object obj) {
        if (obj instanceof Closeable) {
            try {
                ((Closeable) obj).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @MainThread
    public final void clear() {
        this.mCleared = true;
        Map map = this.mBagOfTags;
        if (map != null) {
            synchronized (map) {
                for (Object closeWithRuntimeException : this.mBagOfTags.values()) {
                    closeWithRuntimeException(closeWithRuntimeException);
                }
            }
        }
        onCleared();
    }

    public <T> T getTag(String str) {
        Map map = this.mBagOfTags;
        if (map == null) {
            return null;
        }
        Object obj;
        synchronized (map) {
            obj = this.mBagOfTags.get(str);
        }
        return obj;
    }

    public void onCleared() {
    }

    public <T> T setTagIfAbsent(String str, T t) {
        Object obj;
        Object t2;
        synchronized (this.mBagOfTags) {
            obj = this.mBagOfTags.get(str);
            if (obj == null) {
                this.mBagOfTags.put(str, t2);
            }
        }
        if (obj != null) {
            t2 = obj;
        }
        if (this.mCleared) {
            closeWithRuntimeException(t2);
        }
        return t2;
    }
}
