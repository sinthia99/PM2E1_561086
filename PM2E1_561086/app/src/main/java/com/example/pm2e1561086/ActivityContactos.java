package com.example.pm2e1561086;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import Config.SQLiteConnection;
import Config.Transacciones;
import Models.Contactos;

public class ActivityContactos extends AppCompatActivity {

    SQLiteConnection conexion;
    private GestureDetector gestureDetector;
    Button btn_compartir, btn_eliminar, btn_actualizar, btn_verImagen,btncall;
    ListView list_contactos;
    ArrayList<Contactos> listContacto;
    ArrayList<String> arregloContactos;
    private static final int REQUEST_CALL_PERMISSION = 1;
    String telefono;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        // Initialize the GestureDetector
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //handleDoubleTap(e);
                return true;
            }
        });


        try {
            //Establecer coneccion a base de datos
            conexion = new SQLiteConnection(this, Transacciones.namedb, null, 1);
            btn_compartir = (Button) findViewById(R.id.btn_compartir);
            btn_eliminar = (Button) findViewById(R.id.btn_eliminar);
            btn_actualizar = (Button) findViewById(R.id.btn_actualizar);
            btn_verImagen = (Button) findViewById(R.id.btn_verImagen);
            btncall = (Button) findViewById(R.id.btncall);
            list_contactos = (ListView) findViewById(R.id.list_contactos);

            GetContacts();
            ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arregloContactos);
            list_contactos.setAdapter(adp);

            list_contactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    btn_verImagen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showContactImagePopup(listContacto.get(i).getImagen());
                        }
                    });
                    btn_compartir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String itemPerson = listContacto.get(i).getNombre()+" | "+listContacto.get(i).getCodigo()+" "+listContacto.get(i).getTelefono();
                            shareContact(itemPerson);
                        }
                    });
                    btn_eliminar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteContact(listContacto.get(i).getId());
                        }
                    });
                    btn_actualizar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ActivityContactos.this, ActivityEditar.class);
                            Integer id = listContacto.get(i).getId();
                            String nombre = listContacto.get(i).getNombre();
                            String telefono = listContacto.get(i).getTelefono();
                            String nota = listContacto.get(i).getNota();
                            intent.putExtra("id",id);
                            intent.putExtra("nombres", nombre);
                            intent.putExtra("pais", listContacto.get(i).getPais());
                            intent.putExtra("telefono", listContacto.get(i).getTelefono());
                            intent.putExtra("nota", nota);
                            intent.putExtra("imagen", listContacto.get(i).getImagen());
                            startActivity(intent);
                        }
                    });
                    btncall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            telefono = listContacto.get(i).getTelefono();
                            llamar();
                        }
                    });


                }
            });
            list_contactos.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
            list_contactos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showCallConfirmationDialog(listContacto.get(i).getTelefono(), listContacto.get(i).getNombre());
                    return true;
                }
            });
        }catch (Exception ex){
            ex.toString();
        }

        imagen = (ImageView) findViewById(R.id.imagevolver);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(ActivityContactos.this,ActivityDash.class);
                startActivity(inten);
            }
        });

    }

    private void GetContacts() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos contacto = null;
        listContacto = new ArrayList<Contactos>();

        Cursor cursor = db.rawQuery(Transacciones.SelectTablePersonas, null);
        while(cursor.moveToNext()){
            contacto = new Contactos();
            contacto.setId(cursor.getInt(0));
            contacto.setNombre(cursor.getString(1));
            contacto.setPais(cursor.getString(2));
            contacto.setCodigo(cursor.getString(3));
            contacto.setTelefono(cursor.getString(4));
            contacto.setNota(cursor.getString(5));
            contacto.setImagen(cursor.getBlob(6));


            listContacto.add(contacto);
        }
        cursor.close();
        fillList();
    }

    private void fillList() {
        arregloContactos = new ArrayList<String>();
        for(int i = 0; i < listContacto.size(); i++){
            arregloContactos.add(listContacto.get(i).getNombre()+" | "+listContacto.get(i).getCodigo()+" "+listContacto.get(i).getTelefono());

        }
    }

    public void showContactImagePopup(byte[] imageBlob) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.contact_image_popup);

        ImageView imageView = dialog.findViewById(R.id.contactImageView);

        Bitmap contactImage = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
        imageView.setImageBitmap(contactImage);

        dialog.show();
    }

    public void closePopup(View view) {
        recreate();
    }

    private void shareContact(String contactInfo) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, contactInfo);
        shareIntent.setType("text/plain");

        Intent chooser = Intent.createChooser(shareIntent, "Compartir Contacto via");

        startActivity(chooser);
    }

    private void deleteContact(int contactId) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Borrar Contacto");
        builder.setMessage("Esta Seguro que desea eliminar el contacto?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = conexion.getWritableDatabase();
                String whereClause = "id = ?";
                String[] whereArgs = {String.valueOf(contactId)};
                int deletedRows = db.delete(Transacciones.tablaContactos, whereClause, whereArgs);

                if (deletedRows > 0) {
                    recreate();
                } else {
                    Toast.makeText(getApplicationContext(), "Error! no se pudo eliminar el contacto!", Toast.LENGTH_LONG).show();
                }

                db.close();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recreate();
            }
        });

        builder.create().show();
    }

    private void handleDoubleTap(MotionEvent e) {
        // You can get the position of the tapped item from the MotionEvent
        int position = list_contactos.pointToPosition((int) e.getX(), (int) e.getY());

        // Check if a valid item was tapped
        if (position != AdapterView.INVALID_POSITION) {
            showCallConfirmationDialog(listContacto.get(position).getTelefono(), listContacto.get(position).getNombre());
        }
    }

    private void showCallConfirmationDialog(final String phoneNumber, final String nombre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Llamar a Contacto: "+nombre);
        builder.setMessage("Quiere realizar una llamada al contacto?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, initiate the phone call
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        builder.create().show();
    }

    private void llamar(){
        Toast.makeText(getApplicationContext(),"tel: "+telefono,Toast.LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+telefono));
            startActivity(callIntent);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamar();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    }