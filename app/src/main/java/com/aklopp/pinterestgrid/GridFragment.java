package com.aklopp.pinterestgrid;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A fragment containing the grid and related settings.
 * Created by Allison on 6/2/2015.
 */
public class GridFragment extends Fragment {
    private static final String ADDR_PREFIX = "http://widgets.pinterest.com/v3/pidgets/users/";
    private static final String ADRR_POSTFIX = "/pins/";

    /**
     * The username entered
     */
    private String mUserId = "";
    /**
     * The current URL containing the username
     */
    private String mCurrentURL = "";


    public GridFragment() {
        // intentionally blank
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        RelativeLayout focusedView = (RelativeLayout) rootView.findViewById(R.id.focused_view);
        GridView pinGrid = (GridView) rootView.findViewById(R.id.pin_grid);

        final GridViewAdapter gridAdapter = new GridViewAdapter(getActivity(),R.layout.grid_item, focusedView);
        pinGrid.setAdapter(gridAdapter);

        final EditText userIdField = (EditText) rootView.findViewById(R.id.user_id);
        putCursorAtEnd(userIdField);

        final Button reloadButton = (Button) rootView.findViewById(R.id.reload_button);

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserId = userIdField.getText().toString();
                setCurrentURL();

                putCursorAtEnd(userIdField);
                hideSoftKeyboard(userIdField);

                // reset the grid
                gridAdapter.clear();
                new PopulatePinsAsyncTask(getActivity(), gridAdapter).execute(mCurrentURL);

            }
        });

        userIdField.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_NULL
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    reloadButton.callOnClick();
                    return true;
                } else {
                    return false;
                }
            }
        });

        return rootView;
    }

    /**
     * Concatenate the URL from which to get the JSON.
     */
    private void setCurrentURL()
    {
        mCurrentURL = ADDR_PREFIX + mUserId + ADRR_POSTFIX;
    }

    /**
     * Hide the on-screen keyboard.
     * @param view
     */
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Place the cursor at the end of the given EditText.
     * @param editText
     */
    public void putCursorAtEnd(EditText editText)
    {
        int textLength = editText.getText().length();
        editText.setSelection(textLength, textLength);
    }
}
