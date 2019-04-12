package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ba.unsa.etf.rma.R;

public class InformacijeFrag extends Fragment {
    private TextView nazivKvizaField, brojTacnihPitanjaField;
    private TextView brPreostalihPitanjaField, procenatTacnihField;
    private Button button;
    private String nazivKviza;
    private int brojPitanja;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener callback;

    public InformacijeFrag() {
        // Required empty public constructor
    }

    public static InformacijeFrag newInstance(String nazivKviza, int brojPitanja) {
        InformacijeFrag fragment = new InformacijeFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, nazivKviza);
        args.putInt(ARG_PARAM2, brojPitanja);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nazivKviza = getArguments().getString(ARG_PARAM1);
            brojPitanja = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_informacije, container, false);
        linkControls(view);
        setData();
        setListener();
        return view;
    }

    private void setListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Zavrsi kviz
                callback.onEndQuizButtonPressed();
            }
        });
    }

    private void setData() {
        nazivKvizaField.setText(nazivKviza);
        brojTacnihPitanjaField.setText("0");
        brPreostalihPitanjaField.setText(String.valueOf(brojPitanja));
        procenatTacnihField.setText("0");
    }

    private void linkControls(View view) {
        nazivKvizaField = view.findViewById(R.id.infNazivKviza);
        brojTacnihPitanjaField = view.findViewById(R.id.infBrojTacnihPitanja);
        brPreostalihPitanjaField = view.findViewById(R.id.infBrojPreostalihPitanja);
        procenatTacnihField = view.findViewById(R.id.infProcenatTacni);
        button = view.findViewById(R.id.btnKraj);
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

    public boolean updateData(boolean correct) {
        brPreostalihPitanjaField.setText(String.valueOf(Integer.valueOf(brPreostalihPitanjaField.getText().toString()) - 1));
        if (correct)
            brojTacnihPitanjaField.setText(String.valueOf(Integer.valueOf(brojTacnihPitanjaField.getText().toString()) + 1));
        procenatTacnihField.setText(String.valueOf(Float.valueOf(brojTacnihPitanjaField.getText().toString()) / brojPitanja));
        return Integer.valueOf(brPreostalihPitanjaField.getText().toString()) == 0;
    }

    public interface OnFragmentInteractionListener {
        void onEndQuizButtonPressed();
    }
}
