package com.example.ankit.studentzoneassienment.Assienment1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ankit.studentzoneassienment.Assienment1.PGDetails;
import com.example.ankit.studentzoneassienment.R;

import java.util.ArrayList;

public class CustomListAdaptor extends ArrayAdapter {

    Context _context;
    ArrayList<PGDetails> pg;
    int resource;


    public CustomListAdaptor(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        this._context = context;
        this.pg = objects;
        this.resource = resource;
    }

    public int getCount() {
        return pg.size();
    }

    public PGDetails getItem(int i) {
        return pg.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View v, ViewGroup viewGroup) {
        final PGDetails details = pg.get(i);
        View view;
        LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = infalInflater.inflate( this.resource,null,true);

        TextView name = view.findViewById(R.id.t1);
        ImageView image  = view.findViewById(R.id.i1);
        TextView address = view.findViewById(R.id.t2);
        Button call = view.findViewById(R.id.b2);
        Button direction = view.findViewById(R.id.b3);

        name.setText(details.getName());
        if(details.getGender().equals("Male"))
        {
            image.setImageDrawable(this._context.getResources().getDrawable(R.drawable.bb));
        }else
            image.setImageDrawable(this._context.getResources().getDrawable(R.drawable.g));

        address.setText(details.getAddress()+"\n Avilable Sharing :"+details.getAddress()+"\n Starting from RS "+details.getRent());
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String posted_by = details.getContact_no();
                String uri = "tel:" + posted_by.trim() ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                _context.startActivity(intent);
            }
        });
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Direction",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
