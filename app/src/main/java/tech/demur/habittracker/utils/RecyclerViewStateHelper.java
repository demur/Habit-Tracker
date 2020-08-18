package tech.demur.habittracker.utils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import tech.demur.habittracker.BR;

public class RecyclerViewStateHelper extends BaseObservable {
    private Boolean loadingState = false;
    private Boolean errorState = false;

    public RecyclerViewStateHelper() {
    }

    @Bindable
    public Boolean getLoadingState() {
        return loadingState;
    }

    public void setLoadingState(Boolean state) {
        loadingState = state;
        notifyPropertyChanged(BR.loadingState);
    }

    @Bindable
    public Boolean getErrorState() {
        return errorState;
    }

    public void setErrorState(Boolean state) {
        errorState = state;
        notifyPropertyChanged(BR.errorState);
    }
}