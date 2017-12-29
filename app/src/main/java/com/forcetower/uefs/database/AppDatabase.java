package com.forcetower.uefs.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.forcetower.uefs.database.dao.AAccessDao;
import com.forcetower.uefs.database.dao.ACalendarItemDao;
import com.forcetower.uefs.database.dao.ADisciplineDao;
import com.forcetower.uefs.database.dao.ADisciplineGroupDao;
import com.forcetower.uefs.database.dao.AGradeDao;
import com.forcetower.uefs.database.dao.AGradeInfoDao;
import com.forcetower.uefs.database.dao.AGradeSectionDao;
import com.forcetower.uefs.database.dao.AScrapDao;
import com.forcetower.uefs.database.dao.ASemesterDao;
import com.forcetower.uefs.database.entities.AAccess;
import com.forcetower.uefs.database.entities.ACalendarItem;
import com.forcetower.uefs.database.entities.ADiscipline;
import com.forcetower.uefs.database.entities.ADisciplineGroup;
import com.forcetower.uefs.database.entities.AGrade;
import com.forcetower.uefs.database.entities.AGradeInfo;
import com.forcetower.uefs.database.entities.AGradeSection;
import com.forcetower.uefs.database.entities.AScrap;
import com.forcetower.uefs.database.entities.ASemester;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Database(entities = {
        AAccess.class,
        ACalendarItem.class,
        ASemester.class,
        ADiscipline.class,
        AGrade.class,
        AGradeSection.class,
        AGradeInfo.class,
        AScrap.class,
        ADisciplineGroup.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AAccessDao aAccessDao();
    public abstract ACalendarItemDao aCalendarItemDao();
    public abstract ASemesterDao aSemesterDao();
    public abstract ADisciplineDao aDisciplineDao();
    public abstract AGradeDao aGradeDao();
    public abstract AGradeSectionDao aGradeSectionDao();
    public abstract AGradeInfoDao aGradeInfoDao();
    public abstract AScrapDao aScrapDao();
    public abstract ADisciplineGroupDao aDisciplineGroupDao();
}
