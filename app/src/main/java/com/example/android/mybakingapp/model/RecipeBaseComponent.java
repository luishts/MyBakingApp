package com.example.android.mybakingapp.model;

/**
 * Created by ltorres on 23/06/2017.
 */

public class RecipeBaseComponent {

    private String componentName;

    public RecipeBaseComponent(String componentName){
        this.componentName = componentName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
