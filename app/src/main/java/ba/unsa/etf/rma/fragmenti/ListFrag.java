package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kategorija;

public class ListFrag extends Fragment implements Firebase.KategorijaInterface {
    private ListView listaKategorija;
    private ArrayList<Kategorija> kategorije;
    private ArrayAdapter<Kategorija> adapter;
    private int indexKategorije;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener callback;

    public ListFrag() {
        // Required empty public constructor
    }

    public static ListFrag newInstance(ArrayList<Kategorija> kategorije, int position) {
        ListFrag fragment = new ListFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, kategorije);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }

    public ArrayList<Kategorija> getKategorije() {
        return kategorije;
    }

    public void addKategorije(ArrayList<Kategorija> kategorije) {
        this.kategorije.addAll(kategorije);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kategorije = (ArrayList<Kategorija>) getArguments().getSerializable(ARG_PARAM1);
            indexKategorije = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        linkControls(view);
        setData(view);
        setListener();
        listaKategorija.setSelection(indexKategorije);
        return view;
    }

    private void setListener() {
        final ListFrag listFrag = this;
        listaKategorija.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                callback.onKategorijaClick(kategorije.get(position));
                listFrag.indexKategorije = position;
            }
        });
    }

    private void setData(View view) {
        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, kategorije);
        listaKategorija.setAdapter(adapter);
    }

    private void linkControls(View view) {
        listaKategorija = view.findViewById(R.id.listaKategorija);
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

    public Kategorija getSelectedKategorija() {
        return kategorije.get(indexKategorije);
    }

    public int getIndexKategorije() {
        return indexKategorije;
    }

    @Override
    public void addKategorijeFirebase(ArrayList<Kategorija> kategorije) {
        for (Kategorija kategorija : kategorije) {
            if (!this.kategorije.contains(kategorija))
                this.kategorije.add(kategorija);
        }
        adapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        void onKategorijaClick(Kategorija kategorija);
    }
}
