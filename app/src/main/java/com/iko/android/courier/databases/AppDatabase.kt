package com.iko.android.courier.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iko.android.courier.databases.dao.UserDao
import com.iko.android.courier.databases.entities.User

@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "user_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Assuming your User table is named "users"
                // Add your migration logic here to alter the existing table and add new columns
                db.execSQL("ALTER TABLE users ADD COLUMN accessToken TEXT")
                db.execSQL("ALTER TABLE users ADD COLUMN refreshToken TEXT")
            }
        }

        // Function to clear the database
        fun clearDatabase(context: Context) {
            INSTANCE?.close()
            context.deleteDatabase(DATABASE_NAME)
            INSTANCE = null
        }

    }
}
