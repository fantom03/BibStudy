package com.example.biblicalstudies;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    private CallbackManager callbackManager;
    private static DatabaseReference dbRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        final EditText email = findViewById(R.id.signup_email),
                pass = findViewById(R.id.signup_pass),
                confirmPass = findViewById(R.id.signup_confirmpass);

        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().isEmpty()){
                    confirmPass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signin_lock,0,
                            0,0);
                    pass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signin_lock1,0,
                            0,0);
                }

                else if(editable.toString().equals(pass.getText().toString())){
                    confirmPass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signin_lock,0,
                            R.drawable.ic_check_black_24dp,0);
                    pass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signin_lock1,0,
                            R.drawable.ic_check_black_24dp,0);
                }else{
                    confirmPass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signin_lock,0,
                            R.drawable.ic_social_close,0);
                    pass.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signin_lock1,0,
                            0,0);
                }

            }
        });

        callbackManager = CallbackManager.Factory.create();

        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickSigninPage(View view) {
        final RelativeLayout signIn = findViewById(R.id.view_sign_in);
        final RelativeLayout signUp = findViewById(R.id.view_sign_up);

        signUp.animate().translationX(50f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(0).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() {
                signIn.setVisibility(RelativeLayout.VISIBLE);
                signIn.animate().translationX(0).alpha(1f).
                        setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(100);
                signUp.setVisibility(RelativeLayout.GONE);
            }
        });
    }

    public void onClickSignupPage(View view) {
        final RelativeLayout signIn = findViewById(R.id.view_sign_in);
        final RelativeLayout signUp = findViewById(R.id.view_sign_up);

        signIn.animate().translationX(-50f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(0).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() {
                signIn.setVisibility(RelativeLayout.GONE);
                signUp.setVisibility(RelativeLayout.VISIBLE);
                signUp.animate().translationX(0).alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(100);
            }
        });
    }

    public void onClickCancelSigninPage(View view) {
        finish();
    }

    public void onClickSocialPage(View view) {
        final TextView social_view = findViewById(R.id.social_login);
        final FloatingActionButton fab = findViewById(R.id.social_btn);
        final FloatingActionButton fab1 = findViewById(R.id.social_btn1);
        final FloatingActionButton fab2 = findViewById(R.id.social_btn2);

        social_view.animate().alpha(0)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        social_view.setVisibility(View.GONE);
                        fab.show();
                        fab1.show();
                        fab2.show();

                        fab.animate().rotation(360f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                                .alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600);

                        fab1.animate()
                                .translationX(-getResources().getDimension(R.dimen.standard_55))
                                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                                .alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(600);
                        fab2.animate()
                                .translationX(getResources().getDimension(R.dimen.standard_55))
                                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                                .alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(600);
                    }
                });


    }

    public void onClickSocialClose(View view) {
        final TextView social_view = findViewById(R.id.social_login);
        final FloatingActionButton fab = findViewById(R.id.social_btn);
        final FloatingActionButton fab1 = findViewById(R.id.social_btn1);
        final FloatingActionButton fab2 = findViewById(R.id.social_btn2);

        fab.animate().rotation(-360f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                .alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600);
        fab1.animate()
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                .translationX(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                .alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600);
        fab2.animate()
                .translationX(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                .alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.hide();
                        fab1.hide();
                        fab2.hide();
                        social_view.setVisibility(View.VISIBLE);
                        social_view.animate().alpha(1f)
                                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                    }
                });

    }

    public void onClickSocialGoogle(View view) {
        Toast.makeText(this, "Sorry! Service not available yet. " +
                "Please try different Sign in methods.", Toast.LENGTH_LONG).show();
    }

    public void onClickSocialFacebook(View view) {
        LoginButton fbAuth = findViewById(R.id.login_button);
        fbAuth.performClick();
        fbAuth.setPermissions("email", "name", "first_name", "last_name");
        fbAuth.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(SignIn.this,
                        "Creadentials Retreived.. Further Processing", Toast.LENGTH_LONG).show();
                AccessToken token = loginResult.getAccessToken();
                fbAccessTokenAuthentication(token);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignIn.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onClickSignup(final View view){

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final EditText email = findViewById(R.id.signup_email),
                pass = findViewById(R.id.signup_pass),
                confirmPass = findViewById(R.id.signup_confirmpass);

        if(email.getText().toString().isEmpty()||pass.getText().toString().isEmpty()||
        confirmPass.getText().toString().isEmpty()){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
            if(email.getText().toString().isEmpty()) email.requestFocus();
            else if (pass.getText().toString().isEmpty()) pass.requestFocus();
            else confirmPass.requestFocus();
        }else{
            final ProgressDialog progressDialog = new ProgressDialog(this, true);
            progressDialog.setMessage("Loading");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        try {
                            Thread.sleep(400);
                            auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignIn.this, "Verification Link sent to you email "+
                                                    email.getText().toString(), Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            FirebaseAuth.getInstance().getCurrentUser().delete();
                                            Toast.makeText(SignIn.this, "Failed to send verification email. Email invalid or" +
                                                    " "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            progressDialog.dismiss();
                        }
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(SignIn.this,
                                task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    public void onClickSignin(final View view) {
        final EditText email = findViewById(R.id.signin_email),
                password = findViewById(R.id.signin_password);
        final LinearLayout signIn = findViewById(R.id.signin_btn);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
            if(email.getText().toString().isEmpty()) email.requestFocus();
            else if (password.getText().toString().isEmpty()) password.requestFocus();
        } else {
            signIn.animate().scaleX(0)
                    .setInterpolator(new AnticipateOvershootInterpolator()).setDuration(600)
                    .alpha(0).setInterpolator(new DecelerateInterpolator()).setDuration(400)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.animate().alpha(1f)
                                    .setInterpolator(new AnticipateOvershootInterpolator()).setDuration(10);
                            signIn.setVisibility(View.GONE);
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });

            authenticate(view, email, password, signIn, progressBar);
        }

    }

    public void onClickForgotPassword(final View view){
        EditText email = findViewById(R.id.signin_email);
        if(email.getText().toString().isEmpty()){
            email.requestFocus();
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this, true);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(SignIn.this, "Password reset link is sent to your email.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignIn.this, "Not a valid email or "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void authenticate(View view, final EditText email, final EditText password,
                              final LinearLayout signIn, final ProgressBar progressBar) {

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        String em = email.getText().toString(), pass = password.getText().toString();
        auth.signInWithEmailAndPassword(em, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                Toast.makeText(SignIn.this, "Email not verified. Verify to sign in.", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                progressBar.animate().alpha(0f)
                                        .setInterpolator(new AnticipateOvershootInterpolator()).setDuration(10).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        signIn.setVisibility(View.VISIBLE);
                                        signIn.animate().scaleX(1)
                                                .setInterpolator(new AnticipateOvershootInterpolator()).setDuration(600)
                                                .alpha(1f).setInterpolator(new DecelerateInterpolator()).setDuration(400)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    }
                                                });
                                    }
                                });
                                return;
                            }
                            try {
                                Thread.sleep(400);
                                progressBar.animate().alpha(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                                final TextView txt = findViewById(R.id.progress_check);
                                txt.setVisibility(View.VISIBLE);
                                AnimatorSet set = (AnimatorSet)AnimatorInflater.loadAnimator(SignIn.this,
                                        R.animator.zoom_in);
                                set.setTarget(txt);
                                set.start();
                                Toast.makeText(SignIn.this,
                                        "Sign In Successful!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(SignIn.this,HomeDefault.class);
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        } else {
                            try {
                                Thread.sleep(800);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(SignIn.this,
                                        task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                retainViewSignin(signIn, progressBar, email, password);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    private void retainViewSignin(final LinearLayout signIn, final ProgressBar progressBar,
                                  final EditText email,final EditText password) {

        progressBar.animate().alpha(0)
                .setInterpolator(new DecelerateInterpolator()).setDuration(10)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        signIn.setVisibility(View.VISIBLE);
                        signIn.animate().alpha(1f)
                                .setInterpolator(new AccelerateInterpolator()).setDuration(400)
                                .scaleX(1f).setInterpolator(new AnticipateOvershootInterpolator())
                                .setDuration(600);
                    }
                });

        password.getText().clear();
        password.requestFocus();

    }

    private void fbAccessTokenAuthentication(final AccessToken token){

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignIn.this,
                            "Sign in Successful!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SignIn.this,HomeDefault.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(SignIn.this,
                            task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeDefault.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
