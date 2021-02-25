package androidx.fragment.app;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import f.d.b.a.a;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FragmentStore {
    private static final String TAG = "FragmentManager";
    private final HashMap<String, FragmentStateManager> mActive = new HashMap();
    private final ArrayList<Fragment> mAdded = new ArrayList();
    private FragmentManagerViewModel mNonConfig;

    public void addFragment(@NonNull Fragment fragment) {
        if (this.mAdded.contains(fragment)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fragment already added: ");
            stringBuilder.append(fragment);
            throw new IllegalStateException(stringBuilder.toString());
        }
        synchronized (this.mAdded) {
            this.mAdded.add(fragment);
        }
        fragment.mAdded = true;
    }

    public void burpActive() {
        this.mActive.values().removeAll(Collections.singleton(null));
    }

    public boolean containsActiveFragment(@NonNull String str) {
        return this.mActive.get(str) != null;
    }

    public void dispatchStateChange(int i) {
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                fragmentStateManager.setFragmentManagerState(i);
            }
        }
    }

    public void dump(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
        String t = a.t(str, "    ");
        if (!this.mActive.isEmpty()) {
            printWriter.print(str);
            printWriter.print("Active Fragments:");
            for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
                printWriter.print(str);
                if (fragmentStateManager != null) {
                    Fragment fragment = fragmentStateManager.getFragment();
                    printWriter.println(fragment);
                    fragment.dump(t, fileDescriptor, printWriter, strArr);
                } else {
                    printWriter.println("null");
                }
            }
        }
        int size = this.mAdded.size();
        if (size > 0) {
            printWriter.print(str);
            printWriter.println("Added Fragments:");
            for (int i = 0; i < size; i++) {
                Fragment fragment2 = (Fragment) this.mAdded.get(i);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.println(fragment2.toString());
            }
        }
    }

    @Nullable
    public Fragment findActiveFragment(@NonNull String str) {
        FragmentStateManager fragmentStateManager = (FragmentStateManager) this.mActive.get(str);
        return fragmentStateManager != null ? fragmentStateManager.getFragment() : null;
    }

    @Nullable
    public Fragment findFragmentById(@IdRes int i) {
        Fragment fragment;
        for (int size = this.mAdded.size() - 1; size >= 0; size--) {
            fragment = (Fragment) this.mAdded.get(size);
            if (fragment != null && fragment.mFragmentId == i) {
                return fragment;
            }
        }
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                fragment = fragmentStateManager.getFragment();
                if (fragment.mFragmentId == i) {
                    return fragment;
                }
            }
        }
        return null;
    }

    @Nullable
    public Fragment findFragmentByTag(@Nullable String str) {
        Fragment fragment;
        if (str != null) {
            for (int size = this.mAdded.size() - 1; size >= 0; size--) {
                fragment = (Fragment) this.mAdded.get(size);
                if (fragment != null && str.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        if (str != null) {
            for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
                if (fragmentStateManager != null) {
                    fragment = fragmentStateManager.getFragment();
                    if (str.equals(fragment.mTag)) {
                        return fragment;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public Fragment findFragmentByWho(@NonNull String str) {
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                Fragment findFragmentByWho = fragmentStateManager.getFragment().findFragmentByWho(str);
                if (findFragmentByWho != null) {
                    return findFragmentByWho;
                }
            }
        }
        return null;
    }

    public int findFragmentIndexInContainer(@NonNull Fragment fragment) {
        ViewGroup viewGroup = fragment.mContainer;
        if (viewGroup == null) {
            return -1;
        }
        int indexOf = this.mAdded.indexOf(fragment);
        for (int i = indexOf - 1; i >= 0; i--) {
            Fragment fragment2 = (Fragment) this.mAdded.get(i);
            if (fragment2.mContainer == viewGroup) {
                View view = fragment2.mView;
                if (view != null) {
                    return viewGroup.indexOfChild(view) + 1;
                }
            }
        }
        while (true) {
            indexOf++;
            if (indexOf >= this.mAdded.size()) {
                return -1;
            }
            Fragment fragment3 = (Fragment) this.mAdded.get(indexOf);
            if (fragment3.mContainer == viewGroup) {
                View view2 = fragment3.mView;
                if (view2 != null) {
                    return viewGroup.indexOfChild(view2);
                }
            }
        }
    }

    public int getActiveFragmentCount() {
        return this.mActive.size();
    }

    @NonNull
    public List<FragmentStateManager> getActiveFragmentStateManagers() {
        ArrayList arrayList = new ArrayList();
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                arrayList.add(fragmentStateManager);
            }
        }
        return arrayList;
    }

    @NonNull
    public List<Fragment> getActiveFragments() {
        ArrayList arrayList = new ArrayList();
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                arrayList.add(fragmentStateManager.getFragment());
            } else {
                arrayList.add(null);
            }
        }
        return arrayList;
    }

    @Nullable
    public FragmentStateManager getFragmentStateManager(@NonNull String str) {
        return (FragmentStateManager) this.mActive.get(str);
    }

    @NonNull
    public List<Fragment> getFragments() {
        if (this.mAdded.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList arrayList;
        synchronized (this.mAdded) {
            arrayList = new ArrayList(this.mAdded);
        }
        return arrayList;
    }

    public FragmentManagerViewModel getNonConfig() {
        return this.mNonConfig;
    }

    public void makeActive(@NonNull FragmentStateManager fragmentStateManager) {
        Fragment fragment = fragmentStateManager.getFragment();
        if (!containsActiveFragment(fragment.mWho)) {
            this.mActive.put(fragment.mWho, fragmentStateManager);
            if (fragment.mRetainInstanceChangedWhileDetached) {
                if (fragment.mRetainInstance) {
                    this.mNonConfig.addRetainedFragment(fragment);
                } else {
                    this.mNonConfig.removeRetainedFragment(fragment);
                }
                fragment.mRetainInstanceChangedWhileDetached = false;
            }
            if (FragmentManager.isLoggingEnabled(2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Added fragment to active set ");
                stringBuilder.append(fragment);
                Log.v("FragmentManager", stringBuilder.toString());
            }
        }
    }

    public void makeInactive(@NonNull FragmentStateManager fragmentStateManager) {
        Fragment fragment = fragmentStateManager.getFragment();
        if (fragment.mRetainInstance) {
            this.mNonConfig.removeRetainedFragment(fragment);
        }
        if (((FragmentStateManager) this.mActive.put(fragment.mWho, null)) != null && FragmentManager.isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Removed fragment from active set ");
            stringBuilder.append(fragment);
            Log.v("FragmentManager", stringBuilder.toString());
        }
    }

    public void moveToExpectedState() {
        FragmentStateManager fragmentStateManager;
        Iterator it = this.mAdded.iterator();
        while (it.hasNext()) {
            fragmentStateManager = (FragmentStateManager) this.mActive.get(((Fragment) it.next()).mWho);
            if (fragmentStateManager != null) {
                fragmentStateManager.moveToExpectedState();
            }
        }
        for (FragmentStateManager fragmentStateManager2 : this.mActive.values()) {
            if (fragmentStateManager2 != null) {
                fragmentStateManager2.moveToExpectedState();
                Fragment fragment = fragmentStateManager2.getFragment();
                Object obj = (!fragment.mRemoving || fragment.isInBackStack()) ? null : 1;
                if (obj != null) {
                    makeInactive(fragmentStateManager2);
                }
            }
        }
    }

    public void removeFragment(@NonNull Fragment fragment) {
        synchronized (this.mAdded) {
            this.mAdded.remove(fragment);
        }
        fragment.mAdded = false;
    }

    public void resetActiveFragments() {
        this.mActive.clear();
    }

    public void restoreAddedFragments(@Nullable List<String> list) {
        this.mAdded.clear();
        if (list != null) {
            for (String str : list) {
                Fragment findActiveFragment = findActiveFragment(str);
                if (findActiveFragment != null) {
                    if (FragmentManager.isLoggingEnabled(2)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("restoreSaveState: added (");
                        stringBuilder.append(str);
                        stringBuilder.append("): ");
                        stringBuilder.append(findActiveFragment);
                        Log.v("FragmentManager", stringBuilder.toString());
                    }
                    addFragment(findActiveFragment);
                } else {
                    throw new IllegalStateException(a.u("No instantiated fragment for (", str, ")"));
                }
            }
        }
    }

    @NonNull
    public ArrayList<FragmentState> saveActiveFragments() {
        ArrayList arrayList = new ArrayList(this.mActive.size());
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                Fragment fragment = fragmentStateManager.getFragment();
                FragmentState saveState = fragmentStateManager.saveState();
                arrayList.add(saveState);
                if (FragmentManager.isLoggingEnabled(2)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Saved state of ");
                    stringBuilder.append(fragment);
                    stringBuilder.append(": ");
                    stringBuilder.append(saveState.mSavedFragmentState);
                    Log.v("FragmentManager", stringBuilder.toString());
                }
            }
        }
        return arrayList;
    }

    @Nullable
    public ArrayList<String> saveAddedFragments() {
        synchronized (this.mAdded) {
            if (this.mAdded.isEmpty()) {
                return null;
            }
            ArrayList arrayList = new ArrayList(this.mAdded.size());
            Iterator it = this.mAdded.iterator();
            while (it.hasNext()) {
                Fragment fragment = (Fragment) it.next();
                arrayList.add(fragment.mWho);
                if (FragmentManager.isLoggingEnabled(2)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("saveAllState: adding fragment (");
                    stringBuilder.append(fragment.mWho);
                    stringBuilder.append("): ");
                    stringBuilder.append(fragment);
                    Log.v("FragmentManager", stringBuilder.toString());
                }
            }
            return arrayList;
        }
    }

    public void setNonConfig(@NonNull FragmentManagerViewModel fragmentManagerViewModel) {
        this.mNonConfig = fragmentManagerViewModel;
    }
}
