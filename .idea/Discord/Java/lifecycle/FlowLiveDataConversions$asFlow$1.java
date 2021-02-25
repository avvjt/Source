package androidx.lifecycle;

import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import u.j.g.a;
import u.j.h.a.d;
import u.j.h.a.h;
import u.m.c.j;
import v.a.e1;
import v.a.h0;
import v.a.r0;
import v.a.r1.e;
import v.a.s1.c;
import v.a.t1.l;
import v.a.x;

@d(c = "androidx.lifecycle.FlowLiveDataConversions$asFlow$1", f = "FlowLiveData.kt", l = {91, 95, 96}, m = "invokeSuspend")
/* compiled from: FlowLiveData.kt */
public final class FlowLiveDataConversions$asFlow$1 extends h implements Function2<c<? super T>, Continuation<? super Unit>, Object> {
    public final /* synthetic */ LiveData $this_asFlow;
    public Object L$0;
    public Object L$1;
    public Object L$2;
    public Object L$3;
    public Object L$4;
    public int label;
    private c p$;

    public FlowLiveDataConversions$asFlow$1(LiveData liveData, Continuation continuation) {
        this.$this_asFlow = liveData;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkParameterIsNotNull(continuation, "completion");
        FlowLiveDataConversions$asFlow$1 flowLiveDataConversions$asFlow$1 = new FlowLiveDataConversions$asFlow$1(this.$this_asFlow, continuation);
        flowLiveDataConversions$asFlow$1.p$ = (c) obj;
        return flowLiveDataConversions$asFlow$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((FlowLiveDataConversions$asFlow$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ac A:{Catch:{ all -> 0x00de }} */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x009d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x009e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final Object invokeSuspend(Object obj) {
        Object obj2;
        final Observer flowLiveDataConversions$asFlow$1$observer$1;
        Object obj3;
        Observer observer;
        FlowLiveDataConversions$asFlow$1 flowLiveDataConversions$asFlow$1;
        e eVar;
        a a;
        Throwable th;
        x xVar;
        r0 r0Var = r0.f;
        a aVar = a.COROUTINE_SUSPENDED;
        int i = this.label;
        e eVar2;
        c cVar;
        Object obj4;
        x xVar2;
        if (i == 0) {
            g.throwOnFailure(obj);
            obj2 = this.p$;
            v.a.r1.g gVar = new v.a.r1.g(null);
            flowLiveDataConversions$asFlow$1$observer$1 = new FlowLiveDataConversions$asFlow$1$observer$1(gVar);
            x xVar3 = h0.a;
            e1 t = l.b.t();
            AnonymousClass1 anonymousClass1 = new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null) {
                public int label;
                private CoroutineScope p$;
                public final /* synthetic */ FlowLiveDataConversions$asFlow$1 this$0;

                public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                    j.checkParameterIsNotNull(continuation, "completion");
                    AnonymousClass1 anonymousClass1 = /* anonymous class already generated */;
                    anonymousClass1.p$ = (CoroutineScope) obj;
                    return anonymousClass1;
                }

                public final Object invoke(Object obj, Object obj2) {
                    return ((AnonymousClass1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
                }

                public final Object invokeSuspend(Object obj) {
                    if (this.label == 0) {
                        g.throwOnFailure(obj);
                        this.this$0.$this_asFlow.observeForever(flowLiveDataConversions$asFlow$1$observer$1);
                        return Unit.a;
                    }
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
            };
            this.L$0 = obj2;
            this.L$1 = gVar;
            this.L$2 = flowLiveDataConversions$asFlow$1$observer$1;
            this.label = 1;
            if (g.j0(t, anonymousClass1, this) == aVar) {
                return aVar;
            }
            obj3 = gVar;
        } else if (i == 1) {
            flowLiveDataConversions$asFlow$1$observer$1 = (Observer) this.L$2;
            obj3 = (v.a.r1.d) this.L$1;
            obj2 = (c) this.L$0;
            g.throwOnFailure(obj);
        } else if (i == 2) {
            eVar2 = (e) this.L$3;
            observer = (Observer) this.L$2;
            obj2 = (v.a.r1.d) this.L$1;
            cVar = (c) this.L$0;
            g.throwOnFailure(obj);
            obj4 = cVar;
            flowLiveDataConversions$asFlow$1 = this;
            if (!((Boolean) obj).booleanValue()) {
                xVar2 = h0.a;
                g.N(r0Var, l.b.t(), null, new Function2<CoroutineScope, Continuation<? super Unit>, Object>(flowLiveDataConversions$asFlow$1, null) {
                    public int label;
                    private CoroutineScope p$;
                    public final /* synthetic */ FlowLiveDataConversions$asFlow$1 this$0;

                    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                        j.checkParameterIsNotNull(continuation, "completion");
                        AnonymousClass2 anonymousClass2 = /* anonymous class already generated */;
                        anonymousClass2.p$ = (CoroutineScope) obj;
                        return anonymousClass2;
                    }

                    public final Object invoke(Object obj, Object obj2) {
                        return ((AnonymousClass2) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
                    }

                    public final Object invokeSuspend(Object obj) {
                        if (this.label == 0) {
                            g.throwOnFailure(obj);
                            this.this$0.$this_asFlow.removeObserver(observer);
                            return Unit.a;
                        }
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                }, 2, null);
            }
            xVar2 = h0.a;
            g.N(r0Var, l.b.t(), null, /* anonymous class already generated */, 2, null);
            return Unit.a;
        } else if (i == 3) {
            eVar2 = (e) this.L$4;
            observer = (Observer) this.L$2;
            v.a.r1.d dVar = (v.a.r1.d) this.L$1;
            cVar = (c) this.L$0;
            try {
                g.throwOnFailure(obj);
                eVar = eVar2;
                flowLiveDataConversions$asFlow$1$observer$1 = observer;
                obj3 = dVar;
                obj2 = cVar;
                flowLiveDataConversions$asFlow$1 = this;
                try {
                    flowLiveDataConversions$asFlow$1.L$0 = obj2;
                    flowLiveDataConversions$asFlow$1.L$1 = obj3;
                    flowLiveDataConversions$asFlow$1.L$2 = flowLiveDataConversions$asFlow$1$observer$1;
                    flowLiveDataConversions$asFlow$1.L$3 = eVar;
                    flowLiveDataConversions$asFlow$1.label = 2;
                    a = eVar.a(flowLiveDataConversions$asFlow$1);
                    return aVar;
                } catch (Throwable th2) {
                    th = th2;
                    observer = flowLiveDataConversions$asFlow$1$observer$1;
                    xVar = h0.a;
                    g.N(r0Var, l.b.t(), null, /* anonymous class already generated */, 2, null);
                    throw th;
                }
                if (a == aVar) {
                    return aVar;
                }
                Observer observer2 = flowLiveDataConversions$asFlow$1$observer$1;
                eVar2 = eVar;
                obj = a;
                obj4 = obj2;
                obj2 = obj3;
                observer = observer2;
                try {
                    if (!((Boolean) obj).booleanValue()) {
                        obj = eVar2.next();
                        flowLiveDataConversions$asFlow$1.L$0 = obj4;
                        flowLiveDataConversions$asFlow$1.L$1 = obj2;
                        flowLiveDataConversions$asFlow$1.L$2 = observer;
                        flowLiveDataConversions$asFlow$1.L$3 = obj;
                        flowLiveDataConversions$asFlow$1.L$4 = eVar2;
                        flowLiveDataConversions$asFlow$1.label = 3;
                        if (obj4.emit(obj, flowLiveDataConversions$asFlow$1) == aVar) {
                            return aVar;
                        }
                        eVar = eVar2;
                        flowLiveDataConversions$asFlow$1$observer$1 = observer;
                        obj3 = obj2;
                        obj2 = obj4;
                        flowLiveDataConversions$asFlow$1.L$0 = obj2;
                        flowLiveDataConversions$asFlow$1.L$1 = obj3;
                        flowLiveDataConversions$asFlow$1.L$2 = flowLiveDataConversions$asFlow$1$observer$1;
                        flowLiveDataConversions$asFlow$1.L$3 = eVar;
                        flowLiveDataConversions$asFlow$1.label = 2;
                        a = eVar.a(flowLiveDataConversions$asFlow$1);
                        if (a == aVar) {
                        }
                        return aVar;
                    }
                    xVar2 = h0.a;
                    g.N(r0Var, l.b.t(), null, /* anonymous class already generated */, 2, null);
                    return Unit.a;
                } catch (Throwable th3) {
                    th = th3;
                    xVar = h0.a;
                    g.N(r0Var, l.b.t(), null, /* anonymous class already generated */, 2, null);
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                flowLiveDataConversions$asFlow$1 = this;
                xVar = h0.a;
                g.N(r0Var, l.b.t(), null, /* anonymous class already generated */, 2, null);
                throw th;
            }
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        try {
            eVar = obj3.iterator();
            flowLiveDataConversions$asFlow$1 = this;
            flowLiveDataConversions$asFlow$1.L$0 = obj2;
            flowLiveDataConversions$asFlow$1.L$1 = obj3;
            flowLiveDataConversions$asFlow$1.L$2 = flowLiveDataConversions$asFlow$1$observer$1;
            flowLiveDataConversions$asFlow$1.L$3 = eVar;
            flowLiveDataConversions$asFlow$1.label = 2;
            a = eVar.a(flowLiveDataConversions$asFlow$1);
            if (a == aVar) {
            }
            return aVar;
        } catch (Throwable th5) {
            th = th5;
            flowLiveDataConversions$asFlow$1 = this;
            observer = flowLiveDataConversions$asFlow$1$observer$1;
            xVar = h0.a;
            g.N(r0Var, l.b.t(), null, /* anonymous class already generated */, 2, null);
            throw th;
        }
    }
}
