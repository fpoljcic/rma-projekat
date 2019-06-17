package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.NetworkChangeReceiver;

public class DodajPitanjeAkt extends AppCompatActivity implements Firebase.PitanjeProvjera {
    private EditText questionField, answerField;
    private Button addAnswerBtn, addRightAnswerBtn, addQuestionBtn;
    private ListView answersList;
    private ArrayList<String> odgovori = new ArrayList<>();
    private String tacanOdgovor;
    private ArrayAdapter<String> adapter;
    private AlertDialog alert;

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
                    text.setBackgroundResource(R.color.colorRight);
                else
                    text.setBackgroundResource(R.color.colorDefaultBackground);
                return view;
            }
        };
        answersList.setAdapter(adapter);
    }

    private void setListeners() {
        answersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (odgovori.get(position).equals(tacanOdgovor)) {
                    tacanOdgovor = null;
                    addRightAnswerBtn.setEnabled(true);
                    addRightAnswerBtn.setClickable(true);
                }
                odgovori.remove(odgovori.get(position));
                adapter.notifyDataSetChanged();
            }
        });
        addAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answerField.getText().toString().isEmpty() && !odgovori.contains(answerField.getText().toString())) {
                    odgovori.add(answerField.getText().toString());
                    answerField.getText().clear();
                    answerField.setBackgroundResource(R.color.colorDefaultBackground);
                    adapter.notifyDataSetChanged();
                } else
                    answerField.setBackgroundResource(R.color.colorError);
            }
        });
        addRightAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answerField.getText().toString().isEmpty() && !odgovori.contains(answerField.getText().toString())) {
                    odgovori.add(answerField.getText().toString());
                    tacanOdgovor = answerField.getText().toString();
                    answerField.getText().clear();
                    addRightAnswerBtn.setEnabled(false);
                    addRightAnswerBtn.setClickable(false);
                    answerField.setBackgroundResource(R.color.colorDefaultBackground);
                    adapter.notifyDataSetChanged();
                } else
                    answerField.setBackgroundResource(R.color.colorError);
            }
        });
        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkChangeReceiver.INTERNET_ACCESS) {
                    Toast.makeText(getApplicationContext(), "Nemate internet konekcije!", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean errorPresent = false;
                if (questionField.getText().toString().isEmpty()) {
                    errorPresent = true;
                    questionField.setBackgroundResource(R.color.colorError);
                } else
                    questionField.setBackgroundResource(R.color.colorDefaultBackground);
                if (tacanOdgovor == null) {
                    errorPresent = true;
                    answerField.setBackgroundResource(R.color.colorError);
                } else
                    answerField.setBackgroundResource(R.color.colorDefaultBackground);
                if (errorPresent)
                    return;
                addQuestionBtn.setText(R.string.sacekajte);
                Firebase.containtsPitanje(questionField.getText().toString(), DodajPitanjeAkt.this);
            }
        });
    }

    @Override
    public void pitanjeProvjeraZavrsena(boolean postoji) {
        if (postoji) {
            questionField.setBackgroundResource(R.color.colorError);
            showAlert("Uneseno pitanje već postoji!");
            addQuestionBtn.setText(R.string.spasi_pitanje);
            return;
        }
        questionField.setBackgroundResource(R.color.colorDefaultBackground);
        Intent replyIntent = new Intent();
        replyIntent.putExtra("pitanje", questionField.getText().toString());
        replyIntent.putExtra("odgovori", odgovori);
        replyIntent.putExtra("tacanOdgovor", tacanOdgovor);
        setResult(RESULT_OK, replyIntent);
        if (alert != null)
            alert.dismiss();
        finish();
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // nothing?
            }
        });
        alert = builder.create();
        alert.show();
    }
}
