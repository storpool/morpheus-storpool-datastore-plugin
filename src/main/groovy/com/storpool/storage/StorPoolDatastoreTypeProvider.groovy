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
import com.morpheusdata.core.providers.DatastoreTypeProvider
import com.morpheusdata.model.ComputeServer
import com.morpheusdata.model.ComputeServerGroup
import com.morpheusdata.model.Datastore
import com.morpheusdata.model.OptionType
import com.morpheusdata.model.Snapshot
import com.morpheusdata.model.StorageServer
import com.morpheusdata.model.StorageVolume
import com.morpheusdata.model.VirtualImage
import com.morpheusdata.response.ServiceResponse
import com.storpool.storage.util.StorPoolUtil
import groovy.util.logging.Slf4j

@Slf4j
class StorPoolDatastoreTypeProvider implements DatastoreTypeProvider, DatastoreTypeProvider.MvmProvisionFacet, DatastoreTypeProvider.SnapshotFacet, DatastoreTypeProvider.SnapshotFacet.SnapshotServerFacet, GroovyObject {

	public static final String DATASTORE_TYPE_PROVIDER_CODE = 'storpool.datastore'

	protected MorpheusContext context
	protected Plugin plugin

	public StorPoolDatastoreTypeProvider(Plugin plugin, MorpheusContext ctx) {
		super()

		this.@context = ctx
		this.@plugin = plugin
	}
	/**
	 * Returns the {@link ProvisionProvider} code for linking the generated {@link DatastoreType} with the appropriate {@link ProvisionType}
	 * @return the code of the relevant ProvisionProvider
	 */
	@Override
	String getProvisionTypeCode() {
		log.info("StorPool getProvisionTypeCode")
		return 'storpool.provision'
	}

	/**
	 * Returns the provider code for interacting with the {@link StorageServer} interface
	 * This is optional and can be null if there is no interaction with a storage server whatsoever
	 * @return the code for the storage provider (also matches the {@link StorageServerType} code)
	 */
	@Override
	String getStorageProviderCode() {

		log.info("StorPool getStorageProviderCode")
		return 'storpool.storage'
	}

	/**
	 * Provide custom configuration options when creating a new {@link AccountIntegration}
	 * @return a List of OptionType
	 */
	@Override
	List<OptionType> getOptionTypes() {
		log.info("StorPool getOptionTypes")
		Collection<OptionType> options = []
		options << new OptionType(
				name: "StorPool Storage Server",
				code: "sp-datastore-storage-server",
				displayOrder: Integer.valueOf(0),
				fieldContext: "domain",
				fieldLabel: "StorPool Storage Server",
				fieldCode: "storpool.label.storageServer",
				fieldName: "storageServer.id",
				inputType: OptionType.InputType.SELECT,
				required: true,
				optionSource: "storageServers"
		)
		options << new OptionType(
				name:'SP template',
				code:'storpool.plugin.template',
				displayOrder: Integer.valueOf(1),
				fieldLabel:'SP template',
				fieldName: 'template',
				inputType: OptionType.InputType.TEXT,
				required: true
		)
//		options << new OptionType(
//				name:'API Host',
//				code:'storpool.plugin.apiUrl',
//				displayOrder:1,
//				fieldLabel:'API Host',
//				fieldName: 'serviceHost',
//				inputType: OptionType.InputType.TEXT,
//				required: true
//		)
//		options << new OptionType(
//				name:'API Port',
//				code:'storpool.plugin.apiPort',
//				displayOrder:2,
//				fieldLabel:'API Port',
//				fieldName: 'servicePort',
//				inputType: OptionType.InputType.TEXT,
//				required: true
//		)
//		options << new OptionType(
//				name:'API Token',
//				code:'storpool.plugin.apiToken',
//				displayOrder:3,
//				fieldLabel:'API Token',
//				fieldName: 'serviceToken',
//				inputType: OptionType.InputType.TEXT,
//				required: true
//		)
//		options << new OptionType(
//				name:'SP template',
//				code:'storpool.plugin.template',
//				displayOrder:4,
//				fieldLabel:'SP template',
//				fieldName: 'template',
//				inputType: OptionType.InputType.TEXT,
//				required: true
//		)
		return options
	}

	/**
	 * Flags if this datastore can be created by the user. Some datastores are system injected and cannot be created by the user
	 * @return whether, or not this datastore can be created by the user
	 */
	@Override
	boolean getCreatable() {
		log.info("StorPool getCreatable")
		return true
	}

	/**
	 * Flags if the datastore created for this is editable or not
	 * @return whether, or not this datastore can be edited once added
	 */
	@Override
	boolean getEditable() {
		log.info("StorPool getEditable")
		return true
	}

	/**
	 * Flags if the datastore created for this is removable or not
	 * @return whether, or not this datastore can be removed once added
	 */
	@Override
	boolean getRemovable() {
		log.info("StorPool getRemovable")
		return true
	}

	/**
	 * Perform any operations necessary on the target to remove a volume. This is used to remove a volume on a storage server
	 * It is typically called as part of server teardown.
	 * @param volume the current volume to remove
	 * @param server the server the volume is being removed from (may contain information such as parentServer (hypervisor) or cluster)
	 * @param removeSnapshots whether to remove snapshots associated with the volume. In some implementations this is mandatory and not separate.
	 * @param force whether to force the removal of the volume. This is typically used to force the removal of a volume that is in use.
	 * @return the success state of the removal
	 */
	@Override
	ServiceResponse removeVolume(StorageVolume volume, ComputeServer server, boolean removeSnapshots, boolean force) {
		log.info("StorPool StorageVolume {}, ComputeServer {}", volume, server)

		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Perform any operations necessary on the target to create a volume. This is used to create a volume on a storage server
	 * It is typically called as part of server provisioning.
	 * @param volume the current volume to create
	 * @param server the server the volume is being created on (may contain information such as parentServer (hypervisor) or cluster)
	 * @return the success state and a copy of the volume
	 */
	@Override
	ServiceResponse<StorageVolume> createVolume(StorageVolume volume, ComputeServer server) {
		log.info("StorPool StorageVolume {}, ComputeServer {}", volume, server)
		//TODO:
		// In which cases this method is invoked? Only with the API call create volume or if we keep a cache of a cloned volumes/ create volume from a snapshot?

		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Clones a volume based on a source volume object. This is one of the most important methods for provisioning as most
	 * {@link VirtualImage} provisioning objects are cloned from a local image cache of source volumes. This is where the QCOW2 may reside
	 * Often times you can infer this from the combination of the sourceVolume object as well as its datastore
	 * <p>
	 *     <code>
	 *         String sourceVolumePath = sourceVolume.datastore.externalPath + '/' + sourceVolume.externalId
	 *         //this is the QCOW2 path typically used for clone operations
	 *         String command = "sudo mkdir -p \"${volume.datastore.externalPath}/${server.externalId ?: server.name}\" ; sudo ionice -c 3 cp -f \"${sourceVolume.datastore.externalPath}/${sourceVolume.externalId}\" \"${volume.datastore.externalPath}/${server.externalId ?: server.name}/${volume.volumeName}\""
	 * 		   morpheusContext.executeCommandOnServer(server, command)
	 *     </code>
	 * </p>
	 * @param volume the volume we are creating and cloning into
	 * @param server the server the volume is associated with (typically the workload/vm)
	 * @param sourceVolume the source volume we are cloning from
	 * @return the success state and a copy of the volume
	 */
	@Override
	ServiceResponse<StorageVolume> cloneVolume(StorageVolume volume, ComputeServer server, StorageVolume sourceVolume) {
		log.info("StorPool cloneVolume");
		//TODO:
		// IS this method invoked each time a new virtual machine is created from the same image?
		// Can we keep a cached copy on the Data Store for future virtual machines?
		// If we could keep a cached copy from the respective storage provider, where should we set it (directly in the DB or property of a specific object)?

		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Perform any operations necessary on the target to resize a volume. This is used to resize a volume on a storage server
	 * @param volume the current volume to resize
	 * @param server the server the volume is being resized on (may contain information such as parentServer (hypervisor) or cluster)
	 * @param newSize the new size of the volume... TODO: this exists on the volume record already, is newSize needed?
	 * @return the success state and a copy of the volume
	 */
	@Override
	ServiceResponse<StorageVolume> resizeVolume(StorageVolume volume, ComputeServer server, Long newSize) {
		log.info("StorPool resizeVolume");
		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Perform any operations necessary on the target to create and register a datastore.
	 * Most implementations iterate over the servers on the server group (hypervisors) and register a storage pool
	 * @param datastore the current datastore being created
	 * @return the service response containing success state or any errors upon failure
	 */
	@Override
	ServiceResponse<Datastore> createDatastore(Datastore datastore) {
		log.info("StorPool createDatastore");
		log.info("StorPool Datastore {}", datastore.toString());
		log.info("StorPool {}", datastore.getConfig());
		if (datastore == null || datastore.getStorageServer() == null) {
			 ServiceResponse.error("Could not find data store or server storage");
		}
		ServiceResponse.prepare(datastore);
		try {
			StorPoolUtil.StorPoolConnection conn = new StorPoolUtil.StorPoolConnection(datastore.getStorageServer().getConfigProperty("serviceHost"), datastore.getStorageServer().getConfigProperty("servicePort"), datastore.getStorageServer().getConfigProperty("serviceToken"), datastore.getConfigProperty("template"));

			boolean resp = StorPoolUtil.templateExists(conn);
			if (!resp) {
                ServiceResponse.error("Could not create data store");
            }
		} catch (Exception ex) {
			return ServiceResponse.error("Could not create data store {}", ex);
		}
		//TODO:
		// We need to connect the pool to the hypervisor.
		// c.m.h.KvmBaseHostService - Matching Hypervisor not found for pool: test : Online Hypervisor Count: 1 - []

		log.info("Successfully created datastore on template {}", datastore.getConfigProperty("template"));
		return ServiceResponse.success(datastore);
	}

	/**
	 * Perform any operations necessary on the target to remove a datastore. this method should be implemented
	 * if {@link DatastoreTypeProvider#getRemovable()} is true. otherwise return null or an error.
	 * @param datastore the current datastore being removed
	 * @return the success state of the removal
	 */
	@Override
	ServiceResponse removeDatastore(Datastore datastore) {
		log.info("StorPool removeDatastore");
		return ServiceResponse.success(datastore)
	}

	/**
	 * Clones a volume based on a source being the reference to the actual File in the Virtual Image.
	 * This can be called in the event there is no image cache or we need to directly stream to an image target.
	 * Remember, this code runs in the manager or morpheus appliance and not on the host itself.
	 * In order to stream contents directly to the target , we need to create a link we can fetch using the {@link com.morpheusdata.core.MorpheusFileCopyService}
	 * @see com.morpheusdata.core.MorpheusFileCopyService* @see com.morpheusdata.core.synchronous.MorpheusSynchronousFileCopyService* @param volume the volume we are creating and cloning into
	 * @param server the server the volume is associated with (typically the workload/vm)
	 * @param virtualImage the virtual image this volume is being cloned out of
	 * @param cloudFile the specific disk file (Karman abstraction) that is being cloned
	 * @return the success state and a copy of the volume
	 */
	@Override
	ServiceResponse<StorageVolume> cloneVolume(StorageVolume volume, ComputeServer server, VirtualImage virtualImage, com.bertramlabs.plugins.karman.CloudFileInterface cloudFile) {
		log.info("StorPool cloneVolume")
		//TODO:
		// In which cases this method is invoked?
		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * This is a hook call to allow the plugin to prepare the host for the volume. This could be something like forcing a rescan
	 * or refresh if necessary on the host itself (not the vm)
	 * @param cluster
	 * @param volume
	 * @return
	 */
	@Override
	ServiceResponse<StorageVolume> prepareHostForVolume(ComputeServerGroup cluster, ComputeServer server, StorageVolume volume) {
		log.info("StorPool prepareHostForVolume");
		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * When creating/defining a virtual machine in libvirt, an XML specification must be generated. Within this specificaiton are device elements corresponding
	 * to disks. This method is called to allow the plugin to specify the disk config to be used for the disk device. It is important to factor in the
	 * server record and check if it has uefi or virtioToolsInstalled as this may change your {@link MvmDiskConfig.DiskMode} to VIRTIO
	 * @param cluster
	 * @param server
	 * @param volume
	 * @return
	 */
	@Override
	ServiceResponse<MvmDiskConfig> buildDiskConfig(ComputeServerGroup cluster, ComputeServer server, StorageVolume volume) {
		log.info("StorPool buildDiskConfig")
		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * This is a hook call to allow the plugin to know if a vm is being moved off of a host or removed. It should not be used to remove volume
	 * but rather if there is work to be done to release the volume from the host. This could be something like forcing a rescan.
	 * @param cluster reference to the current cluster this is being run within
	 * @param volume
	 * @return
	 */
	@Override
	ServiceResponse<StorageVolume> releaseVolumeFromHost(ComputeServerGroup cluster, ComputeServer server, StorageVolume volume) {
		log.info("StorPool releaseVolumeFromHost")
		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Creates volume snapshots of all volumes associated with a server. If the server volume is of type 'cdrom',
	 * it is best to not snapshot these as ISO formats are not typically worth snapshotting
	 * A lot of backup providers may use this to create snapshots for exportability or backup purposes.
	 * In those scenarios, it is important to create exportPath information on the {@link SnapshotFile} so the
	 * morphd agent can fetch the snapshot and store it in the backup provider.
	 * @param server the server to create snapshots for (typically a vm). Host can be acquired via {@link ComputeServer#getParentServer()}
	 * @param forBackup whether this snapshot is for backup purposes
	 * @param forExport whether this snapshot is for export purposes (like an image import)
	 * @return the success state and a copy of the snapshot
	 */
	@Override
	ServiceResponse<Snapshot> createSnapshot(ComputeServer server, Boolean forBackup, Boolean forExport) {
		log.info("StorPool createSnapshot")

		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Reverts a server to a snapshot. This is used to revert a server to a previous state. MVM/VME will automatically
	 * ensure the server is powered off during this operation and powered back on to desired user state after this
	 * operation is complete. NOTE: These snapshots are typically volume based and not vm state based.
	 * @param server the server to revert the snapshot on
	 * @param snapshot the snapshot to revert to
	 * @return the success state and a copy of the snapshot
	 */
	@Override
	ServiceResponse<Snapshot> revertSnapshot(ComputeServer server, Snapshot snapshot) {
		log.info("StorPool revertSnapshot")

		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Removes a snapshot from a server. This is used to remove a snapshot from a server. Sometimes,
	 * this means snapshots have to be consolidated or flattened depending on the implementation.
	 * @param server the server to remove the snapshot from
	 * @param snapshot the snapshot to remove
	 * @return the success state of the removal
	 */
	@Override
	ServiceResponse removeSnapshot(ComputeServer server, Snapshot snapshot) {
		log.info("StorPool removeSnapshot")

		return ServiceResponse.error("Not Implemented")
	}

	/**
	 * Returns the Morpheus Context for interacting with data stored in the Main Morpheus Application
	 *
	 * @return an implementation of the MorpheusContext for running Future based rxJava queries
	 */
	@Override
	MorpheusContext getMorpheus() {
		log.info("StorPool getMorpheus")

		return this.@context
	}

	/**
	 * Returns the instance of the Plugin class that this provider is loaded from
	 * @return Plugin class contains references to other providers
	 */
	@Override
	Plugin getPlugin() {
		log.info("StorPool getPlugin");
		return this.@plugin
	}

	/**
	 * A unique shortcode used for referencing the provided provider. Make sure this is going to be unique as any data
	 * that is seeded or generated related to this provider will reference it by this code.
	 * @return short code string that should be unique across all other plugin implementations.
	 */
	@Override
	String getCode() {
		log.info("StorPool getCode");
		return DATASTORE_TYPE_PROVIDER_CODE
	}

	/**
	 * Provides the provider name for reference when adding to the Morpheus Orchestrator
	 * NOTE: This may be useful to set as an i18n key for UI reference and localization support.
	 *
	 * @return either an English name of a Provider or an i18n based key that can be scanned for in a properties file.
	 */
	@Override
	String getName() {
		log.info("StorPool getName")

		return 'StorPool Datastore'
	}

	@Override
	ServiceResponse<Snapshot> createSnapshot(StorageVolume volume) {
		log.info("StorPool createSnapshot")

		return null
	}

	@Override
	ServiceResponse removeSnapshot(StorageVolume volume) {
		log.info("StorPool removeSnapshot")

		return null
	}

	@Override
	ServiceResponse<Snapshot> listSnapshots(StorageServer storageServer) {
		log.info("StorPool listSnapshots")

		return null
	}

	@Override
	ServiceResponse<StorageVolume> cloneVolume(StorageVolume volume, Snapshot sourceSnapshot) {
		log.info("StorPool cloneVolume from snapshot")

		return null
	}
}
