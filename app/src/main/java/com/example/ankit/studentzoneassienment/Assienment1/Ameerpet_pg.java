package com.example.ankit.studentzoneassienment.Assienment1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ankit.studentzoneassienment.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Ameerpet_pg extends AppCompatActivity {

    DatabaseReference db;
    private ListView list;

    ArrayList<PGDetails> pg= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ameerpet_pg);

        db = FirebaseDatabase.getInstance().getReference();

        list = findViewById(R.id.list);


        String city = getIntent().getStringExtra("city");
        Toast.makeText(this,city,Toast.LENGTH_SHORT).show();
        final CustomListAdaptor adptor = new CustomListAdaptor(Ameerpet_pg.this, R.layout.list_view,pg);
        list.setAdapter(adptor);


        db.child(city).orderByChild("location").equalTo("Ameerpet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    PGDetails details = data.getValue(PGDetails.class);
                    pg.add(details);
                    adptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
