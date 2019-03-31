package ba.unsa.etf.rma.aktivnosti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import ba.unsa.etf.rma.R;

public class DodajPitanjeAkt extends AppCompatActivity {
    private EditText questionField, answerField;
    private Button addAnswerBtn, addRightAnswerBtn, addQuestionBtn;
    private ListView answersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje);
        linkControls();
    }

    private void linkControls() {
        questionField = (EditText) findViewById(R.id.etNaziv);
        answerField = (EditText) findViewById(R.id.etOdgovor);
        addAnswerBtn = (Button) findViewById(R.id.btnDodajOdgovor);
        addRightAnswerBtn = (Button) findViewById(R.id.btnDodajTacan);
        addQuestionBtn = (Button) findViewById(R.id.btnDodajPitanje);
        answersList = (ListView) findViewById(R.id.lvOdgovori);
    }
}
