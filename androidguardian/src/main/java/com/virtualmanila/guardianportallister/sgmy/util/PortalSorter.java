package com.virtualmanila.guardianportallister.sgmy.util;

import com.virtualmanila.guardianportallister.sgmy.model.GuardianPortal;

import java.util.Comparator;

/**
 * Created by andhie on 2/1/14.
 */
public class PortalSorter {

    public static class Name implements Comparator<GuardianPortal> {

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {
            return p1.getPortal_name().toUpperCase().compareTo(p2.getPortal_name().toUpperCase());
        }
    }

    public static class Age implements Comparator<GuardianPortal> {

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {

            int ageP1 = p1.getPortalAge();
            int ageP2 = p2.getPortalAge();

            return (ageP1 < ageP2) ? -1 : ((ageP1 == ageP2) ? 0 : 1);
        }
    }

    public static class Owner implements Comparator<GuardianPortal> {

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {
            return p1.getAgent_name().toUpperCase().compareTo(p2.getAgent_name().toUpperCase());
        }
    }

    public static class Distance implements Comparator<GuardianPortal> {

        @Override
        public int compare(GuardianPortal p1, GuardianPortal p2) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

}
