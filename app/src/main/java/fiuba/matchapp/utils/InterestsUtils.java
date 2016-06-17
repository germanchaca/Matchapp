package fiuba.matchapp.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;

/**
 * Created by ger on 10/06/16.
 */
public class InterestsUtils {
    public static final String COMMON_INTERESTS = "common";
    public static final String MORE_INTERESTS = "more";

    @NonNull

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

    public static boolean interestsIsEmpty(List<Interest> data){
        if(data == null) return true;
        for(Interest interest : data){
            if (interest.isSelected()) return false;
        }
        return true;
    }

    public static Map<String, List<UserInterest>> getCommonInterests(List<UserInterest> interests1,List<UserInterest> interests2) {
        Map<String, List<UserInterest>> commonInterests = new HashMap<>();
        for (UserInterest i1 : interests1) {
            boolean added = false;
            for (UserInterest i2 : interests2) {
                if (TextUtils.equals(i1.getCategory(), i2.getCategory()) && TextUtils.equals(i1.getDescription(), i2.getDescription())) {
                    if (!commonInterests.containsKey(COMMON_INTERESTS)) {
                        List<UserInterest> list = new ArrayList<>();
                        list.add(i1);
                        commonInterests.put(COMMON_INTERESTS, list);
                    } else {
                        List<UserInterest> list = commonInterests.get(COMMON_INTERESTS);
                        list.add(i1);
                        commonInterests.put(COMMON_INTERESTS, list);
                    }
                    added = true;
                    break;
                }
            }
            if (!added) {
                if (!commonInterests.containsKey(MORE_INTERESTS)) {
                    List<UserInterest> list = new ArrayList<>();
                    list.add(i1);
                    commonInterests.put(MORE_INTERESTS, list);
                } else {
                    List<UserInterest> list = commonInterests.get(MORE_INTERESTS);
                    list.add(i1);
                    commonInterests.put(MORE_INTERESTS, list);
                }
            }
        }
        return commonInterests;
    }
}
