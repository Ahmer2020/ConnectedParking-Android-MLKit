package com.bbits.park;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreviousTicketsActivity extends AppCompatActivity {

    GlobalContext data= new GlobalContext();
    JSONArray tickets=null;
    private ProgressBar spinner;
    TextView headerRight;
    TabLayout ticketsTab;
//    ViewPager2 simpleViewPager;
ViewPager viewPager;


    private ViewPager pager = null;
//    private MainPagerAdapter pagerAdapter = null;


   public View convertViews(Integer position) {

       LayoutInflater inflater = getLayoutInflater();

       View convertView = (inflater.inflate(R.layout.previous_ticket_layout, null));

       if (convertView == null)
           convertView = inflater.inflate(R.layout.previous_ticket_layout, null);

//            TextView ticketName =  (TextView) convertView.findViewById(R.id.ticketNameTv);

       TextView plate = (TextView) convertView.findViewById(R.id.plateTextView);

       TextView zone = (TextView) convertView.findViewById(R.id.zoneTextView);

       TextView ticketNameTv = (TextView) convertView.findViewById(R.id.ticketNameTextView);

       TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTextView);

       ListView fromTv = convertView.findViewById(R.id.fromToListView);

       LinearLayout imagesView = convertView.findViewById(R.id.imagesView);
       LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

       JSONObject json_data = null;
       try {
           JSONArray json_array = data.getTicketsIssued();

           json_data = json_array.getJSONObject(position);
       } catch (JSONException e) {
           throw new RuntimeException(e);
       }
       if (null != json_data) {
           Log.e("GGGGGG", json_data.toString());
           String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
           try {

               plate.setText(json_data.getString("plate"));
               zone.setText(json_data.getJSONObject("zone").getString("zone_name"));
               ticketNameTv.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
               ticketNum.setText(json_data.getString("ticket_num"));

               String[] parts = json_data.getString("issued_at").split("T");
               String date = parts[0];
               String time = parts[1].substring(0, 5);
               List<String> fromToList = new ArrayList<>();

               fromToList.add(date);
               fromToList.add(time);

               ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                       PreviousTicketsActivity.this,
                       android.R.layout.simple_spinner_item,
                       fromToList);

               fromTv.setAdapter(arrayAdapter);
//                        addView(convertView);
//                        tab.setCustomView(convertView);
//               Integer pageIndex = pagerAdapter.addView(convertView);
////                        pager.setCurrentItem (pageIndex, true);
//
//               pagerAdapter.notifyDataSetChanged();
               return convertView;
           } catch (JSONException e) {
               throw new RuntimeException(e);
           }
       }
       return null;
   }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_tickets);

        ticketsTab = findViewById(R.id.ticketsTab);
        viewPager=(ViewPager)findViewById(R.id.simpleViewPager);

        for(int i = 0; i<data.getTicketsIssued().length(); i++){
            try {
                ticketsTab.addTab(ticketsTab.newTab().setText(data.getTicketsIssued().getJSONObject(i).getJSONObject("ticket").getString("ticket_name")));
//                pagerAdapter.addView(convertViews(i));
//                pagerAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),ticketsTab.getTabCount());
        viewPager.setAdapter(adapter);

//        pagerAdapter = new MainPagerAdapter();
//        pager = (ViewPager) findViewById (R.id.simpleViewPager);
//        pager.setAdapter (pagerAdapter);


        // Create an initial view to display; must be a subclass of FrameLayout.
//        LayoutInflater inflater = this.getLayoutInflater();
//        FrameLayout v0 = (FrameLayout) inflater.inflate (R.layout.previous_ticket_layout, null);
//        pagerAdapter.addView (v0, 0);
//        pagerAdapter.notifyDataSetChanged();

//        selectedTicket = new JSONObject();
//        simpleViewPager = (ViewPager2) findViewById(R.id.simpleViewPager);


//                            pagerAdapter.addView(convertViews(0));
//                    pagerAdapter.notifyDataSetChanged();

//        pagerAdapter.addView(convertViews(0));
//        pagerAdapter.notifyDataSetChanged();



                ticketsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        viewPager.setCurrentItem(tab.getPosition());
//                        View pageToAdd = pagerAdapter.getView (tab.getPosition());
//                        addView(pageToAdd, tab.getPosition());
//                        pagerAdapter.notifyDataSetChanged();
//
//                        setCurrentPage(pageToAdd);


                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
    //                        View pageToRemove = pagerAdapter.getView (tab.getPosition());
    //                        removeView(pageToRemove);
    //                        pagerAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });


//        PagerAdapter adapter = new PagerAdapter
//                (getSupportFragmentManager(), ticketsTab.getTabCount());
//
//        simpleViewPager.setAdapter(adapter);

//        simpleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(ticketsTab));


//        ticketsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//
//
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
////                View convertView = null;
////                convertView = convertViews(tab.getPosition());
////
////                //  addView(convertView);
//////                        tab.setCustomView(convertView);
////                Integer pageIndex = pagerAdapter.addView(convertView);
//                Log.e("GETCUrrenr",getCurrentPage().toString());
//                Log.e("Tab position",String.valueOf(tab.getPosition()));
//
////                pagerAdapter.addView(convertViews(tab.getPosition()));
////                pagerAdapter.notifyDataSetChanged();
//
//                pager.setCurrentItem (tab.getPosition());
//
////
////                pagerAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//                int pageIndex = pagerAdapter.removeView (pager, getCurrentPage());
//                Log.e("After Removing page count id ", String.valueOf(pagerAdapter.getCount()));
//                Log.e("Removed page index is ", String.valueOf(pageIndex));
//                // You might want to choose what page to display, if the current page was "defunctPage".
////                if (pageIndex == pagerAdapter.getCount())
////                    pageIndex--;
////                pager.setCurrentItem(pageIndex);
//
////                Toast.makeText(PreviousTicketsActivity.this, "hbjbhb", Toast.LENGTH_SHORT).show();
//
////                pagerAdapter.removeView(tab.getPosition());
////                Log.e("GETCUrrenr",getCurrentPage().toString());
////                int pageIndex = pagerAdapter.removeView (pager, getCurrentPage());
////                // You might want to choose what page to display, if the current page was "defunctPage".
////                if (pageIndex == pagerAdapter.getCount())
////                    pageIndex--;
////                pager.setCurrentItem (pageIndex);
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
////                Log.e("GETCUrrenr",getCurrentPage().toString());
////                int pageIndex = pagerAdapter.removeView (pager, getCurrentPage());
////                // You might want to choose what page to display, if the current page was "defunctPage".
////                if (pageIndex == pagerAdapter.getCount())
////                    pageIndex--;
////                pager.setCurrentItem (pageIndex);
////                pager.setCurrentItem (tab.getPosition(), false);
////                LayoutInflater inflater = getLayoutInflater();
////
////                View convertView = (inflater.inflate(R.layout.previous_ticket_layout, null));
////
////                if (convertView == null)
////                    convertView = inflater.inflate(R.layout.previous_ticket_layout, null);
////
//////            TextView ticketName =  (TextView) convertView.findViewById(R.id.ticketNameTv);
////
////                TextView plate = (TextView) convertView.findViewById(R.id.plateTextView);
////
////                TextView zone = (TextView) convertView.findViewById(R.id.zoneTextView);
////
////                TextView ticketNameTv = (TextView) convertView.findViewById(R.id.ticketNameTextView);
////
////                TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTextView);
////
////                ListView fromTv = convertView.findViewById(R.id.fromToListView);
////
////                LinearLayout imagesView = convertView.findViewById(R.id.imagesView);
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////
////                JSONObject json_data = null;
////                try {
////                    JSONArray json_array = data.getTicketsIssued();
////
////                    json_data = json_array.getJSONObject(tab.getPosition());
////                } catch (JSONException e) {
////                    throw new RuntimeException(e);
////                }
////                if (null != json_data) {
////                    Log.e("GGGGGG", json_data.toString());
////                    String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
////                    try {
////
////                        plate.setText(json_data.getString("plate"));
////                        zone.setText(json_data.getJSONObject("zone").getString("zone_name"));
////                        ticketNameTv.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
////                        ticketNum.setText(json_data.getString("ticket_num"));
////
////                        String[] parts = json_data.getString("issued_at").split("T");
////                        String date = parts[0];
////                        String time = parts[1].substring(0, 5);
////                        List<String> fromToList = new ArrayList<>();
////
////                        fromToList.add(date);
////                        fromToList.add(time);
////
////                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
////                                PreviousTicketsActivity.this,
////                                android.R.layout.simple_spinner_item,
////                                fromToList);
////
////                        fromTv.setAdapter(arrayAdapter);
//////                        addView(convertView);
//////                        tab.setCustomView(convertView);
////                        Integer pageIndex = pagerAdapter.addView(convertView);
//////                        pager.setCurrentItem (pageIndex, true);
////
////                        pagerAdapter.notifyDataSetChanged();
////                    } catch (JSONException e) {
////                        throw new RuntimeException(e);
////                    }
////                }
//
//            }
//        });

                        headerRight = findViewById(R.id.headerRight);
        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

//
//    public void addView (View newPage, Integer position)
//    {
//        int pageIndex = pagerAdapter.addView (newPage, position);
//        // You might want to make "newPage" the currently displayed page:
//        pager.setCurrentItem (pageIndex, true);
//    }
//    //-----------------------------------------------------------------------------
//    // Here's what the app should do to add a view to the ViewPager.
//    public void addView (View newPage)
//    {
//        int pageIndex = pagerAdapter.addView (newPage);
//        // You might want to make "newPage" the currently displayed page:
//        pager.setCurrentItem (pageIndex, true);
//    }
//
//    //-----------------------------------------------------------------------------
//    // Here's what the app should do to remove a view from the ViewPager.
//    public void removeView (View defunctPage)
//    {
//        int pageIndex = pagerAdapter.removeView (pager, defunctPage);
//        // You might want to choose what page to display, if the current page was "defunctPage".
//        if (pageIndex == pagerAdapter.getCount())
//            pageIndex--;
//        pager.setCurrentItem (pageIndex);
//    }
//
//    //-----------------------------------------------------------------------------
//    // Here's what the app should do to get the currently displayed page.
//    public View getCurrentPage ()
//    {
//        return pagerAdapter.getView (pager.getCurrentItem());
//    }
//
//    //-----------------------------------------------------------------------------
//    // Here's what the app should do to set the currently displayed page.  "pageToShow" must
//    // currently be in the adapter, or this will crash.
//    public void setCurrentPage (View pageToShow)
//    {
//        pager.setCurrentItem (pagerAdapter.getItemPosition (pageToShow), true);
//    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        int mNumOfTabs;
        Fragment fragment = null;

        public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            for (int i = 0; i < mNumOfTabs ; i++) {
                if (i == position) {
                    fragment = new TicketFragment(convertViews(position));
                    break;
                }
            }
            return fragment;

        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }




    }

    public static class TicketFragment extends Fragment {

        public View v;
        public TicketFragment(View v) {
            super(R.layout.previous_ticket_layout);
            this.v = v;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return this.v;
        }
    }

//
//    public class MainPagerAdapter extends PagerAdapter
//    {
//        // This holds all the currently displayable views, in order from left to right.
//        private ArrayList<View> views = new ArrayList<View>();
//
//        //-----------------------------------------------------------------------------
//        // Used by ViewPager.  "Object" represents the page; tell the ViewPager where the
//        // page should be displayed, from left-to-right.  If the page no longer exists,
//        // return POSITION_NONE.
//        @Override
//        public int getItemPosition (Object object)
//        {
//            int index = views.indexOf (object);
//            if (index == -1)
//                return POSITION_NONE;
//            else
//                return index;
//        }
//
//        //-----------------------------------------------------------------------------
//        // Used by ViewPager.  Called when ViewPager needs a page to display; it is our job
//        // to add the page to the container, which is normally the ViewPager itself.  Since
//        // all our pages are persistent, we simply retrieve it from our "views" ArrayList.
//        @Override
//        public Object instantiateItem (ViewGroup container, int position)
//        {
//            View v = views.get (position);
//            container.addView (v);
//            return v;
//        }
//
//        //-----------------------------------------------------------------------------
//        // Used by ViewPager.  Called when ViewPager no longer needs a page to display; it
//        // is our job to remove the page from the container, which is normally the
//        // ViewPager itself.  Since all our pages are persistent, we do nothing to the
//        // contents of our "views" ArrayList.
//        @Override
//        public void destroyItem (ViewGroup container, int position, Object object)
//        {
//            container.removeView (views.get (position));
//        }
//
//        //-----------------------------------------------------------------------------
//        // Used by ViewPager; can be used by app as well.
//        // Returns the total number of pages that the ViewPage can display.  This must
//        // never be 0.
//        @Override
//        public int getCount ()
//        {
//            return views.size();
//        }
//
//        //-----------------------------------------------------------------------------
//        // Used by ViewPager.
//        @Override
//        public boolean isViewFromObject (View view, Object object)
//        {
//            return view == object;
//        }
//
//        //-----------------------------------------------------------------------------
//        // Add "view" to right end of "views".
//        // Returns the position of the new view.
//        // The app should call this to add pages; not used by ViewPager.
//        public int addView (View v)
//        {
//            return addView (v, views.size());
//        }
//
//        //-----------------------------------------------------------------------------
//        // Add "view" at "position" to "views".
//        // Returns position of new view.
//        // The app should call this to add pages; not used by ViewPager.
//        public int addView (View v, int position)
//        {
//            views.add (position, v);
//            return position;
//        }
//
//        //-----------------------------------------------------------------------------
//        // Removes "view" from "views".
//        // Retuns position of removed view.
//        // The app should call this to remove pages; not used by ViewPager.
//        public int removeView (ViewPager pager, View v)
//        {
//            return removeView (pager, views.indexOf (v));
//        }
//
//        //-----------------------------------------------------------------------------
//        // Removes the "view" at "position" from "views".
//        // Retuns position of removed view.
//        // The app should call this to remove pages; not used by ViewPager.
//        public int removeView (ViewPager pager, int position)
//        {
//            // ViewPager doesn't have a delete method; the closest is to set the adapter
//            // again.  When doing so, it deletes all its views.  Then we can delete the view
//            // from from the adapter and finally set the adapter to the pager again.  Note
//            // that we set the adapter to null before removing the view from "views" - that's
//            // because while ViewPager deletes all its views, it will call destroyItem which
//            // will in turn cause a null pointer ref.
//            pager.setAdapter (null);
//            views.remove (position);
//            pager.setAdapter (this);
//
//            return position;
//        }
//
//        //-----------------------------------------------------------------------------
//        // Returns the "view" at "position".
//        // The app should call this to retrieve a view; not used by ViewPager.
//        public View getView (int position)
//        {
//            return views.get (position);
//        }
//
//        // Other relevant methods:
//
//        // finishUpdate - called by the ViewPager - we don't care about what pages the
//        // pager is displaying so we don't use this method.
//    }
//
//

//    public static class TicketDetailFragment extends DialogFragment {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            LayoutInflater inflater = getActivity().getLayoutInflater();
//
//            View convertView = (inflater.inflate(R.layout.ticket_history_detail_layout, null));
//
//            if (convertView == null)
//                convertView = inflater.inflate(R.layout.ticket_history_detail_layout, null);
//
//            TextView ticketName =  (TextView) convertView.findViewById(R.id.ticketNameTv);
//
//            TextView plate = (TextView) convertView.findViewById(R.id.plateTextView);
//
//            TextView zone =  (TextView) convertView.findViewById(R.id.zoneTextView);
//
//            TextView ticketNameTv =  (TextView) convertView.findViewById(R.id.ticketNameTextView);
//
//            TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTextView);
//
//            ListView fromTv =  convertView.findViewById(R.id.fromToListView);
//
//            LinearLayout imagesView = convertView.findViewById(R.id.imagesView);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//
//            Bundle bundle = getArguments();
//            String stringData = bundle.getString("data","");
//
//            JSONObject json_data = null;
//            try {
//                json_data = new JSONObject(stringData);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            if (null != json_data) {
//                Log.e("GGGGGG", json_data.toString());
//                String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
//                try {
//
////                    if(json_data.getJSONObject("images")!=null){
//////
////                        imagesView.removeAllViews();
////                        ArrayList<JSONArray> images = json_data.getJSONArray("images");
////                        for (int i = 0; i < images.size(); i++) {
////                            layoutParams.setMargins(20, 20, 20, 20);
////                            layoutParams.gravity = Gravity.CENTER;
////                            ImageView imageView = new ImageView(getActivity());
////                            imageView.setImageBitmap(images.get(i));
////                            imageView.setLayoutParams(layoutParams);
////
////                            imagesView.addView(imageView);
////
////                        }
////                    }
//
//
//                    ticketName.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
//                    plate.setText(json_data.getString("plate"));
//                    zone.setText(json_data.getJSONObject("zone").getString("zone_name"));
//                    ticketNameTv.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
//                    ticketNum.setText(json_data.getString("ticket_num"));
//
//                    String[] parts = json_data.getString("issued_at").split("T");
//                    String date = parts[0];
//                    String time = parts[1].substring(0, 5);
//                    List<String> fromToList = new ArrayList<>();
//
//                    fromToList.add(date);
//                    fromToList.add(time);
//
//                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                            getActivity(),
//                            android.R.layout.simple_spinner_item,
//                            fromToList);
//
//                    fromTv.setAdapter(arrayAdapter);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            builder.setView(convertView);
//
////            builder.setView(inflater.inflate(R.layout.ticket_history_detail_layout, null));
//            return builder.create();
//        }
//
////        @Override
////        public View getView(int position, View convertView, ViewGroup parent) {
////            if (convertView == null)
////                convertView = getActivity().getLayoutInflater().inflate(R.layout.ticket_history_detail_layout, null);
////
////
////            TextView ticketName =  (TextView) convertView.findViewById(R.id.ticketNameTv);
////
////            TextView plate = (TextView) convertView.findViewById(R.id.plateTextView);
////
////            TextView zone =  (TextView) convertView.findViewById(R.id.zoneTextView);
////
////            TextView ticketNameTv =  (TextView) convertView.findViewById(R.id.ticketNameTextView);
////
////            TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTextView);
////
////            ListView fromTv =  convertView.findViewById(R.id.fromToListView);
////
////            JSONObject json_data = getItem(position);
////            if (null != selectedTicket) {
////                String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
////                try {
////                    plateS = json_data.getString("plate");
////                    String[] parts = json_data.getString("issued_at").split("T"); //returns an array with the 2 parts
////                    String date = parts[0];
////                    String time = parts[1].substring(0, 5);
////                    issuedAtS = date + " " + time;
////
////                    statusS = json_data.getString("ticket_status").toUpperCase();
////                    ticketNumS = json_data.getString("ticket_num");
////                    zoneS = json_data.getJSONObject("zone").getString("zone_name");
////                    cityS = json_data.getJSONObject("city").getString("city_name");
////
////                    plate.setText(plateS);
////                    issuedAt.setText(issuedAtS);
////                    status.setText(statusS);
////                    ticketNum.setText(ticketNumS);
////                    zone.setText(zoneS);
////                    city.setText(cityS);
////
////                } catch (JSONException e) {
////                    throw new RuntimeException(e);
////                }
////
////
////            }
////
////            return convertView;
////        }
//
//
//
//    }



//public class GenericFragment extends Fragment {
//    //        public static final String ARG_OBJECT = "object";
//    PagerAdapter pagerAdapter;
//    ViewPager2 viewPager;
//
//
//    //        @Override
////        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////            TabLayout tabLayout = view.findViewById(R.id.ticketsTab);
////            new TabLayoutMediator(tabLayout, simpleViewPager,
////                    (tab, position) -> tab.setText("OBJECT " + (position + 1))
////            ).attach();
////        }
////    public GenericFragment() {
////        // Required empty public constructor
////    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//// Inflate the layout for this fragment
//        return inflater.inflate(R.layout.previous_ticket_layout, container, false);
//
////            return inflater.inflate(R.layout.previous_ticket_layout, container, false);
//    }
//    @Override
//    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
//        pagerAdapter =new PagerAdapter(this);
//        viewPager = view.findViewById(R.id.simpleViewPager);
//        viewPager.setAdapter(pagerAdapter);
//
//
//        TabLayout tabLayout = view.findViewById(R.id.ticketsTab);
//        new TabLayoutMediator(tabLayout, viewPager,
//                (tab, position) -> tab.setText("OBJECT " + (position + 1))
//        ).attach();
//
////            TabLayout tabLayout = view.findViewById(R.id.ticketsTab);
////            new TabLayoutMediator(tabLayout, simpleViewPager,
////                    (tab, position) -> tab.setText("OBJECT " + (position + 1))
////            ).attach();
//
////            View convertView = (inflater.inflate(R.layout.previous_ticket_layout, null));
////
////            if (convertView == null)
////                convertView = inflater.inflate(R.layout.previous_ticket_layout, null);
//
////            TextView ticketName =  (TextView) convertView.findViewById(R.id.ticketNameTv);
//
//    }
//
////    @Override
////    public void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////    }
//
//
//}

//    public class PagerAdapter extends FragmentStateAdapter {
////        int mNumOfTabs;
//        public PagerAdapter(Fragment fm) {
//            super(fm);
////            this.mNumOfTabs = NumOfTabs;
//        }
//
//
////        @Override
////        public Fragment getItem(int position) {
////
////            GenericFragment tab1 = new GenericFragment();
////            Bundle bundle = new Bundle();
////            bundle.putString("data",data.getTicketsIssued().toString());
//////            bundle.putString("position", Integer.toString(position));
////            tab1.setArguments(bundle);
////            return tab1;
//////                    return null;
////            }
//
////        @Override
////        public int getCount() {
////            return mNumOfTabs;
////        }
//        @NonNull
//        @Override
//        public Fragment createFragment(int position) {
//            // Return a NEW fragment instance in createFragment(int)
//            Fragment fragment = new ObjectFragment();
//            Bundle args = new Bundle();
//            // Our object is just an integer :-P
//            args.putInt(ObjectFragment.ARG_OBJECT, position + 1);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public int getItemCount() {
//            return 3;
//        }
//    }

    // Instances of this class are fragments representing a single
// object in our collection.
//    public class ObjectFragment extends Fragment {
//        public static final String ARG_OBJECT = "object";
//
//        private int mPage;
//
//
//
//        @Nullable
//        @Override
//        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                                 @Nullable Bundle savedInstanceState) {
//            return inflater.inflate(R.layout.previous_ticket_layout, container, false);
//        }
//
//        @Override
//        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////            Bundle args = getArguments();
////            ((TextView) view.findViewById(android.R.id.text1))
////                    .setText(Integer.toString(args.getInt(ARG_OBJECT)));
//
//            TextView plate = (TextView) view.findViewById(R.id.plateTextView);
//
//            TextView zone =  (TextView) view.findViewById(R.id.zoneTextView);
//
//            TextView ticketNameTv =  (TextView) view.findViewById(R.id.ticketNameTextView);
//
//            TextView ticketNum = (TextView) view.findViewById(R.id.ticketNumTextView);
//
//            ListView fromTv =  view.findViewById(R.id.fromToListView);
//
//            LinearLayout imagesView = view.findViewById(R.id.imagesView);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//            Bundle bundle = getArguments();
//            String stringData = bundle.getString("data","");
////            Integer position = Integer.parseInt(bundle.getString("postion",""));
//
//            JSONObject json_data = null;
//            try {
//                JSONArray json_array = new JSONArray(stringData);
//
//                json_data = json_array.getJSONObject(0);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            if (null != json_data) {
//                Log.e("GGGGGG", json_data.toString());
//                String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
//                try {
//
////                    if(json_data.getJSONObject("images")!=null){
//////
////                        imagesView.removeAllViews();
////                        ArrayList<JSONArray> images = json_data.getJSONArray("images");
////                        for (int i = 0; i < images.size(); i++) {
////                            layoutParams.setMargins(20, 20, 20, 20);
////                            layoutParams.gravity = Gravity.CENTER;
////                            ImageView imageView = new ImageView(getActivity());
////                            imageView.setImageBitmap(images.get(i));
////                            imageView.setLayoutParams(layoutParams);
////
////                            imagesView.addView(imageView);
////
////                        }
////                    }
//
////                    ticketName.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
//                    plate.setText(json_data.getString("plate"));
//                    zone.setText(json_data.getJSONObject("zone").getString("zone_name"));
//                    ticketNameTv.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
//                    ticketNum.setText(json_data.getString("ticket_num"));
//
//                    String[] parts = json_data.getString("issued_at").split("T");
//                    String date = parts[0];
//                    String time = parts[1].substring(0, 5);
//                    List<String> fromToList = new ArrayList<>();
//
//                    fromToList.add(date);
//                    fromToList.add(time);
//
//                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                            getActivity(),
//                            android.R.layout.simple_spinner_item,
//                            fromToList);
//
//                    fromTv.setAdapter(arrayAdapter);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }

    }



