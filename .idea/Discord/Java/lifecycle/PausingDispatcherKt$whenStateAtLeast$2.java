package androidx.lifecycle;

import androidx.lifecycle.Lifecycle.State;
import f.i.a.f.f.o.g;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import u.j.g.a;
import u.j.h.a.d;
import u.j.h.a.h;
import u.m.c.j;

@d(c = "androidx.lifecycle.PausingDispatcherKt$whenStateAtLeast$2", f = "PausingDispatcher.kt", l = {162}, m = "invokeSuspend")
/* compiled from: PausingDispatcher.kt */
public final class PausingDispatcherKt$whenStateAtLeast$2 extends h implements Function2<CoroutineScope, Continuation<? super T>, Object> {
    public final /* synthetic */ Function2 $block;
    public final /* synthetic */ State $minState;
    public final /* synthetic */ Lifecycle $this_whenStateAtLeast;
    private /* synthetic */ Object L$0;
    public int label;

    public PausingDispatcherKt$whenStateAtLeast$2(Lifecycle lifecycle, State state, Function2 function2, Continuation continuation) {
        this.$this_whenStateAtLeast = lifecycle;
        this.$minState = state;
        this.$block = function2;
        super(2, continuation);
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        j.checkNotNullParameter(continuation, "completion");
        PausingDispatcherKt$whenStateAtLeast$2 pausingDispatcherKt$whenStateAtLeast$2 = new PausingDispatcherKt$whenStateAtLeast$2(this.$this_whenStateAtLeast, this.$minState, this.$block, continuation);
        pausingDispatcherKt$whenStateAtLeast$2.L$0 = obj;
        return pausingDispatcherKt$whenStateAtLeast$2;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((PausingDispatcherKt$whenStateAtLeast$2) create(obj, (Continuation) obj2)).invokeSuspend(Unit.a);
    }

    public final Object invokeSuspend(Object obj) {
        LifecycleController lifecycleController;
        Throwable th;
        a aVar = a.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            g.throwOnFailure(obj);
            Job job = (Job) ((CoroutineScope) this.L$0).getCoroutineContext().get(Job.e);
            if (job != null) {
                PausingDispatcher pausingDispatcher = new PausingDispatcher();
                LifecycleController lifecycleController2 = new LifecycleController(this.$this_whenStateAtLeast, this.$minState, pausingDispatcher.dispatchQueue, job);
                try {
                    Function2 function2 = this.$block;
                    this.L$0 = lifecycleController2;
                    this.label = 1;
                    obj = g.j0(pausingDispatcher, function2, this);
                    if (obj == aVar) {
                        return aVar;
                    }
                    lifecycleController = lifecycleController2;
                } catch (Throwable th2) {
                    th = th2;
                    lifecycleController = lifecycleController2;
                    lifecycleController.finish();
                    throw th;
                }
            }
            throw new IllegalStateException("when[State] methods should have a parent job".toString());
        } else if (i == 1) {
            lifecycleController = (LifecycleController) this.L$0;
            try {
                g.throwOnFailure(obj);
            } catch (Throwable th3) {
                th = th3;
            }
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        lifecycleController.finish();
        return obj;
    }
}
