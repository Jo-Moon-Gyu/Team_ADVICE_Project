package com.example.adviser.kakaomap;

import static com.example.adviser.activity_login_join.MedicalLoginActivity.PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.activity_usermain.MypageUserActivity;
import com.example.adviser.databinding.ActivityMapUserBinding;
import com.example.adviser.guide.AppGuideUserActivity;
import com.example.adviser.vo.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapUserActivity extends AppCompatActivity implements MapView.POIItemEventListener {

    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private int markerIndex = 1;
    private Set<Integer> markerIndexSet = new HashSet<>();
    private static final int KAKAO_MAP_REQUEST_CODE = 123;
    private boolean isKakaoMapClosed = false;
    private double latitude;
    private double longitude;
    private DrawerLayout drawerLayout;
    private ActivityMapUserBinding binding;
    private boolean isMapViewInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 상단 우측 메뉴버튼
        ImageButton menuBtn = findViewById(R.id.sideBar);
        drawerLayout = findViewById(R.id.drawerLayout);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                    binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            if (item.getItemId() == R.id.home) {
                                Intent intent = new Intent(MapUserActivity.this, MainUserActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.map) {

                            } else if (item.getItemId() == R.id.mypage) {
                                Intent intent = new Intent(MapUserActivity.this, MypageUserActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.guides) {
                                // 이용 안내 이동
                                Intent intent = new Intent(MapUserActivity.this, AppGuideUserActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.logout) {
                                // 로그아웃
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(MapUserActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }


                            return true;
                        }
                    });
                }
            }
        });


        User user = getUserInfoFromSession();

        binding.nick.setText(user.getUserNick()+"님");

        // 위치 권한 요청
        ActivityCompat.requestPermissions(MapUserActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Log.d("kkkk", "1");

        fetchCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 위치 권한 요청 결과 처리
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
                Log.d("kkkk", "2");
            } else {
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchCurrentLocation() {
        // 현재 위치 가져오기
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("kkkk", "3");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    callRestApi(latitude, longitude);
                    showKakaoMap();  // 이 부분이 한 번만 호출되도록 확인
                    Log.d("kkkk", "4");
                } else {
                    Log.e("fetchCurrentLocation", "Location is null");
                    Toast.makeText(MapUserActivity.this, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showKakaoMap() {
        // 카카오맵 표시
        if (!isMapViewInitialized) {
            if (mapView != null) {
                ViewGroup mapViewContainer = findViewById(R.id.mapView);
                mapViewContainer.removeView(mapView);
                mapView = new MapView(MapUserActivity.this);
                Log.d("kkkk", "5");
            } else {
                mapView = new MapView(MapUserActivity.this);
            }

            ViewGroup mapViewContainer = findViewById(R.id.mapView);
            mapViewContainer.addView(mapView);

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 4, true);
            mapView.zoomIn(true);
            mapView.zoomOut(true);

            // 현재 위치 마커 추가
            MapPOIItem marker = createMarker("현재 위치", MapPoint.mapPointWithGeoCoord(latitude, longitude), MapPOIItem.MarkerType.BluePin);
            mapView.addPOIItem(marker);
            isMapViewInitialized = true;

            Log.d("kkkk", "6");
        }
        setMapViewEventListener();
    }

    private MapPOIItem createMarker(String itemName, MapPoint mapPoint, MapPOIItem.MarkerType markerType) {
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(itemName);
        marker.setTag(markerIndex);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(markerType);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        return marker;
    }

    private void callRestApi(double latitude, double longitude) {
        // 중복 호출 방지를 위한 초기화 위치 수정
        markerIndexSet = new HashSet<>();

        if (isMapViewInitialized) {
            return;
        }

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CategoryResult> call = apiInterface.getSearchCategory(
                getString(R.string.restapi_key),
                "HP8",
                longitude,
                latitude,
                5000
        );

        call.enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                if (response.isSuccessful()) {
                    CategoryResult result = response.body();
                    if (result != null) {
                        Log.e("onResponse", "Success");

                        List<Document> documents = result.getDocuments();
                        if (documents != null && !documents.isEmpty()) {
                            for (Document document : documents) {
                                MapPOIItem hospitalMarker = createHospitalMarker(document);
                                mapView.addPOIItem(hospitalMarker);

                                markerIndexSet.add(markerIndex);
                                markerIndex++;

                                Log.e("KAKAO1", document.getPlaceName() + markerIndex);
                            }
                            Log.d("kkkk", "8");
                        } else {
                            Log.e("HospitalInfo", "No hospital information found.");
                        }
                    }
                } else {
                    Log.e("onResponse", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {
                Log.e("onFailure", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private MapPOIItem createHospitalMarker(Document document) {
        MapPOIItem hospitalMarker = new MapPOIItem();
        hospitalMarker.setItemName(document.getPlaceName());
        hospitalMarker.setTag(markerIndex);
        double x = Double.parseDouble(document.getY());
        double y = Double.parseDouble(document.getX());
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
        hospitalMarker.setMapPoint(mapPoint);
        hospitalMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        hospitalMarker.setCustomImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hospitallogo3));
        hospitalMarker.setCustomImageAutoscale(false);
        hospitalMarker.setCustomImageAnchor(0.5f, 1.0f);
        return hospitalMarker;
    }

    private void setMapViewEventListener() {
        mapView.setPOIItemEventListener(this);
    }

    // 이후는 클래스 내부에 implements로 구현한 메서드들이 옵니다.
    // onPOIItemSelected, onCalloutBalloonOfPOIItemTouched, onCalloutBalloonOfPOIItemTouched, onDraggablePOIItemMoved

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem poiItem) {
        Log.d("kkkk", "11");
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        // Do nothing
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem poiItem, MapPOIItem.CalloutBalloonButtonType buttonType) {
        // 풍선 클릭 시
        int markerId = (int) poiItem.getTag(); // 마커의 고유 식별자 가져오기
        Log.d("kkkk", "12");

        // 마커에 따른 동작 수행
        if (markerId > 0) {
            // 풍선 클릭에 대한 동작 수행
            searchPathInKakaoMap(poiItem.getItemName(), poiItem.getMapPoint().getMapPointGeoCoord().latitude, poiItem.getMapPoint().getMapPointGeoCoord().longitude);
            Log.e("KAKAO1", "CalloutBalloonOfPOIItemTouched: " + poiItem.getItemName());
        }
    }

    private void searchPathInKakaoMap(String placeName, double destinationLatitude, double destinationLongitude) {
        // 카카오맵에서 길찾기를 수행하는 메서드
        String url = "kakaomap://search?q=" + placeName + "&p=" + destinationLatitude + "," + destinationLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivityForResult(intent, KAKAO_MAP_REQUEST_CODE);
        Log.e("KAKAO1", "길찾기 시작" + placeName);
        Log.d("kkkk", "15");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("result", String.valueOf(resultCode));
        if (requestCode == KAKAO_MAP_REQUEST_CODE) {
            // 카카오맵 앱에서 돌아왔을 때의 처리
            if (resultCode == RESULT_OK) {
                Log.e("KAKAO1", "카카오맵앱에서 돌아옴");
                Log.d("kkkk", "9");
            } else if (resultCode == RESULT_CANCELED) {
                // 카카오맵 앱이 종료되었음을 플래그로 표시
                isKakaoMapClosed = true;
                Log.e("KAKAO1", "카카오맵 종료");
                Log.d("kkkk", "10");
            }
        }
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem poiItem, MapPoint mapPoint) {
        // 마커를 드래그하여 이동시켰을 때
        Log.d("kkkk", "14");
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때의 동작 정의
        // 예시로 MainActivity로 이동하도록 설정
        super.onBackPressed();
        Intent intent = new Intent(MapUserActivity.this, MainUserActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    // 세션값 가져오기 함수
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        Log.d("MainUserActivity", "User Info JSON: " + userInfoJson);
        return gson.fromJson(userInfoJson, User.class);
    }
}
