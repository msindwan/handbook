package msindwan.handbook.views.widgets;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import msindwan.handbook.R;

/**
 * Created by Mayank Sindwani on 2017-05-15.
 *
 * Accordion:
 * Defines a custom accordion widget for android.
 */
public class Accordion extends LinearLayout {

    /**
     * AccordionListener:
     * Defines the interface for listening to accordion actions.
     */
    public interface AccordionListener {
        boolean onHeaderClick(Panel panel);
    }

    /**
     * Panel:
     * Defines a panel view for the accordion.
     */
    public static class Panel extends RelativeLayout {

        // TODO: For completeness, make colour properties configurable.
        // View components.
        private LinearLayout m_panelLayout;
        private LinearLayout m_panelHeader;
        private TextView m_title;
        private View m_panelView;
        private boolean m_active;
        private int m_height;

        // Constructors.
        public Panel(Context context) {
            super(context);
            m_active = false;
            init(context);
        }

        /**
         * Initializes the component on mount.
         *
         * @param context The initialization context.
         */
        private void init(Context context) {
            inflate(context, R.layout.accordion_panel, this);
            m_panelLayout = (LinearLayout)findViewById(R.id.accordion_panel);
            m_panelHeader = (LinearLayout)findViewById(R.id.accordion_panel_header);
            m_title = (TextView)findViewById(R.id.accordion_panel_header_title);
            m_panelView = null;

            // Listen for global layout changes. Once the panel is rendered,
            // determine the measured height and hide the layout if it isn't
            // active.
            ViewTreeObserver viewTreeObserver = m_panelLayout.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    m_height = m_panelLayout.getMeasuredHeight();
                    if (!m_active) {
                        m_panelLayout.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0
                            )
                        );
                    }
                    // Remove the listener.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        m_panelLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        //noinspection deprecation
                        m_panelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });

        }

        /**
         * Getter for the layout of the panel.
         */
        public LinearLayout getLayout() {
            return m_panelLayout;
        }

        /**
         * Getter for the header of the panel.
         */
        public LinearLayout getHeader() {
            return m_panelHeader;
        }

        /**
         * Sets the panel layout child container to the view specified.
         *
         * @param view The view to set as the child container for the panel
         */
        public void setPanelView(View view) {
            // Add the specified view to the panel layout.
            getLayout().addView(view, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            m_panelView = view;
        }

        /**
         * Getter for the panel child container.
         *
         * @return The panel child container.
         */
        public View getPanelView() {
            return m_panelView;
        }

        /**
         * Sets the title for the panel.
         *
         * @param title the title to set.
         */
        public void setTitle(String title) {
            m_title.setText(title);
        }

        /**
         * Sets the title for the panel.
         *
         * @return the panel title.
         */
        public String getTitle() {
            return m_title.getText().toString();
        }

        /**
         * Getter for the panel layout height.
         *
         * @return the panel layout height.
         */
        public final int getPanelHeight() {
            return m_height;
        }

        /**
         * Activates the panel.
         */
        private void activate() {
            ImageView arrow = (ImageView)m_panelHeader.findViewById(R.id.accordion_panel_arrow);
            arrow.setColorFilter(Color.WHITE);
            arrow.animate().rotation(0).start();

            m_title.setTextColor(Color.WHITE);
            m_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            m_panelHeader.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.colorSecondaryAccent)
            );

            m_active = true;
        }

        /**
         * Deactivates the panel.
         */
        private void deactivate() {
            ImageView arrow = (ImageView)m_panelHeader.findViewById(R.id.accordion_panel_arrow);
            arrow.setColorFilter(Color.BLACK);
            arrow.animate().rotation(-90).start();

            m_title.setTextColor(Color.BLACK);
            m_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            m_panelHeader.setBackgroundColor(Color.WHITE);

            m_active = false;
            m_height = m_panelLayout.getHeight();
        }

    }

    /**
     * PanelAnimator:
     * Defines an animator for accordion panels.
     */
    private class PanelAnimator implements Animator.AnimatorListener {

        private Panel m_panel;

        private PanelAnimator(Panel panel) {
            super();
            m_panel = panel;
        }

        private void enableTouch() {
            // Enable touch events.
            Activity context = (Activity)getContext();
            context.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            );
        }
        @Override
        public void onAnimationStart(Animator animation) {
            // Disable touch events until the animation completes.
            Activity context = (Activity)getContext();
            context.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            );
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            // Re-enable touch events.
            m_panel.getLayout().setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            enableTouch();
        }
        @Override
        public void onAnimationCancel(Animator animation) {
            // Re-enable touch events so we're never stuck in a
            // state without user interaction.
            enableTouch();
        }
        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

    // View components.
    private AccordionListener m_listener;
    private ArrayList<Panel> m_panels;
    private int m_activePanel;

    // Constructors.
    public Accordion(Context context) {
        super(context);
        init(context);
    }

    public Accordion(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(@SuppressWarnings("UnusedParameters") Context context) {
        m_panels = new ArrayList<>();
        setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * Getter for the size of the panels array.
     *
     * @return The number of panels present in the accordion.
     */
    public int getNumPanels() {
        return m_panels.size();
    }

    /**
     * Adds a panel to the collection of accordion panels.
     *
     * @param panel The panel to add.
     */
    public void addPanel(final Panel panel) {
        // Create a new panel instance.
        LinearLayout panelHeader = panel.getHeader();

        // Bind click handler to toggle the panel.
        panelHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_listener == null || m_listener.onHeaderClick(panel)) {
                    setActivePanel(panel);
                }
            }
        });

        if (!m_panels.isEmpty()) {
            // Deactivate and hide the panel.
            panel.deactivate();
            panel.setPadding(10, 0, 10, 10);
        } else {
            // The first panel is activated by default.
            panel.activate();
            m_activePanel = 0;
            panel.setPadding(10, 10, 10, 10);
        }

        // Add the panel.
        m_panels.add(panel);
        addView(panel, new RelativeLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * Gets a panel by its index.
     *
     * @param index The index of the panel to get.
     * @return Returns the panel at the specified index.
     */
    public Panel getPanel(int index) {
        return m_panels.get(index);
    }

    /**
     * Returns the index of the panel provided.
     *
     * @param panel The panel to search for.
     * @return The index of the panel (-1 if not found).
     */
    public int getPanelIndex(Panel panel) {
        return m_panels.indexOf(panel);
    }

    /**
     * Removes the specified panel.
     *
     * @param panel The panel to remove.
     */
    public void removePanel(Panel panel) {
        if (m_panels.size() <= 1)
            return;

        int panelIndex = m_panels.indexOf(panel);

        // Remove the panel references.
        m_panels.remove(panel);
        removeView(panel);

        // Update the active panel.
        if (panelIndex == m_activePanel) {
            // Expand the new active panel.
            m_activePanel = Math.max(panelIndex - 1, 0);

            AnimatorSet set = createSet(panel);
            set.playTogether(expand(getPanel(m_activePanel)));
            set.start();
        }
    }

    /**
     * Create an animator set for the specified panel.
     *
     * @param panel the panel to create a animator set for.
     * @return The animator set.
     */
    private AnimatorSet createSet(Panel panel) {
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new PanelAnimator(panel));
        return set;
    }

    /**
     * Sets the active accordion panel to the view specified.
     *
     * @param panel The accordion panel to activate.
     */
    public void setActivePanel(final Panel panel) {
        Panel activePanel = getPanel(m_activePanel);

        // No sense in toggling the same panel.
        if (activePanel == panel)
            return;

        m_activePanel = m_panels.indexOf(panel);

        // Play the animations together.
        AnimatorSet set = createSet(panel);
        set.playTogether(collapse(activePanel), expand(panel));
        set.start();
    }

    /**
     * Getter for the active panel's index.
     *
     * @return The active panel's index.
     */
    public int getActivePanel() {
        return m_activePanel;
    }

    public void movePanel(Panel panel, int index) {
        int panelIndex = getPanelIndex(panel);
        if (m_activePanel == panelIndex) {
            m_activePanel = index;
        }
        m_panels.set(panelIndex, m_panels.get(index));
        m_panels.set(index, panel);
        removeView(panel);
        addView(panel, index);
    }

    /**
     * Returns a value animator to collapse the specified panel.
     *
     * @param panel the panel to collapse.
     * @return the value animator
     */
    private ValueAnimator collapse(Panel panel) {
        final LinearLayout panelLayout = panel.getLayout();
        panel.deactivate();

        ValueAnimator hideLayout = ValueAnimator
                .ofInt(panel.getPanelHeight(), 0)
                .setDuration(400);

        // Animate collapsing the active tab.
        hideLayout.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                panelLayout.getLayoutParams().height = (Integer)animation.getAnimatedValue();
                panelLayout.requestLayout();
            }
        });

        return hideLayout;
    }

    /**
     * Returns a value animator to expand the specified panel.
     *
     * @param panel the panel to expand.
     * @return the value animator
     */
    private ValueAnimator expand(Panel panel) {
        final LinearLayout panelLayout = panel.getLayout();
        panel.activate();

        ValueAnimator showNewLayout = ValueAnimator
                .ofInt(0, panel.getPanelHeight())
                .setDuration(400);

        // Animate expanding the new tab.
        showNewLayout.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                panelLayout.getLayoutParams().height = (Integer)animation.getAnimatedValue();
                panelLayout.requestLayout();
            }
        });

        return showNewLayout;
    }

    public void setAccordionListener(AccordionListener listener) {
        m_listener = listener;
    }

}
