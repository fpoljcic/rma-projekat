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
import java.util.Collections;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.ListAdapter;
import ba.unsa.etf.rma.klase.Pitanje;

public class KvizoviAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private ListView quizList;
    private ArrayList<Kviz> sviKvizovi = new ArrayList<>();
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        napuni();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkControls();
        setListeners();
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String nazivKviza = intent.getStringExtra("nazivKviza");
        if (nazivKviza != null) {
            int pozicija = intent.getIntExtra("pozicija", -1);
            Kategorija kategorija = (Kategorija) intent.getSerializableExtra("kategorija");
            ArrayList<Pitanje> pitanja = (ArrayList<Pitanje>) intent.getSerializableExtra("pitanja");
            if (pozicija == -1) {
                sviKvizovi.add(sviKvizovi.size() - 1, new Kviz(nazivKviza, pitanja, kategorija));
            } else {
                Kviz kviz = sviKvizovi.get(pozicija);
                kviz.setNaziv(nazivKviza);
                kviz.setPitanja(pitanja);
                kviz.setKategorija(kategorija);
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
        pitanja.add(new Pitanje("Pitanje 1", "Koliko je 5 + 3 ?", odgovori, "8"));
        pitanja.add(new Pitanje("Pitanje 2", "Da li je nebo plavo ?", odgovori, "3"));
        pitanja2.add(new Pitanje("Pitanjeaaaa 0", "Sta ja radim ?", odgovori, "1"));
        kategorije.add(new Kategorija("Osnovna kategorija", "1"));
        kategorije.add(new Kategorija("Pomocna kategorija", "2"));
        kategorije.add(new Kategorija("Svi", "3"));
        sviKvizovi.add(0, new Kviz("Kviz 1", pitanja, kategorije.get(0)));
        sviKvizovi.add(1, new Kviz("Kviz 2", pitanja2, kategorije.get(1)));
    }

    private void setListeners() {
        quizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                if (sviKvizovi.get(position) != null) {
                    myIntent.putExtra("pozicija", position);
                    myIntent.putExtra("naziv", sviKvizovi.get(position).getNaziv());
                    myIntent.putExtra("pitanja", sviKvizovi.get(position).getPitanja());
                }
                ArrayList<Pitanje> mogucaPitanja = new ArrayList<>();
                for (Kviz kviz : sviKvizovi) {
                    if (kviz != null && kviz != sviKvizovi.get(position))
                        mogucaPitanja.addAll(kviz.getPitanja());
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
                if (kategorija.getNaziv().equals("Svi"))
                    kvizovi = sviKvizovi;
                else {
                    for (Kviz original : sviKvizovi) {
                        if (original != null) {
                            if (!original.getKategorija().equals(kategorija))
                                kvizovi.remove(original);
                            else {
                                Kviz kviz = new Kviz();
                                kviz.setNaziv(original.getNaziv());
                                for (Pitanje pitanje : original.getPitanja())
                                    kviz.getPitanja().add(new Pitanje(pitanje.getNaziv(), pitanje.getTekstPitanja(), pitanje.getOdgovori(), pitanje.getTacan()));
                                kviz.setKategorija(new Kategorija(original.getKategorija().getNaziv(), original.getKategorija().getId()));
                                kvizovi.add(kvizovi.size() - 1, kviz);
                            }
                        }
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing?
            }
        });
    }

    private void linkControls() {
        sviKvizovi.add(null);
        kvizovi.add(null);
        categorySpinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
        int layoutID = android.R.layout.simple_list_item_1;
        ArrayAdapter<Kategorija> adapterKat = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(adapterKat);
        quizList = (ListView) findViewById(R.id.lvKvizovi);
        listAdapter = new ListAdapter(this, kvizovi, getResources(), Kviz.class);
        quizList.setAdapter(listAdapter);
    }
}
