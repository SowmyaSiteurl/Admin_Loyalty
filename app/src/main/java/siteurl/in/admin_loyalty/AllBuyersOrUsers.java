package siteurl.in.admin_loyalty;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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

import dmax.dialog.SpotsDialog;
import siteurl.in.admin_loyalty.Adaptors.MyBuyerAdapter;
import siteurl.in.admin_loyalty.Adaptors.MyCustomAdapter;
import siteurl.in.admin_loyalty.Objects.Buyers;
import siteurl.in.admin_loyalty.Objects.Vendors;

public class AllBuyersOrUsers extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, sessionid, uid, login_identity, vnderuser_id, hash;
    private Toolbar mToolbar;
    private TextView toolbartitle;
    MyBuyerAdapter dataAdapter;
    ArrayList<Buyers> buyerarrayalllist = new ArrayList<Buyers>();
    ArrayList<String> stringsbuyer = new ArrayList<>();
    ArrayList<String> hashofbuyer = new ArrayList<>();
    ListView listView;
    ArrayList<JSONObject> rawbuyerlist = new ArrayList<>();
    Dialog alertDialog;

    private SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_buyer_or_user);
        alertDialog = new Dialog(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //getting data from login Activity
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        login_identity = loginpref.getString("loginname", null);
        editor = loginpref.edit();
        listView = (ListView) findViewById(R.id.listView2);

        //UI elements to get the ID
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new SpotsDialog(AllBuyersOrUsers.this, R.style.Custom);
        dialog.dismiss();


        toolbartitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText("All Buyer/Users");
        checkConnection();
        dialog.show();
        displayListView();
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


    //menuItem
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home_logout) {
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
            logout();
        }
        if (id == R.id.home) {
            startActivity(new Intent(AllBuyersOrUsers.this, MainActivity.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void displayListView() {

        StringRequest allUsers = new StringRequest(Request.Method.POST, APIBaseURL.allbuyers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject responsefromserver = null;

                        dialog.dismiss();
                        try {
                            responsefromserver = new JSONObject(response);
                            String response_error = responsefromserver.getString("Error");
                            if (response_error.equals("false")) {
                                JSONArray data = responsefromserver.getJSONArray("data");
                                if (data.length() > 0) {
                                    rawbuyerlist.clear();
                                    buyerarrayalllist.clear();
                                    for (int i = 0; i < data.length(); i++) {

                                        JSONObject data_object = data.getJSONObject(i);
                                        vnderuser_id = data_object.getString("user_id");
                                        String name = data_object.getString("name");
                                        String email = data_object.getString("email");
                                        String phone = data_object.getString("phone");
                                        String address = data_object.getString("address");
                                        hash = data_object.getString("hash");

                                        stringsbuyer.add(vnderuser_id);
                                        hashofbuyer.add(hash);
                                        rawbuyerlist.add(data_object);
                                        buyerarrayalllist.add(new Buyers(vnderuser_id, name, email, phone, address, hash));
                                    }

                                    dataAdapter = new MyBuyerAdapter(getApplicationContext(), R.layout.country_info, buyerarrayalllist);
                                    listView.setAdapter(dataAdapter);

                                    //enables filtering for the contents of the given ListView
                                    listView.setTextFilterEnabled(true);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            Intent callnow = new Intent(AllBuyersOrUsers.this, AllTransactionUser.class);
                                            callnow.putExtra("individialBuyerid", String.valueOf(stringsbuyer.get(i)));
                                            callnow.putExtra("BuyerName", String.valueOf(buyerarrayalllist.get(i).getName()));
                                            callnow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(callnow);
                                        }
                                    });

                                    //Enabling search filter
                                    SearchView searchView = (SearchView) findViewById(R.id.buyersearch);
                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String s) {
                                            dataAdapter.getFilter().filter(s.toString());
                                            return true;
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

                dialog.dismiss();

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(AllBuyersOrUsers.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(AllBuyersOrUsers.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(AllBuyersOrUsers.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(AllBuyersOrUsers.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(AllBuyersOrUsers.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(AllBuyersOrUsers.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AllBuyersOrUsers.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                params.put("user_id", uid);
                params.put("sid", sessionid);
                params.put("api_key", APIBaseURL.APIKEY);
                return params;
            }

        };
        allUsers.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(allUsers);
    }

    //logout method
    public void logout() {

        StringRequest logout = new StringRequest(Request.Method.POST, APIBaseURL.logout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject objectlogout = new JSONObject(response);
                    String response_error = objectlogout.getString("Error");
                    if (response_error.equals("false")) {

                        String response_message = objectlogout.getString("Message");
                        Toast.makeText(AllBuyersOrUsers.this, response_message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AllBuyersOrUsers.this, Admin_Login.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                        editor.clear();
                        editor.commit();
                    } else {
                        String response_message = objectlogout.getString("Message");
                        Toast.makeText(AllBuyersOrUsers.this, response_message, Toast.LENGTH_SHORT).show();
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
                    // Toast.makeText(AllBuyersOrUsers.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(AllBuyersOrUsers.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(AllBuyersOrUsers.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(AllBuyersOrUsers.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(AllBuyersOrUsers.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(AllBuyersOrUsers.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AllBuyersOrUsers.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
