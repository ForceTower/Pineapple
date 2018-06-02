package com.forcetower.uefs.rep.sgrs;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.DisciplineClassItemDao;
import com.forcetower.uefs.db.dao.DisciplineClassMaterialLinkDao;
import com.forcetower.uefs.db.dao.DisciplineDao;
import com.forcetower.uefs.db.dao.DisciplineGroupDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.db.entity.DisciplineClassMaterialLink;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.resources.FetchAllDataResource;
import com.forcetower.uefs.rep.resources.FetchClassDetailsResource;
import com.forcetower.uefs.sgrs.SagresResponse;
import com.forcetower.uefs.sgrs.parsers.SagresDisciplineDetailsParser;
import com.forcetower.uefs.sgrs.parsers.SagresMaterialsParser;

import org.jsoup.nodes.Document;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

import static com.forcetower.uefs.rep.helper.RequestCreator.makeApprovalRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeApprovalRequestBody;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeFormBodyForClassDetails;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeFormBodyForDisciplineDetails;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeLoginRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makePostStudentPage;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestBody;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestClassDetails;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeStudentPageRequest;

/**
 * Created by João Paulo on 09/03/2018.
 */
@Singleton
public class DisciplinesRepository {
    private final AppExecutors executors;
    private final AppDatabase  database;
    private final OkHttpClient client;

    @Inject
    DisciplinesRepository(AppExecutors executors, AppDatabase database, OkHttpClient client) {
        this.executors = executors;
        this.database = database;
        this.client = client;
    }


    public MediatorLiveData<Resource<Integer>> getClassDetails(int groupId) {
        MediatorLiveData<Resource<Integer>> result = new MediatorLiveData<>();

        LiveData<Access> acsSrc = database.accessDao().getAccess();
        result.addSource(acsSrc, access -> {
            result.removeSource(acsSrc);
            if (access == null) {
                result.postValue(Resource.error("You are disconnected", 401, R.string.disconnected));
            } else {
                String username = access.getUsername();
                String password = access.getPassword();
                LiveData<Resource<Document>> initialSrc = createDocumentSource(username, password);
                result.addSource(initialSrc, documentResource -> {
                    //noinspection ConstantConditions
                    if (documentResource.status == Status.SUCCESS) {
                        result.removeSource(initialSrc);
                        executors.diskIO().execute(() -> {
                            Timber.d("Initial Group id: %d", groupId);
                            DisciplineGroup dGrp = database.disciplineGroupDao().getDisciplineGroupByIdDirect(groupId);
                            Discipline discipline = database.disciplineDao().getDisciplinesByIdDirect(dGrp.getDiscipline());
                            String semester = discipline.getSemester();
                            String code = discipline.getCode();
                            String group = dGrp.getGroup();
                            Timber.d("Found values: %s %s %s", semester, code, group);
                            phase2(result, semester, code, group, documentResource.data);
                        });
                    } else if(documentResource.status == Status.ERROR) {
                        result.removeSource(initialSrc);
                        result.postValue(Resource.error(documentResource.message, documentResource.code, R.string.failed_to_connect));
                    }
                });
            }
        });

        return result;
    }

    private void phase2(MediatorLiveData<Resource<Integer>> result, String semester, String code, String group, Document document) {
        Timber.d("Phase 2 is running");
        LiveData<Resource<Integer>> fetch = new FetchClassDetailsResource(executors, semester, code, group, document) {

            @Override
            protected List<DisciplineClassItem> saveResult(@NonNull Document document) {
                document.charset(Charset.forName("ISO-8859-1"));
                DisciplineGroup disciplineGroup = SagresDisciplineDetailsParser.parseDisciplineGroup(document);
                int groupId = defineGroup(disciplineGroup).intValue();
                if (groupId < 0) {
                    Timber.d("Failed on insertion because of reasons...");
                    return null;
                } else {
                    List<DisciplineClassItem> items = SagresDisciplineDetailsParser.parseDisciplineClassItems(document);
                    Timber.d("Group id %d will receive %d class items", groupId, items.size());
                    defineItems(items, groupId);
                    return items;
                }
            }

            @Override
            protected Call createCallPreConnect(@NonNull FormBody.Builder builder) {
                Timber.d("Making request for BIT CONNECT! (Pre connect)");
                Request request = makePostStudentPage(builder);
                Timber.d("Returning to the fetcher");
                return client.newCall(request);
            }

            @Override
            public Call makeFinalConnectCall(@NonNull Document document) {
                Timber.d("Making request for class details");
                document.charset(Charset.forName("ISO-8859-1"));
                FormBody.Builder builder = makeFormBodyForClassDetails(document);
                Request request = makeRequestClassDetails(builder);
                return client.newCall(request);
            }

            @Override
            protected void extractMaterials(@NonNull Document document, int classId) {
                document.charset(Charset.forName("ISO-8859-1"));
                List<DisciplineClassMaterialLink> materials = SagresMaterialsParser.getMaterials(document, classId);
                defineMaterials(materials);
            }

            @Override
            protected Call createMaterialsCall(@NonNull Document document, @NonNull String encoded, int classId) {
                Timber.d("Making request for material download");
                FormBody.Builder builder = makeFormBodyForDisciplineDetails(document, encoded);
                Request request = makeRequestClassDetails(builder);
                return client.newCall(request);
            }
        }.asLiveData();

        result.addSource(fetch, resource -> {
            //noinspection ConstantConditions
            if (resource.status == Status.SUCCESS) {
                Timber.d("Success");
                result.removeSource(fetch);
                result.postValue(resource);
            } else if (resource.status == Status.ERROR) {
                Timber.d("Failed");
                result.removeSource(fetch);
                result.postValue(resource);
            } else {
                Timber.d("Loading");
                result.postValue(resource);
            }
        });
    }

    private LiveData<Resource<Document>> createDocumentSource(String username, String password) {
        MediatorLiveData<Resource<Document>> docSrc = new MediatorLiveData<>();
        LiveData<Resource<Integer>> loginSrc = new FetchAllDataResource(executors) {
            @Override
            protected void initialPage(Document data) {}

            @Override
            public Call createCall() {
                Timber.d("Call in disciplines");
                RequestBody body = makeRequestBody(username, password);
                Request request = makeLoginRequest(body);
                return client.newCall(request);
            }

            @Override
            public Call approvalCall(SagresResponse sgrResponse) {
                Timber.d("Approval call in disciplines");
                Response response = sgrResponse.getResponse();
                Document document = sgrResponse.getDocument();
                String url = response.request().url().url().getHost() + response.request().url().url().getPath();

                RequestBody body = makeApprovalRequestBody(document);
                Request request = makeApprovalRequest(url, body);
                return client.newCall(request);
            }

            @Override
            public Call createStudentPageCall() {
                Timber.d("Creating Student Page Call");
                Request request = makeStudentPageRequest();
                return client.newCall(request);
            }

            @Override
            public void saveResult(@NonNull Document document) {
                docSrc.postValue(Resource.success(document));
            }
        }.asLiveData();

        docSrc.addSource(loginSrc, resource -> {
            //noinspection ConstantConditions
            if (resource.status == Status.ERROR) {
                docSrc.removeSource(loginSrc);
                docSrc.postValue(Resource.error(resource.message, resource.code, (Throwable)null));
            } else if (resource.status == Status.SUCCESS) {
                docSrc.removeSource(loginSrc);
            }
        });

        return docSrc;
    }

    private void defineItems(List<DisciplineClassItem> items, int groupId) {
        DisciplineClassItemDao itemDao = database.disciplineClassItemDao();
        if (items.size() == 0) {
            Timber.d("This is probably a parse fail, so nothing will be changed :)");
            return;
        } else {
            //List<DisciplineClassItem> insertion = new ArrayList<>();
            for (DisciplineClassItem item : items) {
                if (item.getNumber() == -1) {
                    Timber.d("This is a parse error class number, it will be skipped, subject: %s", item.getSubject());
                    continue;
                }

                item.setGroupId(groupId);
                DisciplineClassItem current = itemDao.getItemFromGroupAndNumberDirect(groupId, item.getNumber());
                if (current == null) {
                    current = item;
                    Timber.d("Item %d will be created", item.getNumber());
                    Long uid = itemDao.insertClassItem(current);
                    item.setUid(uid.intValue());
                } else {
                    Timber.d("Item %d will be updated", item.getNumber());
                    current.selectiveCopy(item);
                    Long uid = itemDao.insertClassItem(current);
                    item.setUid(uid.intValue());
                }
                //insertion.add(current);
            }
            /*
            DisciplineClassItem[] insert = new DisciplineClassItem[insertion.size()];
            insertion.toArray(insert);
            itemDao.insertClassItem(insert);
            */
        }
    }

    private void defineMaterials(List<DisciplineClassMaterialLink> materials) {
        DisciplineClassMaterialLinkDao materialLinkDao = database.disciplineClassMaterialLinkDao();
        List<DisciplineClassMaterialLink> insertion = new ArrayList<>();

        for (DisciplineClassMaterialLink link : materials) {
            DisciplineClassMaterialLink current;
            List<DisciplineClassMaterialLink> currentList = materialLinkDao.getMaterialsFromClassDirect(link.getClassId());
            if (currentList.contains(link)) {
                Timber.d("Link %s - %s will be updated", link.getName(), link.getLink());
                current = currentList.get(currentList.indexOf(link));
                current.selectiveCopy(link);
            } else {
                Timber.d("Link %s - %s will be created", link.getName(), link.getLink());
                current = link;
            }

            insertion.add(current);
        }

        DisciplineClassMaterialLink[] insert = new DisciplineClassMaterialLink[insertion.size()];
        insertion.toArray(insert);
        materialLinkDao.insert(insert);
        Timber.d("Links inserted");
    }

    private Long defineGroup(DisciplineGroup created) {
        if (created == null) {
            return -2L;
        }

        DisciplineDao disciplineDao = database.disciplineDao();
        DisciplineGroupDao groupDao = database.disciplineGroupDao();

        String code = created.getCode();
        String smst = created.getSemester();
        String clgr = created.getGroup();
        if (code == null || smst == null) {
            Timber.d("Parse failed for created group");
            return -1L;
        }

        Timber.d("Created data: %s %s %s", smst, code, clgr);
        Timber.d("These are the groups: %s", groupDao.getAllDisciplineGroupsDirect());
        Discipline discipline = disciplineDao.getDisciplinesBySemesterAndCodeDirect(smst, code);

        List<DisciplineGroup> grps = groupDao.getDisciplineGroupsDirect(discipline.getUid());

        DisciplineGroup current;

        if (grps.size() == 1) {
            Timber.d("Only one group... :)");
            current = grps.get(0);
        } else if (clgr != null) {
            current = groupDao.getDisciplineGroupByDisciplineIdAndGroupName(discipline.getUid(), clgr);
            Timber.d("it's not null group, means there's more than 1, so response is true, right? %s ", current != null);
        } else {
            List<DisciplineGroup> groups = groupDao.getDisciplineGroupsDirect(discipline.getUid());
            Timber.d("Group is null, meaning there is only one... Right? %d", groups.size());
            if (groups.size() > 0) {
                current = groups.get(0);
                Timber.d("Got the single one and move on...");
            } else {
                Timber.d("It seems like i was wrong... l u l");
                return -1L;
            }
        }



        if (current == null) {
            Timber.d("A group didn't exist before... Thinking...");
            current = created;
        } else {
            Timber.d("Updating existing group...");
            current.selectiveCopy(created);
        }

        current.setDraft(false);
        Timber.d("Group id for update: %d", current.getUid());
        return groupDao.insertDisciplineGroup(current);
    }

    public void ignoreGroup(int groupId) {
        executors.others().execute(() -> database.disciplineGroupDao().ignoreGroup(groupId));
    }

    public void restoreGroup(int groupId) {
        executors.others().execute(() -> database.disciplineGroupDao().restoreGroup(groupId));
    }

    public LiveData<List<DisciplineClassItem>> getClassesWithMaterials(int groupId) {
        MediatorLiveData<List<DisciplineClassItem>> result = new MediatorLiveData<>();
        LiveData<List<DisciplineClassItem>> classesSrc = database.disciplineClassItemDao().getDisciplineClassItemsFromGroup(groupId);
        result.addSource(classesSrc, classesList -> {
            result.removeSource(classesSrc);
            executors.diskIO().execute(() -> {
                //noinspection ConstantConditions
                for (DisciplineClassItem item : classesList) {
                    List<DisciplineClassMaterialLink> materials = database.disciplineClassMaterialLinkDao().getMaterialsFromClassDirect(item.getUid());
                    item.setMaterials(materials);
                }
                result.postValue(classesList);
            });
        });
        return result;
    }
}
