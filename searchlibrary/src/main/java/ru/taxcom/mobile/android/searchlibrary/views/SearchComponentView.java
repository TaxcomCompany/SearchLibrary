package ru.taxcom.mobile.android.searchlibrary.views;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import ru.taxcom.mobile.android.searchlibrary.util.SearchValidation;
import ru.taxcom.mobile.android.searchlibrary.util.textview.CustomEditText;

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

    CustomEditText getSearchEditText();

    /**
     * устанавливает выпадающий список и адаптер для него
     *
     * @param adapter
     */
    void setAdapter(BaseAdapter adapter);

    /**
     * получение событий нажатия по элементу из выпадающего списка
     *
     * @param listener
     */
    void setOnItemClickListener(AdapterView.OnItemClickListener listener);

    /**
     * скрывает выпадающий список
     */
    void hideDropDownList();

    /**
     * отображает выпадающий список
     */
    void showDropDownList();

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
    void setOnBackPressedListener(CustomEditText.OnBackListener listener);

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
     * возвращает строку заглушку при пустом результате
     *
     * @param isEmptyList
     * @param isFilterActive
     * @param dataText       Привемер:
     *                       На данный момент нет dataText соответствующих поисковому запросу \"%2$s\" и параметрам фильтрации
     * @return
     */
    String getSearchEmptyText(boolean isEmptyList, boolean isFilterActive, String dataText);

    Observable<CharSequence> getObservable();
}

