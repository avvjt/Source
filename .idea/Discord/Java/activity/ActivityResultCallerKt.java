package androidx.activity.result;

import androidx.activity.result.contract.ActivityResultContract;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import u.m.c.j;

/* compiled from: ActivityResultCaller.kt */
public final class ActivityResultCallerKt {
    public static final <I, O> ActivityResultLauncher<Unit> registerForActivityResult(ActivityResultCaller activityResultCaller, ActivityResultContract<I, O> activityResultContract, I i, ActivityResultRegistry activityResultRegistry, Function1<? super O, Unit> function1) {
        j.checkNotNullParameter(activityResultCaller, "$this$registerForActivityResult");
        j.checkNotNullParameter(activityResultContract, "contract");
        j.checkNotNullParameter(activityResultRegistry, "registry");
        j.checkNotNullParameter(function1, "callback");
        ActivityResultLauncher registerForActivityResult = activityResultCaller.registerForActivityResult(activityResultContract, activityResultRegistry, new ActivityResultCallerKt$registerForActivityResult$resultLauncher$1(function1));
        j.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResul…egistry) { callback(it) }");
        return new ActivityResultCallerLauncher(registerForActivityResult, activityResultContract, i);
    }

    public static final <I, O> ActivityResultLauncher<Unit> registerForActivityResult(ActivityResultCaller activityResultCaller, ActivityResultContract<I, O> activityResultContract, I i, Function1<? super O, Unit> function1) {
        j.checkNotNullParameter(activityResultCaller, "$this$registerForActivityResult");
        j.checkNotNullParameter(activityResultContract, "contract");
        j.checkNotNullParameter(function1, "callback");
        ActivityResultLauncher registerForActivityResult = activityResultCaller.registerForActivityResult(activityResultContract, new ActivityResultCallerKt$registerForActivityResult$resultLauncher$2(function1));
        j.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResul…ontract) { callback(it) }");
        return new ActivityResultCallerLauncher(registerForActivityResult, activityResultContract, i);
    }
}
