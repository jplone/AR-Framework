package edu.calstatela.jplone.ardemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by bill on 2/6/18.
 */

interface NetworkCallback {
    void onResult(String result);
}

interface TextInputDialogCallback {
    void onSubmit(String... params);
    void onCancel();
}

public class DisplayJPLDataActivity extends AppCompatActivity implements NetworkCallback, TextInputDialogCallback {
    private static final int READ_REQUEST_CODE = 42;
    private Context appContext;
    private TextView txtData;
    private String query_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        appContext = this;
        Button mUrlButton = (Button) findViewById(R.id.btn_url);
        Button mLoadButton = (Button) findViewById(R.id.btn_load);
        Button mLoginButton = (Button) findViewById(R.id.btn_login);
        txtData = (TextView) findViewById(R.id.txt_data);

        mUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performUrlSet();
            }
        });

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileLoad();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void displayData(String txt) {
        Log.d("JPLData", "Displaying: " + txt);
        txtData.setText(txt);
    }

    public void performUrlSet() {
        UrlDialog ud = new UrlDialog();
        ud.show(getFragmentManager(), "UrlDialogFragment");
    }

    public void performLogin() {
        LoginDialog ld = new LoginDialog();
        ld.show(getFragmentManager(), "LoginDialogFragment");
    }

    @Override
    public void onSubmit(String... params) {
        if(params[0].equals("login")) {
            authenticate(params[1], params[2]);
        }
        else if(params[0].equals("url")) {
            this.query_url = params[1];
        }
    }

    @Override
    public void onCancel() {

    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileLoad() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                Log.i("JPLDataActivity", "Uri: " + uri.toString());
                authenticate(uri);
            }
        }
    }

    private void authenticate(String username, String password) {
        // Tell the URLConnection to use a SocketFactory from our SSLContext
        NetworkTask at = new NetworkTask(this, null);
        at.execute(query_url, username, password);
    }

    private void authenticate(Uri uri) {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt

            InputStream caInput = appContext.getContentResolver().openInputStream(uri);
            //InputStream caInput = new BufferedInputStream(new FileInputStream("ca-cert.crt"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.d("JPLData", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs\
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry("ca", ca);

            // Create an SSLContext that uses our TrustManager
            //final SSLContext context = SSLContext.getInstance("TLS");
            final SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new AdditionalKeyStoresTrustManager(ks)}, new SecureRandom());

            final SSLSocketFactory socketFactory = new SSLSocketFactory() {
                @Override
                public String[] getDefaultCipherSuites() {
                    return new String[0];
                }

                @Override
                public String[] getSupportedCipherSuites() {
                    return new String[0];
                }

                @Override
                public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
                    SSLSocket ssl_socket = (SSLSocket) context.getSocketFactory().createSocket(socket, host, port, autoClose);
                    //String[] protocols = {"TLSv1"};//, "TLSv1"};//, "TLSv1.1", "TLSv1.2"};
                    //String[] ciphers = {"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA384"};
                    //ssl_socket.setEnabledProtocols(protocols);
                    //ssl_socket.setEnabledCipherSuites(ciphers);

                    /*
                    for(String protocol : ssl_socket.getEnabledCipherSuites()) {
                        Log.d("SSLFactory", protocol);
                    }*/

                    return ssl_socket;
                }

                @Override
                public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
                    return null;
                }

                @Override
                public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
                    return null;
                }

                @Override
                public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
                    return null;
                }

                @Override
                public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
                    return null;
                }
            };

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            NetworkTask at = new NetworkTask(this, socketFactory);
            at.execute(query_url);
        }
        catch (Exception e) {
            Log.d("JPLDataActivity", "exception occurred!");
            Log.d("JPLDataActivity", e.toString() + ": " + e.getMessage());
        }
    }

    public static class NetworkTask extends AsyncTask<String, Void, String> {
        private SSLSocketFactory socketFactory;
        private NetworkCallback callback;

        public NetworkTask(NetworkCallback callback, SSLSocketFactory socketFactory) {
            this.callback = callback;
            this.socketFactory = socketFactory;
        }

        @Override
        protected String doInBackground(String... params) {
            if(params.length == 0) // if there is no socket factory being passed in
                return null;
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection =
                        (HttpsURLConnection) url.openConnection();
                if(socketFactory != null)
                    urlConnection.setSSLSocketFactory(socketFactory);
                else {
                    String authString = params[1] + ":" + params[2];
                    byte[] authEncBytes = Base64.encode(authString.getBytes(), 0);
                    urlConnection.setRequestProperty("Authorization", "Basic " + new String(authEncBytes));
                }
                //urlConnection.setSSLSocketFactory(context.getSocketFactory());
                //InputStream in = urlConnection.getInputStream();
                urlConnection.connect();
                Log.d("Connection", urlConnection.getCipherSuite());

                for(Certificate c : urlConnection.getServerCertificates()) {
                    Log.d("Connection", c.toString());
                }

                String response = urlConnection.getResponseMessage();
                Log.d("Connection", response);

                if(response.equals("OK")) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String result = "";
                    String line;
                    int count = 0;
                    while((line = br.readLine()) != null && count < 100) {
                        result += line + "\n";
                        count++;
                    }
                    return result;
                }

                return null;
            }
            catch(Exception e) {
                Log.d("JPLDataAsyncTask", "Exception!");
                Log.d("JPLDataAsyncTask",  e.toString() + ": " + e.getMessage());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            callback.onResult(result);
        }
    }

    @Override
    public void onResult(String result) {
        displayData(result);
    }

    /**
     * Based on http://download.oracle.com/javase/1.5.0/docs/guide/security/jsse/JSSERefGuide.html#X509TrustManager
     */
    public static class AdditionalKeyStoresTrustManager implements X509TrustManager {

        protected ArrayList<X509TrustManager> x509TrustManagers = new ArrayList<X509TrustManager>();

        protected AdditionalKeyStoresTrustManager(KeyStore... additionalkeyStores) {
            final ArrayList<TrustManagerFactory> factories = new ArrayList<TrustManagerFactory>();

            try {
                // The default Trustmanager with default keystore
                final TrustManagerFactory original = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                original.init((KeyStore) null);
                factories.add(original);

                for( KeyStore keyStore : additionalkeyStores ) {
                    final TrustManagerFactory additionalCerts = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    additionalCerts.init(keyStore);
                    factories.add(additionalCerts);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            /*
             * Iterate over the returned trustmanagers, and hold on
             * to any that are X509TrustManagers
             */
            for (TrustManagerFactory tmf : factories)
                for( TrustManager tm : tmf.getTrustManagers() )
                    if (tm instanceof X509TrustManager)
                        x509TrustManagers.add( (X509TrustManager)tm );

            if( x509TrustManagers.size()==0 )
                throw new RuntimeException("Couldn't find any X509TrustManagers");
        }

        /*
         * Delegate to the default trust manager.
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            final X509TrustManager defaultX509TrustManager = x509TrustManagers.get(0);
            defaultX509TrustManager.checkClientTrusted(chain, authType);
        }

        /*
         * Loop over the trustmanagers until we find one that accepts our server
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            for( X509TrustManager tm : x509TrustManagers ) {
                try {
                    tm.checkServerTrusted(chain,authType);
                    return;
                } catch( CertificateException e ) {
                    // ignore
                }
            }
            throw new CertificateException();
        }

        public X509Certificate[] getAcceptedIssuers() {
            final ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
            for( X509TrustManager tm : x509TrustManagers )
                list.addAll(Arrays.asList(tm.getAcceptedIssuers()));
            return list.toArray(new X509Certificate[list.size()]);
        }
    }

    public static class LoginDialog extends DialogFragment {
        private TextInputDialogCallback callback;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_login, null);
            final TextView txtUser = (TextView) view.findViewById(R.id.txt_user);
            final TextView txtPass = (TextView) view.findViewById(R.id.txt_pass);

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(view)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String username = txtUser.getText().toString();
                            String password = txtPass.getText().toString();
                            callback.onSubmit(new String[]{"login", username, password});
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callback.onCancel();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            try {
                callback = (TextInputDialogCallback) activity;
            }
            catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement LoginDialogCallback");
            }
        }
    }

    public static class UrlDialog extends DialogFragment {
        private TextInputDialogCallback callback;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_url, null);
            final TextView txtUrl = (TextView) view.findViewById(R.id.txt_url);

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(view)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String url = txtUrl.getText().toString();
                            callback.onSubmit(new String[]{"url", url});
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callback.onCancel();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            try {
                callback = (TextInputDialogCallback) activity;
            }
            catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement LoginDialogCallback");
            }
        }
    }
}
