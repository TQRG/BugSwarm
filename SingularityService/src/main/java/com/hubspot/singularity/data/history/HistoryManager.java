package com.hubspot.singularity.data.history;

import java.util.Date;
import java.util.List;

import com.google.common.base.Optional;
import com.hubspot.singularity.SingularityDeployHistory;
import com.hubspot.singularity.SingularityRequestHistory;
import com.hubspot.singularity.SingularityTaskHistory;
import com.hubspot.singularity.SingularityTaskIdHistory;
import com.hubspot.singularity.data.history.SingularityMappers.SingularityRequestIdCount;

public interface HistoryManager {

  public enum OrderDirection {
    ASC, DESC;
  }

  void saveRequestHistoryUpdate(SingularityRequestHistory requestHistory);

  void saveTaskHistory(SingularityTaskHistory taskHistory);

  void saveDeployHistory(SingularityDeployHistory deployHistory);

  Optional<SingularityDeployHistory> getDeployHistory(String requestId, String deployId);

  List<SingularityDeployHistory> getDeployHistoryForRequest(String requestId, Integer limitStart, Integer limitCount);

  List<SingularityTaskIdHistory> getTaskHistoryForRequest(String requestId, Integer limitStart, Integer limitCount);

  List<SingularityTaskIdHistory> getTaskHistoryForDeploy(String requestId, String deployId, Integer limitStart, Integer limitCount);

  Optional<SingularityTaskHistory> getTaskHistory(String taskId);

  List<SingularityRequestHistory> getRequestHistory(String requestId, Optional<OrderDirection> orderDirection, Integer limitStart, Integer limitCount);

  List<String> getRequestHistoryLike(String requestIdLike, Integer limitStart, Integer limitCount);

  List<SingularityRequestIdCount> getRequestIdCounts(Date before);

  void purgeTaskHistory(String requestId, int count, Optional<Integer> limit, Optional<Date> purgeBefore, boolean deleteRowInsteadOfUpdate);

}
