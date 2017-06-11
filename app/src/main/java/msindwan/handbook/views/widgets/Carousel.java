/*
 * Copyright (C) 2017 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package msindwan.handbook.views.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import msindwan.handbook.R;
import msindwan.handbook.util.Support;

/**
 * Accordion:
 * Defines a custom accordion widget for android.
 */
public class Carousel extends LinearLayout {

    private ViewPager.OnPageChangeListener m_listener;
    private HorizontalScrollView m_scrollview;
    private LinearLayout m_indicators;
    private ViewPager m_pager;

    // Constructors.
    public Carousel(Context context) {
        super(context);
        init(context);
    }

    public Carousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Initializes the component on mount.
     *
     * @param context The initialization context.
     */
    private void init(Context context) {
        inflate(context, R.layout.carousel, this);
        m_pager = (ViewPager)findViewById(R.id.carousel_view_pager);
        m_indicators = (LinearLayout)findViewById(R.id.carousel_indicators);
        m_scrollview = (HorizontalScrollView)findViewById(R.id.carousel_indicators_scroll_view);
        m_pager.addOnPageChangeListener(onPageChangeListener);
    }

    /**
     * Sets the view pager adapter and updates carousel indicators.
     *
     * @param adapter The adapter to set.
     */
    public void setAdapter(PagerAdapter adapter) {
        m_pager.setAdapter(adapter);
        m_indicators.removeAllViews();

        // Create all of the indicators.
        for (int i = 0; i < adapter.getCount(); i++) {
            Button button = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
            LayoutParams params = new LayoutParams(20, 20);
            params.topMargin = 5;
            params.bottomMargin = 5;
            params.leftMargin = 5;
            params.rightMargin = 5;
            button.setLayoutParams(params);
            ViewCompat.setBackground(
                    button,
                    ContextCompat.getDrawable(getContext(), R.drawable.circle));
            button.setTag(i);
            button.setOnClickListener(onIndicatorClick);
            m_indicators.addView(button);
        }
        if (adapter.getCount() > 0) {
            setActiveIndicator(0);
        }
    }

    /**
     * Sets the view pager listener.
     *
     * @param listener The listener to set.
     */
    public void setViewPagerListener(ViewPager.OnPageChangeListener listener) {
        m_listener = listener;
    }

    /**
     * Gets the position of the active page.
     *
     * @return the position of the active page.
     */
    public int getActivePage() {
        return m_pager.getCurrentItem();
    }

    /**
     * Sets the margins for the indicator container.
     *
     * @param left the left margin.
     * @param top the top margin.
     * @param right the right margin.
     * @param bottom the bottom margin.
     */
    public void setIndicatorMargin(int left, int top, int right, int bottom) {
        LayoutParams p = (LayoutParams)m_scrollview.getLayoutParams();
        p.setMargins(left, top, right, bottom);
    }

    /**
     * Sets the specified indicator to an active state.
     *
     * @param position the position of the indicator to activate.
     */
    public void setActiveIndicator(int position) {
        for (int i = 0; i < m_indicators.getChildCount(); i++) {
            Button indicator = (Button)m_indicators.getChildAt(i);
            if (i == position) {
                Support.setButtonTint(
                    indicator,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.colorAccent)
                    )
                );
            } else {
                Support.setButtonTint(
                    indicator,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.colorGray)
                    )
                );
            }
        }
    }

    /**
     * Listener for indicator clicks.
     */
    private View.OnClickListener onIndicatorClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int)v.getTag();
            setActiveIndicator(position);
            m_pager.setCurrentItem(position);
        }
    };

    /**
     * Listener for view pager clicks.
     */
    private ViewPager.OnPageChangeListener onPageChangeListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (m_listener != null) {
                m_listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }
        @Override
        public void onPageSelected(int position) {
            setActiveIndicator(position);
            if (m_listener != null) {
                m_listener.onPageSelected(position);
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
            if (m_listener != null) {
                m_listener.onPageScrollStateChanged(state);
            }
        }
    };

}
