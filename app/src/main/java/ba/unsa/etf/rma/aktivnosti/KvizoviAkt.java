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
import java.util.Iterator;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.ListAdapter;
import ba.unsa.etf.rma.klase.Pitanje;

public class KvizoviAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private ListView quizList;
    private ArrayList<Kviz> ostaliKvizovi = new ArrayList<>();
    private ArrayList<Kviz> prikazaniKvizovi = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        napuni();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkControls();
        filterByCategory(kategorije.get(0));
        setListeners();
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Kviz kviz = (Kviz) intent.getSerializableExtra("kviz");
        if (kviz != null) {
            int pozicija = intent.getIntExtra("pozicija", -1);
            if (pozicija == -1) {
                if (kviz.getKategorija().getNaziv().equals(((Kategorija) categorySpinner.getSelectedItem()).getNaziv()))
                    prikazaniKvizovi.add(prikazaniKvizovi.size() - 1, kviz);
                else
                    ostaliKvizovi.add(kviz);
            } else {
                Kviz postojeciKviz = prikazaniKvizovi.get(pozicija);
                postojeciKviz.setNaziv(kviz.getNaziv());
                postojeciKviz.setPitanja(kviz.getPitanja());
                postojeciKviz.setKategorija(kviz.getKategorija());
                if (!postojeciKviz.getKategorija().getNaziv().equals(((Kategorija) categorySpinner.getSelectedItem()).getNaziv())) {
                    ostaliKvizovi.add(postojeciKviz);
                    prikazaniKvizovi.remove(postojeciKviz);
                }
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    private void napuni() {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        ArrayList<Pitanje> pitanja2 = new ArrayList<>();
        ArrayList<String> odgovori = new ArrayList<>();
        odgovori.add("3");
        odgovori.add("1");
        odgovori.add("8");
        pitanja.add(new Pitanje("Pitanje 1 - Kviz 1", "Koliko je 5 + 3 ?", odgovori, "8"));
        pitanja.add(new Pitanje("Pitanje 2 - Kviz 1", "Da li je nebo plavo ?", odgovori, "3"));
        pitanja2.add(new Pitanje("Pitanje 1 - Kviz 2", "Sta ja radim ?", odgovori, "1"));
        kategorije.add(new Kategorija("Svi", "0"));
        kategorije.add(new Kategorija("Osnovna kategorija", "1"));
        kategorije.add(new Kategorija("Pomocna kategorija", "2"));
        ostaliKvizovi.add(0, new Kviz("Kviz 1", pitanja, kategorije.get(1)));
        ostaliKvizovi.add(1, new Kviz("Kviz 2", pitanja2, kategorije.get(2)));
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
                ArrayList<Pitanje> mogucaPitanja = new ArrayList<>();
                for (Kviz kviz : prikazaniKvizovi) {
                    if (kviz != null && kviz != prikazaniKvizovi.get(position)) {
                        for (Pitanje pitanje : kviz.getPitanja()) {
                            if (!mogucaPitanja.contains(pitanje))
                                mogucaPitanja.add(pitanje);
                        }
                    }
                }
                myIntent.putExtra("mogucaPitanja", mogucaPitanja);
                myIntent.putExtra("kategorija", kategorije);
                KvizoviAkt.this.startActivity(myIntent);
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
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

    private void filterByCategory(Kategorija kategorija) {
        if (kategorija.getNaziv().equals("Svi")) {
            Iterator<Kviz> iterator = ostaliKvizovi.iterator();
            while (iterator.hasNext()) {
                Kviz next = iterator.next();
                prikazaniKvizovi.add(prikazaniKvizovi.size() - 1, next);
                iterator.remove();
            }
        } else {
            prikazaniKvizovi.remove(null);
            ostaliKvizovi.addAll(prikazaniKvizovi);
            prikazaniKvizovi.clear();
            prikazaniKvizovi.add(null);
            for (int i = 0; i < ostaliKvizovi.size(); i++) {
                Kviz kviz = ostaliKvizovi.get(i);
                if (kviz.getKategorija().equals(kategorija)) {
                    prikazaniKvizovi.add(prikazaniKvizovi.size() - 1, kviz);
                    ostaliKvizovi.remove(kviz);
                    i--;
                }
            }
        }
        listAdapter.notifyDataSetChanged();
    }



    private void linkControls() {
        prikazaniKvizovi.add(null);
        categorySpinner = findViewById(R.id.spPostojeceKategorije);
        int layoutID = android.R.layout.simple_list_item_1;
        ArrayAdapter<Kategorija> adapterKat = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(adapterKat);
        quizList = findViewById(R.id.lvKvizovi);
        listAdapter = new ListAdapter(this, prikazaniKvizovi, getResources(), Kviz.class);
        quizList.setAdapter(listAdapter);
    }
}
