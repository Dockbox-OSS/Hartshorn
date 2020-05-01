package com.darwinreforged.server.modules.extensions.plotsquared.tickets.database;


import com.darwinreforged.server.sponge.DarwinServer;
import com.darwinreforged.server.modules.extensions.plotsquared.tickets.config.TicketConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataStoreManager {

	private final Map<String, Class<? extends IDataStore>> dataStores = new HashMap<>();
	private IDataStore dataStore;

	public DataStoreManager() {

	}

	public boolean load() {
		if (getDataStore() != null) {
			clearDataStores();
		}
		registerDataStore("H2", H2DataStore.class);
		registerDataStore("MYSQL", MYSQLDataStore.class);
		switch (TicketConfig.storageEngine.toUpperCase()) {
			case "MYSQL":
				setDataStoreInstance("MYSQL");
				DarwinServer.getLogger().info("Loading datastore: MySQL");
				return getDataStore().load();
			case "H2":
				setDataStoreInstance("H2");
				DarwinServer.getLogger().info("Loading datastore: H2 ");
				return getDataStore().load();
			default:
				DarwinServer.getLogger().error("Unable to determine selected datastore.");
				DarwinServer.getLogger().info("Available datastores: " + getAvailableDataStores().toString());
				return false;
		}
	}

	/**
	 * Register a new Data Store. This should be run at onLoad()<br>
	 *
	 * @param dataStoreId    ID that identifies this data store <br>
	 * @param dataStoreClass a class that implements IDataStore
	 */
	public void registerDataStore ( String dataStoreId, Class<? extends IDataStore> dataStoreClass ) {
		dataStores.put(dataStoreId, dataStoreClass);
	}

	/**
	 * Unregisters the data store with the provided id
	 *
	 * @param dataStoreId
	 */
	public void unregisterDataStore ( String dataStoreId ) {
		dataStores.remove(dataStoreId);
	}

	/**
	 * Unregisters all data stores
	 */
	public void clearDataStores () {
		dataStores.clear();
	}

	/**
	 * List of registered data stores id
	 *
	 * @return
	 */
	public List<String> getAvailableDataStores () {
		List<String> list = new ArrayList<>();
		list.addAll(dataStores.keySet());
		return Collections.unmodifiableList(list);
	}

	/**
	 * Sets and instantiate the data store
	 *
	 * @param dataStoreId
	 */
	private void setDataStoreInstance ( String dataStoreId ) {
		try {
			dataStore = dataStores.get(dataStoreId).getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SecurityException e) {
			throw new RuntimeException("Couldn't instantiate data store " + dataStoreId + " " + e);
		}
	}

	/**
	 * Gets current data store. Returns null if there isn't an instantiated data
	 * store
	 *
	 * @return
	 */
	public IDataStore getDataStore () {
		return dataStore;
	}

}
