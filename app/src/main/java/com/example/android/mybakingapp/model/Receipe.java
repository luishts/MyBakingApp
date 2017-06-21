package com.example.android.mybakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ltorres on 21/06/2017.
 */

public class Receipe implements Parcelable {

    private long id;
    private String name;
    private Ingredient[] ingredients;
    private Step[] steps;
    private int servings;
    private String image;

    public Receipe() {

    }

    public static final Creator<Receipe> CREATOR = new Creator<Receipe>() {
        @Override
        public Receipe createFromParcel(Parcel in) {
            return new Receipe(in);
        }

        @Override
        public Receipe[] newArray(int size) {
            return new Receipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Receipe(Parcel in) {
        id = in.readLong();
        name = in.readString();
        ingredients = in.createTypedArray(Ingredient.CREATOR);
        steps = in.createTypedArray(Step.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeTypedArray(ingredients, flags);
        dest.writeTypedArray(steps, flags);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
