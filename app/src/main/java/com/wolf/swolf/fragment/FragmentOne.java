package com.wolf.swolf.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolf.swolf.R;

/**
 * <p>Description: </p>
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-07 15:26
 */
public class FragmentOne extends Fragment{

    private RecyclerView mRecyclerView;
    public String[] datas = new String[]{"1","1","1","1","1","1","1","1","1","1"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new FAdapter());

        //设置

        return view;
    }

    public class FAdapter extends RecyclerView.Adapter<FViewHolder> {

        @Override
        public FViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

            return new FViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FViewHolder holder, int position) {
            holder.textView.setText(datas[position]);
        }

        @Override
        public int getItemCount() {
            return datas.length;
        }
    }

    public class FViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;

        public FViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tdddv);
        }
    }

}
