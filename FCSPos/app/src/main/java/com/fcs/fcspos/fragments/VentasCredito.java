package com.fcs.fcspos.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcs.fcspos.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class VentasCredito extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener{

    private FloatingActionButton fab;
    private AlertDialog.Builder builder;

    public VentasCredito() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas_credito,container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonI);
        fab.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Metodo de Identificación");
        builder.setMessage("Escoja Metodo de Identificacion");

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_metodo_id, null);

        builder.setView(dialogView);
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("Cancel", this);
        builder.show();

    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(i == DialogInterface.BUTTON_POSITIVE){
            dialogInterface.cancel();
        }else{
            dialogInterface.cancel();
        }
    }

}
