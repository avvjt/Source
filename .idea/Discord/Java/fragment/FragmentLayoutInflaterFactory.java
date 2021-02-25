package androidx.fragment.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.R;
import f.d.b.a.a;

public class FragmentLayoutInflaterFactory implements Factory2 {
    private static final String TAG = "FragmentManager";
    private final FragmentManager mFragmentManager;

    public FragmentLayoutInflaterFactory(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    @Nullable
    public View onCreateView(@NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        return onCreateView(null, str, context, attributeSet);
    }

    @Nullable
    public View onCreateView(@Nullable View view, @NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        if (FragmentContainerView.class.getName().equals(str)) {
            return new FragmentContainerView(context, attributeSet, this.mFragmentManager);
        }
        Fragment fragment = null;
        if (!"fragment".equals(str)) {
            return null;
        }
        str = attributeSet.getAttributeValue(null, "class");
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Fragment);
        if (str == null) {
            str = obtainStyledAttributes.getString(R.styleable.Fragment_android_name);
        }
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.Fragment_android_id, -1);
        String string = obtainStyledAttributes.getString(R.styleable.Fragment_android_tag);
        obtainStyledAttributes.recycle();
        if (str == null || !FragmentFactory.isFragmentClass(context.getClassLoader(), str)) {
            return null;
        }
        int id = view != null ? view.getId() : 0;
        StringBuilder stringBuilder;
        if (id == -1 && resourceId == -1 && string == null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(attributeSet.getPositionDescription());
            stringBuilder.append(": Must specify unique android:id, android:tag, or have a parent with an id for ");
            stringBuilder.append(str);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        FragmentStateManager createOrGetFragmentStateManager;
        if (resourceId != -1) {
            fragment = this.mFragmentManager.findFragmentById(resourceId);
        }
        if (fragment == null && string != null) {
            fragment = this.mFragmentManager.findFragmentByTag(string);
        }
        if (fragment == null && id != -1) {
            fragment = this.mFragmentManager.findFragmentById(id);
        }
        String str2 = "Fragment ";
        String str3 = "FragmentManager";
        FragmentManager fragmentManager;
        StringBuilder stringBuilder2;
        if (fragment == null) {
            fragment = this.mFragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), str);
            fragment.mFromLayout = true;
            fragment.mFragmentId = resourceId != 0 ? resourceId : id;
            fragment.mContainerId = id;
            fragment.mTag = string;
            fragment.mInLayout = true;
            fragmentManager = this.mFragmentManager;
            fragment.mFragmentManager = fragmentManager;
            fragment.mHost = fragmentManager.getHost();
            fragment.onInflate(this.mFragmentManager.getHost().getContext(), attributeSet, fragment.mSavedFragmentState);
            createOrGetFragmentStateManager = this.mFragmentManager.createOrGetFragmentStateManager(fragment);
            this.mFragmentManager.addFragment(fragment);
            if (FragmentManager.isLoggingEnabled(2)) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str2);
                stringBuilder2.append(fragment);
                stringBuilder2.append(" has been inflated via the <fragment> tag: id=0x");
                stringBuilder2.append(Integer.toHexString(resourceId));
                Log.v(str3, stringBuilder2.toString());
            }
        } else if (fragment.mInLayout) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(attributeSet.getPositionDescription());
            stringBuilder.append(": Duplicate id 0x");
            stringBuilder.append(Integer.toHexString(resourceId));
            stringBuilder.append(", tag ");
            stringBuilder.append(string);
            stringBuilder.append(", or parent id 0x");
            stringBuilder.append(Integer.toHexString(id));
            stringBuilder.append(" with another fragment for ");
            stringBuilder.append(str);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else {
            fragment.mInLayout = true;
            fragmentManager = this.mFragmentManager;
            fragment.mFragmentManager = fragmentManager;
            fragment.mHost = fragmentManager.getHost();
            fragment.onInflate(this.mFragmentManager.getHost().getContext(), attributeSet, fragment.mSavedFragmentState);
            createOrGetFragmentStateManager = this.mFragmentManager.createOrGetFragmentStateManager(fragment);
            if (FragmentManager.isLoggingEnabled(2)) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Retained Fragment ");
                stringBuilder2.append(fragment);
                stringBuilder2.append(" has been re-attached via the <fragment> tag: id=0x");
                stringBuilder2.append(Integer.toHexString(resourceId));
                Log.v(str3, stringBuilder2.toString());
            }
        }
        fragment.mContainer = (ViewGroup) view;
        createOrGetFragmentStateManager.moveToExpectedState();
        createOrGetFragmentStateManager.ensureInflatedView();
        view = fragment.mView;
        if (view != null) {
            if (resourceId != 0) {
                view.setId(resourceId);
            }
            if (fragment.mView.getTag() == null) {
                fragment.mView.setTag(string);
            }
            return fragment.mView;
        }
        throw new IllegalStateException(a.u(str2, str, " did not create a view."));
    }
}
