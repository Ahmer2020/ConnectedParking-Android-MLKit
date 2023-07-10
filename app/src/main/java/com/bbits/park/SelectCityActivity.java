package com.bbits.park;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity {
    ListView lv;
    GlobalContext data= new GlobalContext();
    JSONArray cities= data.getCities();
    TextView headerRight;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    AlertDialog.Builder builder;
    DialogInterface.OnClickListener dialogClickListener;


    SearchView search;
    List<String> cityNames = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    public void onBackPressed() {
        builder.show();
    }

    void logoutSelective(){
        loginPrefsEditor.remove("autoLogin");
        loginPrefsEditor.commit();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        search = (SearchView) findViewById(R.id.searchBar);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
              if (cityNames.contains(query.toLowerCase())) {
                  adapter.getFilter().filter(query);
//                  Toast.makeText(SelectCityActivity.this, "Match found", Toast.LENGTH_LONG).show();

              } else {
//                  Toast.makeText(SelectCityActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
              }
              return false;
          }

          @Override
          public boolean onQueryTextChange(String s) {

              for(int i =0; i<cityNames.size(); i++){

                  if (cityNames.get(i).toLowerCase().contains(s.toLowerCase())) {
                      adapter.getFilter().filter(s);
//                      Toast.makeText(SelectCityActivity.this, "Match found", Toast.LENGTH_LONG).show();
                  } else {
//                      Toast.makeText(SelectCityActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
                  }
              }

              return false;
          }
      });

        builder = new AlertDialog.Builder(this);

        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        logoutSelective();
//                        loginPrefsEditor.clear();
//                        loginPrefsEditor.commit();
////                       startActivity(new Intent(SelectCityActivity.this, LoginActivity.class));
//                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        builder.setMessage("Are You Sure You Want To Logout?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
        Log.i("CITIES:", String.valueOf(cities));
        lv = (ListView) findViewById(R.id.listView);


        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        headerRight = findViewById(R.id.headerRight);

        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutSelective();

//                loginPrefsEditor.clear();
//                loginPrefsEditor.commit();
////                startActivity(new Intent(SelectCityActivity.this, LoginActivity.class));
//                finish();
            }
        });

//
//        Toast t;
//        t = Toast.makeText(this, "Cities:" + cities, Toast.LENGTH_SHORT);
//        t.show();

//        // Instanciating an array list (you don't need to do this,
//        // you already have yours).
//        List<String> your_array_list = new ArrayList<String>();
//        your_array_list.add("foo");
//        your_array_list.add("bar");
//
//        // This is the array adapter, it takes the context of the activity as a
//        // first parameter, the type of list view as a second parameter and your
//        // array as a third parameter.
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_1,
//                your_array_list );
//
//        lv.setAdapter(arrayAdapter);

        //Creating an empty ArrayList of type Object
        List<Object> citiesNames = new ArrayList<>();

//        List<String> citiesNames = new ArrayList<>();

        ArrayList citiesIds = new ArrayList();

        //Checking whether the JSON array has some value or not
        if (cities != null) {

            //Iterating JSON array
            for (int i=0;i<cities.length();i++){
                //Adding each element of JSON array into ArrayList
                try {
                    JSONObject rec = cities.getJSONObject(i);
                    String city = rec.getString("city_name");
                    String id = rec.getString("_id");


                    Log.i("Cities gg:",city);
                    citiesIds.add(id);
                    citiesNames.add(city);
                    
//                    cityNames[i] = city;
                    cityNames.add(city);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

//            ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(
//                this,
//                android.R.layout.simple_list_item_1,
//                citiesNames);
//
//        lv.setAdapter(arrayAdapter);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,cityNames);
            lv.setAdapter(adapter);

            AdapterView.OnItemClickListener cityClickedHandler = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // Do something in response to the click
                    Log.i("City clicked", String.valueOf(citiesIds.get((int) id)));
                    data.setSelectedCityName((String) citiesNames.get((int) id));
                    data.setSelectedCityId((String) citiesIds.get((int) id));

                    Log.i("GetSelectedCity ", String.valueOf(data.getSelectedCityName()));
//                    Toast.makeText(SelectCityActivity.this, data.getSelectedCityId().toString(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SelectCityActivity.this, InfractionActivity.class));

                }
            };
            lv.setOnItemClickListener(cityClickedHandler);
        }
    }
}