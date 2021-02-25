package androidx.appcompat.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import androidx.annotation.CallSuper;
import androidx.annotation.ContentView;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.ActionBarDrawerToggle.Delegate;
import androidx.appcompat.app.ActionBarDrawerToggle.DelegateProvider;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.VectorEnabledTintResources;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.app.TaskStackBuilder.SupportParentable;
import androidx.fragment.app.FragmentActivity;

public class AppCompatActivity extends FragmentActivity implements AppCompatCallback, SupportParentable, DelegateProvider {
    private AppCompatDelegate mDelegate;
    private Resources mResources;

    private boolean performMenuItemShortcut(KeyEvent keyEvent) {
        if (!(VERSION.SDK_INT >= 26 || keyEvent.isCtrlPressed() || KeyEvent.metaStateHasNoModifiers(keyEvent.getMetaState()) || keyEvent.getRepeatCount() != 0 || KeyEvent.isModifierKey(keyEvent.getKeyCode()))) {
            Window window = getWindow();
            if (!(window == null || window.getDecorView() == null || !window.getDecorView().dispatchKeyShortcutEvent(keyEvent))) {
                return true;
            }
        }
        return false;
    }

    public void addContentView(View view, LayoutParams layoutParams) {
        getDelegate().addContentView(view, layoutParams);
    }

    public void attachBaseContext(Context context) {
        super.attachBaseContext(getDelegate().attachBaseContext2(context));
    }

    public void closeOptionsMenu() {
        ActionBar supportActionBar = getSupportActionBar();
        if (!getWindow().hasFeature(0)) {
            return;
        }
        if (supportActionBar == null || !supportActionBar.closeOptionsMenu()) {
            super.closeOptionsMenu();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        ActionBar supportActionBar = getSupportActionBar();
        if (keyCode == 82 && supportActionBar != null && supportActionBar.onMenuKeyEvent(keyEvent)) {
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public <T extends View> T findViewById(@IdRes int i) {
        return getDelegate().findViewById(i);
    }

    @NonNull
    public AppCompatDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = AppCompatDelegate.create((Activity) this, (AppCompatCallback) this);
        }
        return this.mDelegate;
    }

    @Nullable
    public Delegate getDrawerToggleDelegate() {
        return getDelegate().getDrawerToggleDelegate();
    }

    @NonNull
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    public Resources getResources() {
        if (this.mResources == null && VectorEnabledTintResources.shouldBeUsed()) {
            this.mResources = new VectorEnabledTintResources(this, super.getResources());
        }
        Resources resources = this.mResources;
        return resources == null ? super.getResources() : resources;
    }

    @Nullable
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    @Nullable
    public Intent getSupportParentActivityIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mResources != null) {
            this.mResources.updateConfiguration(configuration, super.getResources().getDisplayMetrics());
        }
        getDelegate().onConfigurationChanged(configuration);
    }

    public void onContentChanged() {
        onSupportContentChanged();
    }

    public void onCreate(@Nullable Bundle bundle) {
        AppCompatDelegate delegate = getDelegate();
        delegate.installViewFactory();
        delegate.onCreate(bundle);
        super.onCreate(bundle);
    }

    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder taskStackBuilder) {
        taskStackBuilder.addParentStack((Activity) this);
    }

    public void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (performMenuItemShortcut(keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    public final boolean onMenuItemSelected(int i, @NonNull MenuItem menuItem) {
        if (super.onMenuItemSelected(i, menuItem)) {
            return true;
        }
        ActionBar supportActionBar = getSupportActionBar();
        return (menuItem.getItemId() != 16908332 || supportActionBar == null || (supportActionBar.getDisplayOptions() & 4) == 0) ? false : onSupportNavigateUp();
    }

    public boolean onMenuOpened(int i, Menu menu) {
        return super.onMenuOpened(i, menu);
    }

    public void onNightModeChanged(int i) {
    }

    public void onPanelClosed(int i, @NonNull Menu menu) {
        super.onPanelClosed(i, menu);
    }

    public void onPostCreate(@Nullable Bundle bundle) {
        super.onPostCreate(bundle);
        getDelegate().onPostCreate(bundle);
    }

    public void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    public void onPrepareSupportNavigateUpTaskStack(@NonNull TaskStackBuilder taskStackBuilder) {
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        getDelegate().onSaveInstanceState(bundle);
    }

    public void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    public void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @CallSuper
    public void onSupportActionModeFinished(@NonNull ActionMode actionMode) {
    }

    @CallSuper
    public void onSupportActionModeStarted(@NonNull ActionMode actionMode) {
    }

    @Deprecated
    public void onSupportContentChanged() {
    }

    public boolean onSupportNavigateUp() {
        Intent supportParentActivityIntent = getSupportParentActivityIntent();
        if (supportParentActivityIntent == null) {
            return false;
        }
        if (supportShouldUpRecreateTask(supportParentActivityIntent)) {
            TaskStackBuilder create = TaskStackBuilder.create(this);
            onCreateSupportNavigateUpTaskStack(create);
            onPrepareSupportNavigateUpTaskStack(create);
            create.startActivities();
            try {
                ActivityCompat.finishAffinity(this);
            } catch (IllegalStateException unused) {
                finish();
            }
        } else {
            supportNavigateUpTo(supportParentActivityIntent);
        }
        return true;
    }

    public void onTitleChanged(CharSequence charSequence, int i) {
        super.onTitleChanged(charSequence, i);
        getDelegate().setTitle(charSequence);
    }

    @Nullable
    public ActionMode onWindowStartingSupportActionMode(@NonNull Callback callback) {
        return null;
    }

    public void openOptionsMenu() {
        ActionBar supportActionBar = getSupportActionBar();
        if (!getWindow().hasFeature(0)) {
            return;
        }
        if (supportActionBar == null || !supportActionBar.openOptionsMenu()) {
            super.openOptionsMenu();
        }
    }

    public void setContentView(@LayoutRes int i) {
        getDelegate().setContentView(i);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Deprecated
    public void setSupportProgress(int i) {
    }

    @Deprecated
    public void setSupportProgressBarIndeterminate(boolean z) {
    }

    @Deprecated
    public void setSupportProgressBarIndeterminateVisibility(boolean z) {
    }

    @Deprecated
    public void setSupportProgressBarVisibility(boolean z) {
    }

    public void setTheme(@StyleRes int i) {
        super.setTheme(i);
        getDelegate().setTheme(i);
    }

    @Nullable
    public ActionMode startSupportActionMode(@NonNull Callback callback) {
        return getDelegate().startSupportActionMode(callback);
    }

    public void supportInvalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    public void supportNavigateUpTo(@NonNull Intent intent) {
        NavUtils.navigateUpTo(this, intent);
    }

    public boolean supportRequestWindowFeature(int i) {
        return getDelegate().requestWindowFeature(i);
    }

    public boolean supportShouldUpRecreateTask(@NonNull Intent intent) {
        return NavUtils.shouldUpRecreateTask(this, intent);
    }

    @ContentView
    public AppCompatActivity(@LayoutRes int i) {
        super(i);
    }

    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    public void setContentView(View view, LayoutParams layoutParams) {
        getDelegate().setContentView(view, layoutParams);
    }
}
