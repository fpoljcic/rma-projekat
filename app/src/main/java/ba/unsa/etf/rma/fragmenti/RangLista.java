package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class RangLista extends Fragment {
    private ListView listaIgraca;
    private static final String ARG_PARAM1 = "param1";
    private ArrayList<String> igraci = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public RangLista() {
        // Required empty public constructor
    }

    public static RangLista newInstance(ArrayList<String> igraci) {
        RangLista fragment = new RangLista();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, igraci);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            igraci = (ArrayList<String>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rang_lista, container, false);
        linkControls(view);
        setData(view);
        return view;
    }

    private void setData(View view) {
        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, igraci);
        listaIgraca.setAdapter(adapter);
    }

    private void linkControls(View view) {
        listaIgraca = view.findViewById(R.id.listaIgraca);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
