package pl.kazoroo.tavernFarkle.di

import android.app.Application

class TavernFarkleApp: Application() {
    lateinit var dependencyContainer: DependencyContainer
        private set

    override fun onCreate() {
        super.onCreate()
        dependencyContainer = DependencyContainer(this@TavernFarkleApp)
    }
}