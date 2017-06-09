/*
 * Created by Mayank Sindwani on 2017-05-15.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.widgets;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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

import msindwan.handbook.R;

/**
 * Accordion:
 * Defines a custom accordion widget for android.
 */
public class Accordion extends LinearLayout {

    /**
     * AccordionListener:
     * Defines the interface for listening to accordion actions.
     */
    public interface AccordionListener {
        /**
         * Listener for header click events.
         *
         * @param panel The panel that was clicked.
         * @return True if the default behaviour should persist; false otherwise.
         */
        boolean onHeaderClick(Panel panel);

        /**
         * Returns the view item for a panel given the accordion position.
         *
         * @param position The accordion position.
         * @return The view to render.
         */
        View getItem(int position);

        /**
         * Callback for when a panel is expanded.
         *
         * @param position The position of the panel that was expanded.
         */
        void onPanelExpanded(int position);
    }

    /**
     * Panel:
     * Defines a panel view for the accordion.
     */
    public class Panel extends RelativeLayout {

        // View components.
        private LinearLayout m_panelLayout;
        private LinearLayout m_panelHeader;
        private TextView m_title;
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
            m_height = -1;

            // Listen for global layout changes. Once the panel is rendered,
            // determine the measured height and hide the layout if it isn't
            // active.
            final LinearLayout panelLayout = getLayout();
            ViewTreeObserver viewTreeObserver = panelLayout.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    m_height = panelLayout.getMeasuredHeight();

                    if (!m_active) {
                        panelLayout.removeAllViews();
                    } else {
                        m_listener.onPanelExpanded(getPanelIndex(Panel.this));
                    }

                    // Remove the listener.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        panelLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        //noinspection deprecation
                        panelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
         * Returns the panel view added to the layout.
         *
         * @return the panel view.
         */
        public View getPanelView() {
            return m_panelLayout.getChildAt(0);
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
            if (m_height > 0) {
                m_height = m_panelLayout.getHeight();
            }
        }

        /**
         * Returns a value animator to collapse the specified panel.
         *
         * @return the value animator
         */
        private ValueAnimator collapse() {
            final LinearLayout panelLayout = getLayout();
            deactivate();

            ValueAnimator hideLayout = ValueAnimator
                    .ofInt(getPanelHeight(), 0)
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
         * @return the value animator
         */
        private ValueAnimator expand() {
            final LinearLayout panelLayout = getLayout();
            activate();

            ValueAnimator showNewLayout = ValueAnimator
                    .ofInt(0, getPanelHeight())
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

    }

    /**
     * PanelAnimator:
     * Defines an animator for accordion panels.
     */
    private class PanelAnimator implements Animator.AnimatorListener {

        private Panel m_closedPanel;
        private Panel m_openedPanel;

        private PanelAnimator(Panel closedPanel, Panel openedPanel) {
            super();
            m_closedPanel = closedPanel;
            m_openedPanel = openedPanel;
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
            if (m_closedPanel != null) {
                m_closedPanel.getLayout().removeAllViews();
            }
            if (m_openedPanel != null) {
                m_openedPanel.getLayout().setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                m_listener.onPanelExpanded(getPanelIndex(m_openedPanel));
            }
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

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super_instance_state", super.onSaveInstanceState());
        bundle.putInt("active_panel", m_activePanel);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle)state;
        setActivePanel(bundle.getInt("active_panel", 0));
        state = bundle.getParcelable("super_instance_state");
        super.onRestoreInstanceState(state);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(@SuppressWarnings("UnusedParameters") Context context) {
        setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        setOrientation(LinearLayout.VERTICAL);
        m_activePanel = 0;
    }

    /**
     * Getter for the size of the panels container.
     *
     * @return The number of panels present in the accordion.
     */
    public int getNumPanels() {
        return getChildCount();
    }

    /**
     * Adds a panel to the collection of accordion panels.
     *
     * @return the newly created panel.
     */
    public Panel addPanel() {
        // Create a new panel instance.
        final Panel panel = new Panel(getContext());
        LinearLayout panelHeader = panel.getHeader();

        // Bind click handler to toggle the panel.
        panelHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_listener == null || m_listener.onHeaderClick(panel)) {
                    setActivePanel(getPanelIndex(panel));
                }
            }
        });

        // Add the panel.
        addView(panel, new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

        if (m_listener != null) {
            panel.getLayout().addView(m_listener.getItem(getNumPanels() - 1));
        }

        if (getNumPanels() - 1 != m_activePanel) {
            // Deactivate and hide the panel.
            panel.deactivate();
            panel.setPadding(10, 0, 10, 10);
        } else {
            // The first panel is activated by default.
            panel.activate();
            panel.setPadding(10, 10, 10, 10);
        }

        return panel;
    }

    /**
     * Gets a panel by its index.
     *
     * @param index The index of the panel to get.
     * @return Returns the panel at the specified index.
     */
    public Panel getPanel(int index) {
        return (Panel)getChildAt(index);
    }

    /**
     * Returns the index of the panel provided.
     *
     * @param panel The panel to search for.
     * @return The index of the panel (-1 if not found).
     */
    public int getPanelIndex(Panel panel) {
        return indexOfChild(panel);
    }

    /**
     * Removes the specified panel.
     *
     * @param index The index of the panel to remove.
     */
    public void removePanel(int index) {
        if (getNumPanels() <= 1)
            return;

        // Remove the panel references.
        removeView(getPanel(index));

        // Update the active panel.
        if (index == m_activePanel) {
            // Expand the new active panel.
            m_activePanel = Math.max(index - 1, 0);
            Panel panelToOpen = getPanel(m_activePanel);

            if (m_listener != null) {
                panelToOpen.getLayout().addView(m_listener.getItem(m_activePanel));
            }

            AnimatorSet set = createSet(null, panelToOpen);
            set.playTogether(panelToOpen.expand());
            set.start();
        }
    }

    /**
     * Sets the active accordion panel to the view specified.
     *
     * @param panelIndex The index of the accordion panel to activate.
     */
    public void setActivePanel(final int panelIndex) {
        Panel activePanel = getPanel(m_activePanel);
        Panel panel = getPanel(panelIndex);

        // No sense in toggling the same panel.
        if (activePanel == panel)
            return;

        m_activePanel = getPanelIndex(panel);

        // If the height hasn't been calculated for either panel, use "activate" or
        // "deactivate" rather than collapsing the panels with animations that rely
        // on the height.
        if (activePanel.getPanelHeight() == -1 || panel.getPanelHeight() == -1) {

            if (activePanel.getPanelHeight() == -1 && panel.getPanelHeight() == -1) {
                // Both heights haven't been computed yet, so deactivate and activate them.
                activePanel.deactivate();
                panel.activate();
            } else if (activePanel.getPanelHeight() == -1) {
                // Deactivate the active panel and expand the new panel.
                activePanel.deactivate();
                AnimatorSet set = createSet(null, panel);
                set.playTogether(panel.expand());
                set.start();
            } else {
                // Activate the new panel and collapse the old panel.
                panel.activate();
                AnimatorSet set = createSet(activePanel, null);
                set.playTogether(activePanel.collapse());
                set.start();
            }
            return;
        }

        if (m_listener != null) {
            panel.getLayout().addView(m_listener.getItem(m_activePanel));
        }

        // Play the animations together.
        AnimatorSet set = createSet(activePanel, panel);
        set.playTogether(activePanel.collapse(), panel.expand());
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

    /**
     * Moves a panel to the specified index.
     *
     * @param oldPosition The index of the panel to move.
     * @param newPosition The index to move it to.
     */
    public void movePanel(int oldPosition, int newPosition) {
        if (m_activePanel == oldPosition) {
            m_activePanel = newPosition;
        }
        Panel panel = getPanel(oldPosition);
        removeView(panel);
        addView(panel, newPosition);
    }

    /**
     * Create an animator set for the specified panel.
     *
     * @param panelToClose the panel to collapse.
     * @param panelToOpen the panel to open.
     * @return The animator set.
     */
    private AnimatorSet createSet(Panel panelToClose, Panel panelToOpen) {
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new PanelAnimator(panelToClose, panelToOpen));
        return set;
    }

    /**
     * Sets the accordion listener for the instance.
     *
     * @param listener The listener to set.
     */
    public void setAccordionListener(AccordionListener listener) {
        m_listener = listener;
    }

}
