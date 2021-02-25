package androidx.lifecycle;

import v.a.r1.d;

/* compiled from: FlowLiveData.kt */
public final class FlowLiveDataConversions$asFlow$1$observer$1<T> implements Observer<T> {
    public final /* synthetic */ d $channel;

    public FlowLiveDataConversions$asFlow$1$observer$1(d dVar) {
        this.$channel = dVar;
    }

    public final void onChanged(T t) {
        this.$channel.offer(t);
    }
}
