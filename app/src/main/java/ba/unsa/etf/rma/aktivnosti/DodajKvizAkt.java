package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.ListAdapter;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private EditText quizName;
    private ListView questionsList, optionalQuestionsList;
    private Button button;
    private ListAdapter listAdapter, optListAdapter;
    private ArrayList<Pitanje> pitanja = new ArrayList<>(), mogucaPitanja = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private int pozicija = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz);
        linkControls();
        setListeners();
    }

    private void setListeners() {
        questionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == pitanja.size() - 1) {
                    // Dodaj pitanje
                    Intent myIntent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    DodajKvizAkt.this.startActivity(myIntent);
                } else {
                    mogucaPitanja.add(mogucaPitanja.size() - 1, pitanja.get(position));
                    pitanja.remove(position);
                    listAdapter.notifyDataSetChanged();
                    optListAdapter.notifyDataSetChanged();
                }
            }
        });
        optionalQuestionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pitanja.add(pitanja.size() - 1, mogucaPitanja.get(position));
                mogucaPitanja.remove(position);
                listAdapter.notifyDataSetChanged();
                optListAdapter.notifyDataSetChanged();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dodaj kviz
                Intent myIntent = new Intent(DodajKvizAkt.this, KvizoviAkt.class);
                myIntent.putExtra("pozicija", pozicija);
                myIntent.putExtra("kategorija", (Kategorija) categorySpinner.getSelectedItem());
                myIntent.putExtra("nazivKviza", quizName.getText().toString());
                pitanja.remove(pitanja.size() - 1);
                myIntent.putExtra("pitanja", pitanja);
                DodajKvizAkt.this.startActivity(myIntent);
            }
        });
    }

    private void linkControls() {
        Intent intent = getIntent();
        quizName = findViewById(R.id.etNaziv);
        questionsList = findViewById(R.id.lvDodanaPitanja);
        optionalQuestionsList = findViewById(R.id.lvMogucaPitanja);
        button = findViewById(R.id.btnDodajKviz);
        String naziv = intent.getStringExtra("naziv");
        if (naziv != null) {
            pozicija = intent.getIntExtra("pozicija", -1);
            quizName.setText(naziv);
            pitanja = (ArrayList<Pitanje>) intent.getSerializableExtra("pitanja");
        }
        pitanja.add(null);
        listAdapter = new ListAdapter(this, pitanja, getResources(), Pitanje.class);
        questionsList.setAdapter(listAdapter);
        mogucaPitanja = (ArrayList<Pitanje>) intent.getSerializableExtra("mogucaPitanja");
        optListAdapter = new ListAdapter(this, mogucaPitanja, getResources(), Pitanje.class);
        optionalQuestionsList.setAdapter(optListAdapter);
        categorySpinner = findViewById(R.id.spKategorije);
        int layoutID = android.R.layout.simple_list_item_1;
        kategorije = (ArrayList<Kategorija>) intent.getSerializableExtra("kategorija");
        ArrayAdapter<Kategorija> adapterKat = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(adapterKat);
    }
}
