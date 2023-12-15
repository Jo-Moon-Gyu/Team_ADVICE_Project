package com.example.adviser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adviser.R;
import com.example.adviser.vo.PayVO;

import java.util.ArrayList;

// PayAdapter 클래스
public class PayAdapter extends RecyclerView.Adapter<PayAdapter.ViewHolder> {

    private Context context;
    private int layout;
    private ArrayList<PayVO> dataset;
    private PayClickListener payClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public PayAdapter(Context context, int layout, ArrayList<PayVO> dataset) {
        this.context = context;
        this.layout = layout;
        this.dataset = dataset;
    }

    // 클릭 리스너를 정의하는 인터페이스
    public interface PayClickListener {
        void onPayClicked(PayVO pay);
    }

    // 클릭 리스너를 설정하는 메서드
    public void setPayClickListener(PayClickListener listener) {
        this.payClickListener = listener;
    }

    // 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView payName;
        private TextView payPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 뷰홀더의 각 요소를 초기화하고 클릭 리스너를 설정
            initializeViews(itemView);
        }

        // 각 뷰 요소를 초기화하고 클릭 리스너를 설정하는 메서드
        private void initializeViews(View itemView) {
            img = itemView.findViewById(R.id.payImg);
            payName = itemView.findViewById(R.id.payId);
            payPrice = itemView.findViewById(R.id.price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleItemClick();
                }
            });
        }

        // 뷰홀더의 데이터를 설정하는 메서드
        public void bindData(PayVO item) {
            img.setImageAlpha(item.getPayImg());
            payName.setText(item.getPayName());
            payPrice.setText(item.getPayPrice());
        }

        // 아이템 클릭 처리를 하는 메서드
        private void handleItemClick() {
            if (payClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // 선택된 항목의 위치를 저장하고 리스트를 갱신하여 배경색을 변경
                    selectedPosition = position;
                    notifyDataSetChanged();
                    payClickListener.onPayClicked(dataset.get(position));
                }
            }
        }
    }

    @NonNull
    @Override
    public PayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_payment_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 뷰홀더에 데이터 바인딩
        holder.bindData(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
