package storage;

/**
 * @author yoanmartin
 * Enumeration representing the different state of a user from the storage point of view
 */
public enum ClientToStorageMode {
	STORE {
		@Override
	    public String toString() {
		      return "STORE";
		    }
	}, RETRIEVE {
		@Override
	    public String toString() {
		      return "RETRIEVE";
		    }
	};
}
