package msindwan.alfred.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    // Table projections.
    private static final  String[] TUTORIALS_PROJECTION = new String[] {
        TutorialTable.COL_ID,
        TutorialTable.COL_NAME,
        TutorialTable.COL_DESCRIPTION,
        TutorialTable.COL_NUM_VIEWS
    };
    private static final String[] STEPS_PROJECTION = new String[] {
        StepTable.COL_ID,
        StepTable.COL_TITLE,
        StepTable.COL_INSTRUCTIONS,
        StepTable.COL_TUTORIAL_ID,
        StepTable.COL_INDEX
    };
    private static final String[] REQUIREMENTS_PROJECTION = new String[] {
        RequirementTable.COL_ID,
        RequirementTable.COL_NAME,
        RequirementTable.COL_AMOUNT,
        RequirementTable.COL_UNIT,
        RequirementTable.COL_OPTIONAL,
        RequirementTable.COL_STEP_ID
    };

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
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
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
     * Fetches the tutorial with the given id.
     *
     * @param tutorial The object to store the tutorial data in memory.
     * @param id The id of the tutorial to fetch.
     * @return true if the tutorial was found; false otherwise.
     */
    public boolean fetch(Tutorial tutorial, long id) {
        SQLiteDatabase db = getWritableDatabase();

        // Query for the specified tutorial.
        Cursor cTutorial = db.query(
                TutorialTable.TABLE_NAME,
                TUTORIALS_PROJECTION,
                String.format("%s = ?", TutorialTable.COL_ID),
                new String[] { Long.toString(id) },
                null,
                null,
                null
        );

        if(cTutorial == null)
            return false;

        if (!cTutorial.moveToFirst()) {
            cTutorial.close();
            return false;
        }

        read(tutorial, cTutorial);

        // Fetch all of its steps.
        Cursor cSteps = db.query(
                StepTable.TABLE_NAME,
                STEPS_PROJECTION,
                String.format("%s = ?", StepTable.COL_TUTORIAL_ID),
                new String[]{Long.toString(id)},
                null,
                null,
                StepTable.COL_INDEX
        );

        if (cSteps != null) {
            while (cSteps.moveToNext()) {
                // Create and read each step.
                Step step = new Step();
                step.setTutorialId(id);
                read(step, cSteps);
                tutorial.addStep(step);

                // Fetch all of the step requirements.
                Cursor cRequirements = db.query(
                        RequirementTable.TABLE_NAME,
                        REQUIREMENTS_PROJECTION,
                        String.format("%s = ?", RequirementTable.COL_STEP_ID),
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        null
                );

                if (cRequirements != null) {
                    while (cRequirements.moveToNext()) {
                        // Create and read each requirement.
                        Requirement requirement = new Requirement();
                        read(requirement, cRequirements);
                        step.addRequirement(requirement);
                    }
                    cRequirements.close();
                }
            }
            cSteps.close();
        }
        cTutorial.close();
        return true;
    }

    /**
     * Inserts the tutorial into the database.
     *
     * @param tutorial The tutorial to insert.
     */
    public Long insert(Tutorial tutorial) {
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
    public Long insert(Step step) {
        SQLiteDatabase db = getWritableDatabase();

        if (step.getId() != null) {
            throw new IllegalArgumentException("Step ID already exists.");
        }

        ContentValues values = new ContentValues();
        values.put(StepTable.COL_TITLE, step.getTitle());
        values.put(StepTable.COL_INSTRUCTIONS, step.getInstructions());
        values.put(StepTable.COL_TUTORIAL_ID, step.getTutorialId());
        values.put(StepTable.COL_INDEX, step.getIndex());

        long id  = db.insert(StepTable.TABLE_NAME, null, values);

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

    /**
     * Updates the existing tutorial.
     *
     * @param tutorial The tutorial to update.
     */
    public void update(Tutorial tutorial) {
        SQLiteDatabase db = getWritableDatabase();

        if (tutorial.getId() == null) {
            throw new IllegalArgumentException("Tutorial ID not set.");
        }

        ContentValues values = new ContentValues();
        values.put(TutorialTable.COL_NAME, tutorial.getName());
        values.put(TutorialTable.COL_DESCRIPTION, tutorial.getDescription());
        // TODO: Add updated time.

        db.update(
                TutorialTable.TABLE_NAME,
                values,
                String.format("%s = ?", TutorialTable.COL_ID),
                new String[] {
                    Long.toString(tutorial.getId())
                }
        );

        for (int i = 0; i < tutorial.getNumSteps(); i++) {
            Step step = tutorial.getStep(i);
            step.setTutorialId(tutorial.getId());

            if (step.getId() == null) {
                // The step does not exist, so insert it.
                insert(step);
            } else if (step.isDeleted()) {
                // The step is marked for deletion.
                delete(step);
            } else {
                // Update the existing step.
                update(step);
            }
        }
    }

    /**
     * Updates the existing step.
     *
     * @param step The tutorial step to update.
     */
    public void update(Step step) {
        SQLiteDatabase db = getWritableDatabase();

        if (step.getId() == null) {
            throw new IllegalArgumentException("Step ID not set.");
        }

        ContentValues values = new ContentValues();
        values.put(StepTable.COL_TITLE, step.getTitle());
        values.put(StepTable.COL_INSTRUCTIONS, step.getInstructions());
        values.put(StepTable.COL_TUTORIAL_ID, step.getTutorialId());
        values.put(StepTable.COL_INDEX, step.getIndex());

        String whereClause = String.format("%s = ?", StepTable.COL_ID);
        String[] whereArgs = new String[] {
                Long.toString(step.getId())
        };

        db.update(
                StepTable.TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );

        for (int i = 0; i < step.getNumRequirements(); i++) {
            Requirement requirement = step.getRequirement(i);
            requirement.setStepId(step.getId());

            if (requirement.getId() == null) {
                // The requirement does not exist, so insert it.
                insert(requirement);
            } else if (requirement.isDeleted()) {
                // The requirement is marked for deletion.
                delete(requirement);
            } else {
                // Update the existing requirement.
                update(requirement);
            }
        }
    }

    /**
     * Updates the existing requirement.
     *
     * @param requirement The requirement to update.
     */
    public void update(Requirement requirement) {
        SQLiteDatabase db = getWritableDatabase();

        if (requirement.getId() == null) {
            throw new IllegalArgumentException("Requirement ID not set.");
        }

        ContentValues values = new ContentValues();
        values.put(RequirementTable.COL_NAME, requirement.getName());
        values.put(RequirementTable.COL_AMOUNT, requirement.getAmount());
        values.put(RequirementTable.COL_UNIT, requirement.getUnit());
        values.put(RequirementTable.COL_STEP_ID, requirement.getStepId());
        values.put(RequirementTable.COL_OPTIONAL, requirement.isOptional() ? 1 : 0);

        db.update(
            RequirementTable.TABLE_NAME,
            values,
                String.format("%s = ?", RequirementTable.COL_ID),
            new String[] {
                Long.toString(requirement.getId())
            }
        );
    }

    /**
     * Deletes the existing tutorial.
     *
     * @param tutorial The tutorial to delete.
     */
    public void delete(Tutorial tutorial) {
        SQLiteDatabase db = getWritableDatabase();

        if (tutorial.getId() == null) {
            throw new IllegalArgumentException("Tutorial ID not set.");
        }

        db.delete(
            TutorialTable.TABLE_NAME,
            String.format("%s = ?", TutorialTable.COL_ID),
            new String[] {
                Long.toString(tutorial.getId())
            }
        );
    }

    /**
     * Deletes the existing step.
     *
     * @param step The step to delete.
     */
    public void delete(Step step) {
        SQLiteDatabase db = getWritableDatabase();

        if (step.getId() == null) {
            throw new IllegalArgumentException("Step ID not set.");
        }

        db.delete(
            StepTable.TABLE_NAME,
            String.format("%s = ?", StepTable.COL_ID),
            new String[] {
                Long.toString(step.getId())
            }
        );
    }

    /**
     * Deletes the existing requirement.
     *
     * @param requirement The requirement to delete.
     */
    public void delete(Requirement requirement) {
        SQLiteDatabase db = getWritableDatabase();

        if (requirement.getId() == null) {
            throw new IllegalArgumentException("Requirement ID not set.");
        }

        db.delete(
            RequirementTable.TABLE_NAME,
            String.format("%s = ?", RequirementTable.COL_ID),
            new String[] {
                Long.toString(requirement.getId())
            }
        );
    }

    /**
     * Reads the provided cursor into the requirement.
     *
     * @param tutorial The tutorial to read into.
     */
    public void read(Tutorial tutorial, Cursor cursor) {
        tutorial.setId(
                cursor.getLong(cursor.getColumnIndex(TutorialTable.COL_ID)));
        tutorial.setName(
                cursor.getString(cursor.getColumnIndex(TutorialTable.COL_NAME)));
        tutorial.setDescription(
                cursor.getString(cursor.getColumnIndex(TutorialTable.COL_DESCRIPTION)));
        tutorial.setNumViews(
                cursor.getInt(cursor.getColumnIndex(TutorialTable.COL_NUM_VIEWS)));
    }

    /**
     * Reads the provided cursor into the step.
     *
     * @param step The step to read into.
     */
    public void read(Step step, Cursor cursor) {
        step.setId(
                cursor.getLong(cursor.getColumnIndex(StepTable.COL_ID)));
        step.setTitle(
                cursor.getString(cursor.getColumnIndex(StepTable.COL_TITLE)));
        step.setInstructions(
                cursor.getString(cursor.getColumnIndex(StepTable.COL_INSTRUCTIONS)));
        step.setIndex(
                cursor.getLong(cursor.getColumnIndex(StepTable.COL_INDEX)));
    }

    /**
     * Reads the provided cursor into the requirement.
     *
     * @param requirement The requirement to read into.
     */
    public void read(Requirement requirement, Cursor cursor) {
        requirement.setId(
                cursor.getLong(cursor.getColumnIndex(RequirementTable.COL_ID)));
        requirement.setName(
                cursor.getString(cursor.getColumnIndex(RequirementTable.COL_NAME)));
        requirement.setAmount(
                cursor.getDouble(cursor.getColumnIndex(RequirementTable.COL_AMOUNT)));
        requirement.setUnit(
                cursor.getString(cursor.getColumnIndex(RequirementTable.COL_UNIT)));
        requirement.setOptional(
                cursor.getInt(cursor.getColumnIndex(RequirementTable.COL_OPTIONAL)) == 1);
    }
}
