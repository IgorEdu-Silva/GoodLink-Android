package com.example.goodlink.feature.forum.ui;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.goodlink.R;
import com.example.goodlink.core.domain.auth.AuthState;
import com.example.goodlink.core.domain.forum.policy.ForumAccessPolicy;
import com.example.goodlink.feature.forum.ui.adapter.AdapterPagerForum;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public final class ForumUiBinder {

    public interface UiActions {
        void openFormDialog();
    }

    public static final int TAB_HOME = 0;
    public static final int TAB_SETTINGS = 1;

    private final AppCompatActivity activity;
    private final UiActions actions;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    public ForumUiBinder(AppCompatActivity activity, UiActions actions) {
        this.activity = activity;
        this.actions = actions;
    }

    public void bind(AuthState authState) {
        viewPager = activity.findViewById(R.id.repositoriesView);
        tabLayout = activity.findViewById(R.id.tabLayoutInfo);
        fab = activity.findViewById(R.id.floatingBtnPageCentral);

        viewPager.setAdapter(new AdapterPagerForum(activity));

        fab.setEnabled(ForumAccessPolicy.canUseFab(authState));
        viewPager.setUserInputEnabled(ForumAccessPolicy.canSwipePages(authState));

        disableRestrictedTabIfNeeded(authState);
        attachTabs();

        fab.setOnClickListener(v -> actions.openFormDialog());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                super.onPageSelected(position);
                handlePageSelected(position, authState);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void disableRestrictedTabIfNeeded(AuthState authState) {
        if (authState == null || !authState.isAnonymous()) return;

        TabLayout.Tab restrictedTab = tabLayout.getTabAt(TAB_SETTINGS);
        if (restrictedTab != null && restrictedTab.view != null) {
            restrictedTab.view.setEnabled(false);
            restrictedTab.view.setAlpha(0.4f);
            restrictedTab.view.setOnTouchListener((v, event) -> true);
        }
    }

    private void attachTabs() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == TAB_HOME) tab.setIcon(R.drawable.btn_home);
            else if (position == TAB_SETTINGS) tab.setIcon(R.drawable.btn_settings);
        }).attach();
    }

    private void handlePageSelected(int position, AuthState authState) {
        if (!ForumAccessPolicy.canAccessTab(position, authState)) {
            viewPager.setCurrentItem(TAB_HOME, false);
            Toast.makeText(activity,
                    "Esta aba está desabilitada para acesso anônimo",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        fab.setVisibility(position == TAB_SETTINGS ? View.GONE : View.VISIBLE);

        View container = activity.findViewById(R.id.containerAccount);
        if (container != null) container.setVisibility(View.INVISIBLE);

        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
