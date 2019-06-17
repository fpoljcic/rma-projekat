package ba.unsa.etf.rma.klase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private NetworkInterface networkInterface;

    public NetworkChangeReceiver(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) && networkInterface != null)
            networkInterface.notifyNetChange(!(status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED));
    }

    public interface NetworkInterface {
        void notifyNetChange(boolean internetAccess);
    }
}