package com.jo.coronamap.repository
import com.jo.coronamap.CoronaAPI
import com.jo.coronamap.DataDao
import com.jo.coronamap.Util
import com.jo.coronamap.dataModel.Corona
import com.jo.coronamap.dataModel.DataList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(private val dataDao: DataDao, private val coronaAPI: CoronaAPI) {
    suspend fun getList(page: String): Flow<DataList> = flow {
        emit(coronaAPI!!.getList(page, "10", Util.API_KEY_SECRET))
    }

    suspend fun saveList(corona: Corona) {
        dataDao.insert(corona)
    }


    suspend fun getList() = dataDao.getList()


}