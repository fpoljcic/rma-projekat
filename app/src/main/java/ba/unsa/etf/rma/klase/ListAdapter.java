package ba.unsa.etf.rma.klase;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class ListAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    private Class classType;

    public ListAdapter(Activity a, ArrayList d, Resources resLocal, Class classType) {
        activity = a;
        data = d;
        res = resLocal;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.classType = classType;
    }

    public Class getClassType() {
        return classType;
    }

    public void setClassType(Class classType) {
        this.classType = classType;
    }

    public int getCount() {
        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView name;
        public ImageView icon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.element_liste, null);
            holder = new ViewHolder();
            holder.name = vi.findViewById(R.id.name);
            holder.icon = vi.findViewById(R.id.icon);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.name.setText("No Data");
        } else {
            if (classType == Kviz.class) {
                Kviz object = (Kviz) data.get(position);
                if (object == null) {
                    holder.name.setText("Dodaj Kviz");
                    holder.icon.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/add", null, null));
                } else {
                    holder.name.setText(object.getNaziv());
                    holder.icon.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/" + object.getKategorija().getImage(), null, null));
                }
            } else if (classType == Pitanje.class) {
                Pitanje object = (Pitanje) data.get(position);
                if (object == null) {
                    holder.name.setText("Dodaj Pitanje");
                    holder.icon.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/add", null, null));
                } else {
                    holder.name.setText(object.getNaziv());
                    holder.icon.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/" + object.getImage(), null, null));
                }
            }
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }
}

