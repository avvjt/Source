package androidx.lifecycle;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.lifecycle.Lifecycle.Event;

@RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
public class ReportFragment extends Fragment {
    private static final String REPORT_FRAGMENT_TAG = "androidx.lifecycle.LifecycleDispatcher.report_fragment_tag";
    private ActivityInitializationListener mProcessListener;

    public interface ActivityInitializationListener {
        void onCreate();

        void onResume();

        void onStart();
    }

    @RequiresApi(29)
    public static class LifecycleCallbacks implements ActivityLifecycleCallbacks {
        public static void registerIn(Activity activity) {
            activity.registerActivityLifecycleCallbacks(new LifecycleCallbacks());
        }

        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        }

        public void onActivityDestroyed(@NonNull Activity activity) {
        }

        public void onActivityPaused(@NonNull Activity activity) {
        }

        public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            ReportFragment.dispatch(activity, Event.ON_CREATE);
        }

        public void onActivityPostResumed(@NonNull Activity activity) {
            ReportFragment.dispatch(activity, Event.ON_RESUME);
        }

        public void onActivityPostStarted(@NonNull Activity activity) {
            ReportFragment.dispatch(activity, Event.ON_START);
        }

        public void onActivityPreDestroyed(@NonNull Activity activity) {
            ReportFragment.dispatch(activity, Event.ON_DESTROY);
        }

        public void onActivityPrePaused(@NonNull Activity activity) {
            ReportFragment.dispatch(activity, Event.ON_PAUSE);
        }

        public void onActivityPreStopped(@NonNull Activity activity) {
            ReportFragment.dispatch(activity, Event.ON_STOP);
        }

        public void onActivityResumed(@NonNull Activity activity) {
        }

        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        }

        public void onActivityStarted(@NonNull Activity activity) {
        }

        public void onActivityStopped(@NonNull Activity activity) {
        }
    }

    public static void dispatch(@NonNull Activity activity, @NonNull Event event) {
        if (activity instanceof LifecycleRegistryOwner) {
            ((LifecycleRegistryOwner) activity).getLifecycle().handleLifecycleEvent(event);
            return;
        }
        if (activity instanceof LifecycleOwner) {
            Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
            if (lifecycle instanceof LifecycleRegistry) {
                ((LifecycleRegistry) lifecycle).handleLifecycleEvent(event);
            }
        }
    }

    private void dispatchCreate(ActivityInitializationListener activityInitializationListener) {
        if (activityInitializationListener != null) {
            activityInitializationListener.onCreate();
        }
    }

    private void dispatchResume(ActivityInitializationListener activityInitializationListener) {
        if (activityInitializationListener != null) {
            activityInitializationListener.onResume();
        }
    }

    private void dispatchStart(ActivityInitializationListener activityInitializationListener) {
        if (activityInitializationListener != null) {
            activityInitializationListener.onStart();
        }
    }

    public static ReportFragment get(Activity activity) {
        return (ReportFragment) activity.getFragmentManager().findFragmentByTag(REPORT_FRAGMENT_TAG);
    }

    public static void injectIfNeededIn(Activity activity) {
        if (VERSION.SDK_INT >= 29) {
            LifecycleCallbacks.registerIn(activity);
        }
        FragmentManager fragmentManager = activity.getFragmentManager();
        String str = REPORT_FRAGMENT_TAG;
        if (fragmentManager.findFragmentByTag(str) == null) {
            fragmentManager.beginTransaction().add(new ReportFragment(), str).commit();
            fragmentManager.executePendingTransactions();
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        dispatchCreate(this.mProcessListener);
        dispatch(Event.ON_CREATE);
    }

    public void onDestroy() {
        super.onDestroy();
        dispatch(Event.ON_DESTROY);
        this.mProcessListener = null;
    }

    public void onPause() {
        super.onPause();
        dispatch(Event.ON_PAUSE);
    }

    public void onResume() {
        super.onResume();
        dispatchResume(this.mProcessListener);
        dispatch(Event.ON_RESUME);
    }

    public void onStart() {
        super.onStart();
        dispatchStart(this.mProcessListener);
        dispatch(Event.ON_START);
    }

    public void onStop() {
        super.onStop();
        dispatch(Event.ON_STOP);
    }

    public void setProcessListener(ActivityInitializationListener activityInitializationListener) {
        this.mProcessListener = activityInitializationListener;
    }

    private void dispatch(@NonNull Event event) {
        if (VERSION.SDK_INT < 29) {
            dispatch(getActivity(), event);
        }
    }
}
