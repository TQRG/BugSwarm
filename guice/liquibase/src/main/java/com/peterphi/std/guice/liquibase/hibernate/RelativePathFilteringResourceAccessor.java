package com.peterphi.std.guice.liquibase.hibernate;

import liquibase.resource.ResourceAccessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Intercepts resource requests, changing ./ to mean relative to the master changeset
 */
class RelativePathFilteringResourceAccessor implements ResourceAccessor
{
	private final ResourceAccessor inner;
	private final String folder;


	public RelativePathFilteringResourceAccessor(final ResourceAccessor inner, final String masterChangeset)
	{
		this.inner = inner;

		// Extract the folder (assuming the master changeset is in a folder, otherwise we leave it blank)
		if (masterChangeset.indexOf('/') != -1)
		{
			this.folder = masterChangeset.substring(0, masterChangeset.lastIndexOf('/')) + "/";
		}
		else
		{
			this.folder = "";
		}
	}


	/**
	 * Intercepts getResourcesAsStream, the method used to retrieve resources for the master changeset
	 *
	 * @param path
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	@Override
	public Set<InputStream> getResourcesAsStream(final String path) throws IOException
	{
		// For local paths, try to resolve the path relative to the folder of the master file
		// N.B. Assumes paths are sensible, not .//someFile.xml
		if (path.startsWith("./"))
		{
			final Set<InputStream> streams = inner.getResourcesAsStream(folder + path.substring(2));

			if (streams != null && !streams.isEmpty())
				return streams;
		}

		return inner.getResourcesAsStream(path);
	}


	@Override
	public Set<String> list(final String relativeTo,
	                        final String path,
	                        final boolean includeFiles,
	                        final boolean includeDirectories,
	                        final boolean recursive) throws IOException
	{
		// Does not appear to be used

		return inner.list(relativeTo, path, includeFiles, includeDirectories, recursive);
	}


	@Override
	public ClassLoader toClassLoader()
	{
		// Does not appear to be used

		return inner.toClassLoader();
	}
}
