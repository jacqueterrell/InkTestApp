package com.example.inktestapp.userInterface

import android.app.Application
import android.util.Log
import com.example.inktestapp.certificate.MyCertificate
import com.example.inktestapp.di.Modules
import com.myscript.iink.Engine
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication: Application() {

    companion object {

        private var engine: Engine? = null

        fun getEngine(): Engine {
            if (engine == null) {
                engine = Engine.create(MyCertificate.getBytes()).apply {
                    configuration.let {

                    }
                }
            }
            return engine!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        try {
            startKoin {
                androidContext(applicationContext)
                modules(
                    listOf(Modules.viewModelModule, Modules.appModule)
                )
            }

        } catch (e: IllegalStateException) {
            // koin is already initialized
            Log.d("AppApplication", e.localizedMessage ?: "")
        }
    }
}