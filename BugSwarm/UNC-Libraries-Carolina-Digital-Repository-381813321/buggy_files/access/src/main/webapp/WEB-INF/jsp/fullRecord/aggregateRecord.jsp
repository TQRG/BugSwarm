<%--

    Copyright 2008 The University of North Carolina at Chapel Hill

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ taglib prefix="cdr" uri="http://cdr.lib.unc.edu/cdrUI"%>

<c:choose>
	<c:when test="${not empty briefObject.countMap}">
		<c:set var="childCount" value="${briefObject.countMap.child}"/>
	</c:when>
	<c:otherwise>
		<c:set var="childCount" value="0"/>
	</c:otherwise>
</c:choose>

<c:set var="dataFileUrl">${cdr:getOriginalFileUrl(briefObject)}</c:set>

<div class="onecol full_record_top">
	<div class="contentarea">
		<c:set var="thumbnailObject" value="${briefObject}" scope="request" />
		<c:import url="common/thumbnail.jsp">
			<c:param name="target" value="file" />
			<c:param name="size" value="large" />
		</c:import>
		
		<c:if test="${permsHelper.hasEditAccess(accessGroupSet, briefObject)}">
			<div class="actionlink right"><a href="${adminBaseUrl}/describe/${briefObject.id}">Edit</a></div>
		</c:if>

		<div class="collinfo">
			<div class="collinfo_metadata">
				<h2><c:out value="${briefObject.title}" /></h2>
				
				<ul class="pipe_list smaller">
					<c:if test="${not empty briefObject.creator}">
						<li>
							<span class="bold">Creator<c:if test="${fn:length(briefObject.creator) > 1}">s</c:if>:</span> 
							<c:forEach var="creatorObject" items="${briefObject.creator}" varStatus="creatorStatus">
								<c:out value="${creatorObject}"/><c:if test="${!creatorStatus.last}">; </c:if>
							</c:forEach>
						</li>
					</c:if>
					<c:if test="${not empty briefObject.parentCollection && briefObject.ancestorPathFacet.highestTier > 0}">
						<li>
							<c:url var="parentUrl" scope="page" value="record/${briefObject.parentCollection}" />
							<span class="bold">Collection:</span> 
							<a href="<c:out value='${parentUrl}' />"><c:out value="${briefObject.parentCollectionName}"/></a>
						</li>
					</c:if>
				</ul>
				
				<ul class="pipe_list smaller">
					<li>
						<c:choose>
							<c:when test="${not empty briefObject.contentTypeFacet[0].displayValue}">
								<span class="bold">File Type:</span> <c:out value="${briefObject.contentTypeFacet[0].displayValue}" />
								<c:if test="${briefObject.filesizeSort != -1}">  | <span class="bold">${searchSettings.searchFieldLabels['FILESIZE']}:</span> <c:out value="${cdr:formatFilesize(briefObject.filesizeSort, 1)}"/></c:if>
							</c:when>
							<c:otherwise>
								<span>Contains:</span> ${childCount} item<c:if test="${childCount != 1}">s</c:if>
							</c:otherwise>
						</c:choose>
					</li>
					<c:if test="${not empty briefObject.dateAdded}"><li><span class="bold">${searchSettings.searchFieldLabels['DATE_ADDED']}:</span> <fmt:formatDate pattern="yyyy-MM-dd" value="${briefObject.dateAdded}" /></li></c:if>
					<c:if test="${not empty briefObject.dateCreated}"><li><span class="bold">${searchSettings.searchFieldLabels['DATE_CREATED']}:</span> <fmt:formatDate pattern="yyyy-MM-dd" value="${briefObject.dateCreated}" /></li></c:if>
					<c:if test="${not empty embargoDate}"><li><span class="bold">Embargoed Until:</span> <fmt:formatDate pattern="yyyy-MM-dd" value="${embargoDate}" /></li></c:if>
				</ul>
			
			</div>
		</div>
		<div class="clear">
			<c:choose>
				<c:when test="${permsHelper.hasOriginalAccess(requestScope.accessGroupSet, briefObject)}">
					<div class="actionlink left download">
						<a href="${dataFileUrl}?dl=true">Download</a>
					</div>
					<c:if test="${briefObject.contentTypeFacet[0].displayValue == 'mp4'}">
						<div class="actionlink left">
							<a href="${dataFileUrl}">View</a>
						</div>
					</c:if>
				</c:when>
				<c:when test="${not empty embargoDate && not empty dataFileUrl}">
					<div class="noaction left">
						Available after <fmt:formatDate value="${embargoDate}" pattern="d MMMM, yyyy"/>
					</div>
				</c:when>
			</c:choose>
			
			<c:choose>
				<c:when test="${permsHelper.hasImagePreviewAccess(requestScope.accessGroupSet, briefObject)}">
					<div class="actionlink left">
						<a href="" class="inline_viewer_link jp2_viewer_link">View</a>
					</div>
					<div class="clear_space"></div>
					<link rel="stylesheet" href="/static/plugins/leaflet/leaflet.css">
					<link rel="stylesheet" href="/static/plugins/Leaflet-fullscreen/dist/leaflet.fullscreen.css">
					<div id="jp2_viewer" class="jp2_imageviewer_window" data-url="${cdr:getPreferredDatastream(briefObject, 'IMAGE_JP2000').owner}"></div>
				</c:when>
				<c:when test="${permsHelper.hasOriginalAccess(requestScope.accessGroupSet, briefObject)}">
					<c:choose>
						<c:when test="${briefObject.contentTypeFacet[0].searchKey == 'pdf'}">
							<div class="actionlink left">
								<a href="${dataFileUrl}">View</a>
							</div>
						</c:when>
						<c:when test="${briefObject.contentTypeFacet[0].displayValue == 'mp3'}">
							<div class="actionlink left">
								<a href="" class="inline_viewer_link audio_player_link">Listen</a>
							</div>
							<div class="clear_space"></div>
							<audio class="audio_player inline_viewer" src="${dataFileUrl}">
							</audio>
						</c:when>
					</c:choose>
				</c:when>
			</c:choose>
		</div>
	</div>
</div>

<c:if test="${childCount > 0}">
	<c:set var="defaultWebObjectID">
		<c:forEach items="${briefObject.datastreamObjects}" var="datastream">
			<c:if test="${datastream.name == 'original_file'}">
				<c:out value="${datastream.owner.pid}"/>
			</c:if>
		</c:forEach>
	</c:set>
	<div class="structure aggregate" data-pid="${briefObject.id}" data-exclude="${defaultWebObjectID}">
	</div>
</c:if>

<div class="onecol shadowtop">
	<div class="contentarea">
		<c:if test="${briefObject['abstractText'] != null}">
			<div class="description">
				<p>
					<c:out value="${briefObject['abstractText']}" />
				</p>
			</div>
		</c:if>
		<div class="metadata">
			<table>
				<tr>
					<th>Contains</th>
					<td>
						<c:url var="contentsResultsUrl" scope="page" value='list/${briefObject.id}'></c:url>
						<a href="<c:out value='${contentsResultsUrl}' />">${childCount} item<c:if test="${childCount != 1}">s</c:if></a>
					</td>
				</tr>
				<c:if test="${not empty facetFields}">
					<c:forEach items="${facetFields}" var="facetField">
						<tr>
							<th><c:out value="${searchSettings.searchFieldLabels[facetField.name]}" /></th>
							<td>
								<ul>
									<c:forEach items="${facetField.values}" var="facetValue" varStatus="status">
										<li>
											<c:url var="facetActionUrl" scope="page" value='search'>
												<c:param name="${searchSettings.searchStateParams['FACET_FIELDS']}" value="${searchSettings.searchFieldParams['ANCESTOR_PATH']}:${briefObject.path.searchValue},${briefObject.path.highestTier + 1}"/>
												<c:param name="${searchSettings.searchStateParams['ACTIONS']}" value='${additionalLimitActions}${searchSettings.actions["SET_FACET"]}:${searchSettings.searchFieldParams[facetValue.fieldName]},"${facetValue.searchValue}"'/>
											</c:url>
											<a href="<c:out value="${facetActionUrl}"/>"><c:out value="${facetValue.displayValue}" /></a> (<c:out value="${facetValue.count}" />)
										</li>
									</c:forEach>
								</ul>
							</td>
						</tr>
					</c:forEach>
				</c:if>
			</table>
		</div>
		<div class="metadata">
			${fullObjectView}
		</div>
	</div>
	<c:import url="fullRecord/exports.jsp" />
</div>
<c:import url="fullRecord/neighborList.jsp" />