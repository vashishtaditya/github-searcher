package brinda.aditya

import android.app.Application
import android.content.Context

/*
* Created by Brinda Shah & Aditya Vashisht on DECEMBER 9, 2019
*/

class TheApp : Application() {

    //region OnCreate Method
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    //endregion

    companion object{

        lateinit var context: Context
           private set
    }
}