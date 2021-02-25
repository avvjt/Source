package androidx.transition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.content.res.TypedArrayUtils;
import f.d.b.a.a;
import java.io.IOException;
import java.lang.reflect.Constructor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TransitionInflater {
    private static final ArrayMap<String, Constructor<?>> CONSTRUCTORS = new ArrayMap();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class};
    private final Context mContext;

    private TransitionInflater(@NonNull Context context) {
        this.mContext = context;
    }

    private Object createCustom(AttributeSet attributeSet, Class<?> cls, String str) {
        String attributeValue = attributeSet.getAttributeValue(null, "class");
        if (attributeValue != null) {
            try {
                Object newInstance;
                ArrayMap arrayMap = CONSTRUCTORS;
                synchronized (arrayMap) {
                    Constructor constructor = (Constructor) arrayMap.get(attributeValue);
                    if (constructor == null) {
                        Class asSubclass = Class.forName(attributeValue, false, this.mContext.getClassLoader()).asSubclass(cls);
                        if (asSubclass != null) {
                            constructor = asSubclass.getConstructor(CONSTRUCTOR_SIGNATURE);
                            constructor.setAccessible(true);
                            arrayMap.put(attributeValue, constructor);
                        }
                    }
                    newInstance = constructor.newInstance(new Object[]{this.mContext, attributeSet});
                }
                return newInstance;
            } catch (Exception e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Could not instantiate ");
                stringBuilder.append(cls);
                stringBuilder.append(" class ");
                stringBuilder.append(attributeValue);
                throw new InflateException(stringBuilder.toString(), e);
            }
        }
        throw new InflateException(a.t(str, " tag must have a 'class' attribute"));
    }

    private Transition createTransitionFromXml(XmlPullParser xmlPullParser, AttributeSet attributeSet, Transition transition) throws XmlPullParserException, IOException {
        Transition transition2;
        int depth = xmlPullParser.getDepth();
        TransitionSet transitionSet = transition instanceof TransitionSet ? (TransitionSet) transition : null;
        loop0:
        while (true) {
            transition2 = null;
            while (true) {
                int next = xmlPullParser.next();
                if ((next != 3 || xmlPullParser.getDepth() > depth) && next != 1) {
                    if (next == 2) {
                        String name = xmlPullParser.getName();
                        if ("fade".equals(name)) {
                            transition2 = new Fade(this.mContext, attributeSet);
                        } else if ("changeBounds".equals(name)) {
                            transition2 = new ChangeBounds(this.mContext, attributeSet);
                        } else if ("slide".equals(name)) {
                            transition2 = new Slide(this.mContext, attributeSet);
                        } else if ("explode".equals(name)) {
                            transition2 = new Explode(this.mContext, attributeSet);
                        } else if ("changeImageTransform".equals(name)) {
                            transition2 = new ChangeImageTransform(this.mContext, attributeSet);
                        } else if ("changeTransform".equals(name)) {
                            transition2 = new ChangeTransform(this.mContext, attributeSet);
                        } else if ("changeClipBounds".equals(name)) {
                            transition2 = new ChangeClipBounds(this.mContext, attributeSet);
                        } else if ("autoTransition".equals(name)) {
                            transition2 = new AutoTransition(this.mContext, attributeSet);
                        } else if ("changeScroll".equals(name)) {
                            transition2 = new ChangeScroll(this.mContext, attributeSet);
                        } else if ("transitionSet".equals(name)) {
                            transition2 = new TransitionSet(this.mContext, attributeSet);
                        } else {
                            String str = "transition";
                            if (str.equals(name)) {
                                transition2 = (Transition) createCustom(attributeSet, Transition.class, str);
                            } else if ("targets".equals(name)) {
                                getTargetIds(xmlPullParser, attributeSet, transition);
                            } else if (!"arcMotion".equals(name)) {
                                str = "pathMotion";
                                if (str.equals(name)) {
                                    if (transition != null) {
                                        transition.setPathMotion((PathMotion) createCustom(attributeSet, PathMotion.class, str));
                                    } else {
                                        throw new RuntimeException("Invalid use of pathMotion element");
                                    }
                                } else if (!"patternPathMotion".equals(name)) {
                                    StringBuilder L = a.L("Unknown scene name: ");
                                    L.append(xmlPullParser.getName());
                                    throw new RuntimeException(L.toString());
                                } else if (transition != null) {
                                    transition.setPathMotion(new PatternPathMotion(this.mContext, attributeSet));
                                } else {
                                    throw new RuntimeException("Invalid use of patternPathMotion element");
                                }
                            } else if (transition != null) {
                                transition.setPathMotion(new ArcMotion(this.mContext, attributeSet));
                            } else {
                                throw new RuntimeException("Invalid use of arcMotion element");
                            }
                        }
                        if (transition2 == null) {
                            continue;
                        } else {
                            if (!xmlPullParser.isEmptyElementTag()) {
                                createTransitionFromXml(xmlPullParser, attributeSet, transition2);
                            }
                            if (transitionSet != null) {
                                break;
                            } else if (transition != null) {
                                throw new InflateException("Could not add transition to another transition.");
                            }
                        }
                    }
                }
            }
            transitionSet.addTransition(transition2);
        }
        return transition2;
    }

    /* JADX WARNING: Missing block: B:19:0x0052, code skipped:
            return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private TransitionManager createTransitionManagerFromXml(XmlPullParser xmlPullParser, AttributeSet attributeSet, ViewGroup viewGroup) throws XmlPullParserException, IOException {
        StringBuilder L;
        int depth = xmlPullParser.getDepth();
        TransitionManager transitionManager = null;
        while (true) {
            int next = xmlPullParser.next();
            if ((next != 3 || xmlPullParser.getDepth() > depth) && next != 1) {
                if (next == 2) {
                    String name = xmlPullParser.getName();
                    if (name.equals("transitionManager")) {
                        transitionManager = new TransitionManager();
                    } else if (!name.equals("transition") || transitionManager == null) {
                        L = a.L("Unknown scene name: ");
                        L.append(xmlPullParser.getName());
                    } else {
                        loadTransition(attributeSet, xmlPullParser, viewGroup, transitionManager);
                    }
                }
            }
        }
        L = a.L("Unknown scene name: ");
        L.append(xmlPullParser.getName());
        throw new RuntimeException(L.toString());
    }

    public static TransitionInflater from(Context context) {
        return new TransitionInflater(context);
    }

    @SuppressLint({"RestrictedApi"})
    private void getTargetIds(XmlPullParser xmlPullParser, AttributeSet attributeSet, Transition transition) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return;
            }
            if (next == 2) {
                if (xmlPullParser.getName().equals("target")) {
                    TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, Styleable.TRANSITION_TARGET);
                    int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "targetId", 1, 0);
                    if (namedResourceId != 0) {
                        transition.addTarget(namedResourceId);
                    } else {
                        int namedResourceId2 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "excludeId", 2, 0);
                        if (namedResourceId2 != 0) {
                            transition.excludeTarget(namedResourceId2, true);
                        } else {
                            String namedString = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "targetName", 4);
                            if (namedString != null) {
                                transition.addTarget(namedString);
                            } else {
                                namedString = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "excludeName", 5);
                                if (namedString != null) {
                                    transition.excludeTarget(namedString, true);
                                } else {
                                    String namedString2 = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "excludeClass", 3);
                                    if (namedString2 != null) {
                                        try {
                                            transition.excludeTarget(Class.forName(namedString2), true);
                                        } catch (ClassNotFoundException e) {
                                            obtainStyledAttributes.recycle();
                                            throw new RuntimeException(a.t("Could not create ", namedString2), e);
                                        }
                                    }
                                    namedString2 = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "targetClass", 0);
                                    if (namedString2 != null) {
                                        transition.addTarget(Class.forName(namedString2));
                                    }
                                }
                            }
                        }
                    }
                    obtainStyledAttributes.recycle();
                } else {
                    StringBuilder L = a.L("Unknown scene name: ");
                    L.append(xmlPullParser.getName());
                    throw new RuntimeException(L.toString());
                }
            }
        }
    }

    @SuppressLint({"RestrictedApi"})
    private void loadTransition(AttributeSet attributeSet, XmlPullParser xmlPullParser, ViewGroup viewGroup, TransitionManager transitionManager) throws NotFoundException {
        Scene scene;
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, Styleable.TRANSITION_MANAGER);
        int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "transition", 2, -1);
        int namedResourceId2 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "fromScene", 0, -1);
        Scene scene2 = null;
        if (namedResourceId2 < 0) {
            scene = null;
        } else {
            scene = Scene.getSceneForLayout(viewGroup, namedResourceId2, this.mContext);
        }
        int namedResourceId3 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "toScene", 1, -1);
        if (namedResourceId3 >= 0) {
            scene2 = Scene.getSceneForLayout(viewGroup, namedResourceId3, this.mContext);
        }
        if (namedResourceId >= 0) {
            Transition inflateTransition = inflateTransition(namedResourceId);
            if (inflateTransition != null) {
                if (scene2 == null) {
                    throw new RuntimeException(a.k("No toScene for transition ID ", namedResourceId));
                } else if (scene == null) {
                    transitionManager.setTransition(scene2, inflateTransition);
                } else {
                    transitionManager.setTransition(scene, scene2, inflateTransition);
                }
            }
        }
        obtainStyledAttributes.recycle();
    }

    public Transition inflateTransition(int i) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            Transition createTransitionFromXml = createTransitionFromXml(xml, Xml.asAttributeSet(xml), null);
            xml.close();
            return createTransitionFromXml;
        } catch (XmlPullParserException e) {
            throw new InflateException(e.getMessage(), e);
        } catch (IOException e2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(xml.getPositionDescription());
            stringBuilder.append(": ");
            stringBuilder.append(e2.getMessage());
            throw new InflateException(stringBuilder.toString(), e2);
        } catch (Throwable th) {
            xml.close();
        }
    }

    public TransitionManager inflateTransitionManager(int i, ViewGroup viewGroup) {
        InflateException inflateException;
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            TransitionManager createTransitionManagerFromXml = createTransitionManagerFromXml(xml, Xml.asAttributeSet(xml), viewGroup);
            xml.close();
            return createTransitionManagerFromXml;
        } catch (XmlPullParserException e) {
            inflateException = new InflateException(e.getMessage());
            inflateException.initCause(e);
            throw inflateException;
        } catch (IOException e2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(xml.getPositionDescription());
            stringBuilder.append(": ");
            stringBuilder.append(e2.getMessage());
            inflateException = new InflateException(stringBuilder.toString());
            inflateException.initCause(e2);
            throw inflateException;
        } catch (Throwable th) {
            xml.close();
        }
    }
}
