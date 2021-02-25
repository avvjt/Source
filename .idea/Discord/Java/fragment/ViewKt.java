package androidx.fragment.app;

import android.view.View;
import u.m.c.j;

/* compiled from: View.kt */
public final class ViewKt {
    public static final <F extends Fragment> F findFragment(View view) {
        j.checkNotNullParameter(view, "$this$findFragment");
        Fragment findFragment = FragmentManager.findFragment(view);
        j.checkNotNullExpressionValue(findFragment, "FragmentManager.findFragment(this)");
        return findFragment;
    }
}
