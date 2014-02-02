package com.sentulasia.enl.util;

import com.sentulasia.enl.model.GuardianPortal;

import java.util.List;

/**
 * Created by andhie on 2/1/14.
 */
public class Events {

    public static class OnPullServerListEvent {

        private List<GuardianPortal> liveList;

        private List<GuardianPortal> deadList;

        public OnPullServerListEvent(List<GuardianPortal> liveList, List<GuardianPortal> deadList) {
            this.liveList = liveList;
            this.deadList = deadList;
        }

        public List<GuardianPortal> getLiveList() {
            return liveList;
        }

        public List<GuardianPortal> getDeadList() {
            return deadList;
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
