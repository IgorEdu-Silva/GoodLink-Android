package com.example.goodlink.PopUp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBaseManager.ManagerComment;
import com.example.goodlink.FireBaseManager.FireStoreDataManager;
import com.example.goodlink.Functions.MessagingService;
import com.example.goodlink.Adapter.AdapterComment;
import com.example.goodlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class PopUpComment extends AppCompatActivity {
    private EditText commentAdd;
    private ImageButton sendButton;
    private RecyclerView commentsShow;
    private AdapterComment adapterComment;
    private List<ManagerComment> commentsList;
    private FireStoreDataManager fireStoreDataManager;
    private String playlistId;
    private String userName;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_comment);

        commentAdd = findViewById(R.id.commentAdd);
        sendButton = findViewById(R.id.commentBtnSend);
        commentsShow = findViewById(R.id.commentsShow);
        commentsList = new ArrayList<>();
        fireStoreDataManager = new FireStoreDataManager();

        playlistId = getIntent().getStringExtra("playlistId");
        if (playlistId == null) {
            Log.e("PopUpComment", "playlistId is null");
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userName = currentUser.getDisplayName();
            SharedPreferences sharedPreferences = getSharedPreferences("_", MODE_PRIVATE);
            fcmToken = sharedPreferences.getString("fcm_token", null);
        } else {
            Log.e("PopUpComment", "currentUser is null");
            return;
        }

        commentsShow.setLayoutManager(new LinearLayoutManager(this));
        adapterComment = new AdapterComment(commentsList, playlistId, userName);
        commentsShow.setAdapter(adapterComment);

        sendButton.setOnClickListener(v -> {
            String commentText = commentAdd.getText().toString().trim();

            if (currentUser != null && !commentText.isEmpty()) {
                fireStoreDataManager.saveUserComment(commentText, playlistId, userName, new FireStoreDataManager.OnCommentSavedListener() {
                    @Override
                    public void onCommentSaved() {
                        Toast.makeText(PopUpComment.this, "Comentário enviado com sucesso!", Toast.LENGTH_SHORT).show();
                        loadComments();
                        sendNotificationToPlaylistOwner();
                    }

                    @Override
                    public void onCommentSaveFailed(String errorMessage) {
                        Toast.makeText(PopUpComment.this, "Erro ao enviar comentário: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(PopUpComment.this, "Comentário não pode estar vazio!", Toast.LENGTH_SHORT).show();
            }
        });

        loadComments();
    }

    private void loadComments() {
        fireStoreDataManager.getCommentsByPlaylistId(playlistId, new FireStoreDataManager.OnCommentsLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCommentsLoaded(List<ManagerComment> comments) {
                if (comments != null && !comments.isEmpty()) {
                    commentsList.clear();
                    commentsList.addAll(comments);
                    adapterComment.notifyDataSetChanged();
                    if (adapterComment != null) {
                        adapterComment.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(PopUpComment.this, "Seja o primeiro a comentário nessa playlist!" + comments, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCommentsLoadFailed(String errorMessage) {
                Toast.makeText(PopUpComment.this, "Erro ao carregar comentários: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("PopUpComment", "Erro ao carregar comentários: " + errorMessage);
            }
        });
    }

    private void sendNotificationToPlaylistOwner() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        int uniqueNotificationId = MessagingService.generateUniqueNotificationId();
                        MessagingService.sendNotificationToToken(this, token, uniqueNotificationId, "Novo comentário", "Mais comentários são feitos, atualize e veja-os.");
                    }
                });
    }
}