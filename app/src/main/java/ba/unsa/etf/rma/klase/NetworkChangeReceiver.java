package ba.unsa.etf.rma.klase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private KvizoviAkt kvizoviAkt;

    public NetworkChangeReceiver(KvizoviAkt kvizoviAkt) {
        this.kvizoviAkt = kvizoviAkt;
    }

    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) && kvizoviAkt != null)
            kvizoviAkt.notifyNetChange(!(status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED));
    }
}