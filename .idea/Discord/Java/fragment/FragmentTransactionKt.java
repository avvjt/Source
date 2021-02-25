package androidx.fragment.app;

import android.os.Bundle;
import androidx.annotation.IdRes;
import u.m.c.j;

/* compiled from: FragmentTransaction.kt */
public final class FragmentTransactionKt {
    public static final /* synthetic */ <F extends Fragment> FragmentTransaction add(FragmentTransaction fragmentTransaction, @IdRes int i, String str, Bundle bundle) {
        j.checkNotNullParameter(fragmentTransaction, "$this$add");
        j.reifiedOperationMarker();
        throw null;
    }

    public static /* synthetic */ FragmentTransaction add$default(FragmentTransaction fragmentTransaction, int i, String str, Bundle bundle, int i2, Object obj) {
        i = i2 & 2;
        i = i2 & 4;
        j.checkNotNullParameter(fragmentTransaction, "$this$add");
        j.reifiedOperationMarker();
        throw null;
    }

    public static final /* synthetic */ <F extends Fragment> FragmentTransaction replace(FragmentTransaction fragmentTransaction, @IdRes int i, String str, Bundle bundle) {
        j.checkNotNullParameter(fragmentTransaction, "$this$replace");
        j.reifiedOperationMarker();
        throw null;
    }

    public static /* synthetic */ FragmentTransaction replace$default(FragmentTransaction fragmentTransaction, int i, String str, Bundle bundle, int i2, Object obj) {
        i = i2 & 2;
        i = i2 & 4;
        j.checkNotNullParameter(fragmentTransaction, "$this$replace");
        j.reifiedOperationMarker();
        throw null;
    }

    public static final /* synthetic */ <F extends Fragment> FragmentTransaction add(FragmentTransaction fragmentTransaction, String str, Bundle bundle) {
        j.checkNotNullParameter(fragmentTransaction, "$this$add");
        j.checkNotNullParameter(str, "tag");
        j.reifiedOperationMarker();
        throw null;
    }

    public static /* synthetic */ FragmentTransaction add$default(FragmentTransaction fragmentTransaction, String str, Bundle bundle, int i, Object obj) {
        int i2 = i & 2;
        j.checkNotNullParameter(fragmentTransaction, "$this$add");
        j.checkNotNullParameter(str, "tag");
        j.reifiedOperationMarker();
        throw null;
    }
}
