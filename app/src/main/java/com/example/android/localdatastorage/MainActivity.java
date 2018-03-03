package com.example.android.localdatastorage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.localdatastorage.model.DataItem;
import com.example.android.localdatastorage.sample.SampleDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SIGNIN_REQUEST = 0;
    public static final String MY_GLOBAL_PREFS = "my_global_prefs";
    List<DataItem> dataItemList= SampleDataProvider.dataItemList;
    List<String> itemNames=new ArrayList<>();

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
        DataItemAdapter adapter=new DataItemAdapter(this,dataItemList);

        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(this);
        boolean grid=settings.getBoolean(getString(R.string.prefs_display_in_grid),false);

        RecyclerView recyclerView=findViewById(R.id.rvItems);
        if (grid){
            recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_signin:
                Intent intent = new Intent(MainActivity.this,SigninActivity.class);
                startActivityForResult(intent,SIGNIN_REQUEST);
                return true;
            case R.id.action_settings:
                Intent intentSettings = new Intent(MainActivity.this,PrefsActivity.class);
                startActivity(intentSettings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode==SIGNIN_REQUEST){
            String email=data.getStringExtra(SigninActivity.EMAIL_KEY);
            Toast.makeText(MainActivity.this, "You Signin as "+email, Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor=getSharedPreferences(MY_GLOBAL_PREFS,MODE_PRIVATE).edit();
            editor.putString(SigninActivity.EMAIL_KEY,email);
            editor.apply();
        }
    }
}
