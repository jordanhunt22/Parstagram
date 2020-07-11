package com.example.parstagram.Fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserFragment extends ProfileFragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogOut.setVisibility(View.GONE);
        btnProfile.setVisibility(View.GONE);
    }
}

