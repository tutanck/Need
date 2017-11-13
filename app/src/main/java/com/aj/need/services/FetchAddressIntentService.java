package com.aj.need.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.aj.need.tools.utils.Coord;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Asynchronously handles an intent using a worker thread.
 * Receives a ResultReceiver object and a location through an intent.
 * Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 * <p>
 * // Errors could still arise from using the Geocoder (for example, if there is no
 * // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
 * // simply not have an address for a location. In all these cases, we communicate with the
 * // receiver using a resultCode indicating failure. If an address is found, we use a
 * // resultCode indicating success.
 * <p>
 * // The Geocoder used in this sample. The Geocoder's responses are localized for the given
 * // Locale, which represents a specific geographical or linguistic region. Locales are used
 * // to alter the presentation of information such as numbers or dates to suit the conventions
 * // in the region they describe.
 * <p>
 * // Using getFromLocation() returns an array of Addresses for the area immediately
 * // surrounding the given latitude and longitude. The results are a best guess and are
 * // not guaranteed to be accurate.
 * <p>
 * // Fetch the address lines using {@code getAddressLine},
 * // join them, and send them to the thread. The {@_link android.location.address}
 * // class provides other options for fetching address details that you may prefer
 * // to use. Here are some examples:
 * // getLocality() ("Mountain View", for example)
 * // getAdminArea() ("CA", for example)
 * // getPostalCode() ("94043", for example)
 * // getCountryCode() ("US", for example)
 * // getCountryName() ("United States", for example)
 **/
public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FetchAddressIS";

    public static final String RECEIVER = "RECEIVER";
    public static final String RESULT_DATA_KEY = "RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = "LOCATION_DATA_EXTRA";

    public static final int ADDRESS_FOUND = 1;
    public static final int NO_ADDRESS_FOUND = 0;
    public static final int SERVICE_NOT_AVAILABLE = -1;
    public static final int INVALID_LAT_LNG = -2;
    public static final int NO_LOCATION_DATA_PROVIDED = -3;


    private ResultReceiver mReceiver;


    public FetchAddressIntentService() {
        super(TAG); // Use the TAG to name the worker thread.
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        mReceiver = intent.getParcelableExtra(RECEIVER);

        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver. There is nowhere to send the results.");
            return;
        }

        Coord location = (Coord) intent.getSerializableExtra(LOCATION_DATA_EXTRA);

        if (location == null) {
            deliverResultToReceiver(NO_LOCATION_DATA_PROVIDED, null);
            return;
        }


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation
                    (location.getLatitude(), location.getLongitude(), 1/*nb adr*/);
        } catch (IOException ioe) {// Catch network or other I/O problems.
            deliverResultToReceiver(SERVICE_NOT_AVAILABLE, null);
            ioe.printStackTrace();
            return;
        } catch (IllegalArgumentException iae) {// Catch invalid latitude or longitude values.
            deliverResultToReceiver(INVALID_LAT_LNG, null);
            iae.printStackTrace();
            return;
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            deliverResultToReceiver(NO_ADDRESS_FOUND, null);
            return;
        }

        deliverResultToReceiver(ADDRESS_FOUND, addresses.get(0));
    }


    private void deliverResultToReceiver(int resultCode, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_DATA_KEY, address);
        mReceiver.send(resultCode, bundle);
    }

}
