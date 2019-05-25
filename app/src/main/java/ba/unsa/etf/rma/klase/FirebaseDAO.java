package ba.unsa.etf.rma.klase;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

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
import java.util.ArrayList;

import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

public class FirebaseDAO {
    private static FirebaseDAO instance = new FirebaseDAO();

    private FirebaseDAO() {

    }

    public static FirebaseDAO getInstance() {
        return instance;
    }

    /*
    public interface KvizoviInterface {
        void addKvizovi (ArrayList<Kviz> kvizovi);
    }
    */


    @SuppressLint("StaticFieldLeak")
    public void dodajKviz(final Kviz kviz) {
        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                InputStream inputStream = getClass().getResourceAsStream("/res/raw/secret.json");
                GoogleCredential credentials;
                try {
                    credentials = GoogleCredential.fromStream(inputStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                    credentials.refreshToken();
                    String token = credentials.getAccessToken();
                    if (kviz.getKategorija() == null)
                        kviz.setKategorija(new Kategorija("Svi", "-1"));
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi?access_token=" + token;

                    URL url = new URL(urlString + URLEncoder.encode(token, "UTF-8"));
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");


                    String dokument = "{ \"fields\": {";
                    if (kviz.getPitanja().size() != 0) {
                        dokument += "\"pitanja\": {" +
                                "\"arrayValue\": {" +
                                "\"values\": [";
                        for (int i = 0; i < kviz.getPitanja().size(); i++) {
                            if (i != kviz.getPitanja().size() - 1)
                                dokument += "{\"stringValue\":\"" + kviz.getNaziv() + " | " + kviz.getPitanja().get(i).getNaziv() + "\"},";
                            else
                                dokument += "{\"stringValue\":\"" + kviz.getNaziv() + " | " + kviz.getPitanja().get(i).getNaziv() + "\"}";
                        }
                        dokument += "]}},";
                    }
                    dokument += "\"naziv\":{\"stringValue\":\"" + kviz.getNaziv() + "\"}," +
                            "\"idKategorije\":{\"stringValue\":\"" + kviz.getKategorija().getNaziv() + "\"}}}";


                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = dokument.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    /*
                    int code = urlConnection.getResponseCode();
                    InputStream odgovor = urlConnection.getInputStream();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(odgovor, "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null)
                            response.append((responseLine.trim()));
                        System.out.println("Odgovor" + response);
                    }
                    */
                } catch (IOException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void azuirajKviz(final Kviz postojeciKviz, final Kviz noviKviz) {
        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                InputStream inputStream = getClass().getResourceAsStream("/res/raw/secret.json");
                GoogleCredential credentials;
                try {
                    credentials = GoogleCredential.fromStream(inputStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                    credentials.refreshToken();
                    String token = credentials.getAccessToken();
                    getKviz(token, postojeciKviz.getNaziv());
                    dodajKviz(noviKviz);
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    @SuppressLint("StaticFieldLeak")
    public void kvizovi(final Kategorija kategorija, final KvizoviAkt kvizoviAkt) {
        final ArrayList<Kviz> kvizovi = new ArrayList<>();
        new AsyncTask<String, Integer, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                kvizoviAkt.addKvizovi(kvizovi);
            }

            @Override
            protected Void doInBackground(String... strings) {
                InputStream inputStream = getClass().getResourceAsStream("/res/raw/secret.json");
                GoogleCredential credentials;
                try {
                    credentials = GoogleCredential.fromStream(inputStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                    credentials.refreshToken();
                    String token = credentials.getAccessToken();
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi?access_token=" + token;
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

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
                        Kategorija kateg = getKategorija(token, idKategorije.getString("stringValue"));
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
                            Pitanje pitanje = getPitanje(token, idPitanja);
                            kviz.getPitanja().add(pitanje);
                        }
                        kvizovi.add(kviz);
                    }
                } catch (IOException | JSONException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static Pitanje getPitanje(String token, String idPitanja) throws IOException, JSONException {
        Pitanje pitanje = new Pitanje();
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Pitanja/" + idPitanja + "?access_token=" + token;
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
        String rezultat = convertStreamToString(in);
        JSONObject root = new JSONObject(rezultat);
        JSONObject fields = root.getJSONObject("fields");
        JSONObject indexTacnog = fields.getJSONObject("indexTacnog");
        int indTacnog = indexTacnog.getInt("integerValue");
        JSONObject naziv = fields.getJSONObject("naziv");
        pitanje.setNaziv(naziv.getString("stringValue"));
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

    private static Kategorija getKategorija(String token, String idKategorije) throws IOException, JSONException {
        if (idKategorije.equals("Svi"))
            return null;
        Kategorija kategorija = new Kategorija();
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kategorije/" + idKategorije + "?access_token=" + token;
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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

    private static Kviz getKviz(String token, String idKviza) throws IOException, JSONException {
        Kviz kviz = new Kviz();
        String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi/" + idKviza + "?access_token=" + token;
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
        String rezultat = convertStreamToString(in);
        JSONObject root = new JSONObject(rezultat);
        JSONObject fields = root.getJSONObject("fields");
        JSONObject naziv = fields.getJSONObject("naziv");
        JSONObject idKategorije = fields.getJSONObject("idKategorije");
        kviz.setNaziv(naziv.getString("stringValue"));
        Kategorija kateg = getKategorija(token, idKategorije.getString("stringValue"));
        kviz.setKategorija(kateg);
        JSONObject pitanja;
        try {
            pitanja = fields.getJSONObject("pitanja");
        } catch (JSONException ignored) {
            // nema pitanja
            return kviz;
        }
        JSONObject arrayValue = pitanja.getJSONObject("arrayValue");
        JSONArray values = arrayValue.getJSONArray("values");
        for (int j = 0; j < values.length(); j++) {
            JSONObject jsonObject = values.getJSONObject(j);
            String idPitanja = jsonObject.getString("stringValue");
            Pitanje pitanje = getPitanje(token, idPitanja);
            kviz.getPitanja().add(pitanje);
        }
        return kviz;
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
