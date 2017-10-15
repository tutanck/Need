package com.aj.need.db;

import android.util.Log;

import com.aj.need.tools.regina.Regina;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URISyntaxException;

import io.socket.client.Socket;

/**
 * Created by joan on 18/09/2017.
 */

public class IO implements Regina.SocketClientEventDelegate {

    private IO() {
    }


    public static FirebaseAuth auth;
    public static FirebaseFirestore db;

    static {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

//// TODO: 16/10/2017 rem && regina too
    /* 
    public static Regina r;
    public static Socket socket;

    static {
        try {
            r = new Regina("http://3ab20db0.ngrok.io", new IO());
            socket = r.socket;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }*/


    public void handle(Regina.SocketClientEvent clientEvent) {
        Log.i("socketClientEvent", clientEvent.toString());
    }

    public void handle(Regina.ReginaEvent reginaEvent, String message) {
        Log.i("reginaEvent", reginaEvent.toString() + " : " + message);
    }

}
