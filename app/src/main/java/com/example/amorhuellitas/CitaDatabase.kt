package com.example.amorhuellitas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Cita(
    val id: Int = 0,
    val nombre: String = "",
    val telefono: String = "",
    val fecha: String = "",
    val hora: String = "",
    val raza: String = "",
    val tamano: String = ""
)

class CitaDatabase(context: Context) :
    SQLiteOpenHelper(context, "veterinaria.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE citas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                telefono TEXT NOT NULL,
                fecha TEXT NOT NULL,
                hora TEXT NOT NULL,
                raza TEXT NOT NULL,
                tamano TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun insertar(cita: Cita): Long {
        val valores = ContentValues().apply {
            put("nombre", cita.nombre)
            put("telefono", cita.telefono)
            put("fecha", cita.fecha)
            put("hora", cita.hora)
            put("raza", cita.raza)
            put("tamano", cita.tamano)
        }
        return writableDatabase.insert("citas", null, valores)
    }

    fun actualizar(cita: Cita): Int {
        val valores = ContentValues().apply {
            put("nombre", cita.nombre)
            put("telefono", cita.telefono)
            put("fecha", cita.fecha)
            put("hora", cita.hora)
            put("raza", cita.raza)
            put("tamano", cita.tamano)
        }
        return writableDatabase.update("citas", valores, "id = ?", arrayOf(cita.id.toString()))
    }

    fun eliminar(id: Int): Int {
        return writableDatabase.delete("citas", "id = ?", arrayOf(id.toString()))
    }

    fun consultarTodas(): List<Cita> {
        val lista = mutableListOf<Cita>()
        readableDatabase.rawQuery(
            "SELECT id, nombre, telefono, fecha, hora, raza, tamano FROM citas ORDER BY id DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Cita(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                        hora = cursor.getString(cursor.getColumnIndexOrThrow("hora")),
                        raza = cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                        tamano = cursor.getString(cursor.getColumnIndexOrThrow("tamano"))
                    )
                )
            }
        }
        return lista
    }
}