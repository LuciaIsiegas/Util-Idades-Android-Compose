package com.example.util_idades.shoppinglist.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.snapshots.SnapshotStateList

class DataManager(context: Context) : ComponentActivity() {
    private val db: SQLiteDatabase

    /*
    init es un bloque que se ejecuta inmediatamente tras inicializar un objeto de esta clase
    db.writableDatabase nos permite realizar CRUD sobre la BBDD
     */
    init {
        val helper = CustomSQLiteOpenHelper(context)
        helper.writableDatabase.also { db = it }
    }

    // crea tanto variables como funciones estáticas
    companion object {
        const val ID = "_id"
        const val NAME = "name"
        const val QUANTITY = "quantity"
        const val PRICE = "price"
        const val IMAGE_URL = "image_url"
        const val DB_NAME = "products"
        const val DB_VERSION = 2
        const val TABLE_PRODUCTS = "products"
    }

    // Funcion para insertar productos en la base de datos
    fun insert(name: String?, quantity: Int?, price: Double?, url: String?) {

        // Es una clase que permite escribir y borrar sobre ella
        // Es una clase mapeable (?)
        val values = ContentValues()

        values.put(NAME, name)
        values.put(QUANTITY, quantity)
        values.put(PRICE, price)
        values.put(IMAGE_URL, url)
        db.insert(TABLE_PRODUCTS, null, values)
    }

    fun delete(id: Int) {
        val query = "delete from " + TABLE_PRODUCTS + " where " + ID + " = " + id.toString() + ";"
        db.execSQL(query)
    }

    /*
    SnapshotStateList permite interactuar con cada elemento de manera
    individual sin alterar el resto de filas cosa que sí pasa
    con MutableStateListOf
     */
    fun getProducts(): SnapshotStateList<ProductUIModel> {

        // Devolveremos un SnapshotStateList --> products
        val products = SnapshotStateList<ProductUIModel>()
        // Columnas
        val cols = arrayOf<String>(ID, NAME, QUANTITY, PRICE, IMAGE_URL)
        // cursor (Cursor) nos permite recorrer las filas obtenidas del query
        val cursor = db.query(
            TABLE_PRODUCTS, cols, null, null, null, null, ID
        )

        var product: ProductUIModel
        while (cursor.moveToNext()) {
            product = ProductUIModel(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getFloat(3),
                cursor.getString(4)
            )
            products.add(product)
        }
        cursor.close()
        return products
    }

    /*
    inner class puede acceder a los recursos en la clase padre
     */
    private inner class CustomSQLiteOpenHelper(context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        /*
        Creará la base de datos
         */
        override fun onCreate(db: SQLiteDatabase) {
            val newTableQueryString =
                ("create table " + TABLE_PRODUCTS + "("
                        + ID + " integer primary key autoincrement,"
                        + NAME + " text,"
                        + QUANTITY + " integer,"
                        + PRICE + " float,"
                        + IMAGE_URL + " text);")
            db.execSQL(newTableQueryString)
        }

        /*
        Se ejecutará cada vez que se añade un nuevo producto
         */
        override fun onUpgrade(
            db: SQLiteDatabase, oldVersion: Int, newVersion: Int
        ) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS)
            onCreate(db)
        }

    }

}