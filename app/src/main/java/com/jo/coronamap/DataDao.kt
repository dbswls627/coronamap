package com.jo.coronamap

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.jo.coronamap.dataModel.Corona


@Dao
interface DataDao {
    @Query("select * from Corona")
    suspend fun getList(): List<Corona>

    @Insert(onConflict = REPLACE)
    suspend fun insert(corona: Corona)

    @Delete
    suspend fun delete(corona: Corona)

    @Update
    suspend fun update(corona: Corona)


}
