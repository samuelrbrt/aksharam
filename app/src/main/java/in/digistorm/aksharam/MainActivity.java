/*
 * Copyright (c) 2022 Alan M Varghese <alan@digistorm.in>
 *
 * This files is part of Aksharam, a script teaching app for Indic
 * languages.
 *
 * Aksharam is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Aksharam is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even teh implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package in.digistorm.aksharam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private ViewPager2 viewPager;
    private static PageCollectionAdapter pageCollectionAdapter;
    private int tabPosition;
    private String logTag = MainActivity.class.getName();

    public static void replaceTabFragment(int index, Fragment fragment) {
        pageCollectionAdapter.replaceFragment(index, fragment);
    }

    public static void setActionBarBackButton(boolean enabled) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Simple initialisation so that the app doesn't break on launch
        new Transliterator(getApplicationContext());

        pageCollectionAdapter = new PageCollectionAdapter(this);
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pageCollectionAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(this);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                tabConfigurationStrategy()
        );
        tabLayoutMediator.attach();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // getSupportActionBar().setDisplayOptions(0, ActionBar.D);
        // ActionBar actionBar = getSupportActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // getSupportActionBar().getDisplayOptions();
        Log.d(logTag, "menuItem is: " + item.toString());
        Log.d(logTag, "menuItem id is: " + item.getItemId());
        // findViewById(ActionMenuItem).getS

        return super.onOptionsItemSelected(item);
    }

    public static TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy() {
        return (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText(R.string.letters_tab_header);
                    break;
                case 1:
                    tab.setText(R.string.transliterate_tab_header);
                    break;
                case 2:
                    tab.setText(R.string.practice_tab_header);
                    break;
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (!pageCollectionAdapter.restoreFragment(tabPosition))
            super.onBackPressed();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabPosition = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
