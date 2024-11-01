package com.example.mobileclub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MainDatabaseHelper (context: Context) :

SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION )
{
    data class Usuario(
        val nombre: String,
        val clave: String
    )

    companion object{
        private val DATABASE_NAME = "LOGIN.db"
        private val DATABASE_VERSION = 1
        private val TABLE_USUARIO = "Usuario"
        private val COLUMN_ID = "id"
        private val COLUMN_USER = "usuario"
        private val COLUMN_PASSWORD = "contraseña"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTabe = ("create table " + TABLE_USUARIO + "(" +  COLUMN_ID + " INTEGER primary key autoincrement, " +
                COLUMN_USER + " VARCHAR(100), " + COLUMN_PASSWORD + " TEXT)" )
        db.execSQL(createTabe)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVerdion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO)
        onCreate(db)
    }

    fun addUser(user: Usuario) : Long{
        val db = this.writableDatabase
        val value = ContentValues().apply {
            put(COLUMN_USER, user.nombre)
            put(COLUMN_PASSWORD, user.clave)
        }
        val success = db.insert(TABLE_USUARIO, null, value)
        return success
    }
}