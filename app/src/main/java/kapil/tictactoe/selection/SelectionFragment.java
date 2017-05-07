package kapil.tictactoe.selection;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import kapil.tictactoe.R;

/**
 * This is a fragment for a single selection screen.
 */

public class SelectionFragment extends Fragment implements View.OnClickListener {
    private static final String TITLE_TEXT = "TITLE_TEXT";
    private static final String TOP_BUTTON_IMG_RES_ID = "TOP_BUTTON_IMG_RES_ID";
    private static final String BOTTOM_BUTTON_IMG_RES_ID = "BOTTOM_BUTTON_IMG_RES_ID";

    static final int TOP_BUTTON = 0;
    static final int BOTTOM_BUTTON = 1;

    @IntDef({TOP_BUTTON, BOTTOM_BUTTON})
    @interface ButtonType {

    }

    private String titleText;
    private int topButtonImageResourceId;
    private int bottomButtonImageResourceId;

    private OnValueSelectedListener onValueSelectedListener;

    private TextView title;
    private SqueezeButton topButton;
    private SqueezeButton bottomButton;

    private boolean transitionAnimationEnabled;

    public SelectionFragment() {

    }

    public static SelectionFragment newInstance(String titleText, int topButtonImageResourceId, int bottomButtonImageResourceId) {
        SelectionFragment fragment = new SelectionFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_TEXT, titleText);
        args.putInt(TOP_BUTTON_IMG_RES_ID, topButtonImageResourceId);
        args.putInt(BOTTOM_BUTTON_IMG_RES_ID, bottomButtonImageResourceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onValueSelectedListener = (OnValueSelectedListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            titleText = getArguments().getString(TITLE_TEXT);
            topButtonImageResourceId = getArguments().getInt(TOP_BUTTON_IMG_RES_ID);
            bottomButtonImageResourceId = getArguments().getInt(BOTTOM_BUTTON_IMG_RES_ID);
        }

        transitionAnimationEnabled = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
        setViews();
    }

    private void initViews() {
        title = (TextView) getView().findViewById(R.id.title);
        topButton = (SqueezeButton) getView().findViewById(R.id.top_button);
        bottomButton = (SqueezeButton) getView().findViewById(R.id.bottom_button);

        topButton.setOnClickListener(this);
        bottomButton.setOnClickListener(this);
    }

    private void setViews() {
        title.setText(titleText);
        topButton.setBackgroundResource(topButtonImageResourceId);
        bottomButton.setBackgroundResource(bottomButtonImageResourceId);
    }

    @Override
    public void onClick(View v) {
        if (onValueSelectedListener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.top_button:
                onValueSelectedListener.onValueSelected(this, TOP_BUTTON);
                break;
            case R.id.bottom_button:
                onValueSelectedListener.onValueSelected(this, BOTTOM_BUTTON);
                break;
        }
    }

    public boolean isTransitionAnimationEnabled() {
        return transitionAnimationEnabled;
    }

    public void setTransitionAnimationEnabled(boolean transitionAnimationEnabled) {
        this.transitionAnimationEnabled = transitionAnimationEnabled;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (isTransitionAnimationEnabled()) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        } else {
            Animation animation = new Animation() {
            };
            animation.setDuration(0);
            return animation;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onValueSelectedListener = null;
    }

    interface OnValueSelectedListener {
        void onValueSelected(Fragment fragment, @ButtonType int buttonType);
    }
}
