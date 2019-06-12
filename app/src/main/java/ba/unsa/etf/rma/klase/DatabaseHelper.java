package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "rma_baza.db";

    public static final String RANGLISTE_TABLE = "Rangliste";
    public static final String ID_RANGLISTE = "idRangliste";
    public static final String ID_KVIZA_RANGLISTE = "idKviza";
    public static final String POZICIJA = "pozicija";
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
            POZICIJA + " INTEGER NOT NULL, " +
            NAZIV_IGRACA + " TEXT NOT NULL," +
            "FOREIGN KEY (" + ID_KVIZA_RANGLISTE + ") REFERENCES " + KVIZOVI_TABLE + "(" + NAZIV_KVIZA + "));";
    public static final String CREATE_TABLE_PITANJA = "CREATE TABLE " + PITANJA_TABLE + " (" +
            NAZIV_PITANJA + " TEXT PRIMARY KEY, " +
            INDEX_TACNOG + " INTEGER NOT NULL);";
    public static final String CREATE_TABLE_KVIZOVI_PITANJA = "CREATE TABLE " + KVIZOVI_PITANJA_TABLE + " (" +
            ID_KVIZOVI_PITANJA + " INTEGER PRIMARY KEY, " +
            ID_KVIZA_KVIZOVI_PITANJA + " TEXT, " +
            ID_PITANJA_KVIZOVI_PITANJA + " TEXT, " +
            "FOREIGN KEY (" + ID_KVIZA_KVIZOVI_PITANJA + ") REFERENCES " + KVIZOVI_TABLE + "(" + NAZIV_KVIZA + "), " +
            "FOREIGN KEY (" + ID_PITANJA_KVIZOVI_PITANJA + ") REFERENCES " + PITANJA_TABLE + "(" + NAZIV_PITANJA + "));";
    public static final String CREATE_TABLE_ODGOVORI = "CREATE TABLE " + ODGOVORI_TABLE + " (" +
            ID_ODGOVORA + " INTEGER PRIMARY KEY, " +
            NAZIV_ODGOVORA + " TEXT NOT NULL);";
    public static final String CREATE_TABLE_PITANJA_ODGOVORI = "CREATE TABLE " + PITANJA_ODGOVORI_TABLE + " (" +
            ID_PITANJA_ODGOVORI + " INTEGER PRIMARY KEY, " +
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


    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
