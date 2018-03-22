package siteurl.in.admin_loyalty;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import siteurl.in.admin_loyalty.Adaptors.MyVendorAdapter;
import siteurl.in.admin_loyalty.Objects.Venders_Product;

public class IndividialVendorOffer extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, sessionid, uid, login_identity, vendorIndividialId;
    private Toolbar mToolbar;
    private TextView toolbartitle;
    MyVendorAdapter vendorProddataAdapter;
    ArrayList<Venders_Product> vendorProdarrayalllist = new ArrayList<Venders_Product>();
    ListView listView;
    ArrayList<JSONObject> rawvendorlist = new ArrayList<>();
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individial_vendor_offer);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //getting data from Login Activity
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        login_identity = loginpref.getString("loginname", null);
        editor = loginpref.edit();

        //UI elements to get ID
        listView = (ListView) findViewById(R.id.listView2);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbartitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText("Vendor offer");
        vendorIndividialId = getIntent().getStringExtra("individialvenderid");
        if (!vendorIndividialId.isEmpty()) {
            getIndividualVendorOfferData();
        }
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

    public void getIndividualVendorOfferData() {

        StringRequest vendorOffers = new StringRequest(Request.Method.POST, APIBaseURL.vendoroffers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {

                            jsonObject = new JSONObject(response);
                            String response_error = jsonObject.getString("Error");
                            if (response_error.equals("false")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if (data.length() > 0) {
                                    rawvendorlist.clear();
                                    vendorProdarrayalllist.clear();

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject data_object = data.getJSONObject(i);
                                        String offer_id = data_object.getString("offer_id");
                                        String vendor_id = data_object.getString("vendor_id");
                                        String offer_name = data_object.getString("offer_name");
                                        String offer_description = data_object.getString("offer_description");
                                        String offer_image = data_object.getString("offer_image");
                                        String offer_price = data_object.getString("offer_price");
                                        String updated_at = data_object.getString("updated_at");
                                        String expiry_date = data_object.getString("expiry_date");
                                        String terms_and_condtion = data_object.getString("terms_and_condtion");
                                        String status = data_object.getString("status");

                                        rawvendorlist.add(data_object);
                                        vendorProdarrayalllist.add(new Venders_Product(offer_id, vendor_id, offer_name,
                                                offer_description, offer_image, updated_at, expiry_date, offer_price,
                                                terms_and_condtion, status));
                                    }

                                    vendorProddataAdapter = new MyVendorAdapter(getApplicationContext(), R.layout.allvendorproduct_list, vendorProdarrayalllist);
                                    listView.setAdapter(vendorProddataAdapter);

                                    //enables filtering for the contents of the given ListView
                                    listView.setTextFilterEnabled(true);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        }
                                    });

                                    EditText myFilter = (EditText) findViewById(R.id.myFilter);
                                    myFilter.addTextChangedListener(new TextWatcher() {

                                        public void afterTextChanged(Editable s) {
                                        }

                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            vendorProddataAdapter.getFilter().filter(s.toString());
                                        }
                                    });
                                } else {
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(IndividialVendorOffer.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(IndividialVendorOffer.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(IndividialVendorOffer.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(IndividialVendorOffer.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(IndividialVendorOffer.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(IndividialVendorOffer.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(IndividialVendorOffer.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("vendor_id", vendorIndividialId);
                params.put("sid", sessionid);
                params.put("api_key", APIBaseURL.APIKEY);
                return params;
            }
        };

        vendorOffers.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(vendorOffers);
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
