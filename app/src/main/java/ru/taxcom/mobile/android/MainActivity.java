package ru.taxcom.mobile.android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.taxcom.mobile.android.searchlibrary.views.SearchComponent;
import ru.taxcom.mobile.android.searchlibrary.views.SearchComponentView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchComponentView search = new SearchComponent(this, true);
        search.addSearchViewOnToolbar(this, findViewById(R.id.coordinatorLayout));
        search.display();
    }
}
