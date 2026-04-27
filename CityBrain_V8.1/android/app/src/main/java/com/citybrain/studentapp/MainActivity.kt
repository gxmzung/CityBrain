package com.citybrain.studentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.citybrain.studentapp.data.repository.CityBrainRepository
import com.citybrain.studentapp.ui.navigation.CityBrainApp
import com.citybrain.studentapp.ui.theme.CityBrainTheme

class MainActivity : ComponentActivity() {
    private val repository by lazy { CityBrainRepository.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CityBrainTheme {
                CityBrainApp(repository = repository)
            }
        }
    }
}
