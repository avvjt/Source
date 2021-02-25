package androidx.lifecycle;

import androidx.lifecycle.ViewModelProvider.Factory;
import f.i.a.f.f.o.g;
import kotlin.Lazy;
import kotlin.jvm.functions.Function0;
import u.m.c.j;
import u.q.b;

/* compiled from: ViewModelProvider.kt */
public final class ViewModelLazy<VM extends ViewModel> implements Lazy<VM> {
    private VM cached;
    private final Function0<Factory> factoryProducer;
    private final Function0<ViewModelStore> storeProducer;
    private final b<VM> viewModelClass;

    public ViewModelLazy(b<VM> bVar, Function0<? extends ViewModelStore> function0, Function0<? extends Factory> function02) {
        j.checkNotNullParameter(bVar, "viewModelClass");
        j.checkNotNullParameter(function0, "storeProducer");
        j.checkNotNullParameter(function02, "factoryProducer");
        this.viewModelClass = bVar;
        this.storeProducer = function0;
        this.factoryProducer = function02;
    }

    public boolean isInitialized() {
        return this.cached != null;
    }

    public VM getValue() {
        VM vm = this.cached;
        if (vm != null) {
            return vm;
        }
        ViewModel viewModel = new ViewModelProvider((ViewModelStore) this.storeProducer.invoke(), (Factory) this.factoryProducer.invoke()).get(g.getJavaClass(this.viewModelClass));
        this.cached = viewModel;
        j.checkNotNullExpressionValue(viewModel, "ViewModelProvider(store,…ed = it\n                }");
        return viewModel;
    }
}
