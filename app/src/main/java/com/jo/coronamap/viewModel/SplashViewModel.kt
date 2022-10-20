package com.jo.coronamap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jo.coronamap.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    fun saveList() {

        viewModelScope.launch {
            (1..10).forEach {
                repository.saveList(it.toString())
            }
        }
    }
}