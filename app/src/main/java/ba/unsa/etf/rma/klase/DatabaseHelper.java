package ba.unsa.etf.rma.klase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements Firebase.KategorijaInterface, Firebase.KvizInterface, Firebase.PitanjeInterface, Firebase.RangListaInterface {
    public static final String DATABASE_NAME = "rma_baza.db";

    public static final String RANGLISTE_TABLE = "Rangliste";
    public static final String ID_RANGLISTE = "idRangliste";
    public static final String ID_KVIZA_RANGLISTE = "idKviza";
    public static final String PROCENAT_TACNIH = "procenaTacnih";
    public static final String NAZIV_IGRACA = "nazivIgraca";

    public static final String KVIZOVI_TABLE = "Kvizovi";
    public static final String NAZIV_KVIZA = "nazivKviza";
    public static final String ID_KATEGORIJE = "idKategorije";

    public static final String KATEGORIJE_TABLE = "Kategorije";
    public static final String NAZIV_KATEGORIJE = "nazivKategorije";
    public static final String ID_IKONICE = "idIkonice";

    public static final String PITANJA_TABLE = "Pitanja";
    public static final String NAZIV_PITANJA = "nazivPitanja";
    public static final String INDEX_TACNOG = "indexTacnog";

    public static final String KVIZOVI_PITANJA_TABLE = "KvizoviPitanja";
    public static final String ID_KVIZOVI_PITANJA = "idKvizoviPitanja";
    public static final String ID_KVIZA_KVIZOVI_PITANJA = "idKviza";
    public static final String ID_PITANJA_KVIZOVI_PITANJA = "idPitanja";

    public static final String ODGOVORI_TABLE = "Odgovori";
    public static final String ID_ODGOVORA = "idOdgovora";
    public static final String NAZIV_ODGOVORA = "naziv";

    public static final String PITANJA_ODGOVORI_TABLE = "PitanjaOdgovori";
    public static final String ID_PITANJA_ODGOVORI = "idPitanjaOdgovori";
    public static final String ID_PITANJA_PITANJA_ODGOVORI = "idPitanja";
    public static final String ID_ODGOVORA_PITANJA_ODGOVORI = "idOdgovora";

    public static final int DATABSE_VERSION = 1;

    public static final String CREATE_TABLE_KATEGORIJE = "CREATE TABLE " + KATEGORIJE_TABLE + " (" +
            NAZIV_KATEGORIJE + " TEXT PRIMARY KEY, " +
            ID_IKONICE + " INTEGER NOT NULL);";
    public static final String CREATE_TABLE_KVIZOVI = "CREATE TABLE " + KVIZOVI_TABLE + " (" +
            NAZIV_KVIZA + " TEXT PRIMARY KEY, " +
            ID_KATEGORIJE + " INTEGER," +
            "FOREIGN KEY (" + ID_KATEGORIJE + ") REFERENCES " + KATEGORIJE_TABLE + "(" + NAZIV_KATEGORIJE + "));";
    public static final String CREATE_TABLE_RANGLISTE = "CREATE TABLE " + RANGLISTE_TABLE + " (" +
            ID_RANGLISTE + " INTEGER PRIMARY KEY, " +
            ID_KVIZA_RANGLISTE + " TEXT, " +
            PROCENAT_TACNIH + " REAL NOT NULL, " +
            NAZIV_IGRACA + " TEXT NOT NULL," +
            "FOREIGN KEY (" + ID_KVIZA_RANGLISTE + ") REFERENCES " + KVIZOVI_TABLE + "(" + NAZIV_KVIZA + "));";
    public static final String CREATE_TABLE_PITANJA = "CREATE TABLE " + PITANJA_TABLE + " (" +
            NAZIV_PITANJA + " TEXT PRIMARY KEY, " +
            INDEX_TACNOG + " INTEGER NOT NULL);";
    public static final String CREATE_TABLE_KVIZOVI_PITANJA = "CREATE TABLE " + KVIZOVI_PITANJA_TABLE + " (" +
            ID_KVIZOVI_PITANJA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ID_KVIZA_KVIZOVI_PITANJA + " TEXT, " +
            ID_PITANJA_KVIZOVI_PITANJA + " TEXT, " +
            "FOREIGN KEY (" + ID_KVIZA_KVIZOVI_PITANJA + ") REFERENCES " + KVIZOVI_TABLE + "(" + NAZIV_KVIZA + "), " +
            "FOREIGN KEY (" + ID_PITANJA_KVIZOVI_PITANJA + ") REFERENCES " + PITANJA_TABLE + "(" + NAZIV_PITANJA + "));";
    public static final String CREATE_TABLE_ODGOVORI = "CREATE TABLE " + ODGOVORI_TABLE + " (" +
            ID_ODGOVORA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAZIV_ODGOVORA + " TEXT NOT NULL);";
    public static final String CREATE_TABLE_PITANJA_ODGOVORI = "CREATE TABLE " + PITANJA_ODGOVORI_TABLE + " (" +
            ID_PITANJA_ODGOVORI + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ID_PITANJA_PITANJA_ODGOVORI + " TEXT, " +
            ID_ODGOVORA_PITANJA_ODGOVORI + " INTEGER, " +
            "FOREIGN KEY (" + ID_PITANJA_PITANJA_ODGOVORI + ") REFERENCES " + PITANJA_TABLE + "(" + NAZIV_PITANJA + "), " +
            "FOREIGN KEY (" + ID_ODGOVORA_PITANJA_ODGOVORI + ") REFERENCES " + ODGOVORI_TABLE + "(" + ID_ODGOVORA + "));";

    // Lokacija | //data/data/ba.unsa.etf.rma.klase/databases/rma_baza.db

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Poziva se kada ne postoji baza
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_KATEGORIJE);
        database.execSQL(CREATE_TABLE_KVIZOVI);
        database.execSQL(CREATE_TABLE_RANGLISTE);
        database.execSQL(CREATE_TABLE_PITANJA);
        database.execSQL(CREATE_TABLE_KVIZOVI_PITANJA);
        database.execSQL(CREATE_TABLE_ODGOVORI);
        database.execSQL(CREATE_TABLE_PITANJA_ODGOVORI);
    }

    // Poziva se kada se ne poklapaju verzije baze na disku i trenutne baze
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Brisanje stare verzije
        database.execSQL("DROP TABLE IF EXISTS " + KATEGORIJE_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + KVIZOVI_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + RANGLISTE_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + PITANJA_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + KVIZOVI_PITANJA_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + ODGOVORI_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + PITANJA_ODGOVORI_TABLE);
        // Kreiranje nove
        onCreate(database);
    }

    public void syncFirebase() {
        Firebase.kategorije(this);
        Firebase.kvizovi(null, this);
        Firebase.pitanja(this);
    }

    public boolean dodajKategoriju(Kategorija kategorija) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_KATEGORIJE, kategorija.getNaziv());
        values.put(ID_IKONICE, kategorija.getId());

        return db.insert(KATEGORIJE_TABLE, null, values) != -1;
    }

    public boolean dodajKviz(Kviz kviz) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_KVIZA, kviz.getNaziv());
        if (kviz.getKategorija() != null)
            values.put(ID_KATEGORIJE, kviz.getKategorija().getNaziv());

        for (Pitanje pitanje : kviz.getPitanja()) {
            dodajPitanje(pitanje);
            dodajKvizoviPitanjaVezu(kviz, pitanje);
        }

        return db.insert(KVIZOVI_TABLE, null, values) != -1;
    }

    public boolean dodajPitanje(Pitanje pitanje) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_PITANJA, pitanje.getNaziv());
        values.put(INDEX_TACNOG, pitanje.getIndexTacnog());

        for (String odgovor : pitanje.getOdgovori()) {
            long idOdgovora = dodajOdgovor(odgovor);
            dodajPitanjaOdgovoriVezu(pitanje, idOdgovora);
        }


        return db.insert(PITANJA_TABLE, null, values) != -1;
    }

    private boolean dodajKvizoviPitanjaVezu(Kviz kviz, Pitanje pitanje) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_KVIZA_KVIZOVI_PITANJA, kviz.getNaziv());
        values.put(ID_PITANJA_KVIZOVI_PITANJA, pitanje.getNaziv());

        return db.insert(KVIZOVI_PITANJA_TABLE, null, values) != -1;
    }

    public long dodajOdgovor(String odgovor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_ODGOVORA, odgovor);

        return db.insert(ODGOVORI_TABLE, null, values);
    }

    private boolean dodajPitanjaOdgovoriVezu(Pitanje pitanje, long idOdgovora) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_PITANJA_PITANJA_ODGOVORI, pitanje.getNaziv());
        values.put(ID_ODGOVORA_PITANJA_ODGOVORI, idOdgovora);

        return db.insert(PITANJA_ODGOVORI_TABLE, null, values) != -1;
    }

    public boolean dodajRanglistu(String nazivKviza, double procenatTacnih, String imeIgraca) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_KVIZA_RANGLISTE, nazivKviza);
        values.put(PROCENAT_TACNIH, procenatTacnih);
        values.put(NAZIV_IGRACA, imeIgraca);

        return db.insert(RANGLISTE_TABLE, null, values) != -1;
    }


    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    @Override
    public void addKategorijeFirebase(ArrayList<Kategorija> kategorije) {
        for (Kategorija kategorija : kategorije)
            dodajKategoriju(kategorija);
    }

    @Override
    public void addKvizovi(ArrayList<Kviz> kvizovi) {
        for (Kviz kviz : kvizovi) {
            dodajKviz(kviz);
            Firebase.rangLista(kviz, this);
        }
    }

    @Override
    public void addIgraci(ArrayList<String> igraci, Kviz kviz) {
        for (String igrac : igraci) {
            double procenatTacnih = Double.valueOf(igrac.substring(igrac.lastIndexOf("-") + 2, igrac.length() - 1));
            String imeIgraca = igrac.substring(igrac.indexOf(".") + 2, igrac.lastIndexOf("-") - 2);
            dodajRanglistu(kviz.getNaziv(), procenatTacnih, imeIgraca);
        }
    }

    @Override
    public void getPitanjeId(Pair<String, Pitanje> idPitanja) {

    }

    @Override
    public void addPitanja(ArrayList<Pair<String, Pitanje>> pitanja) {
        //for (Pair<String, Pitanje> pair : pitanja)
        //    dodajPitanje(pair.second);
    }

    public ArrayList<Kviz> kvizovi(final Kategorija kategorija) {
        ArrayList<Kviz> kvizovi = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + KVIZOVI_TABLE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Kviz kviz = new Kviz();
                kviz.setNaziv(cursor.getString((cursor.getColumnIndex(NAZIV_KVIZA))));
                String nazivKategorije = cursor.getString(cursor.getColumnIndex(ID_KATEGORIJE));
                if (kategorija != null && (nazivKategorije == null || !nazivKategorije.equals(kategorija.getNaziv())))
                    continue;
                kviz.setKategorija(vratiKategoriju(nazivKategorije));
                kviz.setPitanja(vratiPitanja(kviz));

                kvizovi.add(kviz);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return kvizovi;
    }

    private ArrayList<Pitanje> vratiPitanja(Kviz kviz) {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        String selectQuery = "SELECT " + NAZIV_PITANJA + ", " + INDEX_TACNOG + " FROM " + PITANJA_TABLE + ", " + KVIZOVI_PITANJA_TABLE + " WHERE " + ID_KVIZA_KVIZOVI_PITANJA + " = '" + kviz.getNaziv() + "' AND " + ID_PITANJA_KVIZOVI_PITANJA + " = " + NAZIV_PITANJA;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Pitanje pitanje = new Pitanje();
                pitanje.setNaziv(cursor.getString(cursor.getColumnIndex(NAZIV_PITANJA)));
                pitanje.setTekstPitanja(pitanje.getNaziv());
                int indexTacnog = cursor.getInt(cursor.getColumnIndex(INDEX_TACNOG));
                pitanje.setOdgovori(vratiOdgovore(pitanje));
                pitanje.setTacan(pitanje.getOdgovori().get(indexTacnog));
                pitanja.add(pitanje);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pitanja;
    }

    private ArrayList<String> vratiOdgovore(Pitanje pitanje) {
        ArrayList<String> odgovori = new ArrayList<>();
        String selectQuery = "SELECT " + NAZIV_ODGOVORA + " FROM " + ODGOVORI_TABLE + ", " + PITANJA_ODGOVORI_TABLE + " WHERE " + ID_PITANJA_PITANJA_ODGOVORI + " = '" + pitanje.getNaziv() + "' AND " + PITANJA_ODGOVORI_TABLE + "." + ID_ODGOVORA_PITANJA_ODGOVORI + " = " + ODGOVORI_TABLE + "." + ID_ODGOVORA;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                odgovori.add(cursor.getString(cursor.getColumnIndex(NAZIV_ODGOVORA)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return odgovori;
    }

    private Kategorija vratiKategoriju(String nazivKategorije) {
        Kategorija kategorija = new Kategorija();
        kategorija.setNaziv(nazivKategorije);
        String selectQuery = "SELECT " + ID_IKONICE + " FROM " + KATEGORIJE_TABLE + " WHERE " + NAZIV_KATEGORIJE + " = '" + nazivKategorije + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            kategorija.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(ID_IKONICE))));
        }
        cursor.close();
        return kategorija;
    }

}
