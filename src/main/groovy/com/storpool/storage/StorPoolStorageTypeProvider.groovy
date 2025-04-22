/*
* Copyright 2025 StorPool Storage AD
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.storpool.storage

import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.core.providers.StorageProvider
import com.morpheusdata.core.providers.StorageProviderVolumes
import com.morpheusdata.model.Icon
import com.morpheusdata.model.OptionType;
import com.morpheusdata.model.StorageGroup
import com.morpheusdata.model.StorageServer
import com.morpheusdata.model.StorageServerType
import com.morpheusdata.model.StorageVolume
import com.morpheusdata.model.StorageVolumeType
import com.morpheusdata.response.ServiceResponse

class StorPoolStorageProvider implements StorageProvider, StorageProviderVolumes{

	public static final String STORAGE_PROVIDER_CODE = 'morpheus-storpool-datastore-plugin.storage'


	protected MorpheusContext morpheusContext
	protected Plugin plugin

	StorPoolStorageProvider(Plugin plugin, MorpheusContext morpheusContext) {
		this.morpheusContext = morpheusContext
		this.plugin = plugin
	}

	/**
	 * Returns the description of the provider type
	 * @return String
	 */
	@Override
	String getDescription() {
		return 'This is a custom storage provider for morpheus-storpool-datastore-plugin'
	}

	/**
	 * Returns the Storage Server Integration logo for display when a user needs to view or add this integration
	 * @return Icon representation of assets stored in the src/assets of the project.
	 */
	@Override
	Icon getIcon() {
		return new Icon(path:"morpheus.svg", darkPath: "morpheus.svg")
	}

	/**
	 * Provide a {@link StorageServerType} to be added to the morpheus environment
	 * as the type for this {@link StorageServer}. The StorageServerType also defines the
	 * OptionTypes for configuration of a new server and its volume types.
	 * @return StorageServerType
	 */
	@Override
	StorageServerType getStorageServerType() {
		StorageServerType storageServerType = new StorageServerType(
                        code: getCode(), name: getName(), description: getDescription(), hasBlock: true, hasObject: false,
                        hasFile: false, hasDatastore: true, hasNamespaces: false, hasGroups: false, hasDisks: true, hasHosts: false,
                        createBlock: true, createObject: false, createFile: false, createDatastore: true, createNamespaces: false,
                        createGroup: false, createDisk: true, createHost: false, hasFileBrowser: true)
                storageServerType.optionTypes = getStorageServerOptionTypes()
                storageServerType.volumeTypes = getStorageVolumeTypes()
                return storageServerType
	}

	Collection<OptionType> getStorageServerOptionTypes() {
        return []
	}

	/**
	 * Validation Method used to validate all inputs applied to the integration of a Storage Provider upon save.
	 * If an input fails validation or authentication information cannot be verified, Error messages should be returned
	 * via a {@link ServiceResponse} object where the key on the error is the field name and the value is the error message.
	 * If the error is a generic authentication error or unknown error, a standard message can also be sent back in the response.
	 *
	 * @param storageServer The Storage Server object contains all the saved information regarding configuration of the Storage Provider
	 * @param opts an optional map of parameters that could be sent. This may not currently be used and can be assumed blank
	 * @return A response is returned depending on if the inputs are valid or not.
	 */
	@Override
	ServiceResponse verifyStorageServer(StorageServer storageServer, Map opts) {
		return ServiceResponse.success()
	}

	/**
	 * Called on the first save / update of a storage server integration. Used to do any initialization of a new integration
	 * Often times this calls the periodic refresh method directly.
	 * @param storageServer The Storage Server object contains all the saved information regarding configuration of the Storage Provider.
	 * @param opts an optional map of parameters that could be sent. This may not currently be used and can be assumed blank
	 * @return a ServiceResponse containing the success state of the initialization phase
	 */
	@Override
	ServiceResponse initializeStorageServer(StorageServer storageServer, Map opts) {
		return ServiceResponse.success()
	}

	/**
	 * Refresh the provider with the associated data in the external system.
	 * @param storageServer The Storage Server object contains all the saved information regarding configuration of the Storage Provider.
	 * @param opts an optional map of parameters that could be sent. This may not currently be used and can be assumed blank
	 * @return a {@link ServiceResponse} object. A ServiceResponse with a success value of 'false' will indicate the
	 * refresh process has failed and will change the storage server status to 'error'
	 */
	@Override
	ServiceResponse refreshStorageServer(StorageServer storageServer, Map opts) {
		return ServiceResponse.success()
	}

	/**
	 * Returns the Morpheus Context for interacting with data stored in the Main Morpheus Application
	 *
	 * @return an implementation of the MorpheusContext for running Future based rxJava queries
	 */
	@Override
	MorpheusContext getMorpheus() {
		return this.@morpheusContext
	}

	/**
	 * Returns the instance of the Plugin class that this provider is loaded from
	 * @return Plugin class contains references to other providers
	 */
	@Override
	Plugin getPlugin() {
		return this.@plugin
	}

	/**
	 * A unique shortcode used for referencing the provided provider. Make sure this is going to be unique as any data
	 * that is seeded or generated related to this provider will reference it by this code.
	 * @return short code string that should be unique across all other plugin implementations.
	 */
	@Override
	String getCode() {
		return STORAGE_PROVIDER_CODE
	}

	/**
	 * Provides the provider name for reference when adding to the Morpheus Orchestrator
	 * NOTE: This may be useful to set as an i18n key for UI reference and localization support.
	 *
	 * @return either an English name of a Provider or an i18n based key that can be scanned for in a properties file.
	 */
	@Override
	String getName() {
		return "morpheus-storpool-datastore-plugin Storage Provider"
	}

	@Override
	ServiceResponse<StorageVolume> createVolume(StorageGroup storageGroup, StorageVolume storageVolume, Map opts) {
		return null
	}

	@Override
	ServiceResponse<StorageVolume> resizeVolume(StorageGroup storageGroup, StorageVolume storageVolume, Map opts) {
		return null
	}

	@Override
	ServiceResponse<StorageVolume> deleteVolume(StorageGroup storageGroup, StorageVolume storageVolume, Map opts) {
		return null
	}

	@Override
	Collection<StorageVolumeType> getStorageVolumeTypes() {
		return []
	}
}
