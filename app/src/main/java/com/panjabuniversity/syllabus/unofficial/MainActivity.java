package com.panjabuniversity.syllabus.unofficial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends Activity {

    String url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isStoragePermissionGranted();
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        if (haveNetworkConnection()) {
            new Spinner2().execute();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No internet Connection");
            builder.setMessage("Please turn on internet connection to continue");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setCancelable(false);
            builder.setNegativeButton("close", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    // Logo AsyncTask
    private class Spinner1 extends AsyncTask<String, String, String> {
        String std;
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        android.widget.Spinner dynamicSpinner = findViewById(R.id.dynamic_spinner);
        ArrayList<String> al = new ArrayList<>();
        ArrayAdapter<String> adapter;
        ArrayList<String> al1 = new ArrayList<>();
        ArrayAdapter<String> adapter1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setTitle("Loading");
            progress.setMessage("Loading...Please wait");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Connect to the web site

                String url1 = params[0];
                Document document = Jsoup.connect(url1).get();
                // Using Elements to get the Meta data


                Elements ul = document
                        .select("div[class=innen termine] > ul > li > a[target=_blank]");

                std = ul.outerHtml();
                String href[] = std.split("\"");
                for (int k = 1; k <= href.length - 1; k += 4) {
                    String items = "http://puchd.ac.in/" + href[k];
                    al1.add(items);
                    adapter1 = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, al1);
                }

                std = std.replaceAll("<a.*\\b_blank\">", "");
                std = std.replaceAll("</a>", "\n");
                String all[] = std.split("\n");
                for (int i = 0; i < all.length; i += 2) {
                    String items = all[i];
                    al.add(items);
                    adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, al);
                    adapter.setDropDownViewResource(R.layout.myspinner);
                }
            } catch (Exception e) {
                MainActivity.this.runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Website Down");
                    builder.setMessage("Website is down,please try again later");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setCancelable(false);
                    builder.setNegativeButton("close", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();


            dynamicSpinner.setAdapter(adapter);
            ProgressBar progressBar = findViewById(R.id.progressBar);

            dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           final int position, long id) {
                    FrameLayout btn = findViewById(R.id.button);


                    btn.setOnClickListener(v -> {


                        MainActivity.this.runOnUiThread(() -> {
                            progressBar.setVisibility(View.VISIBLE);
                        });


                        if (isFileExists("syllabus.pdf")) {
                            File f = new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "syllabus.pdf");
                            f.delete();
                        }
                        url = al1.get(position);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading ...");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "syllabus.pdf");
                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                    });
                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(MainActivity.this, "Hello World", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private boolean isFileExists(String filename) {
        File folder1 = new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
        return folder1.exists();
    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.runOnUiThread(() -> {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);
            });
            File pdfFile = new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "syllabus.pdf");// -> filename = syllabus.pdf
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT < 24) {
                pdfIntent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            } else {
                pdfIntent.setDataAndType(Uri.parse(pdfFile.getPath()), "application/pdf");
            }

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            pdfIntent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            try {

                startActivity(pdfIntent);

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Intent i = new Intent(Intent.ACTION_VIEW);

                i.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=" + url), "text/html");

                startActivity(i);

            }
        }
    };


    @SuppressLint("StaticFieldLeak")
    private class Spinner2 extends AsyncTask<Void, Void, Void> {
        String url = "http://puchd.ac.in/syllabus.php?qstrfacid=10";
        String desc;
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        android.widget.Spinner dynamicSpinner = findViewById(R.id.static_spinner);
        ArrayList<String> al = new ArrayList<>();
        ArrayAdapter<String> adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setTitle("Loading");
            progress.setMessage("Loading...Please wait");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Using Elements to get the Meta data


                Elements ul = document
                        .select("div[class=innen termine] > ul > li > a[href] > b");

                desc = ul.outerHtml();

                desc = desc.replaceAll("<b>", "");
                desc = desc.replaceAll("</b>", "\n");
                String all[] = desc.split("\n");
                for (int i = 0; i < all.length; i += 2) {
                    String items = all[i];

                    al.add(items);
                    adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, al);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                }


            } catch (Exception e) {
                MainActivity.this.runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Website Down");
                    builder.setMessage("Website is down,please try again later");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setCancelable(false);
                    builder.setNegativeButton("close", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            dynamicSpinner.setAdapter(adapter);

            dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String sp1 = String.valueOf(dynamicSpinner.getSelectedItem());
                    if (!sp1.equalsIgnoreCase("Constituent College"))
                        Toast.makeText(MainActivity.this, sp1, Toast.LENGTH_SHORT).show();

                    if (sp1.equalsIgnoreCase("Arts")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=1");
                    } else if (sp1.equalsIgnoreCase("Business Management and Commerce")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=2");
                    } else if (sp1.equalsIgnoreCase("Constituent College")) {
                        Toast.makeText(MainActivity.this, "No courses available,please choose another stream", Toast.LENGTH_SHORT).show();
                    } else if (sp1.startsWith("Dairying")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=14");
                    } else if (sp1.startsWith("Design")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=3");
                    } else if (sp1.startsWith("Education")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=4");
                    } else if (sp1.startsWith("Engi")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=5");
                    } else if (sp1.startsWith("Languages")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=6");
                    } else if (sp1.startsWith("Law")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=7");
                    } else if (sp1.startsWith("Medical")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=8");
                    } else if (sp1.startsWith("Multi")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=11");
                    } else if (sp1.startsWith("Pharmaceutical Sciences")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=9");
                    } else if (sp1.startsWith("Regional")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=16");
                    } else if (sp1.startsWith("Science")) {
                        Spinner1 spd = new Spinner1();
                        spd.execute("http://puchd.ac.in/syllabus.php?qstrfacid=10");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });

        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void click(View v) {
        Intent intent = new Intent(MainActivity.this, Description.class);
        startActivity(intent);
    }
}
