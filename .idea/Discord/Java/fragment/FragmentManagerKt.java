package androidx.fragment.app;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import u.m.c.j;

/* compiled from: FragmentManager.kt */
public final class FragmentManagerKt {
    public static final void commit(FragmentManager fragmentManager, boolean z, Function1<? super FragmentTransaction, Unit> function1) {
        j.checkNotNullParameter(fragmentManager, "$this$commit");
        j.checkNotNullParameter(function1, "body");
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        j.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        function1.invoke(beginTransaction);
        if (z) {
            beginTransaction.commitAllowingStateLoss();
        } else {
            beginTransaction.commit();
        }
    }

    public static /* synthetic */ void commit$default(FragmentManager fragmentManager, boolean z, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            z = false;
        }
        j.checkNotNullParameter(fragmentManager, "$this$commit");
        j.checkNotNullParameter(function1, "body");
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        j.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        function1.invoke(beginTransaction);
        if (z) {
            beginTransaction.commitAllowingStateLoss();
        } else {
            beginTransaction.commit();
        }
    }

    public static final void commitNow(FragmentManager fragmentManager, boolean z, Function1<? super FragmentTransaction, Unit> function1) {
        j.checkNotNullParameter(fragmentManager, "$this$commitNow");
        j.checkNotNullParameter(function1, "body");
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        j.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        function1.invoke(beginTransaction);
        if (z) {
            beginTransaction.commitNowAllowingStateLoss();
        } else {
            beginTransaction.commitNow();
        }
    }

    public static /* synthetic */ void commitNow$default(FragmentManager fragmentManager, boolean z, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            z = false;
        }
        j.checkNotNullParameter(fragmentManager, "$this$commitNow");
        j.checkNotNullParameter(function1, "body");
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        j.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        function1.invoke(beginTransaction);
        if (z) {
            beginTransaction.commitNowAllowingStateLoss();
        } else {
            beginTransaction.commitNow();
        }
    }

    public static final void transaction(FragmentManager fragmentManager, boolean z, boolean z2, Function1<? super FragmentTransaction, Unit> function1) {
        j.checkNotNullParameter(fragmentManager, "$this$transaction");
        j.checkNotNullParameter(function1, "body");
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        j.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        function1.invoke(beginTransaction);
        if (z) {
            if (z2) {
                beginTransaction.commitNowAllowingStateLoss();
            } else {
                beginTransaction.commitNow();
            }
        } else if (z2) {
            beginTransaction.commitAllowingStateLoss();
        } else {
            beginTransaction.commit();
        }
    }

    public static /* synthetic */ void transaction$default(FragmentManager fragmentManager, boolean z, boolean z2, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            z = false;
        }
        if ((i & 2) != 0) {
            z2 = false;
        }
        j.checkNotNullParameter(fragmentManager, "$this$transaction");
        j.checkNotNullParameter(function1, "body");
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        j.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        function1.invoke(beginTransaction);
        if (z) {
            if (z2) {
                beginTransaction.commitNowAllowingStateLoss();
            } else {
                beginTransaction.commitNow();
            }
        } else if (z2) {
            beginTransaction.commitAllowingStateLoss();
        } else {
            beginTransaction.commit();
        }
    }
}
