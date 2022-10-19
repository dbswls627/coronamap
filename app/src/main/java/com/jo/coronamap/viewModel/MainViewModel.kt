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
    var isReady = false
    var b = true

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
}