package msindwan.alfred.views.common;

/**
 * Created by Mayank Sindwani on 2017-05-25.
 *
 * FormView:
 * Defines the interface for forms.
 */
public interface FormView {
    /**
     * Validates the form.
     *
     * @return True if valid; false otherwise.
     */
    boolean validate();
}
