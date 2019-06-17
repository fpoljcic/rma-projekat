package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.DatabaseHelper;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.NetworkChangeReceiver;

public class IgrajKvizAkt extends AppCompatActivity implements InformacijeFrag.OnFragmentInteractionListener, PitanjeFrag.OnFragmentInteractionListener, Firebase.RangListaInterface, NetworkChangeReceiver.NetworkInterface {
    private Kviz kviz;
    private PitanjeFrag pitanjeFrag;
    private InformacijeFrag informacijeFrag;
    private RangLista rangLista;
    private boolean isActive = true;
    private IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private NetworkChangeReceiver receiver = new NetworkChangeReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);
        getIntentData();
        dodajFragmente();
        if (kviz.getPitanja().size() != 0)
            setAlarm();
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

    private void setAlarm() {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        int minute = time.minute;
        if (time.second > 0)
            minute++;
        int x = (int) Math.ceil(kviz.getPitanja().size() / 2.0);
        x += minute;
        while (x >= 60) {
            hour++;
            x -= 60;
        }
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "Alarm za kraj kviza: " + kviz.getNaziv());
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, x);
        alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        startActivity(alarmIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    private void ukloniFragment(FragmentManager fragmentManager, int resource) {
        Fragment fragment = fragmentManager.findFragmentById(resource);
        if (fragment != null)
            fragmentManager.beginTransaction().remove(fragment).commit();
    }

    private void dodajFragmente() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ukloniFragment(fragmentManager, R.id.pitanjePlace);
        ukloniFragment(fragmentManager, R.id.informacijePlace);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        pitanjeFrag = PitanjeFrag.newInstance(kviz.getPitanja());
        fragmentTransaction.add(R.id.pitanjePlace, pitanjeFrag);

        informacijeFrag = InformacijeFrag.newInstance(kviz.getNaziv(), kviz.getPitanja().size());
        fragmentTransaction.add(R.id.informacijePlace, informacijeFrag);

        fragmentTransaction.commit();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        kviz = (Kviz) intent.getSerializableExtra("kviz");
    }

    @Override
    public void onEndQuizButtonPressed() {
        Intent endIntent = new Intent();
        setResult(RESULT_OK, endIntent);
        finish();
    }

    @Override
    public boolean onAnswerListItemClick(boolean correct) {
        return informacijeFrag.updateData(correct);
    }

    @Override
    public void onQuizFinish() {
        Firebase.rangLista(kviz, this);
    }

    @Override
    public String vratiNazivKviza() {
        return kviz.getNaziv();
    }

    @Override
    public double vratiProcenatTacnih() {
        return informacijeFrag.getProcenatTacnih();
    }

    @Override
    public void addIgraci(ArrayList<String> igraci, Kviz kviz) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ukloniFragment(fragmentManager, R.id.pitanjePlace);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        rangLista = RangLista.newInstance(igraci);
        fragmentTransaction.add(R.id.pitanjePlace, rangLista);
        if (isActive)
            fragmentTransaction.commit();
    }

    @Override
    public void notifyNetChange(boolean internetAccess) {
        if (internetAccess) {
            DatabaseHelper.getInstance().syncFirebase();
            DatabaseHelper.getInstance().syncFirebaseIgraci();
        }
    }
}
