package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private EditText quizName;
    private ListView questionsList, optionalQuestionsList;
    private Button dodajKvizBtn, importKvizbtn;
    private ArrayAdapter<Pitanje> listAdapter, optListAdapter;
    private ArrayAdapter<Kategorija> categoryAdapter;
    private ArrayList<Pitanje> pitanja = new ArrayList<>(), mogucaPitanja = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
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
        dodajKvizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dodaj kviz
                if (quizName.getText().toString().isEmpty()) {
                    quizName.setBackgroundResource(R.color.colorError);
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
        importKvizbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Import kviza

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                startActivityForResult(intent, 42);

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
        if (requestCode == 2 && resultCode == RESULT_OK) {
            // Dodaj kategoriju
            Kategorija kategorija = (Kategorija) data.getSerializableExtra("novaKategorija");
            kategorije.add(kategorije.size() - 1, kategorija);
            noveKategorije.add(kategorija);
            categorySpinner.setSelection(kategorije.size() - 2);
            categoryAdapter.notifyDataSetChanged();
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            // Dodaj pitanje
            String naziv = data.getStringExtra("pitanje");
            ArrayList<String> odgovori = (ArrayList<String>) data.getSerializableExtra("odgovori");
            String tacanOdgovor = data.getStringExtra("tacanOdgovor");
            Pitanje pitanje = new Pitanje(naziv, naziv, odgovori, tacanOdgovor);
            if (pitanja.contains(pitanje))
                return;
            pitanja.add(pitanja.size() - 1, pitanje);
            listAdapter.notifyDataSetChanged();
        }
        if (requestCode == 42 && resultCode == RESULT_OK) {
            // Importuj kviz
            Uri uri;
            if (data != null) {
                uri = data.getData();
                String[] result;
                try {
                    result = readTextFromUri(uri).split(",");
                } catch (IOException greska) {
                    greska.printStackTrace();
                    return;
                }
                if (result.length < 4) {
                    showAlert("Neispravan format datoteke");
                    return;
                }
                String nazivKviza = result[0];
                Kviz noviKviz = new Kviz();
                noviKviz.setNaziv(nazivKviza);
                if (kvizovi.contains(noviKviz)) {
                    showAlert("Kviz kojeg importujete već postoji!");
                    return;
                }
                String nazivKategorije = result[1];
                int brojOdgovora = Integer.valueOf(result[2]);
                if (brojOdgovora != result.length - 4) {
                    showAlert("Kviz kojeg importujete ima neispravan broj odgovora!");
                    return;
                }
                int indexTacOdgovora = Integer.valueOf(result[3]);
                if (indexTacOdgovora < 0 || indexTacOdgovora >= brojOdgovora) {
                    showAlert("Kviz kojeg importujete ima neispravan index tačnog odgovora!");
                    return;
                }
                quizName.setText(nazivKviza);
                Kategorija novaKategorija = new Kategorija(nazivKategorije, "0");
                if (!kategorije.contains(novaKategorija)) {
                    kategorije.add(kategorije.size() - 1, novaKategorija);
                    noveKategorije.add(novaKategorija);
                    categorySpinner.setSelection(kategorije.size() - 2);
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    int pos = kategorije.indexOf(novaKategorija);
                    categorySpinner.setSelection(pos);
                }
                Pitanje pitanje = new Pitanje();
                pitanje.setNaziv("Test pitanje");
                pitanje.setTekstPitanja("Test pitanje");
                ArrayList<String> odgovori = new ArrayList<>();
                for (int i = 4; i < result.length; i++) {
                    if (i == indexTacOdgovora)
                        pitanje.setTacan(result[i]);
                    else
                        odgovori.add(result[i]);
                }
                pitanje.setOdgovori(odgovori);
                pitanja.add(pitanja.size() - 1, pitanje);
                listAdapter.notifyDataSetChanged();
            }

        }
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // nothing?
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            stringBuilder.append(line);
        reader.close();
        return stringBuilder.toString();
    }


    private void linkControls() {
        dodajKategoriju = new Kategorija("Dodaj kategoriju", "0");
        quizName = findViewById(R.id.etNaziv);
        questionsList = findViewById(R.id.lvDodanaPitanja);
        optionalQuestionsList = findViewById(R.id.lvMogucaPitanja);
        dodajKvizBtn = findViewById(R.id.btnDodajKviz);
        importKvizbtn = findViewById(R.id.btnImportKviz);
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

        kvizovi = (ArrayList<Kviz>) intent.getSerializableExtra("kvizovi");

        categoryAdapter = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(poz);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent backIntent = new Intent();
        backIntent.putExtra("noveKategorije", noveKategorije);
        setResult(RESULT_CANCELED, backIntent);
        finish();
    }
}
