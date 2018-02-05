package com.example.nedved.ifindit;


import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity  {

    private static final int RECORD_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private TessBaseAPI baseApi;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCDjbuo5V71t0ByASSLEubeOxgyndABoww";
////
public static final String TESS_DATA = "/tessdata";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private TextView textView;
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private String mCurrentPhotoPath;




    /////
    @BindView(R.id.takePicture)
    Button takePicture;
    @BindView(R.id.button2)
    Button b2;
    @BindView(R.id.button3)
    Button b3;
    @BindView(R.id.imageProgress)
    ProgressBar imageUploadProgress;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.spinnerVisionAPI)
    Spinner spinnerVisionAPI;

    @BindView(R.id.visionAPIData)
    TextView visionAPIData;
    private Feature feature;
    Bitmap bitmap;
    int hj,hj1=0;
    Context con;
    private String[] visionAPI = new String[]{"LANDMARK_DETECTION", "LOGO_DETECTION",
            "WEB_DETECTION", "SAFE_SEARCH_DETECTION", "IMAGE_PROPERTIES", "LABEL_DETECTION"};

    public String api = visionAPI[0];
    ArrayList<String> La_d = new ArrayList<>();
    ArrayList<String> Lo_d = new ArrayList<>();
    ArrayList<String> ssd = new ArrayList<>();
    ArrayList<String> ip = new ArrayList<>();
    ArrayList<String> ld = new ArrayList<>();
    ArrayList<String> wb = new ArrayList<>();

    String detectedText="";
     Animation animAlpha;

    private String imagePath;

    private final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 911;
    TextView u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        feature = new Feature();
        feature.setType("LANDMARK_DETECTION");
        feature.setMaxResults(10);
         u =(TextView)findViewById(R.id.textView4);


        con=this;
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.rotate);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, visionAPI);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.r1);
        hj1=0;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {

            makeRequest(Manifest.permission.CAMERA);
        }
    }

    private int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundColor(R.drawable.common_google_signin_btn_icon_light);
            hj1 = 0;
        }



                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                    Uri selectedImageUri = data.getData();
                    imagePath = getRealPathFromURI(selectedImageUri);
                    Toast.makeText(this, imagePath, Toast.LENGTH_LONG).show();

                    File image = new File(imagePath);
                    imageView.setBackgroundColor(R.drawable.common_google_signin_btn_icon_light);

                    Picasso.with(getApplicationContext())
                            .load(image)
                            .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                            .error(R.drawable.common_google_signin_btn_icon_dark_focused)
                            .into(imageView);


                    bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Предоставлены права на чтение.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Права на чтение не были предоставлены.", Toast.LENGTH_LONG).show();
            }
        }


        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {

            }
        }
    }

    private void callCloudVision(final Bitmap bitmap,final Feature feature) {
        imageUploadProgress.setVisibility(View.VISIBLE);

        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();







        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));

        annotateImageRequests.add(annotateImageReq);


        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
               // visionAPIData.setText(visionAPIData.getText()+"***** "+result);
                imageUploadProgress.setVisibility(View.INVISIBLE);
                loda();
            }
        }.execute();
    }

    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);


        List<EntityAnnotation> entityAnnotations;

        String message = "";
        switch (api) {
            case "LANDMARK_DETECTION":
                entityAnnotations = imageResponses.getLandmarkAnnotations();
                 La_d.clear();

                List<EntityAnnotation> entityAnnotation=entityAnnotations;
                if (entityAnnotation != null) {
                    for (EntityAnnotation entity : entityAnnotation) {
                        La_d.add(entity.getDescription());

                        La_d.add(String.valueOf(entity.getLocations()));
                        La_d.add(String.valueOf(entity.getScore()));
                        message = message + "    " + entity.getDescription() + " "+ entity.getLocations();
                        message += "\n";

                    }
                } else {
                    La_d.add("");
                    message="Nothing Found";
                }

                break;
            case "LOGO_DETECTION":
                entityAnnotations = imageResponses.getLogoAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
            case "SAFE_SEARCH_DETECTION":
                SafeSearchAnnotation annotation = imageResponses.getSafeSearchAnnotation();
                message = getImageAnnotation(annotation);
               ssd.add( annotation.getAdult());
                       ssd.add(  String.valueOf(annotation.getMedical()));
                               ssd.add(String.valueOf( annotation.getSpoof()));
                               ssd.add( String.valueOf(annotation.getViolence()));

                break;
            case "IMAGE_PROPERTIES":
                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                message = getImageProperty(imageProperties);
                break;
            case "LABEL_DETECTION":
                entityAnnotations = imageResponses.getLabelAnnotations();
                message = formatAnnotation(entityAnnotations);

                List<EntityAnnotation> entityAnnotation1=entityAnnotations;
                if (entityAnnotation1 != null) {
                    for (EntityAnnotation entity : entityAnnotation1) {
                        ld.add(entity.getDescription());

                        ld.add(String.valueOf(entity.getScore()));


                    }
                } else {
                    ld.add("Nothing Found");
                }

                break;
            case "WEB_DETECTION":
                Object webDetection =imageResponses.get("webDetection");
                wb.clear();


               message="";
                if (webDetection != null) {
                    ArrayMap webDetectionAnnotations = (ArrayMap) webDetection;
///////////////////////////////////////////////////////////////////////////
                    Object webEntities = webDetectionAnnotations.get("visuallySimilarImages");

                    if (webEntities != null) {
                        ArrayList<ArrayMap> webEntitiesList = (ArrayList<ArrayMap>) webEntities;
                        message += "\n";


                        for (ArrayMap webEntity :
                                webEntitiesList) {

                            wb.add(  webEntity.get("url").toString());
                            message=message+ webEntity.get("url");
                        }


                    }
                }

                break;

        }

        return message;
    }

    private String getImageAnnotation(SafeSearchAnnotation annotation) {
        return String.format("adult: %s\nmedical: %s\nspoofed: %s\nviolence: %s\n",
                annotation.getAdult(),
                annotation.getMedical(),
                annotation.getSpoof(),
                annotation.getViolence());
    }

    private String getImageProperty(ImageProperties imageProperties) {
        String message = "";
        DominantColorsAnnotation colors = imageProperties.getDominantColors();
        for (ColorInfo color : colors.getColors()) {
            message = message + "" + color.getPixelFraction() + " - " + color.getColor().getRed() + " - " + color.getColor().getGreen() + " - " + color.getColor().getBlue();
            message = message + "\n";
        }
        return message;
    }

    private String formatAnnotation(List<EntityAnnotation> entityAnnotation) {
        String message = "";

        if (entityAnnotation != null) {
            for (EntityAnnotation entity : entityAnnotation) {
                entity.getDescription();
                message = message + "    " + entity.getDescription() + " "+ entity.getScore();
                message += "\n";
            }
        } else {
            message = "Nothing Found";
        }
        return message;
    }



    public void Clickc(View view) {

        view.startAnimation(animAlpha);
        visionAPIData.setText("");
        u.setVisibility(View.INVISIBLE);
        hj1=0;
        if (isNetworkAvailable()){
            u.setVisibility(View.INVISIBLE);
            loda();
        }else{
            u.setVisibility(View.VISIBLE);
            u.setText("НЕТУ СОЕДИНЕНИЯ С ИНТЕРНЕТОМ!!!");
        }


    }


    public void onBackPressed() {
        hj1=0;
        takePictureFromCamera();

    }

public void loda(){

    u.setVisibility(View.VISIBLE);
    takePicture.setVisibility(View.INVISIBLE);
    b2.setVisibility(View.INVISIBLE);
    b3.setVisibility(View.INVISIBLE);
/*
    if (hj1 == 0) {
        api = "LANDMARK_DETECTION";

        feature.setType(api);
        if (bitmap != null) {
            callCloudVision(bitmap, feature);
        }

        u.setText("11%");

    }
    if (hj1 == 1) {
        api = "LOGO_DETECTION";

        feature.setType(api);
        if (bitmap != null) {
            callCloudVision(bitmap, feature);

        }
        u.setText("74%");
    }

/*
    if (hj1 == 2) {
        api = "LABEL_DETECTION";

        feature.setType(api);
        if (bitmap != null) {
            callCloudVision(bitmap, feature);

        }
        u.setText("37%");

    }

/*
    if (hj1 == 300) {
        api = "SAFE_SEARCH_DETECTION";

        feature.setType(api);
        if (bitmap != null) {
            callCloudVision(bitmap, feature);

        }
        u.setText("46%");

    }
    if (hj1 == 400) {
        api = "IMAGE_PROPERTIES";

        feature.setType(api);
        if (bitmap != null) {
            callCloudVision(bitmap, feature);

        }
        u.setText("66%");

    }*/
    if (hj1 == 0) {
        api = "WEB_DETECTION";

        feature.setType(api);
        if (bitmap != null) {
            callCloudVision(bitmap, feature);

        }

    }
    //////////////////////////////////////////////////////////////////
  /*  if (hj1 == 4) {



        CatTask1 catTask = new CatTask1();
        catTask.execute();

    }
    if (hj1 == 5) {



        takePicture.setVisibility(View.VISIBLE);
        visionAPIData.setMovementMethod(new ScrollingMovementMethod());
        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);
        u.setText("");


        CatTask catTask1 = new CatTask();
        catTask1.execute();
        Intent Intent3 = new Intent(MainActivity.this, Main2Activity.class);
        Intent3.putStringArrayListExtra("La_d", La_d);
        Intent3.putStringArrayListExtra("wb1", wb);
        Intent3.putExtra("fname", detectedText);
        startActivity(Intent3);
    }*/
    if (hj1 == 1) {

        takePicture.setVisibility(View.VISIBLE);

        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);
        u.setText("");



        Intent Intent3 = new Intent(MainActivity.this, Main5Activity.class);
      //  Intent3.putStringArrayListExtra("La_d", La_d);
        Intent3.putStringArrayListExtra("wb1", wb);
        String decode=toBase64();
        Intent3.putExtra("s",decode);
        Log.d("decod",decode);
       // Intent3.putExtra("fname", detectedText);
        startActivity(Intent3);

    }
    hj1++;
    }

    public void clickcamera(View view) {
        view.startAnimation(animAlpha);
        takePictureFromCamera();

    }

    public void gallery(View view) {
        view.startAnimation(animAlpha);
        requestStoragePermission();
        showFileChooser();
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
    }




    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }



    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    /*
    class CatTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            detectedText = "";
            imageUploadProgress.setVisibility(View.VISIBLE);
            visionAPIData.setText(visionAPIData.getText()+"\n"+"второе РАСПОЗНАВАНИЕ");
        }

        @Override
        protected Void doInBackground(Void... params) {

            TextRecognizer textRecognizer = new TextRecognizer.Builder(con)
                    .build();
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> text = textRecognizer.detect(frame);

            if (!textRecognizer.isOperational()) {
                new AlertDialog.Builder(con)
                        .setMessage("Text recognizer could not be set up on your device :(").show();
                return null;
            }

            for (int i = 0; i < text.size(); i++) {
                TextBlock textBlock = text.valueAt(i);

                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText += textBlock.getValue();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            visionAPIData.setText(visionAPIData.getText()+"\n"+detectedText+"\n"+"\n");
            imageUploadProgress.setVisibility(View.INVISIBLE);
            u.setText("100%");

            takePicture.setVisibility(View.VISIBLE);

        }
    }
     */
     private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



//////////////////////////////////////////////////////////////////////////////

 /*   class CatTask1 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageUploadProgress.setVisibility(View.VISIBLE);
            visionAPIData.setText(visionAPIData.getText()+"\n"+"* РАСПОЗНАВАНИЕ первое");
        }

        @Override
        protected Void doInBackground(Void... params) {

            try{
                Log.e(TAG,"1");
                File dir = getExternalFilesDir(TESS_DATA);
                Log.e(TAG,"1");
                if(!dir.exists()){
                    Log.e(TAG,"2");
                    if (!dir.mkdir()) {
                        Log.e(TAG,"1");
                        Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                    }
                }
                String fileList[] = getAssets().list("");
                for(String fileName : fileList){
                    String pathToDataFile = dir + "/" + fileName;
                    if(!(new File(pathToDataFile)).exists()){
                        InputStream in = getAssets().open(fileName);
                        OutputStream out = new FileOutputStream(pathToDataFile);
                        byte [] buff = new byte[1024];
                        int len ;
                        while(( len = in.read(buff)) > 0){
                            out.write(buff,0,len);
                        }
                        in.close();
                        out.close();
                        Log.e(TAG,"4");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            try{

                String result = this.getText(bitmap);
                visionAPIData.setText("[[[[ "+result);
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        private String getText(Bitmap bitmap) {
            try{
                tessBaseAPI = new TessBaseAPI();
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
            String dataPath = getExternalFilesDir("/").getPath() + "/";
            tessBaseAPI.init(dataPath, "rus");
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890абвгдеёжзийклмнопрстуфхцчшщъыь" +
                    "эюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ");
//
//
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=[]}{" +
                    ";:|~`,.<>?");
            tessBaseAPI.setImage(bitmap);
            String retStr = "No result";
            try{
                retStr = tessBaseAPI.getUTF8Text();
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
            tessBaseAPI.end();
            return retStr;

        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            visionAPIData.setText(visionAPIData.getText()+"\n"+"\n"+detectedText);
            imageUploadProgress.setVisibility(View.INVISIBLE);
            u.setText("80%");
            loda();

        }
    }
*/public String toBase64() {
     // Получаем изображение из ImageView


     // Записываем изображение в поток байтов.
     // При этом изображение можно сжать и / или перекодировать в другой формат.
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
     bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

     // Получаем изображение из потока в виде байтов
     byte[] bytes = byteArrayOutputStream.toByteArray();

     // Кодируем байты в строку Base64 и возвращаем
     return Base64.encodeToString(bytes, Base64.DEFAULT);
 }

}

