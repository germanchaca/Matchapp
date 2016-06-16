package fiuba.matchapp.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.UserInterest;

/**
 * Created by ger on 10/06/16.
 */
public class InterestsUtils {   @NonNull

    public static Map<String, List<Interest>> getStringListMap(List<Interest> interests) {
    Map<String,List<Interest> > mapInterestsByCategory = new HashMap<>();
    for (Interest i : interests) {
        if(!mapInterestsByCategory.containsKey(i.getCategory())){
            List<Interest> list = new ArrayList<>();
            list.add(i);
            mapInterestsByCategory.put(i.getCategory(),list);
        }else {
            List<Interest> list = mapInterestsByCategory.get(i.getCategory());
            list.add(i);
            mapInterestsByCategory.put(i.getCategory(),list);
        }
    }
    return mapInterestsByCategory;
    }
    public static Map<String, List<UserInterest>> getStringUserInterestsListMap(List<UserInterest> interests) {
        Map<String,List<UserInterest> > mapInterestsByCategory = new HashMap<>();
        for (UserInterest i : interests) {
            if(!mapInterestsByCategory.containsKey(i.getCategory())){
                List<UserInterest> list = new ArrayList<>();
                list.add(i);
                mapInterestsByCategory.put(i.getCategory(),list);
            }else {
                List<UserInterest> list = mapInterestsByCategory.get(i.getCategory());
                list.add(i);
                mapInterestsByCategory.put(i.getCategory(),list);
            }
        }
        return mapInterestsByCategory;
    }
}
