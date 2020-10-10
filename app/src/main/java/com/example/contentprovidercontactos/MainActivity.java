package com.example.contentprovidercontactos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_READ_CONTACTS = 79;
    ListView list;
    //creamos un array para los contactos y otro para los números
    ArrayList mobileArray;
    ArrayList numberArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

numberArray = new ArrayList();

if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
 mobileArray = getAllContacts();
 } else {
requestPermission();
}


        list = findViewById(R.id.list);

ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, mobileArray);
        list.setAdapter(adapter);

        // asignamos un escuchador a la lista para que al dar tap en un elemento de la lista,
        // nos muestre el número

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
  //la lista esta referenciada a los contactos, pero hemos creado otro array igual al primero pero
  // solo con los números de teléfono
  // entonces imagina 2 casilleros verticales de digamos 3 casillas

//       ARRAY mobileArray                            ARRAY numberArray
//      nº   nombre        tel        ||ØØ                tel
//      1    luis        465655                            465655
//      2    Ana         847473                            847473
//      3    Carolina    7373737                           7373737
// puedes ver que en mobileArray esta todo lo de contactos mientras que en numberArray
// contiene solo los números de cada contacto   entonces si en el array   mobileArray yo recupero
// el contacto con id= 2 obtendré los datos de Ana podemos inferir que si le solicito al
// array  numberArray el contenido de su índice con valor de 2 devolverá el teléfono de Ana!!!!!


                              String number = (String) numberArray.get(position);

   // al dar tap en un toast mostramos el número de teléfono!
                Toast.makeText(MainActivity.this,number,Toast.LENGTH_LONG).show();
            }
        });

    }




    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }


//solicitamos permisos para usar los contactos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }




    private ArrayList getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur!= null ? cur.getCount() : 0) > 0) {
            while (cur!= null && cur.moveToNext()) {

 String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
 //llenamos la lista con los nombres de los contactos
  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

  nameList.add(name);

 if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
  Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
        //llenamos el array numberarray con el campo Phone.NUMBER de los contactos
                        numberArray.add(phoneNo);

                    }
                    pCur.close();
                }
            }
        }
        if (cur!= null) {
            cur.close();
        }
        return nameList;
    }



}