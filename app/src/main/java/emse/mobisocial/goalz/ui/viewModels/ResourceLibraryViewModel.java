package emse.mobisocial.goalz.ui.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import emse.mobisocial.goalz.GoalzApp;
import emse.mobisocial.goalz.dal.IRecommendationRepository;
import emse.mobisocial.goalz.dal.IResourceRepository;
import emse.mobisocial.goalz.dal.repositories.ResourceRepository;
import emse.mobisocial.goalz.model.RecommendationTemplate;
import emse.mobisocial.goalz.model.Resource;

public class ResourceLibraryViewModel extends AndroidViewModel {
    private IResourceRepository resourceRepository;
    public LiveData<List<Resource>> resources;
    private IRecommendationRepository recommendationRepository;

    public ResourceLibraryViewModel(@NonNull Application application) {
        super(application);
        resourceRepository = ((GoalzApp) application).getResourceRepository();
       // resources = resourceRepository.getResources();
    }

    public void getResourceLibrary(String userId) {
        resources = resourceRepository.getLibraryForUser(userId);
    }


    @NotNull
    public final LiveData deleteResource(String userId, String resourceId) {
        return resourceRepository.deleteResourceFromLibrary(userId, resourceId);
    }

    @NotNull
    public final LiveData addResourceToYourGoal(RecommendationTemplate template) {
        return recommendationRepository.addRecommendation(template);
    }
}
