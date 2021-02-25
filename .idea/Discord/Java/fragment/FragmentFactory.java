package androidx.fragment.app;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;
import androidx.fragment.app.Fragment.InstantiationException;
import f.d.b.a.a;
import java.lang.reflect.InvocationTargetException;

public class FragmentFactory {
    private static final SimpleArrayMap<ClassLoader, SimpleArrayMap<String, Class<?>>> sClassCacheMap = new SimpleArrayMap();

    public static boolean isFragmentClass(@NonNull ClassLoader classLoader, @NonNull String str) {
        try {
            return Fragment.class.isAssignableFrom(loadClass(classLoader, str));
        } catch (ClassNotFoundException unused) {
            return false;
        }
    }

    @NonNull
    private static Class<?> loadClass(@NonNull ClassLoader classLoader, @NonNull String str) throws ClassNotFoundException {
        SimpleArrayMap simpleArrayMap = sClassCacheMap;
        SimpleArrayMap simpleArrayMap2 = (SimpleArrayMap) simpleArrayMap.get(classLoader);
        if (simpleArrayMap2 == null) {
            simpleArrayMap2 = new SimpleArrayMap();
            simpleArrayMap.put(classLoader, simpleArrayMap2);
        }
        Class<?> cls = (Class) simpleArrayMap2.get(str);
        if (cls != null) {
            return cls;
        }
        Class cls2 = Class.forName(str, false, classLoader);
        simpleArrayMap2.put(str, cls2);
        return cls2;
    }

    @NonNull
    public static Class<? extends Fragment> loadFragmentClass(@NonNull ClassLoader classLoader, @NonNull String str) {
        String str2 = "Unable to instantiate fragment ";
        try {
            return loadClass(classLoader, str);
        } catch (ClassNotFoundException e) {
            throw new InstantiationException(a.u(str2, str, ": make sure class name exists"), e);
        } catch (ClassCastException e2) {
            throw new InstantiationException(a.u(str2, str, ": make sure class is a valid subclass of Fragment"), e2);
        }
    }

    @NonNull
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String str) {
        String str2 = ": make sure class name exists, is public, and has an empty constructor that is public";
        String str3 = "Unable to instantiate fragment ";
        try {
            return (Fragment) loadFragmentClass(classLoader, str).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (InstantiationException e) {
            throw new InstantiationException(a.u(str3, str, str2), e);
        } catch (IllegalAccessException e2) {
            throw new InstantiationException(a.u(str3, str, str2), e2);
        } catch (NoSuchMethodException e3) {
            throw new InstantiationException(a.u(str3, str, ": could not find Fragment constructor"), e3);
        } catch (InvocationTargetException e4) {
            throw new InstantiationException(a.u(str3, str, ": calling Fragment constructor caused an exception"), e4);
        }
    }
}
