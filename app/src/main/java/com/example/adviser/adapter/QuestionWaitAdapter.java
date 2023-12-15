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
import com.example.adviser.vo.QuestionVO;

import java.util.ArrayList;

public class QuestionWaitAdapter extends RecyclerView.Adapter<QuestionWaitAdapter.ViewHolder> {

    private Context context;
    private int layout;
    private ArrayList<QuestionVO> dataset;


    public QuestionWaitAdapter(Context context, int layout, ArrayList<QuestionVO> dataset) {
        this.context = context;
        this.layout = layout;
        this.dataset = dataset;
    }

    // ViewHolder 클래스 정의
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date;
        TextView question;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 각 뷰 요소를 초기화
            initializeViews(itemView);
        }

        // 각 뷰 요소를 초기화하는 메서드
        private void initializeViews(View itemView) {
            img = itemView.findViewById(R.id.questionImg);
            date = itemView.findViewById(R.id.dateText);
            question = itemView.findViewById(R.id.questionTitle);
        }

        // 뷰홀더의 데이터를 설정하는 메서드
        public void bindData(QuestionVO item) {
            // 이미지 설정
            setImage(item);
            // 날짜와 질문 내용 설정
            date.setText(item.getDateText());
            question.setText(item.getQuestionText());
        }

        // 이미지를 설정하는 메서드
        private void setImage(QuestionVO item) {
            // 이미지가 있는 경우
            if (item.getQuestionImg() != null) {
                img.setImageBitmap(item.getQuestionImg());
            } else {
                // 이미지가 없는 경우
                img.setImageResource(R.drawable.permissions_background); // 기본 이미지나 다른 처리
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_wait_view, parent, false);
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
