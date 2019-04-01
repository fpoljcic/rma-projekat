package ba.unsa.etf.rma.aktivnosti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import ba.unsa.etf.rma.R;

public class DodajKategorijuAkt extends AppCompatActivity {
    private EditText categoryNameField, categoryIconField;
    private Button addIconBtn, addCategoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju);
        linkControls();
    }

    private void linkControls() {
        categoryNameField = findViewById(R.id.etNaziv);
        categoryIconField = findViewById(R.id.etIkona);
        addIconBtn = findViewById(R.id.btnDodajIkonu);
        addCategoryBtn = findViewById(R.id.btnDodajKategoriju);
    }
}
