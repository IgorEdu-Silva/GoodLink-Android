package com.example.goodlink.feature.forum.ui.popup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.R;
import com.example.goodlink.core.domain.model.forum.Comment;
import com.example.goodlink.core.domain.repository.CommentRepository;
import com.example.goodlink.feature.forum.presentation.CommentPresenter;
import com.example.goodlink.feature.forum.presentation.mapper.CommentUiMapper;
import com.example.goodlink.feature.forum.ui.adapter.AdapterComment;
import com.example.goodlink.feature.forum.ui.model.CommentItemUi;
import com.example.goodlink.infrastructure.firebase.firestore.FireStoreDataManager;
import com.example.goodlink.infrastructure.firebase.forum.FirestoreCommentRepository;
import com.example.goodlink.infrastructure.session.FontSettingsRepositoryImplementation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
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

    private String repositoryId;
    private String userName;
    private FirebaseUser currentUser;

    private float fontSize;

    private CommentPresenter presenter;
    private final CommentUiMapper mapper = new CommentUiMapper();

    private Map<Integer, Runnable> menuActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_comment);

        initializeViews();

        if (!initializeUser()) {
            Log.e("PopUpComment", "Usuário ou repositório inválido.");
            return;
        }

        FireStoreDataManager dataManager = new FireStoreDataManager();
        CommentRepository repo = new FirestoreCommentRepository(dataManager);

        presenter = new CommentPresenter(
                dataManager,
                repo,
                repositoryId,
                currentUser.getUid(),
                userName,
                new CommentPresenter.View() {
                    @Override public void render(@NonNull List<Comment> comments) {
                        List<CommentItemUi> ui = new ArrayList<>(comments.size());
                        for (Comment c : comments) ui.add(mapper.toUi(c, currentUser.getUid()));
                        adapterComment.submitList(ui);
                    }

                    @Override public void showMessage(@NonNull String message) {
                        Toast.makeText(PopUpComment.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override public void showError(@NonNull String message) {
                        Toast.makeText(PopUpComment.this, message, Toast.LENGTH_SHORT).show();
                        Log.e("PopUpComment", message);
                    }
                }
        );

        adapterComment = new AdapterComment(new AdapterComment.CommentActionListener() {
            @Override public void onLike(@NonNull String commentId) { presenter.toggleLike(commentId); }
            @Override public void onDislike(@NonNull String commentId) { presenter.toggleDislike(commentId); }
            @Override public void onReport(@NonNull String repositoryId) { /* TODO */ }
        });
        commentsShow.setAdapter(adapterComment);

        presenter.load();

        sendButton.setOnClickListener(v ->
                presenter.sendComment(commentAdd.getText().toString())
        );

        btnBack.setOnClickListener(v -> finish());

        FontSettingsRepositoryImplementation fontRepo = new FontSettingsRepositoryImplementation(this);
        fontSize = fontRepo.getFontSize();
        applyFont(commentAdd);

        initializeMenu();
    }

    private void initializeMenu() {
        menuActions = new HashMap<>();
        menuActions.put(R.id.orderByLike, () -> presenter.sortByLikesDesc());
        menuActions.put(R.id.orderByDisLike, () -> presenter.sortByDislikesDesc());

        btnOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(PopUpComment.this, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_options_comments, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                Runnable action = menuActions.get(item.getItemId());
                if (action != null) { action.run(); return true; }
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

        commentsShow.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean initializeUser() {
        repositoryId = getIntent().getStringExtra("repositoryId");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (repositoryId == null || currentUser == null) {
            Toast.makeText(this, "Usuário ou repositório inválido!", Toast.LENGTH_SHORT).show();
            return false;
        }

        userName = currentUser.getDisplayName();

        SharedPreferences sharedPreferences = getSharedPreferences("_", MODE_PRIVATE);
        String fcmToken = sharedPreferences.getString("fcm_token", null); // se for usar depois

        return true;
    }

    private void applyFont(View v) {
        if (v instanceof TextView) {
            ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
    }
}