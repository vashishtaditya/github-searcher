package brinda.aditya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_results.*

/*
* Created by Brinda Shah & Aditya Vashisht on DECEMBER 9, 2019
*/


class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)


        val data = intent.getSerializableExtra(getString(R.string.user_data_key)) as ArrayList<Users>

        supportActionBar?.title = "${data.size} Results"

        recyclerViewMain.layoutManager = LinearLayoutManager(this)

        recyclerViewMain.adapter = MainAdapter(data)
    }
}
