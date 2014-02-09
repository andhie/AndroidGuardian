package com.sentulasia.enl.util;

import com.sentulasia.enl.model.GuardianPortal;

import java.util.List;

/**
 * Created by andhie on 2/1/14.
 */
public class Events {

    public static class OnNewLivePortalListEvent {

	private List<GuardianPortal> list;

	public OnNewLivePortalListEvent(List<GuardianPortal> list) {
	    this.list = list;
	}

	public List<GuardianPortal> getList() {
	    return list;
	}

    }

    public static class OnNewDeadPortalListEvent {

	private List<GuardianPortal> list;

	public OnNewDeadPortalListEvent(List<GuardianPortal> list) {
	    this.list = list;
	}

	public List<GuardianPortal> getList() {
	    return list;
	}

    }

    public static class OnNoNewPortalData {

	private String type;

	public OnNoNewPortalData(String type) {
	    this.type = type;
	}

	public String getPortalDataType() {
	    return type;
	}
    }

    public static class OnLoadFromFileEvent {

	private List<GuardianPortal> list;

	public OnLoadFromFileEvent(List<GuardianPortal> list) {
	    this.list = list;
	}

	public List<GuardianPortal> getList() {
	    return list;
	}

    }

    public static class OnAddressResolved {

	private String address;

	public OnAddressResolved(String address) {
	    this.address = address;
	}

	public String getAddress() {
	    return address;
	}

    }

    public static class onRequestPortalDetail {

	private GuardianPortal portal;

	public onRequestPortalDetail(GuardianPortal portal) {
	    this.portal = portal;
	}

	public GuardianPortal getPortal() {
	    return portal;
	}

    }
}
