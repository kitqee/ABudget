package com.budget.abudget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class PhotoActivity extends Activity {

    private static final String TAG = "PhotoActivity";

    File directory;
    String _path;
    String imageFileName;
    String ABUDGET_FOLDER = "aBudgetFolder";
    private ImageView mImageView;
    private static int TAKE_PICTURE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mImageView = (ImageView) findViewById(R.id.picture);
    }

    public void onClick(View view) {
        verifyStoragePermissions(PhotoActivity.this);
        createDirectory();
        getThumbnailPicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            // Получаем фото и вставляем в imageView
            Bitmap mBitmap = BitmapFactory.decodeFile(_path);
            mImageView.setImageBitmap(mBitmap);
            toastMessage("Фото " + imageFileName + " сохранено!");
            galleryAddPic();
        }
    }

    private void getThumbnailPicture() {
        // ** Вызов камеры **
        // Генерация уникального имени
        @SuppressLint("SimpleDateFormat") String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "abPhoto" + timeStamp + ".jpg";
        _path = directory.getPath() + File.separator + imageFileName;
        Log.d(TAG, "Получено фото: " + _path);
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);
        // Вызов стандарной камеры
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void galleryAddPic() {
        // ** В галерею! **
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(_path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // ** Запрос прав на запись и чтение с SD карты **
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void createDirectory() {
        // ** Создание директории, если такой не существет **
        directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), ABUDGET_FOLDER);
        Log.d(TAG, "Директория: " + directory.getPath());
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

}
