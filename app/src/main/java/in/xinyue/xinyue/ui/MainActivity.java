package in.xinyue.xinyue.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.ui.fragment.AboutFragment;
import in.xinyue.xinyue.ui.fragment.TabContainerFragment;

/**
 * Created by pzheng on 7/28/2015.
 */
public class MainActivity extends AppCompatActivity {

    // Defining variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // Setting Navigation VIew Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // this method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                // Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                }
                else {
                    menuItem.setChecked(true);
                }

                // closing drawer on item click
                drawerLayout.closeDrawers();

                // check to see which item was being clicked and perform appropriate action
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.content_fragment:
                        fragment = TabContainerFragment.newInstance();
                        transactFragment(fragment);
                        return true;
                    case R.id.about_fragment:
                        fragment = AboutFragment.newInstance();
                        transactFragment(fragment);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Something wrong",
                                Toast.LENGTH_SHORT).show();
                        return true;
                }

            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Setting the acitonbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        Fragment fragment = TabContainerFragment.newInstance();
        transactFragment(fragment);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void transactFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);

        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listmenu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        //if (id == R.id.refresh) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView))
            drawerLayout.closeDrawer(navigationView);
        else if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else
            super.onBackPressed();
    }

}
