package com.example.forge.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.forge.LoginActivity;
import com.example.forge.R;
import com.example.forge.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private ProfileViewModel viewModel;
    private ActivityResultLauncher<String> galleryLauncher;
    private ImageView profilePicture;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        TextView tv_username = view.findViewById(R.id.profileUsername);
        TextView tv_email = view.findViewById(R.id.profileEmail);
        TextView tv_password = view.findViewById(R.id.profilePassword);
        TextView tv_userRole = view.findViewById(R.id.profile_ac);
        Button logoutButton = view.findViewById(R.id.buttonLogout);
        Button deleteAccountButton = view.findViewById(R.id.buttonDeleteAcc);
        Button editProfileButton = view.findViewById(R.id.buttonChangePassword);
        ImageButton addImage = view.findViewById(R.id.add_image);
        profilePicture = view.findViewById(R.id.imageViewAvatar);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        String displayName = firebaseUser.getDisplayName();

        tv_email.setText(auth.getCurrentUser().getEmail());
        tv_password.setText("******");
        tv_username.setText(displayName);

        SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "");
        tv_userRole.setText(userRole);

        viewModel.getProfileImageUrl().observe(getViewLifecycleOwner(), imageUrl -> {
            Glide.with(requireContext()).load(imageUrl).into(profilePicture);
        });

        viewModel.loadProfilePicture();

        addImage.setOnClickListener(v -> {
            ImagePickerDialogFragment dialogFragment = new ImagePickerDialogFragment();
            dialogFragment.show(getChildFragmentManager(), "ImagePickerDialogFragment");
        });

        logoutButton.setOnClickListener(view1 -> showConfirmationDialog(false));

        deleteAccountButton.setOnClickListener(view12 -> showConfirmationDialog(true));

        editProfileButton.setOnClickListener(view13 -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_edit);
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        viewModel.uploadImageToFirebaseStorage(result);
                    }
                });
    }

    private void showConfirmationDialog(boolean isDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView messageTextView = dialogView.findViewById(R.id.textViewMessage);
        Button cancelBtn = dialogView.findViewById(R.id.buttonCancel);
        Button confirmBtn = dialogView.findViewById(R.id.buttonConfirm);

        if (isDelete) {
            messageTextView.setText("Are you sure you want to delete your account?");
        } else {
            messageTextView.setText("Are you sure you want to log out from your account?");
        }

        AlertDialog dialog = builder.create();

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        confirmBtn.setOnClickListener(v -> {
            if (isDelete) {
                viewModel.deleteUserAccount();
            } else {
                viewModel.logoutUser();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
