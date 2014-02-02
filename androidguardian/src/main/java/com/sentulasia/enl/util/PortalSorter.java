package com.sentulasia.enl.util;

import com.sentulasia.enl.model.GuardianPortal;

import android.location.Location;

import java.util.Comparator;

/**
 * Created by andhie on 2/1/14.
 */
public class PortalSorter {

    public enum SortType {
        NAME(1),
        OWNER(2),
        AGE(3),
        DISTANCE(4);

        private final int value;

        SortType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

    }

    public interface Sorter extends Comparator<GuardianPortal> {

        @Override
        int compare(GuardianPortal p1, GuardianPortal p2);

        public SortType getSortType();

    }

    public static class Name implements Sorter {

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {
            return p1.getPortal_name().toUpperCase().compareTo(p2.getPortal_name().toUpperCase());
        }

        @Override
        public SortType getSortType() {
            return SortType.NAME;
        }
    }

    public static class Age implements Sorter {

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {

            int ageP1 = p1.getPortalAge();
            int ageP2 = p2.getPortalAge();

            return -((ageP1 < ageP2) ? -1 : ((ageP1 == ageP2) ? 0 : 1));
        }

        @Override
        public SortType getSortType() {
            return SortType.AGE;
        }
    }

    public static class Owner implements Sorter {

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {
            return p1.getAgent_name().toUpperCase().compareTo(p2.getAgent_name().toUpperCase());
        }

        @Override
        public SortType getSortType() {
            return SortType.OWNER;
        }
    }

    public static class Distance implements Sorter {

        private Location loc;

        public Distance(Location location) {
            loc = location;
        }

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {
            double d1 = p1.getPortalDistance(loc);
            double d2 = p2.getPortalDistance(loc);

            return (d1 < d2) ? -1 : ((d1 == d2) ? 0 : 1);
        }

        @Override
        public SortType getSortType() {
            return SortType.DISTANCE;
        }
    }

}
