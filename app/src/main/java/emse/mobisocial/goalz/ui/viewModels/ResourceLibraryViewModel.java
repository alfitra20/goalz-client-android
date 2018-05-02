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
import emse.mobisocial.goalz.dal.IGoalRepository;
import emse.mobisocial.goalz.dal.IRecommendationRepository;
import emse.mobisocial.goalz.dal.IResourceRepository;
import emse.mobisocial.goalz.dal.repositories.ResourceRepository;
import emse.mobisocial.goalz.model.Goal;
import emse.mobisocial.goalz.model.RecommendationTemplate;
import emse.mobisocial.goalz.model.Resource;

public class ResourceLibraryViewModel extends AndroidViewModel {
    private IResourceRepository resourceRepository;
    private IGoalRepository goalRepository;
    private IRecommendationRepository recommendationRepository;
    public LiveData<List<Resource>> resources;
    public LiveData<List<Goal>> goals;

    public ResourceLibraryViewModel(@NonNull Application application) {
        super(application);
        resourceRepository = ((GoalzApp) application).getResourceRepository();
        goalRepository = ((GoalzApp) application).getGoalRepository();
        recommendationRepository = ((GoalzApp) application).getRecommendationRepository();
    }

    public void getResourceLibrary(String userId) {
        resources = resourceRepository.getLibraryForUser(userId);
        goals = goalRepository.getGoalsForUser(userId);
    }

    public final LiveData deleteResource(String userId, String resourceId) {
        return resourceRepository.deleteResourceFromLibrary(userId, resourceId);
    }

    public final LiveData addRecommendation(RecommendationTemplate recommendationTemplate) {
        return recommendationRepository.addRecommendation(recommendationTemplate);
    }
}
