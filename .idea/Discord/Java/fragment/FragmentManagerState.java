package androidx.fragment.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.fragment.app.FragmentManager.LaunchedFragmentInfo;
import java.util.ArrayList;

@SuppressLint({"BanParcelableUsage"})
public final class FragmentManagerState implements Parcelable {
    public static final Creator<FragmentManagerState> CREATOR = new Creator<FragmentManagerState>() {
        public FragmentManagerState createFromParcel(Parcel parcel) {
            return new FragmentManagerState(parcel);
        }

        public FragmentManagerState[] newArray(int i) {
            return new FragmentManagerState[i];
        }
    };
    public ArrayList<FragmentState> mActive;
    public ArrayList<String> mAdded;
    public BackStackState[] mBackStack;
    public int mBackStackIndex;
    public ArrayList<LaunchedFragmentInfo> mLaunchedFragments;
    public String mPrimaryNavActiveWho = null;
    public ArrayList<String> mResultKeys = new ArrayList();
    public ArrayList<Bundle> mResults = new ArrayList();

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(this.mActive);
        parcel.writeStringList(this.mAdded);
        parcel.writeTypedArray(this.mBackStack, i);
        parcel.writeInt(this.mBackStackIndex);
        parcel.writeString(this.mPrimaryNavActiveWho);
        parcel.writeStringList(this.mResultKeys);
        parcel.writeTypedList(this.mResults);
        parcel.writeTypedList(this.mLaunchedFragments);
    }

    public FragmentManagerState(Parcel parcel) {
        this.mActive = parcel.createTypedArrayList(FragmentState.CREATOR);
        this.mAdded = parcel.createStringArrayList();
        this.mBackStack = (BackStackState[]) parcel.createTypedArray(BackStackState.CREATOR);
        this.mBackStackIndex = parcel.readInt();
        this.mPrimaryNavActiveWho = parcel.readString();
        this.mResultKeys = parcel.createStringArrayList();
        this.mResults = parcel.createTypedArrayList(Bundle.CREATOR);
        this.mLaunchedFragments = parcel.createTypedArrayList(LaunchedFragmentInfo.CREATOR);
    }
}
