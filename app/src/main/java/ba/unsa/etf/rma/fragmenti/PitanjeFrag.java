package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

public class PitanjeFrag extends Fragment {
    private TextView tekstPitanjaField;
    private ListView listaOdgovora;
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int pozicijaPitanja = 0;
    private ArrayList<String> odgovori = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private OnFragmentInteractionListener callback;

    public PitanjeFrag() {
        // Required empty public constructor
    }

    public static PitanjeFrag newInstance(ArrayList<Pitanje> pitanja) {
        PitanjeFrag fragment = new PitanjeFrag();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, pitanja);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pitanja = (ArrayList<Pitanje>) getArguments().getSerializable(ARG_PARAM1);
            Collections.shuffle(pitanja);
        }
    }

    private void setData(View view) {
        tekstPitanjaField.setText(pitanja.get(pozicijaPitanja).getTekstPitanja());
        odgovori.addAll(pitanja.get(pozicijaPitanja).getOdgovori());
        Collections.shuffle(odgovori);
        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, odgovori);
        listaOdgovora.setAdapter(adapter);
    }

    private void linkControls(View view) {
        tekstPitanjaField = view.findViewById(R.id.tekstPitanja);
        listaOdgovora = view.findViewById(R.id.odgovoriPitanja);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pitanje, container, false);
        linkControls(view);
        setData(view);
        setListener();
        return view;
    }

    private void setListener() {
        listaOdgovora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int pozicijaTacnog = odgovori.indexOf(pitanja.get(pozicijaPitanja).getTacan());

                final TextView rightText = (TextView) listaOdgovora.getChildAt(pozicijaTacnog);
                final TextView selectedText = view.findViewById(android.R.id.text1);

                if (position == pozicijaTacnog) {
                    // Izabran tacan odgovor
                    selectedText.setBackgroundResource(R.color.zelena);
                } else {
                    // Izabran pogresan odgovor
                    selectedText.setBackgroundResource(R.color.crvena);
                    rightText.setBackgroundResource(R.color.zelena);
                }
                final boolean quizEnd = callback.onAnswerListItemClick(position == pozicijaTacnog);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (selectedText != rightText)
                            rightText.setBackgroundResource(R.color.colorDefaultBackground);
                        selectedText.setBackgroundResource(R.color.colorDefaultBackground);
                        if (quizEnd) {
                            tekstPitanjaField.setText("Kviz je završen!");
                            odgovori.clear();
                        } else {
                            pozicijaPitanja++;
                            tekstPitanjaField.setText(pitanja.get(pozicijaPitanja).getTekstPitanja());
                            odgovori.clear();
                            odgovori.addAll(pitanja.get(pozicijaPitanja).getOdgovori());
                            Collections.shuffle(odgovori);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });
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

    public interface OnFragmentInteractionListener {
        boolean onAnswerListItemClick(boolean correct);
    }
}
