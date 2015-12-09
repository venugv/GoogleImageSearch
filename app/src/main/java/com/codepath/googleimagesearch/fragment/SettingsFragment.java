package com.codepath.googleimagesearch.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.activity.ImageSearchActivity;
import com.codepath.googleimagesearch.data.SearchFilter;

/**
 * Created by vvenkatraman on 12/3/15.
 */
public class SettingsFragment extends DialogFragment {
    Spinner sizeSpinner;
    Spinner colorSpinner;
    Spinner typeSpinner;
    EditText locationFilterValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container);
        getDialog().setTitle(R.string.settingsDialogTitle);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        this.sizeSpinner = (Spinner) view.findViewById(R.id.spSize);
        this.colorSpinner = (Spinner) view.findViewById(R.id.spColor);
        this.typeSpinner = (Spinner) view.findViewById(R.id.spType);
        this.locationFilterValue = (EditText) view.findViewById(R.id.etLocationFilter);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButonclick(v);
            }
        });
        if (getArguments() != null) {
            SearchFilter filter = getArguments().getParcelable(ImageSearchActivity.SEARCH_FILTER);
            if (filter == null) {
                return;
            }
            this.sizeSpinner.setSelection(((ArrayAdapter) sizeSpinner.getAdapter()).getPosition(filter.getImageSize()));
            this.colorSpinner.setSelection(((ArrayAdapter) colorSpinner.getAdapter()).getPosition(filter.getImageColor()));
            this.typeSpinner.setSelection(((ArrayAdapter) typeSpinner.getAdapter()).getPosition(filter.getImageType()));
            this.locationFilterValue.setText(filter.getImageLocation());
        }
    }

    public void onSaveButonclick(View v) {
        String imageSize = (String) sizeSpinner.getSelectedItem();
        String imageColor = (String) colorSpinner.getSelectedItem();
        String imageType = (String) typeSpinner.getSelectedItem();
        String imageLocation = locationFilterValue.getText().toString();

        ImageSearchActivity activity = (ImageSearchActivity) getActivity();
        SearchFilter filter = new SearchFilter(imageSize, imageColor, imageType, imageLocation);
        activity.setSearchFilter(filter);
        this.dismiss();
    }
}
