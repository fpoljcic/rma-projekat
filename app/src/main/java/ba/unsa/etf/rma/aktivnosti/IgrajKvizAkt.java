package ba.unsa.etf.rma.aktivnosti;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;

public class IgrajKvizAkt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);
        dodajFragmente();
    }

    private void dodajFragmente() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PitanjeFrag pitanjeFrag = new PitanjeFrag();
        fragmentTransaction.add(R.id.pitanjePlace, pitanjeFrag);

        InformacijeFrag informacijeFrag = new InformacijeFrag();
        fragmentTransaction.add(R.id.informacijePlace, informacijeFrag);

        fragmentTransaction.commit();
    }
}
