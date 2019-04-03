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

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconHelper;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class ListAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;

    public ListAdapter(Activity a, ArrayList d, Resources resLocal) {
        activity = a;
        data = d;
        res = resLocal;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.element_liste, null);
            holder = new ViewHolder();
            holder.name = vi.findViewById(R.id.name);
            holder.icon = vi.findViewById(R.id.icon);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        final Kviz object = (Kviz) data.get(position);
        if (object == null) {
            holder.name.setText(R.string.dodaj_kviz);
            holder.icon.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/add", null, null));
        } else {
            holder.name.setText(object.getNaziv());
            final Context context = this.activity;
            final IconHelper iconHelper = IconHelper.getInstance(context);
            iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                @Override
                public void onDataLoaded() {
                    // This happens on UI thread, and is guaranteed to be called.
                    String id = object.getKategorija().getId();
                    Icon icon = iconHelper.getIcon(Integer.valueOf(id));
                    holder.icon.setImageDrawable(icon.getDrawable(context));
                }
            });

        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }
}

