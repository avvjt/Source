package androidx.lifecycle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.internal.SafeIterableMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MediatorLiveData<T> extends MutableLiveData<T> {
    private SafeIterableMap<LiveData<?>, Source<?>> mSources = new SafeIterableMap();

    public static class Source<V> implements Observer<V> {
        public final LiveData<V> mLiveData;
        public final Observer<? super V> mObserver;
        public int mVersion = -1;

        public Source(LiveData<V> liveData, Observer<? super V> observer) {
            this.mLiveData = liveData;
            this.mObserver = observer;
        }

        public void onChanged(@Nullable V v) {
            if (this.mVersion != this.mLiveData.getVersion()) {
                this.mVersion = this.mLiveData.getVersion();
                this.mObserver.onChanged(v);
            }
        }

        public void plug() {
            this.mLiveData.observeForever(this);
        }

        public void unplug() {
            this.mLiveData.removeObserver(this);
        }
    }

    @MainThread
    public <S> void addSource(@NonNull LiveData<S> liveData, @NonNull Observer<? super S> observer) {
        Source source = new Source(liveData, observer);
        Source source2 = (Source) this.mSources.putIfAbsent(liveData, source);
        if (source2 == null || source2.mObserver == observer) {
            if (source2 == null && hasActiveObservers()) {
                source.plug();
            }
            return;
        }
        throw new IllegalArgumentException("This source was already added with the different observer");
    }

    @CallSuper
    public void onActive() {
        Iterator it = this.mSources.iterator();
        while (it.hasNext()) {
            ((Source) ((Entry) it.next()).getValue()).plug();
        }
    }

    @CallSuper
    public void onInactive() {
        Iterator it = this.mSources.iterator();
        while (it.hasNext()) {
            ((Source) ((Entry) it.next()).getValue()).unplug();
        }
    }

    @MainThread
    public <S> void removeSource(@NonNull LiveData<S> liveData) {
        Source source = (Source) this.mSources.remove(liveData);
        if (source != null) {
            source.unplug();
        }
    }
}
