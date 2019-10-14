package com.bt_121shoppe.motorbike.loan.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bt_121shoppe.motorbike.R;


public class two extends Fragment {
    private static final String ARG_NUMBER = "arg_number";

    private Toolbar mToolbar;
    private TextView mTvName;
    private Button mBtnNext, mBtnNextWithFinish;

    private int mNumber;

    public static two newInstance(int number) {
        two fragment = new two();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mNumber = args.getInt(ARG_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create__load_two, container, false);
//        initView(view);
        return view;
    }


//    private void initView(View view) {
//        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        mTvName = (TextView) view.findViewById(R.id.tv_name);
//        mBtnNext = (Button) view.findViewById(R.id.btn_next);
//        mBtnNextWithFinish = (Button) view.findViewById(R.id.btn_next_with_finish);
//
//        String title = "CyclerFragment " + mNumber;
//
//        mToolbar.setTitle(title);
//        initToolbarNav(mToolbar);
//
//        mTvName.setText(title);
//        mBtnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                start(CycleFragment.newInstance(mNumber + 1));
//            }
//        });
//        mBtnNextWithFinish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startWithPop(CycleFragment.newInstance(mNumber + 1));
//            }
//        });
//    }
}
