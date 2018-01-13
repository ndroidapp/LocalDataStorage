package com.example.android.localdatastorage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.android.localdatastorage.model.DataItem;
import com.example.android.localdatastorage.sample.SampleDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
        RecyclerView recyclerView=findViewById(R.id.rvItems);
        recyclerView.setAdapter(adapter);
    }
}
