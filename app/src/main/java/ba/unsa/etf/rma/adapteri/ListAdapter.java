package ba.unsa.etf.rma.adapteri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import ba.unsa.etf.rma.klase.Kviz;

public class ListAdapter extends BaseAdapter {
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

    @Override
    public int getCount() {
        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        TextView name;
        ImageView icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            view = inflater.inflate(R.layout.element_liste, null);
            holder = new ViewHolder();
            holder.name = view.findViewById(R.id.name);
            holder.icon = view.findViewById(R.id.icon);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        final Kviz object = (Kviz) data.get(position);
        if (object == null) {
            holder.name.setText(R.string.dodaj_kviz);
            holder.icon.setImageResource(R.drawable.add);
        } else {
            holder.name.setText(object.getNaziv());
            if (object.getKategorija() == null) {
                holder.icon.setImageResource(R.drawable.generic);
                return view;
            }
            final Context context = this.activity;
            final IconHelper iconHelper = IconHelper.getInstance(context);
            iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                @Override
                public void onDataLoaded() {
                    // This happens on UI thread, and is guaranteed to be called.
                    /*
                        String id = object.getKategorija().getId();
                        Icon icon = null;
                        if (id != null)
                            icon = iconHelper.getIcon(Integer.valueOf(id));
                    */
                    String id = object.getKategorija().getId();
                    Icon icon = iconHelper.getIcon(Integer.valueOf(id));
                    if (holder.name.getText().toString().equals(activity.getString(R.string.dodaj_kviz)))
                        holder.icon.setImageResource(R.drawable.add);
                    else if (icon != null)
                        holder.icon.setImageDrawable(icon.getDrawable(context));
                }
            });

        }
        return view;
    }

}

