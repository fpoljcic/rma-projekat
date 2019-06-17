package ba.unsa.etf.rma.aktivnosti;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.ListAdapter;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListFrag;
import ba.unsa.etf.rma.klase.DatabaseHelper;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.NetworkChangeReceiver;

public class KvizoviAkt extends AppCompatActivity implements ListFrag.OnFragmentInteractionListener, DetailFrag.OnFragmentInteractionListener, Firebase.KvizInterface, Firebase.KategorijaInterface, NetworkChangeReceiver.NetworkInterface {
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
    private IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private NetworkChangeReceiver receiver = new NetworkChangeReceiver(this);
    public static boolean INTERNET_ACCESS;
    public static DatabaseHelper DATABASE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DATABASE = new DatabaseHelper(getApplicationContext());
        // DATABASE.ocistiBazu();
        INTERNET_ACCESS = isNetworkAvailable();
        if (INTERNET_ACCESS)
            DATABASE.syncFirebase();
        int index = 0;
        if (savedInstanceState != null) {
            index = restoreData(savedInstanceState);
        } else
            addStartData();
        checkScreenSize();
        loadData();
        if (!siriEkran) {
            linkControls();
            setListeners();
            categorySpinner.setSelection(index);
        }
    }

    @Override
    protected void onDestroy() {
        DATABASE.closeDatabase();
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private void loadData() {
        if (siriEkran) {
            Firebase.kvizovi(null, detailFrag);
            Firebase.kategorije(listFrag);
        } else {
            Firebase.kvizovi(null, this);
            Firebase.kategorije(this);
        }
    }


    @Override
    public void addKvizovi(ArrayList<Kviz> kvizovi) {
        if (siriEkran) {
            detailFrag.addKvizovi(kvizovi);
            return;
        }
        for (Kviz kviz : kvizovi) {
            if (!this.kvizovi.contains(kviz)) {
                this.kvizovi.add(kviz);
                if (getSelectedKategorija().getNaziv().equals("Svi") || kviz.getKategorija() != null && getSelectedKategorija().equals(kviz.getKategorija()))
                    prikazaniKvizovi.add(0, kviz);
            }
        }
        listAdapter.notifyDataSetChanged();
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
                if (INTERNET_ACCESS)
                    urediKviz(position);
                else
                    Toast.makeText(getApplicationContext(), "Nemate internet konekcije!", Toast.LENGTH_SHORT).show();
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
            if (hasEvent(position))
                return;
            Intent myIntent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
            myIntent.putExtra("kviz", prikazaniKvizovi.get(position));
            startActivityForResult(myIntent, 2);
        }
    }

    private boolean hasEvent(int position) {
        int x = (int) Math.ceil(prikazaniKvizovi.get(position).getPitanja().size() / 2.0);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 0);
        }

        // Imate događaj u kalendaru za Y minuta! ako je Y < X, Y vrijeme do prvog događaja koji slijedi
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        long currentDateTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MINUTE, x);
        long quizEndDateTime = calendar.getTimeInMillis();
        String[] projection = new String[]{CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        String selection = CalendarContract.Events.DTEND + " >= ?";
        String[] selectionArgs = new String[]{Long.toString(currentDateTime)};

        Cursor cur = getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cur == null)
            return false;
        while (cur.moveToNext()) {
            long eventStartDateTime = cur.getLong(0);
            if (eventStartDateTime < quizEndDateTime) {
                if (eventStartDateTime < currentDateTime)
                    showAlert("Imate trenutno aktivan događaj u kalendaru!");
                else
                    showAlert("Imate događaj u kalendaru za " + Math.round((eventStartDateTime - currentDateTime) / 600.0) / 100.0 + " minuta!");
                cur.close();
                return true;
            }
        }
        cur.close();
        return false;
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
        if (findViewById(R.id.detailPlace) != null) {
            detailFrag.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Kviz kviz = (Kviz) data.getSerializableExtra("kviz");
                ArrayList<String> idPitanja = data.getStringArrayListExtra("idPitanja");
                int pozicija = data.getIntExtra("pozicija", -1);
                ArrayList<Kategorija> noveKategorije = (ArrayList<Kategorija>) data.getSerializableExtra("noveKategorije");
                kategorije.addAll(noveKategorije);
                categoryAdapter.notifyDataSetChanged();
                if (pozicija == -1) {
                    kvizovi.add(kviz);
                    Firebase.dodajKviz(kviz, idPitanja);
                    if (((Kategorija) categorySpinner.getSelectedItem()).getNaziv().equals("Svi") || (kviz.getKategorija() != null && kviz.getKategorija().equals(categorySpinner.getSelectedItem())))
                        prikazaniKvizovi.add(prikazaniKvizovi.size() - 1, kviz);
                } else {
                    Kviz postojeciKviz = prikazaniKvizovi.get(pozicija);
                    int pos = kvizovi.indexOf(postojeciKviz);
                    Firebase.azuirajKviz(postojeciKviz.getNaziv(), kviz, idPitanja);
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
        if (kategorija.getNaziv().equals("Svi"))
            Firebase.kvizovi(null, this);
        else
            Firebase.kvizovi(kategorija, this);
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
            if (detailFrag != null) {
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

    @Override
    public void addKategorijeFirebase(ArrayList<Kategorija> kategorije) {
        if (siriEkran) {
            listFrag.addKategorijeFirebase(kategorije);
            return;
        }
        for (Kategorija kategorija : kategorije) {
            if (!this.kategorije.contains(kategorija))
                this.kategorije.add(kategorija);
        }
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyNetChange(boolean internetAccess) {
        if (internetAccess) {
            loadData();
            DATABASE.syncFirebase();
            DATABASE.syncFirebaseIgraci();
        }
        INTERNET_ACCESS = internetAccess;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
