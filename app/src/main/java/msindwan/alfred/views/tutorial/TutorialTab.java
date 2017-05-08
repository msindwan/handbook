package msindwan.alfred.views.tutorial;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import msindwan.alfred.R;

/**
 * Created by Mayank Sindwani on 2017-05-06.
 *
 * TutorialTab:
 * Defines a view component for tutorial tabs.
 */
public class TutorialTab extends RelativeLayout {

    // View components.
    private ImageButton m_tabRemoveButton;
    private Drawable m_tabBackground;
    private Button m_tabButton;

    // Constructors.
    public TutorialTab(Context context) {
        super(context);
        init(context);
    }

    public TutorialTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // Initializes the component.
    private void init(Context context) {
        inflate(context, R.layout.tutorial_tutorial_tab, this);
        m_tabRemoveButton = (ImageButton)findViewById(R.id.tutorial_remove_tab);
        m_tabBackground = getResources().getDrawable(R.drawable.circle);
        m_tabButton = (Button)findViewById(R.id.tutorial_tab);
    }

    // Setter for the tab text.
    public void setText(String text) { m_tabButton.setText(text); }

    // Setter for the tab background colour.
    public void setBackgroundColour(int colour) {
        m_tabBackground.setColorFilter(getResources().getColor(colour), PorterDuff.Mode.SRC_IN);
        m_tabButton.setBackgroundDrawable(m_tabBackground);
    }

    // Registers the click listener for the tab button.
    public void setButtonOnClickListener(View.OnClickListener listener) {
        m_tabButton.setOnClickListener(listener);
    }

    // Registers the click listener for the remove button.
    public void setRemoveOnClickListener(View.OnClickListener listener) {
        m_tabRemoveButton.setOnClickListener(listener);
    }
}
