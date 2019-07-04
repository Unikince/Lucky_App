package com.example.lucky_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.lucky_app.Api.ConsumeAPI;
import com.example.lucky_app.R;
import com.example.lucky_app.utils.FileCompressor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tiper.MaterialSpinner;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Camera extends AppCompatActivity {

    private static final String TAG = "Response";
    static final int REQUEST_TAKE_PHOTO_1=1;
    static final int REQUEST_TAKE_PHOTO_2=2;
    static final int REQUEST_TAKE_PHOTO_3=3;
    static final int REQUEST_TAKE_PHOTO_4=4;
    static final int REQUEST_GALLERY_PHOTO = 5;
    private int REQUEST_TAKE_PHOTO_NUM=0;
    File mPhotoFile;
    FileCompressor mCompressor;

    private EditText etTitle,etVinCode,etMachineCode,etDescription,etPrice,etDiscount_amount,etName,etPhone1,etPhone2,etPhone3,etEmail;
    private ImageView icPostType,icCategory,icType_elec,icBrand,icModel,icYears,icCondition,icColor,icRent,icDiscount_type,
            icTitile,icVincode,icMachineconde,icDescription,icPrice,icDiscount_amount,icName,icEmail,icPhone1,icPhone2,icPhone3;
    private ImageButton addPhone2,addPhone1;
    private TextInputLayout tilPhone2,tilPhone3;
    private MaterialSpinner tvPostType,tvCategory, tvType_elec,tvBrand,tvModel,tvYear,tvCondition,tvColor,tvRent,tvDiscount_type;
    private Button submit_post;
    private ImageView imageView1,imageView2,imageView3,imageView4,imageView5;
    private String name,pass,Encode;
    private ArrayAdapter<String> brands,models;
    private ArrayAdapter<Integer> ID_category,ID_brands,ID_type,ID_year,ID_model;
    private List<String> list_category = new ArrayList<>();
    private List<String> list_type = new ArrayList<>();
    private List<String> list_brand = new ArrayList<>();
    private List<String> list_model = new ArrayList<>();
    private List<String> list_year= new ArrayList<>();
    private List<Integer> list_id_category = new ArrayList<>();
    private List<Integer> list_id_type = new ArrayList<>();
    private List<Integer> list_id_brands = new ArrayList<>();
    private List<Integer> list_id_model = new ArrayList<>();
    private List<Integer> list_id_year = new ArrayList<>();
    String id_cate, id_brand,id_model,id_year,id_type;
    int cate,brand,model,year,type;
    SharedPreferences prefer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        prefer = getSharedPreferences("Register",MODE_PRIVATE);
        name = prefer.getString("name","");
        pass = prefer.getString("pass","");
        Encode = getEncodedString(name,pass);
        ButterKnife.bind(this);
        mCompressor = new FileCompressor(this);

        BottomNavigationView bnavigation = findViewById(R.id.bnaviga);
        bnavigation.getMenu().getItem(2).setChecked(true);
        bnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(),Home.class);
                        startActivity(intent);
                        break;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),Notification.class));
                        break;
                    case R.id.camera:

                        break;
                    case R.id.message:
                        startActivity(new Intent(getApplicationContext(),Message.class));
                        break;
                    case R.id.account :
                        startActivity(new Intent(getApplicationContext(),Account.class));
                        break;
                }
                return false;
            }
        });

        Variable_Field();
        PhoneNumber();
        DropDown();
        Call_category(Encode);
        Call_Type(Encode);
//        Call_Brand(Encode,id_cate);
//        Call_Model(Encode);
        Call_years(Encode);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                REQUEST_TAKE_PHOTO_NUM=REQUEST_TAKE_PHOTO_1;
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                REQUEST_TAKE_PHOTO_NUM=REQUEST_TAKE_PHOTO_2;
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                REQUEST_TAKE_PHOTO_NUM=REQUEST_TAKE_PHOTO_3;
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                REQUEST_TAKE_PHOTO_NUM=REQUEST_TAKE_PHOTO_4;
            }
        });


        submit_post = (Button) findViewById(R.id.btnSubmitPost);
        submit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostData(Encode);
            }
        });

    } // create
    private void PostData(String encode) {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "";
        OkHttpClient client = new OkHttpClient();
        JSONObject post = new JSONObject();
        JSONObject sale = new JSONObject();
        try {

            String postType = tvPostType.getSelectedItem().toString().toLowerCase();

            post.put("title",etTitle.getText().toString().toLowerCase());
            post.put("category", cate );
            post.put("status", "");
            post.put("condition",tvCondition.getSelectedItem().toString().toLowerCase() );
            post.put("discount_type", tvDiscount_type.getSelectedItem().toString().toLowerCase() );
            post.put("discount", etDiscount_amount.getText().toString());
            post.put("user",null );
            post.put("front_image_path", null);
            post.put("right_image_path", null);
            post.put("left_image_path", null);
            post.put("back_image_path", null);
            post.put("created", "");
            post.put("created_by", 1);
            post.put("modified", null);
            post.put("modified_by", null);
            post.put("approved_date", null);
            post.put("approved_by", null);
            post.put("rejected_date", null);
            post.put("rejected_by",null);
            post.put("rejected_comments", "");
            post.put("year", year);
            post.put("modeling", model);
            post.put("description", etDescription.getText().toString().toLowerCase());
            post.put("cost", etPrice.getText().toString().toLowerCase());
            post.put("post_type",tvPostType.getSelectedItem().toString().toLowerCase() );
            post.put("vin_code", etVinCode.getText().toString().toLowerCase());
            post.put("machine_code", etMachineCode.getText().toString().toLowerCase());
            post.put("type", type);
            post.put("contact_phone", etPhone1.getText().toString().toLowerCase());
            post.put("contact_email", etEmail.getText().toString().toLowerCase() );
            post.put("contact_address", "");
            post.put("color", tvColor.getSelectedItem().toString().toLowerCase());


            switch (postType){
                case "sell":
                    url=ConsumeAPI.BASE_URL+"postsale/";
                    Log.d("URL","URL"+url);
                    sale.put("sale_status", 2);
                    sale.put("record_status",2);
                    sale.put("sold_date", null);
                    sale.put("price", etPrice.getText().toString().toLowerCase());
                    sale.put("total_price", etPrice.getText().toString().toLowerCase());

                    post.put("sale_post",new JSONArray("["+sale+"]"));
                    break;
                case "rent":
                    url = ConsumeAPI.BASE_URL+"postrent/";
                    JSONObject rent=new JSONObject();
                    rent.put("rent_status",1);
                    rent.put("record_status",1);
                    rent.put("rent_type",tvRent.getSelectedItem().toString().toLowerCase());
                    rent.put("price",etPrice.getText().toString().toLowerCase());
                    rent.put("total_price",etPrice.getText().toString().toLowerCase());
                    rent.put("rent_date",null);
                    rent.put("return_date",null);
                    rent.put("rent_count_number",0);
                    post.put("rent_post",new JSONArray("["+rent+"]"));
                    break;
                case "buy":
                    url = ConsumeAPI.BASE_URL+"api/v1/postbuys/";
                    JSONObject buy=new JSONObject();
                    buy.put("buy_status",1);
                    buy.put("record_status",1);
                    post.put("buy_post",new JSONArray("["+buy+"]"));
                    break;
            }

//            if(postType == "sell") {
//                //url = String.format("%s%s", ConsumeAPI.BASE_URL, "");
//                url=ConsumeAPI.BASE_URL+"postsale/";
//                Log.d("URL","URL"+url);
//                sale.put("sale_status", 2);
//                sale.put("record_status",2);
//                sale.put("sold_date", null);
//                sale.put("price", etPrice.getText().toString().toLowerCase());
//                sale.put("total_price", etPrice.getText().toString().toLowerCase());
//                post.put("sale_post",new JSONArray("["+sale+"]"));
//            }
//            else if(postType=="rent") {
//                url = String.format("%s%s", ConsumeAPI.BASE_URL, "postrent/");
//                JSONObject rent=new JSONObject();
//                rent.put("rent_status",1);
//                rent.put("record_status",1);
//                rent.put("rent_type",tvRent.getText().toString().toLowerCase());
//                rent.put("price",etPrice.getText().toString().toLowerCase());
//                rent.put("total_price",etPrice.getText().toString().toLowerCase());
//                rent.put("rent_date",null);
//                rent.put("return_date",null);
//                rent.put("rent_count_number",0);
//                post.put("rent_post",new JSONArray("["+rent+"]"));
//            }
//            else if(postType=="buy") {
//                url = String.format("%s%s", ConsumeAPI.BASE_URL, "api/v1/postbuys/");
//
//            }
            Log.d(TAG,url);
            RequestBody body = RequestBody.create(MEDIA_TYPE, post.toString());
            String auth = "Basic " + encode;
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization",auth)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String mMessage = e.getMessage().toString();
                    Log.d("Failure:",mMessage );

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String message = response.body().string();
                    Log.d("Response",message);

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    } //postdata

    private void Call_category(String encode) {

        final String url = String.format("%s%s", ConsumeAPI.BASE_URL,"api/v1/categories/");
        OkHttpClient client = new OkHttpClient();
        String auth = "Basic "+ encode;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",auth)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String respon = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(respon);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id = object.getInt("id");
                        String name = object.getString("cat_name");
                        list_id_category.add(id);
                        list_category.add(name);
                          ID_category = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_id_category);
                        final ArrayAdapter<String> category = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_category);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvCategory.setAdapter(category);
                            }
                        });
                    }


                    tvCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {


                            id_cate = String.valueOf(ID_category.getItem(i));
                            Log.d("ID", id_cate);
                            cate = Integer.parseInt(id_cate);
                            Call_Brand(Encode,id_cate);
                        }

                        @Override
                        public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });


    }  // category

    private void Call_Type(String encode) {
        final String url = String.format("%s%s", ConsumeAPI.BASE_URL,"api/v1/types/");
        OkHttpClient client = new OkHttpClient();
        String auth = "Basic "+ encode;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",auth)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respon = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(respon);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id = object.getInt("id");
                        String name = object.getString("type");
                        list_id_type.add(id);
                        list_type.add(name);
                        ID_type = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_id_type);
                        final ArrayAdapter<String> type = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_type);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvType_elec.setAdapter(type);
                            }
                        });
                    }
                    tvType_elec.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                            id_type = String.valueOf(ID_type.getItem(i));
                            type = Integer.parseInt(id_type);
                        }

                        @Override
                        public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    } // type

    private void Call_Brand(String encode , String id_cate){
        Log.d("Category id:",""+ id_cate);

        final int t = Integer.parseInt(id_cate);
        final String url = String.format("%s%s", ConsumeAPI.BASE_URL,"api/v1/brands/");
        OkHttpClient client = new OkHttpClient();
        String auth = "Basic "+ encode;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",auth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Failure Error",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respon = response.body().string();
              //  int n = 2;
                tvBrand.setAdapter(null);
                try{
                    JSONObject jsonObject = new JSONObject(respon);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        int cate = object.getInt("category");
                    if (cate==t){
                            int id = object.getInt("id");
                            String name = object.getString("brand_name");
                            list_id_brands.add(id);
                            list_brand.add(name);
                            ID_brands = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_id_brands);
                            brands = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_brand);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvBrand.setAdapter(brands);
                                }
                            });
                        }

                    }

                    tvBrand.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                           // brands.clear();
                                id_brand = String.valueOf(ID_brands.getItem(i));
                                Log.d("brand id",id_brand);
                                brand = Integer.parseInt(id_brand);
                                Call_Model(encode, id_brand);
                        }

                        @Override
                        public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

                        }
                    });


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }// brand

    private void Call_Model(String encode,String id_bran){

        int b = Integer.parseInt(id_bran);
        Log.d("brand id:",""+ b);
        final String url = String.format("%s%s", ConsumeAPI.BASE_URL,"api/v1/models/");
        OkHttpClient client = new OkHttpClient();
        String auth = "Basic "+ encode;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",auth)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respon = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(respon);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id = object.getInt("id");
                        int Brand = object.getInt("brand");
                        if (Brand==b) {
                            String name = object.getString("modeling_name");
                            list_id_model.add(id);
                            list_model.add(name);
                            ID_model = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_id_model);
                            models = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_model);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvModel.setAdapter(models);
                                }
                            });
                        }
                    }

                    tvModel.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                           id_model = String.valueOf(ID_model.getItem(i));
                           model = Integer.parseInt(id_model);
                        }

                        @Override
                        public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d("Exception",e.getMessage());
                }
            }
        });
    } // model

    private void Call_years(String encode){
        final String url = String.format("%s%s", ConsumeAPI.BASE_URL,"api/v1/years/");
        OkHttpClient client = new OkHttpClient();
        String auth = "Basic "+ encode;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("Content-Type","application/json")
                .header("Authorization",auth)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respon = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(respon);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id = object.getInt("id");
                        String name = object.getString("year");
                        list_id_year.add(id);
                        list_year.add(name);
                        ID_year = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_id_year);
                        final ArrayAdapter<String> years = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,list_year);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvYear.setAdapter(years);
                            }
                        });

                    }

                    tvYear.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                            id_year = String.valueOf(ID_year.getItem(i));
                            year = Integer.parseInt(id_year);
                        }

                        @Override
                        public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    } // years

    private void DropDown() {
        final String[] posttype = getResources().getStringArray(R.array.posty_type);
        ArrayAdapter<String> post = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, posttype);
        tvPostType.setAdapter(post);

        tvPostType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NotNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                String st = post.getItem(i);
                if (st.equals("Rent")){
                    icRent.setVisibility(View.VISIBLE);
                    tvRent.setVisibility(View.VISIBLE);
                }else {
                    icRent.setVisibility(View.GONE);
                    tvRent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(@NotNull MaterialSpinner materialSpinner) {

            }
        });

        String[] conditions = getResources().getStringArray(R.array.condition);
        ArrayAdapter<String> condition = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,conditions);
        tvCondition.setAdapter(condition);

        String[] colors = getResources().getStringArray(R.array.color);
        ArrayAdapter<String> color = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,colors);
        tvColor.setAdapter(color);

        String[] rents = getResources().getStringArray(R.array.rent_type);
        ArrayAdapter<String> rent = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,rents);
        tvRent.setAdapter(rent);

        String[] discount_type = getResources().getStringArray(R.array.discount_type);
        ArrayAdapter<String> discountType = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,discount_type);
        tvDiscount_type.setAdapter(discountType);

    }
    private void PhoneNumber(){
        icPhone2.setVisibility(View.GONE);
        tilPhone2.setVisibility(View.GONE);
        addPhone2.setVisibility(View.GONE);
        icPhone3.setVisibility(View.GONE);
        tilPhone3.setVisibility(View.GONE);

        addPhone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icPhone2.setVisibility(View.VISIBLE);
                tilPhone2.setVisibility(View.VISIBLE);
                addPhone2.setVisibility(View.VISIBLE);
            }
        });

        addPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icPhone3.setVisibility(View.VISIBLE);
                tilPhone3.setVisibility(View.VISIBLE);
            }
        });
    }
    private String getEncodedString(String username, String password) {
        final String userpass = username+":"+password;
        return Base64.encodeToString(userpass.getBytes(),
                Base64.NO_WRAP);
    }
    private void Variable_Field() {

//textview ///////
        tvPostType = (MaterialSpinner)findViewById(R.id.tvPostType);
        tvCategory = (MaterialSpinner) findViewById(R.id.tvCategory);
        tvType_elec= (MaterialSpinner) findViewById(R.id.tvType_elec);
        tvBrand    = (MaterialSpinner) findViewById(R.id.tvBrand);
        tvModel    = (MaterialSpinner) findViewById(R.id.tvModel);
        tvYear     = (MaterialSpinner) findViewById(R.id.tvYears);
        tvCondition= (MaterialSpinner) findViewById(R.id.tvCondition);
        tvColor    = (MaterialSpinner) findViewById(R.id.tvColor);
        tvRent     = (MaterialSpinner) findViewById(R.id.tvRent);
        tvDiscount_type = (MaterialSpinner)findViewById(R.id.tvDisType);
// edit text ////
        etTitle           = (EditText)findViewById(R.id.etTitle );
        etVinCode         = (EditText)findViewById(R.id.etVinCode );
        etMachineCode     = (EditText)findViewById(R.id.etMachineCode );
        etDescription     = (EditText)findViewById(R.id.etDescription );
        etPrice           = (EditText)findViewById(R.id.etPrice );
        etDiscount_amount = (EditText)findViewById(R.id.etDisAmount );
        etName            = (EditText)findViewById(R.id.etName );
        etPhone1          = (EditText)findViewById(R.id.etphone1 );
        etPhone2          = (EditText)findViewById(R.id.etphone2 );
        etPhone3          = (EditText)findViewById(R.id.etphone3 );
        etEmail           = (EditText)findViewById(R.id.etEmail );

// til phone
        tilPhone2 = (TextInputLayout)findViewById(R.id.tilPhone2);
        tilPhone3 = (TextInputLayout)findViewById(R.id.tilPhone3);

// add phone button

        addPhone2 = (ImageButton)findViewById(R.id.imgBtnPhone2);
        addPhone1 = (ImageButton)findViewById(R.id.imgBtnPhone1);
//// icon  ////////
        icPostType   = (ImageView) findViewById(R.id.imgPostType);
        icCategory   = (ImageView) findViewById(R.id. imgCategory);
        icType_elec  = (ImageView) findViewById(R.id.imgType_elec );
        icBrand      = (ImageView) findViewById(R.id. imgBrand);
        icModel      = (ImageView) findViewById(R.id. imgModel);
        icYears      = (ImageView) findViewById(R.id. imgYear);
        icCondition  = (ImageView) findViewById(R.id. imgCondition);
        icColor      = (ImageView) findViewById(R.id. imgColor);
        icRent       = (ImageView) findViewById(R.id. imgRent);
        icTitile     = (ImageView) findViewById(R.id. imgTitle);
        icVincode    = (ImageView) findViewById(R.id. imgVinCode);
        icMachineconde=(ImageView) findViewById(R.id. imgMachineCode);
        icDescription= (ImageView) findViewById(R.id. imgDescription);
        icPrice      = (ImageView) findViewById(R.id. imgPrice);
        icName       = (ImageView) findViewById(R.id. imgName);
        icEmail      = (ImageView) findViewById(R.id. imgEmail);
        icPhone1     = (ImageView) findViewById(R.id. imgPhone1);
        icPhone2     = (ImageView) findViewById(R.id. imgPhone2 );
        icPhone3     = (ImageView) findViewById(R.id. imgPhone3);
        icDiscount_amount = (ImageView) findViewById(R.id. imgDisAmount);
        icDiscount_type   = (ImageView) findViewById(R.id.imgDisType );

        imageView1=(ImageView) findViewById(R.id.Picture1);
        imageView2=(ImageView) findViewById(R.id.Picture2);
        imageView3=(ImageView) findViewById(R.id.Picture3);
        imageView4=(ImageView) findViewById(R.id.Picture4);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Camera.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }


    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getPackageName() + ".provider",
                        photoFile);
                //BuildConfig.APPLICATION_ID
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_NUM);
            }
        }
    }

    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO_1) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(Camera.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imageView1);
            }
            else if (requestCode == REQUEST_TAKE_PHOTO_2) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(Camera.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imageView2);
            }
            else if (requestCode == REQUEST_TAKE_PHOTO_3) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(Camera.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imageView3);
            }
            else if (requestCode == REQUEST_TAKE_PHOTO_4) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(Camera.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imageView4);
            }
            else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(Camera.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().centerCrop().placeholder(R.drawable.default_profile_pic)).into(imageView1);

            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        //Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    /**
     * Get real file path from URI
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

