package com.example.jeppe_pc.gitasmobil;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class FragmentTakipOrer extends Fragment {

    private ProgressDialog loader;
    private ListView seferler_lw;
    public FragmentTakipOrer() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_fragment_takip_orer, container, false);
        //loader = ProgressDialog.show(getActivity(), "Lütfen bekleyin...", "Arayüz oluşturuluyor.", true);

        seferler_lw = (ListView)layout.findViewById(R.id.seferler_listview);
        ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
        seferler_lw.setAdapter(new SeferTableListViewAdapter( activity_ref, activity_ref.get_otobus_box_data().get_seferler() ));


        return layout;
    }

}
