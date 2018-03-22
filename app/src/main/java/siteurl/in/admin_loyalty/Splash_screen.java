package siteurl.in.admin_loyalty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash_screen extends AppCompatActivity {

    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String loginUserId, sessionId, uId;

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("sessionid", null);
        uId = loginPref.getString("User-id", null);
        editor = loginPref.edit();

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                if (loginPref.contains("User-id")) {
                    loginUserId = loginPref.getString("User-id", null);
                    if (loginUserId.equals("") || loginUserId.equals(null)) {
                        startActivity(new Intent(Splash_screen.this, Admin_Login.class));
                    } else {
                        startActivity(new Intent(Splash_screen.this, MainActivity.class));
                    }
                } else {
                    startActivity(new Intent(Splash_screen.this, Admin_Login.class));
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
