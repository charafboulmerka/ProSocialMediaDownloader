package com.f08.prosaver.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f08.prosaver.R;
import com.f08.prosaver.activity.FullViewActivity;
import com.f08.prosaver.adapter.FileListAdapter;
import com.f08.prosaver.databinding.FragmentHistoryBinding;
import com.f08.prosaver.interfaces.FileListClickInterface;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static androidx.databinding.DataBindingUtil.inflate;

public class AllinOneGalleryFragment extends Fragment implements FileListClickInterface {
    private FragmentHistoryBinding binding;
    FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;
    private Activity activity;
    File mediaPath;
    RecyclerView.LayoutManager mLayoutManager;
    public AllinOneGalleryFragment(File filesOfMedia){
        mediaPath=filesOfMedia;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = getActivity();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("m");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        activity = getActivity();
        getAllFiles();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflate(inflater, R.layout.fragment_history, container, false);
        initViews();
        return binding.getRoot();
    }
    private void initViews(){
        binding.swiperefresh.setOnRefreshListener(() -> {
            getAllFiles();
            binding.swiperefresh.setRefreshing(false);
        });
    }

    private void getAllFiles(){
        fileArrayList = new ArrayList<>();
            mLayoutManager = new GridLayoutManager(activity,3);
        binding.rvFileList.setLayoutManager(mLayoutManager);

        File[] files = mediaPath.listFiles();
        if (files!=null) {
            fileArrayList.addAll(Arrays.asList(files));
            fileListAdapter = new FileListAdapter(activity, fileArrayList, AllinOneGalleryFragment.this);
            binding.rvFileList.setAdapter(fileListAdapter);
            if (files.length>0){
                binding.tvNoResult.setVisibility(View.GONE);
                binding.swiperefresh.setVisibility(View.VISIBLE);
            }else {
                binding.swiperefresh.setVisibility(View.GONE);
                binding.tvNoResult.setVisibility(View.VISIBLE);
            }
        }else {
            binding.swiperefresh.setVisibility(View.GONE);
            binding.tvNoResult.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(activity, FullViewActivity.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        activity.startActivity(inNext);
    }
}
