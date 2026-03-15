package com.example.goodlink.feature.auth.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goodlink.feature.auth.ui.FragmentPageUserAccount;
import com.example.goodlink.feature.auth.ui.settings.appearence.FragmentPageUserApparence;
import com.example.goodlink.infrastructure.session.LoginStateStorage;
import com.example.goodlink.infrastructure.navigation.HelperNavigateToFragment;
import com.example.goodlink.R;
import com.example.goodlink.feature.forum.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentPageUserSettings extends Fragment {
    private ImageView ico_config_account, ico_config_security, ico_config_apparence, ico_config_accessibility, ico_config_language, ico_config_notifications, ico_config_support, ico_config_news;
    private TextView text_config_account, text_config_security, text_config_apparence, text_config_accessibility, text_config_language, text_config_notifications, text_config_support, text_config_news;
    private Button btnLogoutConfig;
    private final Map<Integer, Runnable> actionMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_user_settings, container, false);

        initUI(view);
        setupListener();

        return view;
    }

    private void initUI(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<View> viewList = Arrays.asList(ico_config_account, ico_config_security, ico_config_apparence, ico_config_accessibility, ico_config_language, ico_config_notifications, ico_config_support, ico_config_news,
                text_config_account, text_config_security, text_config_apparence, text_config_accessibility, text_config_language, text_config_notifications, text_config_support, text_config_news);
        setViews(viewList, user != null && !user.isAnonymous());

        btnLogoutConfig = view.findViewById(R.id.btnLogoutConfig);
        ico_config_account = view.findViewById(R.id.ico_config_account);
        text_config_account = view.findViewById(R.id.text_config_account);
        ico_config_security = view.findViewById(R.id.ico_config_security);
        text_config_security = view.findViewById(R.id.text_config_security);
        ico_config_apparence = view.findViewById(R.id.ico_config_apparence);
        text_config_apparence = view.findViewById(R.id.text_config_apparence);
        ico_config_accessibility = view.findViewById(R.id.ico_config_accessibility);
        text_config_accessibility = view.findViewById(R.id.text_config_accessibility);
        ico_config_language = view.findViewById(R.id.ico_config_language);
        text_config_language = view.findViewById(R.id.text_config_language);
        ico_config_notifications = view.findViewById(R.id.ico_config_notifications);
        text_config_notifications = view.findViewById(R.id.text_config_notifications);
        ico_config_support = view.findViewById(R.id.ico_config_support);
        text_config_support = view.findViewById(R.id.text_config_support);
        ico_config_news = view.findViewById(R.id.ico_config_news);
        text_config_news = view.findViewById(R.id.text_config_news);
    }

    private void setupListener() {
        btnLogoutConfig.setOnClickListener(view -> logoutAccount());

        actionMap.put(R.id.ico_config_account, this::openAccountSettings);
        actionMap.put(R.id.text_config_account, this::openAccountSettings);
        actionMap.put(R.id.ico_config_security, this::openSecuritySettings);
        actionMap.put(R.id.text_config_security, this::openSecuritySettings);
        actionMap.put(R.id.ico_config_apparence, this::openApparenceSettings);
        actionMap.put(R.id.text_config_apparence, this::openApparenceSettings);
        actionMap.put(R.id.ico_config_accessibility, this::openAccessibilitySettings);
        actionMap.put(R.id.text_config_accessibility, this::openAccessibilitySettings);
        actionMap.put(R.id.ico_config_language, this::openLanguageSettings);
        actionMap.put(R.id.text_config_language, this::openLanguageSettings);
        actionMap.put(R.id.ico_config_notifications, this::openNotificationSettings);
        actionMap.put(R.id.text_config_notifications, this::openNotificationSettings);
        actionMap.put(R.id.ico_config_support, this::openSupport);
        actionMap.put(R.id.text_config_support, this::openSupport);
        actionMap.put(R.id.ico_config_news, this::openNews);
        actionMap.put(R.id.text_config_news, this::openNews);

        setClickListeners(v -> {
                    Runnable action = actionMap.get(v.getId());
                    if (action != null) action.run();
                },
                ico_config_account,
                text_config_account,
                ico_config_security,
                text_config_security,
                ico_config_apparence,
                text_config_apparence,
                ico_config_accessibility,
                text_config_accessibility,
                ico_config_language,
                text_config_language,
                ico_config_notifications,
                text_config_notifications,
                ico_config_support,
                text_config_support,
                ico_config_news,
                text_config_news
        );
    }

    private void setClickListeners(View.OnClickListener listener, View... views) {
        for (View view : views) {
            if (view != null) view.setOnClickListener(listener);
        }
    }

    private void setViews(List<View> views, boolean isEnable)  {
        for (View view : views) {
            if (view != null) {
                view.setEnabled(isEnable);
            }
        }
    }

    private void openAccountSettings() {
        HelperNavigateToFragment.navigateTo(requireActivity(), new FragmentPageUserAccount(), "UserAccount", true);
    }

    private void openSecuritySettings() {
        HelperNavigateToFragment.navigateTo(requireActivity(), new FragmentPageUserPrivacy(), "UserPrivacy", true);
    }

    private void openApparenceSettings() {
        HelperNavigateToFragment.navigateTo(requireActivity(), new FragmentPageUserApparence(), "UserApparence", true);
    }

    private void openAccessibilitySettings() {
        HelperNavigateToFragment.navigateTo(requireActivity(), new FragmentPageUserAcessibility(), "UserAcessibility", true);
    }

    private void openLanguageSettings() {
        HelperNavigateToFragment.navigateTo(requireActivity(), new FragmentPageUserLanguage(), "UserLanguage", true);
    }

    private void openNotificationSettings() {
        HelperNavigateToFragment.navigateTo(requireActivity(), new FragmentPageUserNotifications(), "UserNotifications", true);
    }

    private void openSupport() {
//        navigateToFragment(new FragmentPageUserSupport(), "UserSupport");
        Toast.makeText(getContext(), "Aba em desenvolvimento...", Toast.LENGTH_SHORT).show();
    }

    private void openNews() {
//        navigateToFragment(new FragmentPageUserNews(), "UserNews");
        Toast.makeText(getContext(), "Aba em desenvolvimento...", Toast.LENGTH_SHORT).show();
    }

    private void logoutAccount() {
        FirebaseAuth.getInstance().signOut();
        viewLogD("LogoutUserAccount", "User logged in successfully");
        LoginStateStorage loginStateStorage = new LoginStateStorage(requireActivity());
        loginStateStorage.setLogin(false);

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void viewLogD(String tag, String message){
        Log.d(tag, message);
    }

    private void viewLogE(String tag, String message){
        Log.e(tag, message);
    }

}