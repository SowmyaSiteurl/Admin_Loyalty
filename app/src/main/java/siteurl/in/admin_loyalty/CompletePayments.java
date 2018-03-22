package siteurl.in.admin_loyalty;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompletePayments extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{


    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, sessionid, uid, login_identity, datereader;
    private Toolbar mToolbar;
    private TextView toolbartitle;
    String reciveid;
    EditText amnt_edt, decr_edt, Edtxt_exp_date, transnumber;
    String amount_str, decr_str, paydate, transe_str;
    ImageView cal_exp;
    Calendar newDate = Calendar.getInstance();
    int mYear, mMonth, mDay;
    String message;
    TextInputLayout textInputLayoutamt, textInputLayoutdes, textInputLayouttrnsId, textInputLayoutdte;
    private SpotsDialog dialog;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_payments);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //getting data from LoginActivity
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        login_identity = loginpref.getString("loginname", null);
        editor = loginpref.edit();

        //UI elements to get the ID
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbartitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText("Complete Payment");

        reciveid = getIntent().getStringExtra("saveid");
        checkConnection();

        amnt_edt = (EditText) findViewById(R.id.amountEDT);
        decr_edt = (EditText) findViewById(R.id.descrEDT);
        cal_exp = (ImageView) findViewById(R.id.calender_exp);
        Edtxt_exp_date = (EditText) findViewById(R.id.edt_Expdate);
        transnumber = (EditText) findViewById(R.id.transactionEDT);

        textInputLayoutamt = findViewById(R.id.amtTextinputlayout);
        textInputLayoutdes = findViewById(R.id.descrTextinputlayout);
        textInputLayouttrnsId = findViewById(R.id.transTextinputlayout);
        textInputLayoutdte = findViewById(R.id.dteTextinputlayout);


        dialog = new SpotsDialog(CompletePayments.this, R.style.Custom);
        dialog.dismiss();

        //calender
        cal_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });
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

    public void datePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(CompletePayments.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        newDate.set(year, month, day);
                        String calender_date = new SimpleDateFormat("yyyy-MM-dd").format(newDate.getTime());

                        String inputPattern = "yyyy-MM-dd";
                        String outputPattern = "dd-MMM-yyyy";
                        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                        Date date = null;
                        String newclldate = null;
                        datereader = calender_date;
                        newDate.set(mYear, mMonth, mDay);
                        try {
                            date = inputFormat.parse(calender_date);
                            newclldate = outputFormat.format(date);
                            Edtxt_exp_date.setText(newclldate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mYear = newDate.get(Calendar.YEAR);
                        mMonth = newDate.get(Calendar.MONTH);
                        mDay = newDate.get(Calendar.DAY_OF_MONTH);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            Edtxt_exp_date.setText("");
                        }
                    }
                });
        datePickerDialog.show();

    }

    //validation for complete payment
    public void Completepaymentnow(View view) {

        dialog.show();

        amount_str = amnt_edt.getText().toString().trim();
        decr_str = decr_edt.getText().toString().trim();
        paydate = Edtxt_exp_date.getText().toString().trim();
        transe_str = transnumber.getText().toString().trim();

        if (amount_str.equals("") || (amount_str.equals(null))) {
            textInputLayoutamt.setError("Enter Amount");
            dialog.dismiss();
            return;
        } else {
            textInputLayoutamt.setError(null);
        }

        if (decr_str.equals("") || (decr_str.equals(null))) {
            textInputLayoutdes.setError("Enter Description");
            dialog.dismiss();
            return;
        } else {
            textInputLayoutdes.setError(null);
        }

        if (paydate.equals("") || (paydate.equals(null))) {

            textInputLayoutdte.setError("Date Can Not Be Empty");
            dialog.dismiss();
            return;
        } else {
            textInputLayoutdte.setError(null);
        }

        if (transe_str.equals("") || (transe_str.equals(null))) {

            textInputLayouttrnsId.setError("Please enter transaction Id Number");
            dialog.dismiss();
            return;
        } else {
            dialog.show();
            textInputLayouttrnsId.setError(null);
            checkConnection();
            completePayment();
        }

    }

    private void completePayment() {
        {
            StringRequest payment_complete = new StringRequest(Request.Method.POST,
                    APIBaseURL.vendorpayment, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String errorresponse = jsonObject.getString("Error");
                        if (errorresponse.equals("false")) {
                            String mesgresponse = jsonObject.getString("Message");
                            Toast.makeText(CompletePayments.this, mesgresponse, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CompletePayments.this, AddPayments.class).
                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        } else {
                            String mesgresponse = jsonObject.getString("Message");
                            Toast.makeText(CompletePayments.this, mesgresponse, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CompletePayments.this, AddPayments.class).
                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    dialog.dismiss();

                    if (error.networkResponse != null) {
                        return;
                    }
                    if (error instanceof ServerError) {
                        // Toast.makeText(CompletePayments.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(CompletePayments.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(CompletePayments.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        //  Toast.makeText(CompletePayments.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(CompletePayments.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(CompletePayments.this, "No Connection Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(CompletePayments.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                    params.put("paymentdate", datereader);
                    params.put("vendor_id", reciveid);
                    params.put("transaction_id", transe_str);
                    params.put("description", decr_str);
                    params.put("amount", amount_str);
                    return params;
                }
            };
            payment_complete.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(payment_complete);
        }
    }

    public void opendailog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(CompletePayments.this, MainActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }

                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
