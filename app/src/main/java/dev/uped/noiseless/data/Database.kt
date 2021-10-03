package dev.uped.noiseless.data

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.uped.noiseless.Database

object DB {
    lateinit var driver: SqlDriver
    private val database: Database by lazy { Database(driver) }
    val measurementQueries
        get() = database.measurementQueries

    fun createDriver(context: Context) {
        driver = AndroidSqliteDriver(Database.Schema, context, "test.db")
    }
}