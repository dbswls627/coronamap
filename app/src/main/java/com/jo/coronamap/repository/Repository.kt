package com.jo.coronamap.repository
import com.jo.coronamap.CoronaAPI
import com.jo.coronamap.DataDao
import com.jo.coronamap.Util
import com.jo.coronamap.dataModel.Corona
import javax.inject.Inject

class Repository @Inject constructor(private val dataDao: DataDao, private val coronaAPI: CoronaAPI) {
    suspend fun getList(page: String) = coronaAPI!!.getList(
        page,
        "10",
        Util.API_KEY_SECRET
    )

    suspend fun saveList(page: String){
            getList(page).list.forEach {
                dataDao.insert(it)
            }
    }

    suspend fun getList() = dataDao.getList()


}