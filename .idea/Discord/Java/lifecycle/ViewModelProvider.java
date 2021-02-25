package androidx.lifecycle;

import android.app.Application;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import java.lang.reflect.InvocationTargetException;

public class ViewModelProvider {
    private static final String DEFAULT_KEY = "androidx.lifecycle.ViewModelProvider.DefaultKey";
    private final Factory mFactory;
    private final ViewModelStore mViewModelStore;

    public interface Factory {
        @NonNull
        <T extends ViewModel> T create(@NonNull Class<T> cls);
    }

    public static class OnRequeryFactory {
        public void onRequery(@NonNull ViewModel viewModel) {
        }
    }

    public static abstract class KeyedFactory extends OnRequeryFactory implements Factory {
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> cls) {
            throw new UnsupportedOperationException("create(String, Class<?>) must be called on implementaions of KeyedFactory");
        }

        @NonNull
        public abstract <T extends ViewModel> T create(@NonNull String str, @NonNull Class<T> cls);
    }

    public static class NewInstanceFactory implements Factory {
        private static NewInstanceFactory sInstance;

        @NonNull
        public static NewInstanceFactory getInstance() {
            if (sInstance == null) {
                sInstance = new NewInstanceFactory();
            }
            return sInstance;
        }

        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> cls) {
            StringBuilder stringBuilder;
            String str = "Cannot create an instance of ";
            try {
                return (ViewModel) cls.newInstance();
            } catch (InstantiationException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e);
            } catch (IllegalAccessException e2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e2);
            }
        }
    }

    public static class AndroidViewModelFactory extends NewInstanceFactory {
        private static AndroidViewModelFactory sInstance;
        private Application mApplication;

        public AndroidViewModelFactory(@NonNull Application application) {
            this.mApplication = application;
        }

        @NonNull
        public static AndroidViewModelFactory getInstance(@NonNull Application application) {
            if (sInstance == null) {
                sInstance = new AndroidViewModelFactory(application);
            }
            return sInstance;
        }

        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> cls) {
            StringBuilder stringBuilder;
            String str = "Cannot create an instance of ";
            if (!AndroidViewModel.class.isAssignableFrom(cls)) {
                return super.create(cls);
            }
            try {
                return (ViewModel) cls.getConstructor(new Class[]{Application.class}).newInstance(new Object[]{this.mApplication});
            } catch (NoSuchMethodException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e);
            } catch (IllegalAccessException e2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e2);
            } catch (InstantiationException e3) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e3);
            } catch (InvocationTargetException e4) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(cls);
                throw new RuntimeException(stringBuilder.toString(), e4);
            }
        }
    }

    public ViewModelProvider(@NonNull ViewModelStoreOwner viewModelStoreOwner) {
        Factory defaultViewModelProviderFactory;
        ViewModelStore viewModelStore = viewModelStoreOwner.getViewModelStore();
        if (viewModelStoreOwner instanceof HasDefaultViewModelProviderFactory) {
            defaultViewModelProviderFactory = ((HasDefaultViewModelProviderFactory) viewModelStoreOwner).getDefaultViewModelProviderFactory();
        } else {
            defaultViewModelProviderFactory = NewInstanceFactory.getInstance();
        }
        this(viewModelStore, defaultViewModelProviderFactory);
    }

    @MainThread
    @NonNull
    public <T extends ViewModel> T get(@NonNull Class<T> cls) {
        String canonicalName = cls.getCanonicalName();
        if (canonicalName != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("androidx.lifecycle.ViewModelProvider.DefaultKey:");
            stringBuilder.append(canonicalName);
            return get(stringBuilder.toString(), cls);
        }
        throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
    }

    @MainThread
    @NonNull
    public <T extends ViewModel> T get(@NonNull String str, @NonNull Class<T> cls) {
        ViewModel viewModel = this.mViewModelStore.get(str);
        if (cls.isInstance(viewModel)) {
            Factory factory = this.mFactory;
            if (factory instanceof OnRequeryFactory) {
                ((OnRequeryFactory) factory).onRequery(viewModel);
            }
            return viewModel;
        }
        ViewModel create;
        Factory factory2 = this.mFactory;
        if (factory2 instanceof KeyedFactory) {
            create = ((KeyedFactory) factory2).create(str, cls);
        } else {
            create = factory2.create(cls);
        }
        this.mViewModelStore.put(str, create);
        return create;
    }

    public ViewModelProvider(@NonNull ViewModelStoreOwner viewModelStoreOwner, @NonNull Factory factory) {
        this(viewModelStoreOwner.getViewModelStore(), factory);
    }

    public ViewModelProvider(@NonNull ViewModelStore viewModelStore, @NonNull Factory factory) {
        this.mFactory = factory;
        this.mViewModelStore = viewModelStore;
    }
}
