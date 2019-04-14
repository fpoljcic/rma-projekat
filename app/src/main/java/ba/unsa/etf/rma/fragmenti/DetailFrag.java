package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
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
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DetailFrag extends Fragment {
    private GridView gridView;
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kviz> prikazaniKvizovi = new ArrayList<>();
    private GridAdpater gridAdpater;
    private static final String ARG_PARAM1 = "param1";

    private OnFragmentInteractionListener callback;

    public DetailFrag() {
        // Required empty public constructor
    }

    public static DetailFrag newInstance(ArrayList<Kviz> kvizovi) {
        DetailFrag fragment = new DetailFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, kvizovi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Kviz kviz = (Kviz) data.getSerializableExtra("kviz");
                int pozicija = data.getIntExtra("pozicija", -1);
                if (kvizovi.contains(kviz) && kvizovi.indexOf(kviz) != pozicija)
                    return;
                ArrayList<Kategorija> noveKategorije = (ArrayList<Kategorija>) data.getSerializableExtra("noveKategorije");
                callback.addKategorije(noveKategorije);
                Kategorija kategorija = callback.getSelectedKategorija();
                if (pozicija == -1) {
                    kvizovi.add(kviz);
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
                /* -- Ovo je potrebno dodat radi bug-a oko ikone...
                prikazaniKvizovi.remove(null);
                gridAdpater.notifyDataSetChanged();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        prikazaniKvizovi.add(null);
                        gridAdpater.notifyDataSetChanged();
                    }
                }, 600);
                */
            } else if (resultCode == RESULT_CANCELED) {
                // Pritisnuto back dugme
                ArrayList<Kategorija> noveKategorije = (ArrayList<Kategorija>) data.getSerializableExtra("noveKategorije");
                callback.addKategorije(noveKategorije);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kvizovi = (ArrayList<Kviz>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        linkControls(view);
        setListener();
        return view;
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
        prikazaniKvizovi.add(null);
        gridView = view.findViewById(R.id.gridKvizovi);
        gridAdpater = new GridAdpater(getActivity(), prikazaniKvizovi, getResources());
        gridView.setAdapter(gridAdpater);
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

    public void filterByCategory(Kategorija kategorija) {
        prikazaniKvizovi.clear();
        if (kategorija.getNaziv().equals("Svi")) {
            prikazaniKvizovi.addAll(kvizovi);
            prikazaniKvizovi.add(null);
        } else {
            for (Kviz kviz : kvizovi) {
                if (kviz.getKategorija() != null && kviz.getKategorija().equals(kategorija))
                    prikazaniKvizovi.add(kviz);
            }
            prikazaniKvizovi.add(null);
        }
        gridAdpater.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        Kategorija getSelectedKategorija();

        ArrayList<Kategorija> getKategorije();

        void addKategorije(ArrayList<Kategorija> kategorije);
    }
}
