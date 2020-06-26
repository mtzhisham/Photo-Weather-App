package dev.moataz.photoweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val weatherSharedViewModel: WeatherSharedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        weatherSharedViewModel.error.observe(this, Observer {
            it?.let {

                Toast.makeText(this, it,  Toast.LENGTH_SHORT).show()

                weatherSharedViewModel.error.value = null
            }


        })

        weatherSharedViewModel.getCurrentWeather(0.0f,0.0f).observe(this, Observer {

            it?.let {

                Log.d("weatherSharedViewModel", it.toString())

            }

        })

    }
}