package ba.unsa.etf.rma.klase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;

import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

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
            ID_IKONICE + " TEXT NOT NULL);";
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
            NAZIV_ODGOVORA + " TEXT UNIQUE NOT NULL);";
    public static final String CREATE_TABLE_PITANJA_ODGOVORI = "CREATE TABLE " + PITANJA_ODGOVORI_TABLE + " (" +
            ID_PITANJA_ODGOVORI + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ID_PITANJA_PITANJA_ODGOVORI + " TEXT, " +
            ID_ODGOVORA_PITANJA_ODGOVORI + " INTEGER, " +
            "FOREIGN KEY (" + ID_PITANJA_PITANJA_ODGOVORI + ") REFERENCES " + PITANJA_TABLE + "(" + NAZIV_PITANJA + "), " +
            "FOREIGN KEY (" + ID_ODGOVORA_PITANJA_ODGOVORI + ") REFERENCES " + ODGOVORI_TABLE + "(" + ID_ODGOVORA + "));";

    private static DatabaseHelper instance;

    private ArrayList<IgracRecord> igracRecords = new ArrayList<>();

    public static DatabaseHelper getInstance() {
        return instance;
    }

    // Lokacija | //data/data/ba.unsa.etf.rma.klase/databases/rma_baza.db

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
        instance = this;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        instance = this;
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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_KATEGORIJE, kategorija.getNaziv());
        values.put(ID_IKONICE, kategorija.getId());

        return db.insertWithOnConflict(KATEGORIJE_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    public boolean dodajKviz(Kviz kviz) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_KVIZA, kviz.getNaziv());
        if (kviz.getKategorija() != null)
            values.put(ID_KATEGORIJE, kviz.getKategorija().getNaziv());

        for (Pitanje pitanje : kviz.getPitanja()) {
            dodajPitanje(pitanje);
            dodajKvizoviPitanjaVezu(kviz, pitanje);
        }

        return db.insertWithOnConflict(KVIZOVI_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    public boolean dodajPitanje(Pitanje pitanje) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_PITANJA, pitanje.getNaziv());
        values.put(INDEX_TACNOG, pitanje.getIndexTacnog());

        for (String odgovor : pitanje.getOdgovori()) {
            long idOdgovora = dodajOdgovor(odgovor);
            dodajPitanjaOdgovoriVezu(pitanje, idOdgovora);
        }

        return db.insertWithOnConflict(PITANJA_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    private boolean dodajKvizoviPitanjaVezu(Kviz kviz, Pitanje pitanje) {
        String selectQuery = "SELECT * FROM " + KVIZOVI_PITANJA_TABLE + " WHERE " + ID_KVIZA_KVIZOVI_PITANJA + " = '" + kviz.getNaziv() + "' AND " + ID_PITANJA_KVIZOVI_PITANJA + " = '" + pitanje.getNaziv() + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();

        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_KVIZA_KVIZOVI_PITANJA, kviz.getNaziv());
        values.put(ID_PITANJA_KVIZOVI_PITANJA, pitanje.getNaziv());

        return db.insertWithOnConflict(KVIZOVI_PITANJA_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    public long dodajOdgovor(String odgovor) {
        String selectQuery = "SELECT * FROM " + ODGOVORI_TABLE + " WHERE " + NAZIV_ODGOVORA + " = '" + odgovor + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            long id = cursor.getLong((cursor.getColumnIndex(ID_ODGOVORA)));
            cursor.close();
            return id;
        }
        cursor.close();

        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAZIV_ODGOVORA, odgovor);

        return db.insert(ODGOVORI_TABLE, null, values);
    }

    private boolean dodajPitanjaOdgovoriVezu(Pitanje pitanje, long idOdgovora) {
        String selectQuery = "SELECT * FROM " + PITANJA_ODGOVORI_TABLE + " WHERE " + ID_PITANJA_PITANJA_ODGOVORI + " = '" + pitanje.getNaziv() + "' AND " + ID_ODGOVORA_PITANJA_ODGOVORI + " = '" + idOdgovora + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();

        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_PITANJA_PITANJA_ODGOVORI, pitanje.getNaziv());
        values.put(ID_ODGOVORA_PITANJA_ODGOVORI, idOdgovora);

        return db.insertWithOnConflict(PITANJA_ODGOVORI_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    public void syncFirebaseIgraci() {
        for (IgracRecord igracRecord : igracRecords) {
            Firebase.dodajIgraca(igracRecord.nazivKviza, igracRecord.imeIgraca, igracRecord.procenatTacnih);
        }
        igracRecords.clear();
    }

    public void updateNazivRangliste(String nazivPostojecegKviza, String nazivNovogKviza) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + RANGLISTE_TABLE + " SET " + ID_KVIZA_RANGLISTE + " = '" + nazivNovogKviza + "' WHERE " + ID_KVIZA_RANGLISTE + " = '" + nazivPostojecegKviza + "'");
    }

    private class IgracRecord {
        private String nazivKviza;
        private double procenatTacnih;
        private String imeIgraca;

        public IgracRecord(String nazivKviza, double procenatTacnih, String imeIgraca) {
            this.nazivKviza = nazivKviza;
            this.procenatTacnih = procenatTacnih;
            this.imeIgraca = imeIgraca;
        }
    }

    public boolean dodajIgraca(String nazivKviza, double procenatTacnih, String imeIgraca) {
        String selectQuery = "SELECT * FROM " + RANGLISTE_TABLE + " WHERE " + ID_KVIZA_RANGLISTE + " = '" + nazivKviza + "' AND " + PROCENAT_TACNIH + " = '" + procenatTacnih + "' AND " + NAZIV_IGRACA + " = '" + imeIgraca + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();

        if (!KvizoviAkt.INTERNET_ACCESS)
            igracRecords.add(new IgracRecord(nazivKviza, procenatTacnih, imeIgraca));

        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_KVIZA_RANGLISTE, nazivKviza);
        values.put(PROCENAT_TACNIH, procenatTacnih);
        values.put(NAZIV_IGRACA, imeIgraca);

        return db.insert(RANGLISTE_TABLE, null, values) != -1;
    }


    public void closeDatabase() {
        SQLiteDatabase db = getReadableDatabase();
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
            String imeIgraca = igrac.substring(igrac.indexOf(".") + 2, igrac.lastIndexOf("-") - 1);
            dodajIgraca(kviz.getNaziv(), procenatTacnih, imeIgraca);
        }
    }

    @Override
    public void getPitanjeId(Pair<String, Pitanje> idPitanja) {
        // ignored
    }

    @Override
    public void addPitanja(ArrayList<Pair<String, Pitanje>> pitanja) {
        for (Pair<String, Pitanje> pair : pitanja)
            dodajPitanje(pair.second);
    }

    public ArrayList<Kviz> kvizovi(final Kategorija kategorija) {
        ArrayList<Kviz> kvizovi = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + KVIZOVI_TABLE + " LEFT JOIN " + KATEGORIJE_TABLE + " ON " + ID_KATEGORIJE + " = " + NAZIV_KATEGORIJE;
        if (kategorija != null)
            selectQuery += " AND " + NAZIV_KATEGORIJE + " = '" + kategorija.getNaziv() + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Kviz kviz = new Kviz();
                kviz.setNaziv(cursor.getString((cursor.getColumnIndex(NAZIV_KVIZA))));
                String nazivKategorije = cursor.getString(cursor.getColumnIndex(NAZIV_KATEGORIJE));
                if (nazivKategorije == null)
                    kviz.setKategorija(null);
                else
                    kviz.setKategorija(new Kategorija(nazivKategorije, cursor.getString(cursor.getColumnIndex(ID_IKONICE))));
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

    public ArrayList<Kategorija> kategorije() {
        ArrayList<Kategorija> kategorije = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + KATEGORIJE_TABLE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Kategorija kategorija = new Kategorija();
                kategorija.setNaziv(cursor.getString((cursor.getColumnIndex(NAZIV_KATEGORIJE))));
                kategorija.setId(cursor.getString((cursor.getColumnIndex(ID_IKONICE))));
                kategorije.add(kategorija);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return kategorije;
    }

    public ArrayList<Pair<String, Pitanje>> pitanja() {
        ArrayList<Pair<String, Pitanje>> pitanja = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + PITANJA_TABLE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Pitanje pitanje = new Pitanje();
                pitanje.setNaziv(cursor.getString((cursor.getColumnIndex(NAZIV_PITANJA))));
                pitanje.setTekstPitanja(cursor.getString((cursor.getColumnIndex(NAZIV_PITANJA))));
                int indexTacnog = cursor.getInt(cursor.getColumnIndex(INDEX_TACNOG));
                pitanje.setOdgovori(vratiOdgovore(pitanje));
                pitanje.setTacan(pitanje.getOdgovori().get(indexTacnog));
                pitanja.add(new Pair<>("", pitanje));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return pitanja;
    }

    public ArrayList<String> rangLista(Kviz kviz) {
        ArrayList<String> igraci = new ArrayList<>();
        String selectQuery = "SELECT " + NAZIV_IGRACA + ", " + PROCENAT_TACNIH + " FROM " + RANGLISTE_TABLE + " WHERE " + ID_KVIZA_RANGLISTE + " = '" + kviz.getNaziv() + "' ORDER BY " + PROCENAT_TACNIH + " DESC";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int pozicija = 1;
            do {
                String pozicijaString = String.valueOf(pozicija);
                pozicija++;
                String imeString = cursor.getString((cursor.getColumnIndex(NAZIV_IGRACA)));
                double procenatTacnih = cursor.getDouble((cursor.getColumnIndex(PROCENAT_TACNIH)));
                String igrac = pozicijaString + ". " + imeString + " - " + procenatTacnih + "%";
                igraci.add(igrac);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return igraci;
    }

    public void azurirajKviz(String nazivPostojecegKviza, Kviz noviKviz) {
        SQLiteDatabase db = getWritableDatabase();
        String updateKvizInfo;
        if (noviKviz.getKategorija() == null)
            updateKvizInfo = "UPDATE " + KVIZOVI_TABLE + " SET " + NAZIV_KVIZA + " = '" + noviKviz.getNaziv() + "', " + ID_KATEGORIJE + " = " + null + " WHERE " + NAZIV_KVIZA + " = '" + nazivPostojecegKviza + "'";
        else
            updateKvizInfo = "UPDATE " + KVIZOVI_TABLE + " SET " + NAZIV_KVIZA + " = '" + noviKviz.getNaziv() + "', " + ID_KATEGORIJE + " = '" + noviKviz.getKategorija().getNaziv() + "' " + " WHERE " + NAZIV_KVIZA + " = '" + nazivPostojecegKviza + "'";
        db.execSQL(updateKvizInfo);
        db.execSQL("DELETE FROM " + KVIZOVI_PITANJA_TABLE + " WHERE " + ID_KVIZA_KVIZOVI_PITANJA + " = '" + nazivPostojecegKviza + "'");
        for (Pitanje pitanje : noviKviz.getPitanja())
            dodajKvizoviPitanjaVezu(noviKviz, pitanje);
    }

    public void ocistiBazu() {
        SQLiteDatabase database = getWritableDatabase();
        onUpgrade(database, DATABSE_VERSION, DATABSE_VERSION);
        database.execSQL("DELETE FROM " + KATEGORIJE_TABLE);
        database.execSQL("DELETE FROM " + KVIZOVI_TABLE);
        database.execSQL("DELETE FROM " + RANGLISTE_TABLE);
        database.execSQL("DELETE FROM " + PITANJA_TABLE);
        database.execSQL("DELETE FROM " + KVIZOVI_PITANJA_TABLE);
        database.execSQL("DELETE FROM " + ODGOVORI_TABLE);
        database.execSQL("DELETE FROM " + PITANJA_ODGOVORI_TABLE);
    }
}
