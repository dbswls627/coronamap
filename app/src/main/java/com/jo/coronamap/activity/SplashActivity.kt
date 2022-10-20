package com.jo.coronamap.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.jo.coronamap.R
import com.jo.coronamap.databinding.ActivitySplashBinding
import com.jo.coronamap.viewModel.MainViewModel
import com.jo.coronamap.viewModel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val intent = Intent(this,MainActivity::class.java)


        CoroutineScope(Dispatchers.Default).launch {
            viewModel.saveList()
            delay(2000)
            startActivity(intent)
        }
    }
}