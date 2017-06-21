package com.balamurugan.altien;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HouseDetailsActivity extends AppCompatActivity {

    public int resourceID;
    public String title;
    ImageView imageView;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        resourceID = bundle.getInt("rid");
        title = bundle.getString("title");

        setContentView(R.layout.activity_house_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setTitle(title);

        imageView = (ImageView) findViewById(R.id.collapsingImage);
        imageView.setImageResource(resourceID);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText(title + " content here! :)");


        /*switch (resourceID){
            case R.drawable.s1:

                break;

            case R.drawable.s2:
                break;

            case R.drawable.s3:
                break;

            case R.drawable.s4:
                break;

            case R.drawable.k1:
                break;

            case R.drawable.k2:
                break;

            case R.drawable.k3:
                break;

            case R.drawable.f1:
                break;

            case R.drawable.f2:
                break;

            case R.drawable.f3:
                break;

            case R.drawable.f4:
                break;

            case R.drawable.f5:
                break;

            case R.drawable.f6:
                break;

            case R.drawable.c1:
                break;

            case R.drawable.c2:
                break;

            case R.drawable.c3:
                break;

            case R.drawable.c4:
                break;

            case R.drawable.c5:
                break;

            case R.drawable.c6:
                break;
        }
        */

    }
}
