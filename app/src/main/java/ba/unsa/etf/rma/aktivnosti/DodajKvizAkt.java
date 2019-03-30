package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private EditText quizName;
    private ListView questionsList, optionalQuestionsList;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz);
        linkControls();
    }

    private void linkControls() {
        Intent intent = getIntent();
        String naziv = intent.getStringExtra("naziv");
        if (naziv != null) {
            categorySpinner = (Spinner) findViewById(R.id.spKategorije);
            int layoutID = android.R.layout.simple_list_item_1;
            ArrayList<String> kategorije = new ArrayList<>();
            kategorije.add(intent.getStringExtra("kategorija"));
            ArrayAdapter<String> adapterKat = new ArrayAdapter<>(this, layoutID, kategorije);
            categorySpinner.setAdapter(adapterKat);
            quizName = (EditText) findViewById(R.id.etNaziv);
            quizName.setText(naziv);
            questionsList = (ListView) findViewById(R.id.lvDodanaPitanja);
            ArrayList<Pitanje> pitanja = (ArrayList<Pitanje>) intent.getSerializableExtra("pitanja");
            ArrayAdapter<Pitanje> adapter = new ArrayAdapter<>(this, layoutID, pitanja);
            questionsList.setAdapter(adapter);
            optionalQuestionsList = (ListView) findViewById(R.id.lvMogucaPitanja);
            button = (Button) findViewById(R.id.btnDodajKviz);
        }
    }
}
