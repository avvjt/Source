package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.annotation.StyleRes;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class DialogFragment extends Fragment implements OnCancelListener, OnDismissListener {
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";
    private static final String SAVED_CANCELABLE = "android:cancelable";
    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
    private static final String SAVED_STYLE = "android:style";
    private static final String SAVED_THEME = "android:theme";
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_NO_FRAME = 2;
    public static final int STYLE_NO_INPUT = 3;
    public static final int STYLE_NO_TITLE = 1;
    private int mBackStackId = -1;
    private boolean mCancelable = true;
    private boolean mCreatingDialog;
    @Nullable
    private Dialog mDialog;
    private boolean mDialogCreated = false;
    private Runnable mDismissRunnable = new Runnable() {
        @SuppressLint({"SyntheticAccessor"})
        public void run() {
            DialogFragment.this.mOnDismissListener.onDismiss(DialogFragment.this.mDialog);
        }
    };
    private boolean mDismissed;
    private Handler mHandler;
    private Observer<LifecycleOwner> mObserver = new Observer<LifecycleOwner>() {
        @SuppressLint({"SyntheticAccessor"})
        public void onChanged(LifecycleOwner lifecycleOwner) {
            if (lifecycleOwner != null && DialogFragment.this.mShowsDialog) {
                View requireView = DialogFragment.this.requireView();
                if (requireView.getParent() != null) {
                    throw new IllegalStateException("DialogFragment can not be attached to a container view");
                } else if (DialogFragment.this.mDialog != null) {
                    if (FragmentManager.isLoggingEnabled(3)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("DialogFragment ");
                        stringBuilder.append(this);
                        stringBuilder.append(" setting the content view on ");
                        stringBuilder.append(DialogFragment.this.mDialog);
                        Log.d(FragmentManager.TAG, stringBuilder.toString());
                    }
                    DialogFragment.this.mDialog.setContentView(requireView);
                }
            }
        }
    };
    private OnCancelListener mOnCancelListener = new OnCancelListener() {
        @SuppressLint({"SyntheticAccessor"})
        public void onCancel(@Nullable DialogInterface dialogInterface) {
            if (DialogFragment.this.mDialog != null) {
                DialogFragment dialogFragment = DialogFragment.this;
                dialogFragment.onCancel(dialogFragment.mDialog);
            }
        }
    };
    private OnDismissListener mOnDismissListener = new OnDismissListener() {
        @SuppressLint({"SyntheticAccessor"})
        public void onDismiss(@Nullable DialogInterface dialogInterface) {
            if (DialogFragment.this.mDialog != null) {
                DialogFragment dialogFragment = DialogFragment.this;
                dialogFragment.onDismiss(dialogFragment.mDialog);
            }
        }
    };
    private boolean mShownByMe;
    private boolean mShowsDialog = true;
    private int mStyle = 0;
    private int mTheme = 0;
    private boolean mViewDestroyed;

    private void dismissInternal(boolean z, boolean z2) {
        if (!this.mDismissed) {
            this.mDismissed = true;
            this.mShownByMe = false;
            Dialog dialog = this.mDialog;
            if (dialog != null) {
                dialog.setOnDismissListener(null);
                this.mDialog.dismiss();
                if (!z2) {
                    if (Looper.myLooper() == this.mHandler.getLooper()) {
                        onDismiss(this.mDialog);
                    } else {
                        this.mHandler.post(this.mDismissRunnable);
                    }
                }
            }
            this.mViewDestroyed = true;
            if (this.mBackStackId >= 0) {
                getParentFragmentManager().popBackStack(this.mBackStackId, 1);
                this.mBackStackId = -1;
            } else {
                FragmentTransaction beginTransaction = getParentFragmentManager().beginTransaction();
                beginTransaction.remove(this);
                if (z) {
                    beginTransaction.commitAllowingStateLoss();
                } else {
                    beginTransaction.commit();
                }
            }
        }
    }

    private void prepareDialog(@Nullable Bundle bundle) {
        if (this.mShowsDialog && !this.mDialogCreated) {
            try {
                this.mCreatingDialog = true;
                Dialog onCreateDialog = onCreateDialog(bundle);
                this.mDialog = onCreateDialog;
                if (this.mShowsDialog) {
                    setupDialog(onCreateDialog, this.mStyle);
                    Context context = getContext();
                    if (context instanceof Activity) {
                        this.mDialog.setOwnerActivity((Activity) context);
                    }
                    this.mDialog.setCancelable(this.mCancelable);
                    this.mDialog.setOnCancelListener(this.mOnCancelListener);
                    this.mDialog.setOnDismissListener(this.mOnDismissListener);
                    this.mDialogCreated = true;
                } else {
                    this.mDialog = null;
                }
                this.mCreatingDialog = false;
            } catch (Throwable th) {
                this.mCreatingDialog = false;
            }
        }
    }

    @NonNull
    public FragmentContainer createFragmentContainer() {
        final FragmentContainer createFragmentContainer = super.createFragmentContainer();
        return new FragmentContainer() {
            @Nullable
            public View onFindViewById(int i) {
                View onFindViewById = DialogFragment.this.onFindViewById(i);
                if (onFindViewById != null) {
                    return onFindViewById;
                }
                return createFragmentContainer.onHasView() ? createFragmentContainer.onFindViewById(i) : null;
            }

            public boolean onHasView() {
                return DialogFragment.this.onHasView() || createFragmentContainer.onHasView();
            }
        };
    }

    public void dismiss() {
        dismissInternal(false, false);
    }

    public void dismissAllowingStateLoss() {
        dismissInternal(true, false);
    }

    @Nullable
    public Dialog getDialog() {
        return this.mDialog;
    }

    public boolean getShowsDialog() {
        return this.mShowsDialog;
    }

    @StyleRes
    public int getTheme() {
        return this.mTheme;
    }

    public boolean isCancelable() {
        return this.mCancelable;
    }

    @MainThread
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getViewLifecycleOwnerLiveData().observeForever(this.mObserver);
        if (!this.mShownByMe) {
            this.mDismissed = false;
        }
    }

    public void onCancel(@NonNull DialogInterface dialogInterface) {
    }

    @MainThread
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new Handler();
        this.mShowsDialog = this.mContainerId == 0;
        if (bundle != null) {
            this.mStyle = bundle.getInt(SAVED_STYLE, 0);
            this.mTheme = bundle.getInt(SAVED_THEME, 0);
            this.mCancelable = bundle.getBoolean(SAVED_CANCELABLE, true);
            this.mShowsDialog = bundle.getBoolean(SAVED_SHOWS_DIALOG, this.mShowsDialog);
            this.mBackStackId = bundle.getInt(SAVED_BACK_STACK_ID, -1);
        }
    }

    @MainThread
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle bundle) {
        if (FragmentManager.isLoggingEnabled(3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onCreateDialog called for DialogFragment ");
            stringBuilder.append(this);
            Log.d(FragmentManager.TAG, stringBuilder.toString());
        }
        return new Dialog(requireContext(), getTheme());
    }

    @MainThread
    public void onDestroyView() {
        super.onDestroyView();
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            this.mViewDestroyed = true;
            dialog.setOnDismissListener(null);
            this.mDialog.dismiss();
            if (!this.mDismissed) {
                onDismiss(this.mDialog);
            }
            this.mDialog = null;
            this.mDialogCreated = false;
        }
    }

    @MainThread
    public void onDetach() {
        super.onDetach();
        if (!(this.mShownByMe || this.mDismissed)) {
            this.mDismissed = true;
        }
        getViewLifecycleOwnerLiveData().removeObserver(this.mObserver);
    }

    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        if (!this.mViewDestroyed) {
            if (FragmentManager.isLoggingEnabled(3)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onDismiss called for DialogFragment ");
                stringBuilder.append(this);
                Log.d(FragmentManager.TAG, stringBuilder.toString());
            }
            dismissInternal(true, true);
        }
    }

    @Nullable
    public View onFindViewById(int i) {
        Dialog dialog = this.mDialog;
        return dialog != null ? dialog.findViewById(i) : null;
    }

    @NonNull
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle bundle) {
        LayoutInflater onGetLayoutInflater = super.onGetLayoutInflater(bundle);
        boolean z = this.mShowsDialog;
        String str = FragmentManager.TAG;
        StringBuilder stringBuilder;
        if (!z || this.mCreatingDialog) {
            if (FragmentManager.isLoggingEnabled(2)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("getting layout inflater for DialogFragment ");
                stringBuilder.append(this);
                String stringBuilder2 = stringBuilder.toString();
                StringBuilder stringBuilder3;
                if (this.mShowsDialog) {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("mCreatingDialog = true: ");
                    stringBuilder3.append(stringBuilder2);
                    Log.d(str, stringBuilder3.toString());
                } else {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("mShowsDialog = false: ");
                    stringBuilder3.append(stringBuilder2);
                    Log.d(str, stringBuilder3.toString());
                }
            }
            return onGetLayoutInflater;
        }
        prepareDialog(bundle);
        if (FragmentManager.isLoggingEnabled(2)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("get layout inflater for DialogFragment ");
            stringBuilder.append(this);
            stringBuilder.append(" from dialog context");
            Log.d(str, stringBuilder.toString());
        }
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            onGetLayoutInflater = onGetLayoutInflater.cloneInContext(dialog.getContext());
        }
        return onGetLayoutInflater;
    }

    public boolean onHasView() {
        return this.mDialogCreated;
    }

    @MainThread
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            bundle.putBundle(SAVED_DIALOG_STATE_TAG, dialog.onSaveInstanceState());
        }
        int i = this.mStyle;
        if (i != 0) {
            bundle.putInt(SAVED_STYLE, i);
        }
        i = this.mTheme;
        if (i != 0) {
            bundle.putInt(SAVED_THEME, i);
        }
        boolean z = this.mCancelable;
        if (!z) {
            bundle.putBoolean(SAVED_CANCELABLE, z);
        }
        z = this.mShowsDialog;
        if (!z) {
            bundle.putBoolean(SAVED_SHOWS_DIALOG, z);
        }
        i = this.mBackStackId;
        if (i != -1) {
            bundle.putInt(SAVED_BACK_STACK_ID, i);
        }
    }

    @MainThread
    public void onStart() {
        super.onStart();
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            this.mViewDestroyed = false;
            dialog.show();
        }
    }

    @MainThread
    public void onStop() {
        super.onStop();
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.hide();
        }
    }

    @MainThread
    public void onViewStateRestored(@Nullable Bundle bundle) {
        super.onViewStateRestored(bundle);
        if (this.mDialog != null && bundle != null) {
            bundle = bundle.getBundle(SAVED_DIALOG_STATE_TAG);
            if (bundle != null) {
                this.mDialog.onRestoreInstanceState(bundle);
            }
        }
    }

    public void performCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        super.performCreateView(layoutInflater, viewGroup, bundle);
        if (this.mView == null && this.mDialog != null && bundle != null) {
            Bundle bundle2 = bundle.getBundle(SAVED_DIALOG_STATE_TAG);
            if (bundle2 != null) {
                this.mDialog.onRestoreInstanceState(bundle2);
            }
        }
    }

    @NonNull
    public final Dialog requireDialog() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            return dialog;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DialogFragment ");
        stringBuilder.append(this);
        stringBuilder.append(" does not have a Dialog.");
        throw new IllegalStateException(stringBuilder.toString());
    }

    public void setCancelable(boolean z) {
        this.mCancelable = z;
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.setCancelable(z);
        }
    }

    public void setShowsDialog(boolean z) {
        this.mShowsDialog = z;
    }

    public void setStyle(int i, @StyleRes int i2) {
        if (FragmentManager.isLoggingEnabled(2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Setting style and theme for DialogFragment ");
            stringBuilder.append(this);
            stringBuilder.append(" to ");
            stringBuilder.append(i);
            stringBuilder.append(", ");
            stringBuilder.append(i2);
            Log.d(FragmentManager.TAG, stringBuilder.toString());
        }
        this.mStyle = i;
        if (i == 2 || i == 3) {
            this.mTheme = 16973913;
        }
        if (i2 != 0) {
            this.mTheme = i2;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP_PREFIX})
    public void setupDialog(@NonNull Dialog dialog, int i) {
        if (!(i == 1 || i == 2)) {
            if (i == 3) {
                Window window = dialog.getWindow();
                if (window != null) {
                    window.addFlags(24);
                }
            } else {
                return;
            }
        }
        dialog.requestWindowFeature(1);
    }

    public void show(@NonNull FragmentManager fragmentManager, @Nullable String str) {
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.add((Fragment) this, str);
        beginTransaction.commit();
    }

    public void showNow(@NonNull FragmentManager fragmentManager, @Nullable String str) {
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.add((Fragment) this, str);
        beginTransaction.commitNow();
    }

    public int show(@NonNull FragmentTransaction fragmentTransaction, @Nullable String str) {
        this.mDismissed = false;
        this.mShownByMe = true;
        fragmentTransaction.add((Fragment) this, str);
        this.mViewDestroyed = false;
        int commit = fragmentTransaction.commit();
        this.mBackStackId = commit;
        return commit;
    }

    public DialogFragment(@LayoutRes int i) {
        super(i);
    }
}
