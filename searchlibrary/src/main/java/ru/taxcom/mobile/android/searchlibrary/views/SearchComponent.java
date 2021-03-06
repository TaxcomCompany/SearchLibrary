package ru.taxcom.mobile.android.searchlibrary.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.searchlibrary.R;
import ru.taxcom.mobile.android.searchlibrary.util.KeyBoardUtils;
import ru.taxcom.mobile.android.searchlibrary.util.SearchValidation;
import ru.taxcom.mobile.android.searchlibrary.util.textview.RegularEditText;

public class SearchComponent extends FrameLayout implements SearchComponentView {

    public interface onSearchListener {

        void searchViewClosed();

        void searchViewOpened();

    }

    private SearchComponent.onSearchListener mOnSearchListener;
    private Context mContext;
    private boolean mDropDownEnabled;
    private RelativeLayout mShadow;
    private RelativeLayout mContentSearch;
    private RegularEditText mSearchEditText;
    private ImageView mClearSearch;
    private RecyclerView mList;
    private RelativeLayout mDropDownLayout;
    private RelativeLayout mRootSearchView;
    private SearchValidation mValidation;

    private void initViews() {
        mShadow = findViewById(R.id.shadow);
        mContentSearch = findViewById(R.id.content_search);
        mSearchEditText = findViewById(R.id.edit_text_search);
        mClearSearch = findViewById(R.id.image_clear_search);
        mList = findViewById(R.id.list);
        mDropDownLayout = findViewById(R.id.dropdown);
        mRootSearchView = findViewById(R.id.root);


        ImageView backSearch = findViewById(R.id.image_back_search);
        backSearch.setOnClickListener(this::onClick);
        mClearSearch.setOnClickListener(this::onClick);
    }

    public SearchComponent(final Context context, boolean isDefaultSearch) {
        this(context, null);
        mContext = context;

        final LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(isDefaultSearch ? R.layout.serchview : R.layout.searchview_with_dropdown, this);
        initViews();

        mSearchEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleClearSearchButton(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSearchEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyBoard();
            }
        });

        if (mShadow != null) {
            mShadow.setOnClickListener(view -> hide());
        }
    }

    public SearchComponent(final Context context, final AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SearchComponent(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
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
    public RegularEditText getSearchEditText() {
        return mSearchEditText;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            mDropDownEnabled = true;
            mList.setAdapter(adapter);
        }
    }

    @Override
    public RecyclerView getList() {
        return mList;
    }

    @Override
    public void setVisibleError(boolean visibleError, boolean visibleBtnRetry) {
        if (isDropDownVisible()) {
            findViewById(R.id.searchErrorView)
                    .setVisibility(visibleError ? View.VISIBLE : View.GONE);
            findViewById(R.id.searchRetryButton)
                    .setVisibility(visibleBtnRetry ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setErrorMessage(String error) {
        TextView errorMessage = findViewById(R.id.searchErrorMessage);
        errorMessage.setText(error);
    }

    @Override
    public void setOnRetryClick(OnClickListener listener) {
        if (mDropDownEnabled) {
            findViewById(R.id.searchRetryButton).setOnClickListener(listener);
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
    public boolean isDropDownVisible() {
        return mDropDownLayout.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setOnSearchListener(SearchComponent.onSearchListener onSearchListener) {
        if (onSearchListener != null) {
            mOnSearchListener = onSearchListener;
        }
    }

    @Override
    public void addTextChangedListener(TextWatcher textWatcher) {
        mSearchEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public void setText(String filterString) {
        mSearchEditText.setText(filterString);
        mSearchEditText.setSelection(filterString.length());
    }

    @Override
    public void setMaxLength(Integer maxLength) {
        mSearchEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    @Override
    public View getContent() {
        return this;
    }

    @Override
    public void hide() {
        hideSearch();
        clearSearch();
        hideDropDownList();
    }

    @Override
    public void hideSearch() {
        if (!isSearchViewVisible()) return;
        if (mOnSearchListener != null) {
            mOnSearchListener.searchViewClosed();
        }
        hideKeyBoard();
        mContentSearch.setVisibility(View.GONE);
    }

    @Override
    public void display() {
        if (isSearchViewVisible()) return;
        if (mOnSearchListener != null) {
            mOnSearchListener.searchViewOpened();
        }
        mContentSearch.setVisibility(VISIBLE);
        mContentSearch.setEnabled(false);

        mSearchEditText.requestFocus();
        showKeyBoard();
    }

    @Override
    public boolean isSearchViewVisible() {
        return mContentSearch.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setTextHint(String hint) {
        mSearchEditText.setHint(hint);
    }

    @Override
    public void setOnActionListener(EditText.OnEditorActionListener listener) {
        mSearchEditText.setOnEditorActionListener(listener);
    }

    @Override
    public void setOnBackPressedListener(RegularEditText.OnBackListener listener) {
        if (listener != null) {
            mSearchEditText.setOnBackListener(listener);
        }
    }

    private void removeParentView(@NonNull View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    private void toggleClearSearchButton(final CharSequence query) {
        mClearSearch.setVisibility(!TextUtils.isEmpty(query) ? View.VISIBLE : View.GONE);
    }

    private void hideKeyBoard() {
        KeyBoardUtils.hideKeyboard(getContext(), mSearchEditText);
    }

    private void showKeyBoard() {
        KeyBoardUtils.showKeyboard(getContext(), mSearchEditText);
    }

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
        mSearchEditText.setText("");
        mClearSearch.setVisibility(View.GONE);
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
            String text = mSearchEditText.getText().toString();
            mSearchEditText.setText(text);
            mSearchEditText.setSelection(text.length());
            display();
        }
    }

    @Override
    public boolean isSearchEmpty() {
        return TextUtils.isEmpty(getSearchEditText().getText().toString());
    }

    @Override
    public void setMarginSearchView(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams FrameLayout = (FrameLayout.LayoutParams) mRootSearchView.getLayoutParams();
        FrameLayout.setMargins(left, top, right, bottom);  // left, top, right, bottom
        mRootSearchView.setLayoutParams(FrameLayout);
    }

    @Override
    public String getSearchEmptyText(boolean isFilterActive, String dataText) {
        return mValidation.getEmptyText(getSearchEditText().getText().toString(), isFilterActive, dataText);
    }

    @Override
    public Observable<CharSequence> getObservable() {
        return RxTextView.textChanges(getSearchEditText());
    }

    @Override
    public void dispose() {
        if (mValidation != null) {
            mValidation.dispose();
        }
    }
}