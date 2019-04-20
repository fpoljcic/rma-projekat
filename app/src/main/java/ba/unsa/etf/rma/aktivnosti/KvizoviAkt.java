package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListFrag;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.adapteri.ListAdapter;

public class KvizoviAkt extends AppCompatActivity implements ListFrag.OnFragmentInteractionListener, DetailFrag.OnFragmentInteractionListener {
    private Spinner categorySpinner;
    private ListView quizList;
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kviz> prikazaniKvizovi = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ArrayAdapter<Kategorija> categoryAdapter;
    private ListAdapter listAdapter;
    private boolean siriEkran;
    private ListFrag listFrag;
    private DetailFrag detailFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int index = 0;
        if (savedInstanceState != null) {
            index = restoreData(savedInstanceState);
        } else
            addStartData();
        checkScreenSize();
        if (!siriEkran) {
            linkControls();
            setListeners();
            categorySpinner.setSelection(index);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("kvizovi", kvizovi);
        savedInstanceState.putSerializable("prikazaniKvizovi", prikazaniKvizovi);
        savedInstanceState.putSerializable("kategorije", kategorije);
        if (listFrag != null)
            savedInstanceState.putInt("position", listFrag.getIndexKategorije());
    }

    private int restoreData(Bundle savedInstanceState) {
        kvizovi = (ArrayList<Kviz>) savedInstanceState.getSerializable("kvizovi");
        prikazaniKvizovi = (ArrayList<Kviz>) savedInstanceState.getSerializable("prikazaniKvizovi");
        kategorije = (ArrayList<Kategorija>) savedInstanceState.getSerializable("kategorije");
        return savedInstanceState.getInt("position");
    }

    private void checkScreenSize() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout frameLayout = findViewById(R.id.listPlace);
        if (frameLayout != null) {
            siriEkran = true;
            detailFrag = (DetailFrag) fragmentManager.findFragmentById(R.id.detailPlace);
            if (detailFrag == null) {
                detailFrag = DetailFrag.newInstance(kvizovi, prikazaniKvizovi);
                fragmentManager.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
            }
            listFrag = (ListFrag) fragmentManager.findFragmentById(R.id.listPlace);
            if (listFrag == null) {
                int selectedCategory = 0;
                if (categorySpinner != null)
                    selectedCategory = categorySpinner.getSelectedItemPosition();
                listFrag = ListFrag.newInstance(kategorije, selectedCategory);
                fragmentManager.beginTransaction().replace(R.id.listPlace, listFrag).commit();
            }
        }
    }

    private void setListeners() {
        quizList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                urediKviz(position);
                return true;
            }
        });
        quizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                igrajKviz(position);
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija kategorija = kategorije.get(position);
                filterByCategory(kategorija);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing?
            }
        });
    }

    private void igrajKviz(int position) {
        if (prikazaniKvizovi.get(position) != null) {
            Intent myIntent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
            myIntent.putExtra("kviz", prikazaniKvizovi.get(position));
            startActivityForResult(myIntent, 2);
        }
    }

    private void urediKviz(int position) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (siriEkran)
            return;
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
        // if (requestCode == 2) // Zavrsio igranje kviza
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
    }

    private void addStartData() {
        prikazaniKvizovi.add(null);
        kategorije.add(new Kategorija("Svi", "0"));
    }


    private void linkControls() {
        categorySpinner = findViewById(R.id.spPostojeceKategorije);
        int layoutID = android.R.layout.simple_list_item_1;
        categoryAdapter = new ArrayAdapter<>(this, layoutID, kategorije);
        categorySpinner.setAdapter(categoryAdapter);
        quizList = findViewById(R.id.lvKvizovi);
        listAdapter = new ListAdapter(this, prikazaniKvizovi, getResources());
        quizList.setAdapter(listAdapter);
    }

    @Override
    public void onKategorijaClick(Kategorija kategorija) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (siriEkran) {
            kvizovi = detailFrag.getKvizovi();
            detailFrag = (DetailFrag) fragmentManager.findFragmentById(R.id.detailPlace);
            if(detailFrag != null) {
                fragmentManager.beginTransaction().remove(detailFrag).commit();
            }
            filterByCategory(kategorija);
            detailFrag = DetailFrag.newInstance(kvizovi, prikazaniKvizovi);
            fragmentManager.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
        }
    }

    @Override
    public Kategorija getSelectedKategorija() {
        if (listFrag != null)
            return listFrag.getSelectedKategorija();
        return (Kategorija) categorySpinner.getSelectedItem();
    }

    @Override
    public ArrayList<Kategorija> getKategorije() {
        if (listFrag != null)
            return listFrag.getKategorije();
        return kategorije;
    }

    @Override
    public void addKategorije(ArrayList<Kategorija> kategorije) {
        if (listFrag != null)
            listFrag.addKategorije(kategorije);
        else {
            this.kategorije.addAll(kategorije);
            categoryAdapter.notifyDataSetChanged();
        }
    }
}
