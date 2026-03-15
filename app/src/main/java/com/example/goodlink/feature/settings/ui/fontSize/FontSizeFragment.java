package com.example.goodlink.feature.settings.ui.fontSize;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.goodlink.R;
import com.example.goodlink.core.domain.settings.FontSettingsGateway;
import com.example.goodlink.core.domain.usecase.GetFontSizeUseCase;
import com.example.goodlink.core.domain.usecase.SetFontSizeUseCase;
import com.example.goodlink.infrastructure.session.FontSettingsRepositoryImplementation;
import static com.example.goodlink.core.domain.settings.FontSizeLimits.*;


public class FontSizeFragment extends Fragment {
    private TextView fontSizeLetter;
    private SeekBar chanceFontSizeLetter;
    private GetFontSizeUseCase getFontSizeUseCase;
    private SetFontSizeUseCase setFontSizeUseCase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FontSettingsGateway gateway =
                new FontSettingsRepositoryImplementation(requireContext());

        getFontSizeUseCase = new GetFontSizeUseCase(gateway);
        setFontSizeUseCase = new SetFontSizeUseCase(gateway);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_font_size, container, false);
        chanceFontSizeLetter = view.findViewById(R.id.chanceConstrastScreen);
        fontSizeLetter = view.findViewById(R.id.fontSizeLetter);

        float fontSize = getFontSizeUseCase.execute();

        final float[] lastSp = {fontSize};

        applyFont(fontSizeLetter, fontSize);

        chanceFontSizeLetter.setProgress((int) ((fontSize - MIN_SP) * 100f / (MAX_SP - MIN_SP))        );

        chanceFontSizeLetter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int prog, boolean fromUser) {
                float sp = MIN_SP + (MAX_SP - MIN_SP) * (prog / 100f);
                lastSp[0] = sp;
                applyFont(fontSizeLetter, sp);
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {
                setFontSizeUseCase.execute(lastSp[0]);
            }
        });

        return view;
    }

    private void applyFont(TextView view, float size) {
        view.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, size);
    }


}