package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.KvizArrayAdapter;
import ba.unsa.etf.rma.klase.Pitanje;

public class KvizoviAkt extends AppCompatActivity {
    private Spinner categorySpinner;
    private ListView quizList;
    private ArrayList<Kviz> kvizovi = new ArrayList<>(1);
    private KvizArrayAdapter kvizArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkControls();
        setListeners();
        napuni();
    }

    private void napuni() {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        ArrayList<String> odgovori = new ArrayList<>();
        odgovori.add("3");
        odgovori.add("1");
        odgovori.add("8");
        pitanja.add(new Pitanje("Pitanje 1", "Koliko je 5 + 3 ?", odgovori, "8"));
        pitanja.add(new Pitanje("Pitanje 2", "Da li je nebo plavo ?", odgovori, "3"));
        kvizovi.add(0, new Kviz("Test 1", pitanja, new Kategorija("Osnovna kategorija", "1")));
    }

    private void setListeners() {
        quizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                if (position != kvizovi.size() - 1) {
                    myIntent.putExtra("naziv", kvizovi.get(position).getNaziv());
                    myIntent.putExtra("pitanja", kvizovi.get(position).getPitanja());
                    myIntent.putExtra("kategorija", kvizovi.get(position).getKategorija().toString());
                }
                KvizoviAkt.this.startActivity(myIntent);
            }
        });
    }

    private void linkControls() {
        kvizovi.add(null);
        categorySpinner = (Spinner) findViewById(R.id.spPostojeceKategorije);
        quizList = (ListView) findViewById(R.id.lvKvizovi);
        kvizArrayAdapter = new KvizArrayAdapter(this, kvizovi, getResources());
        quizList.setAdapter(kvizArrayAdapter);
    }
}
