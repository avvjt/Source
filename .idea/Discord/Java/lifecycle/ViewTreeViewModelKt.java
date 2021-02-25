package androidx.lifecycle;

import android.view.View;
import u.m.c.j;

/* compiled from: ViewTreeViewModel.kt */
public final class ViewTreeViewModelKt {
    public static final ViewModelStoreOwner findViewTreeViewModelStoreOwner(View view) {
        j.checkNotNullParameter(view, "$this$findViewTreeViewModelStoreOwner");
        return ViewTreeViewModelStoreOwner.get(view);
    }
}
