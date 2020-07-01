package brinda.aditya

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
* Created by Brinda Shah & Aditya Vashisht on DECEMBER 9, 2019
*/


class MainActivity : AppCompatActivity() {

    //region properties

    val BASE_URL = "https://api.github.com/search/"
    var searchString = ""
    val minPage = 1
    val maxPage = 100
    val startPage = 30
    val maxRepos = 1000
    val maxFollowers = 10000

    val sharedPref = SharedPreference()

    //endregion

    //region onCreate methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        searchButton.setOnClickListener(){
            fetchData()
        }
        perPageNumberPicker.minValue = minPage
        perPageNumberPicker.maxValue = maxPage
        perPageNumberPicker.value = startPage

        minReposEditText.filters  = arrayOf<InputFilter>(InputFilterMinMax(0, maxRepos))
        minFollowersEditText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, maxFollowers))

        val internetConnected = InternetConnected(this)
        if (!internetConnected.checkNetworkConnectivity()){

            internetConnected.outputErrorMessage()
            return

        }

        searchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchButton.isEnabled = searchUser.text.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {
                searchButton.isEnabled = s.isNotEmpty()
                noResultsMessage.text = ""
            }
        })

        if(sharedPref.contains(getString(R.string.repos_key))) {
            minReposEditText.setText(sharedPref.getValueInt( getString(R.string.repos_key)).toString())
        } else {
            minReposEditText.setText("0")
        }

        if(sharedPref.contains(getString(R.string.followers_key))) {
            minFollowersEditText.setText(sharedPref.getValueInt( getString(R.string.followers_key)).toString())
        } else {
            minFollowersEditText.setText("0")
        }

        if(sharedPref.contains(getString(R.string.page_size_key))) {
            perPageNumberPicker.value = sharedPref.getValueInt( getString(R.string.page_size_key))
        } else {
            perPageNumberPicker.value = 0
        }

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_about -> {
                val intent = Intent(TheApp.context, AboutActivity::class.java) // add intent
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //endregion

    //region fetch method

    private fun fetchData() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val restApi = retrofit.create(RestApi::class.java)


        if(TextUtils.isEmpty(minFollowersEditText.text)){
            minFollowersEditText.setText("0")
        }

        if(TextUtils.isEmpty(minReposEditText.text)){
            minReposEditText.setText("0")
        }


        var minNumberOfFollowers = minFollowersEditText.text.toString().toInt()
        if(minNumberOfFollowers > 0) minNumberOfFollowers -- // the api uses more than so subtract 1 if not zero

        var minNumberOfRepos = minReposEditText.text.toString().toInt()
        if(minNumberOfRepos > 0) minNumberOfRepos -- // the api uses more than so subtract 1 if not zero



        searchString = searchUser.text.toString()

        val request = "${searchString} repos:>$minNumberOfRepos followers:>$minNumberOfFollowers"

        val call = restApi.getUserData(request , perPageNumberPicker.value)

        call.enqueue(object: Callback<ResponseDataClass> {
            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {

                toast(t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {

                val responseBody = response.body()


                val users = responseBody?.items
                val length = users?.size ?: 0

                if (length > 0) { // no point displaying data if we have no results!
                    // An intent is a messaging object that can use to request an action
                    val intent = Intent(TheApp.context, ResultsActivity::class.java) // add intent
                    intent.putExtra(getString(R.string.user_data_key), users) // add data to Bundle
                    startActivity(intent) // start new activity & send the Bundle with the intent
                }
                else {
                    noResultsMessage.text = getString(R.string.no_results, searchString)
                    searchButton.isEnabled = false
                }


            }


        })


    }

    //endregion

    //region onPause
    override  fun onPause() {
        super.onPause()


        val repoString: String = getString(R.string.repos_key)
        val followewrsString: String = getString(R.string.followers_key)
        val pageSizeString: String = getString(R.string.page_size_key)

        sharedPref.save(repoString, minReposEditText.text.toString().toInt())
        sharedPref.save(followewrsString, minFollowersEditText.text.toString().toInt())
        sharedPref.save(pageSizeString, perPageNumberPicker.value.toString().toInt())


    }
    //endregion




    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
