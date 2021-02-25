package androidx.activity.result;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import f.d.b.a.a;

@SuppressLint({"BanParcelableUsage"})
public final class ActivityResult implements Parcelable {
    @NonNull
    public static final Creator<ActivityResult> CREATOR = new Creator<ActivityResult>() {
        public ActivityResult createFromParcel(@NonNull Parcel parcel) {
            return new ActivityResult(parcel);
        }

        public ActivityResult[] newArray(int i) {
            return new ActivityResult[i];
        }
    };
    @Nullable
    private final Intent mData;
    private final int mResultCode;

    public ActivityResult(int i, @Nullable Intent intent) {
        this.mResultCode = i;
        this.mData = intent;
    }

    @NonNull
    public static String resultCodeToString(int i) {
        if (i != -1) {
            return i != 0 ? String.valueOf(i) : "RESULT_CANCELED";
        } else {
            return "RESULT_OK";
        }
    }

    public int describeContents() {
        return 0;
    }

    @Nullable
    public Intent getData() {
        return this.mData;
    }

    public int getResultCode() {
        return this.mResultCode;
    }

    public String toString() {
        StringBuilder L = a.L("ActivityResult{resultCode=");
        L.append(resultCodeToString(this.mResultCode));
        L.append(", data=");
        L.append(this.mData);
        L.append('}');
        return L.toString();
    }

    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(this.mResultCode);
        parcel.writeInt(this.mData == null ? 0 : 1);
        Intent intent = this.mData;
        if (intent != null) {
            intent.writeToParcel(parcel, i);
        }
    }

    public ActivityResult(Parcel parcel) {
        this.mResultCode = parcel.readInt();
        this.mData = parcel.readInt() == 0 ? null : (Intent) Intent.CREATOR.createFromParcel(parcel);
    }
}
