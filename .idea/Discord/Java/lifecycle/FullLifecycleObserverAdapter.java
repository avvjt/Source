package androidx.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.Event;

public class FullLifecycleObserverAdapter implements LifecycleEventObserver {
    private final FullLifecycleObserver mFullLifecycleObserver;
    private final LifecycleEventObserver mLifecycleEventObserver;

    /* renamed from: androidx.lifecycle.FullLifecycleObserverAdapter$1 */
    public static /* synthetic */ class AnonymousClass1 {
        public static final /* synthetic */ int[] $SwitchMap$androidx$lifecycle$Lifecycle$Event;

        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0031 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static {
            Event event;
            int[] iArr;
            Event event2;
            Event.values();
            int[] iArr2 = new int[7];
            $SwitchMap$androidx$lifecycle$Lifecycle$Event = iArr2;
            try {
                event = Event.ON_CREATE;
                iArr2[0] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                event2 = Event.ON_START;
                iArr[1] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                event2 = Event.ON_RESUME;
                iArr[2] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                event2 = Event.ON_PAUSE;
                iArr[3] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                event2 = Event.ON_STOP;
                iArr[4] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            iArr = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
            event2 = Event.ON_DESTROY;
            iArr[5] = 6;
            try {
                int[] iArr3 = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                event = Event.ON_ANY;
                iArr3[6] = 7;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    public FullLifecycleObserverAdapter(FullLifecycleObserver fullLifecycleObserver, LifecycleEventObserver lifecycleEventObserver) {
        this.mFullLifecycleObserver = fullLifecycleObserver;
        this.mLifecycleEventObserver = lifecycleEventObserver;
    }

    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Event event) {
        switch (event.ordinal()) {
            case 0:
                this.mFullLifecycleObserver.onCreate(lifecycleOwner);
                break;
            case 1:
                this.mFullLifecycleObserver.onStart(lifecycleOwner);
                break;
            case 2:
                this.mFullLifecycleObserver.onResume(lifecycleOwner);
                break;
            case 3:
                this.mFullLifecycleObserver.onPause(lifecycleOwner);
                break;
            case 4:
                this.mFullLifecycleObserver.onStop(lifecycleOwner);
                break;
            case 5:
                this.mFullLifecycleObserver.onDestroy(lifecycleOwner);
                break;
            case 6:
                throw new IllegalArgumentException("ON_ANY must not been send by anybody");
        }
        LifecycleEventObserver lifecycleEventObserver = this.mLifecycleEventObserver;
        if (lifecycleEventObserver != null) {
            lifecycleEventObserver.onStateChanged(lifecycleOwner, event);
        }
    }
}
