package siteurl.in.admin_loyalty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProductForRedemption extends AppCompatActivity implements  ConnectivityReceiver.ConnectivityReceiverListener{

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid, sessionid, uid;
    private Toolbar mToolbar;
    private TextView toolbartitle;
    EditText Edtxt_exp_date, EDtprod_name, EDtprod_price, EDtprod_desc, EDtprod_terms;
    ImageView cal_exp, prod_img;
    Calendar newDate = Calendar.getInstance();
    int mYear, mMonth, mDay;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask, proimage, strFileNameone, profile, Str_name, Str_price, Str_desc, Str_terms, Str_expdate, datereader;
    public static final int RequestPermissionCode = 1;
    Bitmap thumbnail;
    TextInputLayout textInputLayoutProductName, textInputLayoutProductPrice, textInputLayoutProductDesc, textInputLayoutTerms, textInputLayoutDate;
    private SpotsDialog dialog;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_for_redemption);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //get data from LoginActivity
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        editor = loginpref.edit();

        checkConnection();

        //UI elements to get the Id
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbartitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText("Add Product For Redemption");

        cal_exp = (ImageView) findViewById(R.id.calender_exp1);
        prod_img = (ImageView) findViewById(R.id.my_product_img);

        Edtxt_exp_date = (EditText) findViewById(R.id.edt_Expdate1);
        EDtprod_name = (EditText) findViewById(R.id.edtproduct_name);
        EDtprod_price = (EditText) findViewById(R.id.edtproduct_price);
        EDtprod_desc = (EditText) findViewById(R.id.edtproduct_desc);
        EDtprod_terms = (EditText) findViewById(R.id.edtproduct_terms);

        textInputLayoutProductName = findViewById(R.id.productnameTextinputlayout);
        textInputLayoutProductPrice = findViewById(R.id.productpriceTextinputlayout);
        textInputLayoutProductDesc = findViewById(R.id.productDescTextinputlayout);
        textInputLayoutTerms = findViewById(R.id.productTermsTextinputlayout);
        textInputLayoutDate = findViewById(R.id.productdateTextinputlayout);

        //calendar
        cal_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog();
            }
        });

        //for selecting image
        prod_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firstcheckPermission();
                selectImage();
            }
        });

        dialog = new SpotsDialog(ProductForRedemption.this, R.style.Custom);
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



    public void dateDialog() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(ProductForRedemption.this,
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

    public void firstcheckPermission() {
        if (checkPermission()) {
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
        int FirstPermissionResult = ContextCompat.checkSelfPermission(this, CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadContactsPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadLocationPermission && ReadContactsPermission /*&& ReadPhoneStatePermission*/) {//&& ReadContactsPermission && ReadPhoneStatePermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProductForRedemption.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ProductForRedemption.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(pickPhoto, "Select File"), SELECT_FILE);

    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prod_img.setImageBitmap(thumbnail);
        strFileNameone = destination.getName();
        Toast.makeText(getApplicationContext(), "photo name" + strFileNameone, Toast.LENGTH_LONG).show();

        proimage = getStringImage(thumbnail);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Uri selectedImageUri = data.getData();
                String imageNameOne = getRealPathFromURI(selectedImageUri);

                File file = new File(imageNameOne);
                strFileNameone = file.getName();
                Toast.makeText(getApplicationContext(), strFileNameone + " " + " real ", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        prod_img.setImageBitmap(thumbnail);
        proimage = getStringImage(thumbnail);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri,
                proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }


    //validations for Redeemption producst
    public void validateaddOffernow(View view) {

        dialog.show();


        Str_name = EDtprod_name.getText().toString().trim();
        Str_price = EDtprod_price.getText().toString().trim();
        Str_desc = EDtprod_desc.getText().toString().trim();
        Str_terms = EDtprod_terms.getText().toString().trim();
        Str_expdate = Edtxt_exp_date.getText().toString().trim();

        if (Str_name.equals("") || (Str_name.equals(null))) {

            textInputLayoutProductName.setError("Enter a valid Product Name");
            dialog.dismiss();
            return;
        } else {
            textInputLayoutProductName.setError(null);
        }

        if (Str_price.equals("") || (Str_price.equals(null))) {

            textInputLayoutProductPrice.setError("Enter a valid Product Price");
            dialog.dismiss();
            return;
        } else {
            textInputLayoutProductPrice.setError(null);
        }

        if (Str_desc.equals("") || (Str_desc.equals(null))) {

            textInputLayoutProductDesc.setError("Enter a valid Product Description");
            dialog.dismiss();
            return;
        } else {
            textInputLayoutProductDesc.setError(null);
        }

        if (Str_terms.equals("") || (Str_terms.equals(null))) {

            textInputLayoutProductDesc.setError("Enter a valid Product Terms and Condition");
            dialog.dismiss();
            return;
        } else {
            textInputLayoutProductDesc.setError(null);
        }

        if (Str_expdate.equals("") || (Str_expdate.equals(null))) {

            textInputLayoutDate.setError("Enter a valid Product Expiry Date");
            dialog.dismiss();
            return;
        } else {
            dialog.show();
            textInputLayoutDate.setError(null);
            testforImage();
        }

    }

    public void testforImage() {
        if (thumbnail == null) {
            Toast.makeText(ProductForRedemption.this, " image not uploaded ", Toast.LENGTH_SHORT).show();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.addimage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            String profiles = getStringImage(bitmap);

            profile = String.valueOf(profiles);
            checkConnection();
            imageUpload();
        } else if (thumbnail != null) {
            profile = getStringImage(thumbnail);
            checkConnection();
            imageUpload();
        }
    }

    public void imageUpload() {

        if (strFileNameone.isEmpty()) {
            strFileNameone = "filename not found";
        }
        StringRequest addRedeem = new StringRequest(Request.Method.POST, APIBaseURL.addredeemproduct,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            String responseError = jsonObject.getString("Error");
                            if (responseError.equals("false")) {
                                String responsemessage = jsonObject.getString("Message");
                                startActivity(new Intent(ProductForRedemption.this, MainActivity.class).
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

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(ProductForRedemption.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(ProductForRedemption.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(ProductForRedemption.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(ProductForRedemption.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(ProductForRedemption.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(ProductForRedemption.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProductForRedemption.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionid);
                params.put("api_key", APIBaseURL.APIKEY);
                params.put("product_name", Str_name);
                params.put("prod_description", Str_desc);
                params.put("product_img", profile);
                params.put("filename", strFileNameone);
                params.put("points_value", Str_price);
                params.put("expiry_date", datereader);
                params.put("terms_and_condition", Str_terms);
                return params;
            }
        };
        addRedeem.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(addRedeem);
    }

    public void allProductForRedemption(View view) {
        startActivity(new Intent(ProductForRedemption.this, AllRedemptionProduct.class).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
