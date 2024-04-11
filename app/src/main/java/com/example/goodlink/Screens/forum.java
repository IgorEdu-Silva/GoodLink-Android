    package com.example.goodlink.Screens;

    import android.os.Bundle;
    import android.widget.SearchView;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.viewpager2.widget.ViewPager2;

    import com.example.goodlink.Fragments.FilterViewModel;
    import com.example.goodlink.Fragments.PagerAdapterFragments;
    import com.example.goodlink.R;
    import com.google.android.material.tabs.TabLayout;
    import com.google.android.material.tabs.TabLayoutMediator;

    public class forum extends AppCompatActivity {
        private boolean telaAtiva = true;
        private SearchView searchView;
        private FilterViewModel filterViewModel;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_forum);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            ViewPager2 viewPager = findViewById(R.id.viewPager);
            TabLayout tabLayout = findViewById(R.id.tabLayoutInfo);

            PagerAdapterFragments pagerAdapter = new PagerAdapterFragments(this);
            viewPager.setAdapter(pagerAdapter);

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setText("Playlists");
                                break;
                            case 1:
                                tab.setText("Formulário");
                                break;
                            case 2:
                                tab.setText("Usuários");
                                break;
                        }
                    }
            ).attach();

            filterViewModel = new ViewModelProvider(this).get(FilterViewModel.class);
            searchView = findViewById(R.id.searchView);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterViewModel.setFilterText(newText);
                    return true;
                }
            });

        }

        @Override
        public void onBackPressed() {
            if (telaAtiva) {
                return;
            }
            super.onBackPressed();
        }

        @Override
        protected void onResume() {
            super.onResume();
            telaAtiva = true;
        }

        @Override
        protected void onPause() {
            super.onPause();
            telaAtiva = false;
        }
    }