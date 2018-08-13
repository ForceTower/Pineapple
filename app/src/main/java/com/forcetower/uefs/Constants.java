package com.forcetower.uefs;

import java.util.regex.Pattern;

/**
 * Created by João Paulo on 05/03/2018.
 */

public class Constants {
    public static final boolean DEBUG = false;
    public static final String DEFAULT_COURSE_IMAGE = "https://raw.githubusercontent.com/ForceTower/UNES_Files/master/UNES.png";
    public static final Pattern URL_PATTERN = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?)://((www)\\.)?)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    //Out date time (in seconds)
    public static final int OUTDATED_SECONDS = 60;
    //Worker name
    public static final String WORKER_SYNC_SAGRES_NAME =  "Sagres Synchronization Worker";
    public static final String WORKER_SYNC_SAGRES_UNIQUE = "sagres_sync_unique_name";
    //Sagres Default Pages
    public static final String SAGRES_SERVER_PAGE = "http://academico2.uefs.br/Portal";
    public static final String SAGRES_LOGIN_PAGE  = SAGRES_SERVER_PAGE + "/Acesso.aspx";
    public static final String SAGRES_GRADE_PAGE  = SAGRES_SERVER_PAGE + "/Modules/Diario/Aluno/Relatorio/Boletim.aspx";
    public static final String SAGRES_DIARY_PAGE  = SAGRES_SERVER_PAGE + "/Modules/Diario/Aluno/Default.aspx";
    public static final String SAGRES_CLASS_PAGE  = SAGRES_SERVER_PAGE + "/Modules/Diario/Aluno/Classe/ConsultaAulas.aspx";
    public static final String SAGRES_GRADE_ANY   = SAGRES_SERVER_PAGE + "/Modules/Diario/Aluno/Relatorio/Boletim.aspx?op=notas";
    public static final String SAGRES_ENROLL_CERT = SAGRES_SERVER_PAGE + "/Modules/Diario/Aluno/Relatorio/ComprovanteMatricula.aspx";
    public static final String SAGRES_FLOWCHART   = SAGRES_SERVER_PAGE + "/Modules/Diario/Aluno/Relatorio/FluxogramaAluno.aspx";
    public static final String SAGRES_HISTORY     = SAGRES_SERVER_PAGE + "/Modules/Diario/Aluno/Relatorio/HistoricoEscolar.aspx";
    //Sagres Default Value Constants
    public static final String LOGIN_VIEW_STATE = "/wEPDwUKMTc5MDkxMTc2NA9kFgJmD2QWBAIBD2QWDAIEDxYCHgRocmVmBT1+L0FwcF9UaGVtZXMvTmV3VGhlbWUvQWNlc3NvRXh0ZXJuby5jc3M/ZnA9NjM2Mzk4MDY3NDQwMDAwMDAwZAIFDxYCHwAFOH4vQXBwX1RoZW1lcy9OZXdUaGVtZS9Db250ZXVkby5jc3M/ZnA9NjM2Mzk4MDY3NDQwMDAwMDAwZAIGDxYCHwAFOX4vQXBwX1RoZW1lcy9OZXdUaGVtZS9Fc3RydXR1cmEuY3NzP2ZwPTYzNjIxNDcxMjMwMDAwMDAwMGQCBw8WAh8ABTl+L0FwcF9UaGVtZXMvTmV3VGhlbWUvTWVuc2FnZW5zLmNzcz9mcD02MzYyMTQ3MTIzMDAwMDAwMDBkAggPFgIfAAU2fi9BcHBfVGhlbWVzL05ld1RoZW1lL1BvcFVwcy5jc3M/ZnA9NjM2MjE0NzEyMzAwMDAwMDAwZAIJDxYCHwAFWC9Qb3J0YWwvUmVzb3VyY2VzL1N0eWxlcy9BcHBfVGhlbWVzL05ld1RoZW1lL05ld1RoZW1lMDEvZXN0aWxvLmNzcz9mcD02MzYxMDU4MjY2NDAwMDAwMDBkAgMPZBYEAgcPDxYEHgRUZXh0BQ1TYWdyZXMgUG9ydGFsHgdWaXNpYmxlaGRkAgsPZBYGAgEPDxYCHwJoZGQCAw88KwAKAQAPFgIeDVJlbWVtYmVyTWVTZXRoZGQCBQ9kFgICAg9kFgICAQ8WAh4LXyFJdGVtQ291bnRmZGTS+Y3bntF2UZMwIIXP8cpv13rKAw==";
    public static final String LOGIN_VW_STT_GEN = "BB137B96";
    public static final String LOGIN_VIEW_VALID = "/wEdAATbze7D9s63/L1c2atT93YlM4nqN81slLG8uEFL8sVLUjoauXZ8QTl2nEJmPx53FYhjUq3W1Gjeb7bKHHg4dlob4GWO7EiBlTRJt8Yw8hywpn30EZA=";
    //Default Background Image
    public static final String BACKGROUND_IMAGE_DEFAULT = "http://hdwpro.com/wp-content/uploads/2017/03/Art-Background-Image-1024x768.png";
    //Notification Groups
    public static final String CHANNEL_GROUP_MESSAGES_ID   = "com.forcetower.uefs.MESSAGES";
    public static final String CHANNEL_GROUP_GRADES_ID     = "com.forcetower.uefs.GRADES";
    public static final String CHANNEL_GROUP_GENERAL_ID    = "com.forcetower.uefs.GENERAL";
    public static final String CHANNEL_GROUP_EVENTS_ID     = "com.forcetower.uefs.EVENTS";
    //Notification Channels
    public static final String CHANNEL_MESSAGES_ID         = "com.forcetower.uefs.MESSAGES.POST";
    public static final String CHANNEL_MESSAGES_DCE_ID     = "com.forcetower.uefs.MESSAGES.DCE";
    public static final String CHANNEL_GRADES_POSTED_ID    = "com.forcetower.uefs.GRADES.POSTED";
    public static final String CHANNEL_GRADES_CREATED_ID   = "com.forcetower.uefs.GRADES.CREATE";
    public static final String CHANNEL_GRADES_CHANGED_ID   = "com.forcetower.uefs.GRADES.CHANGE";
    public static final String CHANNEL_GENERAL_WARNINGS_ID = "com.forcetower.uefs.GENERAL.WARNINGS";
    public static final String CHANNEL_GENERAL_REMOTE_ID   = "com.forcetower.uefs.GENERAL.REMOTE";
    public static final String CHANNEL_EVENTS_GENERAL_ID   = "com.forcetower.uefs.EVENTS.GENERAL";
    //Downloaded files names
    public static final String ENROLLMENT_CERTIFICATE_FILE_NAME = "enrollment_certificate.pdf";
    public static final String FLOWCHART_FILE_NAME = "flowchart.pdf";
    public static final String SCHOLAR_HISTORY_FILE_NAME = "school_history.pdf";
    //Service URL
    public static final String UNES_SERVICE_URL  = DEBUG ? "192.168.15.7" : "unes.herokuapp.com";
    public static final String UNES_SERVICE_BASE = "http" + (DEBUG ? "" : "s") + "://"+ UNES_SERVICE_URL + "/api/";

    public static final String WORKER_CREATE_EVENT = "create_event";
    public static final String WORKER_DOWNLOAD_GRADES = "download_semester_grades_";
    public static final String APPROVE_EVENT_WORKER = "approve_event_";
    public static final String WORKER_DOWNLOAD_GRADES_GENERAL = "download_semester_grades_general";
}
