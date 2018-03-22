package siteurl.in.admin_loyalty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Welcome_splashpage extends AppCompatActivity {

    TextView tv_splash;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String loginuserid,sessionid,uid,login_identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        login_identity = loginpref.getString("loginname", null);
        editor = loginpref.edit();

        setContentView(R.layout.welcome_splashpage); Thread timer = new Thread() {
            public void run() {
                try {
                    tv_splash = (TextView) findViewById(R.id.user_identity);
                    tv_splash.setText(login_identity);
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    {
                        startActivity(new Intent(Welcome_splashpage.this,MainActivity.class));
                    }
                }
            }
        };
        timer.start();

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            Toast.makeText(this, "Good Morning!!", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            Toast.makeText(this, "Good Afternoon!!", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            Toast.makeText(this, "Good Evening!!", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            Toast.makeText(this, "Good Night!!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
