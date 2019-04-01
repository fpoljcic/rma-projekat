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
import ba.unsa.etf.rma.klase.Kviz;
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
    private Kviz kviz;
    private Kategorija dodajKategoriju;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz);
        linkControls();
        setListeners();
        getIntentData();
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
                kviz.setKategorija((Kategorija) categorySpinner.getSelectedItem());
                kviz.setNaziv(quizName.getText().toString());
                pitanja.remove(pitanja.size() - 1);
                kviz.setPitanja(pitanja);
                Intent myIntent = new Intent(DodajKvizAkt.this, KvizoviAkt.class);
                myIntent.putExtra("pozicija", pozicija);
                myIntent.putExtra("kviz", kviz);
                DodajKvizAkt.this.startActivity(myIntent);
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija kategorija = kategorije.get(position);
                if (kategorija == dodajKategoriju) {
                    Intent myIntent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    categorySpinner.setSelection(0);
                    DodajKvizAkt.this.startActivity(myIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing?
            }
        });
    }

    private void linkControls() {
        dodajKategoriju = new Kategorija("Dodaj kategoriju", "0");
        quizName = findViewById(R.id.etNaziv);
        questionsList = findViewById(R.id.lvDodanaPitanja);
        optionalQuestionsList = findViewById(R.id.lvMogucaPitanja);
        button = findViewById(R.id.btnDodajKviz);
        categorySpinner = findViewById(R.id.spKategorije);
    }


    private void getIntentData() {
        Intent intent = getIntent();
        kviz = (Kviz) intent.getSerializableExtra("kviz");
        if (kviz != null && kviz.getNaziv() != null) {
            pozicija = intent.getIntExtra("pozicija", -1);
            quizName.setText(kviz.getNaziv());
            pitanja = kviz.getPitanja();
        }
        Kategorija kategorija = (Kategorija) intent.getSerializableExtra("novaKategorija");
        if (kategorija != null) {
            // Dodavanje kategorije
            kategorije.add(kategorija);
            return;
        }
        pitanja.add(null);
        listAdapter = new ListAdapter(this, pitanja, getResources(), Pitanje.class);
        questionsList.setAdapter(listAdapter);

        mogucaPitanja = (ArrayList<Pitanje>) intent.getSerializableExtra("mogucaPitanja");
        optListAdapter = new ListAdapter(this, mogucaPitanja, getResources(), Pitanje.class);
        optionalQuestionsList.setAdapter(optListAdapter);

        int layoutID = android.R.layout.simple_list_item_1;
        kategorije = (ArrayList<Kategorija>) intent.getSerializableExtra("kategorija");
        kategorije.remove(0);
        kategorije.add(dodajKategoriju);
        int poz = kategorije.indexOf(kviz.getKategorija());
        ArrayAdapter<Kategorija> adapterKat = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(adapterKat);
        categorySpinner.setSelection(poz);
    }
}
