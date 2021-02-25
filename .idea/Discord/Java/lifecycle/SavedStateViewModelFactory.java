package androidx.lifecycle;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelProvider.KeyedFactory;
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class SavedStateViewModelFactory extends KeyedFactory {
    private static final Class<?>[] ANDROID_VIEWMODEL_SIGNATURE;
    private static final Class<?>[] VIEWMODEL_SIGNATURE;
    private final Application mApplication;
    private final Bundle mDefaultArgs;
    private final Factory mFactory;
    private final Lifecycle mLifecycle;
    private final SavedStateRegistry mSavedStateRegistry;

    static {
        Class cls = SavedStateHandle.class;
        ANDROID_VIEWMODEL_SIGNATURE = new Class[]{Application.class, cls};
        VIEWMODEL_SIGNATURE = new Class[]{cls};
    }

    public SavedStateViewModelFactory(@Nullable Application application, @NonNull SavedStateRegistryOwner savedStateRegistryOwner) {
        this(application, savedStateRegistryOwner, null);
    }

    private static <T> Constructor<T> findMatchingConstructor(Class<T> cls, Class<?>[] clsArr) {
        for (Constructor constructor : cls.getConstructors()) {
            if (Arrays.equals(clsArr, constructor.getParameterTypes())) {
                return constructor;
            }
        }
        return null;
    }

    @NonNull
    public <T extends ViewModel> T create(@NonNull String str, @NonNull Class<T> cls) {
        Constructor findMatchingConstructor;
        StringBuilder stringBuilder;
        boolean isAssignableFrom = AndroidViewModel.class.isAssignableFrom(cls);
        if (!isAssignableFrom || this.mApplication == null) {
            findMatchingConstructor = findMatchingConstructor(cls, VIEWMODEL_SIGNATURE);
        } else {
            findMatchingConstructor = findMatchingConstructor(cls, ANDROID_VIEWMODEL_SIGNATURE);
        }
        if (findMatchingConstructor == null) {
            return this.mFactory.create(cls);
        }
        ViewModel viewModel;
        SavedStateHandleController create = SavedStateHandleController.create(this.mSavedStateRegistry, this.mLifecycle, str, this.mDefaultArgs);
        if (isAssignableFrom) {
            try {
                if (this.mApplication != null) {
                    viewModel = (ViewModel) findMatchingConstructor.newInstance(new Object[]{this.mApplication, create.getHandle()});
                    viewModel.setTagIfAbsent("androidx.lifecycle.savedstate.vm.tag", create);
                    return viewModel;
                }
            } catch (IllegalAccessException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to access ");
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e);
            } catch (InstantiationException e2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("A ");
                stringBuilder.append(cls);
                stringBuilder.append(" cannot be instantiated.");
                throw new RuntimeException(stringBuilder.toString(), e2);
            } catch (InvocationTargetException e3) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("An exception happened in constructor of ");
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e3.getCause());
            }
        }
        viewModel = (ViewModel) findMatchingConstructor.newInstance(new Object[]{create.getHandle()});
        viewModel.setTagIfAbsent("androidx.lifecycle.savedstate.vm.tag", create);
        return viewModel;
    }

    public void onRequery(@NonNull ViewModel viewModel) {
        SavedStateHandleController.attachHandleIfNeeded(viewModel, this.mSavedStateRegistry, this.mLifecycle);
    }

    @SuppressLint({"LambdaLast"})
    public SavedStateViewModelFactory(@Nullable Application application, @NonNull SavedStateRegistryOwner savedStateRegistryOwner, @Nullable Bundle bundle) {
        Factory instance;
        this.mSavedStateRegistry = savedStateRegistryOwner.getSavedStateRegistry();
        this.mLifecycle = savedStateRegistryOwner.getLifecycle();
        this.mDefaultArgs = bundle;
        this.mApplication = application;
        if (application != null) {
            instance = AndroidViewModelFactory.getInstance(application);
        } else {
            instance = NewInstanceFactory.getInstance();
        }
        this.mFactory = instance;
    }

    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> cls) {
        String canonicalName = cls.getCanonicalName();
        if (canonicalName != null) {
            return create(canonicalName, cls);
        }
        throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
    }
}
