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

import com.morpheusdata.core.Plugin
import groovy.util.logging.Slf4j

@Slf4j
class StorPoolPlugin extends Plugin {

    @Override
    String getCode() {
        log.info("StorPool getCode");
        return 'storpool'
    }

    @Override
    void initialize() {
        log.info("StorPool initialize");
        this.setName("StorPool");
		this.registerProvider(new StorPoolStorageProvider(this, this.morpheus))
        this.registerProvider(new StorPoolDatastoreTypeProvider(this, this.morpheus))
    }

    /**
     * Called when a plugin is being removed from the plugin manager (aka Uninstalled)
     */
    @Override
    void onDestroy() {
        log.info("StorPool destroy");
        //nothing to do for now
    }
}
