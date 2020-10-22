package com.example.json2sms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity {

    JSONArray jArray = null;
    private static final int REQUEST_SMS = 0;
    SmsManager smsManager = SmsManager.getDefault();
    SubscriptionManager localSubscriptionManager;
    SubscriptionInfo simInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(SEND_SMS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> permissions = new ArrayList<>();
                if (!shouldShowRequestPermissionRationale(SEND_SMS)) {
                    permissions.add(SEND_SMS);
                }
                if (!shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
                    permissions.add(READ_PHONE_STATE);
                }
                if (permissions.size() > 0)
                    requestPermissions(GetStringArray(permissions), REQUEST_SMS);
            }
        }
        localSubscriptionManager = SubscriptionManager.from(this);
        int sims = localSubscriptionManager.getActiveSubscriptionInfoCount();
        ArrayList<String> simids = new ArrayList<>();
        for (int i = 1; i <= sims; i++)
            simids.add("SIM " + i);
        Spinner sp = findViewById(R.id.sims);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                simInfo = (SubscriptionInfo) localList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, simids));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (String permission : permissions) {
            if (permission.equals(READ_PHONE_STATE)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                int sims = localSubscriptionManager.getActiveSubscriptionInfoCount();
                ArrayList<String> simids = new ArrayList<>();
                for (int i = 1; i <= sims; i++)
                    simids.add("SIM " + i);
                Spinner sp = findViewById(R.id.sims);
                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                        simInfo = (SubscriptionInfo) localList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                sp.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, simids));
                return;
            }
        }
    }

    public static String[] GetStringArray(ArrayList<String> arr){
        String str[] = new String[arr.size()];
        for(int i = 0; i<arr.size();i++)
            str[i] = arr.get(i);
        return str;
    }

    public void snd_click(View view) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TextView con = findViewById(R.id.console);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = jArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                smsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(obj.getString("To"), null, obj.getString("Body"), null, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        con.setText("Completed");
    }
    int ACTIVITY_CHOOSE_FILE = 0;
    public void brws_click(View view) {
        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("*/*");
        try{
            startActivityForResult(fileintent, ACTIVITY_CHOOSE_FILE);
        }catch(ActivityNotFoundException e){
            Log.d("JSON2SMS", "No activity can handle picking a file.");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        if (resultCode == RESULT_OK){
            try {
                Uri uri = data.getData();
                InputStreamReader isr = new InputStreamReader(getContentResolver().openInputStream(uri));
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null){
                    sb.append(line).append("\n");
                }
                reader.close();
                JSONObject jObj = new JSONObject(sb.toString());
                jArray = jObj.getJSONArray("SMSs");
                TextView tv = findViewById(R.id.rec);
                tv.setText( jArray.length() + " Recipients");
                Button snd = findViewById(R.id.send_btn);
                snd.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void go2page(View view) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rifat-hossain.github.io/"));
        startActivity(browse);
    }
}
