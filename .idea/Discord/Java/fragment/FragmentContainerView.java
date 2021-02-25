package androidx.fragment.app;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.R;
import f.d.b.a.a;
import java.util.ArrayList;

public final class FragmentContainerView extends FrameLayout {
    private ArrayList<View> mDisappearingFragmentChildren;
    private boolean mDrawDisappearingViewsFirst;
    private ArrayList<View> mTransitioningFragmentViews;

    public FragmentContainerView(@NonNull Context context) {
        super(context);
        this.mDrawDisappearingViewsFirst = true;
    }

    private void addDisappearingFragmentView(@NonNull View view) {
        if (view.getAnimation() != null) {
            ArrayList arrayList = this.mTransitioningFragmentViews;
            if (arrayList != null && arrayList.contains(view)) {
                if (this.mDisappearingFragmentChildren == null) {
                    this.mDisappearingFragmentChildren = new ArrayList();
                }
                this.mDisappearingFragmentChildren.add(view);
            }
        }
    }

    public void addView(@NonNull View view, int i, @Nullable LayoutParams layoutParams) {
        if (FragmentManager.getViewFragment(view) != null) {
            super.addView(view, i, layoutParams);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Views added to a FragmentContainerView must be associated with a Fragment. View ");
        stringBuilder.append(view);
        stringBuilder.append(" is not associated with a Fragment.");
        throw new IllegalStateException(stringBuilder.toString());
    }

    public boolean addViewInLayout(@NonNull View view, int i, @Nullable LayoutParams layoutParams, boolean z) {
        if (FragmentManager.getViewFragment(view) != null) {
            return super.addViewInLayout(view, i, layoutParams, z);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Views added to a FragmentContainerView must be associated with a Fragment. View ");
        stringBuilder.append(view);
        stringBuilder.append(" is not associated with a Fragment.");
        throw new IllegalStateException(stringBuilder.toString());
    }

    public void dispatchDraw(@NonNull Canvas canvas) {
        if (this.mDrawDisappearingViewsFirst && this.mDisappearingFragmentChildren != null) {
            for (int i = 0; i < this.mDisappearingFragmentChildren.size(); i++) {
                super.drawChild(canvas, (View) this.mDisappearingFragmentChildren.get(i), getDrawingTime());
            }
        }
        super.dispatchDraw(canvas);
    }

    public boolean drawChild(@NonNull Canvas canvas, @NonNull View view, long j) {
        if (this.mDrawDisappearingViewsFirst) {
            ArrayList arrayList = this.mDisappearingFragmentChildren;
            if (arrayList != null && arrayList.size() > 0 && this.mDisappearingFragmentChildren.contains(view)) {
                return false;
            }
        }
        return super.drawChild(canvas, view, j);
    }

    public void endViewTransition(@NonNull View view) {
        ArrayList arrayList = this.mTransitioningFragmentViews;
        if (arrayList != null) {
            arrayList.remove(view);
            arrayList = this.mDisappearingFragmentChildren;
            if (arrayList != null && arrayList.remove(view)) {
                this.mDrawDisappearingViewsFirst = true;
            }
        }
        super.endViewTransition(view);
    }

    @RequiresApi(20)
    @NonNull
    public WindowInsets onApplyWindowInsets(@NonNull WindowInsets windowInsets) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).dispatchApplyWindowInsets(new WindowInsets(windowInsets));
        }
        return windowInsets;
    }

    public void removeAllViewsInLayout() {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            addDisappearingFragmentView(getChildAt(childCount));
        }
        super.removeAllViewsInLayout();
    }

    public void removeDetachedView(@NonNull View view, boolean z) {
        if (z) {
            addDisappearingFragmentView(view);
        }
        super.removeDetachedView(view, z);
    }

    public void removeView(@NonNull View view) {
        addDisappearingFragmentView(view);
        super.removeView(view);
    }

    public void removeViewAt(int i) {
        addDisappearingFragmentView(getChildAt(i));
        super.removeViewAt(i);
    }

    public void removeViewInLayout(@NonNull View view) {
        addDisappearingFragmentView(view);
        super.removeViewInLayout(view);
    }

    public void removeViews(int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            addDisappearingFragmentView(getChildAt(i3));
        }
        super.removeViews(i, i2);
    }

    public void removeViewsInLayout(int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            addDisappearingFragmentView(getChildAt(i3));
        }
        super.removeViewsInLayout(i, i2);
    }

    public void setDrawDisappearingViewsLast(boolean z) {
        this.mDrawDisappearingViewsFirst = z;
    }

    public void setLayoutTransition(@Nullable LayoutTransition layoutTransition) {
        throw new UnsupportedOperationException("FragmentContainerView does not support Layout Transitions or animateLayoutChanges=\"true\".");
    }

    public void startViewTransition(@NonNull View view) {
        if (view.getParent() == this) {
            if (this.mTransitioningFragmentViews == null) {
                this.mTransitioningFragmentViews = new ArrayList();
            }
            this.mTransitioningFragmentViews.add(view);
        }
        super.startViewTransition(view);
    }

    public FragmentContainerView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FragmentContainerView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDrawDisappearingViewsFirst = true;
        if (attributeSet != null) {
            String str;
            String classAttribute = attributeSet.getClassAttribute();
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FragmentContainerView);
            if (classAttribute == null) {
                classAttribute = obtainStyledAttributes.getString(R.styleable.FragmentContainerView_android_name);
                str = "android:name";
            } else {
                str = "class";
            }
            obtainStyledAttributes.recycle();
            if (classAttribute != null && !isInEditMode()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("FragmentContainerView must be within a FragmentActivity to use ");
                stringBuilder.append(str);
                stringBuilder.append("=\"");
                stringBuilder.append(classAttribute);
                stringBuilder.append("\"");
                throw new UnsupportedOperationException(stringBuilder.toString());
            }
        }
    }

    public FragmentContainerView(@NonNull Context context, @NonNull AttributeSet attributeSet, @NonNull FragmentManager fragmentManager) {
        super(context, attributeSet);
        this.mDrawDisappearingViewsFirst = true;
        String classAttribute = attributeSet.getClassAttribute();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FragmentContainerView);
        if (classAttribute == null) {
            classAttribute = obtainStyledAttributes.getString(R.styleable.FragmentContainerView_android_name);
        }
        String string = obtainStyledAttributes.getString(R.styleable.FragmentContainerView_android_tag);
        obtainStyledAttributes.recycle();
        int id = getId();
        Fragment findFragmentById = fragmentManager.findFragmentById(id);
        if (classAttribute != null && findFragmentById == null) {
            if (id <= 0) {
                throw new IllegalStateException(a.u("FragmentContainerView must have an android:id to add Fragment ", classAttribute, string != null ? a.t(" with tag ", string) : ""));
            }
            Fragment instantiate = fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), classAttribute);
            instantiate.onInflate(context, attributeSet, null);
            fragmentManager.beginTransaction().setReorderingAllowed(true).add((ViewGroup) this, instantiate, string).commitNowAllowingStateLoss();
        }
    }
}
