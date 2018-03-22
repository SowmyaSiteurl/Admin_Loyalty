package siteurl.in.admin_loyalty;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static android.Manifest.permission.CALL_PHONE;

public class CalltoVendor extends AppCompatActivity {

    String Number;
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.callto_vendor);
        Number = getIntent().getStringExtra("phonenumber");

        // startActivity( new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Number)));
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Number));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermission();
            return;
        }
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(callIntent);
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(CalltoVendor.this, new String[]
                {
                        CALL_PHONE
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission) {
                        Toast.makeText(CalltoVendor.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CalltoVendor.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
