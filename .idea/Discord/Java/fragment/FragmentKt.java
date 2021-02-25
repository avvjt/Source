package androidx.fragment.app;

import android.os.Bundle;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import u.m.c.j;

/* compiled from: Fragment.kt */
public final class FragmentKt {
    public static final void clearFragmentResult(Fragment fragment, String str) {
        j.checkNotNullParameter(fragment, "$this$clearFragmentResult");
        j.checkNotNullParameter(str, "requestKey");
        fragment.getParentFragmentManager().clearFragmentResult(str);
    }

    public static final void clearFragmentResultListener(Fragment fragment, String str) {
        j.checkNotNullParameter(fragment, "$this$clearFragmentResultListener");
        j.checkNotNullParameter(str, "requestKey");
        fragment.getParentFragmentManager().clearFragmentResultListener(str);
    }

    public static final void setFragmentResult(Fragment fragment, String str, Bundle bundle) {
        j.checkNotNullParameter(fragment, "$this$setFragmentResult");
        j.checkNotNullParameter(str, "requestKey");
        j.checkNotNullParameter(bundle, "result");
        fragment.getParentFragmentManager().setFragmentResult(str, bundle);
    }

    public static final void setFragmentResultListener(Fragment fragment, String str, Function2<? super String, ? super Bundle, Unit> function2) {
        j.checkNotNullParameter(fragment, "$this$setFragmentResultListener");
        j.checkNotNullParameter(str, "requestKey");
        j.checkNotNullParameter(function2, "listener");
        fragment.getParentFragmentManager().setFragmentResultListener(str, fragment, new FragmentKt$sam$androidx_fragment_app_FragmentResultListener$0(function2));
    }
}
