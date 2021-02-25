package androidx.lifecycle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Lifecycle {
    @NonNull
    @RestrictTo({Scope.LIBRARY_GROUP})
    public AtomicReference<Object> mInternalScopeRef = new AtomicReference();

    /* renamed from: androidx.lifecycle.Lifecycle$1 */
    public static /* synthetic */ class AnonymousClass1 {
        public static final /* synthetic */ int[] $SwitchMap$androidx$lifecycle$Lifecycle$Event;
        public static final /* synthetic */ int[] $SwitchMap$androidx$lifecycle$Lifecycle$State;

        /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x004e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0031 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x0048 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0042 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x0054 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(29:0|1|2|3|(2:5|6)|7|9|10|11|12|13|15|16|17|19|20|(2:21|22)|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(28:0|1|2|3|(2:5|6)|7|9|10|11|12|13|(2:15|16)|17|19|20|(2:21|22)|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(28:0|1|2|3|(2:5|6)|7|9|10|11|12|13|(2:15|16)|17|19|20|(2:21|22)|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(26:0|(2:1|2)|3|(2:5|6)|7|9|10|(2:11|12)|13|(2:15|16)|17|19|20|(2:21|22)|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Missing block: B:37:?, code skipped:
            return;
     */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static {
            Event.values();
            int[] iArr = new int[7];
            $SwitchMap$androidx$lifecycle$Lifecycle$Event = iArr;
            try {
                Event event = Event.ON_CREATE;
                iArr[0] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                int[] iArr2 = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                Event event2 = Event.ON_STOP;
                iArr2[4] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            int[] iArr3 = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
            Event event3 = Event.ON_START;
            iArr3[1] = 3;
            try {
                iArr3 = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                event3 = Event.ON_PAUSE;
                iArr3[3] = 4;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                int[] iArr4 = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                Event event4 = Event.ON_RESUME;
                iArr4[2] = 5;
            } catch (NoSuchFieldError unused4) {
            }
            int[] iArr5 = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
            Event event5 = Event.ON_DESTROY;
            iArr5[5] = 6;
            try {
                iArr5 = $SwitchMap$androidx$lifecycle$Lifecycle$Event;
                event5 = Event.ON_ANY;
                iArr5[6] = 7;
            } catch (NoSuchFieldError unused5) {
            }
            State.values();
            int[] iArr6 = new int[5];
            $SwitchMap$androidx$lifecycle$Lifecycle$State = iArr6;
            State state = State.CREATED;
            iArr6[2] = 1;
            iArr6 = $SwitchMap$androidx$lifecycle$Lifecycle$State;
            state = State.STARTED;
            iArr6[3] = 2;
            iArr6 = $SwitchMap$androidx$lifecycle$Lifecycle$State;
            State state2 = State.RESUMED;
            iArr6[4] = 3;
            iArr6 = $SwitchMap$androidx$lifecycle$Lifecycle$State;
            state2 = State.DESTROYED;
            iArr6[0] = 4;
            iArr6 = $SwitchMap$androidx$lifecycle$Lifecycle$State;
            state2 = State.INITIALIZED;
            iArr6[1] = 5;
        }
    }

    public enum Event {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY;

        @Nullable
        public static Event downFrom(@NonNull State state) {
            int ordinal = state.ordinal();
            if (ordinal == 2) {
                return ON_DESTROY;
            }
            if (ordinal == 3) {
                return ON_STOP;
            }
            if (ordinal != 4) {
                return null;
            }
            return ON_PAUSE;
        }

        @Nullable
        public static Event downTo(@NonNull State state) {
            int ordinal = state.ordinal();
            if (ordinal == 0) {
                return ON_DESTROY;
            }
            if (ordinal == 2) {
                return ON_STOP;
            }
            if (ordinal != 3) {
                return null;
            }
            return ON_PAUSE;
        }

        @Nullable
        public static Event upFrom(@NonNull State state) {
            int ordinal = state.ordinal();
            if (ordinal == 1) {
                return ON_CREATE;
            }
            if (ordinal == 2) {
                return ON_START;
            }
            if (ordinal != 3) {
                return null;
            }
            return ON_RESUME;
        }

        @Nullable
        public static Event upTo(@NonNull State state) {
            int ordinal = state.ordinal();
            if (ordinal == 2) {
                return ON_CREATE;
            }
            if (ordinal == 3) {
                return ON_START;
            }
            if (ordinal != 4) {
                return null;
            }
            return ON_RESUME;
        }

        @NonNull
        public State getTargetState() {
            int ordinal = ordinal();
            if (ordinal != 0) {
                if (ordinal != 1) {
                    if (ordinal == 2) {
                        return State.RESUMED;
                    }
                    if (ordinal != 3) {
                        if (ordinal != 4) {
                            if (ordinal == 5) {
                                return State.DESTROYED;
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(this);
                            stringBuilder.append(" has no target state");
                            throw new IllegalArgumentException(stringBuilder.toString());
                        }
                    }
                }
                return State.STARTED;
            }
            return State.CREATED;
        }
    }

    public enum State {
        DESTROYED,
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED;

        public boolean isAtLeast(@NonNull State state) {
            return compareTo(state) >= 0;
        }
    }

    @MainThread
    public abstract void addObserver(@NonNull LifecycleObserver lifecycleObserver);

    @MainThread
    @NonNull
    public abstract State getCurrentState();

    @MainThread
    public abstract void removeObserver(@NonNull LifecycleObserver lifecycleObserver);
}
