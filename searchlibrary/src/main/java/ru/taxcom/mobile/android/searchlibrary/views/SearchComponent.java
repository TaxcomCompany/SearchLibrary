package ru.taxcom.mobile.android.searchlibrary.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.searchlibrary.R;
import ru.taxcom.mobile.android.searchlibrary.R2;
import ru.taxcom.mobile.android.searchlibrary.util.KeyBoardUtils;
import ru.taxcom.mobile.android.searchlibrary.util.SearchValidation;
import ru.taxcom.mobile.android.searchlibrary.util.textview.CustomEditText;

public class SearchComponent extends FrameLayout implements SearchComponentView {

    public interface onSearchListener {

        void searchViewClosed();

        void searchViewOpened();

        void onTextChanged(String s);

    }

    private SearchComponent.onSearchListener mOnSearchListener;
    private Context mContext;
    private boolean mDropDownEnabled;

    @Nullable
    @BindView(R2.id.root)
    RelativeLayout mRoot;
    @Nullable
    @BindView(R2.id.shadow)
    RelativeLayout mShadow;
    @BindView(R2.id.content_search)
    RelativeLayout contentSearch;
    @BindView(R2.id.edit_text_search)
    CustomEditText searchEditText;
    @BindView(R2.id.image_clear_search)
    ImageView clearSearch;
    @Nullable
    @BindView(R2.id.search_list)
    ListView mListView;
    @Nullable
    @BindView(R2.id.dropdown)
    LinearLayout mDropDownLayout;

    private SearchValidation mValidation;

    public SearchComponent(final Context context, boolean isDefaultSearch) {
        this(context, null);
        mContext = context;

        final LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(isDefaultSearch ? R.layout.serchview_layout : R.layout.searchview_with_dropdown, this);
        ButterKnife.bind(this);

        searchEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleClearSearchButton(s);

                if (mDropDownEnabled) {
                    toggleDropDownList(s);
                }

                if (mOnSearchListener != null) {
                    mOnSearchListener.onTextChanged(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyBoard();
            }
        });
    }

    public SearchComponent(final Context context, final AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SearchComponent(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void toggleDropDownList(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            hideDropDownList();
        } else {
            showDropDownList();
        }
    }

    @Override
    public void addSearchViewOnToolbarWithDrawer(@NonNull Activity activity, @NonNull View view, @NonNull DrawerLayout drawerLayout) {
        removeParentView(view);
        addView(view, 0);
        removeParentView(this);
        drawerLayout.addView(this, 0);
        activity.setContentView(drawerLayout);
    }

    @Override
    public void addSearchViewOnToolbar(@NonNull final Activity activity, @NonNull View view) {
        removeParentView(view);
        addView(view, 0);
        activity.setContentView(this);
    }

    @Override
    public void addSearchViewOnToolbar(@NonNull final Activity activity, @LayoutRes int res) {
        LayoutInflater inflater = activity.getLayoutInflater();
        addView(inflater.inflate(res, null), 0);
        activity.setContentView(this);
    }

    @Override
    public CustomEditText getSearchEditText() {
        return searchEditText;
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mDropDownEnabled = true;
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        if (listener != null) {
            mListView.setOnItemClickListener(listener);
        }
    }

    @Override
    public void hideDropDownList() {
        if (mDropDownEnabled) {
            mDropDownLayout.setVisibility(GONE);
            mShadow.setVisibility(GONE);
        }
    }

    @Override
    public void showDropDownList() {
        if (mDropDownEnabled) {
            mDropDownLayout.setVisibility(VISIBLE);
            mShadow.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setOnSearchListener(SearchComponent.onSearchListener onSearchListener) {
        if (onSearchListener != null) {
            mOnSearchListener = onSearchListener;
        }
    }

    @Override
    public void addTextChangedListener(TextWatcher textWatcher) {
        searchEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public void setText(String filterString) {
        searchEditText.setText(filterString);
        searchEditText.setSelection(filterString.length());
    }

    @Override
    public View getContent() {
        return this;
    }

    @Override
    public void hide() {
        hideSearch();
        clearSearch();
    }

    @Override
    public void hideSearch() {
        if (!isSearchViewVisible()) return;
        if (mOnSearchListener != null) {
            mOnSearchListener.searchViewClosed();
        }
        hideKeyBoard();
        contentSearch.setVisibility(View.GONE);
    }

    @Override
    public void display() {
        if (isSearchViewVisible()) return;
        if (mOnSearchListener != null) {
            mOnSearchListener.searchViewOpened();
        }
        contentSearch.setVisibility(VISIBLE);
        contentSearch.setEnabled(false);

        searchEditText.requestFocus();
        showKeyBoard();
    }

    @Override
    public boolean isSearchViewVisible() {
        return contentSearch.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setTextHint(String hint) {
        searchEditText.setHint(hint);
    }

    @Override
    public void setOnActionListener(EditText.OnEditorActionListener listener) {
        searchEditText.setOnEditorActionListener(listener);
    }

    @Override
    public void setOnBackPressedListener(CustomEditText.OnBackListener listener) {
        if (listener != null) {
            searchEditText.setOnBackListener(listener);
        }
    }

    private void removeParentView(@NonNull View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    private void toggleClearSearchButton(final CharSequence query) {
        clearSearch.setVisibility(!TextUtils.isEmpty(query) ? View.VISIBLE : View.GONE);
    }

    private void hideKeyBoard() {
        KeyBoardUtils.hideKeyboard(getContext(), searchEditText);
    }

    private void showKeyBoard() {
        KeyBoardUtils.showKeyboard(getContext(), searchEditText);
    }

    @OnClick({R2.id.image_back_search, R2.id.image_clear_search})
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.image_back_search) {
            onCancelSearch();
        } else if (id == R.id.image_clear_search) {
            clearSearch();
        }
    }

    private void onCancelSearch() {
        hide();
    }

    private void clearSearch() {
        searchEditText.setText("");
        clearSearch.setVisibility(View.GONE);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE && isSearchViewVisible()) {
            new Handler().postDelayed(this::showKeyBoard, 300);
        } else {
            hideKeyBoard();
        }
    }

    @Override
    public <T> void setFilteringBySearch(SearchValidation.Predicate<T> filter, Consumer<List<T>> result, List<T> list) {
        mValidation = new SearchValidation(mContext);
        mValidation.setList(list);
        mValidation.setFilteringBySearch(RxTextView.textChanges(getSearchEditText()),
                result, s -> mValidation.filterList(s, filter));
    }

    @Override
    public <T> void refreshList(@NonNull List<T> list) {
        mValidation.setList(list);
    }

    @Override
    public void checkSearchInputAndRefresh() {
        if (!isSearchEmpty()) {
            String text = searchEditText.getText().toString();
            searchEditText.setText(text);
            searchEditText.setSelection(text.length());
            display();
        }
    }

    @Override
    public boolean isSearchEmpty() {
        return TextUtils.isEmpty(getSearchEditText().getText().toString());
    }

    @Override
    public String getSearchEmptyText(boolean isEmptyList, boolean isFilterActive, String dataText) {
        return mValidation.getEmptyText(isEmptyList, getSearchEditText().getText().toString(), isFilterActive, dataText);
    }

    @Override
    public Observable<CharSequence> getObservable() {
        return RxTextView.textChanges(getSearchEditText());
    }
}