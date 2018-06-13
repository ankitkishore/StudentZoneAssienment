package com.example.ankit.studentzoneassienment.Assienment1;


import android.util.Log;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;


public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved;
    /*
 PASS DATABASE REFRENCE
  */
    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }
    //WRITE IF NOT NULL
    public Boolean save(PGDetails pg)
    {
        if(pg==null)
        {
            saved=false;
        }else
        {
            try
            {
                Log.i("yo",pg.getCity());
                db.child(pg.getCity()).push().setValue(pg);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
}