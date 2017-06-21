package com.balamurugan.altien;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.places.AutocompleteFilter;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_CITY_PLACE = 1000;
    private static final int REQUEST_LOCALITY_PLACE = 2000;
    final static int CITY = 1;
    final static int LOCALTIY = 2;
    static int current;
    int filter;
    Fragment fragment;
    static boolean citydone;
    static boolean locdone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle("Altien");

        current = CITY;
        citydone = locdone = false;
        filter = AutocompleteFilter.TYPE_FILTER_CITIES;

        fragment = new CityFragment();

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }




     //   autocompleteFragment.setBoundsBias();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     else  call map

                if (fragment != null) {
                    if(current == CITY && citydone) {
                        fragment = new LocalityFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack("Locality")
                                .replace(R.id.container, fragment).commit();
                        current = LOCALTIY;
                    }
                    else  if(current == LOCALTIY && locdone){
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        MapsActivity.skippedSecond = false;
                        startActivity(intent);
                    }
                }else {

                    if(current == CITY)
                        Snackbar.make(view, "Choose a city!", Snackbar.LENGTH_LONG).show();
                    if(current == LOCALTIY)
                        Snackbar.make(view, "Choose a locality!", Snackbar.LENGTH_LONG).show();
                    // error in creating fragment
                    Log.e("MainActivity", "Error in creating fragment");
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(fragment !=null)
            fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}
