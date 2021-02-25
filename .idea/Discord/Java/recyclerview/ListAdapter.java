package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncDifferConfig.Builder;
import androidx.recyclerview.widget.AsyncListDiffer.ListListener;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;

public abstract class ListAdapter<T, VH extends ViewHolder> extends Adapter<VH> {
    public final AsyncListDiffer<T> mDiffer;
    private final ListListener<T> mListener;

    public ListAdapter(@NonNull ItemCallback<T> itemCallback) {
        AnonymousClass1 anonymousClass1 = new ListListener<T>() {
            public void onCurrentListChanged(@NonNull List<T> list, @NonNull List<T> list2) {
                ListAdapter.this.onCurrentListChanged(list, list2);
            }
        };
        this.mListener = anonymousClass1;
        AsyncListDiffer asyncListDiffer = new AsyncListDiffer(new AdapterListUpdateCallback(this), new Builder(itemCallback).build());
        this.mDiffer = asyncListDiffer;
        asyncListDiffer.addListListener(anonymousClass1);
    }

    @NonNull
    public List<T> getCurrentList() {
        return this.mDiffer.getCurrentList();
    }

    public T getItem(int i) {
        return this.mDiffer.getCurrentList().get(i);
    }

    public int getItemCount() {
        return this.mDiffer.getCurrentList().size();
    }

    public void onCurrentListChanged(@NonNull List<T> list, @NonNull List<T> list2) {
    }

    public void submitList(@Nullable List<T> list) {
        this.mDiffer.submitList(list);
    }

    public void submitList(@Nullable List<T> list, @Nullable Runnable runnable) {
        this.mDiffer.submitList(list, runnable);
    }

    public ListAdapter(@NonNull AsyncDifferConfig<T> asyncDifferConfig) {
        AnonymousClass1 anonymousClass1 = /* anonymous class already generated */;
        this.mListener = anonymousClass1;
        AsyncListDiffer asyncListDiffer = new AsyncListDiffer(new AdapterListUpdateCallback(this), (AsyncDifferConfig) asyncDifferConfig);
        this.mDiffer = asyncListDiffer;
        asyncListDiffer.addListListener(anonymousClass1);
    }
}
