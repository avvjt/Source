package androidx.activity.result;

import android.content.Intent;
import u.m.c.j;

/* compiled from: ActivityResult.kt */
public final class ActivityResultKt {
    public static final int component1(ActivityResult activityResult) {
        j.checkNotNullParameter(activityResult, "$this$component1");
        return activityResult.getResultCode();
    }

    public static final Intent component2(ActivityResult activityResult) {
        j.checkNotNullParameter(activityResult, "$this$component2");
        return activityResult.getData();
    }
}
