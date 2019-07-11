package com.example.lucky_app.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lucky_app.R
import androidx.recyclerview.widget.RecyclerView
import com.example.lucky_app.Activity.Item_API
import com.example.lucky_app.Api.ConsumeAPI
import com.example.lucky_app.Api.Convert_Json_Java
import com.example.lucky_app.Api.User
import com.example.lucky_app.Product_New_Post.MyAdapter_list_grid_image
import com.example.lucky_app.utils.CommonFunction.getEncodedString
import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [FragmentB1]interface.
 */
class FragmentB1: Fragment() {

    var post_id = 0
    private var username: String? = null
    private var password: String? = null
    private var pk: Int? = null
    var encodeAuth=""
    var recyclerView: RecyclerView? = null

    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment, container, false)


//        val tvphone = ACTIVITY.intent.getStringExtra("Phone")
//        val phone = view.findViewById<TextView>(R.id.phone)
//        phone.text = tvphone
        recyclerView = view.findViewById(R.id.recycler_view)


        val preferences = activity!!.getSharedPreferences("Register", Context.MODE_PRIVATE)
        username=preferences.getString("name","")
        password=preferences.getString("pass","")
        encodeAuth="Basic "+ getEncodedString(username,password)
        if (preferences.contains("token")) {
            pk = preferences.getInt("Pk", 0)
        } else if (preferences.contains("id")) {
            pk = preferences.getInt("id", 0)
        }
        getMyLike()

        return view
    }

    private fun getMyLike() {
        val URL_ENDPOINT= ConsumeAPI.BASE_URL+"likebyuser/"
        val client= OkHttpClient()
        val request= Request.Builder()
                .url(URL_ENDPOINT)
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",encodeAuth)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                Log.w("failure Response", mMessage)
            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val mMessage = response.body()!!.string()

                try {
                    activity!!.runOnUiThread {
                        val itemApi = ArrayList<Item_API>()
                        val jsonObject = JSONObject(mMessage)
                        Log.d("Run  :"," la"+jsonObject)
                        val jsonArray = jsonObject.getJSONArray("results")
                        val jsonCount= jsonObject.getInt("count")
                        for (i in 0 until jsonArray.length()) {
                        val `object` = jsonArray.getJSONObject(i)
                        post_id = `object`.getInt("post")
//                        Log.d("Post id ",post_id.toString())

                            val url_user = "http://103.205.26.103:8000/allposts/"+post_id+"/"
                            Log.d("Post id ",url_user)
                            val client1 = OkHttpClient()
                            val request1 = Request.Builder()
                                    .url(url_user)
                                    .header("Accept","application/json")
                                    .header("Content-Type","application/json")
                                    .header("Authorization",encodeAuth)
                                    .build()
                            client1.newCall(request1).enqueue(object : Callback{
                                override fun onFailure(call: Call, e: IOException) {
                                    val mmessage = e.message.toString()
                                    Log.w("failure Response", mmessage)
                                }
                                @Throws(IOException::class)
                                override fun onResponse(call: Call, response: Response) {
                                    val mmessage = response.body()!!.string()
                                    try {
                                        activity!!.runOnUiThread{
                                            val jsonObject1 = JSONObject(mmessage)

                                            val title = jsonObject1.getString("title")
                                            val id = jsonObject1.getInt("id")
                                            val condition = jsonObject1.getString("condition")
                                            val cost = jsonObject1.getDouble("cost")
                                            val image = jsonObject1.getString("base64_front_image")
                                            val img_user = jsonObject1.getString("base64_right_image")
                                            val postType = jsonObject1.getString("post_type")

                                            itemApi.add(Item_API(id,img_user,image,title,cost,condition,postType))

                                            recyclerView!!.adapter = MyAdapter_list_grid_image(itemApi, "List")
                                            recyclerView!!.layoutManager = GridLayoutManager(context,1) as RecyclerView.LayoutManager?


                                        }

                                    } catch (e: JsonParseException) {
                                        e.printStackTrace() }
                                }
                            })
                        }
                    }
                } catch (e: JsonParseException) {
                    e.printStackTrace() }

            }
        })
    }
}