package com.jo.coronamap.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jo.coronamap.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val progress = MutableLiveData(0)
    private lateinit var b: Job

    fun saveList() {
        viewModelScope.launch {
            (1..10).forEach { page ->
                repository.getList(page.toString())
                    .onCompletion {
                        if (page == 10) {
                            b.join()
                            setProgress20()
                        }
                    }
                    .collect {
                        it.list.forEach { corona ->
                            repository.saveList(corona)
                        }
                    }
            }
        }
    }

    fun setProgress80() {
        b = viewModelScope.launch {
            (1..8).forEach {
                delay(200)
                progress.value = progress.value!! + 10
            }
        }
    }

    private fun setProgress20() {
        viewModelScope.launch {
            (1..2).forEach {
                delay(200)
                progress.value = progress.value!! + 10
            }
        }
    }
}
