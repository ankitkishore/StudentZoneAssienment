package com.example.ankit.studentzoneassienment.Assienment1;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ankit.studentzoneassienment.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference db;
    Spinner s1,s2;
    ArrayAdapter<CharSequence> a1,a2;
    Button b1;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s1= findViewById(R.id.s1);
        s2= findViewById(R.id.s2);
        b1 = findViewById(R.id.b1);
        a1 = ArrayAdapter.createFromResource(this, R.array.Lotaion, android.R.layout.simple_spinner_item);
        a1 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(a1);
        fab = findViewById(R.id.fab);

        db = FirebaseDatabase.getInstance().getReference();

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                    spinner1();
                else if(position == 1)
                    spinner2();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = String.valueOf(s2.getSelectedItem());
                if (s.equals("SR_nagar")) {
                    Intent i = new Intent(MainActivity.this, Srnagar.class);
                    i.putExtra("city",String.valueOf(s1.getSelectedItem()));
                    startActivity(i);
                }else if(s.equals("Ameerpet"))
                {
                    Intent i = new Intent(MainActivity.this, Ameerpet_pg.class);
                    i.putExtra("city",String.valueOf(s1.getSelectedItem()));
                    startActivity(i);
                }
                else if(s.equals("HSR_layout"))
                {
                    Intent i = new Intent(MainActivity.this, Hsr.class);
                    i.putExtra("city",String.valueOf(s1.getSelectedItem()));
                    startActivity(i);
                }
                else if(s.equals("Tinfactory"))
                {
                    Intent i = new Intent(MainActivity.this, Tinfactory_pg.class);
                    i.putExtra("city",String.valueOf(s1.getSelectedItem()));
                    startActivity(i);
                }

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
            }
        });

    }
    public void spinner1()
    {
        a2 = ArrayAdapter.createFromResource(this, R.array.Bangalore_localities, android.R.layout.simple_spinner_item);
        a2 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(a2);

    }


    public void spinner2()
    {
        a2 = ArrayAdapter.createFromResource(this, R.array.Hyderabad_localities, android.R.layout.simple_spinner_item);
        a2 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(a2);
    }


    private void displayDialog()
    {
        final Dialog d= new Dialog(MainActivity.this);
        d.setTitle("Save Data in firebase");
        d.setContentView(R.layout.customdialog_layout);
        final EditText name = d.findViewById(R.id.pg_name);
        final EditText address = d.findViewById(R.id.address);
        final EditText sharing = d.findViewById(R.id.sharing);
        final EditText rent = d.findViewById(R.id.rent);
        final EditText contact_no = d.findViewById(R.id.contact_no);
        final Spinner gender = d.findViewById(R.id.gender);
        final Spinner s1= d.findViewById(R.id.s1);
        final Spinner s2= d.findViewById(R.id.s2);
        a1 = ArrayAdapter.createFromResource(this, R.array.Lotaion, android.R.layout.simple_spinner_item);
        a1 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(a1);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    a2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.Bangalore_localities, android.R.layout.simple_spinner_item);
                    a2 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s2.setAdapter(a2);}
                else if(position == 1)
                {
                    a2 = ArrayAdapter.createFromResource(MainActivity.this, R.array.Hyderabad_localities, android.R.layout.simple_spinner_item);
                    a2 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s2.setAdapter(a2);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> categories = new ArrayList<>();
        categories.add("Male");
        categories.add("Female");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(dataAdapter);
        final DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();

        Button btnsave= d.findViewById(R.id.b1);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PGDetails s = new PGDetails();
                s.setName(name.getText().toString());
                s.setAddress(address.getText().toString());
                s.setContact_no(contact_no.getText().toString());
                s.setRent(rent.getText().toString());
                s.setSharing(sharing.getText().toString());
                s.setGender(String.valueOf(gender.getSelectedItem()));
                s.setCity(String.valueOf(s1.getSelectedItem()));
                s.setLocation(String.valueOf(s2.getSelectedItem()));
                FirebaseHelper helper = new FirebaseHelper(db1);
                if (helper.save(s)) {
                    //IF SAVED CLEAR EDITXT
                    Log.i("yo",s.getCity());
                    name.setText("");
                    address.setText("");
                    contact_no.setText("");
                    rent.setText("");
                    sharing.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Name Must Not Be Empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        d.show();
    }

}
