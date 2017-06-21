package com.balamurugan.altien;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment  implements PlaceSelectionListener {

    private static final int REQUEST_CITY_PLACE = 1000;
    int filter;
    Button triggerButton;
    AutocompleteFilter typeFilter;
    View rootView;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

    public CityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       if(rootView == null) {
           rootView = inflater.inflate(R.layout.fragment_city, container, false);
           triggerButton = (Button) rootView.findViewById(R.id.buttonCity);
       }else {
           MainActivity.current = MainActivity.CITY;
       }

        filter = AutocompleteFilter.TYPE_FILTER_CITIES;



        triggerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(filter)
                            .build();

                    Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .setBoundsBias(BOUNDS_INDIA)
                            .build(getActivity());
                    startActivityForResult(intent, REQUEST_CITY_PLACE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });



        return rootView;
    }





    @Override
    public void onPlaceSelected(Place place) {
        //  Log.i(LOG_TAG, "Place Selected: " + place.getName());
        triggerButton.setText(place.getName());
        MainActivity.citydone = true;

        SharedPreferences locSave = getContext().getSharedPreferences("city", 0);
        SharedPreferences.Editor editor = locSave.edit();
        editor.putLong("latitude",Double.doubleToLongBits(place.getLatLng().latitude));
        editor.putLong("longitude",Double.doubleToLongBits(place.getLatLng().longitude));
        editor.putString("name",place.getName().toString());
        editor.commit();
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
        Toast.makeText(getContext(), "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CITY_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroyView() {
        if (rootView.getParent() != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
        super.onDestroyView();
    }


}
