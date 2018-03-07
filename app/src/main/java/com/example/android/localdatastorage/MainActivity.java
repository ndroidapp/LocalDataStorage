package com.example.android.localdatastorage;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.localdatastorage.model.DataItem;
import com.example.android.localdatastorage.sample.SampleDataProvider;
import com.example.android.localdatastorage.utils.JSONHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SIGNIN_REQUEST = 0;
    public static final String MY_GLOBAL_PREFS = "my_global_prefs";
    private static final String TAG = "Import";
    List<DataItem> dataItemList = SampleDataProvider.dataItemList;
    List<String> itemNames = new ArrayList<>();

    private static final int REQUEST_PERMISSION_WRITE = 101;
    private boolean permissionGranted;


//    TextView tvDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Collections.sort(dataItemList, new Comparator<DataItem>() {
            @Override
            public int compare(DataItem o1, DataItem o2) {
                return o1.getItemName().compareTo(o2.getItemName());
            }
        });
        /*for (DataItem item: dataItemList) {
            itemNames.add(item.getItemName());
        }
        Collections.sort(itemNames);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,itemNames
        );*/
        DataItemAdapter adapter = new DataItemAdapter(this, dataItemList);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean grid = settings.getBoolean(getString(R.string.prefs_display_in_grid), false);

        RecyclerView recyclerView = findViewById(R.id.rvItems);
        if (grid) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        recyclerView.setAdapter(adapter);

        if (!permissionGranted) {
            checkPermissions();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signin:
                Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                startActivityForResult(intent, SIGNIN_REQUEST);
                return true;
            case R.id.action_settings:
                Intent intentSettings = new Intent(MainActivity.this, PrefsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.action_export:
                boolean result= JSONHelper.exportToJSON(this, dataItemList);
                if (result){
                    Toast.makeText(MainActivity.this, "Data Exported", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Export Failed", Toast.LENGTH_SHORT).show();

                }
                return true;
            case R.id.action_import:
                List<DataItem> dataItems=JSONHelper.importFromJSON(this);
                if (dataItems!=null){
                    for (DataItem dataItem : dataItems) {
                        Log.i(TAG, "onOptionsItemSelected: "+dataItem.getItemName());
                    }
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SIGNIN_REQUEST) {
            String email = data.getStringExtra(SigninActivity.EMAIL_KEY);
            Toast.makeText(MainActivity.this, "You Signin as " + email, Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = getSharedPreferences(MY_GLOBAL_PREFS, MODE_PRIVATE).edit();
            editor.putString(SigninActivity.EMAIL_KEY, email);
            editor.apply();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // Initiate request for permissions.
    private boolean checkPermissions() {

        if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
            Toast.makeText(this, "This app only works on devices with usable external storage",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            return true;
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "External storage permission granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
