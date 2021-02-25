package androidx.transition;

import android.graphics.Matrix;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GhostViewUtils {
    private GhostViewUtils() {
    }

    @Nullable
    public static GhostView addGhost(@NonNull View view, @NonNull ViewGroup viewGroup, @Nullable Matrix matrix) {
        if (VERSION.SDK_INT == 28) {
            return GhostViewPlatform.addGhost(view, viewGroup, matrix);
        }
        return GhostViewPort.addGhost(view, viewGroup, matrix);
    }

    public static void removeGhost(View view) {
        if (VERSION.SDK_INT == 28) {
            GhostViewPlatform.removeGhost(view);
        } else {
            GhostViewPort.removeGhost(view);
        }
    }
}
