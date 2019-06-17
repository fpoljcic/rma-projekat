package ba.unsa.etf.rma.klase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private NetworkInterface networkInterface;
    public static boolean INTERNET_ACCESS;

    public NetworkChangeReceiver(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            INTERNET_ACCESS = !(status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED);
            if (networkInterface != null)
                networkInterface.notifyNetChange(INTERNET_ACCESS);
        }
    }

    public interface NetworkInterface {
        void notifyNetChange(boolean internetAccess);
    }
}