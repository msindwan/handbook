/*
 * Created by Mayank Sindwani on 2017-05-25.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.views.common;

/**
 * EditFormView:
 * Defines the interface for forms.
 */
public interface EditFormView {
    /**
     * Validates the form.
     *
     * @return True if valid; false otherwise.
     */
    boolean validate();
}
