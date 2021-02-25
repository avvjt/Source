package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import u.j.h.a.d;
import u.j.h.a.h;
import u.m.c.j;

@d(c = "androidx.lifecycle.CoroutineLiveDataKt$addDisposableSource$2", f = "CoroutineLiveData.kt", l = {}, m = "invokeSuspend")
/* compiled from: CoroutineLiveData.kt */
public final class CoroutineLiveDataKt$addDisposableSource$2 extends h implements Function2<CoroutineScope, Continuation<? super EmittedSource>, Object> {
    public final /* synthetic */ LiveData $source;
    public final /* synthetic */ MediatorLiveData $this_addDisposableSource;
    public int label;
    private CoroutineScope p$;

    /* compiled from: CoroutineLiveData.kt */
    /* renamed from: androidx.lifecycle.CoroutineLiveDataKt$addDisposableSource$2$1 */
    public static final class AnonymousClass1<T> implements Observer<S> {
        public final /* synthetic */ CoroutineLiveDataKt$addDisposableSource$2 this$0;

        public AnonymousClass1(CoroutineLiveDataKt$addDisposableSource$2 coroutineLiveDataKt$addDisposableSource$2) {
            this.this$0 = coroutineLiveDataKt$addDisposableSource$2;
        }

        public final void onChanged(T t) {
            this.this$0.$this_addDisposableSource.setValue(t);
        }
    }

    public CoroutineLiveDataKt$addDisposableSource$2(MediatorLiveData mediatorLiveData, LiveData liveData, Continuation continuation) {
        this.$this_addDisposableSource = mediatorLiveData;
        this.$source = liveData;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkParameterIsNotNull(continuation, "completion");
        CoroutineLiveDataKt$addDisposableSource$2 coroutineLiveDataKt$addDisposableSource$2 = new CoroutineLiveDataKt$addDisposableSource$2(this.$this_addDisposableSource, this.$source, continuation);
        coroutineLiveDataKt$addDisposableSource$2.p$ = (CoroutineScope) obj;
        return coroutineLiveDataKt$addDisposableSource$2;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((CoroutineLiveDataKt$addDisposableSource$2) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        if (this.label == 0) {
            g.throwOnFailure(obj);
            this.$this_addDisposableSource.addSource(this.$source, new AnonymousClass1(this));
            return new EmittedSource(this.$source, this.$this_addDisposableSource);
        }
        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
    }
}
