package com.obarey.jeppe_pc.gitasfilotakipmobil;


import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTakipIYS extends Fragment {

    private boolean data_downloaded = false;
    private JSONArray last_data = new JSONArray();
    private String oto;
    private ProgressDialog loader;
    private ListView lw;
    private int data_index = 0, rrp = 10;
    private IYSListViewAdapter lw_adapter;

    public FragmentTakipIYS() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_fragment_takip_iy, container, false);

        Button btn_download_daha_eski = (Button)layout.findViewById(R.id.btn_download_daha_eski);
        lw = (ListView)layout.findViewById(R.id.mesajlar_listview);
        ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
        TextView header = (TextView)layout.findViewById(R.id.otobus_takip_fragment_header);
        oto = activity_ref.get_otobus_box_data().get_oto();
        header.setText( oto );
        if( !data_downloaded ){
            lw_adapter = new IYSListViewAdapter( activity_ref );
            data_download( new NoArgumentCallBack() {
                @Override
                public void action() {
                    lw_adapter.set_data( last_data );
                    lw.setAdapter( lw_adapter );
                }
            });
            data_downloaded = true;
        } else {
            lw.setAdapter( lw_adapter );
        }


        btn_download_daha_eski.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data_index += rrp;
                data_download(new NoArgumentCallBack() {
                    @Override
                    public void action() {
                        lw_adapter.add_data( last_data );
                        lw_adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        return layout;
    }

    private void data_download( final NoArgumentCallBack cb ){
        loader = ProgressDialog.show(getActivity(), "Lütfen bekleyin...", "Veri alınıyor..", true);
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                WebRequest req = new WebRequest();
                try {
                    last_data = req.req(WebRequest.MOBIL_SERVIS_URL, "req=iys_download_index_mod&oto="+oto+"&start="+data_index+"&end="+(data_index+rrp)).getJSONObject("data").getJSONArray("iys_data");
                    final ActivityOtobusTakip activity_ref = (ActivityOtobusTakip)getActivity();
                    activity_ref.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cb.action();
                            loader.dismiss();
                        }
                    });

                } catch( JSONException e ){
                    e.printStackTrace();
                }
            }
        });
        th.setDaemon(true);
        th.start();
    }

}

