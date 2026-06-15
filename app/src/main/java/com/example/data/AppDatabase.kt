package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = 1,
    val jsonString: String
)

@Dao
interface GameStateDao {
    @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
    fun getGameState(): Flow<GameStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameState(state: GameStateEntity)
}

@Database(entities = [GameStateEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameStateDao(): GameStateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "muslim_leveling_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class GameRepository(private val gameStateDao: GameStateDao) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(MuslimLevelingData::class.java)

    val gameStateFlow: Flow<MuslimLevelingData?> = gameStateDao.getGameState().map { entity ->
        entity?.let {
            try {
                adapter.fromJson(it.jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun saveGameState(data: MuslimLevelingData) = withContext(Dispatchers.IO) {
        val jsonString = adapter.toJson(data)
        gameStateDao.saveGameState(GameStateEntity(jsonString = jsonString))
    }
}
