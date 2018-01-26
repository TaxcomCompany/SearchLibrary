package ru.taxcom.mobile.android.searchlibrary.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ru.taxcom.mobile.android.searchlibrary.R;

import static android.text.TextUtils.isEmpty;

public class SearchValidation {

    private final Context mContext;
    private List mList;

    public SearchValidation(@NonNull Context context) {
        mContext = context;
    }

    public <T> void setList(List<T> list) {
        mList = list;
    }

    public interface Predicate<T> {
        boolean filter(T t, String search);
    }

    public static boolean compare(String search, String field) {
        if (isEmpty(field)) {
            return false;
        }
        field = field.toLowerCase();
        return field.contains(search);
    }

    public <T> void setFilteringBySearch(Observable<CharSequence> observable,
                                         Consumer<List<T>> result, Function<String, List<T>> mapper) {
        observable
                .debounce(200, TimeUnit.MILLISECONDS)
                .map(this::mapSearch)
                .map(mapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result);
    }

    private String mapSearch(CharSequence s) {
        return s.toString().trim().toLowerCase();
    }

    public <T> List<T> filterList(String search, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T model : (List<T>) mList) {
            if (predicate.filter(model, search)) {
                result.add(model);
            }
        }
        return result;
    }

    public String getEmptyText(boolean isEmptyList, String search, boolean isFilterActive, String dataText) {
        if (!isEmptyList) {
            return null;
        } else {
            boolean isSearchActive = !isEmpty(search);
            if (isFilterActive && isSearchActive) {
                return mContext.getString(R.string.empty_search_and_filter, dataText, search);
            } else if (isFilterActive) {
                return mContext.getString(R.string.empty_filter, dataText);
            } else if (isSearchActive) {
                return mContext.getString(R.string.empty_search, dataText, search);
            } else {
                return mContext.getString(R.string.empty_data);
            }
        }
    }
}