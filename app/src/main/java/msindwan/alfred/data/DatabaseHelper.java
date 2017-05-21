package msindwan.alfred.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import msindwan.alfred.data.schema.RequirementTable;
import msindwan.alfred.data.schema.StepTable;
import msindwan.alfred.data.schema.TutorialTable;
import msindwan.alfred.models.Requirement;
import msindwan.alfred.models.Step;
import msindwan.alfred.models.Tutorial;

/**
 * Created by Mayank Sindwani on 2017-05-21.
 *
 * DatabaseHelper:
 * A SQLite wrapper for the application database.
 */
@SuppressWarnings("WeakerAccess")
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alfred";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper m_instance;

    /**
     * Gets the singleton instance.
     * @param context the application context.
     *
     * @return the database instance.
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (m_instance == null) {
            m_instance = new DatabaseHelper(context.getApplicationContext());
        }
        return m_instance;
    }

    // Private constructor to prevent instance instantiation.
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TutorialTable.createTable(db);
        StepTable.createTable(db);
        RequirementTable.createTable(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Inserts the tutorial into the database.
     *
     * @param tutorial The tutorial to insert.
     */
    @SuppressWarnings("unused")
    public long insert(Tutorial tutorial) {
        SQLiteDatabase db = getWritableDatabase();

        if (tutorial.getId() != null) {
            throw new IllegalArgumentException("Tutorial ID already exists.");
        }

        ContentValues values = new ContentValues();
        values.put(TutorialTable.COL_NAME, tutorial.getName());
        values.put(TutorialTable.COL_DESCRIPTION, tutorial.getDescription());

        long id = db.insert(TutorialTable.TABLE_NAME, null, values);

        for(int i = 0; i < tutorial.getNumSteps(); i++) {
            Step step = tutorial.getStep(i);
            step.setTutorialId(id);
            insert(step);
        }

        return id;
    }

    /**
     * Inserts the step into the database.
     *
     * @param step The step to insert.
     */
    public long insert(Step step) {
        SQLiteDatabase db = getWritableDatabase();

        if (step.getId() != null) {
            throw new IllegalArgumentException("Step ID already exists.");
        }

        ContentValues values = new ContentValues();
        values.put(StepTable.COL_TITLE, step.getTitle());
        values.put(StepTable.COL_INSTRUCTIONS, step.getInstructions());
        values.put(StepTable.COL_TUTORIAL_ID, step.getTutorialId());
        values.put(StepTable.COL_INDEX, step.getIndex());

        long id = db.insert(StepTable.TABLE_NAME, null, values);

        for (int i = 0; i < step.getNumRequirements(); i++) {
            Requirement requirement = step.getRequirement(i);
            requirement.setStepId(id);
            insert(requirement);
        }

        return id;
    }

    /**
     * Inserts the requirement into the database.
     *
     * @param requirement The requirement to insert.
     */
    public long insert(Requirement requirement) {
        SQLiteDatabase db = getWritableDatabase();

        if (requirement.getId() != null) {
            throw new IllegalArgumentException("Requirement ID already exists.");
        }

        ContentValues values = new ContentValues();
        values.put(RequirementTable.COL_NAME, requirement.getName());
        values.put(RequirementTable.COL_AMOUNT, requirement.getAmount());
        values.put(RequirementTable.COL_UNIT, requirement.getUnit());
        values.put(RequirementTable.COL_STEP_ID, requirement.getStepId());
        values.put(RequirementTable.COL_OPTIONAL, requirement.isOptional() ? 1 : 0);

        return db.insert(RequirementTable.TABLE_NAME, null, values);
    }

}