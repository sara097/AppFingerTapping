package com.example.fingertapping;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

class FileOperations {
    private Context context;
    private String name;
    private String text;

    FileOperations(Context con, String name, String text) {
        this.context=con;
        this.text=text;
        this.name=name+".txt";
    }

    FileOperations(Context context) {
        this.context = context;
    }

    void saveData(boolean append) {

        try {
            //utworzenie pliku do zapisu
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
            File myFile = new File(path, name);
            FileOutputStream fOut = new FileOutputStream(myFile,append);
            OutputStreamWriter out = new OutputStreamWriter(fOut);
            //zapisanie do pliku
            out.write(text);
            out.flush();
            out.close();

            //wyswietlenie komunikatu, że zapisano dane
            Toast.makeText(context, "Data Saved", Toast.LENGTH_LONG).show();

        } catch (java.io.IOException e) {
            //obsluga wyjatku
            //w razie niepowodzenia zapisu do pliku zostaje wyswietlony komunikat a w konsoli zrzut stosu
            Toast.makeText(context, "Data Could not be added", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }

    }

    ArrayList<Integer> readSettings() {

        ArrayList<Integer> output= null;
        try {
            output=new ArrayList<>();
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
            File myFile = new File(path, "settings.txt");
            InputStream inputStream = new FileInputStream(myFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                for (String s : line.split(";")) {
                    output.add(Integer.valueOf(s));
                }
            }
            reader.close();
            inputStream.close();

        } catch (java.io.IOException e) {
            //obsługa wyjątku wraz z wyswietleniem uzytkownikowi komunikatu
            Toast.makeText(context, "Cannot read data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return output;
    }
}
