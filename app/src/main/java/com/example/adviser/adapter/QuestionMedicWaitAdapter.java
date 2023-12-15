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
import com.example.adviser.vo.QuestionMedicVO;

import java.util.ArrayList;

public class QuestionMedicWaitAdapter extends RecyclerView.Adapter<QuestionMedicWaitAdapter.ViewHolder> {

    private Context context;
    private int layout;
    private ArrayList<QuestionMedicVO> dataset;
    private OnQuestionClickListener clickListener;

    public QuestionMedicWaitAdapter(Context context, int layout, ArrayList<QuestionMedicVO> dataset, OnQuestionClickListener clickListener) {
        this.context = context;
        this.layout = layout;
        this.dataset = dataset;
        this.clickListener = clickListener;
    }

    // ViewHolder 클래스 정의
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView date;
        TextView question;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 각 뷰 요소를 초기화하고 클릭 리스너를 설정
            initializeViews(itemView);
        }

        // 각 뷰 요소를 초기화하고 클릭 리스너를 설정하는 메서드
        private void initializeViews(View itemView) {
            img = itemView.findViewById(R.id.questionImg);
            date = itemView.findViewById(R.id.dateText);
            question = itemView.findViewById(R.id.questionTitle);

            // 아이템 뷰에 클릭 리스너 설정
            itemView.setOnClickListener(this);
        }

        // 뷰홀더의 데이터를 설정하는 메서드
        public void bindData(QuestionMedicVO item) {
            img.setImageBitmap(item.getQuestionImg());
            date.setText(item.getCreated_at());
            question.setText(item.getReq_title());
        }

        // 클릭 이벤트 처리
        @Override
        public void onClick(View view) {
            // 어댑터 내의 클릭 리스너를 호출하여 클릭 이벤트 처리
            if (clickListener != null) {
                clickListener.onQuestionClick(getAdapterPosition());
            }
        }
    }

    // 인터페이스 정의
    public interface OnQuestionClickListener {
        void onQuestionClick(int position);
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

    // 특정 위치의 아이템을 가져오는 메서드
    public QuestionMedicVO getItem(int position) {
        return dataset.get(position);
    }
}
