package ba.unsa.etf.rma.aktivnosti;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class DodajPitanjeAkt extends AppCompatActivity {
    private EditText questionField, answerField;
    private Button addAnswerBtn, addRightAnswerBtn, addQuestionBtn;
    private ListView answersList;
    private ArrayList<String> odgovori = new ArrayList<>();
    private String tacanOdgovor;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje);
        linkControls();
        setListeners();
    }


    private void linkControls() {
        questionField = findViewById(R.id.etNaziv);
        answerField = findViewById(R.id.etOdgovor);
        addAnswerBtn = findViewById(R.id.btnDodajOdgovor);
        addRightAnswerBtn = findViewById(R.id.btnDodajTacan);
        addQuestionBtn = findViewById(R.id.btnDodajPitanje);
        answersList = findViewById(R.id.lvOdgovori);

        setAdapter();
    }

    private void setAdapter() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, odgovori) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                if (odgovori.get(position).equals(tacanOdgovor))
                    text.setTextColor(Color.GREEN);
                else
                    text.setTextColor(Color.BLACK);
                return view;
            }
        };
        answersList.setAdapter(adapter);
    }

    private void setListeners() {
        answersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (odgovori.get(position).equals(tacanOdgovor))
                    tacanOdgovor = null;
                odgovori.remove(odgovori.get(position));
                adapter.notifyDataSetChanged();
            }
        });
        addAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answerField.getText().toString().isEmpty()) {
                    odgovori.add(answerField.getText().toString());
                    answerField.getText().clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        addRightAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tacanOdgovor == null && !answerField.getText().toString().isEmpty()) {
                    odgovori.add(answerField.getText().toString());
                    tacanOdgovor = answerField.getText().toString();
                    answerField.getText().clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
