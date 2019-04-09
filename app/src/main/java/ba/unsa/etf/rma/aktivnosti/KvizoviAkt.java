package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.ListAdapter;

public class KvizoviAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private ListView quizList;
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kviz> prikazaniKvizovi = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ArrayAdapter<Kategorija> categoryAdapter;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkControls();
        filterByCategory(kategorije.get(0));
        setListeners();
    }

    private void setListeners() {
        quizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                if (prikazaniKvizovi.get(position) != null) {
                    myIntent.putExtra("pozicija", position);
                    myIntent.putExtra("kviz", prikazaniKvizovi.get(position));
                } else {
                    Kviz kviz = new Kviz();
                    kviz.setKategorija((Kategorija) categorySpinner.getSelectedItem());
                    myIntent.putExtra("kviz", kviz);
                }
                myIntent.putExtra("kategorija", kategorije);
                myIntent.putExtra("kvizovi", kvizovi);
                startActivityForResult(myIntent, 1);
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija kategorija = kategorije.get(position);
                filterByCategory(kategorija);
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
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Kviz kviz = (Kviz) data.getSerializableExtra("kviz");
                int pozicija = data.getIntExtra("pozicija", -1);
                if (kvizovi.contains(kviz) && kvizovi.indexOf(kviz) != pozicija)
                    return;
                ArrayList<Kategorija> noveKategorije = (ArrayList<Kategorija>) data.getSerializableExtra("noveKategorije");
                kategorije.addAll(noveKategorije);
                categoryAdapter.notifyDataSetChanged();
                if (pozicija == -1) {
                    kvizovi.add(kviz);
                    if (((Kategorija) categorySpinner.getSelectedItem()).getNaziv().equals("Svi") || (kviz.getKategorija() != null && kviz.getKategorija().equals(categorySpinner.getSelectedItem())))
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
                    if (!((Kategorija) categorySpinner.getSelectedItem()).getNaziv().equals("Svi") && (postojeciKviz.getKategorija() == null || !postojeciKviz.getKategorija().equals(categorySpinner.getSelectedItem())))
                        prikazaniKvizovi.remove(postojeciKviz);
                }
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                // Pritisnuto back dugme
                ArrayList<Kategorija> noveKategorije = (ArrayList<Kategorija>) data.getSerializableExtra("noveKategorije");
                kategorije.addAll(noveKategorije);
                categoryAdapter.notifyDataSetChanged();
            }
        }
    }

    private void filterByCategory(Kategorija kategorija) {
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
        listAdapter.notifyDataSetChanged();
    }


    private void linkControls() {
        prikazaniKvizovi.add(null);
        kategorije.add(new Kategorija("Svi", "0"));
        categorySpinner = findViewById(R.id.spPostojeceKategorije);
        int layoutID = android.R.layout.simple_list_item_1;
        categoryAdapter = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(categoryAdapter);
        quizList = findViewById(R.id.lvKvizovi);
        listAdapter = new ListAdapter(this, prikazaniKvizovi, getResources());
        quizList.setAdapter(listAdapter);
    }
}
