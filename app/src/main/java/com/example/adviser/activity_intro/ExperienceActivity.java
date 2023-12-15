package com.example.adviser.activity_intro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.adviser.databinding.ActivityExperienceBinding;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ExperienceActivity extends AppCompatActivity {

    // OpenCV 초기화
    static {
        OpenCVLoader.initDebug();
    }

    ActivityExperienceBinding exBinding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Bitmap selectedImageBitmap;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exBinding = ActivityExperienceBinding.inflate(getLayoutInflater());
        setContentView(exBinding.getRoot());

        exBinding.cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCaAndGal();
            }
        });

        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("inception.tflite");
            FileInputStream inputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = assetFileDescriptor.getStartOffset();
            long declaredLength = assetFileDescriptor.getDeclaredLength();
            MappedByteBuffer modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);


            Interpreter.Options options = new Interpreter.Options();

            interpreter = new Interpreter(modelBuffer, options);



            // 모델 로드 후 필요한 초기화 작업 수행
            // 예: UI 구성 요소 초기화, 이벤트 핸들러 설정 등
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void openCaAndGal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("피부 상처 사진을 가져올 방식을 선택하세요.");

        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openGallery();
            }
        });

        builder.setNegativeButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openCamera();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                exBinding.photo.setImageBitmap(selectedImageBitmap);
                processDisplayAndPredict(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            try {
                Bundle extras = data.getExtras();
                selectedImageBitmap = (Bitmap) extras.get("data");
                exBinding.photo.setImageBitmap(selectedImageBitmap);
                processDisplayAndPredict(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processDisplayAndPredict(Bitmap img) {
        Mat src = new Mat();
        Utils.bitmapToMat(img, src);
        processImage(src);

        Mat img3Mat = new Mat();
        processRedFilter(src, img3Mat);

        predictImage(img);
    }

    private void processImage(Mat src) {
        List<Mat> channels = new ArrayList<>();
        Core.split(src, channels);

        for (int i = 0; i < channels.size(); i++) {
            Imgproc.equalizeHist(channels.get(i), channels.get(i));
        }

        Core.merge(channels, src);
    }

    private void processRedFilter(Mat image, Mat result) {
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed1 = new Scalar(10, 255, 255);
        Scalar lowerRed2 = new Scalar(160, 100, 100);
        Scalar upperRed2 = new Scalar(180, 255, 255);

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        List<Mat> hsvChannels = new ArrayList<>();
        Core.split(image, hsvChannels);

        Scalar lowerRedScalar2 = new Scalar(lowerRed2.val[0]);
        Scalar upperRedScalar2 = new Scalar(upperRed2.val[0]);

        Core.inRange(hsvChannels.get(1), lowerRedScalar2, upperRedScalar2, hsvChannels.get(1));
        Core.bitwise_not(hsvChannels.get(0), hsvChannels.get(0));

        hsvChannels.get(0).copyTo(result);
    }

    private void predictImage(Bitmap originalBitmap) {
        int inputSize = 224;  // 224*224
        Bitmap inputBitmap = Bitmap.createScaledBitmap(originalBitmap, inputSize, inputSize, true);

        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        float[][][][] inputData = processImageForModelInput(inputMat);
        float[] result = performInference(inputData);

        displayPredictionResult(result);
    }

    private float[][][][] processImageForModelInput(Mat image) {
        float[][][][] inputData = new float[1][224][224][3];

        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                int value = (int) image.get(i, j)[0];
                inputData[0][i][j][0] = value / 255.0f;
                inputData[0][i][j][1] = value / 255.0f;
                inputData[0][i][j][2] = value / 255.0f;
            }
        }
        return inputData;
    }

    private float[] performInference(float[][][][] inputData) {
        float[][] outputData = new float[1][6];
        interpreter.run(inputData, outputData);
        return outputData[0];
    }

    private void displayPredictionResult(float[] result) {
        float maxProbability = -1;
        int predictedClassIndex = -1;

        for (int i = 0; i < result.length; i++) {
            if (result[i] > maxProbability) {
                maxProbability = result[i];
                predictedClassIndex = i;
            }
        }

        int roundedProbability = Math.round(maxProbability * 100);

        String predictedClassLabel;
        switch (predictedClassIndex) {
            case 0:
                predictedClassLabel = "여드름";
                break;
            case 1:
                predictedClassLabel = "아토피";
                break;
            case 2:
                predictedClassLabel = "타박상";
                break;
            case 3:
                predictedClassLabel = "열상";
                break;
            case 4:
                predictedClassLabel = "찰과상";
                break;
            case 5:
                predictedClassLabel = "화상";
                break;
            default:
                predictedClassLabel = "알 수 없음";
                break;
        }

        ResultText(predictedClassLabel, roundedProbability);
    }

    private void ResultText(String resultText, int resultValue) {
        Intent resultIntent = new Intent(ExperienceActivity.this, ResultExperienceActivity.class);

        Log.d("result", resultText);

        if (selectedImageBitmap != null) {
            // 이미지를 파일로 저장
            File imageFile = saveImageToFile(selectedImageBitmap);

            // 파일 경로를 인텐트에 추가
            resultIntent.putExtra("resultText", resultText);
            resultIntent.putExtra("resultValue", resultValue);
            resultIntent.putExtra("resultImagePath", imageFile.getAbsolutePath());

            exBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(resultIntent);
                    finish();
                }
            });
        }
    }

    private File saveImageToFile(Bitmap bitmap) {
        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, "result_image.jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }
}
