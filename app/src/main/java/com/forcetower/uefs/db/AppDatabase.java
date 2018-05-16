package com.forcetower.uefs.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.forcetower.uefs.db.dao.AccessDao;
import com.forcetower.uefs.db.dao.CalendarEventDao;
import com.forcetower.uefs.db.dao.CalendarItemDao;
import com.forcetower.uefs.db.dao.DisciplineClassItemDao;
import com.forcetower.uefs.db.dao.DisciplineClassLocationDao;
import com.forcetower.uefs.db.dao.DisciplineClassMaterialLinkDao;
import com.forcetower.uefs.db.dao.DisciplineDao;
import com.forcetower.uefs.db.dao.DisciplineGroupDao;
import com.forcetower.uefs.db.dao.GradeDao;
import com.forcetower.uefs.db.dao.GradeInfoDao;
import com.forcetower.uefs.db.dao.GradeSectionDao;
import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.dao.ProfileDao;
import com.forcetower.uefs.db.dao.SemesterDao;
import com.forcetower.uefs.db.dao.TodoItemDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.CalendarEvent;
import com.forcetower.uefs.db.entity.CalendarItem;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineClassMaterialLink;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.db.entity.TodoItem;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Database(entities = {
        Access.class,
        CalendarItem.class,
        Semester.class,
        Discipline.class,
        Grade.class,
        GradeSection.class,
        GradeInfo.class,
        Message.class,
        DisciplineGroup.class,
        DisciplineClassItem.class,
        DisciplineClassLocation.class,
        DisciplineClassMaterialLink.class,
        TodoItem.class,
        Profile.class,
        CalendarEvent.class
}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AccessDao accessDao();
    public abstract CalendarItemDao calendarItemDao();
    public abstract SemesterDao semesterDao();
    public abstract DisciplineDao disciplineDao();
    public abstract GradeDao gradeDao();
    public abstract GradeSectionDao gradeSectionDao();
    public abstract GradeInfoDao gradeInfoDao();
    public abstract MessageDao messageDao();
    public abstract DisciplineGroupDao disciplineGroupDao();
    public abstract DisciplineClassItemDao disciplineClassItemDao();
    public abstract DisciplineClassLocationDao disciplineClassLocationDao();
    public abstract DisciplineClassMaterialLinkDao disciplineClassMaterialLinkDao();
    public abstract TodoItemDao todoItemDao();
    public abstract ProfileDao profileDao();
    public abstract CalendarEventDao calendarEventDao();
}
