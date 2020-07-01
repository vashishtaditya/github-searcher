package brinda.aditya

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import okhttp3.*
import java.io.IOException

/*
* Created by Brinda Shah & Aditya Vashisht on DECEMBER 9, 2019
*/


class DetailsActivity : AppCompatActivity() {

    // region methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        supportActionBar?.title = intent.getStringExtra(CustomViewHolderClass.TITLE_KEY)

        val data = intent.getSerializableExtra(CustomViewHolderClass.OBJECT_KEY) as Users

        // set underline text on TextView control
        val text = data.html_url //"Underlined Text"
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, text.length, 0)
        htmlURLTextView.text = content

        htmlURLTextView.setOnClickListener{
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(getString(R.string.url_key), data.html_url)
            this.startActivity(intent)
        }

        fetchJson(data.url)
    }

    private fun fetchJson(url: String){

        // Using okhttp client here
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback { // can't execute from main thread!
            override fun onFailure(call: Call, e: IOException) {
                toast("Request Failed!")
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                val gson = GsonBuilder().create()
                val result = gson.fromJson(body, UserDetails::class.java)




                runOnUiThread {
                    Picasso.get().load(result.avatar_url).into(avatarImageView)

                    nameTextView.text =  getString(R.string.user_name, result?.name ?: "unknown")
                    locationTextView.text =  getString(R.string.location, result?.location ?: "")
                    companyTextView.text =  getString(R.string.company, result?.company ?: "")
                    followersTextView.text =  getString(R.string.followers, result?.followers ?: "")
                    publicGistTextView.text =  getString(R.string.public_gists, result?.public_gists ?: "")
                    publicReposTextView.text =  getString(R.string.public_repos, result?.public_repos ?: "")
                    updatedTextView.text =  getString(R.string.updated, result?.updated_at ?: "").substring(0,19)
                    createdTextView.text =  getString(R.string.created, result?.created_at ?: "").substring(0,19)

                }
            }
        })
    }
//endregion

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
