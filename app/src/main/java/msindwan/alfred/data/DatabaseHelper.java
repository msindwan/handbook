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
    public Long insert(Tutorial tutorial) {
        SQLiteDatabase db = getWritableDatabase();

        if (tutorial.getId() != null) {
            throw new IllegalArgumentException("Tutorial ID already exists.");
        }

        Boolean inTransaction = db.inTransaction();

        ContentValues values = new ContentValues();
        values.put(TutorialTable.COL_NAME, tutorial.getName());
        values.put(TutorialTable.COL_DESCRIPTION, tutorial.getDescription());

        if (!inTransaction) {
            db.beginTransaction();
        }

        Long id = null;

        try {
            id = db.insert(TutorialTable.TABLE_NAME, null, values);

            for(int i = 0; i < tutorial.getNumSteps(); i++) {
                Step step = tutorial.getStep(i);
                step.setTutorialId(id);
                insert(step);
            }
            if (!inTransaction) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!inTransaction) {
                db.endTransaction();
            }
        }

        return id;
    }

    /**
     * Inserts the step into the database.
     *
     * @param step The step to insert.
     */
    private Long insert(Step step) {
        SQLiteDatabase db = getWritableDatabase();

        if (step.getId() != null) {
            throw new IllegalArgumentException("Step ID already exists.");
        }

        Boolean inTransaction = db.inTransaction();

        ContentValues values = new ContentValues();
        values.put(StepTable.COL_TITLE, step.getTitle());
        values.put(StepTable.COL_INSTRUCTIONS, step.getInstructions());
        values.put(StepTable.COL_TUTORIAL_ID, step.getTutorialId());
        values.put(StepTable.COL_INDEX, step.getIndex());

        Long id = null;

        try {
            id = db.insert(StepTable.TABLE_NAME, null, values);

            for (int i = 0; i < step.getNumRequirements(); i++) {
                Requirement requirement = step.getRequirement(i);
                requirement.setStepId(id);
                insert(requirement);
            }
            if (!inTransaction) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!inTransaction) {
                db.endTransaction();
            }
        }

        return id;
    }

    /**
     * Inserts the requirement into the database.
     *
     * @param requirement The requirement to insert.
     */
    private long insert(Requirement requirement) {
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
     * Fetches the tutorial with the given id.
     *
     * @param tutorial The object to store the tutorial data in memory.
     * @param id The id of the tutorial to fetch.
     * @return true if the tutorial was found; false otherwise.
     */
    public boolean fetch(Tutorial tutorial, long id) {
        SQLiteDatabase db = getWritableDatabase();

        String[] tutorials_projection = new String[] {
            TutorialTable.COL_ID,
            TutorialTable.COL_NAME,
            TutorialTable.COL_DESCRIPTION,
            TutorialTable.COL_NUM_VIEWS
        };

        String whereClause = String.format("%s = ?", TutorialTable.COL_ID);
        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                TutorialTable.TABLE_NAME,
                tutorials_projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                tutorial.setId(
                    cursor.getLong(cursor.getColumnIndex(TutorialTable.COL_ID)));
                tutorial.setName(
                    cursor.getString(cursor.getColumnIndex(TutorialTable.COL_NAME)));
                tutorial.setDescription(
                    cursor.getString(cursor.getColumnIndex(TutorialTable.COL_DESCRIPTION)));
                tutorial.setNumViews(
                    cursor.getInt(cursor.getColumnIndex(TutorialTable.COL_NUM_VIEWS)));

                fetchTutorialSteps(tutorial, id);
                cursor.close();
                return true;
            }
            cursor.close();
        }

        return false;
    }

    private boolean fetchTutorialSteps(Tutorial tutorial, long id) {
        SQLiteDatabase db = getWritableDatabase();

        String[] steps_projection = new String[] {
                StepTable.COL_ID,
                StepTable.COL_TITLE,
                StepTable.COL_INSTRUCTIONS,
                StepTable.COL_TUTORIAL_ID,
                StepTable.COL_INDEX
        };

        String whereClause = String.format("%s = ?", StepTable.COL_TUTORIAL_ID);
        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                StepTable.TABLE_NAME,
                steps_projection,
                whereClause,
                whereArgs,
                null,
                null,
                StepTable.COL_INDEX
        );

        if(cursor != null) {

            if (cursor.getCount() == 0)
                return false;

            while(cursor.moveToNext()) {
                Step step = new Step();

                step.setId(
                    cursor.getLong(cursor.getColumnIndex(StepTable.COL_ID)));
                step.setTitle(
                    cursor.getString(cursor.getColumnIndex(StepTable.COL_TITLE)));
                step.setInstructions(
                    cursor.getString(cursor.getColumnIndex(StepTable.COL_INSTRUCTIONS)));
                step.setIndex(
                    cursor.getLong(cursor.getColumnIndex(StepTable.COL_INDEX)));

                step.setTutorialId(id);
                fetchStepRequirements(step, step.getId());
                tutorial.addStep(step);
            }
            cursor.close();
            return true;
        }

        return false;
    }

    private boolean fetchStepRequirements(Step step, long id) {
        SQLiteDatabase db = getWritableDatabase();

        String[] requirements_projection = new String[] {
                RequirementTable.COL_ID,
                RequirementTable.COL_NAME,
                RequirementTable.COL_AMOUNT,
                RequirementTable.COL_UNIT,
                RequirementTable.COL_OPTIONAL,
                RequirementTable.COL_STEP_ID
        };

        String whereClause = String.format("%s = ?", RequirementTable.COL_STEP_ID);
        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                RequirementTable.TABLE_NAME,
                requirements_projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        if(cursor != null) {
            if (cursor.getCount() == 0)
                return false;

            while(cursor.moveToNext()) {
                Requirement requirement = new Requirement();

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

                step.addRequirement(requirement);
            }
            cursor.close();
            return true;
        }

        return false;
    }

    public void update(Tutorial tutorial) {
        SQLiteDatabase db = getWritableDatabase();

        if (tutorial.getId() == null) {
            throw new IllegalArgumentException("Tutorial ID not set.");
        }

        ContentValues values = new ContentValues();
        values.put(TutorialTable.COL_NAME, tutorial.getName());
        values.put(TutorialTable.COL_DESCRIPTION, tutorial.getDescription());
        // TODO: Add updated time.

        String whereClause = String.format("%s = ?", TutorialTable.COL_ID);
        String[] whereArgs = new String[] {
                Long.toString(tutorial.getId())
        };

        db.update(
                TutorialTable.TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );

        for (int i = 0; i < tutorial.getNumSteps(); i++) {
            Step step = tutorial.getStep(i);
            step.setTutorialId(tutorial.getId());

            if (step.getId() == null) {
                insert(step);
            } else if (step.isDeleted()) {
                delete(step);
            } else {
                update(step);
            }
        }
    }

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
                insert(requirement);
            } else if (step.isDeleted()) {
                delete(requirement);
            } else {
                update(requirement);
            }
        }
    }

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

        String whereClause = String.format("%s = ?", RequirementTable.COL_ID);
        String[] whereArgs = new String[] {
                Long.toString(requirement.getId())
        };

        db.update(
                RequirementTable.TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );
    }

    public void delete(Step step) {
        SQLiteDatabase db = getWritableDatabase();

        if (step.getId() == null) {
            throw new IllegalArgumentException("Step ID not set.");
        }

        String whereClause = String.format("%s = ?", StepTable.COL_ID);
        String[] whereArgs = new String[] {
                Long.toString(step.getId())
        };

        db.delete(
                StepTable.TABLE_NAME,
                whereClause,
                whereArgs
        );
    }

    public void delete(Requirement requirement) {
        SQLiteDatabase db = getWritableDatabase();

        if (requirement.getId() == null) {
            throw new IllegalArgumentException("Requirement ID not set.");
        }

        String whereClause = String.format("%s = ?", RequirementTable.COL_ID);
        String[] whereArgs = new String[] {
                Long.toString(requirement.getId())
        };

        db.delete(
                RequirementTable.TABLE_NAME,
                whereClause,
                whereArgs
        );
    }
}
