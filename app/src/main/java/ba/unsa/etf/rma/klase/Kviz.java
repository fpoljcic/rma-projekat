package ba.unsa.etf.rma.klase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Kviz {
    private String naziv;
    private ArrayList<Pitanje> pitanja;
    private Kategorija kategorija;

    public Kviz() {
        pitanja = new ArrayList<>();
    }

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void dodajPitanje(Pitanje pitanje) {
        pitanja.add(pitanje);
    }

    public String getImage() {
        return "standard";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kviz kviz = (Kviz) o;
        return Objects.equals(naziv, kviz.naziv) &&
                Objects.equals(kategorija, kviz.kategorija);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv, kategorija);
    }
}
