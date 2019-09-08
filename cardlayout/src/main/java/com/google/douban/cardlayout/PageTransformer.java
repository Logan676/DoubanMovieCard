package com.google.douban.cardlayout;

import android.view.View;

/**
 * A PageTransformer is invoked whenever a visible/attached page is scrolled.
 * This offers an opportunity for the application to apply a custom transformation
 * to the page views using animation properties.
 *
 * <p>As property animation is only supported as of Android 3.0 and forward,
 * setting a PageTransformer on a ViewPager on earlier platform versions will
 * be ignored.</p>
 */
public interface PageTransformer {
    /**
     * Apply a property transformation to the given page.
     *
     * @param page     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center
     *                 position of the pager. 0 is front and center. 1 is one full
     *                 page position to the right, and -1 is one page position to the left.
     */
    public void transformPage(View page, float position);
}
