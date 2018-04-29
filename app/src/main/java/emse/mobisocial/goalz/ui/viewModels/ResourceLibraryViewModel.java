package emse.mobisocial.goalz.ui.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import emse.mobisocial.goalz.GoalzApp;
import emse.mobisocial.goalz.dal.IResourceRepository;
import emse.mobisocial.goalz.dal.repositories.ResourceRepository;
import emse.mobisocial.goalz.model.Resource;

public class ResourceLibraryViewModel extends AndroidViewModel {
    private IResourceRepository resourceRepository;
    public LiveData<List<Resource>> resources;

    public ResourceLibraryViewModel(@NonNull Application application) {
        super(application);
        resourceRepository = ((GoalzApp) application).getResourceRepository();
        resources = resourceRepository.getResources();
    }

    public LiveData<List<Resource>> getResourceLibrary(String userId) {
        if (resources == null) {
            resources = new MutableLiveData<List<Resource>>();
            loadResources(userId);
        }
        return resources;
    }

    private void loadResources(String userId) {
        LiveData<List<Resource>> resources = resourceRepository.getLibraryForUser(userId);
        //resources.setValue(meh.getValue());
    }
}
