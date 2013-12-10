package edu.berkeley.cs160.familiarfoods;

import java.io.Serializable;

/**
 * A class used to represent a link between two foods in the database.
 * The foods are stored in alphabetical order for food1 and food2.
 *
 * NOTE: This class should only be used by FamiliarFoodsDatabase.java.
 */
public class FoodLink implements Comparable<FoodLink>, Serializable {
    private static final long serialVersionUID = 1L;
    String food1;
    String food2;
    /** Number of net upvotes/downvotes for this food link. */
    int numVotes = 0;

    public FoodLink(String food1, String food2) {
        // Store the foods in alphabetical order
        int compare = food1.compareToIgnoreCase(food2);
        if (compare == 0) {
            throw new IllegalArgumentException(
                    "Can't link the same food to itself.");
        } else if (compare < 0) {
            this.food1 = food1;
            this.food2 = food2;
        } else {
            this.food2 = food1;
            this.food1 = food2;
        }
        numVotes = 1;
    }

    /**
     * Up-vote this food link.
     * @return the total number of votes after the up-voting.
     */
    public int upVote() {
        numVotes++;
        return getNumVotes();
    }

    /**
     * Down-vote this food link.
     * @return the total number of votes after the down-voting.
     */
    public int downVote() {
        numVotes--;
        return getNumVotes();
    }

    public int getNumVotes() {
        return numVotes;
    }

    /**
     * Given a food name for one of the foods in this link, return the other
     * food that is part of the link.
     *
     * @param foodName a food name that is part of this link
     * @return the name of the other food in this link
     */
    public String getOtherFood(String foodName) {
        if (food1.equals(foodName)) {
            return food2;
        } else if (food2.equals(foodName)) {
            return food1;
        }
        throw new IllegalArgumentException(
                "This food is not part of this link.");
    }

    @Override
    public int compareTo(FoodLink another) {
        // Have the comparison flipped so that highest votes come first
        // in ordering
        return Integer.valueOf(another.getNumVotes()).compareTo(
                Integer.valueOf(getNumVotes()));
    }

    /*
     * Two FoodLinks are "equals" if the respective food1 and food2 Strings
     * are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FoodLink other = (FoodLink) obj;
        if (food1 == null) {
            if (other.food1 != null) {
                return false;
            }
        } else if (!food1.equals(other.food1)) {
            return false;
        }
        if (food2 == null) {
            if (other.food2 != null) {
                return false;
            }
        } else if (!food2.equals(other.food2)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((food1 == null) ? 0 : food1.hashCode());
        result = prime * result + ((food2 == null) ? 0 : food2.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s <--> %s (%s)", food1, food2, numVotes);
    }
}
