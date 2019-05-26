package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.adapteri.GridAdpater;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DetailFrag extends Fragment implements Firebase.KvizInterface {
    private GridView gridView;
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kviz> prikazaniKvizovi = new ArrayList<>();
    private GridAdpater gridAdpater;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener callback;

    public DetailFrag() {
        // Required empty public constructor
    }

    public static DetailFrag newInstance(ArrayList<Kviz> kvizovi, ArrayList<Kviz> prikazaniKvizovi) {
        DetailFrag fragment = new DetailFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, kvizovi);
        args.putSerializable(ARG_PARAM2, prikazaniKvizovi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kvizovi = (ArrayList<Kviz>) getArguments().getSerializable(ARG_PARAM1);
            prikazaniKvizovi = (ArrayList<Kviz>) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        linkControls(view);
        setListener();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Kviz kviz = (Kviz) data.getSerializableExtra("kviz");
                ArrayList<String> idPitanja = data.getStringArrayListExtra("idPitanja");
                int pozicija = data.getIntExtra("pozicija", -1);
                ArrayList<Kategorija> noveKategorije = (ArrayList<Kategorija>) data.getSerializableExtra("noveKategorije");
                callback.addKategorije(noveKategorije);
                Kategorija kategorija = callback.getSelectedKategorija();
                if (pozicija == -1) {
                    kvizovi.add(kviz);
                    Firebase.dodajKviz(kviz, idPitanja);
                    if (kategorija.getNaziv().equals("Svi") || (kviz.getKategorija() != null && kviz.getKategorija().equals(kategorija)))
                        prikazaniKvizovi.add(prikazaniKvizovi.size() - 1, kviz);
                } else {
                    Kviz postojeciKviz = prikazaniKvizovi.get(pozicija);
                    int pos = kvizovi.indexOf(postojeciKviz);
                    postojeciKviz.setNaziv(kviz.getNaziv());
                    postojeciKviz.setPitanja(kviz.getPitanja());
                    postojeciKviz.setKategorija(kviz.getKategorija());
                    kvizovi.get(pos).setNaziv(kviz.getNaziv());
                    kvizovi.get(pos).setPitanja(kviz.getPitanja());
                    kvizovi.get(pos).setKategorija(kviz.getKategorija());
                    if (!kategorija.getNaziv().equals("Svi") && (postojeciKviz.getKategorija() == null || !postojeciKviz.getKategorija().equals(kategorija)))
                        prikazaniKvizovi.remove(postojeciKviz);
                }
                gridAdpater.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                // Pritisnuto back dugme
                ArrayList<Kategorija> noveKategorije = (ArrayList<Kategorija>) data.getSerializableExtra("noveKategorije");
                callback.addKategorije(noveKategorije);
            }
        }
    }

    private void setListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                igrajKviz(position);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                urediKviz(position);
                return true;
            }
        });
    }

    private void igrajKviz(int position) {
        if (prikazaniKvizovi.get(position) != null) {
            Intent myIntent = new Intent(getActivity(), IgrajKvizAkt.class);
            myIntent.putExtra("kviz", prikazaniKvizovi.get(position));
            startActivityForResult(myIntent, 2);
        }
    }

    private void urediKviz(int position) {
        Intent myIntent = new Intent(getActivity(), DodajKvizAkt.class);
        if (prikazaniKvizovi.get(position) != null) {
            myIntent.putExtra("pozicija", position);
            myIntent.putExtra("kviz", prikazaniKvizovi.get(position));
        } else {
            Kviz kviz = new Kviz();
            kviz.setKategorija(callback.getSelectedKategorija());
            myIntent.putExtra("kviz", kviz);
        }
        myIntent.putExtra("kategorija", callback.getKategorije());
        myIntent.putExtra("kvizovi", kvizovi);
        startActivityForResult(myIntent, 1);
    }

    private void linkControls(View view) {
        gridView = view.findViewById(R.id.gridKvizovi);
        int elementWidth = (int) (getScreenWidth() * 0.65) / 500;
        // Za jedan element gridView-a treba sirina od 500px
        gridView.setNumColumns(elementWidth);

        gridAdpater = new GridAdpater(getActivity(), prikazaniKvizovi, getResources());
        gridView.setAdapter(gridAdpater);
    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            callback = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public ArrayList<Kviz> getKvizovi() {
        return kvizovi;
    }

    @Override
    public void addKvizovi(ArrayList<Kviz> kvizovi) {
        for (Kviz kviz : kvizovi) {
            if (!this.kvizovi.contains(kviz)) {
                this.kvizovi.add(kviz);
                if (callback.getSelectedKategorija().getNaziv().equals("Svi") || kviz.getKategorija() != null && callback.getSelectedKategorija().equals(kviz.getKategorija()))
                    prikazaniKvizovi.add(0, kviz);
            }
        }
        gridAdpater.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        Kategorija getSelectedKategorija();

        ArrayList<Kategorija> getKategorije();

        void addKategorije(ArrayList<Kategorija> kategorije);
    }
}
