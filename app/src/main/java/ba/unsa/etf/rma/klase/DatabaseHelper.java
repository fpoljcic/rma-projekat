package ba.unsa.etf.rma.klase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "rma_baza.db";
    public static final String KATEGORIJE_TABLE = "Kategorije";
    public static final String KVIZOVI_TABLE = "Kvizovi";
    public static final String PITANJA_TABLE = "Pitanja";
    public static final String RANGLISTE_TABLE = "Rangliste";
    public static final int DATABSE_VERSION = 1;


    public static final String ID_IKONICE = "id_ikonice";
    public static final String NAZIV_KATEGORIJE = "naziv_kategorije";

    public static final String ID_KATEGORIJE = "id_kategorije";
    public static final String NAZIV_KVIZA = "naziv_kviza";
    public static final String NIZ_PITANJA = "niz_pitanja";

    public static final String INDEX_TACNOG = "id_kategorije";
    public static final String NAZIV_PITANJA = "naziv_pitanja";
    public static final String NIZ_ODGOVORA = "niz_odgovora";

    public static final String ID_RANGLISTE = "id_rangliste";
    public static final String NIZ_IGRACA = "niz_igraca";

    public static final String CREATE_TABLE_KATEGORIJE = "CREATE TABLE " + KATEGORIJE_TABLE + " (" + NAZIV_KATEGORIJE + " TEXT PRIMARY KEY, " + ID_IKONICE + " INTEGER);";
    public static final String CREATE_TABLE_KVIZOVI = "CREATE TABLE " + KVIZOVI_TABLE + " (" + NAZIV_KVIZA + " TEXT PRIMARY KEY, " + ID_KATEGORIJE + " INTEGER, " + NIZ_PITANJA +  " TEXT);";
    public static final String CREATE_TABLE_PITANJA = "CREATE TABLE " + PITANJA_TABLE + " (" + NAZIV_PITANJA + " TEXT PRIMARY KEY, " + INDEX_TACNOG + " INTEGER NOT NULL, " + NIZ_ODGOVORA + " TEXT NOT NULL);";
    public static final String CREATE_TABLE_RANGLISTE = "CREATE TABLE " + RANGLISTE_TABLE + " (" + ID_RANGLISTE + " TEXT PRIMARY KEY, " + NIZ_IGRACA + " TEXT NOT NULL);";

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
        database.execSQL(CREATE_TABLE_PITANJA);
        database.execSQL(CREATE_TABLE_RANGLISTE);
    }

    // Poziva se kada se ne poklapaju verzije baze na disku i trenutne baze
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Brisanje stare verzije
        database.execSQL("DROP TABLE IF EXISTS " + KATEGORIJE_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + KVIZOVI_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + PITANJA_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + RANGLISTE_TABLE);
        // Kreiranje nove
        onCreate(database);
    }

    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
