package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {
    private EditText categoryNameField, categoryIconField;
    private Button addIconBtn, addCategoryBtn;
    private ArrayList<Kategorija> kategorije;
    private Icon[] selectedIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju);
        linkControls();
        setListeners();
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        kategorije = (ArrayList<Kategorija>) intent.getSerializableExtra("kategorije");
    }

    private void linkControls() {
        categoryNameField = findViewById(R.id.etNaziv);
        categoryIconField = findViewById(R.id.etIkona);
        addIconBtn = findViewById(R.id.btnDodajIkonu);
        addCategoryBtn = findViewById(R.id.btnDodajKategoriju);
    }

    private void setListeners() {
        addIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IconDialog iconDialog = new IconDialog();
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.setMaxSelection(1, false);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");
            }
        });
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean errorPresent = false;
                boolean vecPostoji = kategorije.contains(new Kategorija(categoryNameField.getText().toString(), "0"));
                if (categoryNameField.getText().toString().isEmpty() || vecPostoji) {
                    errorPresent = true;
                    categoryNameField.setBackgroundResource(R.color.colorError);
                } else
                    categoryNameField.setBackgroundResource(R.color.colorDefaultBackground);
                if (categoryIconField.getText().toString().isEmpty()) {
                    errorPresent = true;
                    categoryIconField.setBackgroundResource(R.color.colorError);
                } else
                    categoryIconField.setBackgroundResource(R.color.colorDefaultBackground);
                if (!errorPresent) {
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra("novaKategorija", new Kategorija(categoryNameField.getText().toString(), categoryIconField.getText().toString()));
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
                if (vecPostoji)
                    showAlert("Unesena kategorija već postoji!");
            }
        });
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        categoryIconField.setText(String.valueOf(selectedIcons[0].getId()));
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
        AlertDialog alert = builder.create();
        alert.show();
    }
}
