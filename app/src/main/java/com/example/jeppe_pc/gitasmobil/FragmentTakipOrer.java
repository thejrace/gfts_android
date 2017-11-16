package com.example.jeppe_pc.gitasmobil;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentTakipOrer extends Fragment {

    private ProgressDialog loader;
    public FragmentTakipOrer() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_fragment_takip_orer, container, false);
        //loader = ProgressDialog.show(getActivity(), "Lütfen bekleyin...", "Arayüz oluşturuluyor.", true);




        return layout;
    }

}
