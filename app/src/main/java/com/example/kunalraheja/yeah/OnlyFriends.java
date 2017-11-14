package com.example.kunalraheja.yeah;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OnlyFriends extends AppCompatActivity {

    ListView lv;
    FriendAdapter adapter;
    ArrayList<UserAdapterItems> list;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_friends);



        lv=(ListView)findViewById(R.id.only_friends_list_view);
        list=new ArrayList<UserAdapterItems>();
        reference = FirebaseDatabase.getInstance().getReference();
        adapter=  new FriendAdapter(this , list);
        lv.setAdapter(adapter);

        Query query = reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");

        query.addChildEventListener(cli);
        query.orderByChild("uName");

    }

    ChildEventListener cli = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

//            String friendUid=dataSnapshot.getKey();
//            DatabaseReference toFriend=FirebaseDatabase.getInstance().getReference().child("Users").child(friendUid);
//            toFriend.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
           // UserDetail u = dataSnapshot.getValue(UserDetail.class);
            final String uid = dataSnapshot.getKey();
            DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserDetail u1 = dataSnapshot.getValue(UserDetail.class);
                    String n =u1.getuName();
                    String em=u1.getEmail();
                    String url = u1.getPhoto_url();
                    boolean f = true;
                    UserAdapterItems user = new UserAdapterItems(n,em,url,f,uid);
                    list.add(user);
                    adapter.notifyDataSetChanged();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



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
