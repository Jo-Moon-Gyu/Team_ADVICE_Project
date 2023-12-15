package com.example.adviser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adviser.R;
import com.example.adviser.vo.TokenVO;

import java.util.ArrayList;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {

    private Context context;
    private int layout;
    private ArrayList<TokenVO> dataset;
    private TokenClickListener tokenClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public TokenAdapter(Context context, int layout, ArrayList<TokenVO> dataset) {
        this.context = context;
        this.layout = layout;
        this.dataset = dataset;
    }

    // TokenClickListener 인터페이스 정의
    public interface TokenClickListener {
        void onTokenClicked(TokenVO token);
    }

    public void setTokenClickListener(TokenClickListener listener) {
        this.tokenClickListener = listener;
    }

    // ViewHolder 클래스 정의
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tokenName;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 각 뷰 요소를 초기화
            initializeViews(itemView);
        }

        // 각 뷰 요소를 초기화하는 메서드
        private void initializeViews(View itemView) {
            tokenName = itemView.findViewById(R.id.tokenId);
            img = itemView.findViewById(R.id.tokenImg);

            // 아이템 뷰에 클릭 리스너 설정
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tokenClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            selectedPosition = position;
                            notifyDataSetChanged();
                            tokenClickListener.onTokenClicked(dataset.get(position));
                        }
                    }
                }
            });
        }

        // 뷰홀더의 데이터를 설정하는 메서드
        public void bindData(TokenVO item) {
            // 이미지와 토큰 이름 설정
            img.setImageAlpha(item.getImg());
            tokenName.setText(item.getTokenName());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_token_view, parent, false);
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
