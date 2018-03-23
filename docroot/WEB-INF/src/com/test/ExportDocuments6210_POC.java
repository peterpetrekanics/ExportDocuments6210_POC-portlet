package com.test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * Portlet implementation class ExportDocuments6210_POC
 */
public class ExportDocuments6210_POC extends MVCPortlet {
	Group group;
	int DLFileCount;
	List<DLFileEntry> dlFileEntries;
	String version;
	PermissionChecker permissionChecker;
	DLFileEntry myFileEntry;
	InputStream myFile;
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {
		System.out.println("ProcessAction starts..");
		
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			group = GroupLocalServiceUtil.getGroup(themeDisplay.getScopeGroupId());
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		long groupId = group.getGroupId();
		List<DLFolder> myFolder = null;
		try {
			myFolder = DLFolderLocalServiceUtil.getDLFolders(0, 1000);
		} catch (SystemException e1) {
			e1.printStackTrace();
		}
		long folderId = myFolder.get(0).getFolderId();
		
		
		try {
			DLFileCount = DLFileEntryLocalServiceUtil.getFileEntriesCount(groupId, folderId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		System.out.println("there are " + DLFileCount + " files on the server");
		try {
			OrderByComparator obc = null;
			dlFileEntries = DLFileEntryLocalServiceUtil.getFileEntries(groupId, folderId, 0, 1000, obc );
		} catch (SystemException e) {
			e.printStackTrace();
		}
//		System.out.println("dlFileEntries: "+dlFileEntries);
		
		User currentUser = themeDisplay.getUser();
		
		System.out.println("the current user's screenname is: "+currentUser.getScreenName());
		myFileEntry = dlFileEntries.get(0);
		long primKey = myFileEntry.getPrimaryKey();
		String documentName = DLFileEntry.class.getName();
		boolean hasPermissions = false;
		
		permissionChecker = themeDisplay.getPermissionChecker();
		long scopeGroupId = themeDisplay.getScopeGroupId();
		

		hasPermissions = permissionChecker.hasPermission(scopeGroupId, documentName, primKey, ActionKeys.VIEW);
		System.out.println("Checking if the current user has permissions on the document in question: " + hasPermissions);
		
		
		if(hasPermissions){
		
		long fileEntryId = myFileEntry.getFileEntryId();
		version =  myFileEntry.getVersion();
		
		try {
			myFile = DLFileEntryServiceUtil.getFileAsStream(fileEntryId, version);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		byte[] buffer = new byte[myFile.available()];
		myFile.read(buffer);
	
	    File targetFile = new File("/home/peterpetrekanics/Downloads/t.pdf");
	    OutputStream outStream = new FileOutputStream(targetFile);
	    outStream.write(buffer);
	    
	    myFile.close();
	    outStream.close();
		}

		
		System.out.println("ProcessAction ends..");
	}
	
	

}
