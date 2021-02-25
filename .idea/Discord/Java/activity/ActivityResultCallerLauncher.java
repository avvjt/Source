package androidx.activity.result;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.core.app.ActivityOptionsCompat;
import f.i.a.f.f.o.g;
import kotlin.Lazy;
import kotlin.Unit;
import u.m.c.j;

/* compiled from: ActivityResultCaller.kt */
public final class ActivityResultCallerLauncher<I, O> extends ActivityResultLauncher<Unit> {
    private final ActivityResultContract<I, O> callerContract;
    private final I input;
    private final ActivityResultLauncher<I> launcher;
    private final Lazy resultContract$delegate = g.lazy(new ActivityResultCallerLauncher$resultContract$2(this));

    public ActivityResultCallerLauncher(ActivityResultLauncher<I> activityResultLauncher, ActivityResultContract<I, O> activityResultContract, I i) {
        j.checkNotNullParameter(activityResultLauncher, "launcher");
        j.checkNotNullParameter(activityResultContract, "callerContract");
        this.launcher = activityResultLauncher;
        this.callerContract = activityResultContract;
        this.input = i;
    }

    public final ActivityResultContract<I, O> getCallerContract() {
        return this.callerContract;
    }

    public ActivityResultContract<Unit, O> getContract() {
        return getResultContract();
    }

    public final I getInput() {
        return this.input;
    }

    public final ActivityResultLauncher<I> getLauncher() {
        return this.launcher;
    }

    public final ActivityResultContract<Unit, O> getResultContract() {
        return (ActivityResultContract) this.resultContract$delegate.getValue();
    }

    public void unregister() {
        this.launcher.unregister();
    }

    public void launch(Unit unit, ActivityOptionsCompat activityOptionsCompat) {
        this.launcher.launch(this.input, activityOptionsCompat);
    }
}
