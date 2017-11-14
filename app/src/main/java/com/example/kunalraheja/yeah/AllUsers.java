package com.example.kunalraheja.yeah;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class AllUsers extends AppCompatActivity {
    ListView lv;
    UserAdapter adapter;
    ArrayList<UserAdapterItems> list ;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);



        lv=(ListView)findViewById(R.id.listView);
//        searchView=(SearchView)findViewById(R.id.searchView);

        list=new ArrayList<UserAdapterItems>();
        //check



        reference = FirebaseDatabase.getInstance().getReference();
        adapter=  new UserAdapter(this , list);
        lv.setAdapter(adapter);




      //  String search =searchView.getT
        Query query = reference.child("Users");//.limitToFirst(3);
      //  query.limitToFirst(1);
        query.addChildEventListener(cli);


    }

    ChildEventListener cli = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            DatabaseReference friend=FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");
            UserDetail u = dataSnapshot.getValue(UserDetail.class);
            String uid = dataSnapshot.getKey();
            if(friend.child(uid)!=null) {
                String n = u.getuName();
                String em = u.getEmail();
                String url = u.getPhoto_url();
                boolean f = true;
                UserAdapterItems user = new UserAdapterItems(n, em, url, f, uid);
                list.add(user);
                adapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };



}
