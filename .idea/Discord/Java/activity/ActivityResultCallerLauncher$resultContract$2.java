package androidx.activity.result;

import android.content.Context;
import android.content.Intent;
import androidx.activity.result.contract.ActivityResultContract;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import u.m.c.k;

/* compiled from: ActivityResultCaller.kt */
public final class ActivityResultCallerLauncher$resultContract$2 extends k implements Function0<AnonymousClass1> {
    public final /* synthetic */ ActivityResultCallerLauncher this$0;

    /* compiled from: ActivityResultCaller.kt */
    /* renamed from: androidx.activity.result.ActivityResultCallerLauncher$resultContract$2$1 */
    public static final class AnonymousClass1 extends ActivityResultContract<Unit, O> {
        public final /* synthetic */ ActivityResultCallerLauncher$resultContract$2 this$0;

        public AnonymousClass1(ActivityResultCallerLauncher$resultContract$2 activityResultCallerLauncher$resultContract$2) {
            this.this$0 = activityResultCallerLauncher$resultContract$2;
        }

        public O parseResult(int i, Intent intent) {
            return this.this$0.this$0.getCallerContract().parseResult(i, intent);
        }

        public Intent createIntent(Context context, Unit unit) {
            j.checkNotNullParameter(context, "context");
            Intent createIntent = this.this$0.this$0.getCallerContract().createIntent(context, this.this$0.this$0.getInput());
            j.checkNotNullExpressionValue(createIntent, "callerContract.createIntent(context, input)");
            return createIntent;
        }
    }

    public ActivityResultCallerLauncher$resultContract$2(ActivityResultCallerLauncher activityResultCallerLauncher) {
        this.this$0 = activityResultCallerLauncher;
        super(0);
    }

    public final AnonymousClass1 invoke() {
        return new AnonymousClass1(this);
    }
}
