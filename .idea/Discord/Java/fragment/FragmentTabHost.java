package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import f.d.b.a.a;
import java.util.ArrayList;

@Deprecated
public class FragmentTabHost extends TabHost implements OnTabChangeListener {
    private boolean mAttached;
    private int mContainerId;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private TabInfo mLastTab;
    private OnTabChangeListener mOnTabChangeListener;
    private FrameLayout mRealTabContent;
    private final ArrayList<TabInfo> mTabs = new ArrayList();

    public static class DummyTabFactory implements TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            this.mContext = context;
        }

        public View createTabContent(String str) {
            View view = new View(this.mContext);
            view.setMinimumWidth(0);
            view.setMinimumHeight(0);
            return view;
        }
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public String curTab;

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @NonNull
        public String toString() {
            StringBuilder L = a.L("FragmentTabHost.SavedState{");
            L.append(Integer.toHexString(System.identityHashCode(this)));
            L.append(" curTab=");
            return a.D(L, this.curTab, "}");
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.curTab);
        }

        public SavedState(Parcel parcel) {
            super(parcel);
            this.curTab = parcel.readString();
        }
    }

    public static final class TabInfo {
        @Nullable
        public final Bundle args;
        @NonNull
        public final Class<?> clss;
        public Fragment fragment;
        @NonNull
        public final String tag;

        public TabInfo(@NonNull String str, @NonNull Class<?> cls, @Nullable Bundle bundle) {
            this.tag = str;
            this.clss = cls;
            this.args = bundle;
        }
    }

    @Deprecated
    public FragmentTabHost(@NonNull Context context) {
        super(context, null);
        initFragmentTabHost(context, null);
    }

    @Nullable
    private FragmentTransaction doTabChanged(@Nullable String str, @Nullable FragmentTransaction fragmentTransaction) {
        TabInfo tabInfoForTag = getTabInfoForTag(str);
        if (this.mLastTab != tabInfoForTag) {
            Fragment fragment;
            if (fragmentTransaction == null) {
                fragmentTransaction = this.mFragmentManager.beginTransaction();
            }
            TabInfo tabInfo = this.mLastTab;
            if (tabInfo != null) {
                fragment = tabInfo.fragment;
                if (fragment != null) {
                    fragmentTransaction.detach(fragment);
                }
            }
            if (tabInfoForTag != null) {
                fragment = tabInfoForTag.fragment;
                if (fragment == null) {
                    fragment = this.mFragmentManager.getFragmentFactory().instantiate(this.mContext.getClassLoader(), tabInfoForTag.clss.getName());
                    tabInfoForTag.fragment = fragment;
                    fragment.setArguments(tabInfoForTag.args);
                    fragmentTransaction.add(this.mContainerId, tabInfoForTag.fragment, tabInfoForTag.tag);
                } else {
                    fragmentTransaction.attach(fragment);
                }
            }
            this.mLastTab = tabInfoForTag;
        }
        return fragmentTransaction;
    }

    private void ensureContent() {
        if (this.mRealTabContent == null) {
            FrameLayout frameLayout = (FrameLayout) findViewById(this.mContainerId);
            this.mRealTabContent = frameLayout;
            if (frameLayout == null) {
                StringBuilder L = a.L("No tab content FrameLayout found for id ");
                L.append(this.mContainerId);
                throw new IllegalStateException(L.toString());
            }
        }
    }

    private void ensureHierarchy(Context context) {
        if (findViewById(16908307) == null) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, new LayoutParams(-1, -1));
            TabWidget tabWidget = new TabWidget(context);
            tabWidget.setId(16908307);
            tabWidget.setOrientation(0);
            linearLayout.addView(tabWidget, new LinearLayout.LayoutParams(-1, -2, 0.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setId(16908305);
            linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(0, 0, 0.0f));
            frameLayout = new FrameLayout(context);
            this.mRealTabContent = frameLayout;
            frameLayout.setId(this.mContainerId);
            linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        }
    }

    @Nullable
    private TabInfo getTabInfoForTag(String str) {
        int size = this.mTabs.size();
        for (int i = 0; i < size; i++) {
            TabInfo tabInfo = (TabInfo) this.mTabs.get(i);
            if (tabInfo.tag.equals(str)) {
                return tabInfo;
            }
        }
        return null;
    }

    private void initFragmentTabHost(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{16842995}, 0, 0);
        this.mContainerId = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
        super.setOnTabChangedListener(this);
    }

    @Deprecated
    public void addTab(@NonNull TabSpec tabSpec, @NonNull Class<?> cls, @Nullable Bundle bundle) {
        tabSpec.setContent(new DummyTabFactory(this.mContext));
        String tag = tabSpec.getTag();
        TabInfo tabInfo = new TabInfo(tag, cls, bundle);
        if (this.mAttached) {
            Fragment findFragmentByTag = this.mFragmentManager.findFragmentByTag(tag);
            tabInfo.fragment = findFragmentByTag;
            if (!(findFragmentByTag == null || findFragmentByTag.isDetached())) {
                FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
                beginTransaction.detach(tabInfo.fragment);
                beginTransaction.commit();
            }
        }
        this.mTabs.add(tabInfo);
        addTab(tabSpec);
    }

    @Deprecated
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        String currentTabTag = getCurrentTabTag();
        int size = this.mTabs.size();
        FragmentTransaction fragmentTransaction = null;
        for (int i = 0; i < size; i++) {
            TabInfo tabInfo = (TabInfo) this.mTabs.get(i);
            Fragment findFragmentByTag = this.mFragmentManager.findFragmentByTag(tabInfo.tag);
            tabInfo.fragment = findFragmentByTag;
            if (!(findFragmentByTag == null || findFragmentByTag.isDetached())) {
                if (tabInfo.tag.equals(currentTabTag)) {
                    this.mLastTab = tabInfo;
                } else {
                    if (fragmentTransaction == null) {
                        fragmentTransaction = this.mFragmentManager.beginTransaction();
                    }
                    fragmentTransaction.detach(tabInfo.fragment);
                }
            }
        }
        this.mAttached = true;
        FragmentTransaction doTabChanged = doTabChanged(currentTabTag, fragmentTransaction);
        if (doTabChanged != null) {
            doTabChanged.commit();
            this.mFragmentManager.executePendingTransactions();
        }
    }

    @Deprecated
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttached = false;
    }

    @Deprecated
    public void onRestoreInstanceState(@SuppressLint({"UnknownNullness"}) Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            setCurrentTabByTag(savedState.curTab);
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    @NonNull
    @Deprecated
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.curTab = getCurrentTabTag();
        return savedState;
    }

    @Deprecated
    public void onTabChanged(@Nullable String str) {
        if (this.mAttached) {
            FragmentTransaction doTabChanged = doTabChanged(str, null);
            if (doTabChanged != null) {
                doTabChanged.commit();
            }
        }
        OnTabChangeListener onTabChangeListener = this.mOnTabChangeListener;
        if (onTabChangeListener != null) {
            onTabChangeListener.onTabChanged(str);
        }
    }

    @Deprecated
    public void setOnTabChangedListener(@Nullable OnTabChangeListener onTabChangeListener) {
        this.mOnTabChangeListener = onTabChangeListener;
    }

    @Deprecated
    public void setup() {
        throw new IllegalStateException("Must call setup() that takes a Context and FragmentManager");
    }

    @Deprecated
    public void setup(@NonNull Context context, @NonNull FragmentManager fragmentManager) {
        ensureHierarchy(context);
        super.setup();
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        ensureContent();
    }

    @Deprecated
    public FragmentTabHost(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        initFragmentTabHost(context, attributeSet);
    }

    @Deprecated
    public void setup(@NonNull Context context, @NonNull FragmentManager fragmentManager, int i) {
        ensureHierarchy(context);
        super.setup();
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        this.mContainerId = i;
        ensureContent();
        this.mRealTabContent.setId(i);
        if (getId() == -1) {
            setId(16908306);
        }
    }
}
