package androidx.fragment.app;

import androidx.annotation.experimental.Experimental;
import androidx.annotation.experimental.Experimental.Level;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Experimental(level = Level.WARNING)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface FragmentStateManagerControl {
}