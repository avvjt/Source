package androidx.fragment.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import kotlin.jvm.functions.Function2;
import u.m.c.j;

/* compiled from: Fragment.kt */
public final class FragmentKt$sam$androidx_fragment_app_FragmentResultListener$0 implements FragmentResultListener {
    private final /* synthetic */ Function2 function;

    public FragmentKt$sam$androidx_fragment_app_FragmentResultListener$0(Function2 function2) {
        this.function = function2;
    }

    public final /* synthetic */ void onFragmentResult(@NonNull String str, @NonNull Bundle bundle) {
        j.checkNotNullParameter(str, "p0");
        j.checkNotNullParameter(bundle, "p1");
        j.checkNotNullExpressionValue(this.function.invoke(str, bundle), "invoke(...)");
    }
}
