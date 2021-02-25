package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlinx.coroutines.Job;
import u.j.f;
import u.j.g.a;
import u.m.c.j;
import u.m.c.k;
import v.a.h0;
import v.a.i0;
import v.a.k1;
import v.a.t1.l;
import v.a.x;

/* compiled from: CoroutineLiveData.kt */
public final class CoroutineLiveData<T> extends MediatorLiveData<T> {
    private BlockRunner<T> blockRunner;
    private EmittedSource emittedSource;

    /* compiled from: CoroutineLiveData.kt */
    /* renamed from: androidx.lifecycle.CoroutineLiveData$1 */
    public static final class AnonymousClass1 extends k implements Function0<Unit> {
        public final /* synthetic */ CoroutineLiveData this$0;

        public AnonymousClass1(CoroutineLiveData coroutineLiveData) {
            this.this$0 = coroutineLiveData;
            super(0);
        }

        public final void invoke() {
            this.this$0.blockRunner = null;
        }
    }

    public /* synthetic */ CoroutineLiveData(CoroutineContext coroutineContext, long j, Function2 function2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i & 1) != 0) {
            coroutineContext = f.f;
        }
        if ((i & 2) != 0) {
            j = 5000;
        }
        this(coroutineContext, j, function2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0021  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final Object clearSource$lifecycle_livedata_ktx_release(Continuation<? super Unit> continuation) {
        CoroutineLiveData$clearSource$1 coroutineLiveData$clearSource$1;
        Object obj;
        a aVar;
        int i;
        CoroutineLiveData coroutineLiveData;
        Unit unit;
        if (continuation instanceof CoroutineLiveData$clearSource$1) {
            coroutineLiveData$clearSource$1 = (CoroutineLiveData$clearSource$1) continuation;
            int i2 = coroutineLiveData$clearSource$1.label;
            if ((i2 & Integer.MIN_VALUE) != 0) {
                coroutineLiveData$clearSource$1.label = i2 - Integer.MIN_VALUE;
                obj = coroutineLiveData$clearSource$1.result;
                aVar = a.COROUTINE_SUSPENDED;
                i = coroutineLiveData$clearSource$1.label;
                if (i != 0) {
                    g.throwOnFailure(obj);
                    EmittedSource emittedSource = this.emittedSource;
                    if (emittedSource != null) {
                        coroutineLiveData$clearSource$1.L$0 = this;
                        coroutineLiveData$clearSource$1.label = 1;
                        obj = emittedSource.disposeNow(coroutineLiveData$clearSource$1);
                        if (obj == aVar) {
                            return aVar;
                        }
                        coroutineLiveData = this;
                    } else {
                        coroutineLiveData = this;
                        coroutineLiveData.emittedSource = null;
                        return Unit.a;
                    }
                } else if (i == 1) {
                    coroutineLiveData = (CoroutineLiveData) coroutineLiveData$clearSource$1.L$0;
                    g.throwOnFailure(obj);
                } else {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                unit = (Unit) obj;
                coroutineLiveData.emittedSource = null;
                return Unit.a;
            }
        }
        coroutineLiveData$clearSource$1 = new CoroutineLiveData$clearSource$1(this, continuation);
        obj = coroutineLiveData$clearSource$1.result;
        aVar = a.COROUTINE_SUSPENDED;
        i = coroutineLiveData$clearSource$1.label;
        if (i != 0) {
        }
        unit = (Unit) obj;
        coroutineLiveData.emittedSource = null;
        return Unit.a;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0022  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0066 A:{RETURN} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final Object emitSource$lifecycle_livedata_ktx_release(LiveData<T> liveData, Continuation<? super i0> continuation) {
        CoroutineLiveData$emitSource$1 coroutineLiveData$emitSource$1;
        Object obj;
        a aVar;
        int i;
        CoroutineLiveData coroutineLiveData;
        EmittedSource emittedSource;
        if (continuation instanceof CoroutineLiveData$emitSource$1) {
            coroutineLiveData$emitSource$1 = (CoroutineLiveData$emitSource$1) continuation;
            int i2 = coroutineLiveData$emitSource$1.label;
            if ((i2 & Integer.MIN_VALUE) != 0) {
                coroutineLiveData$emitSource$1.label = i2 - Integer.MIN_VALUE;
                obj = coroutineLiveData$emitSource$1.result;
                aVar = a.COROUTINE_SUSPENDED;
                i = coroutineLiveData$emitSource$1.label;
                LiveData liveData2;
                if (i != 0) {
                    g.throwOnFailure(obj);
                    coroutineLiveData$emitSource$1.L$0 = this;
                    coroutineLiveData$emitSource$1.L$1 = liveData;
                    coroutineLiveData$emitSource$1.label = 1;
                    if (clearSource$lifecycle_livedata_ktx_release(coroutineLiveData$emitSource$1) == aVar) {
                        return aVar;
                    }
                    obj = liveData;
                    coroutineLiveData = this;
                } else if (i == 1) {
                    liveData2 = (LiveData) coroutineLiveData$emitSource$1.L$1;
                    CoroutineLiveData coroutineLiveData2 = (CoroutineLiveData) coroutineLiveData$emitSource$1.L$0;
                    g.throwOnFailure(obj);
                    obj = liveData2;
                    coroutineLiveData = coroutineLiveData2;
                } else if (i == 2) {
                    liveData2 = (LiveData) coroutineLiveData$emitSource$1.L$1;
                    coroutineLiveData = (CoroutineLiveData) coroutineLiveData$emitSource$1.L$0;
                    g.throwOnFailure(obj);
                    emittedSource = (EmittedSource) obj;
                    coroutineLiveData.emittedSource = emittedSource;
                    return emittedSource;
                } else {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                coroutineLiveData$emitSource$1.L$0 = coroutineLiveData;
                coroutineLiveData$emitSource$1.L$1 = obj;
                coroutineLiveData$emitSource$1.label = 2;
                obj = CoroutineLiveDataKt.addDisposableSource(coroutineLiveData, obj, coroutineLiveData$emitSource$1);
                if (obj == aVar) {
                    return aVar;
                }
                emittedSource = (EmittedSource) obj;
                coroutineLiveData.emittedSource = emittedSource;
                return emittedSource;
            }
        }
        coroutineLiveData$emitSource$1 = new CoroutineLiveData$emitSource$1(this, continuation);
        obj = coroutineLiveData$emitSource$1.result;
        aVar = a.COROUTINE_SUSPENDED;
        i = coroutineLiveData$emitSource$1.label;
        if (i != 0) {
        }
        coroutineLiveData$emitSource$1.L$0 = coroutineLiveData;
        coroutineLiveData$emitSource$1.L$1 = obj;
        coroutineLiveData$emitSource$1.label = 2;
        obj = CoroutineLiveDataKt.addDisposableSource(coroutineLiveData, obj, coroutineLiveData$emitSource$1);
        if (obj == aVar) {
        }
        emittedSource = (EmittedSource) obj;
        coroutineLiveData.emittedSource = emittedSource;
        return emittedSource;
    }

    public void onActive() {
        super.onActive();
        BlockRunner blockRunner = this.blockRunner;
        if (blockRunner != null) {
            blockRunner.maybeRun();
        }
    }

    public void onInactive() {
        super.onInactive();
        BlockRunner blockRunner = this.blockRunner;
        if (blockRunner != null) {
            blockRunner.cancel();
        }
    }

    public CoroutineLiveData(CoroutineContext coroutineContext, long j, Function2<? super LiveDataScope<T>, ? super Continuation<? super Unit>, ? extends Object> function2) {
        j.checkParameterIsNotNull(coroutineContext, "context");
        j.checkParameterIsNotNull(function2, "block");
        k1 k1Var = new k1((Job) coroutineContext.get(Job.e));
        x xVar = h0.a;
        this.blockRunner = new BlockRunner(this, function2, j, g.a(l.b.t().plus(coroutineContext).plus(k1Var)), new AnonymousClass1(this));
    }
}
