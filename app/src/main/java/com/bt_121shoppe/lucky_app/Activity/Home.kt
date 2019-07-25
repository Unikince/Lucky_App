package com.bt_121shoppe.lucky_app.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Base64
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.custom.sliderimage.logic.SliderImage
import com.bt_121shoppe.lucky_app.Api.ConsumeAPI
import com.bt_121shoppe.lucky_app.Api.User
import com.bt_121shoppe.lucky_app.Buy_Sell_Rent.Buy.Buy
import com.bt_121shoppe.lucky_app.Buy_Sell_Rent.Rent.Rent
import com.bt_121shoppe.lucky_app.Buy_Sell_Rent.Sell.Sell
import com.bt_121shoppe.lucky_app.useraccount.Edit_account
import com.bt_121shoppe.lucky_app.Login_Register.UserAccount
import com.bt_121shoppe.lucky_app.Product_New_Post.MyAdapter_list_grid_image
import com.bt_121shoppe.lucky_app.Product_dicount.MyAdapter
import com.bt_121shoppe.lucky_app.R
import com.bt_121shoppe.lucky_app.Setting.AboutUsActivity
import com.bt_121shoppe.lucky_app.Setting.Setting
import com.bt_121shoppe.lucky_app.Setting.TermPrivacyActivity
import com.bt_121shoppe.lucky_app.Startup.Item
import com.bt_121shoppe.lucky_app.Startup.Search1
import com.bt_121shoppe.lucky_app.Startup.Your_Post
import com.bt_121shoppe.lucky_app.chats.ChatMainActivity
import com.bt_121shoppe.lucky_app.utils.CheckNetwork
import com.bt_121shoppe.lucky_app.utils.CommonFunction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.nav_header_home.*
import net.hockeyapp.android.CrashManager

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    var recyclerView: RecyclerView? = null
    var list_item: ArrayList<Item_API>? = null
    var best_list: RecyclerView? = null
    var grid: ImageView? = null
    var list: ImageView? = null
    var image_list: ImageView? = null
//    var click: String = "Khmer"
    lateinit var sharedPreferences: SharedPreferences
    val myPreferences = "mypref"
    val namekey = "Khmer"
    var english: ImageView? = null
    var khmer: ImageView? = null
    private var pk=0
    private var name=""
    private var pass=""
    private var Encode=""
    private var categoryId:String=""
    private var brandId:String=""
    private var yearId:String=""

    var category: Spinner? = null
    var drawerLayout: DrawerLayout? = null
    private var listItems: ArrayList<String>?=null

    internal lateinit var ddBrand:Button
    internal lateinit var listItems1: Array<String?>
    internal lateinit var categoryIdItems: Array<Int?>
    internal lateinit var brandListItems: Array<String?>
    internal lateinit var brandIdListItems: Array<Int?>
    internal lateinit var yearListItems: Array<String?>
    internal lateinit var yearIdListItems: Array<Int?>

    fun language(lang: String) {
         val locale = Locale(lang)
         Locale.setDefault(locale)
         val confi = Configuration()
         confi.locale = locale
         baseContext.resources.updateConfiguration(confi, baseContext.resources.displayMetrics)
         val editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
         editor.putString("My_Lang", lang)
         editor.apply()
     }
    fun locale() {
        val prefer = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = prefer.getString("My_Lang", "")
        Log.d("language",language)
        language(language)

    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locale()
        sharedPreferences = getSharedPreferences(myPreferences,Context.MODE_PRIVATE)
        setContentView(R.layout.activity_home)

        if(CheckNetwork.isIntenetAvailable(this@Home)){

        }else{
            Toast.makeText(this@Home,"No Internet connection",Toast.LENGTH_LONG).show();
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = " "
        setSupportActionBar(toolbar)

        khmer = findViewById(R.id.khmer)
        english = findViewById(R.id.english)
        val prefer = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = prefer.getString("My_Lang", "")

        val sharedPref: SharedPreferences = getSharedPreferences("Register", Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()

        if (sharedPref.contains("token") || sharedPref.contains("id")){
            navView.setVisibility(View.VISIBLE)
            drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            name = sharedPref.getString("name", "")
            pass = sharedPref.getString("pass", "")
            Encode = CommonFunction.getEncodedString(name,pass)
            if (sharedPref.contains("token")) {
                pk = sharedPref.getInt("Pk",0)
            } else if (sharedPref.contains("id")) {
                pk = sharedPref.getInt("id", 0)
            }
            english!!.setOnClickListener { language("en")
                recreate()
            }
            khmer!!.setOnClickListener { language("km")
                recreate()
            }
            if(language.equals("km")) {
                english!!.visibility = View.VISIBLE
                khmer!!.visibility = View.GONE
            }else{
                english!!.visibility = View.GONE
                khmer!!.visibility = View.VISIBLE
            }
            getUserProfile()
        }else{
//            navView.setVisibility(View.GONE)
            drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            if(language.equals("km")) {
                english!!.visibility = View.VISIBLE
                khmer!!.visibility = View.GONE
            }else{
                english!!.visibility = View.GONE
                khmer!!.visibility = View.VISIBLE
            }
            english!!.setOnClickListener { language("en")
                recreate()
            }
            khmer!!.setOnClickListener { language("km")
                recreate()
            }

        }
        //Log.d("khmer",language)

        requestStoragePermission(false)
        requestStoragePermission(true)
        navView.setNavigationItemSelectedListener(this)
        val bnavigation = findViewById<BottomNavigationView>(R.id.bnaviga)
        bnavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
//                    val intent = Intent(this@Home,Home::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                }
                R.id.notification -> {
                    val intent = Intent(this@Home,Notification::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }
                R.id.camera ->{
                    if (sharedPref.contains("token") || sharedPref.contains("id")) {
                        val intent = Intent(this@Home, Camera::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }else{
                        val intent = Intent(this@Home, UserAccount::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }
                R.id.message -> {
                    if (sharedPref.contains("token") || sharedPref.contains("id")) {
                        val intent = Intent(this@Home,ChatMainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                    }else{
                        val intent = Intent(this@Home, UserAccount::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }
                R.id.account ->{
                    if (sharedPref.contains("token") || sharedPref.contains("id")) {
                        val intent = Intent(this@Home, Account::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }else{
                        val intent = Intent(this@Home, UserAccount::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }

            }
            true
        }

        //val sharedPref: SharedPreferences = getSharedPreferences("Register", Context.MODE_PRIVATE)
        //SliderImage
        val sliderImage = findViewById(R.id.slider) as SliderImage
        val images = listOf("https://i.redd.it/glin0nwndo501.jpg", "https://i.redd.it/obx4zydshg601.jpg",
                            "https://i.redd.it/glin0nwndo501.jpg", "https://i.redd.it/obx4zydshg601.jpg")
        sliderImage.setItems(images)
        sliderImage.addTimerToSlide(3000)
        //  sliderImage.removeTimerSlide()
        sliderImage.getIndicator()
        //Buy sell and Rent
        val buy = findViewById<TextView>(R.id.buy)
        buy.setOnClickListener{
            //      getActivity()!!.getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment_buy_vehicle()).commit()
            val intent = Intent(this@Home, Buy::class.java)
            intent.putExtra("Title","Buy")
            startActivity(intent)
        }
        val sell = findViewById<TextView>(R.id.sell)
        sell.setOnClickListener {
            val intent = Intent(this@Home, Sell::class.java)
            intent.putExtra("Title","Sell")
            startActivity(intent)
        }
        val rent = findViewById<TextView>(R.id.rent)
        rent.setOnClickListener {
            val intent = Intent(this@Home, Rent::class.java)
            intent.putExtra("Title","Rent")
            startActivity(intent)
        }
        //Best Deal
        val version = ArrayList<Item>()
        version.addAll(Item.getType("Discount"))

        best_list = findViewById<RecyclerView>(R.id.horizontal)
        best_list!!.layoutManager = LinearLayoutManager(this@Home, LinearLayout.HORIZONTAL, false)
        //best_list!!.adapter = MyAdapter(version)
        getBest()

        val listview = findViewById<RecyclerView>(R.id.list_new_post)

        recyclerView = findViewById<RecyclerView>(R.id.list_new_post)
        recyclerView!!.layoutManager = GridLayoutManager(this@Home,1)
        Get()

        val ddCategory:Button=findViewById(R.id.sp_category)
        ddBrand=findViewById(R.id.sp_brand)
        val ddYear:Button=findViewById(R.id.sp_year)

        initialCategoryDropdown()
        initialBrandDropdownList()
        initialYearDropdownList()

        ddCategory.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(this@Home)
            mBuilder.setTitle("Choose Category")
            mBuilder.setSingleChoiceItems(listItems1, -1, DialogInterface.OnClickListener { dialogInterface, i ->
                ddCategory.setText(listItems1!!.get(i))
                categoryId=categoryIdItems!!.get(i).toString()
                //Toast.makeText(this@Home, categoryIdItems!!.get(i).toString(), Toast.LENGTH_LONG).show()
                initialBrandDropdownList()
                dialogInterface.dismiss()
            })

            val mDialog = mBuilder.create()
            mDialog.show()
        })

        ddBrand.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(this@Home)
            mBuilder.setTitle("Choose Brand")
            mBuilder.setSingleChoiceItems(brandListItems, -1, DialogInterface.OnClickListener { dialogInterface, i ->
                ddBrand.setText(brandListItems!!.get(i))
                brandId = brandIdListItems!!.get(i).toString()
                //Toast.makeText(this@Home, brandIdListItems!!.get(i).toString(), Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
            })
            val mDialog = mBuilder.create()
            mDialog.show()
        })

        ddYear.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(this@Home)
            mBuilder.setTitle("Choose Year")
            mBuilder.setSingleChoiceItems(yearListItems, -1, DialogInterface.OnClickListener { dialogInterface, i ->
                ddYear.setText(yearListItems!!.get(i))
                yearId=yearIdListItems!!.get(i).toString()
                //Toast.makeText(this@Home, yearIdListItems!!.get(i).toString(), Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
            })

            val mDialog = mBuilder.create()
            mDialog.show()
        })

        val search = findViewById<EditText>(R.id.search)
        search.setOnClickListener{
            val intent = Intent(this@Home, Search1::class.java)
            intent.putExtra("category",categoryId)
            intent.putExtra("brand",brandId)
            intent.putExtra("year",yearId)
            startActivity(intent)
        }

    }
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {

            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_profile -> {
                // Handle the camera action
                val intent = Intent(this@Home, Edit_account::class.java)
                startActivity(intent)
            }
            R.id.nav_post -> {
                val intent = Intent(this@Home, Your_Post::class.java)
                startActivity(intent)
            }
            R.id.nav_like -> {
                val intent = Intent(this@Home,Account::class.java)
                startActivity(intent)
            }
            R.id.nav_loan -> {
                val intent = Intent(this@Home,Account::class.java)
                startActivity(intent)
            }
            R.id.nav_setting -> {
                val intent = Intent(this@Home, Setting::class.java)
                startActivity(intent)
            }
            R.id.nav_about -> {
                val intent = Intent(this@Home, AboutUsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_privacy -> {
                val intent = Intent(this@Home, TermPrivacyActivity::class.java)
                startActivity(intent)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onResume() {
        super.onResume()
        checkForCrashes()
    }

    private fun checkForCrashes() {
        CrashManager.register(this)
    }

    private fun initialCategoryDropdown(){
        val itemApi = ArrayList<String>()
        val url = ConsumeAPI.BASE_URL+"/api/v1/categories/"
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val respon = response.body()!!.string()
                Log.d("Response","initial Category "+ respon)
                try {
                    listItems = ArrayList<String>()

                    val jsonObject = JSONObject(respon)
                    val jsonArray = jsonObject.getJSONArray("results")
                    //Log.d("count",jsonArray.length().toString())
                    listItems1 = arrayOfNulls<String>(jsonArray.length())
                    categoryIdItems= arrayOfNulls<Int>(jsonArray.length())
                    for (i in 0 until jsonArray.length()) {
                        val `object` = jsonArray.getJSONObject(i)
                        var cat_id=`object`.getInt("id")
                        val cagory = `object`.getString("cat_name")
                         runOnUiThread {
                             listItems1[i]=cagory
                             categoryIdItems[i]=cat_id
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun initialBrandDropdownList(){

        val url = ConsumeAPI.BASE_URL+"/api/v1/brands/"
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val respon = response.body()!!.string()
                try {
                    runOnUiThread {
                        ddBrand.setHint(getString(R.string.brand))
                        brandId=""
                        val jsonObject = JSONObject(respon)
                        val jsonArray = jsonObject.getJSONArray("results")

                        if (categoryId.isNullOrEmpty()) {
                            brandListItems = arrayOfNulls<String>(jsonArray.length())
                            brandIdListItems = arrayOfNulls<Int>(jsonArray.length())
                            for (i in 0 until jsonArray.length()) {
                                val `object` = jsonArray.getJSONObject(i)
                                var id = `object`.getInt("id")
                                val brand = `object`.getString("brand_name")
                                    brandListItems[i] = brand
                                    brandIdListItems[i] = id
                            }
                        } else {
                            var count=0
                            for (i in 0 until jsonArray.length()) {
                                val `object` = jsonArray.getJSONObject(i)
                                var cat = `object`.getInt("category").toString()

                                if (categoryId.equals(cat)) {
                                    count++
                                }
                            }
                            brandListItems = arrayOfNulls<String>(count)
                            brandIdListItems = arrayOfNulls<Int>(count)
                            var ccount=0
                            for (i in 0 until jsonArray.length()) {
                                val `object` = jsonArray.getJSONObject(i)
                                var cat = `object`.getInt("category").toString()
                                if (categoryId.equals(cat)) {
                                    var id = `object`.getInt("id")
                                    val brand = `object`.getString("brand_name")
                                    Log.d("HOME","BRAND "+id+" "+brand)
                                    brandListItems[ccount] = brand
                                    brandIdListItems[ccount] = id
                                    ccount++
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun initialYearDropdownList(){
        val url = ConsumeAPI.BASE_URL+"/api/v1/years/"
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val respon = response.body()!!.string()
                //Log.d("Response","initial Category "+ respon)
                try {
                    val jsonObject = JSONObject(respon)
                    val jsonArray = jsonObject.getJSONArray("results")
                    yearListItems = arrayOfNulls<String>(jsonArray.length())
                    yearIdListItems= arrayOfNulls<Int>(jsonArray.length())
                    for (i in 0 until jsonArray.length()) {
                        val `object` = jsonArray.getJSONObject(i)
                        var id=`object`.getInt("id")
                        val year = `object`.getString("year")
                        runOnUiThread {
                            yearListItems[i]=year
                            yearIdListItems[i]=id
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    fun getUserProfile(){
        var user1 = User()
        var URL_ENDPOINT=ConsumeAPI.BASE_URL+"api/v1/users/"+pk
        var MEDIA_TYPE=MediaType.parse("application/json")
        val encodeAuth="Basic $Encode"
        var client= OkHttpClient()
        var request=Request.Builder()
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

                val gson = Gson()
                try {
                    user1= gson.fromJson(mMessage, User::class.java)
                    runOnUiThread {
                        val drawer_username=findViewById<TextView>(R.id.drawer_username)
                        drawer_username.setText(user1.username)
                        if(user1.profile!=null) {
                            val profilepicture: String = if (user1.profile.profile_photo == null) "" else user1.profile.base64_profile_image
                            val coverpicture: String = if (user1.profile.cover_photo == null) "" else user1.profile.base64_cover_photo_image
                            Log.d("TAGGGGG", profilepicture)
                            Log.d("TAGGGGG", coverpicture)
                            //tvUsername!!.setText(user1.username)
                            //Glide.with(this@Account).load(profilepicture).apply(RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imgProfile)
                            //Glide.with(this@Account).load(profilepicture).forImagePreview().into(imgCover)
                            if (profilepicture.isNullOrEmpty()) {
                                imageView!!.setImageResource(R.drawable.user)
                            } else {
                                val decodedString = Base64.decode(profilepicture, Base64.DEFAULT)
                                //var decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                                val imageView = findViewById<CircleImageView>(R.id.imageView)
                                //imageView!!.setImageBitmap(decodedByte)

                                if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= android.os.Build.VERSION_CODES.O_MR1) {
                                    //imageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 150, 150, false))
                                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                                    imageView.setImageBitmap(decodedByte)
                                }else {

                                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                                    imageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 500, 500, false))
                                }

                            }

                            if (coverpicture == null) {

                            } else {
                                val decodedString = Base64.decode(coverpicture, Base64.DEFAULT)
                                var decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                                val cover_layout = findViewById<LinearLayout>(R.id.cover_layout)

                                //imgCover!!.setImageBitmap(decodedByte)
                            }
                        }
                    }

                } catch (e: JsonParseException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun Get(): ArrayList<Item_API>{
        val itemApi = ArrayList<Item_API>()
        val url = "http://103.205.26.103:8000/allposts/"
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val respon = response.body()!!.string()
                Log.d("Response", respon)
                try {
                    val jsonObject = JSONObject(respon)
                    val jsonArray = jsonObject.getJSONArray("results")

                    //Log.d("count",jsonArray.length().toString())

                    for (i in 0 until jsonArray.length()) {

                        val `object` = jsonArray.getJSONObject(i)
                        val title = `object`.getString("title")
                        val id = `object`.getInt("id")
                        val condition = `object`.getString("condition")
                        val cost = `object`.getDouble("cost")
                        val image = `object`.getString("front_image_base64")
                        val img_user = `object`.getString("right_image_base64")
                        val postType = `object`.getString("post_type")

                        var location_duration=""
                        //var count_view=countPostView(Encode,id)

                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
                        val time:Long = sdf.parse(`object`.getString("created")).getTime()
                        val now:Long = System.currentTimeMillis()
                        val ago:CharSequence = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)


                        //itemApi.add(Item_API(id,img_user,image,title,cost,condition,postType,ago.toString(),count_view.toString()))
                        runOnUiThread {

                            var cc=0
                            val URL_ENDPOINT1= ConsumeAPI.BASE_URL+"countview/?post="+id
                            var MEDIA_TYPE=MediaType.parse("application/json")
                            val client1= OkHttpClient()
                            val auth = "Basic $Encode"
                            val request1=Request.Builder()
                                    .url(URL_ENDPOINT1)
                                    .header("Accept","application/json")
                                    .header("Content-Type","application/json")
                                    //.header("Authorization",auth)
                       //             .header("Authorization",auth)
                                    .build()
                            client1.newCall(request1).enqueue(object : Callback{
                                override fun onFailure(call: Call, e: IOException) {
                                    val mMessage1 = e.message.toString()
                                    Log.w("failure Response", mMessage1)
                                }
                                @Throws(IOException::class)
                                override fun onResponse(call: Call, response: Response) {
                                    val mMessage1 = response.body()!!.string()
                                    val gson = Gson()
                                    //Log.d("HOME",mMessage1)
                                    runOnUiThread {
                                        try {
                                            val jsonObject= JSONObject(mMessage1)
                                            Log.d("FFFFFF"," CCOUNT"+jsonObject)
                                            val jsonCount=jsonObject.getInt("count")
                                            cc=jsonCount
                                            itemApi.add(Item_API(id,img_user,image,title,cost,condition,postType,ago.toString(),jsonCount.toString()))

                                            recyclerView!!.adapter = MyAdapter_list_grid_image(itemApi, "List")
                                            //List Grid and Image
                                            list = findViewById(R.id.img_list)
                                            list!!.setOnClickListener {
                                                recyclerView!!.adapter = MyAdapter_list_grid_image(itemApi, "List")
                                                recyclerView!!.layoutManager = GridLayoutManager(this@Home,1)
                                            }
                                            grid = findViewById(R.id.grid)
                                            grid!!.setOnClickListener {
                                                recyclerView!!.adapter = MyAdapter_list_grid_image(itemApi, "Grid")
                                                recyclerView!!.layoutManager = GridLayoutManager(this@Home,2)
                                            }
                                            image_list = findViewById(R.id.btn_image)
                                            image_list!!.setOnClickListener {
                                                recyclerView!!.adapter = MyAdapter_list_grid_image(itemApi, "Image")
                                                recyclerView!!.layoutManager = GridLayoutManager(this@Home,1)
                                            }
                                        } catch (e: JsonParseException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            })

                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
        return itemApi
    }
    private fun getBest(): ArrayList<Item_discount>{
        val itemApi = ArrayList<Item_discount>()
        val url = "http://103.205.26.103:8000/bestdeal/"
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val respon = response.body()!!.string()
                Log.d("Response", respon)
                try {
                    val jsonObject = JSONObject(respon)
                    val jsonArray = jsonObject.getJSONArray("results")

                    Log.d("count",jsonArray.length().toString())

                    for (i in 0 until jsonArray.length()) {
                        var cc =0
                        val `object` = jsonArray.getJSONObject(i)
                        val title = `object`.getString("title")
                        val id = `object`.getInt("id")
                        val condition = `object`.getString("condition")
                        val cost = `object`.getDouble("cost")
                        val discount = `object`.getDouble("discount")
                        val image = `object`.getString("front_image_base64")
                        val img_user = `object`.getString("right_image_base64")
                        val postType = `object`.getString("post_type")
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
                        val time:Long = sdf.parse(`object`.getString("created")).getTime()
                        val now:Long = System.currentTimeMillis()
                        val ago:CharSequence = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)

//                        itemApi.add(Item_discount(id,img_user,image,title,cost,discount,condition,postType,ago.toString()))
                        runOnUiThread {
//                            best_list!!.adapter = MyAdapter(itemApi)
                            val URL_ENDPOINT1= ConsumeAPI.BASE_URL+"countview/?post="+id
                            var MEDIA_TYPE=MediaType.parse("application/json")
                            val client1= OkHttpClient()
                            val auth = "Basic $Encode"
                            val request1=Request.Builder()
                                    .url(URL_ENDPOINT1)
                                    .header("Accept","application/json")
                                    .header("Content-Type","application/json")
                                    //.header("Authorization",auth)
                                    //             .header("Authorization",auth)
                                    .build()
                            client1.newCall(request1).enqueue(object : Callback{
                                override fun onFailure(call: Call, e: IOException) {
                                    val mMessage1 = e.message.toString()
                                    Log.w("failure Response", mMessage1)
                                }
                                @Throws(IOException::class)
                                override fun onResponse(call: Call, response: Response) {
                                    val mMessage1 = response.body()!!.string()
                                    val gson = Gson()
                                    //Log.d("HOME",mMessage1)
                                    runOnUiThread {
                                        try {
                                            val jsonObject= JSONObject(mMessage1)
                                            Log.d("FFFFFF"," CCOUNT"+jsonObject)
                                            val jsonCount=jsonObject.getInt("count")
                                            cc=jsonCount
                                            itemApi.add(Item_discount(id,img_user,image,title,cost,discount,condition,postType,ago.toString(),cc.toString()))

                                            best_list!!.adapter = MyAdapter(itemApi)
                                            //List Grid and Image
                                        } catch (e: JsonParseException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            })

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        return itemApi
    }

    fun countPostView(encode:String,postId:Int):Int{
        var count=0
        val URL_ENDPOINT= ConsumeAPI.BASE_URL+"countview/?post="+postId
        var MEDIA_TYPE=MediaType.parse("application/json")
        val client= OkHttpClient()
        val auth = "Basic $encode"
        val request=Request.Builder()
                .url(URL_ENDPOINT)
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",auth)
                .build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                Log.w("failure Response", mMessage)
            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val mMessage = response.body()!!.string()
                val gson = Gson()
                try {
                    val jsonObject= JSONObject(mMessage)
                    val jsonCount=jsonObject.getInt("count")
                    runOnUiThread {
                        count=jsonCount
                    }

                } catch (e: JsonParseException) {
                    e.printStackTrace()
                }

            }
        })
        return count
    }
    private fun requestStoragePermission(isCamera: Boolean) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                //dispatchTakePictureIntent()
                            } else {
                                //dispatchGalleryIntent()
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { error -> Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }
    // navigating user to app settings
    private fun openSettings() {
        //Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }
    private fun showSettingsDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()

    }

}

