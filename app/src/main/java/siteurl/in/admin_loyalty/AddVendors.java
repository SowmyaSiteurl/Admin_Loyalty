package siteurl.in.admin_loyalty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.LOCATION_HARDWARE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddVendors extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String userid_category_id, sessionid, uid;
    private Toolbar mToolbar;
    private TextView toolbartitle;
    EditText etuname, etemail, etphone, etadress, etconversion;
    String str_email, str_uname, str_phone, str_address, str_conversion;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final int RequestPermissionCode = 1;
    LatLng p1 = null;
    TextInputLayout nametextInputLayout, emailtextInputLayout, phonetextInputLayout, addresstextInputLayout, conversiontextInputLayout;
    private SpotsDialog dialog;
    private static final String NAME = "[a-zA-Z.? ]*";
    Dialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vendors);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //getting data from login activity
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        userid_category_id = loginpref.getString("user_group_id", null);
        editor = loginpref.edit();


        checkConnection();
        //UI elements to get the ID
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbartitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText("Add vendors");

        etemail = (EditText) findViewById(R.id.et_email);
        etuname = (EditText) findViewById(R.id.et_uname);
        etadress = (EditText) findViewById(R.id.et_address);
        etphone = (EditText) findViewById(R.id.et_phone);
        etconversion = (EditText) findViewById(R.id.et_conversion);
        nametextInputLayout = findViewById(R.id.nametilAdmin);
        emailtextInputLayout = findViewById(R.id.emailtilAdmin);
        phonetextInputLayout = findViewById(R.id.phonetilAdmin);
        addresstextInputLayout = findViewById(R.id.addresstilAdmin);
        conversiontextInputLayout = findViewById(R.id.conversiontilAdmin);

        dialog = new SpotsDialog(AddVendors.this, R.style.Custom);
        dialog.dismiss();

    }


    //this is the method to check internet connection
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }


    private void showDialog(boolean isConnected) {

        if (isConnected) {

            alertDialog.dismiss();

        } else {
            alertDialog.setContentView(R.layout.check_internet);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            Button button = alertDialog.findViewById(R.id.tryAgain);
            Button exit = alertDialog.findViewById(R.id.exit);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.exit(0);

                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    checkConnection();
                }
            });
            alertDialog.show();
        }
    }

    //validations for add vendor
    public void addNewVendor(View view) {

        dialog.show();

        str_email = etemail.getText().toString().trim();
        str_uname = etuname.getText().toString().trim();
        str_phone = etphone.getText().toString().trim();
        str_address = etadress.getText().toString().trim();
        str_conversion = etconversion.getText().toString().trim();

        if (str_uname.equals("") || (str_uname.equals(null))) {
            nametextInputLayout.setError("Enter a valid name");
            dialog.dismiss();
            return;
        } else {
            nametextInputLayout.setError(null);
        }

        if (str_email.equals("") || (str_email.equals(null) || !(str_email.matches(EMAIL_PATTERN)))) {
            emailtextInputLayout.setError("Enter a valid email");
            dialog.dismiss();
            return;
        } else {
            emailtextInputLayout.setError(null);
        }

        if (str_phone.equals("") || (str_phone.equals(null)) || (str_phone.length() != 10)) {
            phonetextInputLayout.setError("Enter a valid phone");
            dialog.dismiss();
            return;
        } else {
            phonetextInputLayout.setError(null);
        }


        if (str_address.equals("") || (str_address.equals(null))) {

            addresstextInputLayout.setError("Enter a valid address");
            dialog.dismiss();
            return;
        } else {

            addresstextInputLayout.setError(null);

        }


        if (str_conversion.equals("") || (str_conversion.equals(null))) {
            conversiontextInputLayout.setError("Enter a valid name");
            dialog.dismiss();
            return;
        } else {
            dialog.show();
            conversiontextInputLayout.setError(null);
            checkConnection();
            addVendor();
        }

    }


    public void firstcheckPermission() {
        if (checkPermission()) {
            // Toast.makeText(getContext(), "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) this, new String[]
                {
                        CAMERA, ACCESS_FINE_LOCATION,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }

    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(this, LOCATION_HARDWARE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(this, LOCATION_SERVICE);


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
        // && FourthPermissionResult ==PackageManager.PERMISSION_GRANTED ;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean Locationpermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean LocationService = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    // boolean ReadContactsPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    // boolean ReadPhoneStatePermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (Locationpermission && LocationService /*&& ReadPhoneStatePermission*/) {//&& ReadContactsPermission && ReadPhoneStatePermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        firstcheckPermission();
        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        Log.d("getloc", String.valueOf(p1));
        if (!String.valueOf(p1.toString()).equals("") || !String.valueOf(p1.toString()).equals(null)) {
            //addvendorcontinue();
            Toast.makeText(context, "test good", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "test fail", Toast.LENGTH_SHORT).show();

        }
        return p1;
    }

    public void addVendor() {

        {
            StringRequest continue_addvendor = new StringRequest(Request.Method.POST, APIBaseURL.addvendor, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject vendorobj = new JSONObject(response);
                        String response_error = vendorobj.getString("Error");
                        if (response_error.equals("false")) {
                            String response_message = vendorobj.getString("Message");
                            etemail.setText("");
                            etuname.setText("");
                            etadress.setText("");
                            etphone.setText("");
                            etconversion.setText("");
                            opendailog(response_message);

                        } else {
                            String response_message = vendorobj.getString("Message");
                            etemail.setText("");
                            etuname.setText("");
                            etadress.setText("");
                            etphone.setText("");
                            etconversion.setText("");
                            opendailog(response_message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    dialog.dismiss();

                    if (error.networkResponse != null) {
                        parseVolleyError(error);
                        return;
                    }
                    if (error instanceof ServerError) {
                        //  Toast.makeText(AddVendors.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(AddVendors.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(AddVendors.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        // Toast.makeText(AddVendors.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(AddVendors.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(AddVendors.this, "No Connection Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(AddVendors.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put("name", str_uname);
                    params.put("email", str_email);
                    params.put("phone", str_phone);
                    params.put("address", str_address);
                    params.put("user_id", uid);
                    params.put("category_id", userid_category_id);
                    params.put("sid", sessionid);
                    params.put("gps_location", "");
                    params.put("api_key", APIBaseURL.APIKEY);
                    params.put("convertion_rate",str_conversion);
                    //12.291568760003747 76.62458807229996
                    return params;
                }
            };
            continue_addvendor.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(continue_addvendor);
        }
    }

    public void opendailog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        startActivity(new Intent(AddVendors.this, Vendor_list.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(AddVendors.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder =
                    new android.app.AlertDialog.Builder(AddVendors.this);
            loginErrorBuilder.setIcon(R.mipmap.ic_launcher);
            loginErrorBuilder.setTitle("Error");
            loginErrorBuilder.setMessage(message);
            loginErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            loginErrorBuilder.show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home_logout) {
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
            logout();
        }
        if (id == R.id.home) {
            startActivity(new Intent(AddVendors.this, MainActivity.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void logout() {

        StringRequest logout = new StringRequest(Request.Method.POST, APIBaseURL.logout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject objectlogout = new JSONObject(response);
                    String response_error = objectlogout.getString("Error");
                    if (response_error.equals("false")) {

                        String response_message = objectlogout.getString("Message");
                        Toast.makeText(AddVendors.this, response_message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddVendors.this, Admin_Login.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                        editor.clear();
                        editor.commit();
                    } else {
                        String response_message = objectlogout.getString("Message");
                        Toast.makeText(AddVendors.this, response_message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                    return;
                }
                if (error instanceof ServerError) {
                    //  Toast.makeText(AddVendors.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AddVendors.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(AddVendors.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    // Toast.makeText(AddVendors.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(AddVendors.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AddVendors.this, "No Connection Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(AddVendors.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionid);
                params.put("api_key", APIBaseURL.APIKEY);

                return params;
            }
        };

        logout.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(logout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
    }
}
