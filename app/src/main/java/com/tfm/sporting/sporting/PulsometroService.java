package com.tfm.sporting.sporting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PulsometroService extends Service {
    public PulsometroService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
