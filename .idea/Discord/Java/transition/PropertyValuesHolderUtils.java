package androidx.transition;

import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;

public class PropertyValuesHolderUtils {
    private PropertyValuesHolderUtils() {
    }

    public static PropertyValuesHolder ofPointF(Property<?, PointF> property, Path path) {
        return PropertyValuesHolder.ofObject(property, null, path);
    }
}
