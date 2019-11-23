package ru.taxcom.mobile.android.searchlibrary.views;

import android.app.Activity;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.searchlibrary.util.SearchValidation;
import ru.taxcom.mobile.android.searchlibrary.util.textview.RegularEditText;

public interface SearchComponentView {
    /**
     * Добавляет поиск поверх контента над тулбаром, учитывая наличие дровера
     *
     * @param activity     для подключения
     * @param view         основной контент
     * @param drawerLayout дровер
     */
    void addSearchViewOnToolbarWithDrawer(@NonNull Activity activity, @NonNull View view, @NonNull DrawerLayout drawerLayout);

    /**
     * Добавляет поиск поверх контента над тулбаром
     *
     * @param activity для подключения
     * @param view     основной контент
     */
    void addSearchViewOnToolbar(@NonNull Activity activity, @NonNull View view);

    /**
     * Добавляет поиск поверх контента над тулбаром
     *
     * @param activity для подключения
     * @param res      основной контент
     */
    void addSearchViewOnToolbar(@NonNull Activity activity, @LayoutRes int res);

    RegularEditText getSearchEditText();

    /**
     * устанавливает выпадающий список и адаптер для него
     *
     * @param adapter
     */
    void setAdapter(RecyclerView.Adapter adapter);

    RecyclerView getList();

    void setVisibleError(boolean visibleError, boolean visibleBtnRetry);

    void setErrorMessage(String error);

    void setOnRetryClick(View.OnClickListener listener);

    /**
     * скрывает выпадающий список
     */
    void hideDropDownList();

    /**
     * отображает выпадающий список
     */
    void showDropDownList();

    boolean isDropDownVisible();

    void setOnSearchListener(SearchComponent.onSearchListener onSearchListener);

    /**
     * скрывает и очищает строку поиска
     */
    void hide();

    /**
     * скрывает поиск
     */
    void hideSearch();

    /**
     * отображает поиск
     */
    void display();

    /**
     * отображается ли поиск над контентом
     *
     * @return true если поиск отображается
     */
    boolean isSearchViewVisible();

    /**
     * устанавливает подсказку для поиска
     *
     * @param hint
     */
    void setTextHint(String hint);

    /**
     * получение событий нажатий по клавишам на клавиатуре
     *
     * @param listener
     */
    void setOnActionListener(EditText.OnEditorActionListener listener);

    /**
     * получение событий нажатий аппаратной кнопки back при наличии фокуса на строке ввода поиска
     *
     * @param listener
     */
    void setOnBackPressedListener(RegularEditText.OnBackListener listener);

    /**
     * добавляет listener для получения событий ввода текста
     *
     * @param textWatcher
     */
    void addTextChangedListener(TextWatcher textWatcher);

    /**
     * устанавливает строку в editText
     *
     * @param filterString
     */
    void setText(String filterString);

    /**
     * устанавливает максимальную длину строки в editText
     *
     * @param maxLength
     */
    void setMaxLength(Integer maxLength);

    /**
     * получение основного контента поиска
     *
     * @return view поиска
     */
    View getContent();

    /**
     * устанавливает поиск по локальному списку
     *
     * @param filter сравнивает поля модели со строкой поиска
     * @param result получение результата
     * @param list   список по которому будет происходить поиск
     */
    <T> void setFilteringBySearch(SearchValidation.Predicate<T> filter, Consumer<List<T>> result, List<T> list);


    /**
     * обновление списка для локального поиска
     *
     * @param list
     */
    <T> void refreshList(@NonNull List<T> list);

    /**
     * проверяет есть ли в строке поиска значение и производит поиск по локальному списку
     */
    void checkSearchInputAndRefresh();

    boolean isSearchEmpty();


    /**
     * устанавливает отступы для SearchView
     *
     * @param left левый margin
     * @param top  верхний margin
     * @param right правый margin
     * @param bottom нижний margin
     */
    void setMarginSearchView(int left, int top, int right, int bottom);

    /**
     * возвращает строку заглушку при пустом результате
     *
     * @param isFilterActive
     * @param dataText       Пример:
     *                       На данный момент нет dataText, соответствующих поисковому запросу \"%2$s\" и параметрам фильтрации
     * @return
     */
    String getSearchEmptyText(boolean isFilterActive, String dataText);

    Observable<CharSequence> getObservable();

    /**
     * отписывается от наблюдения за изменением в строке поиска
     */
    void dispose();
}

