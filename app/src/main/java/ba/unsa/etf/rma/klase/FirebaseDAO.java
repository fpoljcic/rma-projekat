package ba.unsa.etf.rma.klase;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FirebaseDAO {
    private static FirebaseDAO instance = new FirebaseDAO();

    private FirebaseDAO() {

    }

    public static FirebaseDAO getInstance() {
        return instance;
    }

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
                    String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kategorije?idKategorije=Testt&key=" + token;
                    String urlString2 = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi?fields=fields&key=" + token +
                            "{\"fields\":" +
                            "{\"idKategorije\": {\"stringValue\": \"" + kviz.getKategorija().getId() + "\"}," +
                            "\"naziv\": {\"stringValue\": \"" + kviz.getNaziv() + "\"}";
                    /*
                    if (kviz.getPitanja().size() == 0)
                        urlString += "}}";
                    else {
                        urlString += ",\"pitanja\": {\"arrayValue\": {\"values\": [";
                        for (int i = 0; i < kviz.getPitanja().size(); i++) {
                            if (i != kviz.getPitanja().size() - 1)
                                urlString += "{\"stringValue\": \"" + kviz.getPitanja().get(i) + "\"},";
                            else
                                urlString += "{\"stringValue\": \"" + kviz.getPitanja().get(i) + "\"}";
                        }
                        urlString += "]}}}}";
                    }
                    */
                    URL url = new URL(urlString);
                    url.openConnection();
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    int x = urlConnection.getResponseCode();
                    System.out.println(x);
                } catch (IOException greska) {
                    greska.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /*
    private static class BackgroundTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... params) {
            InputStream is = getClass().getResourceAsStream("/res/raw/secret.json");
            GoogleCredential credentials = null;
            try {
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            String TOKEN = null;
            if (credentials != null)
                TOKEN = credentials.getAccessToken();
            System.out.println(TOKEN);
            String urlString2 = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi/Bxu0giYw1NJ1SDpQc05q?access_token=" + TOKEN;
            String urlString = "https://firestore.googleapis.com/v1/projects/rma19poljcicfaris20/databases/(default)/documents/Kvizovi?fields=fields&key=" + TOKEN + "\n" +
                    "\n" +
                    "{\n" +
                    " \"fields\": {\n" +
                    "  \"idKategorije\": {\n" +
                    "   \"stringValue\": \"IDKATEGORIJE2\"\n" +
                    "  },\n" +
                    "  \"naziv\": {\n" +
                    "   \"stringValue\": \"CustomKviz\"\n" +
                    "  },\n" +
                    "  \"pitanja\": {\n" +
                    "   \"arrayValue\": {\n" +
                    "    \"values\": [\n" +
                    "     {\n" +
                    "      \"stringValue\": \"1.pitanjeee\"\n" +
                    "     },\n" +
                    "     {\n" +
                    "      \"stringValue\": \"2. pitanmajnekbvhj\"\n" +
                    "     }\n" +
                    "    ]\n" +
                    "   }\n" +
                    "  }\n" +
                    " }\n" +
                    "}";
            URL url = null;
            try {
                url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // ----------------------------------
                urlConnection.setRequestProperty("Authorization", "Bearer " + TOKEN);
                InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
                String rezultat = convertStreamToString(in);
                JSONObject jo = new JSONObject(rezultat);

                JSONObject naziv = jo.getJSONObject("naziv");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {} finally {
            try {
                is.close();
            } catch (IOException e) {}
        }
        return sb.toString();
    }
    */
}
