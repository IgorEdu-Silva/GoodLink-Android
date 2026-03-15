    package com.example.goodlink.infrastructure.firebase.firestore;

    import android.annotation.SuppressLint;
    import android.util.Log;

    import androidx.annotation.NonNull;

    import com.example.goodlink.core.domain.model.forum.RepositoryItem;
    import com.example.goodlink.core.domain.model.user.User;
    import com.example.goodlink.infrastructure.firebase.forum.dto.CommentDocument;
    import com.example.goodlink.infrastructure.firebase.forum.dto.RepositoryDocument;
    import com.example.goodlink.infrastructure.firebase.forum.mapper.CommentMapper;
    import com.example.goodlink.infrastructure.firebase.forum.mapper.RepositoryMapper;
    import com.example.goodlink.infrastructure.firebase.user.dto.UserDto;
    import com.example.goodlink.infrastructure.firebase.user.mapper.UserMapper;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.CollectionReference;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.FirebaseFirestoreException;
    import com.google.firebase.firestore.Query;
    import com.google.firebase.firestore.QueryDocumentSnapshot;
    import com.google.firebase.firestore.FieldValue;
    import com.google.firebase.firestore.Transaction;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;

    public class FireStoreDataManager {
        private final FirebaseFirestore firestore;
        private final CollectionReference usersCollection;
        private final CollectionReference repositoryCollection;
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private final FirebaseAuth firebaseAuth;

        public FireStoreDataManager() {
            firestore = FirebaseFirestore.getInstance();
            usersCollection = firestore.collection("users");
            repositoryCollection = firestore.collection("repository");
            firebaseAuth = FirebaseAuth.getInstance();
        }

        public enum CommentReaction {
            LIKE, DISLIKE
        }

        public void toggleCommentReaction(
                @NonNull String commentId,
                @NonNull String userId,
                @NonNull CommentReaction reaction,
                @NonNull FireStoreDataListener<Void> listener
        ) {
            DocumentReference ref = db.collection("userComments").document(commentId);

            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot snap = transaction.get(ref);

                        List<String> likedBy = (List<String>) snap.get("likedBy");
                        List<String> dislikedBy = (List<String>) snap.get("dislikedBy");

                        boolean liked = likedBy != null && likedBy.contains(userId);
                        boolean disliked = dislikedBy != null && dislikedBy.contains(userId);

                        // regra: LIKE remove DISLIKE e vice-versa
                        if (reaction == CommentReaction.LIKE) {
                            if (liked) {
                                transaction.update(ref, "likedBy", FieldValue.arrayRemove(userId));
                            } else {
                                transaction.update(ref, "likedBy", FieldValue.arrayUnion(userId));
                                if (disliked) transaction.update(ref, "dislikedBy", FieldValue.arrayRemove(userId));
                            }
                        } else { // DISLIKE
                            if (disliked) {
                                transaction.update(ref, "dislikedBy", FieldValue.arrayRemove(userId));
                            } else {
                                transaction.update(ref, "dislikedBy", FieldValue.arrayUnion(userId));
                                if (liked) transaction.update(ref, "likedBy", FieldValue.arrayRemove(userId));
                            }
                        }

                        return null;
                    }).addOnSuccessListener(ignored -> listener.onSuccess(null))
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }

        public void toggleCommentReactionWithCounters(
                @NonNull String commentId,
                @NonNull String userId,
                @NonNull CommentReaction reaction,
                @NonNull FireStoreDataListener<Void> listener
        ) {
            DocumentReference ref = db.collection("userComments").document(commentId);

            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot snap = transaction.get(ref);

                        List<String> likedBy = (List<String>) snap.get("likedBy");
                        List<String> dislikedBy = (List<String>) snap.get("dislikedBy");

                        boolean liked = likedBy != null && likedBy.contains(userId);
                        boolean disliked = dislikedBy != null && dislikedBy.contains(userId);

                        long likesCount = snap.getLong("likesCount") != null ? snap.getLong("likesCount") : 0L;
                        long dislikesCount = snap.getLong("dislikesCount") != null ? snap.getLong("dislikesCount") : 0L;

                        if (reaction == CommentReaction.LIKE) {
                            if (liked) {
                                transaction.update(ref, "likedBy", FieldValue.arrayRemove(userId));
                                transaction.update(ref, "likesCount", Math.max(0L, likesCount - 1L));
                            } else {
                                transaction.update(ref, "likedBy", FieldValue.arrayUnion(userId));
                                transaction.update(ref, "likesCount", likesCount + 1L);

                                if (disliked) {
                                    transaction.update(ref, "dislikedBy", FieldValue.arrayRemove(userId));
                                    transaction.update(ref, "dislikesCount", Math.max(0L, dislikesCount - 1L));
                                }
                            }
                        } else {
                            if (disliked) {
                                transaction.update(ref, "dislikedBy", FieldValue.arrayRemove(userId));
                                transaction.update(ref, "dislikesCount", Math.max(0L, dislikesCount - 1L));
                            } else {
                                transaction.update(ref, "dislikedBy", FieldValue.arrayUnion(userId));
                                transaction.update(ref, "dislikesCount", dislikesCount + 1L);

                                if (liked) {
                                    transaction.update(ref, "likedBy", FieldValue.arrayRemove(userId));
                                    transaction.update(ref, "likesCount", Math.max(0L, likesCount - 1L));
                                }
                            }
                        }
                        return null;
                    }).addOnSuccessListener(ignored -> listener.onSuccess(null))
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }

        public void updateReaction(
                @NonNull String commentId,
                @NonNull CommentReaction reaction,
                @NonNull String userId,
                @NonNull FireStoreDataListener<Void> listener
        ) {

            DocumentReference docRef =
                    FirebaseFirestore.getInstance()
                            .collection("userComments")
                            .document(commentId);

            FirebaseFirestore.getInstance().runTransaction(transaction -> {

                DocumentSnapshot snapshot = transaction.get(docRef);
                if (!snapshot.exists()) {
                    throw new FirebaseFirestoreException(
                            "Comment not found",
                            FirebaseFirestoreException.Code.NOT_FOUND
                    );
                }

                if (reaction == CommentReaction.LIKE) {

                    transaction.update(docRef,
                            "likedBy", FieldValue.arrayUnion(userId),
                            "dislikedBy", FieldValue.arrayRemove(userId)
                    );

                } else {

                    transaction.update(docRef,
                            "dislikedBy", FieldValue.arrayUnion(userId),
                            "likedBy", FieldValue.arrayRemove(userId)
                    );
                }

                return null;

            }).addOnSuccessListener(unused ->
                    listener.onSuccess(null)
            ).addOnFailureListener(e ->
                    listener.onFailure(e.getMessage() == null
                            ? "Erro ao atualizar reação"
                            : e.getMessage())
            );
        }
        public DocumentReference getUserDocumentReference(String userId) {
            return db.collection("users").document(userId);
        }

        public DocumentReference getRepositoryDocumentReference(String repositoryId) {
            return db.collection("repository").document(repositoryId);
        }

        public Task<DocumentSnapshot> getRepositoryData(String repositoryId) {
            return db.collection("repository").document(repositoryId).get();
        }

        public Task<DocumentSnapshot> getUserData(String userId) {
            return db.collection("users").document(userId).get();
        }

        public void addUser(String userId, String name, String email) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);

            usersCollection.document(userId).set(user)
                    .addOnSuccessListener(aVoid -> Log.d("FireStoreDataManager", "User added successfully"))
                    .addOnFailureListener(e -> Log.e("FireStoreDataManager", "Error adding user", e));
        }

        public void getUser(String userId, FireStoreDataListener<User> listener) {
            usersCollection.document(userId).get()
                    .addOnSuccessListener(snap -> {
                        if (!snap.exists()) {
                            listener.onFailure("User not found");
                            return;
                        }
                        UserDto dto = snap.toObject(UserDto.class);
                        if (dto == null) {
                            listener.onFailure("User inválido");
                            return;
                        }
                        listener.onSuccess(UserMapper.toDomain(snap.getId(), dto));
                    })
                    .addOnFailureListener(e -> listener.onFailure(
                            e.getMessage() == null ? "Erro desconhecido" : e.getMessage()
                    ));
        }

        private static final String TAG = "FireStoreDataManager";

        public String getCurrentUserId() {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                return currentUser.getUid();
            } else {
                Log.e(TAG, "Current user is null");
                return null;
            }
        }

        public String generateRepositoryId() {
            return repositoryCollection.document().getId();
        }

        public void getRepositoryFromFirestore(OnRepositoryLoadedListener listener) {
            repositoryCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<RepositoryItem> repositories = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot  : queryDocumentSnapshots) {
                            RepositoryDocument document = snapshot.toObject(RepositoryDocument.class);
                            RepositoryItem item =
                                    RepositoryMapper.toDomain(
                                            snapshot.getId(),
                                            document,
                                            false
                                    );

                            repositories.add(item);
                        }
                        listener.onRepositoriesLoaded(repositories);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching repositories: ", e);
                        listener.onRepositoriesLoadFailed(e.getMessage());
                    });
        }

        public void getUserIdToNameMap(OnUserIdToNameMapListener listener) {
            usersCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<String, String> userIdToNameMap = new HashMap<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String userId = documentSnapshot.getId();
                            String userName = documentSnapshot.getString("name");
                            userIdToNameMap.put(userId, userName);
                        }
                        listener.onUserIdToNameMapLoaded(userIdToNameMap);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching user IDs and names: ", e);
                        listener.onUserIdToNameMapLoadFailed(e.getMessage());
                    });
        }

        public void createRepository(String userId, RepositoryDocument repositoryDocument, OnRepositoryCreatedListener listener) {
            if (userId != null && !userId.isEmpty()) {
                Map<String, Object> repository = getStringObjectMap(repositoryDocument);

                repositoryCollection.add(repository)
                        .addOnSuccessListener(documentReference -> {
                            String repositoryId = documentReference.getId();
                            listener.onRepositoryCreated(repositoryId);

                            Map<String, Object> repositoryIdMap = new HashMap<>();
                            repositoryIdMap.put("repositoryId", repositoryId);

                            usersCollection.document(userId).collection("userRepositories").add(repositoryIdMap)
                                    .addOnSuccessListener(documentReference1 -> Log.d(TAG, "Repository ID added to user's collection: " + userId))
                                    .addOnFailureListener(e -> Log.e(TAG, "Error adding repository ID to user's collection: " + userId, e));
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error creating repository: ", e);
                            listener.onRepositoryCreationFailed(e.getMessage());
                        });
            } else {
                Log.e(TAG, "Error creating repository: UserId is null or empty");
                listener.onRepositoryCreationFailed("UserId is null or empty");
            }
        }

        @NonNull
        private static Map<String, Object> getStringObjectMap(RepositoryDocument repositoryDocument) {
            Map<String, Object> repository = new HashMap<>();
            repository.put("titulo", repositoryDocument.getTitle());
            repository.put("descricao", repositoryDocument.getDescription());
            repository.put("nomeCanal", repositoryDocument.getChannelName());
            repository.put("iframe", repositoryDocument.getIframe());
            repository.put("urlCanal", repositoryDocument.getChannelUrl());
            repository.put("categoria", repositoryDocument.getCategory());
            repository.put("nomeUsuario", repositoryDocument.getUserId());
            repository.put("dataPub", repositoryDocument.getPublishedAt());
            return repository;
        }

        public void saveRepositoryRating(String userId, String repositoryId, String rating, OnRepositoryRatingSavedListener listener) {
            if (userId != null && repositoryId != null && rating != null) {
                DocumentReference userRatingRef = usersCollection.document(userId).collection("userRatings").document(repositoryId);

                Map<String, Object> newRating = new HashMap<>();
                newRating.put("rating", rating);
                newRating.put("userRating", userId);
                newRating.put("repositoryRated", repositoryId);

                userRatingRef.set(newRating)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Rating saved successfully for repository: " + repositoryId);
                            listener.onRepositoryRatingSaved(repositoryId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error saving rating for repository: " + repositoryId, e);
                            listener.onRepositoryRatingSaveFailed(e.getMessage());
                        });
            } else {
                Log.e(TAG, "One or more required fields are null");
                listener.onRepositoryRatingSaveFailed("One or more required fields are null");
            }
        }

        public void saveUserComment(String userComment, String repositoryId, String userName,  OnCommentSavedListener listener) {
            String commentId = db.collection("userComments").document().getId();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            CommentDocument comment = CommentMapper.toDocument(userName, userComment, repositoryId, commentId, currentDate);

            db.collection("userComments")
                    .document(commentId)
                    .set(comment)
                    .addOnSuccessListener(aVoid -> listener.onCommentSaved(commentId))
                    .addOnFailureListener(e -> listener.onCommentSaveFailed(e.getMessage()));
        }

        public void updateCommentLikesAndDislikes(String commentId, List<String> updatedLikedBy, List<String> updatedDislikedBy, FireStoreDataListener<Void> listener) {
            DocumentReference commentRef = db.collection("userComments").document(commentId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("likedBy", updatedLikedBy);
            updates.put("dislikedBy", updatedDislikedBy);

            commentRef.update(updates)
                    .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }


        public void getCommentsByRepositoryId(String repositoryId, OnCommentsLoadedListener listener) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("userComments")
                    .whereEqualTo("repositoryId", repositoryId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<CommentDocument> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CommentDocument comment = document.toObject(CommentDocument.class);
                                comment.setCommentId(document.getId());
                                comments.add(comment);
                            }
                            listener.onCommentsLoaded(comments);
                        } else {
                            listener.onCommentsLoadFailed(task.getException().getMessage());
                        }
                    });
        }

        public void orderComments(String repositoryId, String orderBy, OnCommentsLoadedListener listener) {
            Query query = db.collection("userComments")
                    .whereEqualTo("repositoryId", repositoryId);

            if ("date".equals(orderBy)) {
                query = query.orderBy("date", Query.Direction.DESCENDING);
            } else if ("likes".equals(orderBy)) {
                query = query.orderBy("likes", Query.Direction.DESCENDING);
            } else if ("dislikes".equals(orderBy)) {
                query = query.orderBy("dislikes", Query.Direction.DESCENDING);
            }

            query.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<CommentDocument> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CommentDocument comment = document.toObject(CommentDocument.class);
                                comments.add(comment);
                            }
                            listener.onCommentsLoaded(comments);
                        } else {
                            listener.onCommentsLoadFailed(task.getException().getMessage());
                        }
                    });
        }


        public void getLinksRepositories(String repositoryId, FireStoreDataListener<RepositoryItem> listener) {
            repositoryCollection.document(repositoryId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            RepositoryItem repositoryItem = documentSnapshot.toObject(RepositoryItem.class);
                            listener.onSuccess(repositoryItem);
                        } else {
                            listener.onFailure("Repository not found");
                        }
                    })
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }

        public void removeRepositoryFromFavorites(String userId, String repositoryId, OnRepositoryRemovedFromFavoritesListener listener) {
            if (userId != null && repositoryId != null) {
                usersCollection.document(userId)
                        .collection("userFavorites")
                        .document(repositoryId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Repository removed from favorites for user: " + userId);
                            listener.onRepositoryRemovedFromFavorites(repositoryId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error removing repository from favorites for user: " + userId, e);
                            listener.onRepositoryRemoveFromFavoritesFailed(e.getMessage());
                        });

                repositoryCollection.document(repositoryId)
                        .update("favorited", false)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Repository updated as not favorited: " + repositoryId))
                        .addOnFailureListener(e -> Log.e(TAG, "Error updating repository as not favorited: " + repositoryId, e));
            } else {
                Log.e(TAG, "One or more required fields are null");
                listener.onRepositoryRemoveFromFavoritesFailed("One or more required fields are null");
            }
        }

        public void addRepositoryToFavorites(String userId, String repositoryId, OnRepositoryAddedToFavoritesListener listener) {
            if (userId != null && repositoryId != null) {
                Map<String, Object> repositoryIdMap = new HashMap<>();
                repositoryIdMap.put("repositoryId", repositoryId);

                usersCollection.document(userId)
                        .collection("userFavorites")
                        .document(repositoryId)
                        .set(repositoryIdMap)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Repository added to favorites for user: " + userId);
                            listener.onRepositoryAddedToFavorites(repositoryId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error adding repository to favorites for user: " + userId, e);
                            listener.onRepositoryAddToFavoritesFailed(e.getMessage());
                        });

                repositoryCollection.document(repositoryId)
                        .update("favorited", true)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Repository updated as favorited: " + repositoryId))
                        .addOnFailureListener(e -> Log.e(TAG, "Error updating repository as favorited: " + repositoryId, e));
            } else {
                Log.e(TAG, "One or more required fields are null");
                listener.onRepositoryAddToFavoritesFailed("One or more required fields are null");
            }
        }



        public interface OnCommentSavedListener {
            void onCommentSaved();

            void onCommentSaved(String commentId);
            void onCommentSaveFailed(String errorMessage);
        }


        public interface OnUserIdToNameMapListener {
            void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap);
            void onUserIdToNameMapLoadFailed(String errorMessage);
        }

        public interface OnRepositoryLoadedListener {
            void onRepositoriesLoaded(List<RepositoryItem> repositories);
            void onRepositoriesLoadFailed(String errorMessage);
        }

        public interface OnRepositoryCreatedListener {
            void onRepositoryCreated(String repositoryId);
            void onRepositoryCreationFailed(String errorMessage);
        }

        public interface FireStoreDataListener<T> {
            void onSuccess(T data);
            void onFailure(String errorMessage);
        }

        public interface OnRepositoryRatingSavedListener {
            void onRepositoryRatingSaved(String repositoryId);
            void onRepositoryRatingSaveFailed(String errorMessage);
        }

        public interface OnCommentsLoadedListener {
            @SuppressLint("NotifyDataSetChanged")
            void onCommentsLoaded(List<CommentDocument> comments);

            void onCommentsLoadFailed(String errorMessage);
        }

        public interface OnRepositoryRemovedFromFavoritesListener {
            void onRepositoryRemovedFromFavorites(String removedRepositoryId);

            void onRepositoryRemoveFromFavoritesFailed(String errorMessage);
        }

        public interface OnRepositoryAddedToFavoritesListener {
            void onRepositoryAddedToFavorites(String addedRepositoryId);

            void onRepositoryAddToFavoritesFailed(String errorMessage);
        }
    }
