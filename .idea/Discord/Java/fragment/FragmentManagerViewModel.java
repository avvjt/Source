package androidx.fragment.app;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class FragmentManagerViewModel extends ViewModel {
    private static final Factory FACTORY = new Factory() {
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> cls) {
            return new FragmentManagerViewModel(true);
        }
    };
    private static final String TAG = "FragmentManager";
    private final HashMap<String, FragmentManagerViewModel> mChildNonConfigs = new HashMap();
    private boolean mHasBeenCleared = false;
    private boolean mHasSavedSnapshot = false;
    private boolean mIsStateSaved = false;
    private final HashMap<String, Fragment> mRetainedFragments = new HashMap();
    private final boolean mStateAutomaticallySaved;
    private final HashMap<String, ViewModelStore> mViewModelStores = new HashMap();

    public FragmentManagerViewModel(boolean z) {
        this.mStateAutomaticallySaved = z;
    }

    @NonNull
    public static FragmentManagerViewModel getInstance(ViewModelStore viewModelStore) {
        return (FragmentManagerViewModel) new ViewModelProvider(viewModelStore, FACTORY).get(FragmentManagerViewModel.class);
    }

    public void addRetainedFragment(@NonNull Fragment fragment) {
        String str = "FragmentManager";
        if (this.mIsStateSaved) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v(str, "Ignoring addRetainedFragment as the state is already saved");
            }
        } else if (!this.mRetainedFragments.containsKey(fragment.mWho)) {
            this.mRetainedFragments.put(fragment.mWho, fragment);
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Updating retained Fragments: Added ");
                stringBuilder.append(fragment);
                Log.v(str, stringBuilder.toString());
            }
        }
    }

    public void clearNonConfigState(@NonNull Fragment fragment) {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Clearing non-config state for ");
            stringBuilder.append(fragment);
            Log.d("FragmentManager", stringBuilder.toString());
        }
        FragmentManagerViewModel fragmentManagerViewModel = (FragmentManagerViewModel) this.mChildNonConfigs.get(fragment.mWho);
        if (fragmentManagerViewModel != null) {
            fragmentManagerViewModel.onCleared();
            this.mChildNonConfigs.remove(fragment.mWho);
        }
        ViewModelStore viewModelStore = (ViewModelStore) this.mViewModelStores.get(fragment.mWho);
        if (viewModelStore != null) {
            viewModelStore.clear();
            this.mViewModelStores.remove(fragment.mWho);
        }
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || FragmentManagerViewModel.class != obj.getClass()) {
            return false;
        }
        FragmentManagerViewModel fragmentManagerViewModel = (FragmentManagerViewModel) obj;
        if (!(this.mRetainedFragments.equals(fragmentManagerViewModel.mRetainedFragments) && this.mChildNonConfigs.equals(fragmentManagerViewModel.mChildNonConfigs) && this.mViewModelStores.equals(fragmentManagerViewModel.mViewModelStores))) {
            z = false;
        }
        return z;
    }

    @Nullable
    public Fragment findRetainedFragmentByWho(String str) {
        return (Fragment) this.mRetainedFragments.get(str);
    }

    @NonNull
    public FragmentManagerViewModel getChildNonConfig(@NonNull Fragment fragment) {
        FragmentManagerViewModel fragmentManagerViewModel = (FragmentManagerViewModel) this.mChildNonConfigs.get(fragment.mWho);
        if (fragmentManagerViewModel != null) {
            return fragmentManagerViewModel;
        }
        fragmentManagerViewModel = new FragmentManagerViewModel(this.mStateAutomaticallySaved);
        this.mChildNonConfigs.put(fragment.mWho, fragmentManagerViewModel);
        return fragmentManagerViewModel;
    }

    @NonNull
    public Collection<Fragment> getRetainedFragments() {
        return new ArrayList(this.mRetainedFragments.values());
    }

    @Deprecated
    @Nullable
    public FragmentManagerNonConfig getSnapshot() {
        if (this.mRetainedFragments.isEmpty() && this.mChildNonConfigs.isEmpty() && this.mViewModelStores.isEmpty()) {
            return null;
        }
        HashMap hashMap = new HashMap();
        for (Entry entry : this.mChildNonConfigs.entrySet()) {
            FragmentManagerNonConfig snapshot = ((FragmentManagerViewModel) entry.getValue()).getSnapshot();
            if (snapshot != null) {
                hashMap.put(entry.getKey(), snapshot);
            }
        }
        this.mHasSavedSnapshot = true;
        if (this.mRetainedFragments.isEmpty() && hashMap.isEmpty() && this.mViewModelStores.isEmpty()) {
            return null;
        }
        return new FragmentManagerNonConfig(new ArrayList(this.mRetainedFragments.values()), hashMap, new HashMap(this.mViewModelStores));
    }

    @NonNull
    public ViewModelStore getViewModelStore(@NonNull Fragment fragment) {
        ViewModelStore viewModelStore = (ViewModelStore) this.mViewModelStores.get(fragment.mWho);
        if (viewModelStore != null) {
            return viewModelStore;
        }
        viewModelStore = new ViewModelStore();
        this.mViewModelStores.put(fragment.mWho, viewModelStore);
        return viewModelStore;
    }

    public int hashCode() {
        return this.mViewModelStores.hashCode() + ((this.mChildNonConfigs.hashCode() + (this.mRetainedFragments.hashCode() * 31)) * 31);
    }

    public boolean isCleared() {
        return this.mHasBeenCleared;
    }

    public void onCleared() {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onCleared called for ");
            stringBuilder.append(this);
            Log.d("FragmentManager", stringBuilder.toString());
        }
        this.mHasBeenCleared = true;
    }

    public void removeRetainedFragment(@NonNull Fragment fragment) {
        String str = "FragmentManager";
        if (this.mIsStateSaved) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v(str, "Ignoring removeRetainedFragment as the state is already saved");
            }
            return;
        }
        if ((this.mRetainedFragments.remove(fragment.mWho) != null ? 1 : null) != null && FragmentManager.isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Updating retained Fragments: Removed ");
            stringBuilder.append(fragment);
            Log.v(str, stringBuilder.toString());
        }
    }

    @Deprecated
    public void restoreFromSnapshot(@Nullable FragmentManagerNonConfig fragmentManagerNonConfig) {
        this.mRetainedFragments.clear();
        this.mChildNonConfigs.clear();
        this.mViewModelStores.clear();
        if (fragmentManagerNonConfig != null) {
            Collection<Fragment> fragments = fragmentManagerNonConfig.getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        this.mRetainedFragments.put(fragment.mWho, fragment);
                    }
                }
            }
            Map childNonConfigs = fragmentManagerNonConfig.getChildNonConfigs();
            if (childNonConfigs != null) {
                for (Entry entry : childNonConfigs.entrySet()) {
                    FragmentManagerViewModel fragmentManagerViewModel = new FragmentManagerViewModel(this.mStateAutomaticallySaved);
                    fragmentManagerViewModel.restoreFromSnapshot((FragmentManagerNonConfig) entry.getValue());
                    this.mChildNonConfigs.put(entry.getKey(), fragmentManagerViewModel);
                }
            }
            Map viewModelStores = fragmentManagerNonConfig.getViewModelStores();
            if (viewModelStores != null) {
                this.mViewModelStores.putAll(viewModelStores);
            }
        }
        this.mHasSavedSnapshot = false;
    }

    public void setIsStateSaved(boolean z) {
        this.mIsStateSaved = z;
    }

    public boolean shouldDestroy(@NonNull Fragment fragment) {
        if (!this.mRetainedFragments.containsKey(fragment.mWho)) {
            return true;
        }
        if (this.mStateAutomaticallySaved) {
            return this.mHasBeenCleared;
        }
        return this.mHasSavedSnapshot ^ 1;
    }

    @NonNull
    public String toString() {
        String str;
        StringBuilder stringBuilder = new StringBuilder("FragmentManagerViewModel{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append("} Fragments (");
        Iterator it = this.mRetainedFragments.values().iterator();
        while (true) {
            str = ", ";
            if (!it.hasNext()) {
                break;
            }
            stringBuilder.append(it.next());
            if (it.hasNext()) {
                stringBuilder.append(str);
            }
        }
        stringBuilder.append(") Child Non Config (");
        it = this.mChildNonConfigs.keySet().iterator();
        while (it.hasNext()) {
            stringBuilder.append((String) it.next());
            if (it.hasNext()) {
                stringBuilder.append(str);
            }
        }
        stringBuilder.append(") ViewModelStores (");
        it = this.mViewModelStores.keySet().iterator();
        while (it.hasNext()) {
            stringBuilder.append((String) it.next());
            if (it.hasNext()) {
                stringBuilder.append(str);
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
