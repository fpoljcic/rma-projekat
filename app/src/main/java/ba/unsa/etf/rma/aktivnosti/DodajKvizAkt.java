package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
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
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private EditText quizName;
    private ListView questionsList, optionalQuestionsList;
    private Button button;
    private ArrayAdapter<Pitanje> listAdapter, optListAdapter;
    private ArrayAdapter<Kategorija> categoryAdapter;
    private ArrayList<Pitanje> pitanja = new ArrayList<>(), mogucaPitanja = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ArrayList<Kategorija> noveKategorije = new ArrayList<>();
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
                    startActivityForResult(myIntent, 3);
                } else {
                    mogucaPitanja.add(pitanja.get(position));
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
                if (quizName.getText().toString().isEmpty()) {
                    quizName.setBackgroundColor(Color.parseColor("#FFCCCC"));
                    return;
                }
                if (((Kategorija) categorySpinner.getSelectedItem()).getNaziv().equals("Svi"))
                    kviz.setKategorija(null);
                else
                    kviz.setKategorija((Kategorija) categorySpinner.getSelectedItem());
                kviz.setNaziv(quizName.getText().toString());
                pitanja.remove(pitanja.size() - 1);
                kviz.setPitanja(pitanja);
                Intent replyIntent = new Intent();
                replyIntent.putExtra("pozicija", pozicija);
                replyIntent.putExtra("kviz", kviz);
                kategorije.remove(kategorije.size() - 1);
                replyIntent.putExtra("noveKategorije", noveKategorije);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija kategorija = kategorije.get(position);
                if (kategorija == dodajKategoriju) {
                    Intent myIntent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    myIntent.putExtra("kategorije", kategorije);
                    categorySpinner.setSelection(0);
                    startActivityForResult(myIntent, 2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing?
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            // Dodaj kategoriju
            if (resultCode == RESULT_OK) {
                Kategorija kategorija = (Kategorija) data.getSerializableExtra("novaKategorija");
                kategorije.add(kategorije.size() - 1, kategorija);
                noveKategorije.add(kategorija);
                categorySpinner.setSelection(kategorije.size() - 2);
                categoryAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 3) {
            // Dodaj pitanje
            if (resultCode == RESULT_OK) {
                String naziv = data.getStringExtra("pitanje");
                ArrayList<String> odgovori = (ArrayList<String>) data.getSerializableExtra("odgovori");
                String tacanOdgovor = data.getStringExtra("tacanOdgovor");
                Pitanje pitanje = new Pitanje(naziv, naziv, odgovori, tacanOdgovor);
                if (pitanja.contains(pitanje))
                    return;
                pitanja.add(pitanja.size() - 1, pitanje);
                listAdapter.notifyDataSetChanged();
            }
        }
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
        pitanja.add(new Pitanje());

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pitanja);
        questionsList.setAdapter(listAdapter);

        optListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mogucaPitanja);
        optionalQuestionsList.setAdapter(optListAdapter);

        int layoutID = android.R.layout.simple_list_item_1;
        kategorije = (ArrayList<Kategorija>) intent.getSerializableExtra("kategorija");
        kategorije.add(dodajKategoriju);
        int poz = kategorije.indexOf(kviz.getKategorija());
        categoryAdapter = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(poz);
    }


}
