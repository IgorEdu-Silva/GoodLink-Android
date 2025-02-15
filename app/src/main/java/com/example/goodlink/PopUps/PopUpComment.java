package com.example.goodlink.PopUps;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBaseManager.ManagerComment;
import com.example.goodlink.FireBaseManager.FireStoreDataManager;
import com.example.goodlink.Adapter.AdapterComment;
import com.example.goodlink.R;
import com.example.goodlink.Utils.FontSizeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopUpComment extends AppCompatActivity {
    private EditText commentAdd;
    private Button sendButton;
    private Button btnBack;
    private Button btnOptions;
    private RecyclerView commentsShow;
    private AdapterComment adapterComment;
    private List<ManagerComment> commentsList;
    private FireStoreDataManager fireStoreDataManager;
    private String repositoryId;
    private String userName;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String fcmToken;
    private Map<Integer, Runnable> menuActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_comment);

        initializeViews();

        if (!initializeUser()) {
            Log.e("PopUpComment", "Erro ao inicializar o usuário ou repositório.");
            return;
        }

        loadComments();

        adapterComment = new AdapterComment(this, commentsList, repositoryId, userName, currentUser.getUid(), new AdapterComment.CommentActionListener() {
            @Override
            public void onLikeClicked(ManagerComment comment) {
                handleLikeClick(comment);
            }

            @Override
            public void onDislikeClicked(ManagerComment comment) {
                handleDislikeClick(comment);
            }
        });

        commentsShow.setAdapter(adapterComment);

        sendButton.setOnClickListener(v -> {
            String userComment = commentAdd.getText().toString().trim();

            if (currentUser != null && !userComment.isEmpty()) {
                fireStoreDataManager.saveUserComment(userComment, repositoryId, userName, new FireStoreDataManager.OnCommentSavedListener() {

                    @Override
                    public void onCommentSaved() {
                        Toast.makeText(PopUpComment.this, "Comentário enviado com sucesso!", Toast.LENGTH_SHORT).show();
                        loadComments();
                    }

                    @Override
                    public void onCommentSaved(String commentId) {
                        loadComments();
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

        btnBack.setOnClickListener(v -> {
            syncCommentsWithDatabase();
            finish();
        });

        EditText commentAdd = findViewById(R.id.commentAdd);
        FontSizeUtils.applySpecificFontSize(commentAdd, FontSizeUtils.getFontSize(this));

        initializeMenu();
    }

    private void initializeMenu() {
        menuActions = new HashMap<>();
        menuActions.put(R.id.orderByDate, this::orderByDate);
        menuActions.put(R.id.orderByLike, this::orderByLike);
        menuActions.put(R.id.orderByDisLike, this::orderByDisLike);

        btnOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(PopUpComment.this, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_options_comments, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                Runnable action = menuActions.get(item.getItemId());
                if (action != null) {
                    action.run();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    private void initializeViews() {
        commentAdd = findViewById(R.id.commentAdd);
        sendButton = findViewById(R.id.commentBtnSend);
        commentsShow = findViewById(R.id.commentsView);
        btnBack = findViewById(R.id.btnBackComments);
        btnOptions = findViewById(R.id.btnOptionsComments);

        commentsList = new ArrayList<>();
        fireStoreDataManager = new FireStoreDataManager();
        commentsShow.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean initializeUser() {
        repositoryId = getIntent().getStringExtra("repositoryId");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (repositoryId == null || currentUser == null) {
            Toast.makeText(this, "Usuário ou repositório inválido!", Toast.LENGTH_SHORT).show();
            return false;
        }

        userName = currentUser.getDisplayName();
        SharedPreferences sharedPreferences = getSharedPreferences("_", MODE_PRIVATE);
        fcmToken = sharedPreferences.getString("fcm_token", null);
        return true;
    }

    private void loadComments() {
        loadComments(null);
    }


    private void loadComments(String orderBy) {
        fireStoreDataManager.getCommentsByRepositoryId(repositoryId, new FireStoreDataManager.OnCommentsLoadedListener() {
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
                    Toast.makeText(PopUpComment.this, "Não há nada aqui por enquanto" + comments, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCommentsLoadFailed(String errorMessage) {
                Toast.makeText(PopUpComment.this, "Erro ao carregar comentários: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("PopUpComment", "Erro ao carregar comentários: " + errorMessage);
            }
        });
    }

    private void orderByDate() {
        loadComments("date");
        Toast.makeText(PopUpComment.this, "Ordenar por Data", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void orderByLike() {
        Collections.sort(commentsList, (comment1, comment2) ->
                Integer.compare(comment2.getLikesCount(), comment1.getLikesCount()));
        adapterComment.notifyDataSetChanged();
        Toast.makeText(PopUpComment.this, "Ordenar por Curtidas", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void orderByDisLike() {
        Collections.sort(commentsList, (comment1, comment2) ->
                Integer.compare(comment2.getDislikesCount(), comment1.getDislikesCount()));
        adapterComment.notifyDataSetChanged();
        Toast.makeText(PopUpComment.this, "Ordenar por Descurtidas", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleLikeClick(ManagerComment comment) {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return;
        }

        boolean isCurrentlyLiked = comment.isLikedByUser(currentUserId);
        boolean isCurrentlyDisliked = comment.isDislikedByUser(currentUserId);

        List<String> updatedLikedBy = new ArrayList<>(comment.getLikedBy());
        List<String> updatedDislikedBy = new ArrayList<>(comment.getDislikedBy());

        if (isCurrentlyLiked) {
            updatedLikedBy.remove(currentUserId);
        } else {
            updatedLikedBy.add(currentUserId);
            if (isCurrentlyDisliked) {
                updatedDislikedBy.remove(currentUserId);
            }
        }

        fireStoreDataManager.updateCommentLikesAndDislikes(comment.getCommentId(), updatedLikedBy, updatedDislikedBy, new FireStoreDataManager.FireStoreDataListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                adapterComment.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleDislikeClick(ManagerComment comment) {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return;
        }

        boolean isCurrentlyLiked = comment.isLikedByUser(currentUserId);
        boolean isCurrentlyDisliked = comment.isDislikedByUser(currentUserId);

        List<String> updatedLikedBy = new ArrayList<>(comment.getLikedBy());
        List<String> updatedDislikedBy = new ArrayList<>(comment.getDislikedBy());

        if (isCurrentlyDisliked) {
            updatedDislikedBy.remove(currentUserId);
        } else {
            updatedDislikedBy.add(currentUserId);
            if (isCurrentlyLiked) {
                updatedLikedBy.remove(currentUserId);
            }
        }

        fireStoreDataManager.updateCommentLikesAndDislikes(comment.getCommentId(), updatedLikedBy, updatedDislikedBy, new FireStoreDataManager.FireStoreDataListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                adapterComment.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void updateLikeDislike(ManagerComment comment, boolean isLike, boolean isDislike) {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();

        List<String> updatedLikedBy = new ArrayList<>(comment.getLikedBy());
        List<String> updatedDislikedBy = new ArrayList<>(comment.getDislikedBy());

        if (isLike) {
            updatedLikedBy.add(currentUser.getUid());
            updatedDislikedBy.remove(currentUser.getUid());
        } else {
            updatedLikedBy.remove(currentUser.getUid());
        }

        if (isDislike) {
            updatedDislikedBy.add(currentUser.getUid());
            updatedLikedBy.remove(currentUser.getUid());
        } else {
            updatedDislikedBy.remove(currentUser.getUid());
        }

        fireStoreDataManager.updateCommentLikesAndDislikes(comment.getCommentId(), updatedLikedBy, updatedDislikedBy, new FireStoreDataManager.FireStoreDataListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadComments();
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }

    public void syncCommentsWithDatabase() {
        for (ManagerComment comment : commentsList) {
            fireStoreDataManager.updateCommentLikesAndDislikes(comment.getCommentId(), comment.getLikedBy(), comment.getDislikedBy(), new FireStoreDataManager.FireStoreDataListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d("Firestore", "Likes/Dislikes updated successfully");
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("FirestoreError", "Error updating likes: " + errorMessage);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncCommentsWithDatabase();
        Log.d("PopUpComment", "Sincronização de comentários concluída antes da destruição.");
    }

}