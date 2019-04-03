package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                if (categoryNameField.getText().toString().isEmpty() || kategorije.contains(new Kategorija(categoryNameField.getText().toString(), "0"))) {
                    errorPresent = true;
                    categoryNameField.setBackgroundColor(Color.parseColor("#FFCCCC"));
                } else
                    categoryNameField.setBackgroundColor(Color.parseColor("#FAFAFA"));
                if (categoryIconField.getText().toString().isEmpty()) {
                    errorPresent = true;
                    categoryIconField.setBackgroundColor(Color.parseColor("#FFCCCC"));
                } else
                    categoryIconField.setBackgroundColor(Color.parseColor("#FAFAFA"));
                if (!errorPresent) {
                    Intent replyIntent = new Intent();
                    replyIntent.putExtra("novaKategorija", new Kategorija(categoryNameField.getText().toString(), categoryIconField.getText().toString()));
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        categoryIconField.setText(String.valueOf(selectedIcons[0].getId()));
    }
}
