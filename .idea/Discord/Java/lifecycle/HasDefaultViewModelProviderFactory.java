package androidx.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider.Factory;

public interface HasDefaultViewModelProviderFactory {
    @NonNull
    Factory getDefaultViewModelProviderFactory();
}
