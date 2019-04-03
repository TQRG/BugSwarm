package com.hubspot.baragon.migrations;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.hubspot.baragon.data.BaragonStateDatastore;
import com.hubspot.baragon.models.UpstreamInfo;
import com.hubspot.baragon.utils.ZkParallelFetcher;

public class UpstreamsMigration extends ZkDataMigration {

  private final BaragonStateDatastore baragonStateDatastore;
  private final ZkParallelFetcher zkFetcher;
  private final CuratorFramework curatorFramework;

  @Inject
  public UpstreamsMigration(BaragonStateDatastore baragonStateDatastore,
                            ZkParallelFetcher zkFetcher,
                            CuratorFramework curatorFramework) {
    super(1);
    this.baragonStateDatastore = baragonStateDatastore;
    this.zkFetcher = zkFetcher;
    this.curatorFramework = curatorFramework;
  }

  @Override
  public void applyMigration() {
    try {
      Collection<String> services = new ArrayList<>();

      for (String service : baragonStateDatastore.getServices()) {
        services.add(ZKPaths.makePath(BaragonStateDatastore.SERVICES_FORMAT, service));
      }

      Map<String, Collection<String>> serviceToUpstreams = zkFetcher.fetchChildrenInParallel(services);
      for (Entry<String, Collection<String>> entry : serviceToUpstreams.entrySet()) {
        for (String upstream : entry.getValue()) {
          Optional<UpstreamInfo> maybeUpstream = baragonStateDatastore.getUpstreamInfo(entry.getKey(), upstream);

          UpstreamInfo mergedInfo;
          if (maybeUpstream.isPresent()) {
            UpstreamInfo fromPath = UpstreamInfo.fromString(upstream);
            mergedInfo = new UpstreamInfo(fromPath.getUpstream(), maybeUpstream.get().getRequestId().or(fromPath.getRequestId()), maybeUpstream.get().getRackId().or(fromPath.getRackId()));
          } else {
            mergedInfo = UpstreamInfo.fromString(upstream);
          }

          curatorFramework.inTransaction()
              .delete().forPath(String.format(BaragonStateDatastore.UPSTREAM_FORMAT, entry.getKey(), upstream)).and()
              .create().forPath(String.format(BaragonStateDatastore.UPSTREAM_FORMAT, entry.getKey(), mergedInfo.toPath())).and()
              .commit();
        }
      }
    } catch (Exception e) {
      Throwables.propagate(e);
    }
  }
}
