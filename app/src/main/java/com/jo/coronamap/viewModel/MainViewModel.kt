package com.jo.coronamap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jo.coronamap.dataModel.Corona
import com.jo.coronamap.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    /*   fun getList() {
           viewModelScope.launch {
               Log.d("test", repository.getList("1").list.toString())
           }
       }*/
    val list = MutableLiveData<List<Corona>>()
    val centerName = MutableLiveData<String>()
    val fName = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val update = MutableLiveData<String>()

    fun saveList() {
        viewModelScope.launch {
            repository.saveList()
        }
    }

    fun getList() {
        viewModelScope.launch {
            list.value = repository.getList()
        }
    }

    fun setInfo(corona: Corona) {
        viewModelScope.launch {
            centerName.value = corona.centerName
            fName.value = corona.facilityName
            address.value = corona.address
            phoneNumber.value = corona.phoneNumber
            update.value = corona.updatedAt
        }
    }
}