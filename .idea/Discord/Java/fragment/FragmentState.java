package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.NonNull;

@SuppressLint({"BanParcelableUsage"})
public final class FragmentState implements Parcelable {
    public static final Creator<FragmentState> CREATOR = new Creator<FragmentState>() {
        public FragmentState createFromParcel(Parcel parcel) {
            return new FragmentState(parcel);
        }

        public FragmentState[] newArray(int i) {
            return new FragmentState[i];
        }
    };
    public final Bundle mArguments;
    public final String mClassName;
    public final int mContainerId;
    public final boolean mDetached;
    public final int mFragmentId;
    public final boolean mFromLayout;
    public final boolean mHidden;
    public final int mMaxLifecycleState;
    public final boolean mRemoving;
    public final boolean mRetainInstance;
    public Bundle mSavedFragmentState;
    public final String mTag;
    public final String mWho;

    public FragmentState(Fragment fragment) {
        this.mClassName = fragment.getClass().getName();
        this.mWho = fragment.mWho;
        this.mFromLayout = fragment.mFromLayout;
        this.mFragmentId = fragment.mFragmentId;
        this.mContainerId = fragment.mContainerId;
        this.mTag = fragment.mTag;
        this.mRetainInstance = fragment.mRetainInstance;
        this.mRemoving = fragment.mRemoving;
        this.mDetached = fragment.mDetached;
        this.mArguments = fragment.mArguments;
        this.mHidden = fragment.mHidden;
        this.mMaxLifecycleState = fragment.mMaxState.ordinal();
    }

    public int describeContents() {
        return 0;
    }

    @NonNull
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append("FragmentState{");
        stringBuilder.append(this.mClassName);
        stringBuilder.append(" (");
        stringBuilder.append(this.mWho);
        stringBuilder.append(")}:");
        if (this.mFromLayout) {
            stringBuilder.append(" fromLayout");
        }
        if (this.mContainerId != 0) {
            stringBuilder.append(" id=0x");
            stringBuilder.append(Integer.toHexString(this.mContainerId));
        }
        String str = this.mTag;
        if (!(str == null || str.isEmpty())) {
            stringBuilder.append(" tag=");
            stringBuilder.append(this.mTag);
        }
        if (this.mRetainInstance) {
            stringBuilder.append(" retainInstance");
        }
        if (this.mRemoving) {
            stringBuilder.append(" removing");
        }
        if (this.mDetached) {
            stringBuilder.append(" detached");
        }
        if (this.mHidden) {
            stringBuilder.append(" hidden");
        }
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mClassName);
        parcel.writeString(this.mWho);
        parcel.writeInt(this.mFromLayout);
        parcel.writeInt(this.mFragmentId);
        parcel.writeInt(this.mContainerId);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.mRetainInstance);
        parcel.writeInt(this.mRemoving);
        parcel.writeInt(this.mDetached);
        parcel.writeBundle(this.mArguments);
        parcel.writeInt(this.mHidden);
        parcel.writeBundle(this.mSavedFragmentState);
        parcel.writeInt(this.mMaxLifecycleState);
    }

    public FragmentState(Parcel parcel) {
        this.mClassName = parcel.readString();
        this.mWho = parcel.readString();
        boolean z = true;
        this.mFromLayout = parcel.readInt() != 0;
        this.mFragmentId = parcel.readInt();
        this.mContainerId = parcel.readInt();
        this.mTag = parcel.readString();
        this.mRetainInstance = parcel.readInt() != 0;
        this.mRemoving = parcel.readInt() != 0;
        this.mDetached = parcel.readInt() != 0;
        this.mArguments = parcel.readBundle();
        if (parcel.readInt() == 0) {
            z = false;
        }
        this.mHidden = z;
        this.mSavedFragmentState = parcel.readBundle();
        this.mMaxLifecycleState = parcel.readInt();
    }
}
