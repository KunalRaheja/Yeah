package com.example.kunalraheja.yeah;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Kunal Raheja on 14-08-2016.
 */
public class NotificationTab1 extends Fragment {

    ListView lv;
    Button bt;
    RequestAdapter adapter;
    ArrayList<RequestAdapterItems>list;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.notifivation_tab1, container, false);

        list=new ArrayList<RequestAdapterItems>();
        adapter=new RequestAdapter(getContext(),list);
        lv = (ListView)v.findViewById(R.id.nt1_listview);
        lv.setAdapter(adapter);
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());




        Query query =reference.child("requestReceived");
        query.addChildEventListener(requests);





        return  v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    ChildEventListener requests=new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            final String uid = dataSnapshot.getKey();
            DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserDetail u1 = dataSnapshot.getValue(UserDetail.class);
                    String n =u1.getuName();
                    String url = u1.getPhoto_url();
                    RequestAdapterItems req = new RequestAdapterItems(n,url, uid);
                    list.add(req);
                    adapter.notifyDataSetChanged();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
