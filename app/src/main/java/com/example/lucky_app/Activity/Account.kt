package com.example.lucky_app.Activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.core.content.FileProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.lucky_app.Api.ConsumeAPI
import com.example.lucky_app.Api.User
import com.example.lucky_app.Edit_Account.Edit_account
import com.example.lucky_app.Edit_Account.Sheetviewupload
import com.example.lucky_app.Fragment.Tab_Adapter_Acc
import com.example.lucky_app.R
import com.example.lucky_app.Setting.Setting
import com.example.lucky_app.utils.FileCompressor
import com.example.lucky_app.utils.ImageUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_acount.imgCover
import kotlinx.android.synthetic.main.fragment_acount.imgProfile
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class Account : AppCompatActivity(){//}, Sheetviewupload.BottomSheetListener {

    private val REQUEST_TAKE_PHOTO=1;
    private val REQUEST_GALLARY_PHOTO=2;
    private val GALLERY = 1
    private val CAMERA = 2
    private var type: String? = null
    private var uploadcover: Button? = null
    private var upload: ImageView? = null
    private var uploadprofile: ImageView? = null
    private var tvUsername: TextView?= null

    private var PRIVATE_MODE = 0
    var username=""
    var password=""
    var encodeAuth=""
    var API_ENDPOINT=""
    var pk=0
    var prefs: SharedPreferences?=null
    private var user:User?=null
    var mPhotoFile: File?=null
    var mCompressor: FileCompressor?=null
    var bitmapImage:Bitmap?=null

    //upload image from gallery or camera, it implement from interface BottomSheetListener
    /*
    override fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun gallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }
    */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.lucky_app.R.layout.activity_account)

        val bnavigation = findViewById<BottomNavigationView>(com.example.lucky_app.R.id.bnaviga)
        bnavigation.menu.getItem(4).isChecked = true
        bnavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this@Account,Home::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
                R.id.notification -> {
                    val intent = Intent(this@Account,Notification::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                R.id.camera ->{
                    val intent = Intent(this@Account,Camera::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                R.id.message -> {
                    val intent = Intent(this@Account,Message::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                R.id.account ->{
//                    val intent = Intent(this@Account,Account::class.java)
//                    startActivity(intent)
                }

            }
            true
        }

        //press to show layout sheet_view_upload
        uploadcover = findViewById<Button>(R.id.btnUpload_Cover)
        uploadcover!!.setOnClickListener{
            type = "cover"
            selectImage()
            //val upload = Sheetviewupload()
            //upload.show(supportFragmentManager,upload.tag)
        }
        upload = findViewById<ImageView>(R.id.imgProfile)
        upload!!.setOnClickListener{
            type = "profile"
            selectImage()
            //val upload = Sheetviewupload()
            //upload.show(supportFragmentManager,upload.tag)
        }
        uploadprofile = findViewById<ImageView>(R.id.imgCover)
        uploadprofile!!.setOnClickListener{
            type = "cover"
            //val upload = Sheetviewupload()
            //upload.show(supportFragmentManager,upload.tag)
        }


        val setting = findViewById<ImageButton>(R.id.btn_setting)
        setting.setOnClickListener {
            val intent = Intent(this@Account , Setting::class.java)
            startActivity(intent)
            Toast.makeText(this@Account,"Setting", Toast.LENGTH_SHORT).show()
        }

        val account_edit = findViewById<ImageButton>(R.id.btn_edit)
        account_edit.setOnClickListener {
            Toast.makeText(this@Account,"Home Edit", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@Account, Edit_account::class.java)
            startActivity(intent)
        }
        val tab = findViewById<TabLayout>(R.id.tab)
        val pager = findViewById<ViewPager>(R.id.pager)

        tab.addTab(tab.newTab().setText(com.example.lucky_app.R.string.post))
        tab.addTab(tab.newTab().setText(com.example.lucky_app.R.string.like))
        val adapter = Tab_Adapter_Acc(supportFragmentManager,tab.tabCount)
        pager.adapter = adapter
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab))
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        val preferences = getSharedPreferences("Register", Context.MODE_PRIVATE)
        username=preferences.getString("name","")
        password=preferences.getString("pass","")
        encodeAuth="Basic "+ getEncodedString(username,password)
        if (preferences.contains("token")) {
            pk = preferences.getInt("Pk", 0)
        } else if (preferences.contains("id")) {
            pk = preferences.getInt("id", 0)
        }
        tvUsername=findViewById<TextView>(R.id.tvUsername)
        getUserProfile()
        mCompressor = FileCompressor(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_TAKE_PHOTO){
                try{
                    mPhotoFile=mCompressor!!.compressToFile(mPhotoFile)
                    bitmapImage=BitmapFactory.decodeFile(mPhotoFile!!.getPath())
                }catch (e:IOException) {
                    e.printStackTrace()
                }
                if(type.equals("profile")) {
                    var URL_ENDPOINT = ConsumeAPI.BASE_URL + "api/v1/users/" + pk + "/profilephoto/"
                    var client = OkHttpClient()
                    val profile = JSONObject()
                    val profile_body = JSONObject()
                    try {
                        profile_body.put("profile_photo", ImageUtil.encodeFileToBase64Binary(ImageUtil.createTempFile(this, bitmapImage)))
                        profile.put("profile", profile_body)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(ConsumeAPI.MEDIA_TYPE, profile.toString())
                    val request = Request.Builder()
                            .url(URL_ENDPOINT)
                            .put(body)
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Authorization", encodeAuth)
                            .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            val result = e.message.toString()
                            Log.d("Profile", "Fail:$result")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val result = response.body()!!.string()
                            runOnUiThread {
                                Glide.with(this@Account).load(mPhotoFile).apply(RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imgProfile)
                            }
                            Log.d("Response", result)
                        }
                    })
                }else if(type.equals("cover")){
                    var URL_ENDPOINT = ConsumeAPI.BASE_URL + "api/v1/users/" + pk + "/coverphoto/"
                    var client = OkHttpClient()
                    val profile = JSONObject()
                    val profile_body = JSONObject()
                    try {
                        profile_body.put("cover_photo", ImageUtil.encodeFileToBase64Binary(ImageUtil.createTempFile(this, bitmapImage)))
                        profile.put("profile", profile_body)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(ConsumeAPI.MEDIA_TYPE, profile.toString())
                    val request = Request.Builder()
                            .url(URL_ENDPOINT)
                            .put(body)
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Authorization", encodeAuth)
                            .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            val result = e.message.toString()
                            Log.d("Profile", "Fail:$result")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val result = response.body()!!.string()
                            runOnUiThread {
                                Glide.with(this@Account).load(mPhotoFile).apply(RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imgCover)
                            }
                            Log.d("Response", result)
                        }
                    })
                }

            }
            else if(requestCode==REQUEST_GALLARY_PHOTO){
                val selectedImage = data!!.getData()
                try {
                    mPhotoFile = mCompressor!!.compressToFile(File(getRealPathFromUri(selectedImage)))
                    bitmapImage=BitmapFactory.decodeFile(mPhotoFile!!.getPath())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if(type.equals("profile")) {
                    var URL_ENDPOINT = ConsumeAPI.BASE_URL + "api/v1/users/" + pk + "/profilephoto/"
                    var client = OkHttpClient()
                    val profile = JSONObject()
                    val profile_body = JSONObject()
                    try {
                        profile_body.put("profile_photo", ImageUtil.encodeFileToBase64Binary(ImageUtil.createTempFile(this, bitmapImage)))
                        profile.put("profile", profile_body)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(ConsumeAPI.MEDIA_TYPE, profile.toString())
                    val request = Request.Builder()
                            .url(URL_ENDPOINT)
                            .put(body)
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Authorization", encodeAuth)
                            .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            val result = e.message.toString()
                            Log.d("Profile", "Fail:$result")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val result = response.body()!!.string()
                            runOnUiThread {
                                Glide.with(this@Account).load(mPhotoFile).apply(RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imgProfile)
                            }
                            Log.d("Response", result)
                        }
                    })
                }else if(type.equals("cover")){
                    var URL_ENDPOINT = ConsumeAPI.BASE_URL + "api/v1/users/" + pk + "/coverphoto/"
                    var client = OkHttpClient()
                    val profile = JSONObject()
                    val profile_body = JSONObject()
                    try {
                        profile_body.put("cover_photo", ImageUtil.encodeFileToBase64Binary(ImageUtil.createTempFile(this, bitmapImage)))
                        profile.put("profile", profile_body)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val body = RequestBody.create(ConsumeAPI.MEDIA_TYPE, profile.toString())
                    val request = Request.Builder()
                            .url(URL_ENDPOINT)
                            .put(body)
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Authorization", encodeAuth)
                            .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            val result = e.message.toString()
                            Log.d("Profile", "Fail:$result")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val result = response.body()!!.string()
                            runOnUiThread {
                                Glide.with(this@Account).load(mPhotoFile).apply(RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imgCover)
                            }
                            Log.d("Response", result)
                        }
                    })
                }
                //Glide.with(this@Account).load(mPhotoFile).apply(RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imgProfile)
            }

        }


        /*
        if(requestCode == CAMERA && resultCode == Activity.RESULT_OK)
        {
            val image = data!!.extras!!.get("data") as Bitmap
            if(type.equals("cover"))
            {
                imgCover!!.setImageBitmap(image)
            }
            else
            {
                imgProfile!!.setImageBitmap(image)
            }
        }
        else if(requestCode == GALLERY && resultCode == Activity.RESULT_OK)
        {
            if(data != null)
            {
                val content = data.data
                try{
                    val upload = MediaStore.Images.Media.getBitmap(this.contentResolver,content)
                    if(type.equals("cover"))
                    {
                        imgCover!!.setImageBitmap(upload)
                    }
                    else
                    {
                        imgProfile!!.setImageBitmap(upload)
                    }
                }
                catch(e: IOException)
                {
                    e.printStackTrace()
                }
            }
        }
        */
    }
    fun getEncodedString(username: String,password:String):String{
        val userpass = "$username:$password"
        return Base64.encodeToString(userpass.toByteArray(),
                Base64.NO_WRAP)
    }

    fun getUserProfile(){
        var user1 = User()
        var URL_ENDPOINT=ConsumeAPI.BASE_URL+"api/v1/users/"+pk
        var MEDIA_TYPE=MediaType.parse("application/json")
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
                        val profilepicture: String=if(user1.profile.profile_photo==null) " " else user1.profile.base64_profile_image
                        val coverpicture: String= if(user1.profile.cover_photo==null) " " else user1.profile.base64_cover_photo_image
                        Log.d("TAGGGGG",profilepicture)
                        Log.d("TAGGGGG",coverpicture)
                        tvUsername!!.setText(user1.username)
                        //Glide.with(this@Account).load(profilepicture).apply(RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imgProfile)
                        //Glide.with(this@Account).load(profilepicture).forImagePreview().into(imgCover)
                        if(profilepicture==null){

                        }else
                        {
                            val decodedString = Base64.decode(profilepicture, Base64.DEFAULT)
                            var decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            imgProfile!!.setImageBitmap(decodedByte)
                        }

                        if(coverpicture==null){

                        }else
                        {
                            val decodedString = Base64.decode(coverpicture, Base64.DEFAULT)
                            var decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            imgCover!!.setImageBitmap(decodedByte)
                        }

                    }

                } catch (e: JsonParseException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this@Account)
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo") {
                requestStoragePermission(true)
            } else if (items[item] == "Choose from Library") {
                requestStoragePermission(false)
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }
    private fun requestStoragePermission(isCamera: Boolean) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent()
                            } else {
                                dispatchGalleryIntent()
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
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        this.packageName + ".provider",
                        photoFile)
                //BuildConfig.APPLICATION_ID
                mPhotoFile = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }
    private fun dispatchGalleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, REQUEST_GALLARY_PHOTO)
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
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }
    fun getRealPathFromUri(contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(contentUri, proj, null, null, null)
            assert(cursor != null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }
}
