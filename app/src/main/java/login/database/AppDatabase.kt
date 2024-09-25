package login.database

import androidx.room.Database
import androidx.room.RoomDatabase
import login.database.dao.UserDao
import login.database.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}