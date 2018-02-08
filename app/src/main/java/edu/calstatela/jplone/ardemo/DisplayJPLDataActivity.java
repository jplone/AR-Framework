package edu.calstatela.jplone.ardemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by bill on 2/6/18.
 */

public class DisplayJPLDataActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = this;
        performFileSearch();
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("JPLDataActivity", "Uri: " + uri.toString());
                authenticate(uri);
            }
        }
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

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            //final SSLContext context = SSLContext.getInstance("TLS");
            final SSLContext context = SSLContext.getInstance("TLSv1.2");
            //context.init(null, tmf.getTrustManagers(), null);
            context.init(null, new TrustManager[]{new AdditionalKeyStoresTrustManager(keyStore)}, null);

            //final SSLSocketFactory ssf = new AdditionalKeyStoresSSLSocketFactory(keyStore);
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
                    String[] ciphers = {"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384"};
                    //ssl_socket.setEnabledProtocols(protocols);
                    ssl_socket.setEnabledCipherSuites(ciphers);

                    for(String protocol : ssl_socket.getEnabledCipherSuites()) {
                        Log.d("SSLFactory", protocol);
                    }

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
            AsyncTask<Void, Void, String> at = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... things) {
                    try {
                        URL url = new URL("https://h20hub.jpl.nasa.gov/hydrology/rest/well/master_site_id/");
                        HttpsURLConnection urlConnection =
                                (HttpsURLConnection) url.openConnection();
                        urlConnection.setSSLSocketFactory(socketFactory);
                        //urlConnection.setSSLSocketFactory(context.getSocketFactory());
                        //InputStream in = urlConnection.getInputStream();
                        urlConnection.connect();
                        Log.d("Connection", urlConnection.getCipherSuite());
                        Log.d("Connection", urlConnection.getServerCertificates().toString());
                        InputStream in = urlConnection.getInputStream();
                    }
                    catch(Exception e) {
                        Log.d("JPLDataAsyncTask", "Exception!");
                        Log.d("JPLDataAsyncTask",  e.toString() + ": " + e.getMessage());
                    }

                    return null;
                }
            };

            Void v = null;
            at.execute(v);

            //copyInputStreamToOutputStream(in, System.out);
        }
        catch (Exception e) {
            Log.d("JPLDataActivity", "exception occurred!");
            Log.d("JPLDataActivity", e.toString() + ": " + e.getMessage());
        }
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
}
