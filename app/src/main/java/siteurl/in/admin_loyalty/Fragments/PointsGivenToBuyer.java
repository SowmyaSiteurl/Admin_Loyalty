package siteurl.in.admin_loyalty.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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

import siteurl.in.admin_loyalty.APIBaseURL;
import siteurl.in.admin_loyalty.Adaptors.VendorTransactionAdapter;
import siteurl.in.admin_loyalty.Loyalty_Singlton;
import siteurl.in.admin_loyalty.Objects.VendorTransaction_Object;
import siteurl.in.admin_loyalty.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PointsGivenToBuyer extends Fragment {

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String userdata, sessionid, uid, getsaveuserid,VendorName;
    EditText myFilter;
    SearchView searchView;

    VendorTransactionAdapter vendorTransactionAdapter;
    ArrayList<JSONObject> rawvendorlist = new ArrayList<>();
    ArrayList<VendorTransaction_Object> translinearrayalllist = new ArrayList<VendorTransaction_Object>();
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_points_given_to_buyer, container, false);


        //getting data from login activity
        loginpref = getActivity().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        userdata = loginpref.getString("user_data", null);
        editor = loginpref.edit();

        //UI elements to get the ID
        listView = (ListView) view.findViewById(R.id.vendorPointsGivenList);
       // myFilter = (EditText) view.findViewById(R.id.vendorPointsGivenFilter);
        searchView = (SearchView) view.findViewById(R.id.vendorPointsGivenSearch);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getsaveuserid = getActivity().getIntent().getStringExtra("individialvenderid");
        VendorName = getActivity().getIntent().getStringExtra("vendorName");
        PointsGivenHistory();
    }

    private void PointsGivenHistory() {

        StringRequest PointsGivenByAdmin = new StringRequest(Request.Method.POST,
                APIBaseURL.vendortransaction, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String responseerror = jsonObject.getString("Error");
                    if (responseerror.equals("false")) {
                        String responsemsg = jsonObject.getString("Message");
                        String responsedata = jsonObject.getString("data");
                        JSONArray dataArray = new JSONArray(responsedata);
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data_object = dataArray.getJSONObject(i);
                            String vendor_trans_id = data_object.getString("vendor_trans_id");
                            String user_id = data_object.getString("user_id");
                            String vendor_id = data_object.getString("vendor_id");
                            String points_earned_id = data_object.getString("points_earned_id");
                            String vendor_payment_rec_id = data_object.getString("vendor_payment_rec_id");
                            String opening_balance = data_object.getString("opening_balance");
                            String converted_amount_approved = data_object.getString("converted_amount_approved");
                            String payment_amount = data_object.getString("payment_amount");
                            String closing_balance = data_object.getString("closing_balance");
                            String created_at = data_object.getString("created_at");
                            String updated_at = data_object.getString("updated_at");
                            String status = data_object.getString("status");

                            rawvendorlist.add(data_object);
                            translinearrayalllist.add(new VendorTransaction_Object(vendor_trans_id, user_id, VendorName
                                    , points_earned_id, vendor_payment_rec_id, opening_balance, converted_amount_approved,
                                    payment_amount, closing_balance, created_at, updated_at, status));
                        }

                        vendorTransactionAdapter = new VendorTransactionAdapter(getContext(), R.layout.userdata_layout1, translinearrayalllist);
                        listView.setAdapter(vendorTransactionAdapter);

                        //enables filtering for the contents of the given ListView
                        listView.setTextFilterEnabled(true);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            }
                        });

                        //Enabling search filter

                        /*myFilter.addTextChangedListener(new TextWatcher() {
                            public void afterTextChanged(Editable s) {
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                vendorTransactionAdapter.getFilter().filter(s.toString());
                            }
                        });*/

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {
                                vendorTransactionAdapter.getFilter().filter(s.toString());
                                return true;
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
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
                    // Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(getContext(), "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(getContext(), "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(getContext(), "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(getContext(), "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(getContext(), " Timeout Error", Toast.LENGTH_LONG).show();
                } else
                {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionid);
                params.put("vendor_id", getsaveuserid);
                params.put("api_key", APIBaseURL.APIKEY);
                return params;
            }
        };
        PointsGivenByAdmin.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getActivity()).addtorequestqueue(PointsGivenByAdmin);
    }

}
