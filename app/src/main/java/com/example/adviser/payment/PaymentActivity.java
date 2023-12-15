package com.example.adviser.payment;

import static com.example.adviser.activity_login_join.MedicalLoginActivity.PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.activity_medicmain.ExchangeMedicActivity;
import com.example.adviser.activity_medicmain.MainMedicActivity;
import com.example.adviser.activity_medicmain.MypageMedicActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.guide.AppGuideActivity;
import com.example.adviser.kakaomap.MapUserActivity;
import com.example.adviser.activity_usermain.MypageUserActivity;
import com.example.adviser.adapter.PayAdapter;
import com.example.adviser.adapter.TokenAdapter;
import com.example.adviser.databinding.ActivityPaymentBinding;
import com.example.adviser.vo.PayVO;
import com.example.adviser.vo.Tbl_Deep;
import com.example.adviser.vo.Tbl_Payment;
import com.example.adviser.vo.Tbl_Request;
import com.example.adviser.vo.TokenVO;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements TokenAdapter.TokenClickListener, PayAdapter.PayClickListener {

    // OpenCV 초기화
    static {
        OpenCVLoader.initDebug();
    }
    private boolean isTokenSelected = false;
    private boolean isPaySelected = false;
    TokenAdapter adapter;
    PayAdapter adapter2;
    private DrawerLayout drawerLayout;
    RequestQueue requestQueue;
    private Bitmap resultImageBitmap;
    private Interpreter interpreter;
    ActivityPaymentBinding binding;
    private final String ipUrl = "http://13.209.4.93:8092/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 상단 우측 메뉴버튼
        ImageButton menuBtn = findViewById(R.id.sideBar);
        drawerLayout = findViewById(R.id.drawerLayout);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }else {
                    drawerLayout.openDrawer(GravityCompat.END);
                    binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            if (item.getItemId() == R.id.home) {
                                // 현재페이지 작동 암함
                            } else if (item.getItemId() == R.id.mypage) {
                                // 메딕 마이페이지 이동
                                Intent intent = new Intent(PaymentActivity.this, MypageMedicActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.exchange) {
                                // 메딕 환전요청 이동
                                Intent intent = new Intent(PaymentActivity.this, ExchangeMedicActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.guide) {
                                // 이용 안내 이동
                                Intent intent = new Intent(PaymentActivity.this, AppGuideActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.logout) {
                                // 로그아웃
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            return true;
                        }
                    });
                }
            }
        });

        // 딥러닝 모델 초기화
        initializeDeepLearningModel();

        // 사용자 업로드 이미지 가져오기
        handleUserUploadedImage();


        requestQueue = Volley.newRequestQueue(this);

        // 세션에서 사용자 정보 가져오기
        User user = getUserInfoFromSession();

        // 유저 정보를 레이아웃에 표시한다.
        binding.nick.setText(user.getUserNick() + "님");

        // 토근 값이 있다면 토큰 출력 없다면 text 출력
        ArrayList<TokenVO> dataset = new ArrayList<>();
        ArrayList<PayVO> dataset2 = new ArrayList<>();
        String token = user.getUserToken();
        // "사용 가능한 토큰이 없습니다" 메시지를 출력할 TextView
        TextView messageTextView = findViewById(R.id.messageTextView);
        if (token == null || token.equals("0")) {
            // 사용 가능한 토큰이 없습니다 메시지를 TextView에 설정
            messageTextView.setText("사용 가능한 토큰이 없습니다");
            // 토큰이 없는 경우 어댑터를 null로 설정하거나 다른 작업을 수행하고 싶다면 이 부분에 추가 코드를 작성할 수 있습니다.
            adapter = null;
        } else {
            // 토큰이 있다면 어댑터 설정
            int img = R.drawable.baseline_favorite_24;
            String tokenName = "무료 질문 토큰";
            dataset.add(new TokenVO(tokenName, img));
            adapter = new TokenAdapter(getApplicationContext(), R.layout.user_token_view, dataset);
            adapter.notifyDataSetChanged();
            adapter.setTokenClickListener(new TokenAdapter.TokenClickListener() {
                @Override
                public void onTokenClicked(TokenVO token) {
                    // 선택된 토큰에 대한 동작 수행
                    // 예: isTokenSelected 변수 업데이트 등
                    isTokenSelected = true;
                    isPaySelected = false;

                    binding.payArr.setBackgroundResource(R.drawable.permissions_background);
                    binding.tokenArr.setBackgroundResource(R.drawable.select_background);
                }
            });
            binding.token.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
            binding.token.setAdapter(adapter);
        }

        // 결제 요금 출력
        adapter2 = new PayAdapter(getApplicationContext(), R.layout.user_payment_view, dataset2);
        dataset2.add(new PayVO("결제권", "500원", R.drawable.baseline_attach_money_24));
        adapter2.setPayClickListener(new PayAdapter.PayClickListener() {
            @Override
            public void onPayClicked(PayVO payVO) {
                // 선택된 토큰에 대한 동작 수행
                // 예: isTokenSelected 변수 업데이트 등
                isTokenSelected = false;
                isPaySelected = true;

                binding.payArr.setBackgroundResource(R.drawable.select_background);
                binding.tokenArr.setBackgroundResource(R.drawable.permissions_background);
            }
        });
        binding.pay.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
        binding.pay.setAdapter(adapter2);

        // 종료하기
        binding.closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeQuestionMes();
            }
        });

        // 결제하기 버튼 클릭 시 처리
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isTokenSelected) {
                    // 선택한 리스트에 따른 토큰 결제 로직 수행
                    performTokenPayment();
                } else if (isPaySelected) {
                    // 선택한 리스트에 따른 페이 결제 로직 수행
                    performPayPayment();
                } else {
                    // 토큰도 페이도 선택되지 않은 경우
                    // 기본적인 결제 로직 수행 또는 에러 처리 등을 수행할 수 있습니다.
                    Toast.makeText(PaymentActivity.this, "토큰 또는 페이를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 기능 함수 ----------------------------------

    // 딥러닝 모델 초기화
    private void initializeDeepLearningModel() {
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

    // 사용자 업로드 이미지 처리
    private void handleUserUploadedImage() {
        // 이미지 가져오기
        byte[] resultImageBytes = getIntent().getByteArrayExtra("insertImg");

        if (resultImageBytes != null) {
            resultImageBitmap = BitmapFactory.decodeByteArray(resultImageBytes, 0, resultImageBytes.length);
        } else {
            Log.d("이미지 있냐?", "없어");
            resultImageBitmap = null;
        }
    }



    // 결제 로직 수행
    private void performTokenPayment() {
        // 선택한 리스트에 따른 결제 로직 수행
        showToken();
    }

    private void performPayPayment() {
        showWarningDialog();
    }

    // 결제 버튼 활성화 여부를 업데이트하는 메서드
    private void updateSaveButtonState() {
        binding.saveBtn.setEnabled(isTokenSelected || isPaySelected);
    }

    // TokenClickListener에서 선택 이벤트 처리
    @Override
    public void onTokenClicked(TokenVO token) {
        // Token이 선택되었음을 표시
        isTokenSelected = true;
        // Pay가 선택되지 않았음을 표시
        isPaySelected = false;
        // 결제 버튼 활성화 여부 업데이트
        updateSaveButtonState();
    }

    // PayClickListener에서 선택 이벤트 처리
    @Override
    public void onPayClicked(PayVO pay) {
        // Pay가 선택되었음을 표시
        isPaySelected = true;
        // Token이 선택되지 않았음을 표시
        isTokenSelected = false;
        // 결제 버튼 활성화 여부 업데이트
        updateSaveButtonState();
    }

    // 서버에 사용자 토큰 마이너스 하기
    private void minusToken() throws JSONException {
        User user = getUserInfoFromSession();
        String userEmail = user.getUserEmail();
        Log.d("마이너스 토큰?", userEmail);
        String url = ipUrl + "minusToken?user_email=" + userEmail;
        Tbl_Payment tblPayment = new Tbl_Payment();
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(tblPayment);
        // JSON 데이터를 서버로 전송
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(jsonRequest),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Volley success", "성공: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 에러 처리 로직 추가
                        Log.e("Volley Error", "Error: " + error.toString());
                    }
                });

        requestQueue.add(request);
    }

    // 서버에 결제 정보 넘기기
    private void payData() throws JSONException {
        User user = getUserInfoFromSession();
        String userEmail = user.getUserEmail();
        Log.d("이메일인데화면이왜어두워져?", userEmail);
        String url = ipUrl + "payment";
        Tbl_Payment tblPayment = new Tbl_Payment();
        tblPayment.setUserEmail(userEmail);
        tblPayment.setPaidAmount("500");
        tblPayment.setShareAmount("200");
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(tblPayment);
        // JSON 데이터를 서버로 전송
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonRequest),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Volley success", "성공: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 에러 처리 로직 추가
                        Log.e("Volley Error", "Error: " + error.toString());
                    }
                });

        requestQueue.add(request);
    }

    // 뒤로가기 버튼이 눌렸을 때 실행될 코드
    @Override
    public void onBackPressed() {
        // 원하는 동작 수행
        closeQuestionMes();
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("결제 하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // "예"를 클릭하면 질문 등록 등의 로직을 수행할 수 있습니다.
                try {
                    if (resultImageBitmap != null) {
                        uploadDataToServer();
                        payData();
                    } else {
                        payData();
                    }
                    payClear();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // "아니오"를 클릭하면 아무 동작도 수행하지 않습니다.
                // 여기에 원하는 동작을 추가하세요.
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showToken() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("토큰을 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // "예"를 클릭하면 질문 등록 등의 로직을 수행할 수 있습니다.
                try {
                    if (resultImageBitmap != null) {
                        uploadDataToServer();
                        minusToken();
                    } else {
                        minusToken();
                    }
                    payClear();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // "아니오"를 클릭하면 아무 동작도 수행하지 않습니다.
                // 여기에 원하는 동작을 추가하세요.
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void closeQuestionMes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("결제를 취소 하시겠습니까?\n취소하시면 메인화면으로 돌아갑니다.");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // "예"를 클릭하면 질문 등록 등의 로직을 수행할 수 있습니다.
                // 여기에 원하는 동작을 추가하세요.
                try {
                    deleteData();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Intent intent = new Intent(PaymentActivity.this, MainUserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // "아니오"를 클릭하면 아무 동작도 수행하지 않습니다.
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void payClear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("결제가 완료되었습니다.");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // "예"를 클릭하면 질문 등록 등의 로직을 수행할 수 있습니다.
                // 여기에 원하는 동작을 추가하세요.

                Intent intent = new Intent(PaymentActivity.this, MainUserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 서버에 데이터 삭제요청
    private void deleteData() throws JSONException {
        User user = getUserInfoFromSession();
        String userEmail = user.getUserEmail();
        String url = ipUrl + "deleteWrite";
        Tbl_Request tblRequest = new Tbl_Request();
        tblRequest.setUserEmail(userEmail);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(tblRequest);
        // JSON 데이터를 서버로 전송
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonRequest),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Volley success", "성공: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 에러 처리 로직 추가
                        Log.e("Volley Error", "Error: " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userEmail", userEmail); // 수정된 부분
                return params;
            }
        };
        requestQueue.add(request);
    }

    // 사용자 세션 정보
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        return gson.fromJson(userInfoJson, User.class);
    }

    // 서버에 딥러닝 데이터 업로드
    private void uploadDataToServer() throws JSONException {
        User user = getUserInfoFromSession();
        String userEmail = user.getUserEmail();
        String deepResult = Deepresult();

        // 이미지를 Base64로 변환
        String deepImg = encodeImage(resultImageBitmap, 20);

        // 이미지 파일 이름 생성 (현재 시간 활용)
        String imageFileName = generateImageFileName(userEmail);

        String url = ipUrl + "uploadDeep?imageFileName=" + imageFileName;

        // Tbl_Request 객체 생성 및 값 설정
        Tbl_Deep tblDeep = new Tbl_Deep();
        tblDeep.setUserEmail(userEmail);
        tblDeep.setDeepResult(deepResult);
        tblDeep.setDeepImg(deepImg);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(tblDeep);

        // JSON 데이터를 서버로 전송
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonRequest),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 에러 처리 로직 추가
                        Log.e("Volley Error", "Error: " + error.toString());
                    }
                });

        requestQueue.add(request);
    }

    private String Deepresult() {
        String result = processDisplayAndPredict(resultImageBitmap);
        return result;
    }


    // 이미지를 처리하고 결과를 표시하고 모델을 사용하여 예측하는 메서드
    private String processDisplayAndPredict(Bitmap img) {
        // Bitmap을 Mat으로 변환
        Mat src = new Mat();
        Utils.bitmapToMat(img, src);

        // 이미지 처리
        processImage(src);

        String result = predictImage(img);
        return result;
    }

    // 이미지 처리 메서드
    private void processImage(Mat src) {
        // Split RGB channels (RGB 채널 분리)
        List<Mat> channels = new ArrayList<>();
        Core.split(src, channels);

        // Apply histogram equalization to each RGB channel (각 RGB 채널에 히스토그램 평활화 적용)
        for (int i = 0; i < channels.size(); i++) {
            Imgproc.equalizeHist(channels.get(i), channels.get(i));
        }

        // Merge the channels back into the RGB image (채널을 다시 합치기)
        Core.merge(channels, src);
    }


    // 이미지를 모델에 입력하고 예측 결과를 TextView에 표시하는 메서드
    private String predictImage(Bitmap originalBitmap) {
        // TensorFlow Lite 모델에 입력할 이미지를 준비 (28x28 크기로 조정)
        Bitmap inputBitmap = Bitmap.createScaledBitmap(originalBitmap, 224, 224, true);

        // Bitmap을 Mat으로 다시 변환
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 이미지를 모델에 입력할 형식으로 전처리
        float[][][][] inputData = processImageForModelInput(inputMat);

        // 모델 예측
        float[] result = performInference(inputData);

        // 결과를 TextView에 표시
        String deepResult = displayPredictionResult(result);

        return deepResult;
    }

    // 이미지 전처리 메서드
    // 이미지를 모델에 입력할 형식으로 전처리
    private float[][][][] processImageForModelInput(Mat image) {
        float[][][][] inputData = new float[1][224][224][3]; // 입력 채널 수를 3으로 수정

        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                // 예시로 모든 채널에 같은 값을 사용
                int value = (int) image.get(i, j)[0];
                inputData[0][i][j][0] = value / 255.0f; // 첫 번째 채널
                inputData[0][i][j][1] = value / 255.0f; // 두 번째 채널
                inputData[0][i][j][2] = value / 255.0f; // 세 번째 채널
            }
        }
        return inputData;
    }

    // TensorFlow Lite 모델에 이미지를 입력하고 결과를 반환하는 메서드
    private float[] performInference(float[][][][] inputData) {
        // 모델 예측 실행
        float[][] outputData = new float[1][6]; // 클래스 개수에 맞게 수정
        interpreter.run(inputData, outputData);

        // 결과를 반환
        return outputData[0];
    }

    // 모델 예측 결과를 표시하는 메서드
    private String displayPredictionResult(float[] result) {
        // 배열에서 가장 큰 값과 그에 해당하는 인덱스 찾기
        float maxProbability = -1;
        int predictedClassIndex = -1;

        for (int i = 0; i < result.length; i++) {
            if (result[i] > maxProbability) {
                maxProbability = result[i];
                predictedClassIndex = i;
            }
        }

        // 예상 확률을 반올림하여 정수로 변환
        int roundedProbability = Math.round(maxProbability * 100);

        // 예측 결과에 따라 문자열 지정
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

        // 결과를 TextView에 표시
        // exBinding.predictionText.setText("예상 피부 병변: " + predictedClassLabel + ", 예상 확률: " + roundedProbability + "%");

        return predictedClassLabel;
    }

    // 이미지 인코딩
    private String encodeImage(Bitmap bitmap, int quality) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        } else {
            return "";
        }
    }

    // 이미지 파일 이름 생성 (현재 시간 사용)
    private String generateImageFileName(String userEmail) {
        // 현재 시간을 이용하여 고유한 파일명 생성
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return "Deep_" + userEmail + "_" + timeStamp + ".jpg";
    }

}