/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.phenotype.Configuration;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.samples.vision.face.facetracker.ui.camera.CameraSourcePreview;
import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.path;
import static android.R.attr.rotation;
import static android.os.Environment.getExternalStorageDirectory;
import static java.io.File.separator;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    Button switch_camera;
    Button take_camera;
    Button btn_gallery;
    Button btn_effect;
    private Context mContext;
    private int current_camera = 0;
    private static final int PICK_IMAGE = 0;
    private Uri mCapturedImageURI;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        switch_camera = (Button) findViewById(R.id.switch_camera);
        take_camera = (Button) findViewById(R.id.take_camera);
        btn_gallery = (Button) findViewById(R.id.gallery);
        btn_effect = (Button) findViewById(R.id.effect);


        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_camera = mCameraSource.getCameraFacing();
                try {
                    mPreview.release();
                    if (current_camera == 0) {
                        current_camera = 1;
                        mPreview.stop();
                        createCameraSource();
                        startCameraSource();
                    } else {
                        current_camera = 0;
                        mPreview.stop();
                        createCameraSource();
                        startCameraSource();
                    }
                } catch (RuntimeException e) {

                    Toast toast = Toast.makeText(FaceTrackerActivity.this, "Device do not support front camera", Toast.LENGTH_SHORT);
                    toast.show();
                    current_camera = 0;
                    mPreview.stop();
                    createCameraSource();
                    startCameraSource();
                    //  continue;
                }

            }
        });


//        take_camera.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//
//                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
//                    private File imageFile;
//
//                    @Override
//                    public void onPictureTaken(byte[] bytes) {
//                        try {
//                            // convert byte array into bitmap
//
//                            Bitmap loadedImage = null;
//
//                            loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
//                                    bytes.length);
//
//                            Bitmap bitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight());
//                            // quay hinh anh khi luu
//                            if(current_camera == 0) {
//                                bitmap = rotateImage(bitmap, 90);
//                            }
//                            else if(current_camera==1){
//                                bitmap = rotateImage(bitmap, -90);
//                            }
//                            File dir = new File(
//                                    Environment.getExternalStoragePublicDirectory(
//                                            Environment.DIRECTORY_PICTURES), "MyPhotos");
//
//                            boolean success = true;
//                            if (!dir.exists()) {
//                                try {
//                                    success = dir.mkdirs();
//                                } catch (Exception e) {
//                                    Toast.makeText(getBaseContext(), e.getMessage() + " " + e.getCause(),
//                                            Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                            if (success) {
//                                imageFile = new File(dir.getAbsolutePath()
//                                        + File.separator
//                                        + getPhotoTime()
//                                        + "Image.jpg");
//
//                                imageFile.createNewFile();
//                            } else {
//                                Toast.makeText(getBaseContext(), "Image Not saved",
//                                        Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//
//                            // save image into gallery
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
//
//                            FileOutputStream fout = new FileOutputStream(imageFile);
//                            fout.write(ostream.toByteArray());
//                            fout.close();
//                            ContentValues values = new ContentValues();
//
//                            values.put(MediaStore.Images.Media.DATE_TAKEN,
//                                    System.currentTimeMillis());
//                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                            values.put(MediaStore.MediaColumns.DATA,
//                                    imageFile.getAbsolutePath());
//
//                            FaceTrackerActivity.this.getContentResolver().insert(
//                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//
//  //                              loadedImage = overlay(bitmap,loadBitmapFromView(mGraphicOverlay));
//   //                             loadedImage =RotateBitmap(loadedImage,90);
//   //                         saveToInternalStorage(loadedImage);
//                            Intent takeViewPic = new Intent(FaceTrackerActivity.this, ViewPicture.class);
//                            takeViewPic.putExtra("image thumbnail path", imageFile);
//                            startActivity(takeViewPic);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    private String getPhotoTime(){
//                        SimpleDateFormat sdf=new SimpleDateFormat("ddMMyy_hhmmss");
//                        return sdf.format(new Date());
//                    }
//                    private Bitmap rotateImage(Bitmap source, float angle) {
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(angle);
//                        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),   source.getHeight(), matrix,
//                                true);
//                    }
//                });
//            }
//        });

        take_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Log.d(TAG, "onPictureTaken - jpeg");
                        capturePic(bytes);
                    }

                    private void capturePic(byte[] bytes) {
                        try {
                            // String mainpath = getExternalStorageDirectory() + separator + "MaskIt" + separator + "images" + separator;
                            String mainpath = getExternalStorageDirectory() + separator + "DCIM" + separator + "MyCamera" + separator;
                            File basePath = new File(mainpath);
                            if (!basePath.exists())
                                Log.d("CAPTURE_BASE_PATH", basePath.mkdirs() ? "Success": "Failed");
                            File captureFile = new File(mainpath + "photo_" + getPhotoTime() + ".jpg");
                            if (!captureFile.exists())
                                Log.d("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success": "Failed");
                            FileOutputStream stream = new FileOutputStream(captureFile);
                            stream.write(bytes);
                            stream.flush();
                            stream.close();
                            String picturePath = mainpath + "photo_" + getPhotoTime() + ".jpg";
                            //new add
//                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
//                            Uri contentUri = Uri.fromFile(captureFile);
//                            mediaScanIntent.setData(contentUri);
//                            sendBroadcast(mediaScanIntent);
                            //onPause();
                            Intent takeViewPic = new Intent(FaceTrackerActivity.this, ViewPicture.class);
                            takeViewPic.putExtra("image thumbnail path", picturePath);
                            startActivity(takeViewPic);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    private String getPhotoTime(){
                        SimpleDateFormat sdf=new SimpleDateFormat("ddMMyy_hhmmss");
                        return sdf.format(new Date());
                    }

                });

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "content://media/internal/images/media"));
                startActivity(intent);
            }

        });


}

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {

            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                .setFacing(current_camera)//CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated wireleaseInstance();th the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}
