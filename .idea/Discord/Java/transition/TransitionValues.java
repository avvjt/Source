package androidx.transition;

import android.view.View;
import androidx.annotation.NonNull;
import f.d.b.a.a;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransitionValues {
    public final ArrayList<Transition> mTargetedTransitions = new ArrayList();
    public final Map<String, Object> values = new HashMap();
    public View view;

    public boolean equals(Object obj) {
        if (obj instanceof TransitionValues) {
            TransitionValues transitionValues = (TransitionValues) obj;
            if (this.view == transitionValues.view && this.values.equals(transitionValues.values)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.values.hashCode() + (this.view.hashCode() * 31);
    }

    public String toString() {
        StringBuilder L = a.L("TransitionValues@");
        L.append(Integer.toHexString(hashCode()));
        L.append(":\n");
        L = a.P(L.toString(), "    view = ");
        L.append(this.view);
        String str = "\n";
        L.append(str);
        String t = a.t(L.toString(), "    values:");
        for (String str2 : this.values.keySet()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(t);
            stringBuilder.append("    ");
            stringBuilder.append(str2);
            stringBuilder.append(": ");
            stringBuilder.append(this.values.get(str2));
            stringBuilder.append(str);
            t = stringBuilder.toString();
        }
        return t;
    }

    public TransitionValues(@NonNull View view) {
        this.view = view;
    }
}
