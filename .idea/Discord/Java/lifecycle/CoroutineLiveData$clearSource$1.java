package androidx.lifecycle;

import kotlin.coroutines.Continuation;
import u.j.h.a.c;
import u.j.h.a.d;

@d(c = "androidx.lifecycle.CoroutineLiveData", f = "CoroutineLiveData.kt", l = {234}, m = "clearSource$lifecycle_livedata_ktx_release")
/* compiled from: CoroutineLiveData.kt */
public final class CoroutineLiveData$clearSource$1 extends c {
    public Object L$0;
    public int label;
    public /* synthetic */ Object result;
    public final /* synthetic */ CoroutineLiveData this$0;

    public CoroutineLiveData$clearSource$1(CoroutineLiveData coroutineLiveData, Continuation continuation) {
        this.this$0 = coroutineLiveData;
        super(continuation);
    }

    public final Object invokeSuspend(Object obj) {
        this.result = obj;
        this.label |= Integer.MIN_VALUE;
        return this.this$0.clearSource$lifecycle_livedata_ktx_release(this);
    }
}
