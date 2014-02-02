package com.virtualmanila.guardianportallister.sgmy.util;

import com.virtualmanila.guardianportallister.sgmy.model.GuardianPortal;

import java.util.List;

/**
 * Created by andhie on 2/1/14.
 */
public class Events {

    public static class OnPullServerListEvent {

        private List<GuardianPortal> liveList;

        public OnPullServerListEvent(List<GuardianPortal> list) {
            liveList = list;
        }

        public List<GuardianPortal> getLiveList() {
            return liveList;
        }
    }

    public static class onLoadFromFileEvent {

        private List<GuardianPortal> list;

        public onLoadFromFileEvent(List<GuardianPortal> list) {
            this.list = list;
        }

        public List<GuardianPortal> getList() {
            return list;
        }

    }

    public static class onAddressResolved {

        private String address;

        public onAddressResolved(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

    }
}
