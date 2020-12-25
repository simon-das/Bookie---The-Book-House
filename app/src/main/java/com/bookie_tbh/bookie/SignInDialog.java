package com.bookie_tbh.bookie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SignInDialog extends AppCompatDialogFragment {


//  dialog variables
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private View signInDialogView;

//  variables of sign in view
    private Button signInOutButton;
    private TextView userName, userEmail, signedInVia;
    private ImageView userImage;
    private RequestOptions options;
    private String photoUrl;

//  firebase variables
    private static final int RC_SIGN_IN = 9001;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//      inflating the layout file and setting it into the builder
        builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        signInDialogView = inflater.inflate(R.layout.sign_in_dialog, null);
        builder.setView(signInDialogView);

//      user name, email, image and signed in via initialization
        userName = (TextView) signInDialogView.findViewById(R.id.user_name_textView);
        userEmail = (TextView) signInDialogView.findViewById(R.id.user_email_textView);
        userImage = (ImageView) signInDialogView.findViewById(R.id.user_image);
        signedInVia = (TextView) signInDialogView.findViewById(R.id.signed_in_via_textView);

//      set image alternate options
        options = new RequestOptions().placeholder(R.drawable.book_loading);

//      sign out button initializing and click event
        signInOutButton = (Button) signInDialogView.findViewById(R.id.sign_in_out_button);
        signInOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    signOut();
                else
                    createSignInIntent();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }

//  sign in intent
    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.FirebaseLoginTheme)
                        .setLogo(R.drawable.sign_in)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }


//  check if successfully signed in or not
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                updateUI(user);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Toast.makeText(getContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


//  sign out
    private void signOut() {
        DialogInterface.OnClickListener dialogClickLIstener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        AuthUI.getInstance()
                                .signOut(getContext())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        updateUI(null);
                                    }
                                });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setMessage("Are you sure ?").setPositiveButton("Yes", dialogClickLIstener)
                .setNegativeButton("No", dialogClickLIstener).show();
    }



//  update ui according to user signed in or not
    private void updateUI(FirebaseUser user) {
        if (user != null){
            String providerName = user.getIdToken(false).getResult().getSignInProvider();
            userName.setText("User name: " + user.getDisplayName());
            userEmail.setText("User email: " + user.getEmail());
            signedInVia.setText("Signed in via: " + providerName);
            signInOutButton.setText("Sign out");
            signInOutButton.setBackgroundColor(getResources().getColor(R.color.signOut));

            String oldName, newName;
            oldName = "s96-c";
            newName = "s2000-c";

            if (providerName.equals("facebook.com")){
                photoUrl = user.getPhotoUrl() + "?height=500";
            }else{
                photoUrl = user.getPhotoUrl().toString().replace(oldName, newName);
            }

//          set image
            Glide.with(getContext()).load(photoUrl).timeout(50000).circleCrop().into(userImage);
            Log.d("soo", user.getPhotoUrl().toString() + "\n" + photoUrl);
        }else{
            userName.setText("User name: Guest");
            userEmail.setText("User email: ");
            signedInVia.setText("Signed in via: ");
            signInOutButton.setText("Sign in with Google");
            signInOutButton.setBackgroundColor(getResources().getColor(R.color.signIn));

//          set image
            Glide.with(getContext()).load(R.drawable.user).timeout(50000).circleCrop().into(userImage);
        }
    }


}
