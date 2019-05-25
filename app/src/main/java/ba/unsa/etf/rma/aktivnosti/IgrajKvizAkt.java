package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.FirebaseDAO;
import ba.unsa.etf.rma.klase.Kviz;

public class IgrajKvizAkt extends AppCompatActivity implements InformacijeFrag.OnFragmentInteractionListener, PitanjeFrag.OnFragmentInteractionListener, FirebaseDAO.RangListaInterface {
    private Kviz kviz;
    private PitanjeFrag pitanjeFrag;
    private InformacijeFrag informacijeFrag;
    private RangLista rangLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);
        getIntentData();
        dodajFragmente();
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
        FirebaseDAO.getInstance().rangLista(kviz, this);
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
    public void addIgraci(ArrayList<String> igraci) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ukloniFragment(fragmentManager, R.id.pitanjePlace);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        rangLista = RangLista.newInstance(igraci);
        fragmentTransaction.add(R.id.pitanjePlace, rangLista);
        fragmentTransaction.commit();
    }
}
