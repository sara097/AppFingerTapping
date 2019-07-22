package com.example.fingertapping;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FileSave {
    private Context context;
    private String name;
    private String text;

    public FileSave(Context con, String name, String text) {
        this.context=con;
        this.text=text;
        this.name=name+".txt";
        saveData();
    }

    private void saveData() {

        try {
            //utworzenie pliku do zapisu
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
            File myFile = new File(path, name);
            FileOutputStream fOut = new FileOutputStream(myFile,true);
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
}
