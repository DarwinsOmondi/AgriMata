package com.example.agrimata.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartProduct::class], version = 1, exportSchema = false)
abstract class CartProductDatabase : RoomDatabase() {
    abstract val cartProductDao: CartProductDao

    companion object {
        @Volatile
        private var INSTANCE: CartProductDatabase? = null

        fun getDatabase(context: Context): CartProductDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartProductDatabase::class.java,
                    "cart_product_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}