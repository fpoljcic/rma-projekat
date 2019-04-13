package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;

public class IgrajKvizAkt extends AppCompatActivity implements InformacijeFrag.OnFragmentInteractionListener, PitanjeFrag.OnFragmentInteractionListener {
    private Kviz kviz;
    private PitanjeFrag pitanjeFrag;
    private InformacijeFrag informacijeFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);
        // test da li je fragment vec dodat
        if (savedInstanceState != null)
            return;
        getIntentData();
        dodajFragmente();
    }

    private void dodajFragmente() {
        FragmentManager fragmentManager = getSupportFragmentManager();
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
}
