package com.balamurugan.altien;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener, PlaceSelectionListener {

    private GoogleMap mMap;

    private Location mLastLocation;


    SlidingUpPanelLayout slidingUpPanelLayout;


    //boolean polyDone = false;


    //PolygonOptions rectOptions = new PolygonOptions();

    PolylineOptions polylineOptions = new PolylineOptions();


    Context context;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CoordinatesManager";

    // Contacts table name
    private static String TABLE_COORDINATES = "coordinates";

    int height;


    TextView price;
    TextView type;
    TextView id;
    TextView date;

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_PRICE = "price";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATE = "date";

    Projection projection;
    public double latitude;
    public double longitude;


    boolean filteredList = false;

    Polyline polyline;

    static boolean skippedSecond = false;


    class House{
        public String id;
        public Double lat;
        public Double lon;
        public Double price;
        public Double members;
        public String type;
        public String date;

        public  House(){
            id = null;
            lat = lon = null;
            price = null;
            members = null;
            type = null;
            date = null;
        }
    }

    House db = new House();
    DatabaseHandler dbase;


    List<House> filtered = new ArrayList<>();


    HashMap<Marker, Integer> markerMap = new HashMap<Marker, Integer>();

    HashMap<Marker, Integer> filteredMap = new HashMap<Marker, Integer>();


    private static LatLngBounds myCity;
    LatLng cityLatLng;
    String cityName;

    LatLng localityLatLng;
    String localityName;



    private static int selectedNavItemId = R.id.mapActivity;
    public int selectedTemp;


    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;

    FrameLayout fram_map ;
    Button btn_draw_State;
    static boolean Is_MAP_Moveable = false;

   // List<LatLng> val = ;

    ArrayList<LatLng> val = new ArrayList<>();


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    ArrayList<MarkerOptions> markerList = new ArrayList<>();
    boolean marked = false;
    boolean isLoading = false;
    Place mPlace;
    Marker mMarker;

    ImageButton myLoc;
    ImageButton myHam;

    Toolbar toolbar;

    Context mContext;

    OutputStream outputStream;
    InputStream inputStream;


    Double mylat;
    Double mylng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = this;

        dbase = new DatabaseHandler();

        SharedPreferences locSave = getSharedPreferences("city", 0);
        Double magic = 25 * Math.sqrt(2) / 111.111;

        cityLatLng = new LatLng(Double.longBitsToDouble(locSave.getLong("latitude", 0)), Double.longBitsToDouble(locSave.getLong("longitude", 0)));
        LatLng sw =  new LatLng(cityLatLng.latitude - magic, cityLatLng.longitude - (magic / Math.cos(cityLatLng.latitude)));
        LatLng ne = new LatLng(cityLatLng.latitude + magic, cityLatLng.longitude + (magic / Math.cos(cityLatLng.latitude)));

        id = (TextView) findViewById(R.id.tv);
        price = (TextView) findViewById(R.id.price);
        type = (TextView) findViewById(R.id.type);
        date = (TextView) findViewById(R.id.date);

        cityName = locSave.getString("name",null);

        if(!TABLE_COORDINATES.contains(cityName))
               TABLE_COORDINATES += cityName;

        myCity = new LatLngBounds(sw,ne);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("woohoooo Name");
      /*  setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar(); */


        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        myLoc = (ImageButton) findViewById(R.id.myMapLocationButton);
        myHam = (ImageButton) findViewById(R.id.hamButton);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        NestedScrollableViewHelper helper = new NestedScrollableViewHelper();
        slidingUpPanelLayout.setScrollableViewHelper(helper);



        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( mLastLocation != null) {
                    LatLng home = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(home).title("My location"));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(home)
                            .zoom(12).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                else {
                 // show error retieving location
                    curLocError();
                }
            }
        });

        myHam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        setUpNavView();


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setBoundsBias(myCity);


        fram_map = (FrameLayout) findViewById(R.id.fram_map);
        Button btn_draw_State = (Button) findViewById(R.id.btn_draw_State);


        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Is_MAP_Moveable = false;
                    showSnackBar("Loading.....");
                    fram_map.setVisibility(View.GONE);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    selectedNavItemId = R.id.mapActivity;
                    navigationView.setCheckedItem(selectedNavItemId);
                    mMap.clear();
                    filterPolygon();

            }
        });


        if(!skippedSecond){

            SharedPreferences locOpen = getSharedPreferences("locality", 0);

            localityLatLng = new LatLng(Double.longBitsToDouble(locOpen.getLong("latitude2", 0)), Double.longBitsToDouble(locOpen.getLong("longitude2", 0)));
            localityName = locOpen.getString("name",null);
        }


        fram_map.setOnTouchListener(new View.OnTouchListener() {

            @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            int x_co = Math.round(x);
            int y_co = Math.round(y);

            projection = mMap.getProjection();
            Point x_y_points = new Point(x_co, y_co);

            LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
            latitude = latLng.latitude;

            longitude = latLng.longitude;

            int eventaction = event.getAction();
            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    // finger touches the screen
                    val.add(new LatLng(latitude, longitude));
                    Log.i("mapsActivity", "ACTION DOWN ");

                case MotionEvent.ACTION_MOVE:
                    // finger moves on the screen
                    Log.i("mapsActivity", "ACTION MOVEEEEE ");
                    val.add(new LatLng(latitude, longitude));
                    //break;

                case MotionEvent.ACTION_UP:
                    // finger leaves the screen
                /*    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                    alert.setTitle("Done drawing polygon?");
                    alert.setMessage("Add this location to bookmarks by giving it a name.");

// Set an EditText view to get user input

                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Draw_Map();
                            //  AddPackage task = new AddPackage();

                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show(); */
                    Draw_Map();
                    Log.i("mapsActivity", "ACTION UUUUPPPPPP ");
                    break;
            }

            if (Is_MAP_Moveable == true) {
                return true;

            } else {
                return true;
            }
        }
        });


        Button btn_show_all = (Button) findViewById(R.id.btn_show_all);

        btn_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                showSnackBar("Loading.....");
                GetData task = new GetData();
                task.purl = "http://abhishekvasudevan.com/house.php";
                task.execute();
            }
        });






    }



    public void Draw_Map() {
     /*   rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(Color.BLUE);
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(Color.CYAN);
        polygon = mMap.addPolygon(rectOptions);*/

        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(val);
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(7);
        polyline = mMap.addPolyline(polylineOptions);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpMap();

        // Add a marker in Sydney and move the camera
      //  LatLng sydney = new LatLng(-34, 151);
      //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraPosition cameraPosition = null;
        if(skippedSecond) {
            if (cityName != null)
                mMap.addMarker(new MarkerOptions().position(cityLatLng).title(cityName));
            else
                mMap.addMarker(new MarkerOptions().position(cityLatLng).title("My city"));

            cameraPosition = new CameraPosition.Builder().target(cityLatLng)
                    .zoom(12).build();
        }
        else {
            mMap.addMarker(new MarkerOptions().position(localityLatLng).title(localityName));

            cameraPosition = new CameraPosition.Builder().target(localityLatLng)
                    .zoom(12).build();

            showSnackBar("Loading...");

            filterLocality(localityLatLng);
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    @Override
    public void onPlaceSelected(Place place) {
        //  Log.i(LOG_TAG, "Place Selected: " + place.getName());
       // triggerButton.setText(place.getName());
      //  MainActivity.locdone = true;
        SharedPreferences locSave2 = getSharedPreferences("locality", 0);
        SharedPreferences.Editor editor = locSave2.edit();
        LatLng temp = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        editor.putLong("latitude2",Double.doubleToLongBits(place.getLatLng().latitude));
        editor.putLong("longitude2",Double.doubleToLongBits(place.getLatLng().longitude));
        editor.commit();


        mMap.addMarker(new MarkerOptions().position(temp).title(place.getName().toString()));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(temp)
                .zoom(12).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        filterLocality(temp);

      /*  locationTextView.setText(getString(R.string.formatted_place_data, place
                .getName(), place.getAddress(), place.getPhoneNumber(), place
                .getWebsiteUri(), place.getRating(), place.getId()));
        if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }*/

    }

    @Override
    public void onError(Status status) {
        //   Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }








    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true); // true to enable

        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.getUiSettings().setCompassEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.getUiSettings().setAllGesturesEnabled(true);

        //Marker marker_latlng = null; // MAKE THIS WHATEVER YOU WANT

    /*    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(17.385044, 78.486671)).zoom(15.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.moveCamera(cameraUpdate); */

        mMap.getUiSettings().setRotateGesturesEnabled(true);


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

       /* CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(17.385044, 78.486671)).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); */
        mMap.setMyLocationEnabled(true); // false to disable

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (markerMap != null) {

                    int i = 0;
                  //  slidingUpPanelLayout.setPanelHeight(60);
                    try{
                        i = markerMap.get(marker);
                        House hh = filtered.get(i);
                        id.setText(hh.id);
                        price.setText(hh.price.toString());
                        type.setText(hh.type);
                        date.setText(hh.date);
                        toolbar.setTitle(hh.id);
                        slidingUpPanelLayout.setPanelHeight(height/2);
                    }catch (Exception e){
                        e.printStackTrace();
                    }




                }
                return true;


            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                //mMap.clear();
                if(mMarker != null){
                    mMarker.remove();
                }



                if(skippedSecond) {
                    mMarker = mMap.addMarker(new MarkerOptions().position(point).title("Locality"));

                    filterLocality(point);
                }



                // fetchAltitudeFromService task = new fetchAltitudeFromService();
                //    AsyncTaskCompat.executeParallel(task,null);

             /*   SharedPreferences locSave = mContext.getSharedPreferences("loc", 0);
                SharedPreferences.Editor editor = locSave.edit();
                editor.putLong("latitude",Double.doubleToLongBits(mylat));
                editor.putLong("longitude",Double.doubleToLongBits(mylng));
                editor.commit();



                Toast.makeText(mContext, "Getting altitude....", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Add to bookmarks?");
                alert.setMessage("Add this location to bookmarks by giving it a name.");

// Set an EditText view to get user input
                final EditText input = new EditText(mContext);
                alert.setView(input);

                alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String locn = input.getText().toString();

                        //  AddPackage task = new AddPackage();

                        if(!locn.isEmpty()) {
                            //      task.locname = locn;
                            //      AsyncTaskCompat.executeParallel(task,null);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Name can't be empty - skipping", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show(); */

            }
        });
    }


    void updateMarket(MarkerOptions mymark){

        mMap.addMarker(mymark);

    }




    void writeToFile(String lname){

        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = null;
            inputStream = openFileInput("LocList.xml");
            doc = documentBuilder.parse(inputStream);
            Element myappTag = doc.getDocumentElement();
           /* File myFile = new File(FilePath);
            if(myFile.exists()) {
                if (myFile.canRead()) {
                    if(myFile.canWrite()) {
                       // doc = documentBuilder.parse(new File(getCacheDir() + "my.xml"));
                        doc = documentBuilder.parse(myFile);
                    }
                }
            } */
            Element elem = doc.createElement("location");

            elem.setAttribute("name",lname);
            elem.setAttribute("lat", Double.toString(mylat));
            elem.setAttribute("lng", Double.toString(mylng));

            myappTag.appendChild(elem);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            outputStream = openFileOutput("LocList.xml", MODE_WORLD_READABLE);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }



    public void getData(String purl) {




        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            markerList.clear();

            URL url = new URL(purl);

            // URL url = new URL("http://skiplagged.com/api/pokemon.php?address=central%20park,%20new%20york,%20ny");

            //System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            //  Log.e(LOG_TAG, "Error-PlacesAPI URL", e);
            marked = false;
        } catch (IOException e) {
            //  Log.e(LOG_TAG, "Er-conn-toPlaces API", e);
            marked = false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
 //           JSONObject jsonObj = new JSONObject(jsonResults.toString());

            int pokeId;
            //Bitmap pokePhoto;



            String pokeName = null;

            //          List<HashMap<String, Double>> list = new ArrayList<HashMap<String,Double>>();

            boolean isAvail = true;

            House temp = new House();

            JSONArray pokeList = new JSONArray(jsonResults.toString());

            if(pokeList != null) {
                dbase.clearAlready();
                filtered.clear();


                for (int i = 0; i < pokeList.length(); i++) {
                    try {

                        JSONObject c = pokeList.getJSONObject(i);
                        //    maplat = (Double) c.get("latitude");
                        //    maplng = (Double) c.get("longitude");
                        temp.id = (String) c.get("id").toString();
                        temp.lat = Double.parseDouble(c.get("lat").toString());
                        temp.lon = Double.parseDouble(c.get("lon").toString());
                        //   pokePhoto = getBitmapFromURL(POKE_IMG + "/" + pokeId + ".png");
                        //  pokeName = PokeIndex.getName(String.valueOf(pokeId));
                        temp.price = Double.parseDouble(c.get("price").toString());
                        temp.members = Double.parseDouble(c.get("members").toString());
                        temp.type = (String) c.get("type");
                        temp.date = (String) c.get("date");
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(temp.lat, temp.lon));
                        //.icon(BitmapDescriptorFactory.fromResource(getResourceID("poke_" + pokeId, "drawable", getApplicationContext())))
                 //       updateMarket(marker);
                        markerList.add(marker);
                        filtered.add(temp);

                     //   markerMap.put(marker.,temp);



                        dbase.addCoordinates(temp);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        marked = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                        marked = false;
                    }

                }
            }



//            list.add(hm);
        } catch (JSONException e) {
            Log.e("JSOOOON", "JSON results error", e);
            e.printStackTrace();
            showSnackBar("Error retrieving data...");
            marked = false;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

           // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        mLastLocation = location;

       // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }



    protected void setUpNavView()
    {
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(selectedNavItemId);

        selectedTemp = selectedNavItemId;
    }


    /**
     * Helper method to allow child classes to opt-out of having the
     * hamburger menu.
     * @return
     */



    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {



        // currentItem = menuItem;

        //Checking if the item is in checked state or not, if not make it in checked state
        //  if(currentItem.isChecked()) {
        //       currentItem.setChecked(false);
        //       drawerLayout.closeDrawers();
        //        return true;
        //    }
        //    else currentItem.setChecked(true);

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        return onNavItemSelected(menuItem);


    }


    public boolean onNavItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        Intent intent;
        //  currentFrag = menuItem.getItemId();
        selectedTemp = menuItem.getItemId();

        if(selectedTemp == selectedNavItemId){
            return true;
        }

        selectedNavItemId = selectedTemp;

       switch (selectedTemp){

           case R.id.setBoundaries:

               enableDrawing();
               break;

           case R.id.Options:

               AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

               alert.setTitle("Altien");
               alert.setMessage("Created for Shaastra 2017 \n By \n \tBalamurugan M – ID: SHA1702267\n" +
                       "\tAbhishek V – ID: SHA1712123\n" +
                       "\tSrinath P – ID: SHA1711600\n");

// Set an EditText view to get user input

               alert.setPositiveButton("Cool!", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int whichButton) {
                       //  AddPackage task = new AddPackage();

                   }
               });

               alert.show();

               break;




           default:
                break;
        }




        //Check to see which item was being clicked and perform appropriate action
 /*       switch (selectedTemp){

            //Replacing the main content with ContentFragment Which is our Inbox View;
            case R.id.mapActivity:
             //   intent = new Intent(this, LoggedIn.class);
             //   intent.setFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
             //   startActivity(intent); finish();
                break;

            default:
                Toast.makeText(getApplicationContext(),"Something is Wrong",Toast.LENGTH_SHORT).show();
                break;
        } */
        return true;
        //return super.onOptionsItemSelected(menuItem);
    }

    public void curLocError(){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Couldn't retrieve current location...", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    public void showSnackBar(String s){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, s, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }




    void enableDrawing(){

        fram_map.setVisibility(View.VISIBLE);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Drawing on map has been enabled!", Snackbar.LENGTH_LONG);
        snackbar.show();

        val.clear();



    }



    public class DatabaseHandler extends SQLiteOpenHelper {


        public DatabaseHandler() {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_COORDINATES + "("
                    + KEY_ID + " TEXT," + KEY_LAT + " NUMBER,"
                    + KEY_LON + " NUMBER," + KEY_MEMBERS + " NUMBER," + KEY_PRICE + " NUMBER," + KEY_TYPE + " TEXT," + KEY_DATE + " TEXT" + ")";
            db.execSQL(CREATE_CONTACTS_TABLE);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDINATES);
            // Create tables again
            onCreate(db);
        }
        // Adding new contact
        void addCoordinates(House co) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ID, co.id); // House code
            values.put(KEY_LAT, co.lat); // House Name
            values.put(KEY_LON, co.lon); // House sem
            values.put(KEY_PRICE, co.price); // House credits
            values.put(KEY_MEMBERS, co.members);
            values.put(KEY_TYPE, co.type);
            values.put(KEY_DATE, co.date);


            // Inserting Row
            db.insert(TABLE_COORDINATES, null, values);
            db.close(); // Closing database connection
        }

        public int  getSems(){

            int sems = -1;
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT COUNT(*) " + "FROM " + TABLE_COORDINATES;
            Cursor cursor = db.rawQuery(query, null);;
            if( cursor != null && cursor.moveToFirst() ){
                sems = Integer.parseInt(cursor.getString(0));
                cursor.close();
            }
            if(sems == -1 ){
                return  0;
            }
            return sems;
        }

        public List<House> getHouses(){

            List<House> coList = new ArrayList<House>();

            SQLiteDatabase db = this.getWritableDatabase();
            //      String query = "SELECT * " +  "FROM " + TABLE_COORDINATES + " WHERE (" + KEY_SEM + " = '" + sems + "')" ;
        //    Cursor cursor = db.query(TABLE_COORDINATES,null,KEY_SEM + "=?",new String[]{ sems },null,null,null);
            Cursor cursor = db.query(TABLE_COORDINATES,null,null,null,null,null,null);
            //    cursor.moveToFirst();
            while (cursor.moveToNext() && cursor != null ){
                House co = new House();
                co.id = cursor.getString(cursor.getColumnIndex(KEY_ID));
                co.lat = cursor.getDouble(cursor.getColumnIndex(KEY_LAT));
                co.lon = cursor.getDouble(cursor.getColumnIndex(KEY_LON));
                co.type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
                co.members = cursor.getDouble(cursor.getColumnIndex(KEY_MEMBERS));
                co.price = cursor.getDouble(cursor.getColumnIndex(KEY_PRICE));
                co.date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                coList.add(co);
            }
            cursor.close();
            return coList;
        }

        public void clearAlready(){
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL("DELETE FROM " + TABLE_COORDINATES);
            }catch (SQLiteException e){
                e.printStackTrace();
            }

        }
    }


    void filterPolygon(){

        List<House> temp = new ArrayList<>();

        showSnackBar("Filtering....");



        temp = dbase.getHouses();



        if(temp != null){

            filtered.clear();


            Double trunc1, trunc2;


            for(House hs : temp) {

                ArrayList<LatLng> ps = new ArrayList<>();
                int count = 0;

                trunc2 = BigDecimal.valueOf(hs.lon)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

                for (LatLng ll : val) {

                   // Double toBeTruncated = new Double("3.5789055");

                    trunc1 = BigDecimal.valueOf(ll.longitude)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();



                    if (trunc1.equals(trunc2)) {
                        ps.add(ll);
                        count++;
                    }
                }

                if (count > 1) {

                    for (int i = 0; i < count - 1; i++)
                        if ((ps.get(i).latitude < hs.lat && ps.get(i+1).latitude > hs.lat) || (ps.get(i).latitude > hs.lat && ps.get(i+1).latitude < hs.lat))
                            filtered.add(hs);

                }
            }

        }


        if(!filtered.isEmpty()){

                    mMap.clear();

                    int i=0;
                    filteredList = true;
                    markerMap.clear();
                    MarkerOptions marker = null;
                    for(House hs1 : filtered ){
                        marker = new MarkerOptions().position(new LatLng(hs1.lat, hs1.lon));
                        markerMap.put(mMap.addMarker(marker),i);
                        i++;
                       // updateMarket(marker);
                    }
                    showSnackBar("Map is updated!");
                }
                else{
                    showSnackBar("No results found. Click 'Show all'...");

                    GetData task = new GetData();
                    task.purl = "http://abhishekvasudevan.com/house.php";
                    task.execute();

                }
        val.clear();

    }




    private class GetData extends AsyncTask<Void, Void,Void> {

        public String purl;
        public LatLng waitPoint = null;



        @Override
        protected Void doInBackground(Void... params) {

            getData(purl);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set downloaded image into ImageView
            // ImageView logoimg = (ImageView) findViewById(R.id.logo);
            // logoimg.setImageBitmap(bitmap);
            //    mProgressDialog.dismiss();
            //   TextView tv = (TextView)findViewById(R.id.infotext);
            // tv.setText(Arrays.toString(ward).replaceAll("\\[|\\]", ""));



            if(waitPoint != null){
                filterLocality(waitPoint);
            }



            else if(markerList != null){
                int i=0;
                filteredList = false;
                markerMap.clear();

                try {


                    for (MarkerOptions mymark : markerList) {
                        markerMap.put(mMap.addMarker(mymark), i);
                        i++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

    }


    void filterLocality(LatLng point){

        List<House> temp = new ArrayList<>();

        showSnackBar("Filtering....");



        temp = dbase.getHouses();


        if(temp.isEmpty()){

            showSnackBar("Fetching data...");

            GetData task = new GetData();
            task.purl = "http://abhishekvasudevan.com/house.php";
            task.waitPoint = point;
            task.execute();

        }



        if(temp != null){

            filtered.clear();

            float[] distance = new float[2];
            //int count = 0;

            for(House hs : temp) {

                Location.distanceBetween( hs.lat, hs.lon,
                        point.latitude, point.longitude, distance);

                if( distance[0] <= 3000  ) {

                    filtered.add(hs);
                }
            }

        }


        if(!filtered.isEmpty()){

            mMap.clear();
            int i=0;
            markerMap.clear();
            filteredList = true;
            MarkerOptions marker = null;
            for(House hs1 : filtered ){
                marker = new MarkerOptions().position(new LatLng(hs1.lat, hs1.lon));
                markerMap.put(mMap.addMarker(marker),i);
                i++;
            }
            showSnackBar("Map is updated!");

        }
        else{
            showSnackBar("No results found. Loading all data...");

            GetData task = new GetData();
            task.purl = "http://abhishekvasudevan.com/house.php";
            task.execute();

        }


    }


    @Override
    public void onBackPressed() {
        if(slidingUpPanelLayout.getPanelHeight() > 0){
            slidingUpPanelLayout.setPanelHeight(0);
            return;
        }
        if(slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)){
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        super.onBackPressed();
    }


    public class NestedScrollableViewHelper extends ScrollableViewHelper {
        public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
            if (scrollableView instanceof NestedScrollView) {
                if(isSlidingUp){
                    return scrollableView.getScrollY();
                } else {
                    NestedScrollView nsv = ((NestedScrollView) scrollableView);
                    View child = nsv.getChildAt(0);
                    return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
                }
            } else {
                return 0;
            }
        }
    }

}
