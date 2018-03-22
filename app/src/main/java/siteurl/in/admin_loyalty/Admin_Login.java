package siteurl.in.admin_loyalty;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.SyncStateContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class Admin_Login extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextView mylogin_tv, mForgotpassword;
    EditText mEtPwd, mETusername;
    CheckBox mCbShowPwd;
    TextInputLayout mEmail_forgot;
    String forgotemail, str_email, str_password;
    String response_error, response_message;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    private SpotsDialog dialog;

    private Toolbar mToolbar;
    private TextView toolbarTitle;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);
        alertDialog = new Dialog(this);

        checkConnection();

        //UI elements to get ID
        mylogin_tv = (TextView) findViewById(R.id.getlogin_tv);
        mETusername = (EditText) findViewById(R.id.etusername);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        //  toolbarTitle.setText("Login");

        // get the password EditText
        mEtPwd = (EditText) findViewById(R.id.etPassword);
        // get the show/hide password Checkbox
        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);

        // add onCheckedListener on checkbox
        // when user clicks on this checkbox, this is the handler.
        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        // when user clicks on this checkbox, password will show
        mForgotpassword = (TextView) findViewById(R.id.forgotpassword_tv);
        mForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginpref.edit();

        dialog = new SpotsDialog(Admin_Login.this, R.style.Custom);
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

    //validations for Admin Login
    public void getloginnow(View view) {

        dialog.show();

        str_email = mETusername.getText().toString().trim();
        str_password = mEtPwd.getText().toString().trim();

        if (str_email.equals("") || (str_email.equals(null) || !(str_email.matches(EMAIL_PATTERN)))) {
            dialog.dismiss();
            Toast.makeText(Admin_Login.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (str_password.equals("") || (str_password.equals(null))) {
            dialog.dismiss();
            Toast.makeText(Admin_Login.this, "Enter Valid password", Toast.LENGTH_SHORT).show();
            return;
        } else {
            dialog.show();
            Sign_in();
        }
    }


    public void showForgotPasswordDialog() {

        AlertDialog.Builder forgotpassworddialog = new AlertDialog.Builder(this);
        forgotpassworddialog.setIcon(R.mipmap.ic_launcher);
        forgotpassworddialog.setTitle("Forgot Password");

        LayoutInflater forgotpasswordinflate = LayoutInflater.from(this);
        View forgotView = forgotpasswordinflate.inflate(R.layout.forgotpassword, null);
        mEmail_forgot = forgotView.findViewById(R.id.email_forgotpassword);
        forgotpassworddialog.setView(forgotView);
        forgotpassworddialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                forgotemail = mEmail_forgot.getEditText().getText().toString();
                if (forgotemail.equals("null") || (forgotemail.equals(null) || !(forgotemail.matches(EMAIL_PATTERN)))) {
                    Toast.makeText(Admin_Login.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    showForgotPasswordDialog();
                    return;
                }
                sendEmailToServer();
                dialogInterface.dismiss();
            }
        });
        forgotpassworddialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        forgotpassworddialog.show();
    }

    //this is the method to send email to server(for forgot password)
    public void sendEmailToServer() {

        StringRequest cartData = new StringRequest(Request.Method.POST, APIBaseURL.forgotPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");

                            if (Error.equals("false")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

                            }

                            if (Error.equals("true")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

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
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    Log.d("NetworkError", "Please check your internet connection");
                    Toast.makeText(Admin_Login.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(Admin_Login.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Log.d("AuthFailureError", "Authentication Error");
                    Toast.makeText(Admin_Login.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Log.d("ParseError", "Parse Error");
                    Toast.makeText(Admin_Login.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Log.d("NoConnectionError", "No connection");
                    Toast.makeText(Admin_Login.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Log.d("TimeoutError", "Timeout Error");
                    Toast.makeText(Admin_Login.this, "Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Admin_Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", forgotemail);
                params.put("api_key", APIBaseURL.APIKEY);
                return params;
            }
        };

        cartData.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(Admin_Login.this).addtorequestqueue(cartData);
    }


    public void emailAlert(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_Login.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Loyalty Program application");
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    //this is the method for admin login
    public void Sign_in() {

        {
            StringRequest admin_login = new StringRequest(Request.Method.POST, APIBaseURL.login, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject objectsignup = new JSONObject(response);
                        response_error = objectsignup.getString("Error");
                        if (response_error.equals("true")) {
                            response_message = objectsignup.getString("Message");
                            Toast.makeText(Admin_Login.this, response_message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        } else if (response_error.equals("false")) {
                            response_message = objectsignup.getString("Message");
                            Toast.makeText(Admin_Login.this, response_message, Toast.LENGTH_SHORT).show();

                            String role = objectsignup.getString("role");
                            String data = objectsignup.getString("data");

                            JSONObject dataobject = new JSONObject(data);

                            String name = dataobject.getString("name");
                            String email = dataobject.getString("email");
                            String user_id = dataobject.getString("user_id");
                            String sid = dataobject.getString("sid");
                            String user_group_id = dataobject.getString("user_group_id");

                            editor.putString("loginname", name);
                            editor.putString("loginemail", email);
                            editor.putString("role", role);
                            editor.putString("sessionid", sid);
                            editor.putString("User-id", user_id);
                            editor.putString("user_group_id", user_group_id);
                            editor.putString("login_data", String.valueOf(dataobject));
                            editor.commit();

                            mETusername.getText().clear();
                            mEtPwd.getText().clear();

                            startActivity(new Intent(Admin_Login.this, Welcome_splashpage.class).
                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
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
                        // Toast.makeText(Admin_Login.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Log.d("ServerError", "ServerError");
                        // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                        // Toast.makeText(Admin_Login.this, "Server Error", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Log.d("AuthFailureError", "Authentication Error");
                        //If you are trying to do Http Basic authentication then this error is most likely to come.
                        Toast.makeText(Admin_Login.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Log.d("ParseError", "Parse Error");
                        //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                        Toast.makeText(Admin_Login.this, "Parse Error", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NoConnectionError) {
                        Log.d("NoConnectionError", "No connection");
                        // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                        Toast.makeText(Admin_Login.this, "No connection", Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Log.d("TimeoutError", "Timeout Error");
                        // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                        Toast.makeText(Admin_Login.this, " Timeout Error", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Admin_Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put("email", str_email);
                    params.put("password", str_password);
                    params.put("api_key", APIBaseURL.APIKEY);

                    return params;
                }
            };

            admin_login.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(admin_login);
        }
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
