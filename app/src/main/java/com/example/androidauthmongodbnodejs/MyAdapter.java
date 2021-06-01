package com.example.androidauthmongodbnodejs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder>{ //
    //MainActivity에 item class를 정의해 놓았음

    public static ArrayList<Main_loggedin.item> mDataset;
    public static Context mContext;

    public MyAdapter(Context context, ArrayList<Main_loggedin.item> mDataset) {
        this.mContext = context;
        this.mDataset = mDataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {  //뷰홀더 클래스 - 여기서 클릭 이벤트 작성


        // 사용될 항목들 선언   이름, 학번, 학과, 과목, 강의실
        public TextView mName;
        //public TextView mCode;
        public TextView mDepartment;
        public TextView mClass;
        public TextView mPlace;
        public TextView mprofessor;
        public TextView nowPlaying;
        public ImageView mPhoto; //전공,교양등 강의종류에 따라 적합한 이미지를 넣어주는게 나쁘지 않을듯

        public ViewHolder(View v) {
            super(v);
            mName = (TextView) v.findViewById(R.id.info_name);
            // mCode = (TextView) v.findViewById(R.id.info_code);
            mDepartment = (TextView) v.findViewById(R.id.info_department);
            mClass = (TextView) v.findViewById(R.id.info_class);
            mPlace = (TextView) v.findViewById(R.id.info_place);
            mprofessor= (TextView) v.findViewById(R.id.info_professor);
            nowPlaying= (TextView) v.findViewById(R.id.nowplaying);
//            mPhoto = (ImageView) v.findViewById(R.id.iv_photo); //넣을까 말까


            //클릭 이벤트 구간
            v.setClickable(true);
            v.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    int selected_number = getAdapterPosition(); //현재 선택된 항목의 위치값
                    if (selected_number != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(mContext, My_Class_Information.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("zzz", String.valueOf((mDataset.get(selected_number).lectureName))); //여기서 값을 새로운 페이지로 넘겨
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    // 생성자 - 넘어 오는 데이터타입에 유의
    //   public MyAdapter(ArrayList<MainActivity.item> myDataset)
    //  {
    // mDataset = myDataset;
    //  }
    //뷰홀더

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder(v);
        return holder;
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder (MyAdapter.ViewHolder holder,int position){
        holder.mName.setText(mDataset.get(position).getLectureName());
        // holder.mCode.setText(mDataset.get(position).getCode()+""); //int를 가져온다는점 유의    +""
        holder.mDepartment.setText(mDataset.get(position).getProfessorName() + " 교수님");
        holder.mClass.setText(mDataset.get(position).getLectureClass());
        holder.mPlace.setText(mDataset.get(position).getType());
        holder.mprofessor.setText(mDataset.get(position).getAttend());
        holder.nowPlaying.setText("강의가 진행중 입니다!");
        //holder.mPhoto.setImageBitmap(mDataset.get(position).getPhoto()); //첨부된 이미지를 연결해줘야 하는데

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }


}
