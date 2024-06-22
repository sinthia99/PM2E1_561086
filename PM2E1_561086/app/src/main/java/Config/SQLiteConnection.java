package Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConnection extends SQLiteOpenHelper
{
    public SQLiteConnection(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Transacciones.CreateTableContactos);
        sqLiteDatabase.execSQL(Transacciones.CreateTablePais);
        sqLiteDatabase.execSQL(Transacciones.InsertPaises);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Transacciones.DropTableContactos);
        sqLiteDatabase.execSQL(Transacciones.DropTablePais);
        onCreate(sqLiteDatabase);
    }

    public boolean updateData(int id, String nombre, String pais, String codigo, String telefono, String nota) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Transacciones.nombres, nombre);
        contentValues.put(Transacciones.pais, pais);
        contentValues.put(Transacciones.codigo, codigo);
        contentValues.put(Transacciones.telefono, telefono);
        contentValues.put(Transacciones.nota, nota);
        int result = db.update(Transacciones.tablaContactos, contentValues, Transacciones.id + " = ?", new String[]{String.valueOf(id)});
        return result > 0; // Retorna true si se actualizó al menos una fila
    }
}
