package ba.unsa.etf.rma.klase;

import android.os.AsyncTask;
import android.util.Pair;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class Firebase {
    private static String token = "";

    private static HttpURLConnection getHTTPConnection(String urlString, boolean refresh) throws IOException {
        if (refresh) {
            InputStream inputStream = Firebase.class.getResourceAsStream("/res/raw/secret.json");
            GoogleCredential credentials;
            if (inputStream == null)
                throw new IOException("Problem sa citanjem secret.json datoteke!");
            credentials = GoogleCredential.fromStream(inputStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
            credentials.refreshToken();
            token = credentials.getAccessToken();
        }
        URL url = new URL(urlString + URLEncoder.encode(token, "UTF-8"));
        return (HttpURLConnection) url.openConnection();
    }

    public static void dodajKviz(final Kviz kviz, final ArrayList<String> idPitanja) {
        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    DatabaseHelper.getInstance().dodajKviz(kviz);
                    dodajKvizFun(kviz, idPitanja);
                } catch (IOException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static void dodajKvizFun(final Kviz kviz, final ArrayList<String> idPitanja) throws IOException {
        String naziv = kviz.getNaziv().replaceAll(" ", "_");
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi?documentId=" + naziv + "&access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        String idKategorije;
        if (kviz.getKategorija() == null)
            idKategorije = "Svi";
        else
            idKategorije = kviz.getKategorija().getNaziv().replaceAll(" ", "_");
        String dokument = "{ \"fields\": {";
        if (idPitanja.size() != 0) {
            dokument += "\"pitanja\": {\"arrayValue\": {\"values\": [";
            for (int i = 0; i < idPitanja.size(); i++) {
                if (i != idPitanja.size() - 1)
                    dokument += "{\"stringValue\":\"" + idPitanja.get(i) + "\"},";
                else
                    dokument += "{\"stringValue\":\"" + idPitanja.get(i) + "\"}";
            }
            dokument += "]}},";
        }
        dokument += "\"naziv\":{\"stringValue\":\"" + kviz.getNaziv() + "\"}," +
                "\"idKategorije\":{\"stringValue\":\"" + idKategorije + "\"}}}";

        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        urlConnection.getResponseCode();
    }

    public static void azuirajKviz(final String nazivPostojecegKviza, final Kviz noviKviz, final ArrayList<String> idPitanja) {
        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    DatabaseHelper.getInstance().azurirajKviz(nazivPostojecegKviza, noviKviz);
                    if (!nazivPostojecegKviza.equals(noviKviz.getNaziv())) {
                        dodajKvizFun(noviKviz, idPitanja);
                        obrisiKviz(nazivPostojecegKviza);
                        StringBuilder idRangliste = new StringBuilder();
                        if (postojiRanglista(nazivPostojecegKviza, idRangliste)) {
                            DatabaseHelper.getInstance().updateNazivRangliste(nazivPostojecegKviza, noviKviz.getNaziv());
                            updateNazivRangliste(idRangliste, nazivPostojecegKviza, noviKviz.getNaziv());
                        }
                    } else
                        updateKviz(noviKviz, idPitanja);
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static void updateKviz(Kviz noviKviz, ArrayList<String> idPitanja) throws IOException {
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi/" + noviKviz.getNaziv().replaceAll(" ", "_") + "?access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PATCH");
        urlConnection.setRequestProperty("Content-Type", "application/json-patch");
        urlConnection.setRequestProperty("Accept", "application/json");
        String idKategorije;
        if (noviKviz.getKategorija() == null)
            idKategorije = "Svi";
        else
            idKategorije = noviKviz.getKategorija().getNaziv().replaceAll(" ", "_");
        String dokument = "{ \"fields\": {";
        if (idPitanja.size() != 0) {
            dokument += "\"pitanja\": {\"arrayValue\": {\"values\": [";
            for (int i = 0; i < idPitanja.size(); i++) {
                if (i != idPitanja.size() - 1)
                    dokument += "{\"stringValue\":\"" + idPitanja.get(i) + "\"},";
                else
                    dokument += "{\"stringValue\":\"" + idPitanja.get(i) + "\"}";
            }
            dokument += "]}},";
        }
        dokument += "\"naziv\":{\"stringValue\":\"" + noviKviz.getNaziv() + "\"}," +
                "\"idKategorije\":{\"stringValue\":\"" + idKategorije + "\"}}}";

        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        urlConnection.getResponseCode();
    }

    private static void obrisiKviz(String id) throws IOException {
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi/" + id.replace(" ", "_") + "?access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, false);
        urlConnection.setRequestMethod("DELETE");
        urlConnection.getResponseCode();
    }

    public static void pitanja(final PitanjeInterface pitanjeInterface) {
        new AsyncTask<String, Integer, ArrayList<Pair<String, Pitanje>>>() {
            @Override
            protected void onPostExecute(ArrayList<Pair<String, Pitanje>> pitanja) {
                super.onPostExecute(pitanja);
                pitanjeInterface.addPitanja(pitanja);
            }

            @Override
            protected ArrayList<Pair<String, Pitanje>> doInBackground(String... strings) {
                if (!NetworkChangeReceiver.INTERNET_ACCESS)
                    return DatabaseHelper.getInstance().pitanja();
                ArrayList<Pair<String, Pitanje>> pitanja = new ArrayList<>();
                try {
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Pitanja?access_token=";
                    HttpURLConnection urlConnection = getHTTPConnection(urlString, true);

                    InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
                    String rezultat = convertStreamToString(in);
                    JSONObject root = new JSONObject(rezultat);
                    JSONArray documents = root.getJSONArray("documents");
                    for (int i = 0; i < documents.length(); i++) {
                        Pitanje pitanje = new Pitanje();
                        JSONObject pitanjeJson = documents.getJSONObject(i);
                        JSONObject fields = pitanjeJson.getJSONObject("fields");
                        JSONObject naziv = fields.getJSONObject("naziv");
                        String nazivPitanja = naziv.getString("stringValue");
                        String name = pitanjeJson.getString("name");
                        String idPitanja = name.substring(name.lastIndexOf("/") + 1);
                        pitanje.setNaziv(nazivPitanja);
                        pitanje.setTekstPitanja(nazivPitanja);
                        JSONObject odgovori;
                        odgovori = fields.getJSONObject("odgovori");
                        JSONObject arrayValue = odgovori.getJSONObject("arrayValue");
                        JSONArray values = arrayValue.getJSONArray("values");
                        for (int j = 0; j < values.length(); j++) {
                            JSONObject jsonObject = values.getJSONObject(j);
                            pitanje.getOdgovori().add(jsonObject.getString("stringValue"));
                        }
                        JSONObject indexTacnog = fields.getJSONObject("indexTacnog");
                        pitanje.setTacan(pitanje.getOdgovori().get(indexTacnog.getInt("integerValue")));
                        pitanja.add(new Pair<>(idPitanja, pitanje));
                    }
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return pitanja;
            }
        }.execute();
    }

    private static boolean postojiRanglista(final String nazivKviza, StringBuilder idKviza) {
        try {
            String dokument = "{\"structuredQuery\": {" +
                    "\"where\" : {" +
                    "\"fieldFilter\" : { " +
                    "\"field\": {\"fieldPath\": \"nazivKviza\"}, " +
                    "\"op\":\"EQUAL\", " +
                    "\"value\": {\"stringValue\": \"" + nazivKviza + "\"}}}," +
                    "\"select\": { \"fields\": [{\"fieldPath\": \"naziv\"} ] }, " +
                    "\"from\": [{\"collectionId\": \"" + "Rangliste" + "\"}]," +
                    "\"limit\": 1000 }}";
            String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents:runQuery?access_token=";
            HttpURLConnection urlConnection = getHTTPConnection(urlString, true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            urlConnection.getResponseCode();

            InputStream in = urlConnection.getInputStream();
            String rezultat = convertStreamToString(in);
            rezultat = "{\"documents\": " + rezultat + "}";
            JSONObject jsonObject = new JSONObject(rezultat);
            JSONArray documents = jsonObject.getJSONArray("documents");
            JSONObject jsonObject2 = documents.getJSONObject(0);

            try {
                jsonObject2.getJSONObject("document");
                JSONObject document = jsonObject2.getJSONObject("document");
                String id = document.getString("name");
                id = id.substring(id.lastIndexOf("/") + 1);
                idKviza.append(id);
            } catch (JSONException ignored) {
                // Ne postoji dokument sa datim id-om
                return false;
            }
        } catch (IOException | JSONException greska) {
            greska.printStackTrace();
        }
        return true;
    }

    public static void dodajIgraca(final String nazivKviza, final String ime, final double procenatTacnih, final boolean firebaseSync) {
        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                if (!firebaseSync && !DatabaseHelper.getInstance().dodajIgraca(nazivKviza, procenatTacnih, ime))
                    return null; // vec postoji isti igrac sa istim procentom
                try {
                    StringBuilder idRangliste = new StringBuilder();
                    if (!postojiRanglista(nazivKviza, idRangliste))
                        dodajRanglistu(nazivKviza, ime, procenatTacnih);
                    else
                        updateRanglistu(idRangliste.toString(), nazivKviza, ime, procenatTacnih);
                } catch (IOException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static void dodajRanglistu(String nazivKviza, String ime, double procenatTacnih) throws IOException {
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Rangliste?access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, false);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        String dokument = "{ \"fields\": {\"nazivKviza\":{\"stringValue\":\"" + nazivKviza + "\"}," +
                "\"lista\":{\"mapValue\":{\"fields\":{\"" + "1" + "\": {" +
                "\"mapValue\":{\"fields\":{\"" + ime + "\": {\"doubleValue\":\"" + procenatTacnih + "\"}}}}}}}}}";
        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        urlConnection.getResponseCode();
    }

    private static ArrayList<Pair<String, Double>> vratiRanglistu(String idRangliste) {
        ArrayList<Pair<String, Double>> igraci = new ArrayList<>();
        try {
            String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Rangliste/" + idRangliste + "?access_token=";
            HttpURLConnection urlConnection = getHTTPConnection(urlString, false);

            InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
            String rezultat = convertStreamToString(in);
            JSONObject root = new JSONObject(rezultat);
            JSONObject fields = root.getJSONObject("fields");
            JSONObject lista = fields.getJSONObject("lista");
            JSONObject mapValue = lista.getJSONObject("mapValue");
            JSONObject fields1 = mapValue.getJSONObject("fields");
            Iterator<String> keys = fields1.keys();
            while (keys.hasNext()) {
                String pozicijaString = keys.next();
                JSONObject pozicija = fields1.getJSONObject(pozicijaString);
                JSONObject mapValue1 = pozicija.getJSONObject("mapValue");
                JSONObject fields2 = mapValue1.getJSONObject("fields");
                Iterator<String> keys1 = fields2.keys();
                while (keys1.hasNext()) {
                    String imeString = keys1.next();
                    JSONObject ime = fields2.getJSONObject(imeString);
                    double procenatTacnih = ime.getDouble("doubleValue");
                    igraci.add(new Pair<>(imeString, procenatTacnih));
                }
            }
        } catch (IOException | JSONException greska) {
            greska.printStackTrace();
        }
        return igraci;
    }

    private static void updateRanglistu(String idRangliste, String nazivKviza, String ime, double procenatTacnih) throws IOException {
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Rangliste/" + idRangliste + "?access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, false);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PATCH");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        ArrayList<Pair<String, Double>> igraci = vratiRanglistu(idRangliste);
        igraci.add(new Pair<>(ime, procenatTacnih));
        Collections.sort(igraci, new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> s1, Pair<String, Double> s2) {
                return s2.second.compareTo(s1.second);
            }
        });
        String dokument = "{ \"fields\": {\"nazivKviza\":{\"stringValue\":\"" + nazivKviza + "\"},\"lista\":{\"mapValue\":{\"fields\":{";
        for (int i = 0; i < igraci.size(); i++) {
            if (i != igraci.size() - 1)
                dokument += "\"" + (i + 1) + "\": {\"mapValue\":{\"fields\":{\"" + igraci.get(i).first + "\": {\"doubleValue\":\"" + igraci.get(i).second + "\"}}}},";
            else
                dokument += "\"" + (i + 1) + "\": {\"mapValue\":{\"fields\":{\"" + igraci.get(i).first + "\": {\"doubleValue\":\"" + igraci.get(i).second + "\"}}}}";
        }
        dokument += "}}}}}";

        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        urlConnection.getResponseCode();
    }

    private static void updateNazivRangliste(StringBuilder idRangliste, String nazivPostojecegKviza, String noviNazivKviza) throws IOException, JSONException {
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Rangliste/" + idRangliste.toString() + "?access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, false);

        InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
        String rezultat = convertStreamToString(in);
        JSONObject root = new JSONObject(rezultat);

        JSONObject fields = root.getJSONObject("fields");
        rezultat = fields.toString();
        rezultat = rezultat.replaceAll("\"" + nazivPostojecegKviza + "\"", "\"" + noviNazivKviza + "\"");
        rezultat = "{ \"fields\": " + rezultat + "}";
        urlConnection = getHTTPConnection(urlString, false);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PATCH");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = rezultat.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        urlConnection.getResponseCode();
    }

    public interface KategorijaProvjera {
        void kategorijaProvjeraZavrsena(boolean postoji);
    }

    public static void containtsKategoriju(final String nazivKategorije, final KategorijaProvjera kategorijaProvjera) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPostExecute(Boolean postoji) {
                super.onPostExecute(postoji);
                kategorijaProvjera.kategorijaProvjeraZavrsena(postoji);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return provjeri("Kategorije", nazivKategorije);
            }
        }.execute();
    }

    public interface PitanjeProvjera {
        void pitanjeProvjeraZavrsena(boolean postoji);
    }

    public static void containtsPitanje(final String nazivPitanja, final PitanjeProvjera pitanjeProvjera) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPostExecute(Boolean postoji) {
                super.onPostExecute(postoji);
                pitanjeProvjera.pitanjeProvjeraZavrsena(postoji);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return provjeri("Pitanja", nazivPitanja);
            }
        }.execute();
    }

    public interface KvizProvjera {
        void kvizProvjeraZavrsena(boolean postoji);
    }

    public static void containtsKviz(final String nazivKviza, final KvizProvjera kvizProvjera) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPostExecute(Boolean postoji) {
                super.onPostExecute(postoji);
                kvizProvjera.kvizProvjeraZavrsena(postoji);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return provjeri("Kvizovi", nazivKviza);
            }
        }.execute();
    }

    private static boolean provjeri(final String nazivKolekcije, final String nazivString) {
        try {
            String dokument = "{\"structuredQuery\": {" +
                    "\"where\" : {" +
                    "\"fieldFilter\" : { " +
                    "\"field\": {\"fieldPath\": \"naziv\"}, " +
                    "\"op\":\"EQUAL\", " +
                    "\"value\": {\"stringValue\": \"" + nazivString + "\"}}}," +
                    "\"select\": { \"fields\": [{\"fieldPath\": \"naziv\"} ] }, " +
                    "\"from\": [{\"collectionId\": \"" + nazivKolekcije + "\"}]," +
                    "\"limit\": 1000 }}";
            String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents:runQuery?access_token=";
            HttpURLConnection urlConnection = getHTTPConnection(urlString, true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            urlConnection.getResponseCode();

            InputStream in = urlConnection.getInputStream();
            String rezultat = convertStreamToString(in);
            rezultat = "{\"documents\": " + rezultat + "}";
            JSONObject jsonObject = new JSONObject(rezultat);
            JSONArray documents = jsonObject.getJSONArray("documents");
            JSONObject jsonObject2 = documents.getJSONObject(0);

            try {
                jsonObject2.getJSONObject("document");
            } catch (JSONException ignored) {
                // Ne postoji dokument sa datim id-om
                return false;
            }
        } catch (IOException | JSONException greska) {
            greska.printStackTrace();
            return false;
        }
        return true;
    }

    public interface RangListaInterface {
        void addIgraci(ArrayList<String> igraci, Kviz kviz);
    }

    public static void rangLista(final Kviz kviz, final RangListaInterface rangListaInterface) {
        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPostExecute(ArrayList<String> igraci) {
                super.onPostExecute(igraci);
                Collections.sort(igraci, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        int poz1 = Integer.valueOf(s1.substring(0, s1.indexOf(".")));
                        int poz2 = Integer.valueOf(s2.substring(0, s2.indexOf(".")));
                        return poz1 - poz2;
                    }
                });
                rangListaInterface.addIgraci(igraci, kviz);
            }

            @Override
            protected ArrayList<String> doInBackground(String... strings) {
                if (!NetworkChangeReceiver.INTERNET_ACCESS)
                    return DatabaseHelper.getInstance().rangLista(kviz);
                ArrayList<String> igraci = new ArrayList<>();
                StringBuilder idRangliste = new StringBuilder();
                if (!postojiRanglista(kviz.getNaziv(), idRangliste))
                    return igraci;
                try {
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Rangliste/" + idRangliste.toString() + "?access_token=";
                    HttpURLConnection urlConnection = getHTTPConnection(urlString, true);

                    InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
                    String rezultat = convertStreamToString(in);
                    JSONObject root = new JSONObject(rezultat);

                    JSONObject fields = root.getJSONObject("fields");
                    JSONObject lista = fields.getJSONObject("lista");
                    JSONObject mapValue = lista.getJSONObject("mapValue");
                    JSONObject fields1 = mapValue.getJSONObject("fields");
                    Iterator<String> keys = fields1.keys();
                    while (keys.hasNext()) {
                        String pozicijaString = keys.next();
                        JSONObject pozicija = fields1.getJSONObject(pozicijaString);
                        JSONObject mapValue1 = pozicija.getJSONObject("mapValue");
                        JSONObject fields2 = mapValue1.getJSONObject("fields");
                        Iterator<String> keys1 = fields2.keys();
                        while (keys1.hasNext()) {
                            String imeString = keys1.next();
                            JSONObject ime = fields2.getJSONObject(imeString);
                            double procenatTacnih = ime.getDouble("doubleValue");
                            String igrac = pozicijaString + ". " + imeString + " - " + procenatTacnih + "%";
                            igraci.add(igrac);
                        }
                    }
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return igraci;
            }
        }.execute();
    }

    public interface KategorijaInterface {
        void addKategorijeFirebase(ArrayList<Kategorija> kategorije);
    }

    public static void kategorije(final KategorijaInterface kategorijaInterface) {
        new AsyncTask<String, Integer, ArrayList<Kategorija>>() {
            @Override
            protected void onPostExecute(ArrayList<Kategorija> kategorije) {
                super.onPostExecute(kategorije);
                kategorijaInterface.addKategorijeFirebase(kategorije);
            }

            @Override
            protected ArrayList<Kategorija> doInBackground(String... strings) {
                if (!NetworkChangeReceiver.INTERNET_ACCESS)
                    return DatabaseHelper.getInstance().kategorije();
                ArrayList<Kategorija> kategorije = new ArrayList<>();
                try {
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kategorije?access_token=";
                    HttpURLConnection urlConnection = getHTTPConnection(urlString, true);

                    InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
                    String rezultat = convertStreamToString(in);
                    JSONObject root = new JSONObject(rezultat);
                    JSONArray documents = root.getJSONArray("documents");
                    for (int i = 0; i < documents.length(); i++) {
                        Kategorija kategorija = new Kategorija();
                        JSONObject kvizJson = documents.getJSONObject(i);
                        JSONObject fields = kvizJson.getJSONObject("fields");
                        JSONObject naziv = fields.getJSONObject("naziv");
                        kategorija.setNaziv(naziv.getString("stringValue"));
                        JSONObject idIkonice = fields.getJSONObject("idIkonice");
                        kategorija.setId(idIkonice.getString("stringValue"));
                        kategorije.add(kategorija);
                    }
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return kategorije;
            }
        }.execute();
    }

    public interface KvizInterface {
        void addKvizovi(ArrayList<Kviz> kvizovi);
    }

    public static void kvizovi(final Kategorija kategorija, final KvizInterface kvizInterface) {
        new AsyncTask<String, Integer, ArrayList<Kviz>>() {
            @Override
            protected void onPostExecute(ArrayList<Kviz> kvizovi) {
                super.onPostExecute(kvizovi);
                kvizInterface.addKvizovi(kvizovi);
            }

            @Override
            protected ArrayList<Kviz> doInBackground(String... strings) {
                if (!NetworkChangeReceiver.INTERNET_ACCESS)
                    return DatabaseHelper.getInstance().kvizovi(kategorija);
                ArrayList<Kviz> kvizovi = new ArrayList<>();
                try {
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi?access_token=";
                    HttpURLConnection urlConnection = getHTTPConnection(urlString, true);

                    InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
                    String rezultat = convertStreamToString(in);
                    JSONObject root = new JSONObject(rezultat);
                    JSONArray documents = root.getJSONArray("documents");
                    for (int i = 0; i < documents.length(); i++) {
                        Kviz kviz = new Kviz();
                        JSONObject kvizJson = documents.getJSONObject(i);
                        JSONObject fields = kvizJson.getJSONObject("fields");
                        JSONObject naziv = fields.getJSONObject("naziv");
                        JSONObject idKategorije = fields.getJSONObject("idKategorije");
                        kviz.setNaziv(naziv.getString("stringValue"));
                        Kategorija kateg = getKategorija(idKategorije.getString("stringValue"));
                        if (kategorija != null && (kateg == null || !kateg.equals(kategorija)))
                            continue;
                        kviz.setKategorija(kateg);
                        JSONObject pitanja;
                        try {
                            pitanja = fields.getJSONObject("pitanja");
                        } catch (JSONException ignored) {
                            // nema pitanja
                            kvizovi.add(kviz);
                            continue;
                        }
                        JSONObject arrayValue = pitanja.getJSONObject("arrayValue");
                        JSONArray values = arrayValue.getJSONArray("values");
                        for (int j = 0; j < values.length(); j++) {
                            JSONObject jsonObject = values.getJSONObject(j);
                            String idPitanja = jsonObject.getString("stringValue");
                            Pitanje pitanje = getPitanje(idPitanja);
                            kviz.getPitanja().add(pitanje);
                        }
                        kvizovi.add(kviz);
                    }
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return kvizovi;
            }
        }.execute();
    }

    private static Pitanje getPitanje(String idPitanja) throws IOException, JSONException {
        Pitanje pitanje = new Pitanje();
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Pitanja/" + idPitanja + "?access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, false);
        InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
        String rezultat = convertStreamToString(in);
        JSONObject root = new JSONObject(rezultat);
        JSONObject fields = root.getJSONObject("fields");
        JSONObject indexTacnog = fields.getJSONObject("indexTacnog");
        int indTacnog = indexTacnog.getInt("integerValue");
        JSONObject naziv = fields.getJSONObject("naziv");
        pitanje.setNaziv(naziv.getString("stringValue"));
        pitanje.setTekstPitanja(naziv.getString("stringValue"));
        JSONObject odgovori = fields.getJSONObject("odgovori");
        JSONObject arrayValue = odgovori.getJSONObject("arrayValue");
        JSONArray values = arrayValue.getJSONArray("values");
        for (int i = 0; i < values.length(); i++) {
            String stringValue = values.getJSONObject(i).getString("stringValue");
            pitanje.getOdgovori().add(stringValue);
            if (i == indTacnog)
                pitanje.setTacan(stringValue);
        }
        return pitanje;
    }

    private static Kategorija getKategorija(String idKategorije) throws IOException, JSONException {
        if (idKategorije.equals("Svi"))
            return null;
        Kategorija kategorija = new Kategorija();
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kategorije/" + idKategorije.replaceAll(" ", "_") + "?access_token=";
        HttpURLConnection urlConnection = getHTTPConnection(urlString, false);
        InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
        String rezultat = convertStreamToString(in);
        JSONObject root = new JSONObject(rezultat);
        JSONObject fields = root.getJSONObject("fields");
        JSONObject idIkonice = fields.getJSONObject("idIkonice");
        kategorija.setId(idIkonice.getString("stringValue"));
        JSONObject naziv = fields.getJSONObject("naziv");
        kategorija.setNaziv(naziv.getString("stringValue"));
        return kategorija;
    }

    public interface PitanjeInterface {
        void getPitanjeId(Pair<String, Pitanje> idPitanja);

        void addPitanja(ArrayList<Pair<String, Pitanje>> pitanja);
    }

    public static void dodajPitanje(final Pitanje pitanje, final PitanjeInterface pitanjeInterface) {
        final Pitanje pitanjePair = new Pitanje();
        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPostExecute(ArrayList<String> idPair) {
                super.onPostExecute(idPair);
                Pair<String, Pitanje> pair;
                if (idPair.size() != 0)
                    pair = new Pair<>(idPair.get(0), pitanjePair);
                else
                    pair = new Pair<>(null, pitanjePair);
                pitanjeInterface.getPitanjeId(pair);
            }

            @Override
            protected ArrayList<String> doInBackground(String... strings) {
                DatabaseHelper.getInstance().dodajPitanje(pitanje);
                ArrayList<String> idPair = new ArrayList<>();
                try {
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Pitanja?access_token=";
                    HttpURLConnection urlConnection = getHTTPConnection(urlString, true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");


                    String dokument = "{ \"fields\": {";
                    dokument += "\"odgovori\": {" +
                            "\"arrayValue\": {" +
                            "\"values\": [";
                    for (int i = 0; i < pitanje.getOdgovori().size(); i++) {
                        if (i != pitanje.getOdgovori().size() - 1)
                            dokument += "{\"stringValue\":\"" + pitanje.getOdgovori().get(i) + "\"},";
                        else
                            dokument += "{\"stringValue\":\"" + pitanje.getOdgovori().get(i) + "\"}";
                    }
                    dokument += "]}},";
                    dokument += "\"naziv\":{\"stringValue\":\"" + pitanje.getNaziv() + "\"}," +
                            "\"indexTacnog\":{\"integerValue\":\"" + pitanje.getOdgovori().indexOf(pitanje.getTacan()) + "\"}}}";


                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    urlConnection.getResponseCode();

                    InputStream odgovor = urlConnection.getInputStream();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null)
                            response.append((responseLine.trim()));
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String name = jsonObject.getString("name");
                        idPair.add(name.substring(name.lastIndexOf("/") + 1));
                        pitanjePair.setNaziv(pitanje.getNaziv());
                        pitanjePair.setTacan(pitanje.getTacan());
                        pitanjePair.setOdgovori(pitanje.getOdgovori());
                        pitanjePair.setTekstPitanja(pitanje.getTekstPitanja());
                    }
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return idPair;
            }
        }.execute();
    }

    public static void dodajKategoriju(final Kategorija kategorija) {
        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                DatabaseHelper.getInstance().dodajKategoriju(kategorija);
                try {
                    String naziv = kategorija.getNaziv().replaceAll(" ", "_");
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kategorije?documentId=" + naziv + "&access_token=";

                    HttpURLConnection urlConnection = getHTTPConnection(urlString, true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");


                    String dokument = "{ \"fields\": {";
                    dokument += "\"naziv\":{\"stringValue\":\"" + kategorija.getNaziv() + "\"}," +
                            "\"idIkonice\":{\"stringValue\":\"" + kategorija.getId() + "\"}}}";

                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    urlConnection.getResponseCode();

                } catch (IOException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ignored) {
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
        return sb.toString();
    }

}